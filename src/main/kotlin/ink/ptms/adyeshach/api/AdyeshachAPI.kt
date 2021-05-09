package ink.ptms.adyeshach.api

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.event.CustomDatabaseEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.manager.*
import ink.ptms.adyeshach.common.script.KnownController
import ink.ptms.adyeshach.common.script.ScriptHandler
import ink.ptms.adyeshach.common.util.serializer.Converter
import ink.ptms.adyeshach.common.util.serializer.Serializer
import ink.ptms.adyeshach.internal.database.DatabaseLocal
import ink.ptms.adyeshach.internal.database.DatabaseMongodb
import io.izzel.taboolib.internal.gson.JsonParser
import io.izzel.taboolib.kotlin.Mirror
import io.izzel.taboolib.kotlin.MirrorData
import io.izzel.taboolib.module.db.local.SecuredFile
import io.izzel.taboolib.module.inject.PlayerContainer
import io.izzel.taboolib.util.Files
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.io.File
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

object AdyeshachAPI {

    val mirror = Mirror()

    @PlayerContainer
    val onlinePlayers = CopyOnWriteArrayList<String>()

    @PlayerContainer
    private val managerPrivate = ConcurrentHashMap<String, ManagerPrivate>()
    private val managerPrivateTemp = ConcurrentHashMap<String, ManagerPrivateTemp>()
    private val managerPublic = ManagerPublic()
    private val managerPublicTemp = ManagerPublicTemp()
    private val database by lazy {
        when (val db = Adyeshach.conf.getString("Database.method")!!.toUpperCase()) {
            "LOCAL" -> DatabaseLocal()
            "MONGODB" -> DatabaseMongodb()
            else -> CustomDatabaseEvent(db).call().database ?: throw RuntimeException("Storage method \"${Adyeshach.conf.getString("Database.method")}\" not supported.")
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

    fun fromYaml(section: ConfigurationSection): EntityInstance? {
        return fromJson(Converter.yamlToJson(section).toString())
    }

    fun fromYaml(source: String): EntityInstance? {
        return fromJson(Converter.yamlToJson(SecuredFile.loadConfiguration(source)).toString())
    }

    fun fromJson(inputStream: InputStream): EntityInstance? {
        var entityInstance: EntityInstance? = null
        Files.read(inputStream) {
            entityInstance = fromJson(it.readLines().joinToString(""))
        }
        return entityInstance
    }

    fun fromJson(file: File): EntityInstance? {
        var entityInstance: EntityInstance? = null
        Files.read(file) {
            entityInstance = fromJson(it.readLines().joinToString(""))
        }
        return entityInstance
    }

    fun fromJson(source: String): EntityInstance? {
        val entityType = try {
            EntityTypes.valueOf(JsonParser.parseString(source).asJsonObject.get("entityType").asString)
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
        val entity = ArrayList<EntityInstance>()
        entity.addAll(getEntityManagerPublic().getEntities().filter { filter(it) })
        entity.addAll(getEntityManagerPublicTemporary().getEntities().filter { filter(it) })
        if (player != null) {
            entity.addAll(getEntityManagerPrivate(player).getEntities().filter { filter(it) })
            entity.addAll(getEntityManagerPrivateTemporary(player).getEntities().filter { filter(it) })
        }
        return if (player != null) entity.minByOrNull { it.position.toLocation().toDistance(player.location) } else entity.firstOrNull()
    }

    fun registerKnownController(name: String, event: KnownController) {
        ScriptHandler.knownControllers[name] = event
    }

    fun getKnownController(name: String): KnownController? {
        return ScriptHandler.getKnownController(name)
    }

    fun getKnownController(): Map<String, KnownController> {
        return ScriptHandler.knownControllers
    }

    fun Location.toDistance(loc: Location): Double {
        return if (this.world!!.name == loc.world!!.name) {
            this.distance(loc)
        } else {
            Double.MAX_VALUE
        }
    }

    fun mirrorFuture(id: String, func: Mirror.MirrorFuture.() -> Unit) {
        mirror.mirrorFuture(id, func)
    }

    fun mirrorFinish(id: String, time: Long) {
        mirror.dataMap.computeIfAbsent(id) { MirrorData() }.finish(time)
    }
}