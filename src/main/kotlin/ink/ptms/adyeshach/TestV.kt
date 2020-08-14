package ink.ptms.adyeshach

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.type.EntityTypes
import ink.ptms.adyeshach.common.entity.type.impl.AdyCat
import ink.ptms.adyeshach.common.path.PathFinderProxy
import ink.ptms.adyeshach.common.path.PathType
import io.izzel.taboolib.module.ai.SimpleAiSelector
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.db.local.SecuredFile
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.util.Files
import io.izzel.taboolib.util.Reflection
import io.izzel.taboolib.util.lite.Effects
import net.minecraft.server.v1_16_R1.*
import org.bukkit.DyeColor
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftSilverfish
import org.bukkit.entity.Cat
import org.bukkit.entity.Player
import org.bukkit.entity.Silverfish
import java.io.File

/**
 * @author Arasple
 * @date 2020/8/3 21:55
 * Villager test
 */
object TestV {

    var proxy: Silverfish? = null

    @TInject
    val testV: CommandBuilder = CommandBuilder
            .create("test", Adyeshach.plugin)
            .execute { sender, _ ->
                if (sender is Player) {
                    Files.read(File(Adyeshach.plugin.dataFolder, "output.json")) {
                        val entity = AdyeshachAPI.fromJson(it.lines().toArray().joinToString("\n"), sender)
                        entity?.respawn()
                        sender.sendMessage(entity!!.toJson())
                    }
                    Files.read(File(Adyeshach.plugin.dataFolder, "output.yml")) {
                        val entity = AdyeshachAPI.fromYaml(it.lines().toArray().joinToString("\n"), sender)
                        entity?.respawn()
                    }
                    sender.sendMessage("done.")
                }
            }

}