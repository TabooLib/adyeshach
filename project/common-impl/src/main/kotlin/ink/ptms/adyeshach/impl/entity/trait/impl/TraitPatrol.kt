package ink.ptms.adyeshach.impl.entity.trait.impl

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.entity.path.InterpolatedLocation
import ink.ptms.adyeshach.core.entity.path.PathFinderHandler
import ink.ptms.adyeshach.core.entity.path.ResultNavigation
import ink.ptms.adyeshach.core.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.core.util.plus
import ink.ptms.adyeshach.impl.entity.trait.Trait
import ink.ptms.adyeshach.impl.util.ChunkAccess
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.warning
import taboolib.common5.clong
import taboolib.library.xseries.XMaterial
import taboolib.module.configuration.util.mapListAs
import taboolib.platform.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

@Suppress("DuplicatedCode")
object TraitPatrol : Trait() {

    const val PATROL_NEXT_MOVE = "PATROL_NEXT_MOVE"

    /** 编辑缓存 */
    val editCacheMap = ConcurrentHashMap<String, EntityInstance>()

    /** 节点缓存 */
    val nodesCacheMap = ConcurrentHashMap<String, List<InterpolatedLocation>>()

    /**
     * 巡逻触发周期
     */
    @Schedule(period = 2, async = true)
    fun process() {
        data.getKeys(false).forEach {
            val entity = Adyeshach.api().getEntityFinder().getEntityFromUniqueId(it)
            // 触发巡逻的前提 —— 不在编辑模式、不在移动、存在观察者
            if (entity != null && !entity.isEditing() && !entity.hasTag(StandardTags.IS_MOVING, StandardTags.IS_MOVING_START) && entity.hasViewer()) {
                // 获取所有节点
                val nodes = entity.getTraitPatrolNodes()
                if (nodes.isEmpty()) {
                    return@forEach
                }
                // 设置等待
                if (entity.getTag(PATROL_NEXT_MOVE) == null) {
                    entity.setTag(PATROL_NEXT_MOVE, System.currentTimeMillis() + entity.getTraitPatrolWaitTime())
                    return@forEach
                }
                // 等待移动
                if (entity.getTag(PATROL_NEXT_MOVE).clong > System.currentTimeMillis()) {
                    return@forEach
                }
                // 获取节点序号
                val index = entity.getNodeIndex()
                if (index < nodes.size) {
                    try {
                        entity.moveFrames = nodes[index].reset()
                        entity.setNodeIndex(index + 1)
                    } catch (e: Exception) {
                        warning("Patrol Error: $e")
                    }
                } else {
                    entity.setNodeIndex(0)
                }
                // 移除等待时间
                entity.removeTag(PATROL_NEXT_MOVE)
            }
        }
    }

    /**
     * 编辑模式展示
     */
    @Schedule(period = 20, async = true)
    fun edit() {
        Bukkit.getOnlinePlayers().forEach { player ->
            if (editCacheMap.containsKey(player.name)) {
                val entity = editCacheMap[player.name]!!
                val nodes = entity.getTraitPatrolNodes()
                nodes.forEachIndexed { i, node ->
                    // 播放轨迹
//                    node.reset()
//                    while (node.index < node.length) {
//                        val next = node.next()
//                        if (next != null) {
//                            player.spawnParticle(Particle.FLAME, next.clone().plus(y = if (i % 2 == 0) 0.0 else 0.2), 5, 0.0, 0.0, 0.0, 0.0)
//                        }
//                    }
                    val pos = node.target.clone()
                    // 节点粒子
                    player.spawnParticle(Particle.END_ROD, pos.clone().plus(0.5, 0.5, 0.5), 10, 0.0, 1.0, 0.0, 0.0)
                    // 节点全息
                    val hologram = Adyeshach.api().getHologramHandler().createHologram(player, pos.clone().plus(0.5, 1.0, 0.5), listOf("#${i + 1}"))
                    // 延迟删除
                    submit(delay = 20) { hologram.remove() }
                }
                language.sendLang(player, "trait-patrol", entity.getTraitPatrolNodeCount())
            }
        }
    }

