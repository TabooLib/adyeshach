package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.bukkit.*
import ink.ptms.adyeshach.common.bukkit.data.VectorNull
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import ink.ptms.adyeshach.common.entity.editor.*
import ink.ptms.adyeshach.common.entity.type.*
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.warning
import taboolib.common5.Coerce
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.getName
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.*
import java.util.function.Consumer
import kotlin.collections.set

@Suppress("UNCHECKED_CAST")
object EntityMetas {

    private val minecraftVersion = MinecraftVersion.majorLegacy

    /**
     * 接口
     */
    @Awake(LifeCycle.ENABLE)
    private fun init0() {
        from<AdyFish> {
            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11300 to 12), "fromBucket", false)
        }
        from<AdyRaider> {
            natural(at(11700 to 16, 11500 to 15, 11400 to 14), "isCelebrating", false)
        }
        from<AdyEntityAgeable> {
            if (MinecraftVersion.major == 0) {
                //Negative = Child
                natural(at(10800 to 12), "isBaby", 0.toByte()) {
                    it.modify { player, entity ->
                        val origin = entity.getMetadata<Byte>("isBaby")
                        entity.setMetadata("isBaby", (if (origin == 0.toByte()) -1 else 0).toByte())
                        entity.openEditor(player)
                    }
                    it.display { player, entity ->
                        if (entity.getMetadata<Byte>("isBaby") < 0) {
                            true
                        } else {
                            false
                        }.toDisplay(player)
                    }
                }
            } else {
                natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11100 to 12, 10900 to 11), "isBaby", false)
            }
        }
        from<AdyMob> {
            mask(at(11700 to 15, 11500 to 14, 11400 to 13, 11000 to 11), "isLeftHanded", 0x02)
            mask(at(11700 to 15, 11600 to 14), "isAgressive", 0x04)
        }
        from<AdyHorseChested> {
            natural(at(11700 to 19, 11500 to 18, 11400 to 17, 11100 to 15), "hasChest", false)
        }
        from<AdyEntityTameable> {
            mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "isSitting", 0x01)
            mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "isAngry", 0x02)
            mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "isTamed", 0x04)
        }
        from<AdyHorseBase> {
            if (minecraftVersion >= 11300) {
                mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11300 to 13), "isTamed", 0x02)
                mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11300 to 13), "isSaddled", 0x04)
                mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11300 to 13), "hasBred", 0x08)
                mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11300 to 13), "isEating", 0x10)
                mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11300 to 13), "isRearing", 0x20)
                mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11300 to 13), "isMouthOpen", 0x40)
            } else {
                mask(at(11100 to 13, 10900 to 12), "isTamed", 0x02)
                mask(at(11100 to 13, 10900 to 12), "isSaddled", 0x04)
                mask(at(11100 to 13, 10900 to 12), "hasChest", 0x08)
                mask(at(11100 to 13, 10900 to 12), "hasBred", 0x10)
                mask(at(11100 to 13, 10900 to 12), "isEating", 0x20)
                mask(at(11100 to 13, 10900 to 12), "isRearing", 0x40)
                mask(at(11100 to 13, 10900 to 12), "isMouthOpen", 0x80.toByte())
            }
        }
    }

    /**
     * 不含有编辑器的基本元数据类型
     */
    @Awake(LifeCycle.ENABLE)
    private fun init1() {
        from<AdyBat> {
            mask(at(11700 to 16, 11600 to 15, 11000 to 12, 10900 to 11), "isHanging", 0x01)
        }
        from<AdyBlaze> {
            mask(at(11700 to 16, 11600 to 15, 11100 to 12, 10900 to 11), "fire", 0x01)
        }
        from<AdyPillager> {
            natural(at(11700 to 17), "isCharging", false)
        }
        from<AdyFireball> {
            natural(at(11600 to 7), "item", ItemStack(Material.AIR))
        }
        from<AdyThrownPotion> {
            natural(at(11700 to 8, 11600 to 7), "item", ItemStack(Material.AIR))
        }
        from<AdyItem> {
            natural(at(11700 to 8, 11300 to 7, 11000 to 6, 10900 to 5, 10800 to 10), "item", buildItem(XMaterial.BEDROCK))
        }
        from<AdySlime> {
            if (MinecraftVersion.major == 0) {
                //only 1.8.x byte type
                natural(16, "size", 1.toByte())
            } else {
                natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "size", 1)
            }
        }
        from<AdyPig> {
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12, 10800 to 16), "hasSaddle", false)
        }
        from<AdyWitch> {
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11300 to 12), "isDrinkingPotion", false)
        }
        from<AdyGhast> {
            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "isAttacking", false)
        }
        from<AdyPolarBear> {
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13), "isStanding", false)
        }
        from<AdyPhantom> {
            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11300 to 12), "size", 0)
        }
        from<AdyThrownEnderPearl> {
            natural(at(11700 to 8, 11600 to 7), "item", buildItem(XMaterial.ENDER_PEARL))
        }
        from<AdyThrownExperienceBottle> {
            natural(at(11700 to 8, 11600 to 7), "item", buildItem(XMaterial.EXPERIENCE_BOTTLE))
        }
        from<AdySnowball> {
            natural(at(11700 to 8, 11600 to 7), "item", buildItem(XMaterial.SNOWBALL))
        }
        from<AdyMinecartFurnace> {
            natural(at(11700 to 14, 11400 to 13, 11000 to 12, 10900 to 11), "hasFuel", false)
        }
        from<AdyVex> {
            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11300 to 13, 11100 to 12), "attackMode", false)
        }
        from<AdyWitherSkull> {
            natural(at(11700 to 8, 11400 to 7, 11000 to 6, 10900 to 5), "invulnerable", false)
        }
        from<AdyThrownEgg> {
            natural(at(11700 to 8, 11600 to 7), "item", buildItem(XMaterial.EGG))
        }
        from<AdyPrimedTNT> {
            natural(at(11700 to 8, 11400 to 7, 11000 to 6, 10900 to 5), "fuseTime", 80)
        }
        from<AdyPufferfish> {
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11300 to 13), "puffState", 0)
        }
        from<AdySmallFireball> {
            natural(at(11700 to 8, 11600 to 7), "item", buildItem(XMaterial.FIRE_CHARGE))
        }
        from<AdySpider> {
            mask(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11, 10800 to 16), "isClimbing", 0x01)
        }
        from<AdyEyeOfEnder> {
            natural(at(11700 to 8, 11400 to 7), "item", buildItem(XMaterial.ENDER_EYE))
        }
        from<AdyFireworkRocket> {
            natural(at(11700 to 8, 11400 to 7, 11100 to 6, 10900 to 9, 10800 to 8), "fireworkInfo", ItemStack(Material.AIR))
        }
        from<AdySnowGolem> {
            mask(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "hasPumpkinHat", 0x10, true)
            mask(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "hasNoPumpkinHat", 0x00)
        }
        from<AdyStrider> {
            natural(at(11700 to 18), "isShaking", false)
            natural(at(11700 to 19), "hasSaddle", false)
        }
        from<AdyThrownTrident> {
            natural(at(11400 to 10, 11300 to 10), "loyaltyLevel", 0)
            natural(at(11500 to 11), "hasEnchantmentGlint", false)
        }
        from<AdyDolphin> {
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11300 to 13), "findTreasure", false)
            natural(at(11700 to 18, 11500 to 17, 11400 to 16, 11300 to 14), "hasFish", false)
        }
        from<AdyEndCrystal> {
            natural(at(11700 to 8, 11400 to 7, 11000 to 6, 10900 to 5), "beamTarget", VectorNull())
            natural(at(11700 to 9, 11400 to 8, 11000 to 7, 10900 to 6), "showBottom", true)
        }
        from<AdyItemFrame> {
            natural(at(11700 to 8, 11300 to 7, 11000 to 6, 10900 to 5, 10800 to 8), "item", ItemStack(Material.AIR))
            natural(at(11700 to 9, 11300 to 8, 11000 to 7, 10900 to 6, 10800 to 9), "rotation", 0)
        }
        from<AdyEnderman> {
            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11, 10800 to 16), "carriedBlock", MaterialData(Material.AIR))
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12, 10800 to 18), "isScreaming", false)
            natural(at(11700 to 18, 11500 to 17), "isStaring", false)
        }
        from<AdyArrow> {
            mask(at(11700 to 8, 11600 to 7, 11000 to 6, 10900 to 5, 10800 to 16), "isCritical", 0x01)
            mask(at(11700 to 8, 11400 to 7), "noclip", 0x02)
            natural(at(11700 to 9, 11600 to 8, 11500 to 9), "piercingLevel", 0.toByte())
            natural(at(11700 to 10, 11600 to 9), "color", -1) { it.useColorEditor() }
        }
        from<AdyAreaEffectCloud> {
            natural(at(11700 to 8, 11600 to 7), "radius", 0.5f)
            natural(at(11700 to 9, 11600 to 8), "color", 0) { it.useColorEditor() }
            natural(at(11700 to 10, 11600 to 9), "ignoreRadius", false)
            natural(at(11700 to 11, 11600 to 10), "particle", BukkitParticles.EFFECT) { it.useEnumsEditor() }
        }
        from<AdyGuardian> {
            mask(at(11200 to -1, 11000 to 12, 10900 to 11, 10800 to 16), "isRetractingSpikes", 0x02)
            mask(at(11200 to -1, 11000 to 12, 10900 to 11, 10800 to 16), "isElderly", 0x04)
            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11100 to 13), "isRetractingSpikes", false)
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12, 10800 to 17), "targetEntity", 0) {
                it.editable = false
            }
        }
        from<AdyTurtle> {
            val index = at(11500 to 18, 11400 to 17, 11300 to 15)
            natural(index + 0, "hasEgg", false)
            natural(index + 1, "layingEgg", false)
            natural(index + 3, "isGoingHome", false)
            natural(index + 4, "isTraveling", false)
        }
        from<AdyPanda> {
            val index = at(11700 to 22, 11500 to 21, 11400 to 20)
            mask(index, "isSneezing", 0x02)
            mask(index, "isEating", 0x04)
            mask(index, "isSitting", 0x08)
            mask(index, "isOnBack", 0x10)
        }
        from<AdyPiglin> {
            val index = at(11700 to 16, 11600 to 15)
            natural(index + 0, "isImmuneToZombification", false)
            natural(index + 1, "isBaby", false)
            natural(index + 2, "isChargingCrossbow", false)
            natural(index + 3, "isDancing", false)
        }
        from<AdyGoat> {
            val index = at(11900 to 17)
            natural(index + 0, "isScreamingGoat", false)
            natural(index + 1, "hasLeftHorn", true)
            natural(index + 2, "hasRightHorn", true)
        }
        from<AdyArmorStand> {
            mask(at(11700 to 15, 11500 to 14, 11400 to 13, 11000 to 11, 10800 to 10), "isSmall", 0x01)
            mask(at(11700 to 15, 11500 to 14, 11400 to 13, 11000 to 11, 10800 to 10), "hasArms", 0x04)
            mask(at(11700 to 15, 11500 to 14, 11400 to 13, 11000 to 11, 10800 to 10), "noBasePlate", 0x08)
            mask(at(11700 to 15, 11500 to 14, 11400 to 13, 11000 to 11, 10800 to 10), "isMarker", 0x10)
            mask(at(11000 to -1, 10800 to 10), "hasGravity", 0x02)
            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10800 to 11), "angleHead", EulerAngle(0.0, 0.0, 0.0)) {
                it.editable = false
            }
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10800 to 12), "angleBody", EulerAngle(0.0, 0.0, 0.0)) {
                it.editable = false
            }
            natural(at(11700 to 18, 11500 to 17, 11400 to 16, 11000 to 14, 10800 to 13), "angleLeftArm", EulerAngle(-10.0, 0.0, -10.0)) {
                it.editable = false
            }
            natural(at(11700 to 19, 11500 to 18, 11400 to 17, 11000 to 15, 10800 to 14), "angleRightArm", EulerAngle(-15.0, 0.0, 10.0)) {
                it.editable = false
            }
            natural(at(11700 to 20, 11500 to 19, 11400 to 18, 11000 to 16, 10800 to 15), "angleLeftLeg", EulerAngle(-1.0, 0.0, -1.0)) {
                it.editable = false
            }
            natural(at(11700 to 21, 11500 to 20, 11400 to 19, 11000 to 17, 10800 to 16), "angleRightLeg", EulerAngle(1.0, 0.0, 1.0)) {
                it.editable = false
            }
            naturalEditor("angle") {
                it.hybrid = true
                it.modify { player, entity ->
                    EditorHandler.armorStandLookup[player.name] = entity to null
                    player.inventory.takeItem(99) { item -> item.hasLore(player.asLangText("editor-armorstand-tool-lore")) }
                    player.giveItem(buildItem(XMaterial.REDSTONE_TORCH) {
                        name = "§7${player.asLangText("editor-armorstand-tool-name", "NONE")}"
                        lore += "§8${player.asLangText("editor-armorstand-tool-lore")}"
                        shiny()
                        colored()
                    })
                    player.sendLang("editor-armorstand-tool")
                    player.closeInventory()
                }
                it.display { player, _ -> player.asLangText("editor-no-preview") }
            }
        }
    }

    /**
     * 含有编辑器的复杂元数据类型
     */
    @Awake(LifeCycle.ENABLE)
    private fun init2() {
        from<AdyRabbit> {
            val index = at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12, 10800 to 18)
            natural(index, "type", if (MinecraftVersion.major == 0) Rabbit.Type.BLACK.ordinal.toByte() else Rabbit.Type.BLACK.ordinal) { it.useIndexEditor(Rabbit.Type::class.java, "type") }
        }
        from<AdyParrot> {
            val index = at(11700 to 19, 11500 to 18, 11400 to 17, 11200 to 15)
            natural(index, "color", 0) { it.useIndexEditor(Parrot.Variant::class.java, "color") }
        }
        from<AdySpellcasterIllager> {
            val index = at(11700 to 17, 11400 to 16, 11200 to 15, 11100 to 13)
            natural(index, "spell", 0) { it.useIndexEditor(Spellcaster.Spell::class.java, "spell") }
        }
        from<AdySheep> {
            val index = at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12, 10800 to 16)
            natural(index, "dyeColor", 0) { it.useIndexEditor(DyeColor::class.java, "dyeColor") }
            mask(index, "isSheared", 0x10)
        }
        from<AdyMushroom> {
            natural(at(11700 to 17, 11500 to 16, 11400 to 15), "type", BukkitMushroom.RED.name.lowercase()) {
                it.useEnumsEditor(type = BukkitMushroom::class.java, key = "type")
            }
        }
        from<AdyEndDragon> {
            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "dragonPhase", BukkitDragonPhase.HOVERING_WITH_NO_AI.ordinal) {
                it.useIndexEditor(BukkitDragonPhase::class.java, "dragonPhase")
            }
        }
        from<AdyWolf> {
            natural(at(11700 to 19, 11400 to 18, 11000 to 16, 10900 to 15, 10800 to 19), "isBegging", false)
            natural(at(11700 to 20, 11400 to 19, 11000 to 17, 10900 to 16, 10800 to 20), "collarColor", DyeColor.RED.ordinal) {
                it.useIndexEditor(type = DyeColor::class.java, key = "collarColor")
            }
        }
        from<AdyCreeper> {
            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11, 10800 to 16), "state", BukkitCreeperState.IDLE.ordinal) {
                it.useIndexEditor(BukkitCreeperState::class.java, "state")
            }
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12, 10800 to 17), "isCharged", false)
            natural(at(11700 to 18, 11500 to 17, 11400 to 16, 11000 to 14, 10900 to 13), "isIgnited", false)
        }
        from<AdyLlama> {
            natural(at(11700 to 21, 11500 to 20, 11400 to 19, 11100 to 17), "carpetColor", 0) {
                it.useIndexEditor(DyeColor::class.java, "carpetColor")
            }
            natural(at(11700 to 22, 11500 to 21, 11400 to 20, 11100 to 18), "color", 0) {
                it.useIndexEditor(Llama.Color::class.java, "color")
            }
        }
        from<AdyExperienceOrb> {
            naturalEditor("amount") {
                it.reset { _, entity -> entity.amount = 1 }
                it.modify { player, entity ->
                    player.edit(entity, entity.amount) { args -> entity.amount = Coerce.toInteger(args.replace("\"", "")) }
                }
                it.display { _, entity -> entity.amount }
            }
        }
        from<AdyCat> {
            natural(at(11700 to 19, 11500 to 18), "type", Cat.Type.TABBY.ordinal) {
                it.useIndexEditor(Cat.Type::class.java, "type")
            }
            natural(at(11700 to 20), "isLying", false)
            natural(at(11700 to 21), "isRelaxed", false)
            natural(at(11700 to 22, 11500 to 21), "color", DyeColor.RED.ordinal) {
                it.useIndexEditor(DyeColor::class.java, "color")
            }
        }
        from<AdyPainting> {
            naturalEditor("painting") {
                it.useEnumsEditor(type = BukkitPaintings::class.java, key = "painting_painting") { getPainting() }
                it.reset { _, entity -> entity.setPainting(BukkitPaintings.KEBAB) }
            }
            naturalEditor("direction") {
                it.useEnumsEditor(type = BukkitDirection::class.java, key = "painting_direction") { getDirection() }
                it.reset { _, entity -> entity.setDirection(BukkitDirection.NORTH) }
            }
        }
        from<AdyZombieVillager> {
            if (minecraftVersion >= 11400) {
                natural(at(11700 to 19), "isConverting", false)
                natural(at(11700 to 20, 11500 to 19, 11400 to 18), "villagerData", VillagerData(Villager.Type.PLAINS, Villager.Profession.NONE)) {
                    it.editable = false
                }
            } else {
                natural(at(11400 to -1, 11300 to 17, 11100 to 16), "profession", BukkitProfession.FARMER.ordinal) {
                    it.editable = false
                }
            }
            if (minecraftVersion >= 11400) {
                naturalEditor("villagerType") {
                    it.useEnumsEditor(type = Villager.Type::class.java, key = "villager_type") {
                        getMetadata<VillagerData>("villagerData").type
                    }
                    it.reset { _, entity -> entity.setVillagerData(VillagerData(Villager.Type.PLAINS, entity.getVillagerData().profession)) }
                }
                naturalEditor("villagerProfession") {
                    it.useEnumsEditor(type = Villager.Profession::class.java, key = "villager_profession") {
                        getMetadata<VillagerData>("villagerData").profession
                    }
                    it.reset { _, entity -> entity.setVillagerData(VillagerData(entity.getVillagerData().type, Villager.Profession.NONE)) }
                }
            } else {
                naturalEditor("villagerProfession") {
                    it.useEnumsEditor(type = BukkitProfession::class.java, key = "villager_profession_legacy") {
                        BukkitProfession.values()[getMetadata("profession")]
                    }
                    it.reset { _, entity -> entity.setLegacyProfession(BukkitProfession.FARMER) }
                }
            }
        }
        from<AdyZombie> {
            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11, 10800 to 12), "isBaby", false)
            if (MinecraftVersion.majorLegacy >= 11700) {
                natural(at(11700 to 18), "isBecomingDrowned", false)
            } else {
                natural(at(11500 to 17, 11400 to 16, 11300 to 15), "isDrowning", false)
                natural(at(11500 to 18, 11400 to 17, 11300 to 16, 11100 to 15, 11000 to 14, 10900 to 13, 10800 to 14), "isConverting", false)
            }
            natural(at(11400 to -1, 11200 to 14, 11000 to 15, 10900 to 14), "isHandsHeldUp", false)
            natural(at(11100 to -1, 11000 to 13, 10900 to 12, 10800 to 13), "zombieType", 0)
        }
        from<AdyWither> {
            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11, 10800 to 17), "firstHeadTarget", 0) {
                it.editable = false
            }
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12, 10800 to 18), "secondHeadTarget", 0) {
                it.editable = false
            }
            natural(at(11700 to 18, 11500 to 17, 11400 to 16, 11000 to 14, 10900 to 13, 10800 to 19), "thirdHeadTarget", 0) {
                it.editable = false
            }
            natural(at(11700 to 19, 11500 to 18, 11400 to 17, 11000 to 15, 10900 to 14, 10800 to 20), "invulnerableTime", 0)
        }
        from<AdyBoat> {
            natural(at(11700 to 8, 11600 to 7, 11000 to 6, 10900 to 5, 10800 to 17), "sinceLastHit", 0)
            natural(at(11700 to 9, 11600 to 8, 11000 to 7, 10900 to 6, 10800 to 18), "forwardDirection", 1)
            natural(at(11700 to 10, 11600 to 9, 11000 to 8, 10900 to 7, 10800 to 19), "damageTaken", 0f)
            natural(at(11700 to 11, 11600 to 10, 11000 to 9, 10900 to 8), "type", BukkitBoat.OAK.ordinal) {
                it.useIndexEditor(type = BukkitBoat::class.java, key = "type")
            }
            natural(at(11700 to 12, 11600 to 11, 11000 to 11, 10900 to 10), "leftPaddleTurning", false)
            natural(at(11700 to 13, 11600 to 12, 11000 to 10, 10900 to 9), "rightPaddleTurning", false)
            natural(at(11700 to 14, 11600 to 13, 11300 to 12), "splashTimer", 0)
        }
        from<AdyHorse> {
            natural(at(11700 to 19, 11500 to 18, 11400 to 17, 11000 to 15, 10900 to 14), "variant", 0) {
                it.editable = false
            }
            naturalEditor("horseColor") {
                it.useEnumsEditor(type = Horse.Color::class.java, key = "horse_color") { getColor() }
                it.reset { _, entity -> entity.setColor(Horse.Color.WHITE) }
            }
            naturalEditor("horseStyle") {
                it.useEnumsEditor(type = Horse.Style::class.java, key = "horse_style") { getStyle() }
                it.reset { _, entity -> entity.setStyle(Horse.Style.NONE) }
            }
        }
        from<AdyShulker> {
            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "attachFace", BukkitDirection.DOWN.ordinal) {
                it.useIndexEditor(type = BukkitDirection::class.java, key = "attachFace")
            }
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "attachPosition", Vector(0, 0, 0))
            natural(at(11700 to 18, 11500 to 17, 11400 to 16, 11000 to 14, 10900 to 13), "shieldHeight", 0.toByte())
            natural(at(11700 to 19, 11500 to 18, 11400 to 17, 11100 to 15), "color", DyeColor.PURPLE.ordinal) {
                it.useIndexEditor(type = DyeColor::class.java, key = "color")
            }
        }
        from<AdyOcelot> {
            mask(at(11400 to -1, 11000 to 13, 10900 to 12), "isSitting", 0x01)
            mask(at(11400 to -1, 11000 to 13, 10900 to 12), "isAngry", 0x02)
            mask(at(11400 to -1, 11000 to 13, 10900 to 12), "isTamed", 0x04)
            if (minecraftVersion >= 11400) {
                natural(at(11700 to 17, 11500 to 16, 11400 to 15), "isTrusting", false)
            } else {
                natural(at(11000 to 15, 10900 to 14), "type", BukkitOcelotType.UNTAMED.ordinal) {
                    it.useIndexEditor(type = BukkitOcelotType::class.java, key = "type")
                }
            }
        }
        from<AdyFox> {
            mask(at(11700 to 18, 11500 to 17, 11400 to 16), "isSitting", 0x01, false)
            mask(at(11700 to 18, 11500 to 17, 11400 to 16), "isCrouching", 0x04, false)
            mask(at(11700 to 18, 11500 to 17), "isInterested", 0x08, false)
            mask(at(11700 to 18, 11500 to 17), "isPouncing", 0x10, false)
            mask(at(11700 to 18, 11500 to 17, 11400 to 16), "isSleeping", 0x20, false)
            mask(at(11700 to 18, 11500 to 17), "isFaceplanted", 0x40, false)
            mask(at(11700 to 18, 11500 to 17), "isDefending", 0x80.toByte(), false)
            natural(at(11700 to 17, 11500 to 16, 11400 to 15), "type", Fox.Type.RED.ordinal) {
                it.useIndexEditor(type = Fox.Type::class.java, key = "type")
            }
        }
        from<AdyBee> {
            mask(at(11700 to 17, 11500 to 16), "unUsed", 0x01)
            mask(at(11700 to 17, 11500 to 16), "isFlip", 0x02)
            mask(at(11700 to 17, 11500 to 16), "hasStung", 0x04)
            mask(at(11700 to 17, 11500 to 16), "hasNectar", 0x08)
            natural(at(11700 to 18, 11500 to 17), "angerTicks", 0) {
                it.editable = false
            }
            naturalEditor("isAngered") {
                it.reset { _, entity -> entity.setAngered(false) }
                it.modify { player, entity ->
                    entity.setAngered(!entity.isAngered())
                    entity.openEditor(player)
                }
                it.display { player, entity -> entity.isAngered().toDisplay(player) }
            }
        }
        from<AdyTropicalFish> {
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11300 to 13), "variant", 0) {
                it.editable = false
            }
            naturalEditor("bodyColor") {
                it.useEnumsEditor(type = DyeColor::class.java, key = "body_color") { getBodyColor() }
                it.reset { _, entity -> entity.setBodyColor(DyeColor.WHITE) }
            }
            naturalEditor("patternColor") {
                it.useEnumsEditor(type = DyeColor::class.java, key = "pattern_color") { getPatternColor() }
                it.reset { _, entity -> entity.setPatternColor(DyeColor.WHITE) }
            }
            naturalEditor("pattern") {
                it.useEnumsEditor(type = TropicalFish.Pattern::class.java, key = "pattern") { getPattern() }
                it.reset { _, entity -> entity.setPattern(TropicalFish.Pattern.KOB) }
            }
        }
        from<AdyEntityLiving> {
            mask(at(11700 to 8), "isHandActive", 0x01)
            mask(at(11700 to 8), "activeHand", 0x02)
            mask(at(11700 to 8), "isInRiptideSpinAttack", 0x04)
            natural(at(11700 to 9, 11400 to 8, 11000 to 7, 10900 to 6, 10800 to 6), "health", 1.0f)
            natural(at(11700 to 10, 11400 to 9, 11000 to 8, 10900 to 7, 10800 to 7), "potionEffectColor", 0) { it.useColorEditor() }
            naturalEditor("equipment") { it.useHybridEquipmentEditor() }
            naturalEditor("isDie") {
                it.reset { _, entity -> entity.die(false) }
                it.modify { player, entity ->
                    entity.die(die = !entity.isDie)
                    entity.openEditor(player)
                }
                it.display { player, entity -> entity.isDie.toDisplay(player) }
            }
        }
        from<AdyFallingBlock> {
            naturalEditor("block") {
                it.reset { _, entity -> entity.setMaterial(Material.STONE, 0) }
                it.modify { player, entity ->
                    player.openMenu<Basic>(player.asLangText("editor-item-input")) {
                        handLocked(false)
                        rows(1)
                        map("####@####")
                        set('#', XMaterial.BLACK_STAINED_GLASS_PANE) {
                            name = "§f"
                        }
                        set('@', ItemStack(entity.material, 1, entity.data.toShort()))
                        onClick('#')
                        onClose { e ->
                            val item = e.inventory.getItem(4)
                            if (item.isNotAir()) {
                                entity.setMaterial(item!!.type, item.durability.toByte())
                            }
                            entity.openEditor(player)
                        }
                    }
                }
                it.display { player, entity -> ItemStack(entity.material, 1, entity.data.toShort()).getName(player) }
            }
        }
        from<AdyMinecart> {
            natural(at(11700 to 11, 11400 to 10, 11000 to 9, 10900 to 8), "customBlock", 0) {
                it.editable = false
            }
            natural(at(11700 to 12, 11400 to 11, 11000 to 10, 10900 to 9), "customBlockPosition", 6)
            natural(at(11700 to 13, 11400 to 12, 11000 to 11, 10900 to 10), "showCustomBlock", false)
            naturalEditor("block") {
                it.reset { _, entity -> entity.setCustomBlock(MaterialData(Material.AIR, 0)) }
                it.modify { player, entity ->
                    player.openMenu<Basic>(player.asLangText("editor-item-input")) {
                        handLocked(false)
                        rows(1)
                        map("####@####")
                        set('#', XMaterial.BLACK_STAINED_GLASS_PANE) {
                            name = "§f"
                        }
                        set('@', entity.getCustomBlock().toItemStack(1))
                        onClick('#')
                        onClose { e ->
                            try {
                                entity.setCustomBlock((e.inventory.getItem(4) ?: ItemStack(Material.AIR)).data!!)
                            } catch (t: Throwable) {
                                t.printStackTrace()
                            }
                            entity.openEditor(player)
                        }
                    }
                }
                it.display { player, entity -> kotlin.runCatching { entity.getCustomBlock().toItemStack(1).getName(player) }.getOrElse { "-" } }
            }
        }
        from<AdyVillager> {
            if (minecraftVersion >= 11400) {
                natural(at(11700 to 17), "headShakeTimer", 0)
                natural(at(11700 to 18, 11500 to 17, 11400 to 16), "villagerData", VillagerData(Villager.Type.PLAINS, Villager.Profession.NONE)) {
                    it.editable = false
                }
            } else {
                natural(at(11000 to 13, 10900 to 12), "profession", BukkitProfession.FARMER.ordinal) {
                    it.editable = false
                }
            }
            if (minecraftVersion >= 11400) {
                naturalEditor("villagerType") {
                    it.useEnumsEditor(type = Villager.Type::class.java, key = "villager_type") {
                        getMetadata<VillagerData>("villagerData").type
                    }
                    it.reset { _, entity -> entity.setVillagerData(VillagerData(Villager.Type.PLAINS, entity.getVillagerData().profession)) }
                }
                naturalEditor("villagerProfession") {
                    it.useEnumsEditor(type = Villager.Profession::class.java, key = "villager_profession") {
                        getMetadata<VillagerData>("villagerData").profession
                    }
                    it.reset { _, entity -> entity.setVillagerData(VillagerData(entity.getVillagerData().type, Villager.Profession.NONE)) }
                }
            } else {
                naturalEditor("villagerProfession") {
                    it.useEnumsEditor(type = BukkitProfession::class.java, key = "villager_profession_legacy") {
                        BukkitProfession.values()[getMetadata("profession")]
                    }
                    it.reset { _, entity -> entity.setLegacyProfession(BukkitProfession.FARMER) }
                }
            }
        }
        from<EntityInstance> {
            mask(0, "onFire", 0x01)
            mask(0, "isCrouched", 0x02)
            mask(0, "unUsedRiding", 0x04)
            mask(0, "isSprinting", 0x08)
            mask(0, "isSwimming", 0x010)
            mask(0, "isInvisible", 0x20)
            mask(0, "isGlowing", 0x40)
            mask(0, "isFlyingElytra", 0x80.toByte())
            natural(2, "customName", TextComponent(""))
            natural(3, "isCustomNameVisible", false)
            natural(at(11000 to 5), "noGravity", false)
            natural(at(11400 to 6), "pose", BukkitPose.STANDING) {
                it.reset { _, entity -> entity.setPose(BukkitPose.STANDING) }
                it.useEnumsEditor(type = BukkitPose::class.java, key = "pose") { getPose() }
                it.display { _, entity -> entity.getPose() }
            }
            natural(at(11700 to 7), "ticksFrozenInPowderedSnow", 0)
            naturalEditor("visibleDistance") {
                it.reset { _, entity -> entity.visibleDistance = -1.0 }
                it.modify { player, entity -> player.edit(entity, entity.visibleDistance) { value -> entity.visibleDistance = Coerce.toDouble(value) } }
                it.display { _, entity -> entity.visibleDistance }
            }
            naturalEditor("moveSpeed") {
                it.reset { _, entity -> entity.moveSpeed = 0.2 }
                it.modify { player, entity -> player.edit(entity, entity.moveSpeed) { value -> entity.moveSpeed = Coerce.toDouble(value) } }
                it.display { _, entity -> entity.moveSpeed }
            }
            if (AdyeshachAPI.modelEngineHooked) {
                naturalEditor("modelEngine") {
                    it.reset { _, entity -> entity.modelEngineName = "" }
                    it.modify { player, entity -> player.edit(entity, entity.modelEngineName) { value -> entity.modelEngineName = value } }
                    it.display { _, entity -> entity.modelEngineName.ifEmpty { "§7_" } }
                }
            }
        }
        from<AdyHuman> {
            mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12, 10800 to 10), "skinCape", 0x01, false)
            mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12, 10800 to 10), "skinJacket", 0x02, false)
            mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12, 10800 to 10), "skinLeftSleeve", 0x04, false)
            mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12, 10800 to 10), "skinRightSleeve", 0x08, false)
            mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12, 10800 to 10), "skinLeftPants", 0x10, false)
            mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12, 10800 to 10), "skinRightPants", 0x20, false)
            mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12, 10800 to 10), "skinHat", 0x40, false)
            natural(at(11700 to 12, 11600 to 11), "arrowsInEntity", 0)
            natural(at(11700 to 13, 11600 to 12), "beeStingersInEntity", 0)
            naturalEditor("isSleepingLegacy") {
                it.reset { _, entity -> entity.setSleeping(false) }
                it.modify { player, entity ->
                    entity.setSleeping(!entity.isSleeping())
                    entity.openEditor(player)
                }
                it.display { player, entity -> entity.isSleeping().toDisplay(player) }
            }
            naturalEditor("isHideFromTabList") {
                it.reset { _, entity -> entity.isHideFromTabList = true }
                it.modify { player, entity ->
                    entity.isHideFromTabList = !entity.isHideFromTabList
                    entity.openEditor(player)
                }
                it.display { player, entity -> entity.isHideFromTabList.toDisplay(player) }
            }
            naturalEditor("playerName") {
                it.reset { _, entity -> entity.setName("Adyeshach") }
                it.modify { player, entity ->
                    player.edit(entity, entity.getName()) { name ->
                        val cleanedName = name.replace("\"", "")
                        entity.setName(if (cleanedName.length > 16) cleanedName.substring(0, 16) else cleanedName)
                    }
                }
                it.display { _, entity -> entity.getName() }
            }
            naturalEditor("playerPing") {
                it.reset { _, entity -> entity.setPing(0) }
                it.modify { player, entity ->
                    player.edit(entity, entity.getPing()) { args -> entity.setPing(Coerce.toInteger(args.replace("\"", ""))) }
                }
                it.display { _, entity -> entity.getPing() }
            }
            naturalEditor("playerTexture") {
                it.reset { _, entity -> entity.resetTexture() }
                it.modify { player, entity ->
                    player.edit(entity, entity.getTextureName()) { args -> entity.setTexture(args.replace("\"", "")) }
                }
                it.display { _, entity -> entity.getTextureName().ifEmpty { "§7_" } }
            }
        }
    }

    inline fun <reified T : EntityInstance> from(reg: IMetaRegister<T>.() -> Unit) {
        try {
            IMetaRegister(T::class.java).also(reg)
        } catch (_: NoClassDefFoundError) {
        } catch (_: NoSuchFieldError) {
        } catch (ex: Throwable) {
            warning("Type: ${T::class.java.name}")
            ex.printStackTrace()
        }
    }

    class IMetaRegister<T : EntityInstance>(val useType: Class<T>) {

        fun mask(index: Int, key: String, mask: Byte, def: Boolean = false) {
            AdyeshachAPI.registerEntityMetaMask(useType, index, key, mask, def)
        }

        fun natural(index: Int, key: String, def: Any, editor: Consumer<MetaEditor<T>>? = null) {
            AdyeshachAPI.registerEntityMetaNatural(useType, index, key, def, editor as? Consumer<MetaEditor<*>>)
        }

        fun naturalEditor(key: String, editor: Consumer<MetaEditor<T>>) {
            AdyeshachAPI.registerEntityMetaNaturalEditor(useType, key, editor as Consumer<MetaEditor<*>>)
        }
    }
}