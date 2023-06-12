package ink.ptms.adyeshach.impl.description

/**
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
enum class CustomType(val id: String) {

    UUID("UUID"),

    PARTICLE("Particle"),

    EULER_ANGLE("EulerAngle"),

    ITEM_STACK("ItemStack"),

    OPT_BLOCK_POS("OptBlockPos"),

    OPT_BLOCK_ID("OptBlockID"),

    BLOCK_ID("BlockID"),

    OPT_CHAT("OptChat"),

    CHAT("Chat"),

    PAINTING("Painting"),

    VILLAGER_DATA("VillagerData"),

    BUKKIT_POSE("BukkitPose"),

    /**
     * 1.19.4 新增
     */

    CAT_TYPE("Cat.Type"),

    FROG_VARIANT("Frog.Variant"),

    VECTOR3("Vector3"),

    QUATERNION("Quaternion"),

    /**
     * 1.20 新增
     */
    SNIFFER_STATE("SnifferState"),

    ;
}