package ink.ptms.adyeshach.common.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.getName
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.isNotAir
import java.util.*

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyFallingBlock : AdyEntity(EntityTypes.FALLING_BLOCK) {

    @Expose
    var material = Material.STONE
        private set

    @Expose
    var data = 0.toByte()
        private set

    init {
        registerEditor("block")
            .reset { _, _ ->
                material = Material.STONE
                data = 0.toByte()
            }
            .modify { player, entity, _ ->
                player.openMenu<Basic>("Adyeshach Editor : Input") {
                    rows(1)
                    map("####@####")
                    set('#', XMaterial.BLACK_STAINED_GLASS_PANE) {
                        name = "§f"
                    }
                    set('@', ItemStack(material, 1, data.toShort()))
                    onClick('#')
                    onClose {
                        val item = it.inventory.getItem(4)
                        if (item.isNotAir()) {
                            material = item!!.type
                            data = item.durability.toByte()
                        }
                        destroy()
                        spawn(getLocation())
                        Editor.open(player, entity)
                    }
                }
            }
            .display { player, _, _ ->
                ItemStack(material, 1, data.toShort()).getName(player)
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

    fun setData(data: Byte) {
        this.data = data
        respawn()
    }
}