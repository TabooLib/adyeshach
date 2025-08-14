package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.serializer.UnknownWorldException
import org.bukkit.Bukkit
import taboolib.common.io.digest
import taboolib.common.io.newFile
import taboolib.common.io.newFolder
import taboolib.common.platform.function.getDataFolder
import java.io.File
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
                loadEntityFromFile(file)
            } catch (ex: UnknownWorldException) {
                if (AdyeshachSettings.isAutoDeleteWorld(ex.world)) {
                    file.delete()
                }
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }
        // 关联实体合法性验证
        activeEntity.forEach { it.verifyPassenger() }
    }

    override fun onSave() {
        activeEntity.forEach { entity ->
            // 不再保存衍生单位
            if (entity.isDerived()) {
                return@forEach
            }
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
        val file = newFile(getDataFolder(), "npc/${entityInstance.uniqueId}.json")
        if (file.exists()) {
            file.writeText(entityInstance.toJson())
            file.copyTo(File(getDataFolder(), "npc/trash/${entityInstance.uniqueId}.json"), overwrite = true)
            file.delete()
        }
        hash.remove(entityInstance.uniqueId)
    }

    override fun isTemporary(): Boolean {
        return false
    }

    override fun loadEntityFromFile(file: File): EntityInstance {
        val entity = super.loadEntityFromFile(file)
        if (entity.visibleAfterLoaded) {
            Bukkit.getOnlinePlayers().forEach { p -> entity.addViewer(p) }
        }
        return entity
    }
}