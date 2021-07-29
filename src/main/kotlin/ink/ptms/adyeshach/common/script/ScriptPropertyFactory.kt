package ink.ptms.adyeshach.common.script

import ink.ptms.adyeshach.api.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent
import openapi.kether.ScriptProperty
import taboolib.common5.Coerce
import taboolib.module.kether.KetherProperty

object ScriptPropertyFactory {

    @KetherProperty(bind = AdyeshachEntityDamageEvent::class, shared = true)
    fun ketherProperty1() = object : ScriptProperty("AdyeshachEntityDamageEvent") {

        override fun read(instance: Any, key: String): OperationResult {
            val event = instance as AdyeshachEntityDamageEvent
            return OperationResult.successful(when (key) {
                "player" -> event.player.name
                "id" -> event.entity.id
                "uniqueId" -> event.entity.uniqueId
                "cancelled" -> event.isCancelled
                else -> return OperationResult.failed()
            })
        }

        override fun write(instance: Any, key: String, value: Any?): OperationResult {
            val event = instance as AdyeshachEntityDamageEvent
            when (key) {
                "cancelled" -> event.isCancelled = Coerce.toBoolean(value)
            }
            return OperationResult.successful()
        }
    }

    @KetherProperty(bind = AdyeshachEntityInteractEvent::class, shared = true)
    fun ketherProperty2() = object : ScriptProperty("AdyeshachEntityInteractEvent") {

        override fun read(instance: Any, key: String): OperationResult {
            val event = instance as AdyeshachEntityInteractEvent
            return OperationResult.successful(when (key) {
                "player" -> event.player.name
                "id" -> event.entity.id
                "uniqueId" -> event.entity.uniqueId
                "action" -> if (event.isMainHand) "HAND" else "OFF_HAND"
                "cancelled" -> event.isCancelled
                else -> return OperationResult.failed()
            })
        }

        override fun write(instance: Any, key: String, value: Any?): OperationResult {
            val event = instance as AdyeshachEntityInteractEvent
            when (key) {
                "cancelled" -> event.isCancelled = Coerce.toBoolean(value)
            }
            return OperationResult.successful()
        }
    }
}