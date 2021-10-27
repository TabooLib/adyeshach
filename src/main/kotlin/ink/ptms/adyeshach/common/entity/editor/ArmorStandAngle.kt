package ink.ptms.adyeshach.common.entity.editor

import ink.ptms.adyeshach.common.bukkit.BukkitRotation
import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

enum class ArmorStandAngle(val add: (AdyArmorStand) -> (Unit), val subtract: (AdyArmorStand) -> (Unit), val item: XMaterial) {

    // 旋转
    ROTATION(
        { it.setHeadRotation(it.position.yaw + 2f, it.position.pitch) },
        { it.setHeadRotation(it.position.yaw - 2f, it.position.pitch) },
        XMaterial.GOLDEN_HELMET),

    // 坐标
    BASE_X({ it.teleport(it.position.add(1.0, 0.0, 0.0)) }, { it.teleport(it.position.subtract(1.0, 0.0, 0.0)) }, XMaterial.ARMOR_STAND),
    BASE_Y({ it.teleport(it.position.add(0.0, 1.0, 0.0)) }, { it.teleport(it.position.subtract(0.0, 1.0, 0.0)) }, XMaterial.ARMOR_STAND),
    BASE_Z({ it.teleport(it.position.add(0.0, 0.0, 1.0)) }, { it.teleport(it.position.subtract(0.0, 0.0, 1.0)) }, XMaterial.ARMOR_STAND),

    // 头部
    HEAD_X(
        { it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).add(1.0, 0.0, 0.0)) },
        { it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).subtract(1.0, 0.0, 0.0)) },
        XMaterial.IRON_HELMET),
    HEAD_Y(
        { it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).add(0.0, 1.0, 0.0)) },
        { it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).subtract(0.0, 1.0, 0.0)) },
        XMaterial.IRON_HELMET),
    HEAD_Z(
        { it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).add(0.0, 0.0, 1.0)) },
        { it.setRotation(BukkitRotation.HEAD, it.getRotation(BukkitRotation.HEAD).subtract(0.0, 0.0, 1.0)) },
        XMaterial.IRON_HELMET),

    // 身体
    BODY_X(
        { it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).add(1.0, 0.0, 0.0)) },
        { it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).subtract(1.0, 0.0, 0.0)) },
        XMaterial.IRON_CHESTPLATE),
    BODY_Y(
        { it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).add(0.0, 1.0, 0.0)) },
        { it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).subtract(0.0, 1.0, 0.0)) },
        XMaterial.IRON_CHESTPLATE),
    BODY_Z(
        { it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).add(0.0, 0.0, 1.0)) },
        { it.setRotation(BukkitRotation.BODY, it.getRotation(BukkitRotation.BODY).subtract(0.0, 0.0, 1.0)) },
        XMaterial.IRON_CHESTPLATE),

    // 左手
    LEFT_ARM_X(
        { it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).add(1.0, 0.0, 0.0)) },
        { it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).subtract(1.0, 0.0, 0.0)) },
        XMaterial.STICK),
    LEFT_ARM_Y(
        { it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).add(0.0, 1.0, 0.0)) },
        { it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).subtract(0.0, 1.0, 0.0)) },
        XMaterial.STICK),
    LEFT_ARM_Z(
        { it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).add(0.0, 0.0, 1.0)) },
        { it.setRotation(BukkitRotation.LEFT_ARM, it.getRotation(BukkitRotation.LEFT_ARM).subtract(0.0, 0.0, 1.0)) },
        XMaterial.STICK),

    // 右手
    RIGHT_ARM_X(
        { it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).add(1.0, 0.0, 0.0)) },
        { it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).subtract(1.0, 0.0, 0.0)) },
        XMaterial.BLAZE_ROD),
    RIGHT_ARM_Y(
        { it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).add(0.0, 1.0, 0.0)) },
        { it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).subtract(0.0, 1.0, 0.0)) },
        XMaterial.BLAZE_ROD),
    RIGHT_ARM_Z(
        { it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).add(0.0, 0.0, 1.0)) },
        { it.setRotation(BukkitRotation.RIGHT_ARM, it.getRotation(BukkitRotation.RIGHT_ARM).subtract(0.0, 0.0, 1.0)) },
        XMaterial.BLAZE_ROD),

    // 左脚
    LEFT_LEG_X(
        { it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).add(1.0, 0.0, 0.0)) },
        { it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).subtract(1.0, 0.0, 0.0)) },
        XMaterial.IRON_LEGGINGS),
    LEFT_LEG_Y(
        { it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).add(0.0, 1.0, 0.0)) },
        { it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).subtract(0.0, 1.0, 0.0)) },
        XMaterial.IRON_LEGGINGS),
    LEFT_LEG_Z(
        { it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).add(0.0, 0.0, 1.0)) },
        { it.setRotation(BukkitRotation.LEFT_LEG, it.getRotation(BukkitRotation.LEFT_LEG).subtract(0.0, 0.0, 1.0)) },
        XMaterial.IRON_LEGGINGS),

    // 右脚
    RIGHT_LEG_X(
        { it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).add(1.0, 0.0, 0.0)) },
        { it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).subtract(1.0, 0.0, 0.0)) },
        XMaterial.GOLDEN_LEGGINGS),
    RIGHT_LEG_Y(
        { it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).add(0.0, 1.0, 0.0)) },
        { it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).subtract(0.0, 1.0, 0.0)) },
        XMaterial.GOLDEN_LEGGINGS),
    RIGHT_LEG_Z(
        { it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).add(0.0, 0.0, 1.0)) },
        { it.setRotation(BukkitRotation.RIGHT_LEG, it.getRotation(BukkitRotation.RIGHT_LEG).subtract(0.0, 0.0, 1.0)) },
        XMaterial.GOLDEN_LEGGINGS);

    fun toItem(): ItemStack {
        return buildItem(item) { name = "§7${this@ArmorStandAngle}" }
    }
}