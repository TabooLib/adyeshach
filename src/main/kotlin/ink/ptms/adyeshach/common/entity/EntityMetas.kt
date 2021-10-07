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
import org.bukkit.entity.Fox
import org.bukkit.entity.Horse
import org.bukkit.entity.TropicalFish
import org.bukkit.entity.Villager
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common5.Coerce
import taboolib.module.nms.MinecraftVersion
import java.util.function.Consumer

@Suppress("UNCHECKED_CAST")
object EntityMetas {

    private val minecraftVersion = MinecraftVersion.majorLegacy

    @Awake(LifeCycle.ENABLE)
    private fun init() {
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
        from<AdyBat> {
            mask(at(11700 to 16, 11600 to 15, 11000 to 12, 10900 to 11), "isHanging", 0x01)
        }
        from<AdyBlaze> {
            mask(at(11700 to 16, 11600 to 15, 11100 to 12, 10900 to 11), "fire", 0x01)
        }
        from<AdyItem> {
            natural(at(11700 to 8, 11300 to 7, 11000 to 6, 10900 to 5), "item", ItemStack(Material.BEDROCK))
        }
        from<AdySlime> {
            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "size", 1)
        }
        from<AdyPillager> {
            natural(at(11700 to 17), "isCharging", false)
        }
        from<AdyPig> {
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "hasSaddle", false)
        }
        from<AdyThrownPotion> {
            natural(at(11700 to 8, 11600 to 7), "item", ItemStack(Material.AIR))
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
        from<AdyFireball> {
            natural(at(11600 to 7), "item", ItemStack(Material.AIR))
        }
        from<AdyThrownEnderPearl> {
            natural(at(11700 to 8, 11600 to 7), "item", ItemStack(Material.ENDER_PEARL))
        }
        from<AdyThrownExperienceBottle> {
            natural(at(11700 to 8, 11600 to 7), "item", ItemStack(Material.EXPERIENCE_BOTTLE))
        }
        from<AdySnowball> {
            natural(at(11700 to 8, 11600 to 7), "item", ItemStack(Material.SNOWBALL))
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
            natural(at(11700 to 8, 11600 to 7), "item", ItemStack(Material.EGG))
        }
        from<AdyPrimedTNT> {
            natural(at(11700 to 8, 11400 to 7, 11000 to 6, 10900 to 5), "fuseTime", 80)
        }
        from<AdyPufferfish> {
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11300 to 13), "puffState", 0)
        }
        from<AdySmallFireball> {
            natural(at(11700 to 8, 11600 to 7), "item", ItemStack(Material.FIRE_CHARGE))
        }
        from<AdySpider> {
            mask(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "isClimbing", 0x01)
        }
        from<AdyEyeOfEnder> {
            natural(at(11700 to 8, 11400 to 7), "item", ItemStack(Material.ENDER_EYE))
        }
        from<AdyFireworkRocket> {
            natural(at(11700 to 8, 11400 to 7, 11100 to 6, 10900 to 9), "fireworkInfo", ItemStack(Material.AIR))
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
            natural(at(11700 to 8, 11300 to 7, 11000 to 6, 10900 to 5), "item", ItemStack(Material.AIR))
            natural(at(11700 to 9, 11300 to 8, 11000 to 7, 10900 to 6), "rotation", 0)
        }
        from<AdyEnderman> {
            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "carriedBlock", MaterialData(Material.AIR))
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "isScreaming", false)
            natural(at(11700 to 18, 11500 to 17), "isStaring", false)
        }
        from<AdyArrow> {
            mask(at(11700 to 8, 11600 to 7, 11000 to 6, 10900 to 5), "isCritical", 0x01)
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
            mask(at(11200 to -1, 11000 to 12, 10900 to 11), "isRetractingSpikes", 0x02)
            mask(at(11200 to -1, 11000 to 12, 10900 to 11), "isElderly", 0x04)
            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11100 to 13), "isRetractingSpikes", false)
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "targetEntity", false)
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
        from<AdyMushroom> {
            natural(at(11700 to 17, 11500 to 16, 11400 to 15), "type", BukkitMushroom.RED.name.lowercase()) {
                it.useEnumsEditor(type = BukkitMushroom::class.java, key = "text")
            }
        }
        from<AdyWolf> {
            natural(at(11700 to 19, 11400 to 18, 11000 to 16, 10900 to 15), "isBegging", false)
            natural(at(11700 to 20, 11400 to 19, 11000 to 17, 10900 to 16), "collarColor", DyeColor.RED.ordinal) {
                it.useEnumsEditor(type = DyeColor::class.java, key = "int", useIndex = true) {
                    DyeColor.values()[getMetadata("collarColor")]
                }
            }
        }
        from<AdyZombieVillager> {
            if (minecraftVersion >= 11400) {
                natural(at(11700 to 19), "isConverting", false)
                natural(at(11700 to 20, 11500 to 19, 11400 to 18), "villagerData", VillagerData(Villager.Type.PLAINS, Villager.Profession.NONE))
            } else {
                natural(at(11400 to -1, 11300 to 17, 11100 to 16), "profession", BukkitProfession.FARMER.ordinal) {
                    it.editable = false
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
                it.useEnumsEditor(type = Fox.Type::class.java, key = "int", useIndex = true) {
                    Fox.Type.values()[getMetadata("type")]
                }
            }
        }
        from<AdyArmorStand> {
            mask(at(11700 to 15, 11500 to 14, 11400 to 13, 11000 to 11, 10900 to 10), "isSmall", 0x01)
            mask(at(11700 to 15, 11500 to 14, 11400 to 13, 11000 to 11, 10900 to 10), "hasArms", 0x04)
            mask(at(11700 to 15, 11500 to 14, 11400 to 13, 11000 to 11, 10900 to 10), "noBasePlate", 0x08)
            mask(at(11700 to 15, 11500 to 14, 11400 to 13, 11000 to 11, 10900 to 10), "isMarker", 0x10)
            mask(at(11000 to -1, 10900 to 10), "hasGravity", 0x02)
            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "angleHead", EulerAngle(0.0, 0.0, 0.0))
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "angleBody", EulerAngle(0.0, 0.0, 0.0))
            natural(at(11700 to 18, 11500 to 17, 11400 to 16, 11000 to 14, 10900 to 13), "angleLeftArm", EulerAngle(-10.0, 0.0, -10.0))
            natural(at(11700 to 19, 11500 to 18, 11400 to 17, 11000 to 15, 10900 to 14), "angleRightArm", EulerAngle(-15.0, 0.0, 10.0))
            natural(at(11700 to 20, 11500 to 19, 11400 to 18, 11000 to 16, 10900 to 15), "angleLeftLeg", EulerAngle(-1.0, 0.0, -1.0))
            natural(at(11700 to 21, 11500 to 20, 11400 to 19, 11000 to 17, 10900 to 16), "angleRightLeg", EulerAngle(1.0, 0.0, 1.0))
        }
        from<AdyEntityLiving> {
            mask(at(11700 to 8), "isHandActive", 0x01)
            mask(at(11700 to 8), "activeHand", 0x02)
            mask(at(11700 to 8), "isInRiptideSpinAttack", 0x04)
            natural(at(11700 to 9, 11400 to 8, 11000 to 7, 10900 to 6), "health", 1.0f)
            natural(at(11700 to 10, 11400 to 9, 11000 to 8, 10900 to 7), "potionEffectColor", 0) { it.useColorEditor() }
            naturalEditor("equipmentHelmet") { it.useEquipmentEditor(EquipmentSlot.HEAD) }
            naturalEditor("equipmentChestplate") { it.useEquipmentEditor(EquipmentSlot.CHEST) }
            naturalEditor("equipmentLeggings") { it.useEquipmentEditor(EquipmentSlot.LEGS) }
            naturalEditor("equipmentBoots") { it.useEquipmentEditor(EquipmentSlot.FEET) }
            naturalEditor("equipmentHand") { it.useEquipmentEditor(EquipmentSlot.HEAD) }
            naturalEditor("equipmentOffhand") { it.useEquipmentEditor(EquipmentSlot.OFF_HAND) }
            naturalEditor("isDie") {
                it.reset { _, entity -> entity.die(false) }
                it.modify { player, entity ->
                    entity.die(die = !entity.isDie)
                    entity.openEditor(player)
                }
                it.display { player, entity -> entity.isDie.toDisplay(player) }
            }
        }
        from<AdyHuman> {
            natural(at(11700 to 12, 11600 to 11), "arrowsInEntity", 0)
            natural(at(11700 to 13, 11600 to 12), "beeStingersInEntity", 0)
            mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinCape", 0x01, true)
            mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinJacket", 0x02, true)
            mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinLeftSleeve", 0x04, true)
            mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinRightSleeve", 0x08, true)
            mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinLeftPants", 0x10, true)
            mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinRightPants", 0x20, true)
            mask(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinHat", 0x40, true)
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
            naturalEditor("playerName")
                .reset { _, _ ->
                    setName("Adyeshach NPC")
                }
                .modify { player, entity, _ ->
                    player.inputSign(arrayOf(getName(), "", "请在第一行输入内容")) {
                        if (it[0].isNotEmpty()) {
                            val name = "${it[0]}${it[1]}"
                            setName(if (name.length > 16) name.substring(0, 16) else name)
                        }
                        entity.openEditor(player)
                    }
                }
                .display { _, _, _ ->
                    if (getName().isEmpty()) "§7_" else Editor.toSimple(getName())
                }
            naturalEditor("playerPing")
                .reset { _, _ ->
                    setPing(60)
                }
                .modify { player, entity, _ ->
                    player.inputSign(arrayOf("${getPing()}", "", "请在第一行输入内容")) {
                        if (it[0].isNotEmpty()) {
                            setPing(NumberConversions.toInt(it[0]))
                        }
                        entity.openEditor(player)
                    }
                }
                .display { _, _, _ ->
                    getPing().toString()
                }
            naturalEditor("playerTexture")
                .reset { _, _ ->
                    resetTexture()
                }
                .modify { player, entity, _ ->
                    player.inputSign(arrayOf(getTextureName(), "", "请在第一行输入内容")) {
                        if (it[0].isNotEmpty()) {
                            setTexture(it[0])
                        }
                        entity.openEditor(player)
                    }
                }
                .display { _, _, _ ->
                    if (gameProfile.textureName.isEmpty()) "§7_" else Editor.toSimple(gameProfile.textureName)
                }
        }
        from<AdyExperienceOrb> {
            naturalEditor("amount") {
                it.reset { _, entity ->
                    (entity as AdyExperienceOrb).amount = 1
                }
                it.modify { player, entity ->
                    player.edit(entity, (entity as AdyExperienceOrb).amount) { args ->
                        entity.amount = Coerce.toInteger(args)
                    }
                }
                it.display { _, entity ->
                    (entity as AdyExperienceOrb).amount
                }
            }
        }
        from<AdyPainting> {
            naturalEditor("painting") {
                it.useEnumsEditor(type = BukkitPaintings::class.java, key = "painting_painting") {
                    (this as AdyPainting).getPainting()
                }
                it.reset { _, entity ->
                    (entity as AdyPainting).setPainting(BukkitPaintings.KEBAB)
                }
            }
            naturalEditor("direction") {
                it.useEnumsEditor(type = BukkitDirection::class.java, key = "painting_direction") {
                    (this as AdyPainting).getDirection()
                }
                it.reset { _, entity ->
                    (entity as AdyPainting).setDirection(BukkitDirection.NORTH)
                }
            }
        }
        from<AdyHorse> {
            natural(at(11700 to 19, 11500 to 18, 11400 to 17, 11000 to 15, 10900 to 14), "variant", 0) {
                it.editable = false
            }
            naturalEditor("horseColor") {
                it.useEnumsEditor(type = Horse.Color::class.java, key = "horse_color") {
                    (this as AdyHorse).getColor()
                }
                it.reset { _, entity ->
                    (entity as AdyHorse).setColor(Horse.Color.WHITE)
                }
            }
            naturalEditor("horseStyle") {
                it.useEnumsEditor(type = Horse.Color::class.java, key = "horse_style") {
                    (this as AdyHorse).getStyle()
                }
                it.reset { _, entity ->
                    (entity as AdyHorse).setStyle(Horse.Style.NONE)
                }
            }
        }
        from<AdyTropicalFish> {
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11300 to 13), "variant", 0) {
                it.editable = false
            }
            naturalEditor("bodyColor") {
                it.useEnumsEditor(type = DyeColor::class.java, key = "body_color") {
                    (this as AdyTropicalFish).getBodyColor()
                }
                it.reset { _, entity ->
                    (entity as AdyTropicalFish).setBodyColor(DyeColor.WHITE)
                }
            }
            naturalEditor("pattern") {
                it.useEnumsEditor(type = TropicalFish.Pattern::class.java, key = "pattern") {
                    (this as AdyTropicalFish).getPattern()
                }
                it.reset { _, entity ->
                    (entity as AdyTropicalFish).setPattern(TropicalFish.Pattern.KOB)
                }
            }
            naturalEditor("patternColor") {
                it.useEnumsEditor(type = DyeColor::class.java, key = "pattern_color") {
                    (this as AdyTropicalFish).getPatternColor()
                }
                it.reset { _, entity ->
                    (entity as AdyTropicalFish).setPatternColor(DyeColor.WHITE)
                }
            }
        }
        from<AdyVillager> {
            if (minecraftVersion >= 11400) {
                natural(at(11700 to 17), "headShakeTimer", 0)
                natural(at(11700 to 18, 11500 to 17, 11400 to 16), "villagerData", VillagerData(Villager.Type.PLAINS, Villager.Profession.NONE))
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
                    it.reset { _, entity ->
                        (entity as AdyVillager).setVillagerData(VillagerData(Villager.Type.PLAINS, entity.getVillagerData().profession))
                    }
                }
                naturalEditor("villagerProfession") {
                    it.useEnumsEditor(type = Villager.Profession::class.java, key = "villager_profession") {
                        getMetadata<VillagerData>("villagerData").profession
                    }
                    it.reset { _, entity ->
                        (entity as AdyVillager).setVillagerData(VillagerData(entity.getVillagerData().type, Villager.Profession.NONE))
                    }
                }
            } else {
                naturalEditor("villagerProfession") {
                    it.useEnumsEditor(type = BukkitProfession::class.java, key = "villager_profession_legacy") {
                        BukkitProfession.values()[getMetadata("profession")]
                    }
                    it.reset { _, entity ->
                        (entity as AdyVillager).setLegacyProfession(BukkitProfession.FARMER)
                    }
                }
            }
        }
