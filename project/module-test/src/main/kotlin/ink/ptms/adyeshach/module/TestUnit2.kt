package ink.ptms.adyeshach.module

import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.bukkit.data.EmptyVector
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Art
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Cat
import org.bukkit.entity.Frog
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import taboolib.module.nms.MinecraftVersion

fun testCreateMeta(sender: CommandSender) {
    val metadataHandler = Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler()
    sender.info("Testing createMeta()")
    sender.test("createByteMeta") {
        metadataHandler.createByteMeta(0, 0)
    }
    // Int
    sender.test("createIntMeta") {
        metadataHandler.createIntMeta(0, 0)
    }
    // Float
    sender.test("createFloatMeta") {
        metadataHandler.createFloatMeta(0, 0f)
    }
    // String
    sender.test("createStringMeta") {
        metadataHandler.createStringMeta(0, "")
    }
    sender.test("OptionalComponent") {
        metadataHandler.createOptionalComponentMeta(0, "")
    }
    // ItemStack
    sender.test("createItemStackMeta") {
        metadataHandler.createItemStackMeta(0, ItemStack(Material.AIR))
    }
    // BlockState
    sender.test("createBlockStateMeta") {
        metadataHandler.createBlockStateMeta(0, MaterialData(Material.AIR))
    }
    // Boolean
    sender.test("createBooleanMeta") {
        metadataHandler.createBooleanMeta(0, false)
    }
    // Particle
    if (MinecraftVersion.majorLegacy >= 11300) {
        sender.test("createParticleMeta") {
            metadataHandler.createParticleMeta(0, BukkitParticles.FLAME)
        }
    }
    // BlockPos
    sender.test("createBlockPosMeta") {
        metadataHandler.createBlockPosMeta(0, EmptyVector())
    }
    // EulerAngle
    sender.test("createEulerAngleMeta") {
        metadataHandler.createEulerAngleMeta(0, EulerAngle.ZERO)
    }
    if (MinecraftVersion.majorLegacy >= 11400) {
        // VillagerData
        sender.test("createVillagerData") {
            metadataHandler.createVillagerDataMeta(0, VillagerData(VillagerData.Type.PLAINS, VillagerData.Profession.NONE))
        }
        // Pose
        sender.test("createPoseMeta") {
            metadataHandler.createPoseMeta(0, BukkitPose.STANDING)
        }
    }
    // 11900+
    if (MinecraftVersion.majorLegacy >= 11900) {
        // CatVariant
        sender.test("createCatVariantMeta") {
            metadataHandler.createCatVariantMeta(0, Cat.Type.TABBY)
        }
        // FrogVariant
        sender.test("createFrogVariantMeta") {
            metadataHandler.createFrogVariantMeta(0, Frog.Variant.TEMPERATE)
        }
        // Art
        sender.test("createArtMeta") {
            metadataHandler.createPaintingVariantMeta(0, Art.KEBAB)
        }
    }
    sender.info("OK")
}