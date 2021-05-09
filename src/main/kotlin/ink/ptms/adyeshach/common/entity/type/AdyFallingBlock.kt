package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.entity.EntityTypes
import io.izzel.taboolib.internal.gson.annotations.Expose
import io.izzel.taboolib.module.i18n.I18n
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import io.izzel.taboolib.util.lite.Materials
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyFallingBlock() : AdyEntity(EntityTypes.FALLING_BLOCK) {

    @Expose
    private var material = Material.STONE

    @Expose
    private var data = 0.toByte()

    init {
        registerEditor("block")
            .reset { _, _ ->
                material = Material.STONE
                data = 0.toByte()
            }
            .modify { player, entity, meta ->
                MenuBuilder.builder(Adyeshach.plugin)
                    .title("Adyeshach Editor : Input")
                    .rows(1)
                    .items("####@####")
                    .put('#', ItemBuilder(Materials.BLACK_STAINED_GLASS_PANE.parseItem()).name("§f").build())
                    .put('@', ItemStack(material, 1, data.toShort()))
                    .event {
                        if (it.slot == '#') {
                            it.isCancelled = true
                        }
                    }.close {
                        val item = it.inventory.getItem(4)
                        if (Items.nonNull(item)) {
                            material = item!!.type
                            data = item.durability.toByte()
                        }
                        destroy()
                        spawn(getLocation())
                        Editor.open(player, entity)
                    }.open(player)
            }
            .display { player, _, _ ->
                I18n.get().getName(player, ItemStack(material, 1, data.toShort()))
            }
    }

    override fun visible(viewer: Player, visible: Boolean) {
        if (visible) {
            spawn(viewer) {
                NMS.INSTANCE.spawnEntityFallingBlock(viewer, index, UUID.randomUUID(), position.toLocation(), material, data)
            }
        } else {
            destroy(viewer) {
                NMS.INSTANCE.destroyEntity(viewer, index)
            }
        }
    }

    fun setMaterial(material: Material, data: Byte) {
        this.material = material
        this.data = data
        respawn()
    }

    fun setMaterial(material: Material) {
        this.material = material
        respawn()
    }

    fun getMaterial(material: Material): Material {
        return material
    }

    fun setData(data: Byte) {
        this.data = data
        respawn()
    }

    fun getData(data: Byte): Byte {
        return data
    }
}