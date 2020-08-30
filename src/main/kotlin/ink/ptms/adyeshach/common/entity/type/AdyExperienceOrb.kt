package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.entity.EntityTypes
import io.izzel.taboolib.internal.gson.annotations.Expose
import io.izzel.taboolib.util.lite.Signs
import org.bukkit.entity.Player
import org.bukkit.util.NumberConversions

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyExperienceOrb : AdyEntity(EntityTypes.EXPERIENCE_ORB) {

    @Expose
    var amount = 1

    init {
        registerEditor("amount")
                .reset { entity, meta ->
                    amount = 1
                }
                .modify { player, entity, meta ->
                    Signs.fakeSign(player, arrayOf("$amount", "", "请在第一行输入内容")) {
                        if (it[0].isNotEmpty()) {
                            amount = NumberConversions.toInt(it[0])
                        }
                        Editor.open(player, entity)
                    }
                }
                .display { _, entity, meta ->
                    "$amount"
                }
    }

    override fun visible(viewer: Player, visible: Boolean) {
        if (visible) {
            spawn(viewer) {
                NMS.INSTANCE.spawnEntityExperienceOrb(viewer, index, position.toLocation(), amount)
            }
        } else {
            destroy(viewer) {
                NMS.INSTANCE.destroyEntity(viewer, index)
            }
        }
    }
}