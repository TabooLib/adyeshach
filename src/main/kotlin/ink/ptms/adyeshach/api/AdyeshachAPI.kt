package ink.ptms.adyeshach.api

import com.google.gson.JsonParser
import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.event.CustomDatabaseEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.manager.*
import ink.ptms.adyeshach.common.script.KnownController
import ink.ptms.adyeshach.common.script.ScriptHandler
import ink.ptms.adyeshach.common.util.serializer.Converter
import ink.ptms.adyeshach.common.util.serializer.Serializer
import ink.ptms.adyeshach.common.util.serializer.UnknownWorldException
import ink.ptms.adyeshach.internal.database.DatabaseLocal
import ink.ptms.adyeshach.internal.database.DatabaseMongodb
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.*
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.SecuredFile
import java.io.File
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList

object AdyeshachAPI {

    val onlinePlayers = CopyOnWriteArrayList<String>()

    private val managerPrivate = ConcurrentHashMap<String, ManagerPrivate>()
    private val managerPrivateTemp = ConcurrentHashMap<String, ManagerPrivateTemp>()
    private val managerPublic = ManagerPublic()
    private val managerPublicTemp = ManagerPublicTemp()
    private val database by lazy {
        when (val db = Adyeshach.conf.getString("Database.method")!!.uppercase(Locale.getDefault())) {
            "LOCAL" -> DatabaseLocal()
            "MONGODB" -> DatabaseMongodb()
            else -> {
                val event = CustomDatabaseEvent(db)
                event.call()
                event.database ?: error("Storage method \"${Adyeshach.conf.getString("Database.method")}\" not supported.")
            }
        }
    }

    fun getEntityManagerPublic(): Manager {
        return managerPublic
    }

    fun getEntityManagerPublicTemporary(): Manager {
        return managerPublicTemp
    }

    fun getEntityManagerPrivate(player: Player): Manager {
        return managerPrivate.computeIfAbsent(player.name) { ManagerPrivate(player.name, database) }
    }

    fun getEntityManagerPrivateTemporary(player: Player): Manager {
        return managerPrivateTemp.computeIfAbsent(player.name) { ManagerPrivateTemp(player.name) }
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

    fun getEntity(player: Player? = null, filter: (EntityInstance) -> Boolean): EntityInstance? {
        val entity = getEntities(player, filter)
        return if (player != null) entity.minByOrNull { it.position.toLocation().toDistance(player.location) } else entity.firstOrNull()
    }

    fun getEntities(player: Player? = null, filter: (EntityInstance) -> Boolean): List<EntityInstance> {
        val entity = ArrayList<EntityInstance>()
        entity.addAll(getEntityManagerPublic().getEntities().filter { filter(it) })
        entity.addAll(getEntityManagerPublicTemporary().getEntities().filter { filter(it) })
        if (player != null) {
            entity.addAll(getEntityManagerPrivate(player).getEntities().filter { filter(it) })
            entity.addAll(getEntityManagerPrivateTemporary(player).getEntities().filter { filter(it) })
        }
        return entity
    }

    fun registerKnownController(name: String, event: KnownController) {
        ScriptHandler.controllers[name] = event
    }

    fun getKnownController(name: String): KnownController? {
        return ScriptHandler.getKnownController(name)
    }

    fun getKnownController(): Map<String, KnownController> {
        return ScriptHandler.controllers
    }

    fun Location.toDistance(loc: Location): Double {
        return if (this.world!!.name == loc.world!!.name) {
            this.distance(loc)
        } else {
            Double.MAX_VALUE
        }
    }

    @Awake(LifeCycle.DISABLE)
    private fun e() {
        onlinePlayers().forEach { database.push(it.cast()) }
    }

    @SubscribeEvent
    private fun e(e: PlayerQuitEvent) {
        onlinePlayers.remove(e.player.name)
        managerPrivate.remove(e.player.name)
        managerPrivateTemp.remove(e.player.name)
        submit(async = true) {
            database.push(e.player)
            database.release(e.player)
        }
    }
}