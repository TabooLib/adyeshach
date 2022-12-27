package ink.ptms.adyeshach.impl.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.bukkit.BukkitAnimation
import ink.ptms.adyeshach.core.bukkit.BukkitPose
import ink.ptms.adyeshach.core.bukkit.data.GameProfile
import ink.ptms.adyeshach.core.bukkit.data.PingBar
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyHuman
import ink.ptms.adyeshach.core.entity.type.minecraftVersion
import ink.ptms.adyeshach.core.event.AdyeshachGameProfileGenerateEvent
import ink.ptms.adyeshach.core.util.getEnum
import ink.ptms.adyeshach.impl.util.ifTrue
import org.bukkit.entity.Player
import taboolib.common.platform.Schedule
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.common5.cbool
import taboolib.common5.cint
import taboolib.module.chat.colored
import taboolib.platform.util.onlinePlayers
import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultHuman
 *
 * @author 坏黑
 * @since 2022/6/29 19:05
 */
abstract class DefaultHuman(entityTypes: EntityTypes) : DefaultEntityLiving(entityTypes), AdyHuman {

    /** 玩家 UUID */
    internal val pid: UUID
        get() = normalizeUniqueId

    /** 是否已经生成 */
    internal var spawned = false

    /** 玩家信息 */
    @Expose
    internal val gameProfile = GameProfile()

    /** 是否睡眠 */
    @Expose
    internal var isSleepingLegacy = false

    /** 是否从玩家列表中移除 */
    @Expose
    override var isHideFromTabList = true
        set(value) {
            if (value) {
                forViewers { removePlayerInfo(it) }
            } else {
                forViewers { addPlayerInfo(it) }
            }
            field = value
        }

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        return if (visible) {
            prepareSpawn(viewer) {
                // 创建玩家信息
                addPlayerInfo(viewer)
                // 创建客户端对应表
                registerClientEntity(viewer)
                // 生成实体
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnNamedEntity(viewer, index, pid, position.toLocation())
                // 启用皮肤
                setSkinEnabled(true)
                // 修复玩家类型视角和装备无法正常显示的问题
                submit(delay = 1) {
                    setHeadRotation(yaw, pitch, forceUpdate = true)
                    updateEquipment()
                }
                // 更新状态
                submit(delay = 5) {
                    if (isDie) {
                        die(viewer = viewer)
                    }
                    if (isSleepingLegacy) {
                        setSleeping(true)
                    }
                    if (isHideFromTabList) {
                        removePlayerInfo(viewer)
                    }
                }
                spawned = true
            }
        } else {
            prepareDestroy(viewer) {
                // 移除玩家信息
                removePlayerInfo(viewer)
                // 销毁实体
                Adyeshach.api().getMinecraftAPI().getEntityOperator().destroyEntity(viewer, index)
                // 移除客户端对应表
                unregisterClientEntity(viewer)
            }
        }
    }

    @Suppress("SpellCheckingInspection")
    override fun setCustomMeta(key: String, value: String): Boolean {
        super.setCustomMeta(key, value).ifTrue { return true }
        return when (key) {
            "hidefromtablist", "hide_from_tab_list", "is_hide_from_tab_list" -> {
                isHideFromTabList = value.cbool
                true
            }
            "name", "player_name" -> {
                setName(value)
                true
            }
            "ping", "player_ping" -> {
                setPing(value.cint)
                true
            }
            "ping_bar", "playe_ping_bar" -> {
                setPingBar(PingBar::class.java.getEnum(value))
                true
            }
            "texture", "player_texture" -> {
                setTexture(value)
                true
            }
            "sleeping", "is_sleeping" -> {
                setSleeping(value.cbool)
                true
            }
            else -> false
        }
    }

    override fun setName(name: String) {
        gameProfile.name = name.colored()
        refreshPlayer()
    }

    override fun getName(): String {
        return gameProfile.name
    }

    override fun setPing(ping: Int) {
        gameProfile.ping = ping
        refreshPlayer(false)
    }

    override fun setPingBar(pingBar: PingBar) {
        gameProfile.setPingBar(pingBar)
        refreshPlayer(false)
    }

    override fun getPing(): Int {
        return gameProfile.ping
    }

    override fun setTexture(name: String) {
        gameProfile.textureName = name
        // 加载皮肤
        Adyeshach.api().getNetworkAPI().getSkin().getTexture(name).thenAccept { setTexture(it.value(), it.signature()) }
    }

    override fun setTexture(texture: String, signature: String) {
        gameProfile.texture = arrayOf(texture, signature)
        refreshPlayer()
    }

    override fun getTexture(): Array<String> {
        return gameProfile.texture
    }

    override fun getTextureName(): String {
        return gameProfile.textureName
    }

    override fun resetTexture() {
        gameProfile.texture = arrayOf("")
        refreshPlayer()
    }

    override fun setSleeping(value: Boolean) {
        if (value) {
            if (minecraftVersion >= 11400) {
                setPose(BukkitPose.SLEEPING)
            } else {
                // 1.13.2 以下版本无法使用 setPose 设置睡眠状态
                Adyeshach.api().getMinecraftAPI().getEntityOperator().updatePlayerSleeping(getVisiblePlayers(), index, position.toLocation())
            }
        } else {
            if (minecraftVersion >= 11400) {
                setPose(BukkitPose.STANDING)
            } else {
                sendAnimation(BukkitAnimation.LEAVE_BED)
            }
            teleport(position)
        }
        isSleepingLegacy = value
    }

    override fun isSleeping(): Boolean {
        return if (minecraftVersion >= 11400) {
            getPose() == BukkitPose.SLEEPING
        } else {
            isSleepingLegacy
        }
    }

    override fun refreshPlayerInfo(viewer: Player) {
        removePlayerInfo(viewer)
        addPlayerInfo(viewer)
        // 短暂延迟后删除玩家信息
        submit(delay = 5) {
            if (isHideFromTabList) {
                removePlayerInfo(viewer)
            }
        }
    }

    protected fun refreshPlayer(respawn: Boolean = true) {
        if (spawned) {
            forViewers { refreshPlayerInfo(it) }
            // 是否刷新实体
            if (respawn) {
                despawn()
                respawn()
            }
        }
    }

    protected fun addPlayerInfo(viewer: Player) {
        val event = AdyeshachGameProfileGenerateEvent(this, viewer, gameProfile.clone())
        event.call()
        val handler = Adyeshach.api().getMinecraftAPI().getEntityPlayerHandler()
        handler.addPlayerInfo(viewer, pid, event.gameProfile)
    }

    protected fun removePlayerInfo(viewer: Player) {
        Adyeshach.api().getMinecraftAPI().getEntityPlayerHandler().removePlayerInfo(viewer, pid)
    }

    companion object {

        @Schedule(delay = 200, period = 200)
        internal fun playerTextureRefresh200() {
            val finder = Adyeshach.api().getEntityFinder()
            var i = 0L
            onlinePlayers.forEach {
                submitAsync(delay = i++) {
                    finder.getVisibleEntities(it).filterIsInstance<AdyHuman>().forEach { human ->
                        human.refreshPlayerInfo(it)
                    }
                }
            }
        }
    }
}