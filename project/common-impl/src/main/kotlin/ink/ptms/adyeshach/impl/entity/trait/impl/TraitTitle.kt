package ink.ptms.adyeshach.impl.entity.trait.impl

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachHologram
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.manager.ManagerType
import ink.ptms.adyeshach.core.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityVisibleEvent
import ink.ptms.adyeshach.impl.entity.trait.Trait
import ink.ptms.adyeshach.impl.util.Inputs.inputBook
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.configuration.util.getStringListColored
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.runKether
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

object TraitTitle : Trait() {

    // <玩家, <NPC, 全息>>
    val playerLookup = ConcurrentHashMap<String, MutableMap<String, AdyeshachHologram>>()

    // <NPC, <玩家, 全息>>
    val entityLookup = ConcurrentHashMap<String, MutableMap<String, AdyeshachHologram>>()

    @Awake(LifeCycle.ACTIVE)
    fun enable() {
        Adyeshach.api().getEventBus().prepareTeleport { e ->
            if (e.isPositionChanged && entityLookup.containsKey(e.entity.uniqueId) && !e.entity.isDerived()) {
                entityLookup[e.entity.uniqueId]!!.forEach {
                    it.value.teleport(e.location.clone().add(0.0, e.entity.entitySize.height + e.entity.getTraitTitleHeight(), 0.0))
                }
            }
            true
        }
    }

    @Awake(LifeCycle.DISABLE)
    fun cancel() {
        // 插件卸载时无论通过何种 lookup 删除所有全息对象均可
        playerLookup.forEach { it.value.forEach { holo -> holo.value.remove() } }
    }

