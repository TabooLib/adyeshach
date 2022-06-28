package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.common.api.MinecraftEntityMetadataHandler
import ink.ptms.adyeshach.common.api.MinecraftMeta
import ink.ptms.adyeshach.common.api.MinecraftMetadataParser
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import ink.ptms.adyeshach.impl.nms.parser.*
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Art
import org.bukkit.entity.Cat
import org.bukkit.entity.Frog
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import taboolib.module.nms.MinecraftVersion

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftEntityMetadataHandler
 *
 * @author 坏黑
 * @since 2022/6/28 00:09
 */
class DefaultMinecraftEntityMetadataHandler : MinecraftEntityMetadataHandler {

    val registeredParser = LinkedHashMap<Class<*>, MinecraftMetadataParser<*>>()

    init {
        addParser(Int::class.java, IntParser())
        addParser(Byte::class.java, ByteParser())
        addParser(Float::class.java, FloatParser())
        addParser(String::class.java, StringParser())
        addParser(Boolean::class.java, BooleanParser())
        addParser(Vector::class.java, VectorParser())
        addParser(ItemStack::class.java, ItemStackParser())
        addParser(EulerAngle::class.java, EulerAngleParser())
        addParser(MaterialData::class.java, BlockStateParser())
        addParser(VillagerData::class.java, VillagerDataParser())
        addParser(TextComponent::class.java, TextComponentParser())
        addParser(BukkitParticles::class.java, BukkitParticleParser())
        addParser(BukkitPose::class.java, BukkitPoseParser())
        // 1.19+
        if (MinecraftVersion.majorLegacy >= 11900) {
            addParser(Cat.Type::class.java, CatVariantParser())
            addParser(Frog.Variant::class.java, FrogVariantParser())
            addParser(Art::class.java, PaintingVariantParser())
        }
    }

    override fun addParser(type: Class<*>, metadataParser: MinecraftMetadataParser<*>) {
        registeredParser[type] = metadataParser
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getParser(data: T): MinecraftMetadataParser<T>? {
        registeredParser.entries.forEach { (k, v) ->
            if (k.isAssignableFrom((data as Any).javaClass)) {
                return v as MinecraftMetadataParser<T>
            }
        }
        return null
    }

    override fun getParsers(): List<MinecraftMetadataParser<*>> {
        return registeredParser.values.toList()
    }

    override fun createByteMeta(index: Int, value: Byte): MinecraftMeta {
        TODO("Not yet implemented")
    }

    override fun createIntMeta(index: Int, value: Int): MinecraftMeta {
        TODO("Not yet implemented")
    }

    override fun createFloatMeta(index: Int, value: Float): MinecraftMeta {
        TODO("Not yet implemented")
    }

    override fun createStringMeta(index: Int, value: String): MinecraftMeta {
        TODO("Not yet implemented")
    }

    override fun createComponentMeta(index: Int, text: String): MinecraftMeta {
        TODO("Not yet implemented")
    }

    override fun createOptionalComponentMeta(index: Int, text: String?): MinecraftMeta {
        TODO("Not yet implemented")
    }

    override fun createItemStackMeta(index: Int, itemStack: ItemStack): MinecraftMeta {
        TODO("Not yet implemented")
    }

    override fun createBlockStateMeta(index: Int, blockData: MaterialData?): MinecraftMeta {
        TODO("Not yet implemented")
    }

    override fun createBooleanMeta(index: Int, value: Boolean): MinecraftMeta {
        TODO("Not yet implemented")
    }

    override fun createParticleMeta(index: Int, particle: BukkitParticles): MinecraftMeta {
        TODO("Not yet implemented")
    }

    override fun createRotationsMeta(index: Int, vector: Vector?): MinecraftMeta {
        TODO("Not yet implemented")
    }

    override fun createEulerAngleMetaMeta(index: Int, value: EulerAngle): MinecraftMeta {
        TODO("Not yet implemented")
    }

    override fun createVillagerDataMeta(index: Int, villagerData: VillagerData): MinecraftMeta {
        TODO("Not yet implemented")
    }

    override fun createPoseMeta(index: Int, pose: BukkitPose): MinecraftMeta {
        TODO("Not yet implemented")
    }

    override fun createCatVariantMeta(index: Int, type: Cat.Type): MinecraftMeta {
        TODO("Not yet implemented")
    }

    override fun createFrogVariantMeta(index: Int, type: Frog.Variant): MinecraftMeta {
        TODO("Not yet implemented")
    }

    override fun createPaintingVariantMeta(index: Int, type: Art): MinecraftMeta {
        TODO("Not yet implemented")
    }
}