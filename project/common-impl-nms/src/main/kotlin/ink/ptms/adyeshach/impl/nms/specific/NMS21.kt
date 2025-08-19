package ink.ptms.adyeshach.impl.nms.specific

import ink.ptms.adyeshach.core.MinecraftMeta
import ink.ptms.adyeshach.core.MinecraftScoreboardOperator
import ink.ptms.adyeshach.core.bukkit.BukkitPose
import ink.ptms.adyeshach.impl.nms.NMSEntityPose
import org.bukkit.Location
import taboolib.common.util.unsafeLazy
import taboolib.module.nms.nmsProxy
import java.util.*

interface NMS21 {

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
    fun createSyncPosition(entityId: Int, location: Location, onGround:Boolean): Any

    /**
     * 创建队伍数据包
     */
    fun createTeam(team: MinecraftScoreboardOperator.Team, method: MinecraftScoreboardOperator.TeamMethod): Any

    /**
     * 获取实体姿势
     */
    fun getPose(pose: BukkitPose): NMSEntityPose

    companion object {
        val instance by unsafeLazy { nmsProxy<NMS21>() }
    }
}