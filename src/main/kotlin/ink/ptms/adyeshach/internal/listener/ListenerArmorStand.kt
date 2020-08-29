package ink.ptms.adyeshach.internal.listener

import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.bukkit.BukkitRotation
import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import ink.ptms.adyeshach.internal.command.Helper
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.KV
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import io.izzel.taboolib.util.lite.Materials
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

/**
 * @Author sky
 * @Since 2020-03-17 21:08
 */
@TListener
class ListenerArmorStand : Listener, Helper {

    @TSchedule(period = 20)
    fun e() {
        Bukkit.getOnlinePlayers().forEach {
            if (it.isOp && Editor.editArmorStand.containsKey(it.name) && Items.hasLore(it.inventory.itemInMainHand, "Adyeshach Tool")) {
                TLocale.Display.sendActionBar(it, "§7Press §fSHIFT + LEFT-CLICK §7to increment §8| §7Press §fSHIFT + RIGHT-CLICK §7to decrement")
            }
        }
    }

    @EventHandler
    fun e(e: PlayerInteractEvent) {
        // edit mode
        if (e.player.isOp && e.hand == EquipmentSlot.HAND && Editor.editArmorStand.containsKey(e.player.name) && Items.hasLore(e.player.inventory.itemInMainHand, "Adyeshach Tool")) {
            e.isCancelled = true
            if (e.player.isSneaking) {
                val bind = Editor.editArmorStand[e.player.name]!!.key ?: return
                val angle = Editor.editArmorStand[e.player.name]!!.value ?: return
                if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {
                    angle.add.invoke(bind)
                } else if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
                    angle.subtract.invoke(bind)
                }
                e.player.playSound(e.player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
            } else {
                MenuBuilder.builder()
                        .title("ArmorStand Angles")
                        .rows(3)
                        .buildAsync { inv ->
                            Angle.values().forEach { inv.addItem(it.toItem()) }
                        }.event {
                            it.isCancelled = true
                            try {
                                if (it.rawSlot >= 0 && it.rawSlot < Angle.values().size) {
                                    val bind = Editor.editArmorStand[e.player.name]!!.key ?: return@event
                                    val angle = Angle.values()[it.rawSlot]
                                    Editor.editArmorStand[e.player.name] = KV(bind, angle)
                                    e.player.closeInventory()
                                    e.player.inventory.setItemInMainHand(ItemBuilder(e.player.inventory.itemInMainHand).name("&7Angle: &f${angle.name}").colored().build())
                                    TLocale.Display.sendTitle(e.player, "§3§lArmorStand Angel", "§7Select Angle: §8${angle.name}", 0, 40, 10)
                                    TLocale.Display.sendActionBar(e.player, "§7Press §fSHIFT + LEFT-CLICK §7to increment §8| §7Press §fSHIFT + RIGHT-CLICK §7to decrement")
                                }
                            } catch (t: Throwable) {
                                t.printStackTrace()
                            }
                        }.open(e.player)
            }
        }
    }

    enum class Angle(val add: (AdyArmorStand) -> (Unit) = { }, val subtract: (AdyArmorStand) -> (Unit) = { }, val item: Materials = Materials.PAPER) {

        // 旋转
        ROTATION({
            it.setHeadRotation(it.position.yaw + 2f, it.position.pitch)
        }, {
            it.setHeadRotation(it.position.yaw - 2f, it.position.pitch)
        }, Materials.GOLDEN_HELMET),

        // 坐标
        BASE_X({ it.teleport(it.position.add(1.0, 0.0, 0.0)) }, { it.teleport(it.position.subtract(1.0, 0.0, 0.0)) }, Materials.ARMOR_STAND),
        BASE_Y({ it.teleport(it.position.add(0.0, 1.0, 0.0)) }, { it.teleport(it.position.subtract(0.0, 1.0, 0.0)) }, Materials.ARMOR_STAND),
        BASE_Z({ it.teleport(it.position.add(0.0, 0.0, 1.0)) }, { it.teleport(it.position.subtract(0.0, 0.0, 1.0)) }, Materials.ARMOR_STAND),

        // 头部
        HEAD_X({ it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).add(1.0, 0.0, 0.0)) }, { it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).subtract(1.0, 0.0, 0.0)) }, Materials.IRON_HELMET),
        HEAD_Y({ it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).add(0.0, 1.0, 0.0)) }, { it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).subtract(0.0, 1.0, 0.0)) }, Materials.IRON_HELMET),
        HEAD_Z({ it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).add(0.0, 0.0, 1.0)) }, { it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).subtract(0.0, 0.0, 1.0)) }, Materials.IRON_HELMET),

        // 身体
        BODY_X({ it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).add(1.0, 0.0, 0.0)) }, { it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).subtract(1.0, 0.0, 0.0)) }, Materials.IRON_CHESTPLATE),
        BODY_Y({ it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).add(0.0, 1.0, 0.0)) }, { it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).subtract(0.0, 1.0, 0.0)) }, Materials.IRON_CHESTPLATE),
        BODY_Z({ it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).add(0.0, 0.0, 1.0)) }, { it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).subtract(0.0, 0.0, 1.0)) }, Materials.IRON_CHESTPLATE),

        // 左手
        LEFT_ARM_X({ it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).add(1.0, 0.0, 0.0)) }, { it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).subtract(1.0, 0.0, 0.0)) }, Materials.STICK),
        LEFT_ARM_Y({ it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).add(0.0, 1.0, 0.0)) }, { it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).subtract(0.0, 1.0, 0.0)) }, Materials.STICK),
        LEFT_ARM_Z({ it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).add(0.0, 0.0, 1.0)) }, { it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).subtract(0.0, 0.0, 1.0)) }, Materials.STICK),

        // 右手
        RIGHT_ARM_X({ it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).add(1.0, 0.0, 0.0)) }, { it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).subtract(1.0, 0.0, 0.0)) }, Materials.BLAZE_ROD),
        RIGHT_ARM_Y({ it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).add(0.0, 1.0, 0.0)) }, { it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).subtract(0.0, 1.0, 0.0)) }, Materials.BLAZE_ROD),
        RIGHT_ARM_Z({ it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).add(0.0, 0.0, 1.0)) }, { it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).subtract(0.0, 0.0, 1.0)) }, Materials.BLAZE_ROD),

        // 左脚
        LEFT_LEG_X({ it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).add(1.0, 0.0, 0.0)) }, { it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).subtract(1.0, 0.0, 0.0)) }, Materials.IRON_LEGGINGS),
        LEFT_LEG_Y({ it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).add(0.0, 1.0, 0.0)) }, { it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).subtract(0.0, 1.0, 0.0)) }, Materials.IRON_LEGGINGS),
        LEFT_LEG_Z({ it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).add(0.0, 0.0, 1.0)) }, { it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).subtract(0.0, 0.0, 1.0)) }, Materials.IRON_LEGGINGS),

        // 右脚
        RIGHT_LEG_X({ it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).add(1.0, 0.0, 0.0)) }, { it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).subtract(1.0, 0.0, 0.0)) }, Materials.GOLDEN_LEGGINGS),
        RIGHT_LEG_Y({ it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).add(0.0, 1.0, 0.0)) }, { it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).subtract(0.0, 1.0, 0.0)) }, Materials.GOLDEN_LEGGINGS),
        RIGHT_LEG_Z({ it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).add(0.0, 0.0, 1.0)) }, { it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).subtract(0.0, 0.0, 1.0)) }, Materials.GOLDEN_LEGGINGS);

        fun toItem(): ItemStack {
            return ItemBuilder(item.parseItem()).name("§7$name").build()
        }
    }
}
