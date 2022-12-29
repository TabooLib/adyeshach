package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.serializer.UnknownWorldException
import org.bukkit.Bukkit
import taboolib.common.io.digest
import taboolib.common.io.newFile
import taboolib.common.io.newFolder
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.warning
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.manager.LocalPersistentManager
 *
 * @author 坏黑
 * @since 2022/12/29 15:37
 */
open class LocalPersistentManager : DefaultManager() {

    val hash = ConcurrentHashMap<String, String>()

    override fun onEnable() {
        activeEntity.clear()
        newFolder(getDataFolder(), "npc").listFiles()?.filter { it.extension == "json" }?.forEach { file ->
            try {
                val entity = Adyeshach.api().getEntitySerializer().fromJson(file.readText(StandardCharsets.UTF_8))
                if (Adyeshach.api().getEntityTypeRegistry().getBukkitEntityTypeOrNull(entity.entityType) == null) {
                    warning("Entity \"${entity.entityType.name}\" not supported this minecraft version.")
                } else {
                    entity.manager = this
                    activeEntity += entity
                    hash[entity.uniqueId] = entity.toJson().digest("sha-1")
                    if (entity.visibleAfterLoaded) {
                        Bukkit.getOnlinePlayers().forEach { p -> entity.addViewer(p) }
                    }
                }
            } catch (ex: UnknownWorldException) {
                if (AdyeshachSettings.isAutoDeleteWorld(ex.world)) {
                    file.delete()
                }
            }
        }
    }

    override fun onSave() {
        activeEntity.forEach { entity ->
            val json = entity.toJson()
            val jsonHash = json.digest("sha-1")
            if (hash[entity.uniqueId] != jsonHash) {
                hash[entity.uniqueId] = jsonHash
                newFile(getDataFolder(), "npc/${entity.uniqueId}.json").writeText(json)
            }
        }
    }

    override fun remove(entityInstance: EntityInstance) {
        super.remove(entityInstance)
        val file = File(getDataFolder(), "npc/${entityInstance.uniqueId}.json")
        if (file.exists()) {
            file.copyTo(File(getDataFolder(), "npc/trash/${entityInstance.uniqueId}.json"))
            file.delete()
        }
        hash.remove(entityInstance.uniqueId)
    }

    override fun isTemporary(): Boolean {
        return false
    }
}