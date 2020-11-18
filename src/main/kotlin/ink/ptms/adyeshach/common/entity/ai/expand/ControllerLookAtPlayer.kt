package ink.ptms.adyeshach.common.entity.ai.expand

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller
import io.izzel.taboolib.util.lite.Numbers
import org.bukkit.GameMode
import org.bukkit.potion.PotionEffectType

/**
 * 看向周围玩家
 *
 * @Author sky
 * @Since 2020-08-19 22:09
 */
class ControllerLookAtPlayer(entity: EntityInstance) : Controller(entity) {

    override fun isAsync(): Boolean {
        return true
    }

    override fun shouldExecute(): Boolean {
        return Numbers.random(0.01) && !entity!!.isControllerMoving()
    }

    override fun onTick() {
        entity!!.viewPlayers.getViewPlayers()
                .filterNot {
                    it.hasPotionEffect(PotionEffectType.INVISIBILITY)
                            || it.gameMode == GameMode.SPECTATOR
                            || it.isInvulnerable
                }.minByOrNull { it.location.distance(entity.position.toLocation()) }?.let {
                    if (it.location.distance(entity.position.toLocation()) < 16) {
                        entity.controllerLook(it.eyeLocation, smooth = true)
                    }
                }
    }
}