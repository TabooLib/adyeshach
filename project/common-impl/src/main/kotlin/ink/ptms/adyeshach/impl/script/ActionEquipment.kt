package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.entity.EntityEquipable
import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.core.util.toItem
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import ink.ptms.adyeshach.impl.throwUndefinedError
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.type.BukkitEquipment
import java.util.concurrent.CompletableFuture

/**
 * @author sky
 */
class ActionEquipment(val equipment: BukkitEquipment, val item: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (script.getManager() == null || !script.isEntitySelected()) {
            script.throwUndefinedError()
        }
        frame.newFrame(item).run<Any>().thenAccept { item ->
            val itemStack = item.toString().toItem()
            script.getEntities().filterIsInstance<EntityEquipable>().forEach {
                when (equipment) {
                    BukkitEquipment.HAND -> it.setItemInMainHand(itemStack)
                    BukkitEquipment.OFF_HAND -> it.setItemInOffHand(itemStack)
                    BukkitEquipment.FEET -> it.setBoots(itemStack)
                    BukkitEquipment.LEGS -> it.setLeggings(itemStack)
                    BukkitEquipment.CHEST -> it.setChestplate(itemStack)
                    BukkitEquipment.HEAD -> it.setHelmet(itemStack)
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["equipment"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            val equipment = BukkitEquipment.fromString(it.nextToken()) ?: BukkitEquipment.HAND
            val item = it.next(ArgTypes.ACTION)
            ActionEquipment(equipment, item)
        }
    }
}