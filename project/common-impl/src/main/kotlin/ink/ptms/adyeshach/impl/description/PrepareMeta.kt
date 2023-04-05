package ink.ptms.adyeshach.impl.description

/**
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
abstract class PrepareMeta(val name: String) {

    abstract fun register(entityClass: Class<*>, index: Int, group: String)

    override fun toString(): String {
        return name
    }
}