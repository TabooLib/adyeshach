package ink.ptms.adyeshach.internal.trait.impl

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.path.PathFinderProxy
import ink.ptms.adyeshach.common.entity.path.ResultNavigation
import ink.ptms.adyeshach.common.util.Tasks
import ink.ptms.adyeshach.internal.trait.Trait
import io.izzel.taboolib.internal.xseries.XMaterial
import io.izzel.taboolib.module.hologram.THologram
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.lite.Effects
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot

class Patrol : Trait(), Listener {

    val edit = HashMap<String, EntityInstance>()

    init {
        Tasks.timer(0, 1, true) {
            data.getKeys(false).forEach {
                val entityInstance = AdyeshachAPI.getEntityFromUniqueId(it)
                if (entityInstance != null
                    && !entityInstance.isEditing()
                    && !entityInstance.isControllerMoving()
                    && !entityInstance.hasTag("tryMoving")
                    && entityInstance.hasViewer()
                ) {
                    val index = entityInstance.index()
                    val nodes = entityInstance.nodes()
                    if (index < nodes.size) {
                        try {
                            entityInstance.controllerMove(nodes[index])
                            entityInstance.index(index + 1)
                        } catch (e: Exception) {
                            println("[Adyeshach] Patrol Error: $e")
                        }
                    } else {
                        entityInstance.index(0)
                    }
                }
            }
        }
        Tasks.timer(0, 40, true) {
            Bukkit.getOnlinePlayers().forEach { player ->
                if (player.name in edit) {
                    val nodes = edit[player.name]!!.nodes()
                    if (nodes.isNotEmpty()) {
                        var p = nodes[0]
                        nodes.forEachIndexed { i, node ->
                            if (i > 0) {
                                PathFinderProxy.request(p, node, edit[player.name]!!.entityType.getPathType()) { r ->
                                    (r as ResultNavigation).pointList.forEachIndexed { index, p ->
                                        Tasks.delay(index * 2L) {
                                            Effects.create(Particle.FLAME, Location(player.world, p.x + 0.5, p.y + 0.5, p.z + 0.5))
                                                .count(5)
                                                .player(player)
                                                .play()
                                        }
                                    }
                                }
                                p = node
                            }
                            if (i > 1 && i + 1 == nodes.size) {
                                PathFinderProxy.request(node, nodes[0], edit[player.name]!!.entityType.getPathType()) { r ->
                                    (r as ResultNavigation).pointList.forEachIndexed { index, p ->
                                        Tasks.delay(index * 2L) {
                                            Effects.create(Particle.FLAME, Location(player.world, p.x + 0.5, p.y + 0.5, p.z + 0.5))
                                                .count(5)
                                                .player(player)
                                                .play()
                                        }
                                    }
                                }
                            }
                            THologram.create(Location(player.world, node.x + 0.5, node.y + 1, node.z + 0.5), "#${i + 1}", player)
                                .deleteOn(40)
                            Effects.create(Particle.VILLAGER_HAPPY, Location(player.world, node.x + 0.5, node.y + 0.5, node.z + 0.5))
                                .offset(doubleArrayOf(0.0, 1.0, 0.0))
                                .count(20)
                                .player(player)
                                .play()
                        }
                    }
                    TLocale.Display.sendTitle(player, "", "§7Path Nodes: §8${nodes.size}", 0, 60, 0)
                    TLocale.Display.sendActionBar(player, "§7Press §fF §7to save entity's path node §8| §7Press §fSHIFT + F §7to reset")
                }
            }
        }
    }

    fun EntityInstance.nodes(nodes: List<Location>) {
        data.set("$uniqueId.nodes", nodes)
    }

    @Suppress("UNCHECKED_CAST")
    fun EntityInstance.nodes(): List<Location> {
        return data.getList("$uniqueId.nodes", ArrayList<Location>()) as List<Location>
    }

    fun EntityInstance.edit(value: Boolean) {
        data.set("$uniqueId.edit", value)
    }

    fun EntityInstance.isEditing(): Boolean {
        return data.getBoolean("$uniqueId.edit")
    }

    fun EntityInstance.index(value: Int) {
        data.set("$uniqueId.index", value)
    }

    fun EntityInstance.index(): Int {
        return data.getInt("$uniqueId.index")
    }

    @EventHandler
    fun e(e: PlayerQuitEvent) {
        edit.remove(e.player.name)
    }

    @EventHandler
    fun e(e: PlayerSwapHandItemsEvent) {
        if (e.player.name in edit) {
            e.isCancelled = true
            e.player.playSound(e.player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
            if (e.player.isSneaking) {
                edit[e.player.name]?.nodes(emptyList())
                TLocale.Display.sendTitle(e.player, "", "§7Path Nodes: §80", 0, 40, 0)
            } else {
                edit.remove(e.player.name)?.edit(false)
                Items.takeItem(e.player.inventory, { Items.hasName(e.player.itemInHand, "Patrol Tool") }, 999)
                TLocale.Display.sendTitle(e.player, "", "", 0, 40, 0)
            }
        }
    }

    @EventHandler
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
            TLocale.Display.sendTitle(e.player, "", "§7Path Nodes: §8${entity.nodes().size}", 0, 40, 0)
        }
    }

    override fun getName(): String {
        return "patrol"
    }

    override fun edit(player: Player, entityInstance: EntityInstance) {
        edit[player.name] = entityInstance
        entityInstance.edit(true)
        player.inventory.addItem(
            ItemBuilder(XMaterial.BLAZE_ROD).name("§bPatrol Tool").lore("", "§7Left-Click to add path node and Right-Click to remove.").shiny().build()
        )
    }
}