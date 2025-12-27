package ink.ptms.adyeshach.core.entity

import ink.ptms.adyeshach.core.entity.manager.Manager
import java.util.concurrent.ConcurrentSkipListSet

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.EntityRefSet
 *
 * 一个支持延迟解析的实体引用集合。
 * - 反序列化时存储 UUID 字符串到 pendingUuids
 * - 调用 resolve() 后将 UUID 解析为 EntityInstance
 * - 序列化时从 instances 提取 UUID 输出（仅同 manager 的实体）
 * - 支持跨 manager 的实体引用（运行时有效，但不会被持久化）
 */
class EntityRefSet(
    /** 拥有此集合的实体（用于序列化时过滤同 manager 的实体） */
    var owner: EntityInstance? = null
) {

    /** 待解析的 UUID 列表（反序列化后、resolve 前使用） */
    val pendingUuids = ConcurrentSkipListSet<String>()

    /** 已解析的实体实例 */
    val instances = ConcurrentSkipListSet<EntityInstance>(compareBy { it.uniqueId })

    /** 返回所有 UUID（优先从 instances 获取，否则返回 pendingUuids）
     *  序列化时只返回与 owner 同一个 manager 的实体（跨 manager 的引用不持久化）
     */
    fun getUuids(): Set<String> {
        return if (instances.isNotEmpty()) {
            val ownerManager = owner?.manager
            instances
                .filter { ownerManager == null || it.manager == ownerManager }
                .mapTo(linkedSetOf()) { it.uniqueId }
        } else {
            pendingUuids.toSet()
        }
    }

    /** 添加实体实例 */
    fun add(entity: EntityInstance): Boolean {
        pendingUuids.remove(entity.uniqueId)
        return instances.add(entity)
    }

    /** 移除实体实例 */
    fun remove(entity: EntityInstance): Boolean {
        pendingUuids.remove(entity.uniqueId)
        return instances.remove(entity)
    }

    /** 检查是否包含指定实体 */
    fun contains(entity: EntityInstance): Boolean {
        return instances.contains(entity) || pendingUuids.contains(entity.uniqueId)
    }

    /** 检查是否包含指定 UUID */
    fun containsUuid(uuid: String): Boolean {
        return pendingUuids.contains(uuid) || instances.any { it.uniqueId == uuid }
    }

    /** 是否为空 */
    fun isEmpty(): Boolean = instances.isEmpty() && pendingUuids.isEmpty()

    /** 是否非空 */
    fun isNotEmpty(): Boolean = !isEmpty()

    /** 返回元素数量（包括已解析和待解析的） */
    val size: Int get() = instances.size + pendingUuids.size

    /** 清空所有数据 */
    fun clear() {
        pendingUuids.clear()
        instances.clear()
    }

    /** 遍历所有 UUID（包括已解析和待解析的） */
    fun forEachUuid(action: (String) -> Unit) {
        instances.forEach { action(it.uniqueId) }
        pendingUuids.forEach(action)
    }

    /**
     * 解析待处理的 UUID，将其转换为 EntityInstance
     * @param manager 实体管理器
     */
    fun resolve(manager: Manager) {
        val iterator = pendingUuids.iterator()
        while (iterator.hasNext()) {
            val uuid = iterator.next()
            val entity = manager.getEntityByUniqueId(uuid)
            if (entity != null) {
                instances.add(entity)
                iterator.remove()
            }
        }
    }

    /**
     * 验证并清理无效的实体引用
     * 保留 instances 中仍然有效的实体，其余清除
     */
    fun verify() {
        // 只保留有效的实体引用
        val validInstances = instances.filter { !it.isRemoved }
        instances.clear()
        instances.addAll(validInstances)
        // 清空 pendingUuids，因为已经不需要了
        pendingUuids.clear()
    }

    override fun toString(): String {
        return "EntityRefSet(instances=${instances.map { it.uniqueId }}, pending=$pendingUuids)"
    }
}
