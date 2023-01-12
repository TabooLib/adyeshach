package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.serializer.UnknownWorldException
import ink.ptms.adyeshach.impl.storage.EntityStorage
import org.bukkit.entity.Player
import taboolib.common.io.digest
import taboolib.common.platform.function.warning
import java.util.concurrent.ConcurrentHashMap

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.manager.PlayerPersistentManager
 *
 * @author 坏黑
 * @since 2023/1/10 20:24
 */
@Deprecated("1.0 保留功能")
class PlayerPersistentManager(owner: Player) : DefaultPlayerManager(owner) {

    val hash = ConcurrentHashMap<String, String>()

    override fun onEnable() {
        activeEntity.clear()
        // 异步加载
        val data = EntityStorage.database.get(owner)
        val conf = data.getConfigurationSection("AdyeshachNPC") ?: return
        conf.getKeys(false).forEach {
            try {
                // 反序列化单位
                val entity = Adyeshach.api().getEntitySerializer().fromSection(conf.getConfigurationSection(it)!!) { k ->
                    // 2021/12/05 因为发现在 MongoDB 中无法使用 $ 符号，因此进行转换
                    if (k.startsWith("_mark_")) "$${k.substring("_mark_".length)}" else k
                }
                // 生成单位
                if (Adyeshach.api().getEntityTypeRegistry().getBukkitEntityTypeOrNull(entity.entityType) == null) {
                    warning("Entity \"${entity.entityType.name}\" not supported this minecraft version.")
                } else {
                    entity.manager = this
                    entity.addViewer(owner)
                    add(entity)
                }
            } catch (_: UnknownWorldException) {
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }
    }

    override fun onSave() {
        val data = EntityStorage.database.get(owner)
        activeEntity.forEach { entity ->
            val json = entity.toJson()
            val jsonHash = json.digest("sha-1")
            if (hash[entity.uniqueId] != jsonHash) {
                hash[entity.uniqueId] = jsonHash
                val section = data.getConfigurationSection("AdyeshachNPC.${entity.uniqueId}") ?: data.createSection("AdyeshachNPC.${entity.uniqueId}")
                entity.toSection(section) { k -> if (k.startsWith("$")) "_mark_${k.substring(1)}" else k }
            }
        }
        // 同步数据
        EntityStorage.database.update(owner)
    }

    override fun remove(entityInstance: EntityInstance) {
        super.remove(entityInstance)
        val data = EntityStorage.database.get(owner)
        data["AdyeshachNPC.${entityInstance.uniqueId}"] = null
        hash.remove(entityInstance.uniqueId)
    }

    override fun isTemporary(): Boolean {
        return false
    }
}