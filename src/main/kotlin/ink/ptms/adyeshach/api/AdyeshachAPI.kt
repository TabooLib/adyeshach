package ink.ptms.adyeshach.api

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.event.CustomDatabaseEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.manager.Manager
import ink.ptms.adyeshach.common.entity.manager.ManagerPrivate
import ink.ptms.adyeshach.common.entity.manager.ManagerPublic
import ink.ptms.adyeshach.common.util.serializer.Converter
import ink.ptms.adyeshach.common.util.serializer.Serializer
import ink.ptms.adyeshach.internal.database.DatabaseLocal
import ink.ptms.adyeshach.internal.database.DatabaseMongodb
import io.izzel.taboolib.internal.gson.JsonParser
import io.izzel.taboolib.module.db.local.SecuredFile
import io.izzel.taboolib.module.inject.PlayerContainer
import io.izzel.taboolib.util.Files
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.io.File
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap

object AdyeshachAPI {

    @PlayerContainer
    private val managerPrivate = ConcurrentHashMap<String, ManagerPrivate>()
    private val managerPublic = ManagerPublic()
    private val database by lazy {
        when (Adyeshach.conf.getString("Database.method")!!.toUpperCase()) {
            "LOCAL" -> DatabaseLocal()
            "METHOD" -> DatabaseMongodb()
            else -> CustomDatabaseEvent().call().database ?: throw RuntimeException("Storage method \"${Adyeshach.conf.getString("Database.method")}\" not supported.")
        }
    }

    fun getEntityManager(): Manager {
        return managerPublic
    }

    fun getEntityManager(player: Player): Manager {
        return managerPrivate.computeIfAbsent(player.name) { ManagerPrivate(player.name, database) }
    }

    fun fromYaml(section: ConfigurationSection, player: Player?): EntityInstance? {
        return fromJson(Converter.yamlToJson(section).toString(), player)
    }

    fun fromYaml(source: String, player: Player?): EntityInstance? {
        return fromJson(Converter.yamlToJson(SecuredFile.loadConfiguration(source)).toString(), player)
    }

    fun fromJson(inputStream: InputStream, player: Player?): EntityInstance? {
        var entityInstance: EntityInstance? = null
        Files.read(inputStream) {
            entityInstance = fromJson(it.readLines().joinToString(""), player)
        }
        return entityInstance
    }

    fun fromJson(file: File, player: Player?): EntityInstance? {
        var entityInstance: EntityInstance? = null
        Files.read(file) {
            entityInstance = fromJson(it.readLines().joinToString(""), player)
        }
        return entityInstance
    }

    fun fromJson(source: String, player: Player?): EntityInstance? {
        val entityType = try {
            EntityTypes.valueOf(JsonParser.parseString(source).asJsonObject.get("entityType").asString)
        } catch (t: Throwable) {
            t.printStackTrace()
            return null
        }
        return Serializer.gson.fromJson(source, entityType.entityBase)
    }
}