package ink.ptms.adyeshach.core

import ink.ptms.adyeshach.core.bukkit.BukkitParticles
import ink.ptms.adyeshach.core.bukkit.BukkitPose
import ink.ptms.adyeshach.core.bukkit.data.VillagerData
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import java.util.UUID

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.MinecraftEntityMetadataHandler
 *
 * @author 坏黑
 * @since 2022/6/15 18:09
 */
interface MinecraftEntityMetadataHandler {

    /**
     * 注册元数据鉴别器
     */
    fun addParser(type: Class<*>, metadataParser: MinecraftMetadataParser<*>)

    /**
     * 获取对应元数据鉴别器
     */
    fun <T> getParser(data: T): MinecraftMetadataParser<T>?

    /**
     * 获取所有元数据鉴别器
     */
    fun getParsers(): List<MinecraftMetadataParser<*>>

    /**
     * 生成 Byte 类型元数据，对应 BYTE 字段
     */
    fun createByteMeta(index: Int, value: Byte): MinecraftMeta

    /**
     * 生成 Int 类型的元数据，对应 INT 字段
     */
    fun createIntMeta(index: Int, value: Int): MinecraftMeta

    /**
     * 生成 Float 类型的元数据，对应 FLOAT 字段
     */
    fun createFloatMeta(index: Int, value: Float): MinecraftMeta

    /**
     * 生成 String 类型的元数据，对应 STRING 字段
     */
    fun createStringMeta(index: Int, value: String): MinecraftMeta

    /**
     * 生成 Optional<IChatBaseComponent\> 类型的元数据，对应 OPTIONAL_COMPONENT 字段。
     */
    fun createOptionalComponentMeta(index: Int, text: String?): MinecraftMeta

    /**
     * 生成 ItemStack 类型的元数据，对应 ITEM_STACK 字段。
     */
    fun createItemStackMeta(index: Int, itemStack: ItemStack): MinecraftMeta

    /**
     * 生成 Optional<IBlockData> 类型的元数据，对应 BLOCK_DATA 字段
     */
    fun createBlockStateMeta(index: Int, blockData: MaterialData?): MinecraftMeta
    
    /**
     * 生成 Boolean 类型的元数据
     */
    fun createBooleanMeta(index: Int, value: Boolean): MinecraftMeta

    /**
     * 生成 ParticleParam 类型的元数据，对应 PARTICLE 字段
     *
     * 1.13+
     */
    fun createParticleMeta(index: Int, particle: BukkitParticles): MinecraftMeta

    /**
     * 生成 Vector3f 类型的元数据，对应 OPTIONAL_BLOCK_POS 字段
     */
    fun createBlockPosMeta(index: Int, vector: Vector?): MinecraftMeta

    /**
     * 生成 Vector3f 类型的元数据，对应 ROTATIONS 字段
     */
    fun createEulerAngleMeta(index: Int, value: EulerAngle): MinecraftMeta

    /**
     * 生成 UUID 类型的元数据，对应 UUID 字段
     */
    fun createUUIDMeta(index: Int, value: UUID): MinecraftMeta

    /**
     * 生成 VillagerData 类型的元数据，对应 VILLAGER_DATA 字段
     *
     * 1.14+
     */
    fun createVillagerDataMeta(index: Int, villagerData: VillagerData): MinecraftMeta

    /**
     * 生成 EntityPose 类型的元数据，对应 POSE 字段
     *
     * 1.14+
     */
    fun createPoseMeta(index: Int, pose: BukkitPose): MinecraftMeta

    /**
     * 生成 CatVariant 类型的元数据，对应 CAT_VARIANT 字段
     *
     * 1.19+
     */
    fun createCatVariantMeta(index: Int, type: Any): MinecraftMeta

    /**
     * 生成 FrogVariant 类型的元数据，对应 FROG_VARIANT 字段
     *
     * 1.19+
     */
    fun createFrogVariantMeta(index: Int, type: Any): MinecraftMeta

    /**
     * 生成 Holder<PaintingVariant> 类型的元数据，对应 PAINTING_VARIANT 字段
     *
     * 1.19+
     */
    fun createPaintingVariantMeta(index: Int, type: Any): MinecraftMeta
}