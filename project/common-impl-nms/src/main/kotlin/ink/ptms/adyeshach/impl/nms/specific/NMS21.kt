package ink.ptms.adyeshach.impl.nms.specific

import com.mojang.authlib.properties.PropertyMap
import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftScoreboardOperator
import ink.ptms.adyeshach.core.bukkit.*
import ink.ptms.adyeshach.core.bukkit.data.GameProfile
import ink.ptms.adyeshach.core.bukkit.data.VillagerData
import ink.ptms.adyeshach.impl.nms.NMSEntityPose
import ink.ptms.adyeshach.impl.nms.NMSVillagerProfession
import ink.ptms.adyeshach.impl.nms.NMSVillagerType
import org.bukkit.Art
import org.bukkit.Location
import org.bukkit.material.MaterialData
import taboolib.common.util.unsafeLazy
import taboolib.module.nms.nmsProxy
import java.util.*

interface NMS21 {

    /**
     * 获取玩家数据
     */
    fun getProperties(uuid: UUID, gameProfile: GameProfile): PropertyMap

    /**
     * 创建头颅转向数据包
     */
    fun createEntityHead(entityId: Int, yHeadRot: Byte): Any

    /**
     * 序列化文本为Json
     */
    fun toJson(compound: Any): String

    /**
     * 创建生成实体数据包
     */
    fun createSpawnEntity(entityId: Int, uuid: UUID, location: Location, yaw: Float, pitch: Float, data: Int, entityType: Int, yhead: Double): Any

    /**
     * 创建实体元数据数据包
     */
    fun createEntityMetadata(entityId: Int, packedItems: List<MinecraftMeta>): Any

    /**
     * 创建实体骑乘数据包
     */
    fun createPassengers(entityId: Int, vararg passengers: Int): Any

    /**
     * 获取区块
     */
    fun getChunk(chunk: Any?): Any?

    /**
     * 创建传送数据包
     */
    fun createTeleport(entityId: Int, location: Location, yaw: Byte, pitch: Byte, onGround: Boolean): Any

    /**
     * 创建位置同步数据包
     */
    fun createSyncPosition(entityId: Int, location: Location, onGround: Boolean): Any

    /**
     * 创建队伍数据包
     */
    fun createTeam(team: MinecraftScoreboardOperator.Team, method: MinecraftScoreboardOperator.TeamMethod): Any

    /**
     * 获取实体姿势
     */
    fun getPose(pose: BukkitPose): NMSEntityPose

    /**
     * 获取村民类型
     */
    fun getVillagerType(type: VillagerData.Type): NMSVillagerType

    /**
     * 获取村民职业
     */
    fun getVillagerProfession(profession: VillagerData.Profession): NMSVillagerProfession

    /**
     * 获取画的类型id
     */
    fun getArtType(art: BukkitPaintings): Int

    /**
     * bukkit的Art转为CraftArt
     */
    fun artBukkitToNotch(art: Art): Any

    /**
     * 创建空的方块数据(解析有问题了)
     */
    fun createOptBlockStateMeta(index: Int, material: MaterialData?): Any

    /**
     * 创建鸡变种类型数据
     */
    fun createChickenMeta(index: Int, type: BukkitChickenType): Any

    /**
     * 创建犰狳状态类型数据
     */
    fun createArmadilloMeta(index: Int, value: BukkitArmadilloState): Any

    /**
     * 创建猪变种类型数据
     */
    fun createPigVariantMeta(index: Int, value: BukkitPigVariant): Any

    /**
     * 创建狼变种类型数据
     */
    fun createWolfVariantMeta(index: Int, value: BukkitWolfVariant): Any

    /**
     * 创建猫变种类型数据
     */
    fun createCatVariantMeta(index: Int, value: BukkitCatType): Any

    fun createCopperGolemWeatherState(index: Int, value: BukkitCopperWeatherState): Any

    fun createCopperGolemStatuePose(index: Int, value: BukkitCopperGolemStatuePose): Any

    fun createCowVariant(index: Int, value: BukkitCowVariant): Any

    companion object {
        val instance by unsafeLazy { nmsProxy<NMS21>() }
    }
}