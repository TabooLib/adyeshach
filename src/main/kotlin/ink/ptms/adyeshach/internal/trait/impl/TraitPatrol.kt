package ink.ptms.adyeshach.internal.trait.impl

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.event.AdyeshachEntityRemoveEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.path.PathFinderHandler
import ink.ptms.adyeshach.common.entity.path.ResultNavigation
import ink.ptms.adyeshach.internal.trait.Trait
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.submit
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.*

object TraitPatrol : Trait() {

    val edit = HashMap<String, EntityInstance>()
    val nodesCacheMap = HashMap<String, List<Location>>()

    /**
     * 巡逻触发周期
     */
    @Schedule(period = 5, async = true)
    fun process() {
        data.getKeys(false).forEach {
            val entity = AdyeshachAPI.getEntityFromUniqueId(it)
            // 触发巡逻的前提：不在编辑模式、不在移动、不在准备移动、存在观察者
            if (entity != null && !entity.isEditing() && !entity.isControllerMoving() && !entity.hasTag("tryMoving") && entity.hasViewer()) {
                val index = entity.index()
                val nodes = entity.nodes()
                if (index < nodes.size) {
                    try {
                        entity.controllerMove(nodes[index])
                        entity.index(index + 1)
                    } catch (e: Exception) {
                        println("[Adyeshach] Patrol Error: $e")
                    }
                } else {
                    entity.index(0)
                }
            }
        }
    }

    /**
     * 编辑模式展示
     */
    @Schedule(period = 40, async = true)
    fun edit() {
        Bukkit.getOnlinePlayers().forEach { player ->
            if (player.name in edit) {
                val nodes = edit[player.name]!!.nodes()
                if (nodes.isNotEmpty()) {
                    var p = nodes[0]
                    nodes.forEachIndexed { i, node ->
                        if (i > 0) {
                            PathFinderHandler.request(p, node, edit[player.name]!!.entityType.getPathType()) { r ->
                                (r as ResultNavigation).pointList.forEachIndexed { index, p ->
                                    submit(delay = index * 2L) {
                                        val pos = Location(player.world, p.x + 0.5, p.y + 0.5, p.z + 0.5)
                                        player.spawnParticle(Particle.FLAME, pos, 5, 0.0, 0.0, 0.0, 0.0)
                                    }
                                }
                            }
                            p = node
                        }
                        if (i > 1 && i + 1 == nodes.size) {
                            PathFinderHandler.request(node, nodes[0], edit[player.name]!!.entityType.getPathType()) { r ->
                                (r as ResultNavigation).pointList.forEachIndexed { index, p ->
                                    submit(delay = index * 2L) {
                                        val pos = Location(player.world, p.x + 0.5, p.y + 0.5, p.z + 0.5)
                                        player.spawnParticle(Particle.FLAME, pos, 5, 0.0, 0.0, 0.0, 0.0)
                                    }
                                }
                            }
                        }
                        val hologram = AdyeshachAPI.createHologram(player, Location(player.world, node.x + 0.5, node.y + 1, node.z + 0.5), listOf("#${i + 1}"))
                        submit(delay = 40) {
                            hologram.delete()
                        }
                        val pos = Location(player.world, node.x + 0.5, node.y + 0.5, node.z + 0.5)
                        player.spawnParticle(Particle.VILLAGER_HAPPY, pos, 20, 0.0, 1.0, 0.0, 0.0)
                    }
                }
                player.sendLang("trait-patrol", nodes.size)
            }
        }
    }

    fun EntityInstance.nodes(nodes: List<Location>) {
        if (nodes.isEmpty()) {
            data["$uniqueId.nodes"] = null
            nodesCacheMap.remove(uniqueId)
        } else {
            data["$uniqueId.nodes"] = nodes.map { it.serialize() }
            nodesCacheMap[uniqueId] = nodes.toList()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun EntityInstance.nodes(): List<Location> {
        return nodesCacheMap.computeIfAbsent(uniqueId) {
            data.getList("$uniqueId.nodes")?.map { Location.deserialize(it as MutableMap<String, Any>) } ?: emptyList()
        }
    }

    fun EntityInstance.edit(value: Boolean) {
        data["$uniqueId.edit"] = value
    }

    fun EntityInstance.isEditing(): Boolean {
        return data.getBoolean("$uniqueId.edit")
    }

    fun EntityInstance.index(value: Int) {
        data["$uniqueId.index"] = value
    }

    fun EntityInstance.index(): Int {
        return data.getInt("$uniqueId.index")
    }

    @SubscribeEvent
    fun e(e: AdyeshachEntityRemoveEvent) {
        e.entity.nodes(emptyList())
    }

    @SubscribeEvent
    fun e(e: PlayerQuitEvent) {
        edit.remove(e.player.name)
    }

    @SubscribeEvent
    fun e(e: PlayerSwapHandItemsEvent) {
        if (e.player.name in edit) {
            e.isCancelled = true
            e.player.playSound(e.player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
            if (e.player.isSneaking) {
                edit[e.player.name]?.nodes(emptyList())
                e.player.sendLang("trait-patrol", 0)
            } else {
                edit.remove(e.player.name)?.edit(false)
                adaptPlayer(e.player).sendTitle("", "", 0, 40, 0)
                e.player.inventory.takeItem(999) { it.hasName(e.player.asLangText("trait-patrol-tool-name")) }
            }
        }
    }

    @SubscribeEvent
    fun e(e: PlayerInteractEvent) {
        if (e.player.name in edit && e.hand == EquipmentSlot.HAND) {
            val entity = edit[e.player.name]!!
            if (e.action == Action.LEFT_CLICK_BLOCK && e.item?.type == Material.BLAZE_ROD) {
                e.isCancelled = true
                entity.nodes(entity.nodes().toMutableSet().let {
                    it.add(e.clickedBlock!!.location.add(0.0, 1.0, 0.0))
                    it.toList()
                })
            }
            if (e.action == Action.RIGHT_CLICK_BLOCK && e.item?.type == Material.BLAZE_ROD) {
                e.isCancelled = true
                e.player.playSound(e.player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
                entity.nodes(entity.nodes().toMutableSet().let {
                    it.remove(e.clickedBlock!!.location.add(0.0, 1.0, 0.0))
                    it.toList()
                })
            }
            e.player.sendLang("trait-patrol", entity.nodes().size)
        }
    }

    override fun getName(): String {
        return "patrol"
    }

    override fun edit(player: Player, entityInstance: EntityInstance) {
        edit[player.name] = entityInstance
        entityInstance.edit(true)
        player.giveItem(buildItem(XMaterial.BLAZE_ROD) {
            name = player.asLangText("trait-patrol-tool-name")
            lore += ""
            lore += player.asLangText("trait-patrol-tool-lore")
            shiny()
        })
    }
}