package ink.ptms.adyeshach.common.util

import io.izzel.taboolib.module.inject.PlayerContainer
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.util.book.BookFormatter
import io.izzel.taboolib.util.book.builder.BookBuilder
import io.izzel.taboolib.util.chat.TextComponent
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.lite.Materials
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerEditBookEvent
import org.bukkit.inventory.meta.BookMeta
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object Inputs {

    @PlayerContainer
    private val bookData = ConcurrentHashMap<String, (List<String>) -> (Unit)>()

    fun bookIn(player: Player, bookMeta: List<String> = emptyList(), listen: (List<String>) -> (Unit)) {
        Items.takeItem(player.inventory, { Items.hasLore(it, "Inputs") }, 99)
        player.inventory.addItem(ItemBuilder(BookBuilder(Materials.WRITABLE_BOOK.parseItem()).pagesRaw(bookMeta.joinToString("\n")).build()).name("§fAdyeshach Book").lore("§7Inputs").build())
        bookData[player.name] = listen
    }

    @TListener
    class Events : Listener {

        @EventHandler
        fun e(e: PlayerEditBookEvent) {
            if (e.newBookMeta.lore?.get(0) == "§7Inputs") {
                Items.takeItem(e.player.inventory, { Items.hasLore(it, "Inputs") }, 99)
                val listen = bookData.remove(e.player.name) ?: return
                val lines = e.newBookMeta.pages.flatMap {
                    TextComponent(it).toPlainText().replace("§0", "").split("\n")
                }
                listen.invoke(lines)
            }
        }
    }
}