package ink.ptms.adyeshach.common.script

import ink.ptms.adyeshach.common.util.Tasks
import org.bukkit.Bukkit
import java.util.concurrent.Executor

object ScriptSchedulerExecutor : Executor {

    override fun execute(command: Runnable) {
        if (Bukkit.isPrimaryThread()) {
            command.run()
        } else {
            Tasks.task(true) { command.run() }
        }
    }
}