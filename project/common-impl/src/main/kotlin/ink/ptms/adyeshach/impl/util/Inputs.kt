package ink.ptms.adyeshach.impl.util

import ink.ptms.adyeshach.core.Adyeshach
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEditBookEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildBook
import taboolib.platform.util.giveItem
import taboolib.platform.util.hasName
import taboolib.platform.util.takeItem
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

object Inputs {

    private val bookData = ConcurrentHashMap<String, Consumer<List<String>>>()

    fun Player.inputBook(lines: List<String> = emptyList(), listen: Consumer<List<String>>) {
        val inputName = Adyeshach.api().getLanguage().getLang(this, "editor-input-book-name")
        inventory.takeItem(99) { it.hasName(name) }
        giveItem(buildBook {
            write(lines.joinToString("\n"))
            material = XMaterial.WRITABLE_BOOK.parseMaterial()!!
            name = inputName
            author = "bukkitObj"
            lore += Adyeshach.api().getLanguage().getLang(this@inputBook, "editor-input-book-description").toString()
        })
        bookData[name] = listen
    }

    @SubscribeEvent
    private fun onQuit(e: PlayerQuitEvent) {
        bookData.remove(e.player.name)
    }

    @SubscribeEvent
    private fun onEdit(e: PlayerEditBookEvent) {
        if (e.previousBookMeta.displayName == Adyeshach.api().getLanguage().getLang(e.player, "editor-input-book-name")) {
            val listen = bookData.remove(e.player.name) ?: return
            val lines = e.newBookMeta.pages.flatMap {
                var legacyText = TextComponent(it).toLegacyText()
                if (legacyText.startsWith('§')) {
                    legacyText = legacyText.substring(2)
                }
                legacyText.split("\n")
            }
            listen.accept(lines)
            submit(delay = 1) {
                e.player.inventory.takeItem(99) { it.itemMeta == e.newBookMeta }
            }
        }
    }
}