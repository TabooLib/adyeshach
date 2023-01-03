package ink.ptms.adyeshach.common.entity

import com.google.common.base.Enums
import ink.ptms.adyeshach.common.entity.path.PathType
import ink.ptms.adyeshach.common.entity.type.*
import ink.ptms.adyeshach.core.entity.type.minecraftVersion
import ink.ptms.adyeshach.core.util.getEnum
import org.bukkit.entity.EntityType

/**
 * @author sky
 * @since 2020-08-04 12:53
 */
@Deprecated("Outdated but usable")
enum class EntityTypes(val entityBase: Class<out EntityInstance>, val toV1: (ink.ptms.adyeshach.core.entity.EntityInstance) -> EntityInstance) {

    ALLAY(AdyAllay::class.java, { AdyAllay(it) }),

    AREA_EFFECT_CLOUD(AdyAreaEffectCloud::class.java, { AdyAreaEffectCloud(it) }),

    ARMOR_STAND(AdyArmorStand::class.java, { AdyArmorStand(it) }),

    ARROW(AdyArrow::class.java, { AdyArrow(it) }),

    BAT(AdyBat::class.java, { AdyBat(it) }),

    BEE(AdyBee::class.java, { AdyBee(it) }),

    BLAZE(AdyBlaze::class.java, { AdyBlaze(it) }),

    BOAT(AdyBoat::class.java, { AdyBoat(it) }),

    CAT(AdyCat::class.java, { AdyCat(it) }),

    CAVE_SPIDER(AdyCaveSpider::class.java, { AdyCaveSpider(it) }),

    CHICKEN(AdyChicken::class.java, { AdyChicken(it) }),

    COD(AdyCod::class.java, { AdyCod(it) }),

    COW(AdyCow::class.java, { AdyCow(it) }),

    CREEPER(AdyCreeper::class.java, { AdyCreeper(it) }),

    DOLPHIN(AdyDolphin::class.java, { AdyDolphin(it) }),

    DONKEY(AdyDonkey::class.java, { AdyDonkey(it) }),

    DRAGON_FIREBALL(AdyDragonFireball::class.java, { AdyDragonFireball(it) }),

    DROWNED(AdyDrowned::class.java, { AdyDrowned(it) }),

    ELDER_GUARDIAN(AdyElderGuardian::class.java, { AdyElderGuardian(it) }),

    END_CRYSTAL(AdyEndCrystal::class.java, { AdyEndCrystal(it) }),

    ENDER_DRAGON(AdyEndDragon::class.java, { AdyEndDragon(it) }),

    ENDERMAN(AdyEnderman::class.java, { AdyEnderman(it) }),

    ENDERMITE(AdyEndermite::class.java, { AdyEndermite(it) }),

    EVOKER(AdyEvoker::class.java, { AdyEvoker(it) }),

    EVOKER_FANGS(AdyEvokerFangs::class.java, { AdyEvokerFangs(it) }),

    EXPERIENCE_ORB(AdyExperienceOrb::class.java, { AdyExperienceOrb(it) }),

    EYE_OF_ENDER(AdyEyeOfEnder::class.java, { AdyEyeOfEnder(it) }),

    FALLING_BLOCK(AdyFallingBlock::class.java, { AdyFallingBlock(it) }),

    FIREWORK_ROCKET(AdyFireworkRocket::class.java, { AdyFireworkRocket(it) }),

    FOX(AdyFox::class.java, { AdyFox(it) }),

    GHAST(AdyGhast::class.java, { AdyGhast(it) }),

    GIANT(AdyGiant::class.java, { AdyGiant(it) }),

    GLOW_ITEM_FRAME(AdyGlowItemFrame::class.java, { AdyGlowItemFrame(it) }),

    GLOW_SQUID(AdyGlowSquid::class.java, { AdyGlowSquid(it) }),

    GOAT(AdyGoat::class.java, { AdyGoat(it) }),

    GUARDIAN(AdyGuardian::class.java, { AdyGuardian(v2 = it) }),

    HOGLIN(AdyHoglin::class.java, { AdyHoglin(it) }),

    HORSE(AdyHorse::class.java, { AdyHorse(it) }),

    HUSK(AdyHusk::class.java, { AdyHusk(it) }),

