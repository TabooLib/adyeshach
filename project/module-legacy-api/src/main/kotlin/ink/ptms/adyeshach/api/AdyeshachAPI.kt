package ink.ptms.adyeshach.api

import ink.ptms.adyeshach.common.entity.*
import ink.ptms.adyeshach.common.entity.ai.ControllerGenerator
import ink.ptms.adyeshach.common.entity.manager.*
import ink.ptms.adyeshach.common.util.serializer.UnknownWorldException
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.manager.ManagerType
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.function.*
import taboolib.library.configuration.ConfigurationSection
import java.io.File
import java.io.InputStream
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Function

@Deprecated("Outdated but usable")
object AdyeshachAPI {

    fun getEntityManagerPublic(): Manager {
        return LegacyManager(Adyeshach.api().getPublicEntityManager(ManagerType.PERSISTENT))
    }

    fun getEntityManagerPublicTemporary(): Manager {
        return LegacyManager(Adyeshach.api().getPublicEntityManager(ManagerType.TEMPORARY))
    }

    fun getEntityManagerPrivate(player: Player): Manager {
        return LegacyManager(Adyeshach.api().getPrivateEntityManager(player, ManagerType.PERSISTENT))
    }

    fun getEntityManagerPrivateTemporary(player: Player): Manager {
        return LegacyManager(Adyeshach.api().getPrivateEntityManager(player, ManagerType.TEMPORARY))
    }

    @Throws(UnknownWorldException::class)
    fun fromYaml(section: ConfigurationSection, transfer: Function<String, String> = Function { it }): EntityInstance? {
        val v2 = Adyeshach.api().getEntitySerializer().fromYaml(section.toString(), transfer)
        return EntityTypes.adapt(v2)
    }

    @Throws(UnknownWorldException::class)
    fun fromYaml(source: String): EntityInstance? {
        val v2 = Adyeshach.api().getEntitySerializer().fromYaml(source)
        return EntityTypes.adapt(v2)
    }

    @Throws(UnknownWorldException::class)
    fun fromJson(inputStream: InputStream): EntityInstance? {
        val v2 = Adyeshach.api().getEntitySerializer().fromJson(inputStream.readBytes().decodeToString())
        return EntityTypes.adapt(v2)
    }

    @Throws(UnknownWorldException::class)
    fun fromJson(file: File): EntityInstance? {
        val v2 = Adyeshach.api().getEntitySerializer().fromJson(file.readText())
        return EntityTypes.adapt(v2)
    }

    @Throws(UnknownWorldException::class)
    fun fromJson(source: String): EntityInstance? {
        val v2 = Adyeshach.api().getEntitySerializer().fromJson(source)
        return EntityTypes.adapt(v2)
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
        val v2 = Adyeshach.api().getEntityFinder().getEntityFromClientUniqueId(uniqueId, player) ?: return null
        return EntityTypes.adapt(v2)
    }

    fun getEntity(player: Player? = null, filter: Function<EntityInstance, Boolean>): EntityInstance? {
        return Adyeshach.api().getEntityFinder().getEntity(player) {
            EntityTypes.adapt(it)?.let { entity -> filter.apply(entity) } ?: false
        }?.let {
            EntityTypes.adapt(it)!!
        }
    }

    fun getEntities(player: Player? = null, filter: Function<EntityInstance, Boolean>): List<EntityInstance> {
        return Adyeshach.api().getEntityFinder().getEntities(player) {
            EntityTypes.adapt(it)?.let { entity -> filter.apply(entity) } ?: false
        }.map {
            EntityTypes.adapt(it)!!
        }
    }

    fun getVisibleEntities(player: Player): List<EntityInstance> {
        return Adyeshach.api().getEntityFinder().getVisibleEntities(player).map { EntityTypes.adapt(it)!! }
    }

//    fun registerEntityMetaMask(type: Class<*>, index: Int, key: String, mask: Byte, def: Boolean = false) {
//    }
//
//    fun registerEntityMetaNatural(type: Class<*>, index: Int, key: String, def: Any, editor: Consumer<MetaEditor<*>>? = null) {
//    }
//
//    fun registerEntityMetaNaturalEditor(type: Class<*>, key: String, editor: Consumer<MetaEditor<*>>) {
//    }
//
//    fun registerEntityMetaEditorGenerator(vararg type: Class<*>, editor: Consumer<MetaEditor<*>>) {
//    }

    fun registerControllerGenerator(name: String, event: ControllerGenerator) {
        IllegalStateException("Outdated api is being called, please contact the plugin author to update.").printStackTrace()
    }

    fun getControllerGenerator(name: String): ControllerGenerator? {
        IllegalStateException("Outdated api is being called, please contact the plugin author to update.").printStackTrace()
        return null
    }

    fun getControllerGenerator(): Map<String, ControllerGenerator> {
        IllegalStateException("Outdated api is being called, please contact the plugin author to update.").printStackTrace()
        return emptyMap()
    }

    fun createHologram(location: Location, content: List<String>): Hologram<*> {
        return Hologram.AdyeshachImpl().also { it.create(location, content) }
    }

    fun createHologram(player: Player, location: Location, content: List<String>): Hologram<*> {
        return Hologram.AdyeshachImpl().also { it.create(player, location, content) }
    }

    fun createHolographic(player: Player, location: Location, vararg message: String) {
        createHolographic(player, location, 40, { it }, *message)
    }

    fun createHolographic(player: Player, location: Location, stay: Long = 40L, transfer: Function<String, String> = Function { it }, vararg message: String) {
        Adyeshach.api().getHologramHandler().sendHologramMessage(player, location, message.toList(), stay)
    }

    fun invokeKether(source: String, player: Player? = null, vars: Map<String, Any> = emptyMap()): CompletableFuture<Any?> {
        return Adyeshach.api().getKetherHandler().invoke(source, player, vars)
    }

    fun parseFunction(source: String, player: Player? = null, vars: Map<String, Any> = emptyMap()): String {
        return Adyeshach.api().getKetherHandler().parseInline(source, player, vars)
    }
}