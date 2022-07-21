package ink.ptms.adyeshach.api

import com.google.gson.JsonParser
import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.event.CustomDatabaseEvent
import ink.ptms.adyeshach.api.nms.NMS.Companion.shutdownPool
import ink.ptms.adyeshach.common.entity.*
import ink.ptms.adyeshach.common.entity.ai.ControllerGenerator
import ink.ptms.adyeshach.common.entity.editor.EditorHandler
import ink.ptms.adyeshach.common.entity.editor.MetaEditor
import ink.ptms.adyeshach.common.entity.manager.*
import ink.ptms.adyeshach.common.entity.manager.database.DatabaseLocal
import ink.ptms.adyeshach.common.entity.manager.database.DatabaseMongodb
import ink.ptms.adyeshach.common.entity.manager.database.DatabaseNull
import ink.ptms.adyeshach.common.entity.type.AdyHuman
import ink.ptms.adyeshach.common.util.serializer.Converter
import ink.ptms.adyeshach.common.util.serializer.Serializer
import ink.ptms.adyeshach.common.util.serializer.UnknownWorldException
import ink.ptms.adyeshach.common.util.safeDistance
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.WorldUnloadEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.onlinePlayers
import taboolib.common.platform.function.submit
import taboolib.common.util.nonPrimitive
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.KetherShell
import java.io.File
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer
import java.util.function.Function

object AdyeshachAPI {

    internal val betonQuestHooked by lazy { Bukkit.getPluginManager().getPlugin("BetonQuest") != null }
    internal val modelEngineHooked by lazy { Bukkit.getPluginManager().getPlugin("ModelEngine") != null }

    internal val onlinePlayerMap = CopyOnWriteArrayList<String>()
    internal val clientEntityMap = ConcurrentHashMap<String, MutableMap<Int, ClientEntity>>()

    internal val managerPrivate = ConcurrentHashMap<String, ManagerPrivate>()
    internal val managerPrivateTemporary = ConcurrentHashMap<String, ManagerPrivateTemp>()

    internal val managerPublic = ManagerPublic()
    internal val managerPublicTemporary = ManagerPublicTemp()

    internal val registeredEntityMeta = LinkedHashMap<Class<*>, ArrayList<Meta<*>>>()
    internal val registeredControllerGenerator = LinkedHashMap<String, ControllerGenerator>()

    internal val database by lazy {
        when (val db = Adyeshach.conf.getString("Database.method")!!.uppercase()) {
            "LOCAL" -> DatabaseLocal()
            "MONGODB" -> DatabaseMongodb()
            "DISABLE" -> DatabaseNull()
            else -> {
                val event = CustomDatabaseEvent(db)
                event.call()
                event.database ?: error("\"${Adyeshach.conf.getString("Database.method")}\" not supported.")
            }
        }
    }

    /**
     * 获取公共单位管理器
     */
    fun getEntityManagerPublic(): Manager {
        return managerPublic
    }

    /**
     * 获取临时的公共单位管理器
     */
    fun getEntityManagerPublicTemporary(): Manager {
        return managerPublicTemporary
    }

    /**
     * 获取私有单位管理器
     * @param player 玩家
     */
    fun getEntityManagerPrivate(player: Player): Manager {
        return managerPrivate.computeIfAbsent(player.name) { ManagerPrivate(player.name, database) }
    }

    /**
     * 获取临时的私有单位管理器
     * @param player 玩家
     */
    fun getEntityManagerPrivateTemporary(player: Player): Manager {
        return managerPrivateTemporary.computeIfAbsent(player.name) { ManagerPrivateTemp(player.name) }
    }

    /**
     * 从配置文件读取单位
     * @param section 原始 yaml 实例
     */
    @Throws(UnknownWorldException::class)
    fun fromYaml(section: ConfigurationSection, transfer: Function<String, String> = Function { it }): EntityInstance? {
        val conf = Configuration.empty(Type.JSON)
        section.getValues(true).forEach { (k, v) ->
            if (v !is ConfigurationSection) {
                conf[transfer.apply(k)] = v
            }
        }
        return fromJson(conf.saveToString())
    }

    /**
     * 从配置文件读取单位
     * @param source 序列化后的 yaml 文件
     */
    @Throws(UnknownWorldException::class)
    fun fromYaml(source: String): EntityInstance? {
        return fromJson(Converter.yamlToJson(Configuration.loadFromString(source)).toString())
    }

    /**
     * 从 Json 输入流中读取单位
     */
    @Throws(UnknownWorldException::class)
    fun fromJson(inputStream: InputStream): EntityInstance? {
        return fromJson(inputStream.readBytes().toString(StandardCharsets.UTF_8))
    }

