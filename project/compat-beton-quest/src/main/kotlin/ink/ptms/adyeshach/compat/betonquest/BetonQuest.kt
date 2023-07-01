package ink.ptms.adyeshach.compat.betonquest

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.StandardTags
import ink.ptms.adyeshach.core.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import pl.betoncraft.betonquest.config.Config
import pl.betoncraft.betonquest.conversation.CombatTagger
import pl.betoncraft.betonquest.conversation.Conversation
import pl.betoncraft.betonquest.utils.PlayerConverter
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.warning
import taboolib.common.util.unsafeLazy
import taboolib.common5.Baffle
import java.util.concurrent.TimeUnit

object BetonQuest {

    val isBetonQuestHooked by unsafeLazy { Bukkit.getPluginManager().getPlugin("BetonQuest") != null }

    val limit = Baffle.of(500, TimeUnit.MILLISECONDS)

    fun logic(player: Player, entity: EntityInstance) {
        // NPC 正在移动
        if (entity.hasTag(StandardTags.IS_MOVING) || entity.hasTag(StandardTags.IS_PATHFINDING)) {
            return
        }
        // 冷却
        if (!limit.hasNext(player.name)) {
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
        if (isBetonQuestHooked && e.player.hasPermission("betonquest.conversation")) {
            try {
                logic(e.player, e.entity)
            } catch (ex: NoClassDefFoundError) {
                e.player.sendMessage("§cThe current version of BetonQuest is not supported at this time")
            }
        }
    }

    @SubscribeEvent
    private fun onInteract(e: AdyeshachEntityInteractEvent) {
        if (isBetonQuestHooked && e.isMainHand && e.player.hasPermission("betonquest.conversation")) {
            try {
                logic(e.player, e.entity)
            } catch (ex: NoClassDefFoundError) {
                e.player.sendMessage("§cThe current version of BetonQuest is not supported at this time")
            }
        }
    }

    @SubscribeEvent
    private fun onQuit(e: PlayerQuitEvent) {
        limit.reset(e.player.name)
    }
}