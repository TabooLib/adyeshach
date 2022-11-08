package ink.ptms.adyeshach.common.entity.ai.expand

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.util.safeDistance
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.potion.PotionEffectType
import taboolib.common5.Coerce

/**
 * @author sky
 * @since 2020-08-19 22:09
 */
class ControllerLookAtPlayerAlways(entity: EntityInstance) : Controller(entity) {

    override fun isAsync(): Boolean {
        return true
    }

    override fun shouldExecute(): Boolean {
        return (entity!!.getTag("isFreeze") == "true") || !entity.isControllerMoving()
    }

    override fun onTick() {
        // 冻结时间小于 0.5 秒不会执行
        // 避免出现客户端服务端位置不同步：2022/11/8 23:00
        if (entity!!.hasTag("isFreeze_Time") && System.currentTimeMillis() - (entity.getTagValue("isFreeze_Time") as Long) < 350L) {
            return
        }
        entity.viewPlayers.getViewPlayers()
            .filterNot {
                it.hasPotionEffect(PotionEffectType.INVISIBILITY) || it.gameMode == GameMode.SPECTATOR || it.isInvulnerable
            }.minByOrNull {
                it.location.safeDistance(entity.position.toLocation())
            }?.let {
                if (it.location.safeDistance(entity.position.toLocation()) < 16) {
                    entity.controllerLook(it.eyeLocation)
                }
            }
    }

    fun isOnGround() = kotlin.runCatching { entity!!.isControllerOnGround() }.getOrDefault(true)
}