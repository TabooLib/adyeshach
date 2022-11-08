package ink.ptms.adyeshach.common.entity.ai.expand

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller
import ink.ptms.adyeshach.common.util.safeDistance
import org.bukkit.GameMode
import org.bukkit.potion.PotionEffectType
import taboolib.common.util.random

/**
 * 看向周围玩家
 *
 * @author sky
 * @since 2020-08-19 22:09
 */
class ControllerLookAtPlayer(entity: EntityInstance) : Controller(entity) {

    var look = 0

    override fun isAsync(): Boolean {
        return true
    }

    override fun shouldExecute(): Boolean {
        if (entity!!.getTag("isFreeze") == "true" || !entity.isControllerMoving()) {
            if (random(0.01)) {
                look = random(10, 60)
            }
            if (look > 0) {
                look--
                return true
            }
        }
        return false
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
                    entity.controllerLook(it.eyeLocation, smooth = true)
                }
            }
    }
}