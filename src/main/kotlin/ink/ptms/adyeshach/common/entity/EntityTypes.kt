package ink.ptms.adyeshach.common.entity

import com.google.common.base.Enums
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.editor.minecraftVersion
import ink.ptms.adyeshach.common.entity.path.PathType
import ink.ptms.adyeshach.common.entity.type.*
import org.bukkit.entity.EntityType

/**
 * @author sky
 * @since 2020-08-04 12:53
 */
enum class EntityTypes(
    val bukkitType: EntityType?,
    val bukkitId: Int,
    val entitySize: EntitySize,
    val entityBase: Class<out EntityInstance>,
    val internalName: String? = null,
    val flying: Boolean = false,
) {

    AREA_EFFECT_CLOUD(
        Enums.getIfPresent(EntityType::class.java, "AREA_EFFECT_CLOUD").orNull(),
        3,
        EntitySize(2.0, 0.5),
        AdyAreaEffectCloud::class.java,
    ),

    ARMOR_STAND(
        Enums.getIfPresent(EntityType::class.java, "ARMOR_STAND").orNull(),
        78,
        EntitySize(0.5, 1.975),
        AdyArmorStand::class.java
    ),

    ARROW(
        Enums.getIfPresent(EntityType::class.java, "ARROW").orNull(),
        60,
        EntitySize(0.5, 0.5),
        AdyArrow::class.java,
        flying = true
    ),

    BAT(
        Enums.getIfPresent(EntityType::class.java, "BAT").orNull(),
        65,
        EntitySize(0.5, 0.9),
        AdyBat::class.java,
        flying = true
    ),

    BEE(
        Enums.getIfPresent(EntityType::class.java, "BEE").orNull(),
        -1,
        EntitySize(0.7, 0.6),
        AdyBee::class.java,
        flying = true
    ),

    BLAZE(
        Enums.getIfPresent(EntityType::class.java, "BLAZE").orNull(),
        61,
        EntitySize(0.6, 1.8),
        AdyBlaze::class.java
    ),

    BOAT(
        Enums.getIfPresent(EntityType::class.java, "BOAT").orNull(),
        1,
        EntitySize(1.375, 0.5625),
        AdyBoat::class.java
    ),

    CAT(
        Enums.getIfPresent(EntityType::class.java, "CAT").orNull(),
        98,
        EntitySize(0.6, 0.7),
        AdyCat::class.java
    ),

    CAVE_SPIDER(
        Enums.getIfPresent(EntityType::class.java, "CAVE_SPIDER").orNull(),
        59,
        EntitySize(0.7, 0.5),
        AdyCaveSpider::class.java
    ),

    CHICKEN(
        Enums.getIfPresent(EntityType::class.java, "CHICKEN").orNull(),
        93,
        EntitySize(0.4, 0.7),
        AdyChicken::class.java
    ),

    COD(
        Enums.getIfPresent(EntityType::class.java, "COD").orNull(),
        -1,
        EntitySize(0.5, 0.3),
        AdyCod::class.java
    ),

    COW(
        Enums.getIfPresent(EntityType::class.java, "COW").orNull(),
        92,
        EntitySize(0.9, 1.4),
        AdyCow::class.java
    ),

    CREEPER(
        Enums.getIfPresent(EntityType::class.java, "CREEPER").orNull(),
        50,
        EntitySize(0.6, 1.7),
        AdyCreeper::class.java
    ),

    DOLPHIN(
        Enums.getIfPresent(EntityType::class.java, "DOLPHIN").orNull(),
        -1,
        EntitySize(0.9, 0.6),
        AdyDolphin::class.java
    ),

    DONKEY(
        Enums.getIfPresent(EntityType::class.java, "DONKEY").orNull(),
        31,
        EntitySize(1.5, 1.39648),
        AdyDonkey::class.java
    ),

    DRAGON_FIREBALL(
        Enums.getIfPresent(EntityType::class.java, "DRAGON_FIREBALL").orNull(),
        93,
        EntitySize(1.0, 1.0),
        AdyDragonFireball::class.java
    ),

    DROWNED(
        Enums.getIfPresent(EntityType::class.java, "DROWNED").orNull(),
        -1,
        EntitySize(0.6, 1.95),
        AdyDrowned::class.java
    ),

    ELDER_GUARDIAN(
        Enums.getIfPresent(EntityType::class.java, "ELDER_GUARDIAN").orNull(),
        4,
        EntitySize(1.9975, 1.9975),
        AdyElderGuardian::class.java
    ),

    END_CRYSTAL(
        Enums.getIfPresent(EntityType::class.java, "ENDER_CRYSTAL").orNull(),
        51,
        EntitySize(2.0, 2.0),
        AdyEndCrystal::class.java
    ),

    ENDER_DRAGON(
        Enums.getIfPresent(EntityType::class.java, "ENDER_DRAGON").orNull(),
        63,
        EntitySize(16.0, 8.0),
        AdyEndDragon::class.java
    ),

    ENDERMAN(
        Enums.getIfPresent(EntityType::class.java, "ENDERMAN").orNull(),
        58,
        EntitySize(16.0, 3.0),
        AdyEnderman::class.java
    ),

    ENDERMITE(
        Enums.getIfPresent(EntityType::class.java, "ENDERMITE").orNull(),
        67,
        EntitySize(0.4, 0.3),
        AdyEndermite::class.java
    ),

    EVOKER(
        Enums.getIfPresent(EntityType::class.java, "EVOKER").orNull(),
        -1,
        EntitySize(0.6, 1.95),
        AdyEvoker::class.java
    ),

    EVOKER_FANGS(
        Enums.getIfPresent(EntityType::class.java, "EVOKER_FANGS").orNull(),
        -1,
        EntitySize(0.5, 0.8),
        AdyEvokerFangs::class.java
    ),

    // 需要 [Spawn Experience Orb] 包
    EXPERIENCE_ORB(
        Enums.getIfPresent(EntityType::class.java, "EXPERIENCE_ORB").orNull(),
        -1,
        EntitySize(0.5, 0.5),
        AdyExperienceOrb::class.java
    ),

    EYE_OF_ENDER(
        Enums.getIfPresent(EntityType::class.java, "ENDER_SIGNAL").orNull(),
        72,
        EntitySize(0.25, 0.25),
        AdyEyeOfEnder::class.java,
        flying = true
    ),

    FALLING_BLOCK(
        Enums.getIfPresent(EntityType::class.java, "FALLING_BLOCK").orNull(),
        70,
        EntitySize(0.98, 0.98),
        AdyFallingBlock::class.java
    ),

    FIREWORK_ROCKET(
        Enums.getIfPresent(EntityType::class.java, "FIREWORK").orNull(),
        76,
        EntitySize(0.25, 0.25),
        AdyFireworkRocket::class.java,
        flying = true
    ),

    FOX(
        Enums.getIfPresent(EntityType::class.java, "FOX").orNull(),
        -1,
        EntitySize(0.6, 0.7),
        AdyFox::class.java
    ),

    GHAST(
        Enums.getIfPresent(EntityType::class.java, "GHAST").orNull(),
        56,
        EntitySize(4.0, 4.0),
        AdyGhast::class.java,
        flying = true
    ),

    GIANT(
        Enums.getIfPresent(EntityType::class.java, "GIANT").orNull(),
        53,
        EntitySize(3.6, 12.0),
        AdyGiant::class.java
    ),

    GUARDIAN(
        Enums.getIfPresent(EntityType::class.java, "GUARDIAN").orNull(),
        68,
        EntitySize(0.85, 0.85),
        AdyGuardian::class.java
    ),

    HOGLIN(
        Enums.getIfPresent(EntityType::class.java, "HOGLIN").orNull(),
        -1,
        EntitySize(1.39648, 1.4),
        AdyHoglin::class.java
    ),

    HORSE(
        Enums.getIfPresent(EntityType::class.java, "HORSE").orNull(),
        100,
        EntitySize(1.39648, 1.6),
        AdyHorse::class.java
    ),


    HUSK(
        Enums.getIfPresent(EntityType::class.java, "HUSK").orNull(),
        23,
        EntitySize(0.6, 1.95),
        AdyHusk::class.java
    ),

    ILLUSIONER(
        Enums.getIfPresent(EntityType::class.java, "ILLUSIONER").orNull(),
        -1,
        EntitySize(0.6, 1.95),
        AdyIllusioner::class.java
    ),

    IRON_GOLEM(
        Enums.getIfPresent(EntityType::class.java, "IRON_GOLEM").orNull(),
        99,
        EntitySize(1.4, 2.7),
        AdyIronGolem::class.java
    ),

    ITEM(
        Enums.getIfPresent(EntityType::class.java, "DROPPED_ITEM").orNull(),
        2,
        EntitySize(0.25, 0.25),
        AdyItem::class.java
    ),

    ITEM_FRAME(
        Enums.getIfPresent(EntityType::class.java, "ITEM_FRAME").orNull(),
        71,
        EntitySize(0.75, 0.75),
        AdyItemFrame::class.java
    ),

    FIREBALL(
        Enums.getIfPresent(EntityType::class.java, "FIREBALL").orNull(),
        63,
        EntitySize(1.0, 1.0),
        AdyFireball::class.java,
        flying = true
    ),

    LEASH_KNOT(
        Enums.getIfPresent(EntityType::class.java, "LEASH_HITCH").orNull(),
        77,
        EntitySize(0.375, 0.5),
        AdyLeashKnot::class.java
    ),

    LLAMA(
        Enums.getIfPresent(EntityType::class.java, "LLAMA").orNull(),
        103,
        EntitySize(0.9, 1.87),
        AdyLlama::class.java
    ),

    LLAMA_SPIT(
        Enums.getIfPresent(EntityType::class.java, "LLAMA_SPIT").orNull(),
        68,
        EntitySize(0.25, 0.25),
        AdyLlamaSpit::class.java,
        flying = true
    ),

    MAGMA_CUBE(
        Enums.getIfPresent(EntityType::class.java, "MAGMA_CUBE").orNull(),
        62,
        EntitySize(0.51000005, 0.51000005),
        AdyMagmaCube::class.java
    ),

    // 1.17+
    // The marker entity (marked in red) must not be spawned, as it is server-side only

    MINECART(
        Enums.getIfPresent(EntityType::class.java, "MINECART").orNull(),
        10,
        EntitySize(0.98, 0.7),
        AdyMinecart::class.java
    ),

    MINECART_CHEST(
        Enums.getIfPresent(EntityType::class.java, "MINECART_CHEST").orNull(),
        10,
        EntitySize(0.98, 0.7),
        AdyMinecartChest::class.java,
        "CHEST_MINECART"
    ),

    MINECART_COMMAND(
        Enums.getIfPresent(EntityType::class.java, "MINECART_COMMAND").orNull(),
        10,
        EntitySize(0.98, 0.7),
        AdyMinecartCommandBlock::class.java,
        "COMMAND_BLOCK_MINECART"
    ),

    MINECART_FURNACE(
        Enums.getIfPresent(EntityType::class.java, "MINECART_FURNACE").orNull(),
        10,
        EntitySize(0.98, 0.7),
        AdyMinecartFurnace::class.java,
        "FURNACE_MINECART"
    ),

    MINECART_HOPPER(
        Enums.getIfPresent(EntityType::class.java, "MINECART_HOPPER").orNull(),
        10,
        EntitySize(0.98, 0.7),
        AdyMinecartHopper::class.java,
        "HOPPER_MINECART"
    ),

    MINECART_MOB_SPAWNER(
        Enums.getIfPresent(EntityType::class.java, "MINECART_MOB_SPAWNER").orNull(),
        10,
        EntitySize(0.98, 0.7),
        AdyMinecartSpawner::class.java,
        "SPAWNER_MINECART"
    ),

    MINECART_TNT(
        Enums.getIfPresent(EntityType::class.java, "MINECART_TNT").orNull(),
        10,
        EntitySize(0.98, 0.7),
        AdyMinecartTNT::class.java,
        "TNT_MINECART"
    ),

    MULE(
        Enums.getIfPresent(EntityType::class.java, "MULE").orNull(),
        32,
        EntitySize(1.39648, 1.6),
        AdyMule::class.java
    ),

    MUSHROOM(
        Enums.getIfPresent(EntityType::class.java, "MUSHROOM_COW").orNull(),
        96,
        EntitySize(0.9, 1.4),
        AdyMushroom::class.java,
        "MOOSHROOM"
    ),

    OCELOT(
        Enums.getIfPresent(EntityType::class.java, "OCELOT").orNull(),
        98,
        EntitySize(0.6, 0.7),
        AdyOcelot::class.java
    ),

    // 需要 [Spawn Paintings] 包
    PAINTING(
        Enums.getIfPresent(EntityType::class.java, "PAINTING").orNull(),
        -1,
        EntitySize(0.0625, 0.0625), // varies in types
        AdyPainting::class.java
    ),

    PANDA(
        Enums.getIfPresent(EntityType::class.java, "PANDA").orNull(),
        -1,
        EntitySize(1.3, 1.25),
        AdyPanda::class.java
    ),

    PARROT(
        Enums.getIfPresent(EntityType::class.java, "PARROT").orNull(),
        105,
        EntitySize(0.5, 0.9),
        AdyParrot::class.java,
        flying = true
    ),

    PHANTOM(
        Enums.getIfPresent(EntityType::class.java, "PHANTOM").orNull(),
        -1,
        EntitySize(0.9, 0.5),
        AdyPhantom::class.java,
        flying = true
    ),

    PIG(
        Enums.getIfPresent(EntityType::class.java, "PIG").orNull(),
        90,
        EntitySize(0.9, 0.9),
        AdyPig::class.java
    ),


    PIGLIN(
        Enums.getIfPresent(EntityType::class.java, "PIGLIN").orNull(),
        -1,
        EntitySize(0.6, 1.95),
        AdyPiglin::class.java
    ),

    PIGLIN_BRUTE(
        Enums.getIfPresent(EntityType::class.java, "PIGLIN_BRUTE").orNull(),
        -1,
        EntitySize(0.6, 1.95),
        AdyPiglinBrute::class.java
    ),

    PILLAGER(
        Enums.getIfPresent(EntityType::class.java, "PILLAGER").orNull(),
        -1,
        EntitySize(0.6, 1.95),
        AdyPillager::class.java
    ),

    POLAR_BEAR(
        Enums.getIfPresent(EntityType::class.java, "POLAR_BEAR").orNull(),
        102,
        // ver<1.16 -> 1.3,1.4
        EntitySize(1.4, 1.4),
        AdyPolarBear::class.java
    ),

    PRIMED_TNT(
        Enums.getIfPresent(EntityType::class.java, "PRIMED_TNT").orNull(),
        50,
        EntitySize(0.98, 0.98),
        AdyPrimedTNT::class.java,
        "TNT"
    ),

    PUFFERFISH(
        Enums.getIfPresent(EntityType::class.java, "PUFFERFISH").orNull(),
        -1,
        EntitySize(0.7, 0.7),
        AdyPufferfish::class.java
    ),

    RABBIT(
        Enums.getIfPresent(EntityType::class.java, "RABBIT").orNull(),
        101,
        EntitySize(0.4, 0.5),
        AdyRabbit::class.java
    ),

    RAVAGER(
        Enums.getIfPresent(EntityType::class.java, "RAVAGER").orNull(),
        -1,
        EntitySize(1.95, 2.2),
        AdyRavager::class.java
    ),

    SALMON(
        Enums.getIfPresent(EntityType::class.java, "SALMON").orNull(),
        -1,
        EntitySize(0.7, 0.4),
        AdySalmon::class.java
    ),

    SHEEP(
        Enums.getIfPresent(EntityType::class.java, "SHEEP").orNull(),
        91,
        EntitySize(0.9, 1.3),
        AdySheep::class.java
    ),

    SHULKER(
        Enums.getIfPresent(EntityType::class.java, "SHULKER").orNull(),
        69,
        // SecondValue: 1.0-2.0 (depending on peek)
        EntitySize(1.0, 2.0),
        AdyShulker::class.java
    ),

    SHULKER_BULLET(
        Enums.getIfPresent(EntityType::class.java, "SHULKER_BULLET").orNull(),
        67,
        EntitySize(0.3125, 0.3125),
        AdyShulkerBullet::class.java,
        flying = true
    ),

    SILVERFISH(
        Enums.getIfPresent(EntityType::class.java, "SILVERFISH").orNull(),
        60,
        EntitySize(0.4, 0.3),
        AdySilverfish::class.java
    ),

    SKELETON(
        Enums.getIfPresent(EntityType::class.java, "SKELETON").orNull(),
        51,
        EntitySize(0.6, 1.99),
        AdySkeleton::class.java
    ),

    SKELETON_HORSE(
        Enums.getIfPresent(EntityType::class.java, "SKELETON_HORSE").orNull(),
        28,
        EntitySize(1.396484, 1.6),
        AdySkeletonHorse::class.java
    ),

    SLIME(
        Enums.getIfPresent(EntityType::class.java, "SLIME").orNull(),
        55,
        EntitySize(0.51000005, 0.51000005),
        AdySlime::class.java
    ),

    SMALL_FIREBALL(
        Enums.getIfPresent(EntityType::class.java, "SMALL_FIREBALL").orNull(),
        64,
        EntitySize(0.3125, 0.3125),
        AdySmallFireball::class.java,
        flying = true
    ),

    SNOW_GOLEM(
        Enums.getIfPresent(EntityType::class.java, "SNOWMAN").orNull(),
        97,
        EntitySize(0.7, 1.9),
        AdySnowGolem::class.java
    ),

    SNOWBALL(
        Enums.getIfPresent(EntityType::class.java, "SNOWBALL").orNull(),
        61,
        EntitySize(0.25, 0.25),
        AdySnowball::class.java,
        flying = true
    ),

    SPECTRAL_ARROW(
        Enums.getIfPresent(EntityType::class.java, "SPECTRAL_ARROW").orNull(),
        91,
        EntitySize(0.5, 0.5),
        AdySpectralArrow::class.java,
        flying = true
    ),

    SPIDER(
        Enums.getIfPresent(EntityType::class.java, "SPIDER").orNull(),
        52,
        EntitySize(1.4, 0.9),
        AdySpider::class.java
    ),

    SQUID(
        Enums.getIfPresent(EntityType::class.java, "SQUID").orNull(),
        94,
        EntitySize(0.8, 0.8),
        AdySquid::class.java
    ),

    STRAY(
        Enums.getIfPresent(EntityType::class.java, "STRAY").orNull(),
        6,
        EntitySize(0.6, 1.99),
        AdyStray::class.java
    ),

    STRIDER(
        Enums.getIfPresent(EntityType::class.java, "STRIDER").orNull(),
        6,
        EntitySize(0.9, 1.7),
        AdyStrider::class.java
    ),

    THROWN_EGG(
        Enums.getIfPresent(EntityType::class.java, "EGG").orNull(),
        62,
        EntitySize(0.25, 0.25),
        AdyThrownEgg::class.java,
        "EGG",
        flying = true
    ),

    THROWN_ENDER_PEARL(
        Enums.getIfPresent(EntityType::class.java, "ENDER_PEARL").orNull(),
        65,
        EntitySize(0.25, 0.25),
        AdyThrownEnderPearl::class.java,
        "ENDER_PEARL",
        flying = true
    ),

    THROWN_EXPERIENCE_BOTTLE(
        Enums.getIfPresent(EntityType::class.java, "THROWN_EXP_BOTTLE").orNull(),
        75,
        EntitySize(0.25, 0.25),
        AdyThrownExperienceBottle::class.java,
        "EXPERIENCE_BOTTLE",
        flying = true
    ),

    THROWN_POTION(
        Enums.getIfPresent(EntityType::class.java, "SPLASH_POTION").orNull(),
        73,
        EntitySize(0.25, 0.25),
        AdyThrownPotion::class.java,
        "POTION",
        flying = true
    ),

    THROWN_TRIDENT(
        Enums.getIfPresent(EntityType::class.java, "TRIDENT").orNull(),
        73,
        EntitySize(0.5, 0.5),
        AdyThrownTrident::class.java,
        "TRIDENT",
        flying = true
    ),

    TRADER_LLAMA(
        Enums.getIfPresent(EntityType::class.java, "TRADER_LLAMA").orNull(),
        -1,
        EntitySize(0.9, 1.87),
        AdyTraderLlama::class.java
    ),

    TROPICAL_FISH(
        Enums.getIfPresent(EntityType::class.java, "TROPICAL_FISH").orNull(),
        -1,
        EntitySize(0.5, 0.4),
        AdyTropicalFish::class.java
    ),

    TURTLE(
        Enums.getIfPresent(EntityType::class.java, "TURTLE").orNull(),
        -1,
        EntitySize(1.2, 0.4),
        AdyTurtle::class.java
    ),

    VEX(
        Enums.getIfPresent(EntityType::class.java, "VEX").orNull(),
        -1,
        EntitySize(0.4, 0.8),
        AdyVex::class.java,
        flying = true
    ),

    VILLAGER(
        Enums.getIfPresent(EntityType::class.java, "VILLAGER").orNull(),
        120,
        EntitySize(0.6, 1.95),
        AdyVillager::class.java
    ),

    VINDICATOR(
        Enums.getIfPresent(EntityType::class.java, "VINDICATOR").orNull(),
        -1,
        EntitySize(0.6, 1.95),
        AdyVindicator::class.java
    ),

    WANDERING_TRADER(
        Enums.getIfPresent(EntityType::class.java, "WANDERING_TRADER").orNull(),
        -1,
        EntitySize(0.6, 1.95),
        AdyWanderingTrader::class.java
    ),

    WITCH(
        Enums.getIfPresent(EntityType::class.java, "WITCH").orNull(),
        -1,
        EntitySize(0.6, 1.95),
        AdyWitch::class.java,
        flying = true
    ),

    WITHER(
        Enums.getIfPresent(EntityType::class.java, "WITHER").orNull(),
        64,
        EntitySize(0.9, 3.5),
        AdyWither::class.java
    ),

    WITHER_SKELETON(
        Enums.getIfPresent(EntityType::class.java, "WITHER_SKELETON").orNull(),
        5,
        EntitySize(0.7, 2.4),
        AdyWitherSkeleton::class.java
    ),

    WITHER_SKULL(
        Enums.getIfPresent(EntityType::class.java, "WITHER_SKULL").orNull(),
        66,
        EntitySize(0.3125, 0.3125),
        AdyWitherSkull::class.java,
        flying = true
    ),

    WOLF(
        Enums.getIfPresent(EntityType::class.java, "WOLF").orNull(),
        95,
        EntitySize(0.6, 0.85),
        AdyWolf::class.java
    ),

    ZOGLIN(
        Enums.getIfPresent(EntityType::class.java, "ZOGLIN").orNull(),
        -1,
        EntitySize(1.39648, 1.4),
        AdyZoglin::class.java
    ),

    ZOMBIE(
        Enums.getIfPresent(EntityType::class.java, "ZOMBIE").orNull(),
        54,
        EntitySize(0.6, 1.95),
        AdyZombie::class.java
    ),

    ZOMBIE_HORSE(
        Enums.getIfPresent(EntityType::class.java, "ZOMBIE_HORSE").orNull(),
        29,
        EntitySize(1.39648, 1.6),
        AdyZombieHorse::class.java
    ),

    ZOMBIE_VILLAGER(
        Enums.getIfPresent(EntityType::class.java, "ZOMBIE_VILLAGER").orNull(),
        27,
        EntitySize(0.6, 1.95),
        AdyZombieVillager::class.java
    ),

    ZOMBIE_PIGMAN(
        Enums.getIfPresent(EntityType::class.java, "PIG_ZOMBIE").orNull(),
        57,
        EntitySize(0.6, 1.95),
        AdyZombiePigman::class.java
    ),

    ZOMBIFIED_PIGLIN(
        Enums.getIfPresent(EntityType::class.java, "ZOMBIFIED_PIGLIN").orNull(),
        -1,
        EntitySize(0.6, 1.95),
        AdyZombifiedPiglin::class.java
    ),

    PLAYER(
        Enums.getIfPresent(EntityType::class.java, "PLAYER").orNull(),
        0,
        EntitySize(0.6, 1.8),
        AdyHuman::class.java
    ),

    FISHING_HOOK(
        Enums.getIfPresent(EntityType::class.java, "FISHING_HOOK").orNull(),
        90,
        EntitySize(0.25, 0.25),
        AdyFishingHook::class.java,
        "FISHING_BOBBER"
    );

    val constructor = entityBase.getDeclaredConstructor()

    fun newInstance(): EntityInstance {
        return constructor.newInstance()
    }

    fun getEntityTypeNMS(): Any {
        return NMS.INSTANCE.getEntityTypeNMS(this)
    }

    fun getPathType(): PathType {
        val h = entitySize.height
        return when {
            flying && minecraftVersion >= 11500 -> {
                PathType.FLY
            }
            h <= 1 -> {
                PathType.WALK_1
            }
            h <= 2 -> {
                PathType.WALK_2
            }
            else -> {
                PathType.WALK_3
            }
        }
    }

    companion object {

        fun fromBukkit(bukkitType: EntityType): EntityTypes? {
            return values().firstOrNull { it.bukkitType == bukkitType }
        }
    }
}