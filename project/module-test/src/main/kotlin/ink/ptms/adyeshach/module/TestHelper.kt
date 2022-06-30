package ink.ptms.adyeshach.module

import org.bukkit.command.CommandSender

fun CommandSender.info(any: Any?) {
    sendMessage("§c[Adyeshach] §7${any.toString().replace("&", "§")}")
}

fun CommandSender.test(name: String, onlyError: Boolean = true, test: () -> Any?) {
    kotlin.runCatching {
        test()
        if (!onlyError) {
            info("  -- $name: §aOK")
        }
    }.onFailure {
        info("  -- $name: §cERROR (${it.message})")
    }
}

fun CommandSender.testValue(name: String, value: Any, onlyError: Boolean = true, test: () -> Any?) {
    kotlin.runCatching {
        val result = test()
        if (result == value) {
            if (!onlyError) {
                info("  -- $name: §aOK")
            }
        } else {
            info("  -- $name: §6FAILED ($result != $value)")
        }
    }.onFailure {
        info("  -- $name: §cERROR (${it.message})")
    }
}

fun CommandSender.testClass(name: String, value: String, onlyError: Boolean = true, test: () -> Any?) {
    kotlin.runCatching {
        val result = test()
        if (result?.javaClass?.simpleName == value) {
            if (!onlyError) {
                info("  -- $name: §aOK")
            }
        } else {
            info("  -- $name: §6FAILED")
            info("     -- (${result?.javaClass?.simpleName} != $value)")
        }
    }.onFailure {
        info("  -- $name: §cERROR (${it.message})")
    }
}