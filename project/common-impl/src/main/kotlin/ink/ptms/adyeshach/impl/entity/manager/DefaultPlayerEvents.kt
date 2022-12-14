package ink.ptms.adyeshach.impl.entity.manager

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.SpawnTrigger
import ink.ptms.adyeshach.core.event.AdyeshachPlayerJoinEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.module.nms.PacketReceiveEvent
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.manager.DefaultPlayerEvents
 *
 * @author 坏黑
 * @since 2022/8/18 10:41
 */
internal object DefaultPlayerEvents {

    val onlinePlayerSet = CopyOnWriteArraySet<String>()

    /**
     * 进入游戏初始化管理器
     */
    @SubscribeEvent
    fun onJoin(e: PlayerJoinEvent) {
        if (AdyeshachSettings.spawnTrigger == SpawnTrigger.JOIN) {
            // 延迟初始化
            submit(delay = 20) { Adyeshach.api().setupEntityManager(e.player) }
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
        Adyeshach.api().releaseEntityManager(e.player)
    }

    /**
     * 延迟进入检查器
     */
    @SubscribeEvent
    fun onReceive(e: PacketReceiveEvent) {
        if (e.packet.name == "PacketPlayInPosition" && e.player.name !in onlinePlayerSet) {
            onlinePlayerSet += e.player.name
            AdyeshachPlayerJoinEvent(e.player).call()
        }
    }
}