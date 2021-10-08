package ink.ptms.adyeshach.api

import com.google.gson.JsonParser
import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.event.CustomDatabaseEvent
import ink.ptms.adyeshach.common.entity.*
import ink.ptms.adyeshach.common.entity.ai.ControllerGenerator
import ink.ptms.adyeshach.common.entity.editor.EditorHandler
import ink.ptms.adyeshach.common.entity.editor.MetaEditor
import ink.ptms.adyeshach.common.entity.manager.*
import ink.ptms.adyeshach.common.entity.manager.database.DatabaseLocal
import ink.ptms.adyeshach.common.entity.manager.database.DatabaseMongodb
import ink.ptms.adyeshach.common.util.serializer.Converter
import ink.ptms.adyeshach.common.util.serializer.Serializer
import ink.ptms.adyeshach.common.util.serializer.UnknownWorldException
import ink.ptms.adyeshach.common.util.toDistance
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.platform.function.onlinePlayers
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.warning
import taboolib.common.util.nonPrimitive
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.SecuredFile
import java.io.File
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer
import java.util.function.Function
import kotlin.collections.LinkedHashMap

object AdyeshachAPI {

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
            else -> {
                val event = CustomDatabaseEvent(db)
                event.call()
                event.database ?: error("\"${Adyeshach.conf.getString("Database.method")}\" not supported.")
            }
        }
    }

    fun getEntityManagerPublic(): Manager {
        return managerPublic
    }

    fun getEntityManagerPublicTemporary(): Manager {
        return managerPublicTemporary
    }

    fun getEntityManagerPrivate(player: Player): Manager {
        return managerPrivate.computeIfAbsent(player.name) { ManagerPrivate(player.name, database) }
    }

    fun getEntityManagerPrivateTemporary(player: Player): Manager {
        return managerPrivateTemporary.computeIfAbsent(player.name) { ManagerPrivateTemp(player.name) }
    }

    @Throws(UnknownWorldException::class)
    fun fromYaml(section: ConfigurationSection): EntityInstance? {
        return fromJson(Converter.yamlToJson(section).toString())
    }

    @Throws(UnknownWorldException::class)
    fun fromYaml(source: String): EntityInstance? {
        return fromJson(Converter.yamlToJson(SecuredFile.loadConfiguration(source)).toString())
    }

    @Throws(UnknownWorldException::class)
    fun fromJson(inputStream: InputStream): EntityInstance? {
        return fromJson(inputStream.readBytes().toString(StandardCharsets.UTF_8))
    }

    @Throws(UnknownWorldException::class)
    fun fromJson(file: File): EntityInstance? {
        return fromJson(file.readText(StandardCharsets.UTF_8))
    }

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

    fun getEntityNearly(player: Player): EntityInstance? {
        return getEntity(player) { true }
    }

    fun getEntityFromEntityId(id: Int, player: Player? = null): EntityInstance? {
        return getEntity(player) { it.index == id }
    }

    fun getEntityFromId(id: String, player: Player? = null): EntityInstance? {
        return getEntity(player) { it.id == id }
    }

    fun getEntityFromUniqueId(id: String, player: Player? = null): EntityInstance? {
        return getEntity(player) { it.uniqueId == id }
    }

    fun getEntityFromUniqueIdOrId(id: String, player: Player? = null): EntityInstance? {
        return getEntity(player) { it.id == id || it.uniqueId == id }
    }

    fun getEntityFromClientUniqueId(player: Player, uniqueId: UUID): EntityInstance? {
        return clientEntityMap[player.name]?.values?.firstOrNull { it.clientId == uniqueId }?.entity
    }

    fun getEntity(player: Player? = null, filter: Function<EntityInstance, Boolean>): EntityInstance? {
        val entity = getEntities(player, filter)
        return if (player != null) entity.minByOrNull { it.position.toLocation().toDistance(player.location) } else entity.firstOrNull()
    }

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

    @Awake(LifeCycle.DISABLE)
    internal fun e() {
        onlinePlayers().forEach { database.push(it.cast()) }
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
    }
}