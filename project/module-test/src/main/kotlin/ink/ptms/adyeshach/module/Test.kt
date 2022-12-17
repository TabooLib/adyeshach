package ink.ptms.adyeshach.module

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityEquipable
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.entity.path.PathType
import ink.ptms.adyeshach.core.entity.type.*
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Axolotl
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggest
import taboolib.common.platform.function.submit
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.actionBar
import taboolib.platform.util.onlinePlayers

@CommandHeader(name = "adytest")
object Test {

    lateinit var entity: AdyEntity

    // @Schedule(period = 1)
    fun onTick() {
        if (this::entity.isInitialized) {
            val op = onlinePlayers.firstOrNull() ?: return
            entity.teleport(op.location.clone().add(0.0, 3.0, 0.0))
        }
    }

    @CommandBody
    val test = subCommand {
        execute<CommandSender> { sender, _, _ ->
            sender.info("MinecraftEntityMetadataHandler")
            testParser(sender)
            testCreateMeta(sender)
            sender.info("MinecraftHelper")
            testAdapter(sender)
        }
    }

    @CommandBody
    val spawn2 = subCommand {
        execute<Player> { sender, _, _ ->
            val npc = sender.world.spawn(sender.location, Zombie::class.java)
            npc.setVelocity(sender.eyeLocation.direction.multiply(sender.itemInHand.amount.toDouble().coerceAtLeast(0.5)))
            submit(delay = 60) {
                npc.remove()
            }
        }
    }

    @CommandBody
    val spawn = subCommand {
        dynamic {
            suggest { EntityTypes.values().map { it.name } }
            execute<Player> { sender, _, args ->
                val type = EntityTypes.valueOf(args.uppercase())
                val item = ItemStack(Material.DIAMOND_SWORD)
                cost("all 1") {
                    if (::entity.isInitialized) {
                        entity.remove()
                    }
                    val npc = Adyeshach.api().getPublicEntityManager().create(type, sender.location) as AdyEntity
                    if (npc is AdyHuman) {
                        npc.setName("傻逼")
                        npc.setTexture("bukkitObj")
                    }
                    if (npc is EntityEquipable) {
                        npc.setItemInMainHand(item)
                    }
                    if (npc is AdyAxolotl) {
                        npc.setColor(Axolotl.Variant.BLUE)
                    }
                    if (npc is AdyFallingBlock) {
                        npc.setMaterial(Material.DIAMOND_BLOCK)
                    }
                    if (npc is AdyItem) {
                        submit(delay = 20) {
                            npc.setItem(ItemStack(Material.DIAMOND_BLOCK))
                        }
                    }
                    if (npc is AdyArmorStand) {
                        npc.setTag(StandardTags.DERIVED, "AdyeshachTest")
                    }
                    npc.setCustomName("坏黑")
                    npc.setCustomNameVisible(true)
                    npc.setGlowing(true)
                    npc.setVelocity(sender.eyeLocation.direction.multiply(sender.itemInHand.amount.toDouble().coerceAtLeast(0.5)))
                    npc.moveSpeed = 0.2
                    entity = npc
                }
                sender.info("OK")
            }
        }
    }

    @CommandBody
    val tp = subCommand {
        execute<Player> { sender, _, _ ->
            entity.teleport(sender.location)
            sender.info("OK")
        }
    }

    @CommandBody
    val move = subCommand {
        execute<Player> { sender, _, _ ->
            entity.controllerMove(sender.location, PathType.WALK_2, 0.2)
            sender.info("OK")
        }
    }

    @CommandBody
    val look = subCommand {
        execute<Player> { sender, _, _ ->
            entity.controllerLook(sender.location)
            sender.info("OK")
        }
    }

    @CommandBody
    val despawn = subCommand {
        execute<Player> { sender, _, _ ->
            entity.despawn(removeFromManager = true)
            sender.info("OK")
        }
    }

    @CommandBody
    val holo1 = subCommand {
        execute<Player> { sender, _, _ ->
            val handler = Adyeshach.api().getHologramHandler()
            handler.sendHologramMessage(sender, sender.eyeLocation, listOf("我是", "我是傻逼", "我是一个超级无敌大傻逼"))
            sender.info("OK")
        }
    }