//        from<AdyBoat> {
//            /**
//             * 1.13 -> Index 6-12, 且 leftPaddleTurning / rightPaddleTurning 换位置
//             * 1.12 -> Index 6-11, 在 1.13 基础上砍掉最后一个
//             * 1.9 ->  Index 5-10 和 1.12 对应
//             */
//            natural(at(11700 to 8, 11600 to 7, 11000 to 6, 10900 to 5), "sinceLastHit", 0)
//            natural(at(11700 to 9, 11600 to 8, 11000 to 7, 10900 to 6), "forwardDirection", 1)
//            natural(at(11700 to 10, 11600 to 9, 11000 to 8, 10900 to 7), "damageTaken", 0f)
//            natural(at(11700 to 11, 11600 to 10, 11000 to 9, 10900 to 8), "type", BukkitBoat.OAK.ordinal)
//                .from(Editors.enums(BukkitBoat::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
//                .display { _, entity, _ ->
//                    BukkitBoat.values()[entity.getMetadata("type")].name
//                }.build()
//            natural(at(11700 to 12, 11600 to 11, 11000 to 11, 10900 to 10), "leftPaddleTurning", false)
//            natural(at(11700 to 13, 11600 to 12, 11000 to 10, 10900 to 9), "rightPaddleTurning", false)
//            natural(at(11700 to 14, 11600 to 13, 11300 to 12), "splashTimer", 0)
//        }
//        from<AdyOcelot> {
//            mask(at(11400 to -1, 11000 to 13, 10900 to 12), "isSitting", 0x01)
//            mask(at(11400 to -1, 11000 to 13, 10900 to 12), "isAngry", 0x02)
//            mask(at(11400 to -1, 11000 to 13, 10900 to 12), "isTamed", 0x04)
//
//            if (minecraftVersion >= 11400) {
//                natural(at(11700 to 17, 11500 to 16, 11400 to 15), "isTrusting", false)
//            } else {
//                natural(at(11000 to 15, 10900 to 14), "type", BukkitOcelotType.UNTAMED.ordinal)
//                    .from(Editors.enums(BukkitOcelotType::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
//                    .display { _, entity, _ ->
//                        BukkitOcelotType.values()[entity.getMetadata("type")].name
//                    }.build()
//            }
//        }
//        from<AdyBee> {
//            /**
//             * 1.15+ 一致
//             */
//            mask(at(11700 to 17, 11500 to 16), "unUsed", 0x01)
//            mask(at(11700 to 17, 11500 to 16), "isFlip", 0x02)
//            mask(at(11700 to 17, 11500 to 16), "hasStung", 0x04)
//            mask(at(11700 to 17, 11500 to 16), "hasNectar", 0x08)
//            natural(at(11700 to 18, 11500 to 17), "angerTicks", 0)
//                .canEdit(false)
//                .build()
//            naturalEditor("isAngered")
//                .reset { entity, _ ->
//                    (entity as AdyBee).setAngered(false)
//                }
//                .modify { player, entity, _ ->
//                    (entity as AdyBee).setAngered(!entity.isAngered())
//                    entity.openEditor(player)
//                }
//                .display { _, entity, _ ->
//                    (entity as AdyBee).isAngered().toDisplay()
//                }
//        }
//        from<AdyShulker> {
//            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "attachFace", BukkitDirection.DOWN.ordinal)
//                .from(Editors.enums(BukkitDirection::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
//                .display { _, entity, _ ->
//                    BukkitDirection.values()[entity.getMetadata("attachFace")].name
//                }.build()
//            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "attachPosition", VectorNull())
//            natural(at(11700 to 18, 11500 to 17, 11400 to 16, 11000 to 14, 10900 to 13), "shieldHeight", 0.toByte())
//            natural(at(11700 to 19, 11500 to 18, 11400 to 17, 11100 to 15), "color", DyeColor.PURPLE.ordinal)
//                .from(Editors.enums(DyeColor::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
//                .display { _, entity, _ ->
//                    DyeColor.values()[entity.getMetadata("color")].name
//                }.build()
//        }
        from<AdyWither> {
            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "firstHeadTarget", 0) {
                it.editable = false
            }
            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "secondHeadTarget", 0) {
                it.editable = false
            }
            natural(at(11700 to 18, 11500 to 17, 11400 to 16, 11000 to 14, 10900 to 13), "thirdHeadTarget", 0) {
                it.editable = false
            }
            natural(at(11700 to 19, 11500 to 18, 11400 to 17, 11000 to 15, 10900 to 14), "invulnerableTime", 0)
        }
