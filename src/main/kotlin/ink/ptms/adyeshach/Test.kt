package ink.ptms.adyeshach

import ink.ptms.adyeshach.common.util.Serializer
import io.izzel.taboolib.internal.gson.annotations.Expose
import io.izzel.taboolib.util.Ref
import io.izzel.taboolib.util.Reflection

/**
 * @Author sky
 * @Since 2020-08-03 15:57
 */
object Test {

//    @TInject
//    val command1 = CommandBuilder.create("player", Adyeshach.plugin)
//            .execute { sender, _ ->
//                if (sender is Player) {
//                    Bukkit.getScheduler().runTask(Adyeshach.plugin, Runnable {
//                        val e = TestHuman(sender.world, GameProfile(UUID.randomUUID(), "Bakurit_Maueic"))
//                        e.setLocation(sender.location.x, sender.location.y, sender.location.z, sender.location.yaw, sender.location.pitch)
//                        e.headRotation = sender.location.yaw
//                        e.valid = true
//                        e.bukkitEntity.isSleepingIgnored = true
//                        TPacketHandler.sendPacket(sender, PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, e))
//                        (sender.world as CraftWorld).handle.chunkProvider.addEntity(e)
//                        Bukkit.getScheduler().runTaskLaterAsynchronously(Adyeshach.plugin, Runnable {
//                            e.setFlag(6, true)
//                            sender.sendMessage("§c[System] §7Glowing Enabled.")
//                        }, 20)
//                        Bukkit.getScheduler().runTaskLaterAsynchronously(Adyeshach.plugin, Runnable {
//                            e.setFlag(6, false)
//                            sender.sendMessage("§c[System] §7Glowing Disabled.")
//                        }, 40)
//                        Bukkit.getScheduler().runTaskLaterAsynchronously(Adyeshach.plugin, Runnable {
//                            PlayerControllerLook(e).run {
//                                a(sender.location.x, sender.location.y, sender.location.z, 180f, 0f)
//                                a()
//                            }
//                            sender.sendMessage("§c[System] §7Looking for you.")
//                        }, 60L)
//                        Bukkit.getScheduler().runTaskLater(Adyeshach.plugin, Runnable {
//                            NMS.HANDLE.removeEntity(sender, e.bukkitEntity)
//                            sender.sendMessage("§c[System] §7Removed.")
//                        }, 100)
//                        sender.sendMessage("§c[System] §7Done 3.")
//                    })
//                }
//            }
//
//    @TInject
//    val command2 = CommandBuilder.create("test", Adyeshach.plugin)
//            .execute { sender, _ ->
//                if (sender is Player) {
//                    Bukkit.getScheduler().runTask(Adyeshach.plugin, Runnable {
//                        val e = EntityTypes.VILLAGER.a((sender.world as CraftWorld).handle)!!
//                        e.setLocation(sender.location.x, sender.location.y, sender.location.z, sender.location.yaw, sender.location.pitch)
//                        e.headRotation = sender.location.yaw
//                        e.valid = true
//                        (sender.world as CraftWorld).handle.chunkProvider.addEntity(e)
//                        e.tick()
//                        Bukkit.getScheduler().runTaskLaterAsynchronously(Adyeshach.plugin, Runnable {
//                            e.setFlag(6, true)
//                            sender.sendMessage("§c[System] §7Glowing Enabled.")
//                        }, 20)
//                        Bukkit.getScheduler().runTaskLaterAsynchronously(Adyeshach.plugin, Runnable {
//                            e.setFlag(6, false)
//                            sender.sendMessage("§c[System] §7Glowing Disabled.")
//                        }, 40)
//                        Bukkit.getScheduler().runTaskLaterAsynchronously(Adyeshach.plugin, Runnable {
//                            e.controllerLook.a((sender as CraftPlayer).handle, 180f, 0f)
//                            e.controllerLook.a()
//                            sender.sendMessage("§c[System] §7Looking for you.")
//                        }, 60L)
//                        Bukkit.getScheduler().runTaskLater(Adyeshach.plugin, Runnable {
//                            (0L..10L).forEach {
//                                Bukkit.getScheduler().runTaskLater(Adyeshach.plugin, Runnable {
//                                    SimpleAiSelector.getExecutor().navigationMove(e.bukkitEntity as LivingEntity, sender.location, 1.0)
//                                    e.tick()
//                                    sender.sendMessage("§c[System] §7Moving to you.")
//                                }, it)
//                            }
//                        }, 80L)
//                        Bukkit.getScheduler().runTaskLater(Adyeshach.plugin, Runnable {
//                            NMS.HANDLE.removeEntity(sender, e.bukkitEntity)
//                            sender.sendMessage("§c[System] §7Removed.")
//                        }, 100)
//                        sender.sendMessage("§c[System] §7Done.")
//                    })
//                }
//            }
}