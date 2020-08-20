package ink.ptms.adyeshach.common.util.serializer

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class SerializerType(val baseClass: KClass<*>)