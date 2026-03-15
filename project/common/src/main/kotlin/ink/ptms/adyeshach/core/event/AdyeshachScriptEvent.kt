package ink.ptms.adyeshach.core.event

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.manager.Manager
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * 脚本控制相关事件
 * 父类不会作为事件触发
 */
sealed class AdyeshachScriptEvent {

    /**
     * 选择单位
     * 可以更改选择的结果
     */
    class Select(
        val entity: MutableList<EntityInstance>,
        val manager: Manager,
        val sender: Player?,
        val input: String,
        val world: String,
        val byId: Boolean
    ): BukkitProxyEvent()

    /**
     * 看向坐标
     * 可以取消来阻止脚本的执行
     */
    class Look(
        val entity: List<EntityInstance>,
        val sender: Player?,
        val smooth: Boolean,
        var to: Location?,
        var x: Double?,
        var y: Double?,
        var z: Double?,
    ) : BukkitProxyEvent()

    /**
     * 传送
     */
    class Teleport(
        val entity: List<EntityInstance>,
        val sender: Player?,
        var to: Location
    ) : BukkitProxyEvent()
}