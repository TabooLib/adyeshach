package ink.ptms.adyeshach.internal.listener

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.common.entity.EntityInstance
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import pl.betoncraft.betonquest.config.Config
import pl.betoncraft.betonquest.conversation.CombatTagger
import pl.betoncraft.betonquest.conversation.Conversation
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException
import pl.betoncraft.betonquest.utils.PlayerConverter
import taboolib.common.LifeCycle
import taboolib.common.platform.SkipTo
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.warning
import taboolib.common5.Baffle
import java.util.concurrent.TimeUnit

@SkipTo(LifeCycle.LOAD)
object ListenerBetonQuest {

    val limit = Baffle.of(500, TimeUnit.MILLISECONDS)

    fun logic(player: Player, entity: EntityInstance) {
        // NPC 正在移动
        if (entity.isTryMoving() || entity.isControllerMoving()) {
            return
        }
        try {
            // 1.12.5
            val playerID = PlayerConverter.getID(player)
            if (CombatTagger.isTagged(playerID)) {
                try {
                    Config.sendNotify(null, playerID, "busy", "busy,error")
                } catch (ex: Throwable) {
                    warning("The notify system was unable to play a sound for the 'busy' category. Error was: '${ex.message}'")
                    ex.printStackTrace()
                }
                return
            }
            val assignment = Config.getNpc(entity.id)
            if (assignment != null) {
                Conversation(playerID, assignment, entity.getLocation())
            }
        } catch (ex: NoClassDefFoundError) {
            // 2.0.0
            val playerID = org.betonquest.betonquest.utils.PlayerConverter.getID(player)
            if (org.betonquest.betonquest.conversation.CombatTagger.isTagged(playerID)) {
                try {
                    org.betonquest.betonquest.config.Config.sendNotify(null, playerID, "busy", "busy,error")
                } catch (ex: Throwable) {
                    warning("The notify system was unable to play a sound for the 'busy' category. Error was: '${ex.message}'")
                    ex.printStackTrace()
                }
                return
            }
            val assignment = org.betonquest.betonquest.config.Config.getNpc(entity.id)
            if (assignment != null) {
                org.betonquest.betonquest.conversation.Conversation(playerID, assignment, entity.getLocation())
            }
        }
    }

    @SubscribeEvent
    private fun onDamage(e: AdyeshachEntityDamageEvent) {
        if (AdyeshachAPI.betonQuestHooked && e.player.hasPermission("betonquest.conversation")) {
            logic(e.player, e.entity)
        }
    }

    @SubscribeEvent
    private fun onInteract(e: AdyeshachEntityInteractEvent) {
        if (AdyeshachAPI.betonQuestHooked && e.isMainHand && e.player.hasPermission("betonquest.conversation")) {
            logic(e.player, e.entity)
        }
    }

    @SubscribeEvent
    private fun onQuit(e: PlayerQuitEvent) {
        limit.reset(e.player.name)
    }
}