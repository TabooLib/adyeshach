package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachParallelTask
import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.util.safeDistance
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI
import ink.ptms.adyeshach.impl.DefaultAdyeshachBooster
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.function.throttle
import taboolib.common.io.newFile
import taboolib.common.platform.Awake
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.common.platform.function.warning
import taboolib.common.util.t
import taboolib.platform.bukkit.parallel
import taboolib.platform.util.onlinePlayers
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.manager.DefaultManagerHandler
 *
 * @author 坏黑
 * @since 2022/8/18 10:51
 */
@OptIn(ExperimentalTime::class)
object DefaultManagerHandler {

    // MANAGER_INIT 的依赖项
    var dependOn = listOf(AdyeshachParallelTask.GENERATE_ENTITY_CLASS)

    // 当前游戏刻的玩家列表
    var playersInGameTick: Collection<Player> = listOf()

    // 是否首次触发（通常视为预热）
    var isFirstReport = true

    // 实体卡顿报告
    val entityReport = throttle<Player, Duration>(5000) { player, time ->
        warning(
            """
                处理玩家 ${player.name} 的私有实体管理器用时 $time!
                Processing the private entity manager for player ${player.name} took $time!
            """.t()
        )
        warning(
            """
                详细信息已输出到 dump/${player.name}.log 文件中, 请注意查看!
                Detailed information has been output to the dump/${player.name}.log file, please check it out!
            """.t()
        )
        dump(player)
    }

    fun startup() {
        // 公共管理器
        DefaultAdyeshachBooster.api.localPublicEntityManager.onEnable()
        // 私有管理器
        onlinePlayers.forEach { Adyeshach.api().setupEntityManager(it) }
        // 可见性更新
        submitAsync(period = AdyeshachSettings.visibleRefreshInterval.toLong()) {
            playersInGameTick = Bukkit.getOnlinePlayers().filter { it.hasMetadata("adyeshach_setup") }
            // 公共管理器
            DefaultAdyeshachBooster.api.localPublicEntityManager.checkVisible()
            DefaultAdyeshachBooster.api.localPublicEntityManagerTemporary.checkVisible()
            // 私有管理器
            playersInGameTick.forEach { player ->
                DefaultAdyeshachAPI.playerEntityTemporaryManagerMap[player]?.checkVisible()
            }
        }
        // Tick
        submit(period = 1) {
            // 公共管理器
            DefaultAdyeshachBooster.api.localPublicEntityManager.onTick()
            DefaultAdyeshachBooster.api.localPublicEntityManagerTemporary.onTick()
            // 私有管理器
            DefaultAdyeshachAPI.playerEntityTemporaryManagerMap.values().forEach { manager ->
                val time = measureTime { manager.onTick() }
                // 如果处理这个玩家的时间超过 50ms 则在后台进行报告，报告周期为 5 秒 1 次。
                if (time > 50.milliseconds) {
                    if (isFirstReport) {
                        isFirstReport = false
                    } else {
                        entityReport(manager.owner, time)
                    }
                }
            }
        }
        // 自动保存
        submitAsync(period = 1200, delay = 1200) {
            // 公共管理器
            DefaultAdyeshachBooster.api.localPublicEntityManager.onSave()
        }
    }

