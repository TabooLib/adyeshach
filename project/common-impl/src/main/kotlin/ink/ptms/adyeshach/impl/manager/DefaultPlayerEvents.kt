package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.SpawnTrigger
import ink.ptms.adyeshach.core.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.core.event.AdyeshachPlayerJoinEvent
import ink.ptms.adyeshach.core.util.safeDistance
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import ink.ptms.adyeshach.impl.DefaultAdyeshachBooster
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.util.Vector
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.PacketReceiveEvent
import taboolib.platform.util.bukkitPlugin
import taboolib.platform.util.onlinePlayers
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.manager.DefaultPlayerEvents
 *
 * @author 坏黑
 * @since 2022/8/18 10:41
 */
internal object DefaultPlayerEvents {

    val onlinePlayerSet = CopyOnWriteArraySet<String>()

    @Awake(LifeCycle.ACTIVE)
    fun onActive() {
        // 释放玩家的数据包缓冲区
        val packetHandler = Adyeshach.api().getMinecraftAPI().getPacketHandler()
        Bukkit.getScheduler().runTaskTimerAsynchronously(bukkitPlugin, Runnable {
            onlinePlayers.forEach {
                packetHandler.flush(it)
            }
        }, 1, 1)
    }

    /**
     * 进入游戏初始化管理器
     */
    @SubscribeEvent
    fun onJoin(e: PlayerJoinEvent) {
        if (AdyeshachSettings.spawnTrigger == SpawnTrigger.JOIN) {
            // 延迟初始化
            submit(delay = AdyeshachSettings.spawnDelay.toLong()) {
                Adyeshach.api().setupEntityManager(e.player)
            }
        }
    }

    /**
     * 进入游戏初始化管理器（延迟）
     */
    @SubscribeEvent
    fun onLateJoin(e: AdyeshachPlayerJoinEvent) {
        if (AdyeshachSettings.spawnTrigger == SpawnTrigger.KEEP_ALIVE) {
            Adyeshach.api().setupEntityManager(e.player)
        }
    }

    /**
     * 离开游戏释放管理器
     */
    @SubscribeEvent
    fun onQuit(e: PlayerQuitEvent) {
        onlinePlayerSet -= e.player.name
        Adyeshach.api().getMinecraftAPI().getPacketHandler().cleanup(e.player)
        Adyeshach.api().releaseEntityManager(e.player)
    }

    /**
     * 传送时更新管理器
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onTeleport(e: PlayerTeleportEvent) {
        if (e.from.world == e.to.world && e.from.distance(e.to) > AdyeshachSettings.visibleDistance) {
            // 下一主线程 tick 再 checkVisible，避免与传送当帧状态竞态
            submit(delay = 1) { syncVisibleAfterTeleport(e.player) }
        }
    }

    /**
     * 切换世界时更新管理器
     */
    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onTeleport(e: PlayerChangedWorldEvent) {
        // 下一主线程 tick 再 checkVisible，避免与传送当帧状态竞态
        submit(delay = 1) { syncVisibleAfterTeleport(e.player) }
    }

    /**
     * 玩家完成传送后按当前位置重新收敛管理器可见状态
     *
     * @param player 已完成传送的玩家
     */
    fun syncVisibleAfterTeleport(player: Player) {
        DefaultAdyeshachBooster.api.localPublicEntityManager.checkVisible(player)
        DefaultAdyeshachBooster.api.localPublicEntityManagerTemporary.checkVisible(player)
        DefaultAdyeshachAPI.playerEntityTemporaryManagerMap[player]?.checkVisible(player)
    }

    /**
     * 延迟进入检查器
     * 交互判断
     */
    @SubscribeEvent
    fun onReceive(e: PacketReceiveEvent) {
        val packet = e.packet
        val player = e.player
        if (packet.name in listOf("PacketPlayInPosition", "Pos") && player.name !in onlinePlayerSet) {
            onlinePlayerSet += player.name
            AdyeshachPlayerJoinEvent(player).call()
        }
        if (packet.name in listOf("PacketPlayInUseEntity", "ServerboundInteractPacket")) {
            val id = packet.read<Int>(if (MinecraftVersion.versionId < 12005) "a" else "entityId") ?: return
            val entity = Adyeshach.api().getEntityFinder().getEntityFromEntityId(id, player) ?: return
            // 判定观察者并检测作弊
            if (entity.isViewer(player) && entity.getLocation().safeDistance(player.location) < 10) {
                if (MinecraftVersion.isUniversal) {
                    // 1.21+的字段变为c了,太操蛋了
                    // nm的缓存傻逼玩意,换了就必须清缓存
                    val action = if (MinecraftVersion.versionId >= 12005) {
                        packet.read<Any>("action")
                    } else {
                        packet.read("b")
                    }!!

                    val actionOrdinal = (action.invokeMethod<Any>("getType") as Enum<*>).ordinal

                    when (actionOrdinal) {
                        // 冒险与生存模式可能只发送不携带命中位置的普通 INTERACT
                        0 -> {
                            val hand = action.getProperty<Any>("hand").toString() == "MAIN_HAND"
                            submit { AdyeshachEntityInteractEvent(entity, player, hand, Vector(0, 0, 0)).call() }
                        }
                        // 左键
                        1 -> {
                            submit { AdyeshachEntityDamageEvent(entity, player).call() }
                        }
                        // 右键
                        2 -> {
                            val location = kotlin.runCatching { action.getProperty<Any>("location") }.getOrNull()!!
                            val vector = Adyeshach.api().getMinecraftAPI().getHelper().vec3dToVector(location)
                            val hand = action.getProperty<Any>("hand").toString() == "MAIN_HAND"
                            submit { AdyeshachEntityInteractEvent(entity, player, hand, vector).call() }
                        }
                    }
                } else {
                    // 低版本 EnumEntityUseAction 为枚举类型
                    // 通过字符串判断点击方式
                    when (packet.read<Any>("action")!!.toString()) {
                        "ATTACK" -> {
                            submit { AdyeshachEntityDamageEvent(entity, player).call() }
                        }

                        "INTERACT_AT" -> {
                            val location = packet.read<Any>("c")
                            val vector = location?.let { Adyeshach.api().getMinecraftAPI().getHelper().vec3dToVector(it) } ?: Vector(0, 0, 0)
                            val hand = packet.read<Any>("d").toString() == "MAIN_HAND"
                            submit { AdyeshachEntityInteractEvent(entity, player, hand, vector).call() }
                        }
                    }
                }
            }
        }
    }
}
