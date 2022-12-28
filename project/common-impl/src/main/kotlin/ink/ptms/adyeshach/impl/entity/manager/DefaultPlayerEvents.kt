package ink.ptms.adyeshach.impl.entity.manager

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.SpawnTrigger
import ink.ptms.adyeshach.core.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.core.event.AdyeshachPlayerJoinEvent
import ink.ptms.adyeshach.core.util.safeDistance
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.util.Vector
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.module.nms.MinecraftVersion
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
     * 交互判断
     */
    @SubscribeEvent
    fun onReceive(e: PacketReceiveEvent) {
        if (e.packet.name == "PacketPlayInPosition" && e.player.name !in onlinePlayerSet) {
            onlinePlayerSet += e.player.name
            AdyeshachPlayerJoinEvent(e.player).call()
        }
        if (e.packet.name == "PacketPlayInUseEntity") {
            val entity = Adyeshach.api().getEntityFinder().getEntityFromEntityId(e.packet.read("a")!!, e.player) ?: return
            // 判定观察者并检测作弊
            if (entity.isViewer(e.player) && entity.getLocation().safeDistance(e.player.location) < 10) {
                if (MinecraftVersion.isUniversal) {
                    val action = e.packet.source.getProperty<Any>("b", remap = false)!!
                    // 高版本 EnumEntityUseAction 不再是枚举类型
                    // 通过类名判断点击方式
                    when (action.javaClass.simpleName) {
                        // 左键
                        "d" -> {
                            submit { AdyeshachEntityDamageEvent(entity, e.player).call() }
                        }
                        // 右键
                        "e" -> {
                            val location = action.getProperty<Any>("b", remap = false)
                            val vector = location?.let { Adyeshach.api().getMinecraftAPI().getHelper().vec3dToVector(it) } ?: Vector(0, 0, 0)
                            val hand = action.getProperty<Any>("a", remap = false).toString() == "MAIN_HAND"
                            submit { AdyeshachEntityInteractEvent(entity, e.player, hand, vector).call() }
                        }
                    }
                } else {
                    // 低版本 EnumEntityUseAction 为枚举类型
                    // 通过字符串判断点击方式
                    when (e.packet.source.getProperty<Any>("action")!!.toString()) {
                        "ATTACK" -> {
                            submit { AdyeshachEntityDamageEvent(entity, e.player).call() }
                        }
                        "INTERACT_AT" -> {
                            val location = e.packet.read<Any>("c")
                            val vector = location?.let { Adyeshach.api().getMinecraftAPI().getHelper().vec3dToVector(it) } ?: Vector(0, 0, 0)
                            val hand = e.packet.read<Any>("d").toString() == "MAIN_HAND"
                            submit { AdyeshachEntityInteractEvent(entity, e.player, hand, vector).call() }
                        }
                    }
                }
            }
        }
    }
}