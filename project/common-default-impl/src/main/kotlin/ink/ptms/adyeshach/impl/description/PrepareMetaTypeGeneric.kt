package ink.ptms.adyeshach.impl.description

import taboolib.common5.Coerce

/**
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
class PrepareMetaTypeGeneric(val type: GenericType) : PrepareMetaType {

    override fun parse(name: String, args: List<String>): PrepareMeta {
        return when (type) {
            GenericType.INT -> PrepareMetaNatural(name, Coerce.toInteger(args.getOrNull(1) ?: 0))
            GenericType.FLOAT -> PrepareMetaNatural(name, Coerce.toFloat(args.getOrNull(1) ?: 0))
            GenericType.BOOLEAN -> PrepareMetaNatural(name, Coerce.toBoolean(args.getOrNull(1) ?: false))
            GenericType.STRING -> PrepareMetaNatural(name, args.getOrNull(1) ?: "")
            GenericType.BYTE -> PrepareMetaNatural(name, Coerce.toBoolean(args.getOrNull(1) ?: 0))
            GenericType.BYTE_MASKED -> PrepareMetaMasked(
                name,
                Coerce.toByte(args[0].substring(0, args[0].length - 1)),
                Coerce.toBoolean(args.getOrNull(1) ?: false)
            )
        }
    }
}