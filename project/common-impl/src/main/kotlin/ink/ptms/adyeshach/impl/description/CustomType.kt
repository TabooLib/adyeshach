package ink.ptms.adyeshach.impl.description

/**
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
enum class CustomType(val id: String) {

    PARTICLE("Particle"),

    EULER_ANGLE("EulerAngle"),

    VECTOR("Vector"),

    ITEM_STACK("ItemStack"),

    MATERIAL("Material"),

    PAINTING("Painting"),

    SHEEP_COLOR("SheepColor"),

    VILLAGER_DATA("VillagerData"),

    TEXT_COMPONENT("TextComponent"),

    BUKKIT_POSE("BukkitPose"),
}