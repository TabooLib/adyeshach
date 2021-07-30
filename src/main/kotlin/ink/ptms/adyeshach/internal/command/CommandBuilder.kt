package ink.ptms.adyeshach.internal.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import taboolib.common.platform.CommandBuilder
import taboolib.common5.Coerce

fun CommandBuilder.CommandComponent.dynamicLocation(
    func1: CommandBuilder.CommandComponentDynamic.() -> Unit,
    func2: (CommandBuilder.CommandComponentDynamic.() -> Unit)? = null,
) {
    // world
    dynamic {
        suggestion<CommandSender> { _, _ -> Bukkit.getWorlds().map { it.name } }
        // x
        dynamic {
            restrict<CommandSender> { _, _, argument -> Coerce.asDouble(argument).isPresent }
            // y
            dynamic {
                restrict<CommandSender> { _, _, argument -> Coerce.asDouble(argument).isPresent }
                // z
                dynamic {
                    func1(this)
                    if (func2 != null) {
                        // yaw
                        dynamic(optional = true) {
                            restrict<CommandSender> { _, _, argument -> Coerce.asDouble(argument).isPresent }
                            // pitch
                            dynamic {
                                restrict<CommandSender> { _, _, argument -> Coerce.asDouble(argument).isPresent }
                                func2(this)
                            }
                        }
                    }
                }
            }
        }
    }
}