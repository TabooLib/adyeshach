package ink.ptms.adyeshach.internal.trait.impl

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.createHologram
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
import taboolib.common.platform.SubscribeEvent
import taboolib.common.platform.adaptPlayer
import taboolib.common.platform.submit
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.*

object Patrol : Trait() {

    val edit = HashMap<String, EntityInstance>()

    init {
        submit(period = 5, async = true) {
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
        submit(period = 40, async = true) {
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
                            val hologram = player.createHologram(Location(player.world, node.x + 0.5, node.y + 1, node.z + 0.5), listOf("#${i + 1}"))
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
                adaptPlayer(e.player).sendTitle("", "§7Path Nodes: §80", 0, 40, 0)
            } else {
                edit.remove(e.player.name)?.edit(false)
                adaptPlayer(e.player).sendTitle("", "", 0, 40, 0)
                e.player.inventory.takeItem(999) { it.hasName("Patrol Tool") }
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
            adaptPlayer(e.player).sendTitle("", "§7Path Nodes: §8${entity.nodes().size}", 0, 40, 0)
        }
    }

    override fun getName(): String {
        return "patrol"
    }

    override fun edit(player: Player, entityInstance: EntityInstance) {
        edit[player.name] = entityInstance
        entityInstance.edit(true)
        player.giveItem(buildItem(XMaterial.BLAZE_ROD) {
            name = "§bPatrol Tool"
            lore += ""
            lore += "§7Left-Click to add path node and Right-Click to remove."
            shiny()
        })
    }
}