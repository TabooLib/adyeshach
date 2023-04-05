package ink.ptms.adyeshach.impl.description

import taboolib.common5.cbool
import taboolib.common5.cbyte
import taboolib.common5.cfloat
import taboolib.common5.cint

/**
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
class PrepareMetaTypeGeneric(val type: GenericType) : PrepareMetaType {

    override fun parse(name: String, args: List<String>): PrepareMeta {
        return when (type) {
            GenericType.INT -> PrepareMetaNatural(name, args.getOrNull(1).cint, "Int")
            GenericType.FLOAT -> PrepareMetaNatural(name, args.getOrNull(1).cfloat, "Float")
            GenericType.BOOLEAN -> PrepareMetaNatural(name, args.getOrNull(1).cbool, "Boolean")
            GenericType.STRING -> PrepareMetaNatural(name, args.getOrNull(1) ?: "", "String")
            GenericType.BYTE -> PrepareMetaNatural(name, args.getOrNull(1).cbyte, "Byte")
            GenericType.BYTE_MASKED -> PrepareMetaMasked(name, args[0].substring(0, args[0].length - 1).cbyte, args.getOrNull(1).cbool)
        }
    }
}