    @Awake(LifeCycle.ENABLE)
    private fun onEnable() {
        parallel(AdyeshachParallelTask.MANAGER_INIT, dependOn = dependOn, runOn = LifeCycle.ACTIVE) {
            try {
                startup()
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }
    }

    @Awake(LifeCycle.DISABLE)
    private fun onDisable() {
        // 公共管理器
        DefaultAdyeshachBooster.api.localPublicEntityManagerTemporary.onDisable()
        DefaultAdyeshachBooster.api.localPublicEntityManager.onDisable()
        DefaultAdyeshachBooster.api.localPublicEntityManager.onSave()
        // 私有管理器
        onlinePlayers.forEach { Adyeshach.api().releaseEntityManager(it, false) }
    }

    fun dump(player: Player) {
        val manager = DefaultAdyeshachAPI.playerEntityTemporaryManagerMap.get(player)!!
        dump(manager, "player_${player.name}")
    }

    fun dumpPublic() {
        dump(DefaultAdyeshachBooster.api.localPublicEntityManager as DefaultManager, "public")
        dump(DefaultAdyeshachBooster.api.localPublicEntityManagerTemporary, "public_temporary")
    }

    fun dump(manager: DefaultManager, name: String) {
        val activeEntity = manager.activeEntity.sortedBy { it.id }

        // 基础统计
        val totalCount = activeEntity.size
        val visibleCount = activeEntity.count { it.viewPlayers.hasVisiblePlayer() }
        val nitwitCount = activeEntity.count { it.isNitwit }
        val hasControllerCount = activeEntity.count { (it as DefaultEntityInstance).controller.isNotEmpty() }
        val passengers = activeEntity.sumOf { (it as DefaultEntityInstance).passengers.size }
        val tickableCount = manager.tickableEntities.size

        // 性能优化开关统计（统计非默认值的数量）
        val noVisEvent = activeEntity.count { (it as DefaultEntityInstance).isDisableVisibleEvent }
        val noVehCheck = activeEntity.count { (it as DefaultEntityInstance).isDisableVehicleCheckOnTick }
        val noVehSync = activeEntity.count { (it as DefaultEntityInstance).isDisableVehicleRotationSync }
        val noRotFix = activeEntity.count { !(it as DefaultEntityInstance).isRotationFixOnSpawn }
        val noPassRefresh = activeEntity.count { !(it as DefaultEntityInstance).isPassengerRefreshOnSpawn }
        val usePosInterval = activeEntity.count { !(it as DefaultEntityInstance).isIgnoredClientPositionUpdateInterval }

        // 按实体类型分组统计
        val typeStats = activeEntity.groupingBy { it.entityType }.eachCount().entries
            .sortedByDescending { it.value }
            .take(10)

        warning("-- Manager Details ($name) --")
        warning("   Total: $totalCount | Visible: $visibleCount | Tickable: $tickableCount")
        warning("   Nitwit: $nitwitCount | With Controller: $hasControllerCount | Passengers: $passengers")
        warning("   Perf flags: noVisEvent=$noVisEvent, noVehCheck=$noVehCheck, noVehSync=$noVehSync, noRotFix=$noRotFix, noPassRefresh=$noPassRefresh, usePosInterval=$usePosInterval")
        warning("   Top types: ${typeStats.joinToString { "${it.key}=${it.value}" }}")
        warning("-- End of Manager Details --")

        // 输出到文件
        submitAsync {
            newFile(getDataFolder().resolve("dump/$name.log")).writeText(
                buildString {
                    append("================== Manager Dump ==================\n")
                    append("Manager: $name\n")
                    append("Time: ${java.time.LocalDateTime.now()}\n\n")

                    append("=== Statistics ===\n")
                    append("  Total entities: $totalCount\n")
                    append("  Visible entities: $visibleCount\n")
                    append("  Tickable entities: $tickableCount\n")
                    append("  Nitwit entities: $nitwitCount (${if (totalCount > 0) nitwitCount * 100 / totalCount else 0}%)\n")
                    append("  With controller: $hasControllerCount\n")
                    append("  Total passengers: $passengers\n\n")

                    append("=== Performance Flags (non-default values) ===\n")
                    append("  isDisableVisibleEvent=true: $noVisEvent\n")
                    append("  isDisableVehicleCheckOnTick=true: $noVehCheck\n")
                    append("  isDisableVehicleRotationSync=true: $noVehSync\n")
                    append("  isRotationFixOnSpawn=false: $noRotFix\n")
                    append("  isPassengerRefreshOnSpawn=false: $noPassRefresh\n")
                    append("  isIgnoredClientPositionUpdateInterval=false: $usePosInterval\n\n")

                    append("=== Entity Type Distribution ===\n")
                    activeEntity.groupingBy { it.entityType }.eachCount().entries
                        .sortedByDescending { it.value }
                        .forEach { append("  ${it.key}: ${it.value}\n") }
                    append("\n")

                    append("=== Tickable Entities ===\n")
                    val ownerLocation = (manager as? DefaultPlayerManager)?.owner?.location
                    val tickableSorted = if (ownerLocation != null) {
                        manager.tickableEntities.sortedBy { it.getLocation().safeDistance(ownerLocation) }
                    } else {
                        manager.tickableEntities.sortedBy { it.id }
                    }
                    tickableSorted.forEach {
                        it as DefaultEntityInstance
                        val distStr = if (ownerLocation != null) ", Distance: ${it.getLocation().safeDistance(ownerLocation)}" else ""
                        append("  [${it.entityType}] ${it.id}\n")
                        append("    - UniqueId: ${it.uniqueId}\n")
                        append("    - Location: ${it.getLocation()}$distStr\n")
                        append("    - Nitwit: ${it.isNitwit}, Controllers: ${it.controller.size}\n")
                    }
                    append("\n")

                    append("=== Visible Entities (hasVisiblePlayer) ===\n")
                    val visibleSorted = if (ownerLocation != null) {
                        activeEntity.filter { it.viewPlayers.hasVisiblePlayer() }.sortedBy { it.getLocation().safeDistance(ownerLocation) }
                    } else {
                        activeEntity.filter { it.viewPlayers.hasVisiblePlayer() }.sortedBy { it.id }
                    }
                    visibleSorted.forEach {
                        it as DefaultEntityInstance
                        val distStr = if (ownerLocation != null) ", Distance: ${it.getLocation().safeDistance(ownerLocation)}" else ""
                        append("  [${it.entityType}] ${it.id}\n")
                        append("    - UniqueId: ${it.uniqueId}\n")
                        append("    - Location: ${it.getLocation()}$distStr\n")
                        append("    - Nitwit: ${it.isNitwit}, Controllers: ${it.controller.size}\n")
                    }
                    append("\n")

                    append("=== All Entities ===\n")
                    val allSorted = if (ownerLocation != null) {
                        activeEntity.sortedBy { it.getLocation().safeDistance(ownerLocation) }
                    } else {
                        activeEntity.sortedBy { it.id }
                    }
                    allSorted.forEach {
                        it as DefaultEntityInstance
                        val distStr = if (ownerLocation != null) ", Distance: ${it.getLocation().safeDistance(ownerLocation)}" else ""
                        val flags = mutableListOf<String>()
                        if (it.isNitwit) flags += "nitwit"
                        if (it.viewPlayers.hasVisiblePlayer()) flags += "visible"
                        if (it.controller.isNotEmpty()) flags += "ctrl:${it.controller.size}"
                        // 性能开关（仅显示非默认值）
                        if (it.isDisableVisibleEvent) flags += "noVisEvent"
                        if (it.isDisableVehicleCheckOnTick) flags += "noVehCheck"
                        if (!it.isRotationFixOnSpawn) flags += "noRotFix"
                        if (!it.isPassengerRefreshOnSpawn) flags += "noPassRefresh"
                        if (!it.isIgnoredClientPositionUpdateInterval) flags += "usePosInterval"

                        append("  [${it.entityType}] ${it.id} (${flags.joinToString(", ")})\n")
                        append("    - Location: ${it.getLocation()}$distStr\n")

                        if (it.passengers.isNotEmpty()) {
                            append("    - Passengers:\n")
                            val pt = measureTime {
                                it.passengers.forEachUuid { p ->
                                    val find = manager.getEntityByUniqueId(p)
                                    if (find == null) {
                                        append("        - $p (not found)\n")
                                    } else {
                                        append("        - $p (${find.id}, ${find.entityType})\n")
                                    }
                                }
                            }
                            append("    - Passenger lookup time: $pt\n")
                        }
                    }
                    append("\n================== End of Dump ==================\n")
                }
            )
        }
    }
}