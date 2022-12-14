package ink.ptms.adyeshach.module

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityEquipable
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyAxolotl
import ink.ptms.adyeshach.core.entity.type.AdyEntity
import ink.ptms.adyeshach.core.entity.type.AdyFallingBlock
import ink.ptms.adyeshach.core.entity.type.AdyHuman
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Axolotl
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.Schedule
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

    @Schedule(period = 1)
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
    val spawn = subCommand {
        dynamic {
            suggest { EntityTypes.values().map { it.name } }
            execute<Player> { sender, _, args ->
                val type = EntityTypes.valueOf(args.uppercase())
                val item = ItemStack(Material.DIAMOND_SWORD)
                cost("all 1") {
                    if (::entity.isInitialized) {
                        entity.despawn(removeFromManager = true)
                    }
                    val npc = Adyeshach.api().getPublicEntityManager().create(type, sender.location.add(0.0, 3.0, 0.0)) as AdyEntity
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
                    npc.setCustomName("坏黑")
                    npc.setCustomNameVisible(true)
                    npc.setGlowing(true)
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
    val loop = subCommand {
        execute<Player> { sender, _, _ ->
            sender.sendMessage("发起循环测试, 手持命令方块可暂停，离线自动中止。")
            val loc = sender.eyeLocation.add(sender.eyeLocation.direction.multiply(2))
            var npc: AdyEntity? = null
            var i = 0
            val entityTypes = EntityTypes.values().filter {
                Adyeshach.api().getEntityTypeHandler().getEntityFlags(it).contains("INVALID")
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