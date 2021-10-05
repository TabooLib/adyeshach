package ink.ptms.adyeshach.common.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Player

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyExperienceOrb : AdyEntity(EntityTypes.EXPERIENCE_ORB) {

    @Expose
    var amount = 1

    init {
//        registerEditor("amount")
//            .reset { _, _ ->
//                amount = 1
//            }
//            .modify { player, entity, _ ->
//                player.inputSign(arrayOf("$amount", "", "请在第一行输入内容")) {
//                    if (it[0].isNotEmpty()) {
//                        amount = NumberConversions.toInt(it[0])
//                    }
//                    entity.openEditor(player)
//                }
//            }
//            .display { _, _, _ -> "$amount" }
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