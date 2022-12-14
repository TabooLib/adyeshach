package ink.ptms.adyeshach.core.serializer

import kotlin.reflect.KClass

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.serializer.SerializerType
 *
 * @author sky
 * @since 2021/5/29 12:27 上午
 */
@Target(AnnotationTarget.CLASS)
annotation class SerializerType(val baseClass: KClass<*>)