    @Schedule(period = 100, async = true)
    fun update() {
        Adyeshach.api().getPublicEntityManager(ManagerType.PERSISTENT).getEntities { !it.isDerived() }.forEach { update(it) }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onVisible(e: AdyeshachEntityVisibleEvent) {
        // 只有在玩家能够看到 NPC 时才会生成全息，所以只通过 Visible 事件创建或回收全息
        if (e.entity.isPublic()) {
            if (e.visible) {
                create(e.viewer, e.entity)
            } else {
                remove(e.viewer, e.entity)
            }
        }
    }

    @SubscribeEvent
    private fun onDelete(e: AdyeshachEntityRemoveEvent) {
        e.entity.setTraitTitle(null)
        e.entity.setTraitTitleHeight(null)
    }

    @SubscribeEvent
    private fun onQuit(e: PlayerQuitEvent) {
        // 玩家退出时必须清空位于 playerLookup 中的容器，并删除全息对象
        playerLookup.remove(e.player.name)?.forEach {
            it.value.remove()
            // 同时回收 entityLookup 中对应的缓存
            entityLookup[it.key]!!.remove(e.player.name)
        }
    }

    /**
     * 创建全息
     */
    fun create(entity: EntityInstance) {
        Bukkit.getOnlinePlayers().forEach { create(it, entity) }
    }

    /**
     * 创建全息
     */
    fun create(viewer: Player, entity: EntityInstance) {
        // 是否存在 title 配置
        if (data.contains(entity.uniqueId)) {
            // 先移除
            remove(viewer, entity)
            // 再创建
            val loc = entity.getLocation().add(0.0, entity.entitySize.height + entity.getTraitTitleHeight(), 0.0)
            val message = data.getStringListColored(entity.uniqueId).map {
                runKether { KetherFunction.parse(it, namespace = listOf("adyeshach"), sender = adaptCommandSender(viewer)) }.toString()
            }
            if (message.isEmpty()) {
                return
            }
            val hologram = Adyeshach.api().getHologramHandler().createHologram(viewer, loc, message)
            // 写入 playerLookup 并删除之前存在的全息对象
            val playerHologramMap = playerLookup.computeIfAbsent(viewer.name) { ConcurrentHashMap() }
            playerHologramMap.put(entity.uniqueId, hologram)?.remove()
            // 写入 entityLookup
            val entityHologramMap = entityLookup.computeIfAbsent(entity.uniqueId) { ConcurrentHashMap() }
            entityHologramMap.put(viewer.name, hologram)?.remove()
        }
    }

    /**
     * 更新全息内容
     */
    fun update(entity: EntityInstance) {
        Bukkit.getOnlinePlayers().forEach { update(it, entity) }
    }

    /**
     * 更新全息内容
     */
    fun update(viewer: Player, entity: EntityInstance) {
        val playerHologramMap = playerLookup[viewer.name] ?: return
        if (playerHologramMap.containsKey(entity.uniqueId)) {
            val hologram = playerHologramMap[entity.uniqueId]!!
            val message = data.getStringListColored(entity.uniqueId)
            // 只有内容中存在 Kether 脚本才会触发刷新机制
            if (message.isNotEmpty() && message.any { it.contains("{{") }) {
                val newMessage = message.map {
                    runKether { KetherFunction.parse(it, namespace = listOf("adyeshach"), sender = adaptCommandSender(viewer)) }.toString()
                }
                // 如果长度不变，则更新内容
                if (hologram.contents().size == newMessage.size) {
                    hologram.contents().mapIndexed { index, item ->
                        (item as AdyeshachHologram.ItemByText).text = newMessage[index]
                    }
                } else {
                    hologram.update(newMessage.map { Adyeshach.api().getHologramHandler().createHologramItem(it) })
                }
            }
        }
    }

    /**
     * 移除全息缓存
     */
    fun remove(entity: EntityInstance) {
        Bukkit.getOnlinePlayers().forEach { remove(it, entity) }
    }

    /**
     * 移除全息缓存
     */
    fun remove(viewer: Player, entity: EntityInstance) {
        val playerHologramMap = playerLookup[viewer.name] ?: return
        if (playerHologramMap.containsKey(entity.uniqueId)) {
            // 移除 playerLookup 中的缓存并删除全息实例
            playerHologramMap.remove(entity.uniqueId)!!.remove()
            // 移除 entityLookup 中的缓存
            entityLookup[entity.uniqueId]!!.remove(viewer.name)
        }
    }

    override fun id(): String {
        return "title"
    }

    override fun edit(player: Player, entityInstance: EntityInstance): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        language.sendLang(player, "trait-title")
        player.inputBook(data.getStringList(entityInstance.uniqueId)) {
            remove(entityInstance)
            if (it.all { line -> line.isBlank() }) {
                data[entityInstance.uniqueId] = null
            } else {
                data[entityInstance.uniqueId] = it
                create(entityInstance)
            }
            future.complete(null)
        }
        return future
    }
}

/** 设置实体头衔 */
fun EntityInstance.setTraitTitle(title: List<String>?) {
    TraitTitle.remove(this)
    if (title == null || title.all { line -> line.isBlank() }) {
        TraitTitle.data[uniqueId] = null
    } else {
        TraitTitle.data[uniqueId] = title
        TraitTitle.create(this)
    }
}

/** 获取实体头衔 */
fun EntityInstance.getTraitTitle(): List<String> {
    return TraitTitle.data.getStringList(uniqueId)
}

/** 设置实体头衔高度 */
fun EntityInstance.setTraitTitleHeight(height: Double?) {
    if (height == null) {
        TraitTitle.data["height.$uniqueId"] = null
    } else {
        TraitTitle.data["height.$uniqueId"] = height
        TraitTitle.entityLookup[uniqueId]?.forEach {
            it.value.teleport(getLocation().add(0.0, entitySize.height + getTraitTitleHeight(), 0.0))
        }
    }
}

/** 获取实体头衔高度 */
fun EntityInstance.getTraitTitleHeight(): Double {
    return TraitTitle.data.getDouble("height.$uniqueId", 0.25)
}