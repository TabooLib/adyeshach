package ink.ptms.adyeshach.impl.description

import com.eatthepath.uuid.FastUUID
import ink.ptms.adyeshach.core.bukkit.BukkitParticles
import ink.ptms.adyeshach.core.bukkit.BukkitPose
import ink.ptms.adyeshach.core.bukkit.data.VillagerData
import ink.ptms.adyeshach.core.entity.type.AdySniffer
import ink.ptms.adyeshach.core.util.getEnumOrNull
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Art
import org.bukkit.Material
import org.bukkit.entity.Cat
import org.bukkit.entity.Frog
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import taboolib.common5.Quat
import taboolib.common5.cdouble
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.MinecraftVersion

/**
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
class PrepareMetaTypeCustom(val type: CustomType) : PrepareMetaType {

    val majorLegacy = MinecraftVersion.majorLegacy

    /**
     * 识别自定义类型的参数
     * 对于支持定义默认值的参数，将从 args 中读取，反之自定默认值
     *
     * 例如：
     * !ItemStack
     * !ItemStack ENDER_EYE
     * !Cat.Type
     */
    override fun parse(name: String, args: List<String>): PrepareMeta {
        return when (type) {
            CustomType.UUID -> PrepareMetaNatural(name, FastUUID.parseUUID(args[1]), "UUID")
            CustomType.PARTICLE -> PrepareMetaNatural(name, parseParticle(args), "Particle")
            CustomType.EULER_ANGLE -> PrepareMetaNatural(name, parseEulerAngle(args), "EulerAngle")
            CustomType.ITEM_STACK -> PrepareMetaNatural(name, parseItemStack(args), "ItemStack")
            CustomType.OPT_BLOCK_POS -> PrepareMetaNatural(name, parseVector(args), "OptBlockPos")
            CustomType.OPT_BLOCK_ID -> PrepareMetaNatural(name, MaterialData(Material.AIR), "OptBlockID")
            CustomType.BLOCK_ID -> PrepareMetaNatural(name, MaterialData(Material.AIR), "BlockID")
            CustomType.OPT_CHAT -> PrepareMetaNatural(name, TextComponent(""), "OptChat")
            CustomType.CHAT -> PrepareMetaNatural(name, TextComponent(""), "Chat")
            CustomType.VILLAGER_DATA -> PrepareMetaNatural(name, VillagerData(), "VillagerData")
            CustomType.BUKKIT_POSE -> PrepareMetaNatural(name, BukkitPose.STANDING, "BukkitPose")
            CustomType.VECTOR3 -> PrepareMetaNatural(name, parseVector(args), "Vector3")
            CustomType.QUATERNION -> PrepareMetaNatural(name, parseQuat(args), "Quaternion")
            // 特殊版本约定
            CustomType.PAINTING -> PrepareMetaNatural(name, Art.KEBAB, "Art")
            CustomType.CAT_TYPE -> PrepareMetaNatural(name, Cat.Type.TABBY, "Cat.Type")
            CustomType.FROG_VARIANT -> PrepareMetaNatural(name, Frog.Variant.TEMPERATE, "Frog.Variant")
            // 1.20 新增
            CustomType.SNIFFER_STATE -> PrepareMetaNatural(name, AdySniffer.State.IDLING, "SnifferState")
        }
    }

    private fun parseQuat(args: List<String>) =
        Quat(args.getOrNull(1).cdouble, args.getOrNull(2).cdouble, args.getOrNull(3).cdouble, args.getOrNull(4).cdouble)

    private fun parseVector(args: List<String>) =
        Vector(args.getOrNull(1).cdouble, args.getOrNull(2).cdouble, args.getOrNull(3).cdouble)

    private fun parseItemStack(args: List<String>) =
        XMaterial.matchXMaterial(args.getOrElse(1) { "AIR" }).orElse(XMaterial.AIR).parseItem() ?: ItemStack(Material.AIR)

    private fun parseEulerAngle(args: List<String>) =
        EulerAngle(args.getOrNull(1).cdouble, args.getOrNull(2).cdouble, args.getOrNull(3).cdouble)

    private fun parseParticle(args: List<String>) =
        BukkitParticles::class.java.getEnumOrNull(args[1]) ?: BukkitParticles.HAPPY_VILLAGER
}