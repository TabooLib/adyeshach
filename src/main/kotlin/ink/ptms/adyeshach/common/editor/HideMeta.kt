package ink.ptms.adyeshach.common.editor

import ink.ptms.adyeshach.common.entity.EntityMetaable
import ink.ptms.adyeshach.common.entity.type.AdyHuman
import kotlin.reflect.KClass

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.editor.MetaBlocked
 *
 * @author sky
 * @since 2021/1/14 11:50 上午
 */
enum class HideMeta(val clazz: KClass<*>, val key: String) {

    HUMAN_1(AdyHuman::class, "customName"),

    HUMAN_2(AdyHuman::class, "isCustomNameVisible");

    companion object {

        fun isHideMeta(entity: EntityMetaable, meta: EntityMetaable.Meta): Boolean {
            return values().any { it.clazz.isInstance(entity) && it.key == meta.key }
        }
    }
}