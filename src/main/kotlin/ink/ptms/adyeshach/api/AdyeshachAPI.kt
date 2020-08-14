package ink.ptms.adyeshach.api

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.event.CustomDatabaseInitEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.common.util.serializer.Converter
import ink.ptms.adyeshach.common.util.serializer.Serializer
import ink.ptms.adyeshach.internal.database.impl.DatabaseLocal
import ink.ptms.adyeshach.internal.database.impl.DatabaseMongodb
import io.izzel.taboolib.internal.gson.JsonParser
import io.izzel.taboolib.module.db.local.SecuredFile
import io.izzel.taboolib.util.Files
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.io.File
import java.io.InputStream
import java.lang.RuntimeException

object AdyeshachAPI {

    val database by lazy {
        when (Adyeshach.conf.getString("Database.method")!!.toUpperCase()) {
            "LOCAL" -> DatabaseLocal()
            "METHOD" -> DatabaseMongodb()
            else -> CustomDatabaseInitEvent().call().database ?: throw RuntimeException("Storage method \"${Adyeshach.conf.getString("Database.method")}\" not supported.")
        }
    }

    fun spawn(entityTypes: EntityTypes, player: Player, location: Location): EntityInstance {
        return spawn(entityTypes, player, location) { }
    }

    fun spawn(entityTypes: EntityTypes, player: Player, location: Location, function: (EntityInstance) -> (Unit)): EntityInstance {
        if (entityTypes.bukkitType == null) {
            throw RuntimeException("Entity \"${entityTypes.name}\" not supported this minecraft version.")
        }
        val entityInstance = entityTypes.entityBase.newInstance()
        function.invoke(entityInstance)
        entityInstance.owner = player
        entityInstance.spawn(location)
        entityInstance.updateMetadata()
        entityInstance.setHeadRotation(location.yaw, location.pitch)
        return entityInstance
    }

    fun fromYaml(section: ConfigurationSection, player: Player): EntityInstance? {
        return fromJson(Converter.yamlToJson(section).toString(), player)
    }

    fun fromYaml(source: String, player: Player): EntityInstance? {
        return fromJson(Converter.yamlToJson(SecuredFile.loadConfiguration(source)).toString(), player)
    }

    fun fromJson(inputStream: InputStream, player: Player): EntityInstance? {
        var entityInstance: EntityInstance? = null
        Files.read(inputStream) {
            entityInstance = fromJson(it.readLines().joinToString(""), player)
        }
        return entityInstance
    }

    fun fromJson(file: File, player: Player): EntityInstance? {
        var entityInstance: EntityInstance? = null
        Files.read(file) {
            entityInstance = fromJson(it.readLines().joinToString(""), player)
        }
        return entityInstance
    }

    fun fromJson(source: String, player: Player): EntityInstance? {
        val entityType = try {
            EntityTypes.valueOf(JsonParser.parseString(source).asJsonObject.get("entityType").asString)
        } catch (t: Throwable) {
            t.printStackTrace()
            return null
        }
        return Serializer.gson.fromJson(source, entityType.entityBase).run {
            this.owner = player
            this
        }
    }
}