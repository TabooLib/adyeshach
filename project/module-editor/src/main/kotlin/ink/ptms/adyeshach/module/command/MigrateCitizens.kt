package ink.ptms.adyeshach.module.command

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.bukkit.BukkitCreeperState
import ink.ptms.adyeshach.core.bukkit.BukkitPose
import ink.ptms.adyeshach.core.entity.manager.ManagerType
import ink.ptms.adyeshach.core.entity.type.*
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.npc.skin.SkinnableEntity
import net.citizensnpcs.util.NMS
import org.bukkit.command.CommandSender
import org.bukkit.entity.*
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.function.info
import taboolib.common.platform.function.warning

/**
 * @author sky
 * @since 2020-08-14 18:18
 */
object MigrateCitizens {

    fun migrate(sender: CommandSender) {
        val npcList = CitizensAPI.getNPCRegistry().toList().filter { it.isSpawned }
        if (npcList.isEmpty()) {
            sender.sendMessage("No Citizens NPC found.")
            return
        }
        npcList.forEach { npc ->
            val citizenEntity = npc.entity
            if (citizenEntity == null) {
                sender.sendMessage("Citizens NPC \"${npc.name}\" (${npc.id}) is invalid or entity chunk not loaded.")
                return@forEach
            }
            val entityTypes = try {
                Adyeshach.api().getEntityTypeRegistry().getEntityTypeFromBukkit(citizenEntity.type)
            } catch (ex: Throwable) {
                ex.printStackTrace()
                return
            }
            val entity = Adyeshach.api().getPublicEntityManager(ManagerType.PERSISTENT).create(entityTypes, citizenEntity.location)
            entity.id = "citizens_${npc.id}"
            // 基本信息
            entity.setCustomName(npc.name)
            entity.setCustomNameVisible(true)
            entity.setGlowing(citizenEntity.isGlowing)
            // 姿态
            try {
                entity.setPose(BukkitPose.valueOf(citizenEntity.pose.name))
            } catch (ignore: Throwable) {
            }
            // 游泳
            try {
                entity.setSwimming((citizenEntity as LivingEntity).isSwimming)
            } catch (ignore: Throwable) {
            }
            // 类型兼容
            if (entity is AdyEntityAgeable && citizenEntity is Ageable) {
                entity.setBaby(citizenEntity.age < 0)
            }
            if (entity is AdyArmorStand && citizenEntity is ArmorStand) {
                entity.setBasePlate(citizenEntity.hasBasePlate())
                entity.setInvisible(!citizenEntity.isVisible)
                entity.setSmall(citizenEntity.isSmall)
                entity.setArms(citizenEntity.hasArms())
            }
            if (entity is AdyBee && citizenEntity is Bee) {
                entity.setAngered(citizenEntity.anger > 0)
                entity.setNectar(citizenEntity.hasNectar())
                entity.setStung(citizenEntity.hasStung())
            }
            if (entity is AdyCat && citizenEntity is Cat) {
                entity.setCollarColor(citizenEntity.collarColor)
                entity.setType(citizenEntity.catType)
            }
            if (entity is AdyFox && citizenEntity is Fox) {
                entity.setType(citizenEntity.foxType)
                entity.setSitting(citizenEntity.isSitting)
                entity.setSleeping(citizenEntity.isSleeping)
                entity.setCrouching(citizenEntity.isCrouching)
            }
            if (entity is AdyHorse && citizenEntity is Horse) {
                entity.setColor(citizenEntity.color)
                entity.setStyle(citizenEntity.style)
            }
            if (entity is AdyItem && citizenEntity is Item) {
                entity.setItem(citizenEntity.itemStack)
            }
            if (entity is AdyLlama && citizenEntity is Llama) {
                entity.setType(citizenEntity.color)
            }
            if (entity is AdyMinecart && citizenEntity is Minecart) {
                entity.setCustomBlock(citizenEntity.displayBlock)
                entity.setCustomBlockOffset(citizenEntity.displayBlockOffset)
            }
            if (entity is AdyOcelot && citizenEntity is Ocelot) {
                entity.setType(citizenEntity.catType)
            }
            if (entity is AdyParrot && citizenEntity is Parrot) {
                entity.setColor(citizenEntity.variant)
            }
            if (entity is AdyPhantom && citizenEntity is Phantom) {
                entity.setSize(citizenEntity.size)
            }
            if (entity is AdyCreeper && citizenEntity is Creeper) {
                entity.setState(if (citizenEntity.isPowered) BukkitCreeperState.FUSE else BukkitCreeperState.IDLE)
            }
            if (entity is AdyRabbit && citizenEntity is Rabbit) {
                entity.setType(citizenEntity.rabbitType)
            }
            if (entity is AdySheep && citizenEntity is Sheep) {
                citizenEntity.color?.let { entity.setDyeColor(it) }
                entity.setSheared(citizenEntity.isSheared)
            }
            if (entity is AdyShulker && citizenEntity is Shulker) {
                citizenEntity.color?.let { entity.setColor(it) }
            }
            if (entity is AdySlime && citizenEntity is Slime) {
                entity.setSize(citizenEntity.size)
            }
            if (entity is AdySnowGolem && citizenEntity is Snowman) {
                entity.setPumpkinHat(!citizenEntity.isDerp)
            }
            if (entity is AdyPufferfish && citizenEntity is PufferFish) {
                entity.setPuffState(citizenEntity.puffState)
            }
            if (entity is AdyTropicalFish && citizenEntity is TropicalFish) {
                entity.setPatternColor(citizenEntity.patternColor)
                entity.setBodyColor(citizenEntity.bodyColor)
                entity.setPattern(citizenEntity.pattern)
            }
            if (entity is AdyHuman && citizenEntity is Player) {
                val skin = if (citizenEntity is SkinnableEntity) citizenEntity else NMS.getSkinnable(citizenEntity)
                if (skin != null && !skin.profile.properties.isEmpty) {
                    val property = skin.profile.properties.entries().first().value
                    entity.setName(npc.name)
                    entity.setTexture(property.value, property.signature)
                }
            }
            if (entity is AdyEntityLiving && citizenEntity is LivingEntity) {
                citizenEntity.equipment?.apply {
                    entity.setEquipment(EquipmentSlot.HAND, itemInMainHand)
                    entity.setEquipment(EquipmentSlot.OFF_HAND, itemInOffHand)
                    helmet?.apply { entity.setEquipment(EquipmentSlot.HEAD, this) }
                    chestplate?.apply { entity.setEquipment(EquipmentSlot.CHEST, this) }
                    leggings?.apply { entity.setEquipment(EquipmentSlot.LEGS, this) }
                    boots?.apply { entity.setEquipment(EquipmentSlot.FEET, this) }
                }
            }
            npc.despawn()
            sender.sendMessage("Migrated ${npc.name} (${npc.id}) -> ${entity.id} (${entity.uniqueId})")
        }
    }
}