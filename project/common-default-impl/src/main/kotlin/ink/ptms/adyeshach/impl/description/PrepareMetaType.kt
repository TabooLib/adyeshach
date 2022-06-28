package ink.ptms.adyeshach.impl.description

/**
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
interface PrepareMetaType {

    fun parse(name: String, args: List<String>): PrepareMeta
}