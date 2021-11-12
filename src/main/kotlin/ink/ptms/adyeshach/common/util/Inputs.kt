package ink.ptms.adyeshach.common.util

import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEditBookEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

object Inputs {

    private val bookData = ConcurrentHashMap<String, Consumer<List<String>>>()

    fun Player.inputBook(lines: List<String> = emptyList(), listen: Consumer<List<String>>) {
        val inputName = asLangText("editor-text-input-name")
        inventory.takeItem(99) { it.hasName(name) }
        giveItem(buildBook {
            write(lines.joinToString("\n"))
            material = XMaterial.WRITABLE_BOOK.parseMaterial()!!
            name = inputName
            author = "123"
            lore += asLangText("editor-text-input-lore")
        })
        bookData[name] = listen
    }

    @SubscribeEvent
    fun e(e: PlayerQuitEvent) {
        bookData.remove(e.player.name)
    }

    @SubscribeEvent
    fun e(e: PlayerEditBookEvent) {
        if (e.previousBookMeta.displayName == e.player.asLangText("editor-text-input-name")) {
            val listen = bookData.remove(e.player.name) ?: return
            val lines = e.newBookMeta.pages.flatMap {
                TextComponent(it).toPlainText().replace("§0", "").split("\n")
            }
            listen.accept(lines)
            submit(delay = 1) {
                e.player.inventory.takeItem(99) { it.itemMeta == e.newBookMeta }
            }
        }
    }
}