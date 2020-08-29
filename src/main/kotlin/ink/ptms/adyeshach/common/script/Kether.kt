package ink.ptms.adyeshach.common.script

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.common.script.action.*
import ink.ptms.adyeshach.common.script.action.ActionCall
import io.izzel.kether.common.actions.KetherTypes
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.api.QuestStorage
import io.izzel.kether.common.api.storage.LocalYamlStorage
import io.izzel.taboolib.module.inject.TFunction
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.util.Coerce
import io.izzel.taboolib.util.Files
import org.bukkit.Bukkit
import org.bukkit.Location


object Kether {

    var storage: QuestStorage? = null
        private set

    @TFunction.Init
    fun init() {
        KetherTypes.registerInternals(
                ScriptService.registry,
                ScriptService
        )
        val registry = ScriptService.registry
        registry.registerAction("log", ActionLog.parser())
        registry.registerAction("call", ActionCall.parser<QuestContext>())
        registry.registerAction("wait", ActionWait.parser())
        registry.registerAction("always", ActionAlways.parser())
        registry.registerAction("command", ActionCommand.parser())
        registry.registerAction("create", ActionCreate.parser())
        registry.registerAction("delete", ActionDelete.parser())
        registry.registerAction("destroy", ActionDestroy.parser())
        registry.registerAction("remove", ActionRemove.parser())
        registry.registerAction("look", ActionLook.parser())
        registry.registerAction("move", ActionMove.parser())
        registry.registerAction("respawn", ActionRespawn.parser())
        registry.registerAction("teleport", ActionTeleport.parser())
        registry.registerAction("select", ActionSelect.parser())
        registry.registerAction("use", ActionUse.parser())

    }

    @TSchedule
    fun tick() {
        try {
            storage = LocalYamlStorage(ScriptService, Files.folder(Adyeshach.plugin.dataFolder, "npc/script data").toPath())
            storage!!.init()
        } catch (e: Exception) {
            println("[Adyeshach] Script data storage initialization failed")
            e.printStackTrace()
        }
        try {
            ScriptService.loadAll()
        } catch (e: Exception) {
            println("[Adyeshach] An error occurred while loading the script")
            e.printStackTrace()
        }
    }

    fun toLocation(str: String): Location {
        val args = str.split(",")
        return Location(
                Bukkit.getWorld(args[0]),
                Coerce.toDouble(args.getOrElse(1) { "0" }),
                Coerce.toDouble(args.getOrElse(2) { "0" }),
                Coerce.toDouble(args.getOrElse(3) { "0" }),
                Coerce.toFloat(args.getOrElse(4) { "0" }),
                Coerce.toFloat(args.getOrElse(5) { "0" })
        )
    }
}