package ink.ptms.adyeshach.internal.listener

import ink.ptms.adyeshach.common.bukkit.BukkitRotation
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.SubscribeEvent
import taboolib.common.platform.submit
import taboolib.library.xseries.XMaterial
import taboolib.module.chat.colored
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.*

/**
 * @Author sky
 * @Since 2020-03-17 21:08
 */
object ListenerArmorStand {

    @Awake(LifeCycle.ACTIVE)
    fun init() {
        submit(period = 20) {
            Bukkit.getOnlinePlayers().forEach {
                if (it.isOp && Editor.editArmorStand.containsKey(it.name) && it.inventory.itemInMainHand.hasLore("Adyeshach Tool")) {
                    it.sendLang("armor-stand-editor-2")
                }
            }
        }
    }

    @SubscribeEvent
    fun e(e: PlayerInteractEvent) {
        // edit mode
        if (e.player.isOp && e.hand == EquipmentSlot.HAND && Editor.editArmorStand.containsKey(e.player.name) && e.player.inventory.itemInMainHand.hasLore("Adyeshach Tool")) {
            e.isCancelled = true
            if (e.player.isSneaking) {
                val bind = Editor.editArmorStand[e.player.name]!!.first
                val angle = Editor.editArmorStand[e.player.name]!!.second ?: return
                if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {
                    angle.add.invoke(bind)
                } else if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
                    angle.subtract.invoke(bind)
                }
                e.player.playSound(e.player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
            } else {
                e.player.openMenu<Basic>(e.player.asLangText("armor-stand-editor-name").toString()) {
                    rows(3)
                    onBuild(async = true) { _, inv ->
                        Angle.values().forEach { inv.addItem(it.toItem()) }
                    }
                    onClick(lock = true) {
                        if (it.rawSlot >= 0 && it.rawSlot < Angle.values().size) {
                            val bind = Editor.editArmorStand[e.player.name]!!.first
                            val angle = Angle.values()[it.rawSlot]
                            Editor.editArmorStand[e.player.name] = bind to angle
                            e.player.closeInventory()
                            e.player.inventory.setItemInMainHand(e.player.inventory.itemInMainHand.modifyMeta {
                                setDisplayName("&7Angle: &f${angle.name}".colored())
                            })
                            e.player.sendLang("armor-stand-editor-1", angle.name)
                            e.player.sendLang("armor-stand-editor-2")
                        }
                    }
                }
            }
        }
    }

    enum class Angle(val add: (AdyArmorStand) -> (Unit) = { }, val subtract: (AdyArmorStand) -> (Unit) = { }, val item: XMaterial = XMaterial.PAPER) {

        // 旋转
        ROTATION({
            it.setHeadRotation(it.position.yaw + 2f, it.position.pitch)
        }, {
            it.setHeadRotation(it.position.yaw - 2f, it.position.pitch)
        }, XMaterial.GOLDEN_HELMET),

        // 坐标
        BASE_X({ it.teleport(it.position.add(1.0, 0.0, 0.0)) }, { it.teleport(it.position.subtract(1.0, 0.0, 0.0)) }, XMaterial.ARMOR_STAND),
        BASE_Y({ it.teleport(it.position.add(0.0, 1.0, 0.0)) }, { it.teleport(it.position.subtract(0.0, 1.0, 0.0)) }, XMaterial.ARMOR_STAND),
        BASE_Z({ it.teleport(it.position.add(0.0, 0.0, 1.0)) }, { it.teleport(it.position.subtract(0.0, 0.0, 1.0)) }, XMaterial.ARMOR_STAND),

        // 头部
        HEAD_X({ it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).add(1.0, 0.0, 0.0)) },
            { it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).subtract(1.0, 0.0, 0.0)) },
            XMaterial.IRON_HELMET),
        HEAD_Y({ it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).add(0.0, 1.0, 0.0)) },
            { it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).subtract(0.0, 1.0, 0.0)) },
            XMaterial.IRON_HELMET),
        HEAD_Z({ it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).add(0.0, 0.0, 1.0)) },
            { it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).subtract(0.0, 0.0, 1.0)) },
            XMaterial.IRON_HELMET),

        // 身体
        BODY_X({ it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).add(1.0, 0.0, 0.0)) },
            { it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).subtract(1.0, 0.0, 0.0)) },
            XMaterial.IRON_CHESTPLATE),
        BODY_Y({ it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).add(0.0, 1.0, 0.0)) },
            { it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).subtract(0.0, 1.0, 0.0)) },
            XMaterial.IRON_CHESTPLATE),
        BODY_Z({ it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).add(0.0, 0.0, 1.0)) },
            { it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).subtract(0.0, 0.0, 1.0)) },
            XMaterial.IRON_CHESTPLATE),

        // 左手
        LEFT_ARM_X({ it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).add(1.0, 0.0, 0.0)) },
            { it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).subtract(1.0, 0.0, 0.0)) },
            XMaterial.STICK),
        LEFT_ARM_Y({ it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).add(0.0, 1.0, 0.0)) },
            { it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).subtract(0.0, 1.0, 0.0)) },
            XMaterial.STICK),
        LEFT_ARM_Z({ it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).add(0.0, 0.0, 1.0)) },
            { it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).subtract(0.0, 0.0, 1.0)) },
            XMaterial.STICK),

        // 右手
        RIGHT_ARM_X({ it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).add(1.0, 0.0, 0.0)) },
            { it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).subtract(1.0, 0.0, 0.0)) },
            XMaterial.BLAZE_ROD),
        RIGHT_ARM_Y({ it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).add(0.0, 1.0, 0.0)) },
            { it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).subtract(0.0, 1.0, 0.0)) },
            XMaterial.BLAZE_ROD),
        RIGHT_ARM_Z({ it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).add(0.0, 0.0, 1.0)) },
            { it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).subtract(0.0, 0.0, 1.0)) },
            XMaterial.BLAZE_ROD),

        // 左脚
        LEFT_LEG_X({ it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).add(1.0, 0.0, 0.0)) },
            { it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).subtract(1.0, 0.0, 0.0)) },
            XMaterial.IRON_LEGGINGS),
        LEFT_LEG_Y({ it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).add(0.0, 1.0, 0.0)) },
            { it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).subtract(0.0, 1.0, 0.0)) },
            XMaterial.IRON_LEGGINGS),
        LEFT_LEG_Z({ it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).add(0.0, 0.0, 1.0)) },
            { it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).subtract(0.0, 0.0, 1.0)) },
            XMaterial.IRON_LEGGINGS),

        // 右脚
        RIGHT_LEG_X({ it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).add(1.0, 0.0, 0.0)) },
            { it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).subtract(1.0, 0.0, 0.0)) },
            XMaterial.GOLDEN_LEGGINGS),
        RIGHT_LEG_Y({ it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).add(0.0, 1.0, 0.0)) },
            { it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).subtract(0.0, 1.0, 0.0)) },
            XMaterial.GOLDEN_LEGGINGS),
        RIGHT_LEG_Z({ it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).add(0.0, 0.0, 1.0)) },
            { it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).subtract(0.0, 0.0, 1.0)) },
            XMaterial.GOLDEN_LEGGINGS);

        fun toItem(): ItemStack {
            return buildItem(item) {
                name = "§7${this@Angle.name}"
            }
        }
    }
}