    /**
     * 从 Json 文件中读取单位
     */
    @Throws(UnknownWorldException::class)
    fun fromJson(file: File): EntityInstance? {
        return fromJson(file.readText(StandardCharsets.UTF_8))
    }

    /**
     * 从 Json 序列化后文件中读取单位
     */
    @Throws(UnknownWorldException::class)
    fun fromJson(source: String): EntityInstance? {
        val entityType = try {
            EntityTypes.valueOf(JsonParser().parse(source).asJsonObject.get("entityType").asString)
        } catch (ex: UnknownWorldException) {
            throw ex
        } catch (t: Throwable) {
            t.printStackTrace()
            return null
        }
        return Serializer.gson.fromJson(source, entityType.entityBase)
    }

    /**
     * 获取玩家就近的单位
     * 包含公共、私有单位管理器中的单位
     */
    fun getEntityNearly(player: Player): EntityInstance? {
        return getEntity(player) { true }
    }

    /**
     * 通过实体 id 获取单位
     */
    fun getEntityFromEntityId(id: Int, player: Player? = null): EntityInstance? {
        return getEntity(player) { it.index == id }
    }

    /**
     * 通过单位 id 获取单位
     */
    fun getEntityFromId(id: String, player: Player? = null): EntityInstance? {
        return getEntity(player) { it.id == id }
    }

    /**
     * 通过 uuid 获取单位
     */
    fun getEntityFromUniqueId(id: String, player: Player? = null): EntityInstance? {
        return getEntity(player) { it.uniqueId == id }
    }

    /**
     * 通过 uuid 或单位 id 获取单位
     */
    fun getEntityFromUniqueIdOrId(id: String, player: Player? = null): EntityInstance? {
        return getEntity(player) { it.id == id || it.uniqueId == id }
    }

    /**
     * 通过玩家数据包中的 uuid 获取单位
     */
    fun getEntityFromClientUniqueId(player: Player, uniqueId: UUID): EntityInstance? {
        return clientEntityMap[player.name]?.values?.firstOrNull { it.clientId == uniqueId }?.entity
    }

    /**
     * 获取一个单位
     * @param filter 过滤
     */
    fun getEntity(player: Player? = null, filter: Function<EntityInstance, Boolean>): EntityInstance? {
        val entity = getEntities(player, filter)
        return if (player != null) entity.minByOrNull { it.position.toLocation().safeDistance(player.location) } else entity.firstOrNull()
    }

    /**
     * 获取多个单位
     * @param filter 过滤
     */
    fun getEntities(player: Player? = null, filter: Function<EntityInstance, Boolean>): List<EntityInstance> {
        val entity = ArrayList<EntityInstance>()
        entity.addAll(getEntityManagerPublic().getEntities().filter { filter.apply(it) })
        entity.addAll(getEntityManagerPublicTemporary().getEntities().filter { filter.apply(it) })
        if (player != null) {
            entity.addAll(getEntityManagerPrivate(player).getEntities().filter { filter.apply(it) })
            entity.addAll(getEntityManagerPrivateTemporary(player).getEntities().filter { filter.apply(it) })
        }
        return entity
    }

    /**
     * 获取所有可见单位
     */
    fun getVisibleEntities(player: Player): List<EntityInstance> {
        val distance = AdyeshachSettings.visibleDistance
        val entity = ArrayList<EntityInstance>()
        entity.addAll(getEntityManagerPublic().getEntities().filter { it.position.toLocation().safeDistance(player.location) <= distance })
        entity.addAll(getEntityManagerPublicTemporary().getEntities().filter { it.position.toLocation().safeDistance(player.location) <= distance })
        entity.addAll(getEntityManagerPrivate(player).getEntities().filter { it.position.toLocation().safeDistance(player.location) <= distance })
        entity.addAll(getEntityManagerPrivateTemporary(player).getEntities().filter { it.position.toLocation().safeDistance(player.location) <= distance })
        return entity
    }

    /**
     * 注册元数据模型（布尔值）
     */
    fun registerEntityMetaMask(type: Class<*>, index: Int, key: String, mask: Byte, def: Boolean = false) {
        val meta = MetaMasked<EntityInstance>(index, key, mask, def)
        val ge = EditorHandler.editorGenerator[Boolean::class.java.nonPrimitive()]
        if (ge != null) {
            meta.editor = MetaEditor(meta).also { ge.accept(it) }
        }
        registeredEntityMeta.computeIfAbsent(type) { ArrayList() } += meta
    }

