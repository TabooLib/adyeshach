package ink.ptms.adyeshach.internal.listener

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.entity.element.EntityRotation
import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import ink.ptms.adyeshach.internal.command.Helper
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.PlayerContainer
import io.izzel.taboolib.module.inject.TFunction
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.ArrayUtil
import io.izzel.taboolib.util.KV
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import io.izzel.taboolib.util.lite.Materials
import io.izzel.taboolib.util.lite.Signs
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import java.util.concurrent.ConcurrentHashMap

/**
 * @Author sky
 * @Since 2020-03-17 21:08
 */
@TListener
class ListenerArmorStand : Listener, Helper {

    @EventHandler
    fun e(e: PlayerInteractEvent) {
        // edit mode
        if (e.player.isOp && e.hand == EquipmentSlot.HAND && Editor.editArmorStand.containsKey(e.player.name) && Items.hasLore(e.player.itemInHand, "Adyeshach Tool")) {
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
                                    toInfo(e.player, "Select Angle: &f${angle.name}")
                                }
                            } catch (t: Throwable) {
                                t.printStackTrace()
                            }
                        }.open(e.player)
            }
        }
    }

    enum class Angle(val add: (AdyArmorStand) -> (Unit) = { }, val subtract: (AdyArmorStand) -> (Unit) = { }, val item: Material = Material.PAPER) {

        // 旋转
        ROTATION({
            it.setHeadRotation(it.position.yaw + 2f, it.position.pitch)
        }, {
            it.setHeadRotation(it.position.yaw - 2f, it.position.pitch)
        }, Materials.GOLDEN_HELMET.parseMaterial()!!),

        // 坐标
        BASE_X({ it.teleport(it.position.add(0.05, 0.0, 0.0)) }, { it.teleport(it.position.subtract(0.05, 0.0, 0.0)) }, Material.ARMOR_STAND),
        BASE_Y({ it.teleport(it.position.add(0.0, 0.05, 0.0)) }, { it.teleport(it.position.subtract(0.0, 0.05, 0.0)) }, Material.ARMOR_STAND),
        BASE_Z({ it.teleport(it.position.add(0.0, 0.0, 0.05)) }, { it.teleport(it.position.subtract(0.0, 0.0, 0.05)) }, Material.ARMOR_STAND),

        // 头部
        HEAD_X({ it.setRotation(EntityRotation.HEAD, it.getRotation(EntityRotation.HEAD).add(0.05, 0.0, 0.0)) }, { it.setRotation(EntityRotation.HEAD, it.getRotation(EntityRotation.HEAD).subtract(0.05, 0.0, 0.0)) }, Material.IRON_HELMET),
        HEAD_Y({ it.setRotation(EntityRotation.HEAD, it.getRotation(EntityRotation.HEAD).add(0.0, 0.05, 0.0)) }, { it.setRotation(EntityRotation.HEAD, it.getRotation(EntityRotation.HEAD).subtract(0.0, 0.05, 0.0)) }, Material.IRON_HELMET),
        HEAD_Z({ it.setRotation(EntityRotation.HEAD, it.getRotation(EntityRotation.HEAD).add(0.0, 0.0, 0.05)) }, { it.setRotation(EntityRotation.HEAD, it.getRotation(EntityRotation.HEAD).subtract(0.0, 0.0, 0.05)) }, Material.IRON_HELMET),

        // 身体
        BODY_X({ it.setRotation(EntityRotation.BODY, it.getRotation(EntityRotation.HEAD).add(0.05, 0.0, 0.0)) }, { it.setRotation(EntityRotation.BODY, it.getRotation(EntityRotation.HEAD).subtract(0.05, 0.0, 0.0)) }, Material.IRON_CHESTPLATE),
        BODY_Y({ it.setRotation(EntityRotation.BODY, it.getRotation(EntityRotation.HEAD).add(0.0, 0.05, 0.0)) }, { it.setRotation(EntityRotation.BODY, it.getRotation(EntityRotation.HEAD).subtract(0.0, 0.05, 0.0)) }, Material.IRON_CHESTPLATE),
        BODY_Z({ it.setRotation(EntityRotation.BODY, it.getRotation(EntityRotation.HEAD).add(0.0, 0.0, 0.05)) }, { it.setRotation(EntityRotation.BODY, it.getRotation(EntityRotation.HEAD).subtract(0.0, 0.0, 0.05)) }, Material.IRON_CHESTPLATE),

        // 左手
        LEFT_ARM_X({ it.setRotation(EntityRotation.LEFT_ARM, it.getRotation(EntityRotation.LEFT_ARM).add(0.05, 0.0, 0.0)) }, { it.setRotation(EntityRotation.LEFT_ARM, it.getRotation(EntityRotation.LEFT_ARM).subtract(0.05, 0.0, 0.0)) }, Material.STICK),
        LEFT_ARM_Y({ it.setRotation(EntityRotation.LEFT_ARM, it.getRotation(EntityRotation.LEFT_ARM).add(0.0, 0.05, 0.0)) }, { it.setRotation(EntityRotation.LEFT_ARM, it.getRotation(EntityRotation.LEFT_ARM).subtract(0.0, 0.05, 0.0)) }, Material.STICK),
        LEFT_ARM_Z({ it.setRotation(EntityRotation.LEFT_ARM, it.getRotation(EntityRotation.LEFT_ARM).add(0.0, 0.0, 0.05)) }, { it.setRotation(EntityRotation.LEFT_ARM, it.getRotation(EntityRotation.LEFT_ARM).subtract(0.0, 0.0, 0.05)) }, Material.STICK),

        // 右手
        RIGHT_ARM_X({ it.setRotation(EntityRotation.RIGHT_ARM, it.getRotation(EntityRotation.RIGHT_ARM).add(0.05, 0.0, 0.0)) }, { it.setRotation(EntityRotation.RIGHT_ARM, it.getRotation(EntityRotation.RIGHT_ARM).subtract(0.05, 0.0, 0.0)) }, Material.BLAZE_ROD),
        RIGHT_ARM_Y({ it.setRotation(EntityRotation.RIGHT_ARM, it.getRotation(EntityRotation.RIGHT_ARM).add(0.0, 0.05, 0.0)) }, { it.setRotation(EntityRotation.RIGHT_ARM, it.getRotation(EntityRotation.RIGHT_ARM).subtract(0.0, 0.05, 0.0)) }, Material.BLAZE_ROD),
        RIGHT_ARM_Z({ it.setRotation(EntityRotation.RIGHT_ARM, it.getRotation(EntityRotation.RIGHT_ARM).add(0.0, 0.0, 0.05)) }, { it.setRotation(EntityRotation.RIGHT_ARM, it.getRotation(EntityRotation.RIGHT_ARM).subtract(0.0, 0.0, 0.05)) }, Material.BLAZE_ROD),

        // 左脚
        LEFT_LEG_X({ it.setRotation(EntityRotation.LEFT_LEG, it.getRotation(EntityRotation.LEFT_LEG).add(0.05, 0.0, 0.0)) }, { it.setRotation(EntityRotation.LEFT_LEG, it.getRotation(EntityRotation.LEFT_LEG).subtract(0.05, 0.0, 0.0)) }, Material.IRON_LEGGINGS),
        LEFT_LEG_Y({ it.setRotation(EntityRotation.LEFT_LEG, it.getRotation(EntityRotation.LEFT_LEG).add(0.0, 0.05, 0.0)) }, { it.setRotation(EntityRotation.LEFT_LEG, it.getRotation(EntityRotation.LEFT_LEG).subtract(0.0, 0.05, 0.0)) }, Material.IRON_LEGGINGS),
        LEFT_LEG_Z({ it.setRotation(EntityRotation.LEFT_LEG, it.getRotation(EntityRotation.LEFT_LEG).add(0.0, 0.0, 0.05)) }, { it.setRotation(EntityRotation.LEFT_LEG, it.getRotation(EntityRotation.LEFT_LEG).subtract(0.0, 0.0, 0.05)) }, Material.IRON_LEGGINGS),

        // 右脚
        RIGHT_LEG_X({ it.setRotation(EntityRotation.RIGHT_LEG, it.getRotation(EntityRotation.RIGHT_LEG).add(0.05, 0.0, 0.0)) }, { it.setRotation(EntityRotation.RIGHT_LEG, it.getRotation(EntityRotation.RIGHT_LEG).subtract(0.05, 0.0, 0.0)) }, Material.GOLDEN_LEGGINGS),
        RIGHT_LEG_Y({ it.setRotation(EntityRotation.RIGHT_LEG, it.getRotation(EntityRotation.RIGHT_LEG).add(0.0, 0.05, 0.0)) }, { it.setRotation(EntityRotation.RIGHT_LEG, it.getRotation(EntityRotation.RIGHT_LEG).subtract(0.0, 0.05, 0.0)) }, Material.GOLDEN_LEGGINGS),
        RIGHT_LEG_Z({ it.setRotation(EntityRotation.RIGHT_LEG, it.getRotation(EntityRotation.RIGHT_LEG).add(0.0, 0.0, 0.05)) }, { it.setRotation(EntityRotation.RIGHT_LEG, it.getRotation(EntityRotation.RIGHT_LEG).subtract(0.0, 0.0, 0.05)) }, Material.GOLDEN_LEGGINGS);

        fun toItem(): ItemStack {
            return ItemBuilder(item).name("§7$name").build()
        }
    }
}
