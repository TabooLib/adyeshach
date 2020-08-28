package ink.ptms.adyeshach.api

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.event.CustomDatabaseEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.manager.*
import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import ink.ptms.adyeshach.common.util.serializer.Converter
import ink.ptms.adyeshach.common.util.serializer.Serializer
import ink.ptms.adyeshach.internal.database.DatabaseLocal
import ink.ptms.adyeshach.internal.database.DatabaseMongodb
import ink.ptms.adyeshach.internal.listener.ListenerArmorStand
import io.izzel.taboolib.internal.gson.JsonParser
import io.izzel.taboolib.module.db.local.SecuredFile
import io.izzel.taboolib.module.inject.PlayerContainer
import io.izzel.taboolib.util.Files
import io.izzel.taboolib.util.KV
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.io.File
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

object AdyeshachAPI {

    @PlayerContainer
    val onlinePlayers = CopyOnWriteArrayList<String>()
    @PlayerContainer
    private val managerPrivate = ConcurrentHashMap<String, ManagerPrivate>()
    private val managerPrivateTemp = ConcurrentHashMap<String, ManagerPrivateTemp>()
    private val managerPublic = ManagerPublic()
    private val managerPublicTemp = ManagerPublicTemp()
    private val database by lazy {
        when (Adyeshach.conf.getString("Database.method")!!.toUpperCase()) {
            "LOCAL" -> DatabaseLocal()
            "METHOD" -> DatabaseMongodb()
            else -> CustomDatabaseEvent().call().database ?: throw RuntimeException("Storage method \"${Adyeshach.conf.getString("Database.method")}\" not supported.")
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

    fun getEntityFromEntityId(id: Int, player: Player? = null): EntityInstance? {
        var entity: EntityInstance? = null
        if (entity == null) {
            entity = getEntityManagerPublic().getEntities().firstOrNull { it.index == id }
        }
        if (entity == null && player != null) {
            entity = getEntityManagerPrivate(player).getEntities().firstOrNull { it.index == id }
        }
        if (entity == null) {
            entity = getEntityManagerPublicTemporary().getEntities().firstOrNull { it.index == id }
        }
        if (entity == null && player != null) {
            entity = getEntityManagerPrivateTemporary(player).getEntities().firstOrNull { it.index == id }
        }
        return entity
    }

    fun getEntityFromUniqueId(id: String, player: Player? = null): EntityInstance? {
        var entity: EntityInstance? = null
        if (entity == null) {
            entity = getEntityManagerPublic().getEntities().firstOrNull { it.uniqueId == id }
        }
        if (entity == null && player != null) {
            entity = getEntityManagerPrivate(player).getEntities().firstOrNull { it.uniqueId == id }
        }
        if (entity == null) {
            entity = getEntityManagerPublicTemporary().getEntities().firstOrNull { it.uniqueId == id }
        }
        if (entity == null && player != null) {
            entity = getEntityManagerPrivateTemporary(player).getEntities().firstOrNull { it.uniqueId == id }
        }
        return entity
    }
}