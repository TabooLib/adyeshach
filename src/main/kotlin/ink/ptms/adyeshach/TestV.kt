package ink.ptms.adyeshach

import ink.ptms.adyeshach.common.entity.type.impl.AdyCat
import ink.ptms.adyeshach.common.entity.type.impl.AdyHuman
import ink.ptms.adyeshach.common.util.Tasks
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import org.bukkit.DyeColor
import org.bukkit.entity.Cat
import org.bukkit.entity.Player

/**
 * @author Arasple
 * @date 2020/8/3 21:55
 * Villager test
 */
object TestV {

    @TInject
    val testV: CommandBuilder = CommandBuilder
            .create("test-v", Adyeshach.plugin)
            .execute { sender, _ ->
                if (sender is Player) {
                    val entity = AdyCat(sender)
                    entity.spawn(sender.location)
                    entity.setBaby(true)
                    entity.setTamed(true)
                    entity.setSitting(true)
                    entity.setCollarColor(DyeColor.WHITE)
                    entity.setType(Cat.Type.ALL_BLACK)
                    sender.sendMessage("§c[System] §7Done.")
                }
            }
}