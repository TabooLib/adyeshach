package ink.ptms.adyeshach.impl.nms

import ink.ptms.adyeshach.api.dataserializer.createDataSerializer
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.MinecraftPacketHandler
import ink.ptms.adyeshach.core.MinecraftScoreboardOperator
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.module.nms.MinecraftVersion

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nms.DefaultMinecraftScoreboardOperator
 *
 * @author 坏黑
 * @since 2023/1/10 01:44
 */
class DefaultMinecraftScoreboardOperator : MinecraftScoreboardOperator {

    val isUniversal = MinecraftVersion.isUniversal
    val major = MinecraftVersion.major

    val packetHandler: MinecraftPacketHandler
        get() = Adyeshach.api().getMinecraftAPI().getPacketHandler()

    @Suppress("DuplicatedCode")
    override fun updateTeam(player: List<Player>, team: MinecraftScoreboardOperator.Team, method: MinecraftScoreboardOperator.TeamMethod) {
        // 版本判断
        val packet: Any = when (major) {
            // 1.9, 1.10, 1.11, 1.12
            in 1..4 -> NMS9PacketPlayOutScoreboardTeam().also {
                it.a(createDataSerializer {
                    writeUtf(team.name, 16)
                    writeByte(method.ordinal.toByte())
                    // ADD or CHANGE
                    if (method.ordinal == 0 || method.ordinal == 2) {
                        writeUtf("", 32) // 展示名称
                        writeUtf("", 16) // 前缀
                        writeUtf("", 16) // 后缀
                        writeByte(if (team.canSeeInvisible) 2 else 0) // 设置（比如什么 allowFriendlyFire、canSeeFriendlyInvisible）
                        writeUtf(if (team.nameTagVisible) "always" else "never", 32)
                        writeUtf(if (team.collision) "always" else "never", 32)
                        writeByte(team.color.ordinal.toByte())
                    }
                    // ADD or JOIN or LEAVE
                    if (method.ordinal == 0 || method.ordinal == 3 || method.ordinal == 4) {
                        writeVarInt(team.members.size)
                        team.members.forEach { name -> writeUtf(name, 40) }
                    }
                }.toNMS() as NMS9PacketDataSerializer)
            }
            // 1.13, 1.14, 1.15, 1.16
            in 5..8 -> NMS13PacketPlayOutScoreboardTeam().also {
                it.a(createDataSerializer {
                    writeUtf(team.name, 16)
                    writeByte(method.ordinal.toByte())
                    // ADD or CHANGE
                    if (method.ordinal == 0 || method.ordinal == 2) {
                        writeUtf("{\"text\":\"\"}") // 展示名称
                        writeByte(if (team.canSeeInvisible) 2 else 0) // 设置
                        writeUtf(if (team.nameTagVisible) "always" else "never", 40)
                        writeUtf(if (team.collision) "always" else "never", 40)
                        // 1.13 开始这个玩意儿从 byte 变成了 int
                        writeVarInt(team.color.ordinal)
                        // 1.13 开始这两个玩意儿放到了最后
                        writeUtf("{\"text\":\"\"}", 16)
                        writeUtf("{\"text\":\"\"}", 16)
                    }
                    // ADD or JOIN or LEAVE
                    if (method.ordinal == 0 || method.ordinal == 3 || method.ordinal == 4) {
                        writeVarInt(team.members.size)
                        team.members.forEach { name -> writeUtf(name, 40) }
                    }
                }.toNMS() as NMS13PacketDataSerializer)
            }
            // 1.17, 1.18, 1.19, 1.20
            9, 10, 11, 12 -> NMSPacketPlayOutScoreboardTeam(createDataSerializer {
                writeUtf(team.name, 16)
                writeByte(method.ordinal.toByte())
                // ADD or CHANGE
                if (method.ordinal == 0 || method.ordinal == 2) {
                    writeComponent("{\"text\":\"\"}")
                    writeByte(2) // 设置
                    writeUtf(if (team.nameTagVisible) "always" else "never", 40)
                    writeUtf(if (team.collision) "always" else "never", 40)
                    writeVarInt(team.color.ordinal)
                    writeComponent("{\"text\":\"\"}")
                    writeComponent("{\"text\":\"\"}")
                }
                // ADD or JOIN or LEAVE
                if (method.ordinal == 0 || method.ordinal == 3 || method.ordinal == 4) {
                    writeVarInt(team.members.size)
                    team.members.forEach { name -> writeUtf(name) }
                }
            }.toNMS() as NMSPacketDataSerializer)
            // 不支持
            else -> error("Unsupported version.")
        }
        // 发送数据包
        packetHandler.sendPacket(player, packet)
    }
}