    ILLUSIONER(AdyIllusioner::class.java, { AdyIllusioner(it) }),

    IRON_GOLEM(AdyIronGolem::class.java, { AdyIronGolem(it) }),

    ITEM(AdyItem::class.java, { AdyItem(it) }),

    ITEM_FRAME(AdyItemFrame::class.java, { AdyItemFrame(it) }),

    FIREBALL(AdyFireball::class.java, { AdyFireball(it) }),

    LEASH_KNOT(AdyLeashKnot::class.java, { AdyLeashKnot(it) }),

    LLAMA(AdyLlama::class.java, { AdyLlama(it) }),

    LLAMA_SPIT(AdyLlamaSpit::class.java, { AdyLlamaSpit(it) }),

    MAGMA_CUBE(AdyMagmaCube::class.java, { AdyMagmaCube(it) }),

    MINECART(AdyMinecart::class.java, { AdyMinecart(it) }),

    MINECART_CHEST(AdyMinecartChest::class.java, { AdyMinecartChest(it) }),

    MINECART_COMMAND(AdyMinecartCommandBlock::class.java, { AdyMinecartCommandBlock(it) }),

    MINECART_FURNACE(AdyMinecartFurnace::class.java, { AdyMinecartFurnace(it) }),

    MINECART_HOPPER(AdyMinecartHopper::class.java, { AdyMinecartHopper(it) }),

    MINECART_MOB_SPAWNER(AdyMinecartSpawner::class.java, { AdyMinecartSpawner(it) }),

    MINECART_TNT(AdyMinecartTNT::class.java, { AdyMinecartTNT(it) }),

    MULE(AdyMule::class.java, { AdyMule(it) }),

    MUSHROOM(AdyMushroom::class.java, { AdyMushroom(it) }),

    OCELOT(AdyOcelot::class.java, { AdyOcelot(it) }),

    PAINTING(AdyPainting::class.java, { AdyPainting(it) }),

    PANDA(AdyPanda::class.java, { AdyPanda(it) }),

    PARROT(AdyParrot::class.java, { AdyParrot(it) }),

    PHANTOM(AdyPhantom::class.java, { AdyPhantom(it) }),

    PIG(AdyPig::class.java, { AdyPig(it) }),

    PIGLIN(AdyPiglin::class.java, { AdyPiglin(it) }),

    PIGLIN_BRUTE(AdyPiglinBrute::class.java, { AdyPiglinBrute(it) }),

    PILLAGER(AdyPillager::class.java, { AdyPillager(it) }),

    POLAR_BEAR(AdyPolarBear::class.java, { AdyPolarBear(it) }),

    PRIMED_TNT(AdyPrimedTNT::class.java, { AdyPrimedTNT(it) }),

    PUFFERFISH(AdyPufferfish::class.java, { AdyPufferfish(it) }),

    RABBIT(AdyRabbit::class.java, { AdyRabbit(it) }),

    RAVAGER(AdyRavager::class.java, { AdyRavager(it) }),

    SALMON(AdySalmon::class.java, { AdySalmon(it) }),

    SHEEP(AdySheep::class.java, { AdySheep(it) }),

    SHULKER(AdyShulker::class.java, { AdyShulker(it) }),

    SHULKER_BULLET(AdyShulkerBullet::class.java, { AdyShulkerBullet(it) }),

    SILVERFISH(AdySilverfish::class.java, { AdySilverfish(it) }),

    SKELETON(AdySkeleton::class.java, { AdySkeleton(it) }),

    SKELETON_HORSE(AdySkeletonHorse::class.java, { AdySkeletonHorse(it) }),

    SLIME(AdySlime::class.java, { AdySlime(it) }),

    SMALL_FIREBALL(AdySmallFireball::class.java, { AdySmallFireball(it) }),

    SNOW_GOLEM(AdySnowGolem::class.java, { AdySnowGolem(it) }),

    SNOWBALL(AdySnowball::class.java, { AdySnowball(it) }),

    SPECTRAL_ARROW(AdySpectralArrow::class.java, { AdySpectralArrow(it) }),

    SPIDER(AdySpider::class.java, { AdySpider(it) }),

    SQUID(AdySquid::class.java, { AdySquid(it) }),

    STRAY(AdyStray::class.java, { AdyStray(it) }),

    STRIDER(AdyStrider::class.java, { AdyStrider(it) }),

