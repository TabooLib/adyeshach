package ink.ptms.adyeshach.impl.description

import ink.ptms.adyeshach.core.bukkit.BukkitParticles
import ink.ptms.adyeshach.core.bukkit.BukkitPose
import ink.ptms.adyeshach.core.bukkit.data.VillagerData
import ink.ptms.adyeshach.core.util.getEnumOrNull
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Art
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import taboolib.library.xseries.XMaterial

/**
 * @author 坏黑
 * @since 2022/6/19 18:07
 */
class PrepareMetaTypeCustom(val type: CustomType) : PrepareMetaType {

    override fun parse(name: String, args: List<String>): PrepareMeta {
        return when (type) {
            CustomType.PARTICLE -> PrepareMetaNatural(
                name,
                BukkitParticles::class.java.getEnumOrNull(args[1]) ?: BukkitParticles.HAPPY_VILLAGER
            )
            CustomType.EULER_ANGLE -> PrepareMetaNatural(
                name,
                EulerAngle(args.getOrElse(1) { "0" }.toDouble(), args.getOrElse(2) { "0" }.toDouble(), args.getOrElse(3) { "0" }.toDouble())
            )
            CustomType.VECTOR -> PrepareMetaNatural(
                name,
                Vector(args.getOrElse(1) { "0" }.toDouble(), args.getOrElse(2) { "0" }.toDouble(), args.getOrElse(3) { "0" }.toDouble())
            )
            CustomType.ITEM_STACK -> PrepareMetaNatural(
                name,
                XMaterial.matchXMaterial(args.getOrElse(1) { "AIR" }).orElse(XMaterial.AIR).parseItem() ?: ItemStack(Material.AIR)
            )
            CustomType.MATERIAL -> PrepareMetaNatural(
                name,
                XMaterial.matchXMaterial(args.getOrElse(1) { "AIR" }).orElse(XMaterial.AIR).parseMaterial() ?: Material.AIR
            )
            CustomType.PAINTING -> PrepareMetaNatural(name, Art.KEBAB)
            CustomType.SHEEP_COLOR -> PrepareMetaNatural(name, 0)
            CustomType.VILLAGER_DATA -> PrepareMetaNatural(name, VillagerData(VillagerData.Type.PLAINS, VillagerData.Profession.NONE))
            CustomType.TEXT_COMPONENT -> PrepareMetaNatural(name, TextComponent(""))
            CustomType.BUKKIT_POSE -> PrepareMetaNatural(name, BukkitPose.STANDING)
        }
    }
}