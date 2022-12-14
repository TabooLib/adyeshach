package ink.ptms.adyeshach.module

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityTypes
import org.bukkit.command.CommandSender

fun testAdapter(sender: CommandSender) {
    val typeHandler = Adyeshach.api().getEntityTypeHandler()
    val helper = Adyeshach.api().getMinecraftAPI().getHelper()
    sender.info("Testing adapt(EntityTypes)")
    EntityTypes.values().forEach { entity ->
        sender.test(entity.name) {
            runCatching { typeHandler.getBukkitEntityType(entity) }.onSuccess { helper.adapt(entity) }
        }
    }
    sender.info("OK")
}