    @CommandBody
    val holo2 = subCommand {
        execute<Player> { sender, _, _ ->
            val handler = Adyeshach.api().getHologramHandler()
            handler.createHologramByText(sender.eyeLocation, listOf("我是", "我是脑瘫", "我是一个脑瘫"))
            sender.info("OK")
        }
    }

    @CommandBody
    val holo3 = subCommand {
        execute<Player> { sender, _, _ ->
            val handler = Adyeshach.api().getHologramHandler()
            val h1 = handler.createHologramItem("我是")
            val h2 = handler.createHologramItem(ItemStack(Material.DIAMOND_BLOCK))
            val h3 = handler.createHologramItem("我是一个脑瘫")
            handler.createHologram(sender.eyeLocation, listOf(h1, h2, h3))
            sender.info("OK")
        }
    }

    @CommandBody
    val meta = subCommand {
        dynamic {
            suggest { EntityTypes.values().map { it.name } }
            execute<Player> { sender, _, args ->
                val type = EntityTypes.valueOf(args.uppercase())
                val adyClass = Adyeshach.api().getEntityTypeRegistry().getAdyClassFromEntityType(type)

                fun read(cla: Class<*>, level: Int = 0) {
                    sender.sendMessage(cla.name)
                    cla.interfaces.forEach {
                        read(it, level + 1)
                    }
                }
                read(adyClass)
            }
        }
    }

    @CommandBody
    val edit = subCommand {
        execute<Player> { sender, _, args ->
            Adyeshach.editor()?.openEditor(sender, entity)
        }
    }

    @CommandBody
    val highest = subCommand {
        execute<Player> { sender, _, _ ->
            val access = Adyeshach.api().getMinecraftAPI().getWorldAccess().createBlockAccess(sender.world, sender.location.chunk.x, sender.location.chunk.z)
            val x = sender.location.blockX
            val y = sender.location.blockY
            val z = sender.location.blockZ
            sender.sendMessage("highest -> ${access.getHighestBlock(x, y, z)} -> ${access.getBlockHeight(x, y, z)}")
            cost("getBlockType") {
                repeat(100000) {
                    access.getBlockType(x, y, z)
                }
            }
            cost("getHighestBlock") {
                repeat(100000) {
                    access.getHighestBlock(x, y, z)
                }
            }
            cost("getBlockHeight") {
                repeat(100000) {
                    access.getBlockHeight(x, y, z)
                }
            }
            cost("getHighestBlockAt") {
                repeat(100000) {
                    sender.world.getHighestBlockAt(x, z).y
                }
            }
            cost("getBlockAt(x, y, z).boundingBox") {
                repeat(100000) {
                    sender.world.getBlockAt(x, y, z).boundingBox
                }
            }
        }
    }

    @CommandBody
    val loop = subCommand {
        execute<Player> { sender, _, _ ->
            sender.sendMessage("发起循环测试, 手持命令方块可暂停，离线自动中止。")
            val loc = sender.eyeLocation.add(sender.eyeLocation.direction.multiply(2))
            var npc: AdyEntity? = null
            var i = 0
            val entityTypes = EntityTypes.values().filter {
                Adyeshach.api().getEntityTypeRegistry().getEntityFlags(it).contains("INVALID")
            }
            sender.sendMessage("无效的实体类型总计 ${entityTypes.size} 项")
            submit(period = 20) {
                if (!sender.isOnline) {
                    cancel()
                    return@submit
                }
                if (sender.itemInHand.type == XMaterial.COMMAND_BLOCK.parseMaterial()) {
                    sender.actionBar("暂停循环测试")
                    return@submit
                }
                npc?.despawn(removeFromManager = true)
                if (i >= entityTypes.size) {
                    sender.sendMessage("循环测试完成")
                    cancel()
                    return@submit
                }
                val type = entityTypes[i++]
                try {
                    val entity = Adyeshach.api().getPublicEntityManager().create(type, loc) as AdyEntity
                    entity.setCustomNameVisible(true)
                    entity.setCustomName("测试实体:$type")
                    entity.setGlowing(true)
                    npc = entity
                    sender.sendMessage("§a循环测试 $i, 类型：$type")
                } catch (ex: Throwable) {
                    sender.sendMessage("§c循环测试 $i, 类型：$type, 失败：${ex.message}")
                }
            }
        }
    }
}