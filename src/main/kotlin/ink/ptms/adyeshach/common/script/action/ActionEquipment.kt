package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.entity.EntityEquipable
import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import ink.ptms.zaphkiel.ZaphkielAPI
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.type.BukkitEquipment
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionEquipment(val equipment: BukkitEquipment, val item: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val s = frame.script()
        if (s.getManager() == null) {
            error("No manager selected.")
        }
        if (!s.entitySelected()) {
            error("No entity selected.")
        }
        frame.newFrame(item).run<Any>().thenAccept { item ->
            val itemStack = ZaphkielAPI.getItemStack(item.toString()) ?: ItemStack(Material.AIR)
            s.getEntities()!!.filterIsInstance<EntityEquipable>().forEach {
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