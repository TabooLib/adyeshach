package ink.ptms.adyeshach.impl.entity.type

import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyAreaEffectCloud
import ink.ptms.adyeshach.impl.util.ifTrue
import ink.ptms.adyeshach.impl.util.toRGB

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultAreaEffectCloud
 *
 * @author 坏黑
 * @since 2023/1/10 00:25
 */
@Suppress("SpellCheckingInspection")
abstract class DefaultAreaEffectCloud(entityTypes: EntityTypes) : DefaultEntity(entityTypes), AdyAreaEffectCloud {

    @Suppress("DuplicatedCode")
    override fun setCustomMeta(key: String, value: String?): Boolean {
        super.setCustomMeta(key, value).ifTrue { return true }
        return when (key) {
            "color" -> {
                // 对 RGB 写法进行兼容
                if (value != null && value.contains(',')) {
                    setColor(value.toRGB())
                    true
                } else {
                    false
                }
            }
            else -> false
        }
    }
}