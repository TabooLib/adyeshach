package ink.ptms.adyeshach.core.entity

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.MetaNatural
 *
 * @author 坏黑
 * @since 2022/6/16 23:07
 */
abstract class MetaNatural<T, E : EntityInstance>(index: Int, key: String, group: String, def: T) : Meta<E>(index, key, group, def!!)