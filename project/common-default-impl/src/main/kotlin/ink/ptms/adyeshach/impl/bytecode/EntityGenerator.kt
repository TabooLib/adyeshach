package ink.ptms.adyeshach.impl.bytecode

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.bytecode.EntityGenerator
 *
 * @author 坏黑
 * @since 2022/6/19 17:10
 */
interface EntityGenerator {

    /**
     * 生成实体类
     */
    fun generate(className: String, baseClass: String, interfaces: List<String>): ByteArray
}