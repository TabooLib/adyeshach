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
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import taboolib.module.nms.MinecraftVersion

fun testParser(sender: CommandSender) {
    val metadataHandler = Adyeshach.api().getMinecraftAPI().getEntityMetadataHandler()
    sender.info("Testing getParser()")
    sender.testClass("Int", "IntParser") {
        metadataHandler.getParser(0)
    }
    sender.testClass("Byte", "ByteParser") {
        metadataHandler.getParser(0.toByte())
    }
    sender.testClass("Float", "FloatParser") {
        metadataHandler.getParser(0f)
    }
    sender.testClass("String", "StringParser") {
        metadataHandler.getParser("")
    }
    sender.testClass("Boolean", "BooleanParser") {
        metadataHandler.getParser(false)
    }
    sender.testClass("Vector", "BlockPosParser") {
        metadataHandler.getParser(EmptyVector())
    }
    sender.testClass("ItemStack", "ItemStackParser") {
        metadataHandler.getParser(ItemStack(Material.AIR))
    }
    sender.testClass("EulerAngle", "EulerAngleParser") {
        metadataHandler.getParser(EulerAngle.ZERO)
    }
    sender.testClass("MaterialData", "BlockStateParser") {
        metadataHandler.getParser(MaterialData(Material.AIR))
    }
    sender.testClass("VillagerData", "VillagerDataParser") {
        metadataHandler.getParser(VillagerData(VillagerData.Type.PLAINS, VillagerData.Profession.NONE))
    }
    sender.testClass("TextComponent", "TextComponentParser") {
        metadataHandler.getParser(TextComponent(""))
    }
    sender.testClass("BukkitParticles", "BukkitParticleParser") {
        metadataHandler.getParser(BukkitParticles.FLAME)
    }
    sender.testClass("BukkitPose", "BukkitPoseParser") {
        metadataHandler.getParser(BukkitPose.STANDING)
    }
    // 1.19+
    if (MinecraftVersion.majorLegacy >= 11900) {
        sender.testClass("Cat.Type", "CatVariantParser") {
            metadataHandler.getParser(Cat.Type.TABBY)
        }
        sender.testClass("Frog.Variant", "FrogVariantParser") {
            metadataHandler.getParser(Frog.Variant.TEMPERATE)
        }
        sender.testClass("Art", "PaintingVariantParser") {
            metadataHandler.getParser(Art.KEBAB)
        }
    }
    sender.info("OK")
}