    @SubscribeEvent
    private fun onRemove(e: AdyeshachEntityRemoveEvent) {
        e.entity.setTraitPatrolNodes(emptyList())
    }

    @SubscribeEvent
    private fun onQuit(e: PlayerQuitEvent) {
        editCacheMap.remove(e.player.name)
    }

    @SubscribeEvent
    private fun onSwap(e: PlayerDropItemEvent) {
        if (handle(e)) {
            e.isCancelled = true
        }
    }

    @SubscribeEvent
    private fun onSwap(e: PlayerSwapHandItemsEvent) {
        if (handle(e)) {
            e.isCancelled = true
        }
    }

    private fun handle(e: PlayerEvent): Boolean {
        if (editCacheMap.containsKey(e.player.name)) {
            e.player.playSound(e.player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
            if (e.player.isSneaking) {
                editCacheMap[e.player.name]?.setTraitPatrolNodes(emptyList())
                language.sendLang(e.player, "trait-patrol", 0)
            } else {
                val entity = editCacheMap.remove(e.player.name)
                entity?.setEditing(false)
                entity?.refreshTraitPatrolNodes()
                adaptPlayer(e.player).sendTitle("", "", 0, 40, 0)
                e.player.inventory.takeItem(999) { it.hasName(language.getLang(e.player, "trait-patrol-tool-name")) }
            }
            return true
        }
        return false
    }

    @SubscribeEvent
    private fun onInteract(e: PlayerInteractEvent) {
        if (editCacheMap.containsKey(e.player.name) && e.hand == EquipmentSlot.HAND) {
            val entity = editCacheMap[e.player.name]!!
            if (e.isLeftClickBlock() && e.item?.type == Material.BLAZE_ROD) {
                e.isCancelled = true
                e.player.playSound(e.player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
                // 增加节点
                val nodes = TraitPatrol.data.mapListAs("${entity.uniqueId}.nodes") { Location.deserialize(it) }.toMutableList()
                nodes += e.clickedBlock!!.location.add(0.0, 1.0, 0.0)
                // 更新节点
                entity.setTraitPatrolNodes(nodes)
            }
            if (e.isRightClickBlock() && e.item?.type == Material.BLAZE_ROD) {
                e.isCancelled = true
                e.player.playSound(e.player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
                // 移除节点
                val nodes = TraitPatrol.data.mapListAs("${entity.uniqueId}.nodes") { Location.deserialize(it) }.toMutableList()
                nodes.remove(e.clickedBlock!!.location.add(0.0, 1.0, 0.0))
                // 更新节点
                entity.setTraitPatrolNodes(nodes)
            }
            language.sendLang(e.player, "trait-patrol", entity.getTraitPatrolNodeCount())
        }
    }

    override fun id(): String {
        return "patrol"
    }

    override fun edit(player: Player, entityInstance: EntityInstance): CompletableFuture<Void> {
        editCacheMap[player.name] = entityInstance
        entityInstance.setEditing(true)
        player.giveItem(buildItem(XMaterial.BLAZE_ROD) {
            name = language.getLang(player, "trait-patrol-tool-name")
            lore += ""
            lore += language.getLang(player, "trait-patrol-tool-lore").toString()
            shiny()
        })
        return CompletableFuture.completedFuture(null)
    }

    /**
     * 设置单位编辑模式
     */
    private fun EntityInstance.setEditing(value: Boolean) {
        TraitPatrol.data["$uniqueId.edit"] = value
    }

    /**
     * 单位是否在编辑模式
     */
    private fun EntityInstance.isEditing(): Boolean {
        return TraitPatrol.data.getBoolean("$uniqueId.edit")
    }

    /**
     * 设置巡逻节点序号
     */
    private fun EntityInstance.setNodeIndex(value: Int) {
        TraitPatrol.data["$uniqueId.index"] = value
    }

    /**
     * 获取巡逻节点序号
     */
    private fun EntityInstance.getNodeIndex(): Int {
        return TraitPatrol.data.getInt("$uniqueId.index")
    }
}

/**
 * 设置巡逻等待时间
 */
fun EntityInstance.setTraitPatrolWaitTime(value: Long) {
    TraitPatrol.data["$uniqueId.wait"] = value
}

/**
 * 获取巡逻等待时间
 */
fun EntityInstance.getTraitPatrolWaitTime(): Long {
    return TraitPatrol.data.getLong("$uniqueId.wait", 1000).coerceAtLeast(100)
}

/**
 * 设置单位的移动节点
 */
fun EntityInstance.setTraitPatrolNodes(nodes: List<Location>) {
    if (nodes.isEmpty()) {
        TraitPatrol.data[uniqueId] = null
        TraitPatrol.nodesCacheMap.remove(uniqueId)
    } else {
        TraitPatrol.data["$uniqueId.nodes"] = nodes.map { it.serialize() }
        // 更新移动轨迹
        TraitPatrol.nodesCacheMap[uniqueId] = emptyList()
        nodes.toList().toInterpolatedLocation(moveSpeed).thenAccept {
            TraitPatrol.nodesCacheMap[uniqueId] = it
        }
    }
}

/**
 * 获取单位的移动轨迹
 */
fun EntityInstance.getTraitPatrolNodes(): List<InterpolatedLocation> {
    if (TraitPatrol.nodesCacheMap.containsKey(uniqueId)) {
        return TraitPatrol.nodesCacheMap[uniqueId]!!
    }
    TraitPatrol.nodesCacheMap[uniqueId] = emptyList()
    TraitPatrol.data.mapListAs("$uniqueId.nodes") { Location.deserialize(it) }.toList().toInterpolatedLocation(moveSpeed).thenAccept {
        TraitPatrol.nodesCacheMap[uniqueId] = it
    }
    return emptyList()
}

/**
 * 获取单位的移动轨迹数量
 */
fun EntityInstance.getTraitPatrolNodeCount(): Int {
    return TraitPatrol.data.getMapList("$uniqueId.nodes").size
}

/**
 * 刷新单位的移动轨迹
 */
fun EntityInstance.refreshTraitPatrolNodes() {
    TraitPatrol.nodesCacheMap.remove(uniqueId)
}

/**
 * 节点转轨迹
 */
fun List<Location>.toInterpolatedLocation(moveSpeed: Double): CompletableFuture<List<InterpolatedLocation>> {
    if (size < 2) {
        return CompletableFuture.completedFuture(emptyList())
    }
    val future = CompletableFuture<List<InterpolatedLocation>>()
    val list = mutableListOf<InterpolatedLocation>()
    fun process(cur: Int) {
        if (cur + 1 < size) {
            get(cur).toInterpolatedLocation(get(cur + 1), moveSpeed).thenAccept {
                list += it
                process(cur + 1)
            }
        } else {
            get(cur).toInterpolatedLocation(get(0), moveSpeed).thenAccept {
                list += it
                future.complete(list)
            }
        }
    }
    process(0)
    return future
}

/**
 * 获取移动轨迹
 */
fun Location.toInterpolatedLocation(to: Location, moveSpeed: Double): CompletableFuture<InterpolatedLocation> {
    val future = CompletableFuture<InterpolatedLocation>()
    // 请求路径
    PathFinderHandler.request(this, to) {
        it as ResultNavigation
        // 如果路径无效
        if (it.pointList.isEmpty()) {
            return@request
        }
        // 修正路径高度
        val chunkAccess = ChunkAccess.getChunkAccess(world)
        it.pointList.forEach { p -> p.y = chunkAccess.getBlockHighest(p.x, p.y, p.z) }
        // 返回移动路径
        future.complete(it.toInterpolated(world, moveSpeed, to))
    }
    return future
}