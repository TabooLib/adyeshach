package ink.ptms.adyeshach.core.entity

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.MetaMasked
 *
 * @author 坏黑
 * @since 2022/6/16 23:06
 */
abstract class MetaMasked<T : EntityInstance>(index: Int, key: String, group: String, val mask: Byte, def: Boolean) : Meta<T>(index, key, group, def)