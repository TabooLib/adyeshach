package ink.ptms.adyeshach.internal.migrate

import com.mojang.authlib.properties.Property
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.bukkit.BukkitCreeperState
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.*
import io.izzel.taboolib.module.lite.SimpleReflection
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.npc.entity.EntityHumanNPC
import net.citizensnpcs.npc.skin.Skin
import org.bukkit.entity.*
import org.bukkit.inventory.EquipmentSlot

/**
 * @Author sky
 * @Since 2020-08-14 18:18
 */
class MigrateCitizens : Migrate() {

    override fun depend(): String {
        return "Citizens"
    }

    override fun migrate() {
        CitizensAPI.getNPCRegistry().forEach { npc ->
            if (npc.entity == null) {
                println("[Adyeshach][Migrate] Citizens ${npc.name}'s entity was not found.")
                return@forEach
            }
            val entityTypes = EntityTypes.fromBukkit(npc.entity.type)
            if (entityTypes == null) {
                println("[Adyeshach][Migrate] Citizens ${npc.entity.type} not supported.")
                return@forEach
            }
            npc.destroy()
            val entity = AdyeshachAPI.getEntityManagerPublic().create(entityTypes, npc.entity.location)
            entity.setCustomName(npc.fullName)
            entity.setCustomNameVisible(true)
            if (entity is AdyEntityAgeable && npc.entity is Ageable) {
                entity.setBaby((npc.entity as Ageable).age < 0)
            }
            if (entity is AdyArmorStand && npc.entity is ArmorStand) {
                entity.setBasePlate((npc.entity as ArmorStand).hasBasePlate())
                entity.setInvisible(!(npc.entity as ArmorStand).isVisible)
                entity.setSmall((npc.entity as ArmorStand).isSmall)
                entity.setArms((npc.entity as ArmorStand).hasArms())
            }
            if (entity is AdyBee && npc.entity is Bee) {
                entity.setAngered((npc.entity as Bee).anger > 0)
                entity.setNectar((npc.entity as Bee).hasNectar())
                entity.setStung((npc.entity as Bee).hasStung())
            }
            if (entity is AdyCat && npc.entity is Cat) {
                entity.setCollarColor((npc.entity as Cat).collarColor)
                entity.setType((npc.entity as Cat).catType)
            }
            if (entity is AdyFox && npc.entity is Fox) {
                entity.setType((npc.entity as Fox).foxType)
                entity.setSitting((npc.entity as Fox).isSitting)
                entity.setSleeping((npc.entity as Fox).isSleeping)
                entity.setCrouching((npc.entity as Fox).isCrouching)
            }
            if (entity is AdyHorse && npc.entity is Horse) {
                entity.setColor((npc.entity as Horse).color)
                entity.setStyle((npc.entity as Horse).style)
            }
            if (entity is AdyItem && npc.entity is Item) {
                entity.setItem((npc.entity as Item).itemStack)
            }
            if (entity is AdyLlama && npc.entity is Llama) {
                entity.setType((npc.entity as Llama).color)
            }
            if (entity is AdyMinecart && npc.entity is Minecart) {
                entity.setCustomBlock((npc.entity as Minecart).displayBlock)
                entity.setCustomBlockOffset((npc.entity as Minecart).displayBlockOffset)
            }
            if (entity is AdyOcelot && npc.entity is Ocelot) {
                entity.setType((npc.entity as Ocelot).catType)
            }
            if (entity is AdyParrot && npc.entity is Parrot) {
                entity.setColor((npc.entity as Parrot).variant)
            }
            if (entity is AdyPhantom && npc.entity is Phantom) {
                entity.setSize((npc.entity as Phantom).size)
            }
            if (entity is AdyCreeper && npc.entity is Creeper) {
                entity.setState(if ((npc.entity as Creeper).isPowered) BukkitCreeperState.FUSE else BukkitCreeperState.IDLE)
            }
            if (entity is AdyRabbit && npc.entity is Rabbit) {
                entity.setType((npc.entity as Rabbit).rabbitType)
            }
            if (entity is AdySheep && npc.entity is Sheep) {
                (npc.entity as Sheep).color?.let { entity.setDyeColor(it) }
                entity.setSheared((npc.entity as Sheep).isSheared)
            }
            if (entity is AdyShulker && npc.entity is Shulker) {
                (npc.entity as Shulker).color?.let { entity.setColor(it) }
            }
            if (entity is AdySlime && npc.entity is Slime) {
                entity.setSize((npc.entity as Slime).size)
            }
            if (entity is AdySnowGolem && npc.entity is Snowman) {
                entity.setPumpkinHat(!(npc.entity as Snowman).isDerp)
            }
            if (entity is AdyPufferfish && npc.entity is PufferFish) {
                entity.setPuffState((npc.entity as PufferFish).puffState)
            }
            if (entity is AdyTropicalFish && npc.entity is TropicalFish) {
                entity.setPatternColor((npc.entity as TropicalFish).patternColor)
                entity.setBodyColor((npc.entity as TropicalFish).bodyColor)
                entity.setPattern((npc.entity as TropicalFish).pattern)
            }
            if (entity is AdyHuman && npc.entity is Player) {
                val playerNPC = npc.entity as EntityHumanNPC.PlayerNPC
                val property = SimpleReflection.getFieldValueChecked(Skin::class.java, playerNPC.skinTracker.skin, "skinData", true) as Property
                entity.setName(playerNPC.name)
                entity.setTexture(property.value, property.signature)
            }
            if (entity is AdyEntityLiving && npc.entity is LivingEntity) {
                (npc.entity as LivingEntity).equipment?.helmet?.let { entity.setEquipment(EquipmentSlot.HEAD, it) }
                (npc.entity as LivingEntity).equipment?.chestplate?.let { entity.setEquipment(EquipmentSlot.CHEST, it) }
                (npc.entity as LivingEntity).equipment?.leggings?.let { entity.setEquipment(EquipmentSlot.LEGS, it) }
                (npc.entity as LivingEntity).equipment?.boots?.let { entity.setEquipment(EquipmentSlot.FEET, it) }
                (npc.entity as LivingEntity).equipment?.itemInMainHand?.let { entity.setEquipment(EquipmentSlot.HAND, it) }
                (npc.entity as LivingEntity).equipment?.itemInOffHand?.let { entity.setEquipment(EquipmentSlot.OFF_HAND, it) }
            }
            try {
                entity.setPose(BukkitPose.valueOf(npc.entity.pose.name))
            } catch (ignore: Throwable) {
            }
            try {
                entity.setSwimming((npc.entity as LivingEntity).isSwimming)
            } catch (ignore: Throwable) {
            }
            entity.setGlowing(npc.entity.isGlowing)
        }
    }
}