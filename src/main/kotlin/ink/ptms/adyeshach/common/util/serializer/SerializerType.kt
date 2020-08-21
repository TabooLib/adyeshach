package ink.ptms.adyeshach.common.util.serializer

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class SerializerType(val baseClass: KClass<*>)