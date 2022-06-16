package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.common.bukkit.nms.AdyeshachNMS
import ink.ptms.adyeshach.common.entity.path.PathType
import org.bukkit.entity.EntityType
import java.lang.reflect.Constructor

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.entity.EntityTypes
 *
 * @author 坏黑
 * @since 2022/6/15 18:08
 */
@Suppress("SpellCheckingInspection")
enum class EntityTypes {

    ALLAY,

    AREA_EFFECT_CLOUD,

    ARMOR_STAND,

    ARROW,

    BAT,

    BEE,

    BLAZE,

    BOAT,

    CAT,

    CAVE_SPIDER,

    CHICKEN,

    COD,

    COW,

    CREEPER,

    DOLPHIN,

    DONKEY,

    DRAGON_FIREBALL,

    DROWNED,

    ELDER_GUARDIAN,

    END_CRYSTAL,

    ENDER_DRAGON,

    ENDERMAN,

    ENDERMITE,

    EVOKER,

    EVOKER_FANGS,

    EXPERIENCE_ORB,

    EYE_OF_ENDER,

    FALLING_BLOCK,

    FIREWORK_ROCKET,

    FOX,

    GHAST,

    GIANT,

    GLOW_ITEM_FRAME,

    GLOW_SQUID,

    GOAT,

    GUARDIAN,

    HOGLIN,

    HORSE,

    HUSK,

    ILLUSIONER,

    IRON_GOLEM,

    ITEM,

    ITEM_FRAME,

    FIREBALL,

    LEASH_KNOT,

    LLAMA,

    LLAMA_SPIT,

    MAGMA_CUBE,

    MINECART,

    MINECART_CHEST,

    MINECART_COMMAND,

    MINECART_FURNACE,

    MINECART_HOPPER,

    MINECART_MOB_SPAWNER,

    MINECART_TNT,

    MULE,

    MUSHROOM,

    OCELOT,

    PAINTING,

    PANDA,

    PARROT,

    PHANTOM,

    PIG,

    PIGLIN,

    PIGLIN_BRUTE,

    PILLAGER,

    POLAR_BEAR,

    PRIMED_TNT,

    PUFFERFISH,

    RABBIT,

    RAVAGER,

    SALMON,

    SHEEP,

    SHULKER,

    SHULKER_BULLET,

    SILVERFISH,

    SKELETON,

    SKELETON_HORSE,

    SLIME,

    SMALL_FIREBALL,

    SNOW_GOLEM,

    SNOWBALL,

    SPECTRAL_ARROW,

    SPIDER,

    SQUID,

    STRAY,

    STRIDER,

    THROWN_EGG,

    THROWN_ENDER_PEARL,

    THROWN_EXPERIENCE_BOTTLE,

    THROWN_POTION,

    THROWN_TRIDENT,

    TRADER_LLAMA,

    TROPICAL_FISH,

    TURTLE,

    VEX,

    VILLAGER,

    VINDICATOR,

    WANDERING_TRADER,

    WITCH,

    WITHER,

    WITHER_SKELETON,

    WITHER_SKULL,

    WOLF,

    ZOGLIN,

    ZOMBIE,

    ZOMBIE_HORSE,

    ZOMBIE_VILLAGER,

    ZOMBIE_PIGMAN,

    ZOMBIFIED_PIGLIN,

    PLAYER,

    FISHING_HOOK;

    /**
     * 获取对应 Bukkit 类型
     */
    val bukkitType: EntityType
        get() = TODO()

    /**
     * 获取对应 Bukkit 序号
     */
    val bukkitId: Int
        get() = TODO()

    /**
     * 获取实体大小
     */
    val entitySize: EntitySize
        get() = TODO()

    /**
     * 获取对应 Adyeshach 类型
     */
    val entityBase: Class<EntityInstance>
        get() = TODO()

    /**
     * 获取内部名称
     */
    val internalName: String?
        get() = null

    /**
     * 是否为飞行单位
     */
    val flying: Boolean
        get() = TODO()

    /**
     * 获取对应 Adyeshach 类型构造器
     */
    val constructor: Constructor<EntityInstance> = entityBase.getDeclaredConstructor()

    /**
     * 创建对应 Adyeshach 类型实例
     */
    fun newInstance(): EntityInstance = constructor.newInstance()

    /**
     * 获取对应 NMS 类型
     */
    fun getEntityTypeNMS() = AdyeshachNMS.getHelper().adapt(this)

    /**
     * 获取寻路类型
     */
    fun getPathType(): PathType = TODO()
}