    THROWN_EGG(AdyThrownEgg::class.java, { AdyThrownEgg(it) }),

    THROWN_ENDER_PEARL(AdyThrownEnderPearl::class.java, { AdyThrownEnderPearl(it) }),

    THROWN_EXPERIENCE_BOTTLE(AdyThrownExperienceBottle::class.java, { AdyThrownExperienceBottle(it) }),

    THROWN_POTION(AdyThrownPotion::class.java, { AdyThrownPotion(it) }),

    THROWN_TRIDENT(AdyThrownTrident::class.java, { AdyThrownTrident(it) }),

    TRADER_LLAMA(AdyTraderLlama::class.java, { AdyTraderLlama(it) }),

    TROPICAL_FISH(AdyTropicalFish::class.java, { AdyTropicalFish(it) }),

    TURTLE(AdyTurtle::class.java, { AdyTurtle(it) }),

    VEX(AdyVex::class.java, { AdyVex(it) }),

    VILLAGER(AdyVillager::class.java, { AdyVillager(it) }),

    VINDICATOR(AdyVindicator::class.java, { AdyVindicator(it) }),

    WANDERING_TRADER(AdyWanderingTrader::class.java, { AdyWanderingTrader(it) }),

    WITCH(AdyWitch::class.java, { AdyWitch(it) }),

    WITHER(AdyWither::class.java, { AdyWither(it) }),

    WITHER_SKELETON(AdyWitherSkeleton::class.java, { AdyWitherSkeleton(it) }),

    WITHER_SKULL(AdyWitherSkull::class.java, { AdyWitherSkull(it) }),

    WOLF(AdyWolf::class.java, { AdyWolf(it) }),

    ZOGLIN(AdyZoglin::class.java, { AdyZoglin(it) }),

    ZOMBIE(AdyZombie::class.java, { AdyZombie(it) }),

    ZOMBIE_HORSE(AdyZombieHorse::class.java, { AdyZombieHorse(it) }),

    ZOMBIE_VILLAGER(AdyZombieVillager::class.java, { AdyZombieVillager(it) }),

    ZOMBIE_PIGMAN(AdyZombiePigman::class.java, { AdyZombiePigman(it) }),

    ZOMBIFIED_PIGLIN(AdyZombifiedPiglin::class.java, { AdyZombifiedPiglin(it) }),

    PLAYER(AdyHuman::class.java, { AdyHuman(it) }),

    FISHING_HOOK(AdyFishingHook::class.java, { AdyFishingHook(it) });

    val bukkitType: EntityType?
        get() = Enums.getIfPresent(EntityType::class.java, name).orNull()

    val bukkitId: Int
        get() = -1

    val entitySize: EntitySize
        get() = EntitySize(0.0, 0.0)

    val internalName: String
        get() = error("Outdated api is being called, please contact the plugin author to update.")

    val flying: Boolean
        get() = false

    fun v2(): ink.ptms.adyeshach.core.entity.EntityTypes {
        return ink.ptms.adyeshach.core.entity.EntityTypes::class.java.getEnum(name)
    }

    fun newInstance(): EntityInstance {
        error("Outdated api is being called, please contact the plugin author to update.")
    }

    fun getEntityTypeNMS(): Any {
        error("Outdated api is being called, please contact the plugin author to update.")
    }

    fun getPathType(): PathType {
        val h = entitySize.height
        return when {
            flying && minecraftVersion >= 11500 -> PathType.FLY
            h <= 1 -> PathType.WALK_1
            h <= 2 -> PathType.WALK_2
            else -> PathType.WALK_3
        }
    }
    
    companion object {

        fun fromBukkit(bukkitType: EntityType): EntityTypes? {
            return values().firstOrNull { it.bukkitType == bukkitType }
        }

        fun fromClass(clazz: Class<*>): EntityTypes? {
            return values().firstOrNull { it.entityBase == clazz }
        }

        fun adapt(v2: ink.ptms.adyeshach.core.entity.EntityTypes): EntityTypes? {
            return values().firstOrNull { it.name == v2.name }
        }

        fun adapt(v2: ink.ptms.adyeshach.core.entity.EntityInstance): EntityInstance? {
            return adapt(v2.entityType)?.toV1?.invoke(v2)
        }
    }
}