    /**
     * 注册元数据模型（专业类型）
     */
    fun registerEntityMetaNatural(type: Class<*>, index: Int, key: String, def: Any, editor: Consumer<MetaEditor<*>>? = null) {
        val meta = MetaNatural<Any, EntityInstance>(index, key, def)
        val ge = editor ?: EditorHandler.editorGenerator[def.javaClass]
        if (ge != null) {
            meta.editor = MetaEditor(meta).also { ge.accept(it) }
        }
        registeredEntityMeta.computeIfAbsent(type) { ArrayList() } += meta
    }

    /**
     * 注册元数据模型（专业类型）编辑器
     */
    fun registerEntityMetaNaturalEditor(type: Class<*>, key: String, editor: Consumer<MetaEditor<*>>) {
        registerEntityMetaNatural(type, -2, key, 0, editor)
    }

    /**
     * 注册特定类型的 MetaEditor 生成器
     */
    fun registerEntityMetaEditorGenerator(vararg type: Class<*>, editor: Consumer<MetaEditor<*>>) {
        type.forEach { EditorHandler.editorGenerator[it] = editor }
    }

    /**
     * 注册 Controller 生成器
     */
    fun registerControllerGenerator(name: String, event: ControllerGenerator) {
        registeredControllerGenerator[name] = event
    }

    /**
     * 获取 Controller 生成器
     */
    fun getControllerGenerator(name: String): ControllerGenerator? {
        return registeredControllerGenerator.entries.firstOrNull { it.key.equals(name, true) }?.value
    }

    /**
     * 获取所有 Controller 生成器（副本）
     */
    fun getControllerGenerator(): Map<String, ControllerGenerator> {
        return registeredControllerGenerator.toMap()
    }

    /**
     * 创建全息
     */
    fun createHologram(player: Player, location: Location, content: List<String>): Hologram<*> {
        return Hologram.AdyeshachImpl().also { it.create(player, location, content) }
    }

    /**
     * 创建全息通告
     * 以全息形式发送位于世界中的提示信息
     */
    fun createHolographic(player: Player, location: Location, vararg message: String) {
        createHolographic(player, location, 40, { it }, *message)
    }

    /**
     * 创建全息通告
     * 以全息形式发送位于世界中的提示信息
     *
     * @param location 坐标
     * @param stay 停留时间
     * @param transfer 过渡转换
     * @param message 内容
     */
    fun createHolographic(player: Player, location: Location, stay: Long = 40L, transfer: Function<String, String> = Function { it }, vararg message: String) {
        val key = "${location.world!!.name},${location.x},${location.y},${location.z}"
        val messages = HolographicCache.holographicMap.computeIfAbsent(player.name) { ConcurrentHashMap() }
        if (messages.containsKey(key)) {
            return
        }
        val holographic = Holographic(player, location, message.toList()) { transfer.apply(it) }
        messages[key] = holographic
        submit(delay = stay) {
            messages.remove(key)
            holographic.cancel()
        }
    }

    fun invokeKether(source: String, player: Player, vars: Map<String, Any>): CompletableFuture<Any?> {
        val map = KetherShell.VariableMap(*vars.map { it.key to it.value }.toTypedArray())
        return KetherShell.eval(source, sender = adaptPlayer(player), namespace = listOf("adyeshach"), vars = map)
    }

    fun parseFunction(source: String, player: Player): String {
        return KetherFunction.parse(source, sender = adaptPlayer(player), namespace = listOf("adyeshach"))
    }

    @Awake(LifeCycle.DISABLE)
    internal fun onDisable() {
        onlinePlayers().forEach { database.push(it.cast()) }
    }

    @Schedule(delay = 200, period = 200)
    internal fun playerTextureRefresh200() {
        var i = 0L
        Bukkit.getOnlinePlayers().forEach {
            submit(async = true, delay = i++) {
                getVisibleEntities(it).filterIsInstance<AdyHuman>().forEach { human ->
                    human.refreshPlayerInfo(it)
                }
            }
        }
    }

    // 当世界被卸载时, 该世界的所有虚拟实体应当被清除
    @SubscribeEvent
    internal fun onWorldChange(e: WorldUnloadEvent) {
        getEntities { true }
            .filter { it.getWorld().uid == e.world.uid }
            .forEach {
                it.remove()
            }
    }

    @SubscribeEvent
    internal fun e(e: PlayerQuitEvent) {
        clientEntityMap.remove(e.player.name)
        onlinePlayerMap.remove(e.player.name)
        managerPrivate.remove(e.player.name)
        managerPrivateTemporary.remove(e.player.name)
        submit(async = true) {
            database.push(e.player)
            database.release(e.player)
        }
        e.player.shutdownPool()
    }
}