package ink.ptms.adyeshach.module.editor.meta.impl

import ink.ptms.adyeshach.core.bukkit.data.VillagerData
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.type.AdyVillager
import ink.ptms.adyeshach.module.editor.lang
import ink.ptms.adyeshach.module.editor.meta.MetaEditor
import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.module.chat.colored
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked
import taboolib.platform.util.Slots
import taboolib.platform.util.buildItem

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.meta.impl.MetaVillager
 *
 * @author 坏黑
 * @since 2022/12/27 04:05
 */
@Suppress("DuplicatedCode")
class MetaVillager : MetaEditor {

    override fun open(entity: EntityInstance, player: Player, def: String) {
        entity as AdyVillager
        player.openMenu<Basic>(player.lang("input-villager-data")) {
            rows(3)
            set(Slots.LINE_2_MIDDLE - 1, buildItem(Material.OAK_LOG) {
                name = player.lang("input-villager-type")
                lore += player.lang("input-villager-description", entity.getVillagerData().type)
            }) {
                openType(entity, player)
            }
            set(Slots.LINE_2_MIDDLE + 1, buildItem(Material.BOOKSHELF) {
                name = player.lang("input-villager-profession")
                lore += player.lang("input-villager-description", entity.getVillagerData().profession)
            }) {
                openProfession(entity, player)
            }
        }
    }

    fun openType(entity: EntityInstance, player: Player) {
        entity as AdyVillager
        player.openMenu<Linked<VillagerData.Type>>(player.lang("input-villager-data-choose")) {
            rows(6)
            slots(Slots.CENTER)
            elements { VillagerData.Type.values().toList() }
            onGenerate { _, element, _, _ ->
                buildItem(element.material) {
                    name = "&7${element}".colored()
                    if (element == entity.getVillagerData().type) {
                        shiny()
                    }
                }
            }
            onClick { _, element ->
                entity.setVillagerData(entity.getVillagerData().copy(type = element))
                openType(entity, player)
            }
        }
    }

    fun openProfession(entity: EntityInstance, player: Player) {
        entity as AdyVillager
        player.openMenu<Linked<VillagerData.Profession>>(player.lang("input-villager-data-choose")) {
            rows(6)
            slots(Slots.CENTER)
            elements { VillagerData.Profession.values().toList() }
            onGenerate { _, element, _, _ ->
                buildItem(element.material) {
                    name = "&7${element}".colored()
                    if (element == entity.getVillagerData().profession) {
                        shiny()
                    }
                }
            }
            onClick { _, element ->
                entity.setVillagerData(entity.getVillagerData().copy(profession = element))
                openProfession(entity, player)
            }
        }
    }
}