//        from<AdyParrot> {
//            natural(at(11700 to 19, 11500 to 18, 11400 to 17, 11200 to 15), "color", Parrot.Variant.RED.ordinal)
//                .from(Editors.enums(Parrot.Variant::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
//                .display { _, entity, _ ->
//                    Parrot.Variant.values()[entity.getMetadata("color")].name
//                }.build()
//        }
//        from<AdySheep> {
//            val index = at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12)
//            natural(index, "dyeColor", DyeColor.WHITE.ordinal)
//                .from(Editors.enums(DyeColor::class) { _, entity, meta, _, e -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} ${(e as DyeColor).ordinal}" })
//                .display { _, entity, _ ->
//                    DyeColor.values()[entity.getMetadata("dyeColor")].name
//                }.build()
//            mask(index, "isSheared", 0x10)
//        }
//        from<AdyLlama> {
//            natural(at(11700 to 21, 11500 to 20, 11400 to 19, 11100 to 17), "carpetColor", -1)
//                .from(Editors.enums(DyeColor::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
//                .display { _, entity, _ ->
//                    DyeColor.values()[entity.getMetadata("carpetColor")].name
//                }.build()
//            natural(at(11700 to 22, 11500 to 21, 11400 to 20, 11100 to 18), "color", Llama.Color.CREAMY.ordinal)
//                .from(Editors.enums(Llama.Color::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
//                .display { _, entity, _ ->
//                    Llama.Color.values()[entity.getMetadata("color")].name
//                }.build()
//        }
//        from<AdyRabbit> {
//            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "type", Rabbit.Type.BLACK.ordinal)
//                .from(Editors.enums(Rabbit.Type::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
//                .display { _, entity, _ ->
//                    Rabbit.Type.values()[entity.getMetadata("type")].name
//                }.build()
//        }
//        from<AdyCat> {
//            /**
//             * 仅 1.16, 1.15 有属性
//             */
//            natural(at(11700 to 19, 11500 to 18), "type", Cat.Type.TABBY.ordinal)
//                .from(Editors.enums(Cat.Type::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
//                .display { _, entity, _ ->
//                    Cat.Type.values()[entity.getMetadata("type")].name
//                }.build()
//            natural(at(11700 to 20), "isLying", false)
//            natural(at(11700 to 21), "isRelaxed", false)
//            natural(at(11700 to 22, 11500 to 21), "color", DyeColor.RED.ordinal)
//                .from(Editors.enums(DyeColor::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
//                .display { _, entity, _ ->
//                    DyeColor.values()[entity.getMetadata("color")].name
//                }.build()
//        }
//        from<AdyZombie> {
//            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "isBaby", false)
//            if (MinecraftVersion.majorLegacy >= 11700) {
//                natural(at(11700 to 18), "isBecomingDrowned", false)
//            } else {
//                natural(at(11500 to 17, 11400 to 16, 11300 to 15), "isDrowning", false)
//                natural(at(11500 to 18, 11400 to 17, 11300 to 16, 11100 to 15, 11000 to 14, 10900 to 13), "isConverting", false)
//            }
//            natural(at(11400 to -1, 11200 to 14, 11000 to 15, 10900 to 14), "isHandsHeldUp", false)
//            natural(at(11100 to -1, 11000 to 13, 10900 to 12), "zombieType", 0)
//        }
//        from<AdyEndDragon> {
//            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "dragonPhase", BukkitDragonPhase.HOVERING_WITH_NO_AI.ordinal)
//                .from(Editors.enums(BukkitDragonPhase::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
//                .display { _, entity, _ ->
//                    Editor.toSimple(BukkitDragonPhase.values()[entity.getMetadata("dragonPhase")].name)
//                }.build()
//        }
//        from<AdyCreeper> {
//            natural(at(11700 to 16, 11500 to 15, 11400 to 14, 11000 to 12, 10900 to 11), "state", BukkitCreeperState.IDLE.ordinal)
//                .from(Editors.enums(BukkitCreeperState::class) { _, entity, meta, index, _ -> "/adyeshachapi edit int ${entity.uniqueId} ${meta.key} $index" })
//                .display { _, entity, _ ->
//                    BukkitCreeperState.values()[entity.getMetadata("state")].name
//                }.build()
//            natural(at(11700 to 17, 11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "isCharged", false)
//            natural(at(11700 to 18, 11500 to 17, 11400 to 16, 11000 to 14, 10900 to 13), "isIgnited", false)
//        }
//        from<AdyFallingBlock> {
//            naturalEditor("block")
//                .reset { _, _ ->
//                    material = Material.STONE
//                    data = 0.toByte()
//                }
//                .modify { player, entity, _ ->
//                    player.openMenu<Basic>("Adyeshach Editor : Input") {
//                        rows(1)
//                        map("####@####")
//                        set('#', XMaterial.BLACK_STAINED_GLASS_PANE) {
//                            name = "§f"
//                        }
//                        set('@', ItemStack(material, 1, data.toShort()))
//                        onClick('#')
//                        onClose {
//                            val item = it.inventory.getItem(4)
//                            if (item.isNotAir()) {
//                                material = item!!.type
//                                data = item.durability.toByte()
//                            }
//                            destroy()
//                            spawn(getLocation())
//                            entity.openEditor(player)
//                        }
//                    }
//                }
//                .display { player, _, _ ->
//                    ItemStack(material, 1, data.toShort()).getName(player)
//                }
//        }
//        from<AdyMinecart> {
//            natural(at(11700 to 11, 11400 to 10, 11000 to 9, 10900 to 8), "customBlock", 0)
//                .canEdit(false)
//                .build()
//            natural(at(11700 to 12, 11400 to 11, 11000 to 10, 10900 to 9), "customBlockPosition", 6)
//            natural(at(11700 to 13, 11400 to 12, 11000 to 11, 10900 to 10), "showCustomBlock", false)
//            naturalEditor("block")
//                .reset { _, _ ->
//                    setCustomBlock(MaterialData(Material.AIR, 0))
//                }
//                .modify { player, entity, _ ->
//                    player.openMenu<Basic>("Adyeshach Editor : Input") {
//                        rows(1)
//                        map("####@####")
//                        set('#', XMaterial.BLACK_STAINED_GLASS_PANE) {
//                            name = "§f"
//                        }
//                        set('@', getCustomBlock().toItemStack(1))
//                        onClick('#')
//                        onClose {
//                            try {
//                                setCustomBlock((it.inventory.getItem(4) ?: ItemStack(Material.AIR)).data!!)
//                            } catch (t: Throwable) {
//                                t.printStackTrace()
//                            }
//                            entity.openEditor(player)
//                        }
//                    }
//                }
//                .display { player, _, _ ->
//                    getCustomBlock().toItemStack(1).getName(player)
//                }
//        }
    }

    inline fun <reified T : EntityInstance> from(reg: IMetaRegister<T>.() -> Unit) {
        IMetaRegister(T::class.java).also(reg)
    }

    class IMetaRegister<T : EntityInstance>(val useType: Class<T>) {

        fun mask(index: Int, key: String, mask: Byte, def: Boolean = false) {
            AdyeshachAPI.registerEntityMetaMask(useType, index, key, mask, def)
        }

        fun natural(index: Int, key: String, def: Any, editor: Consumer<MetaEditor<T>>? = null) {
            AdyeshachAPI.registerEntityMetaNatural(useType, index, key, def, editor)
        }

        fun naturalEditor(key: String, editor: Consumer<MetaEditor<T>>) {
            AdyeshachAPI.registerEntityMetaNaturalEditor(useType, key, editor)
        }
    }
}