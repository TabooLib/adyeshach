package ink.ptms.adyeshach.common.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitDirection
import ink.ptms.adyeshach.common.bukkit.BukkitPaintings
import ink.ptms.adyeshach.common.entity.editor.Editors
import ink.ptms.adyeshach.common.entity.ClientEntity
import ink.ptms.adyeshach.common.entity.EntityTypes
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
class AdyPainting : AdyEntity(EntityTypes.PAINTING) {

    @Expose
    private var painting: BukkitPaintings = BukkitPaintings.KEBAB
    @Expose
    private var direction: BukkitDirection = BukkitDirection.NORTH

    init {
//        naturalEditor("painting")
//                .from(Editors.enums(BukkitPaintings::class) { _, entity, meta, _, e -> "/adyeshachapi edit painting_painting ${entity.uniqueId} ${meta.key} $e" })
//                .reset { _, _ ->
//                    setPainting(BukkitPaintings.KEBAB)
//                }
//                .display { _, _, _ ->
//                    getPainting().name
//                }.build()
//        naturalEditor("direction")
//                .from(Editors.enums(BukkitDirection::class) { _, entity, meta, _, e -> "/adyeshachapi edit painting_direction ${entity.uniqueId} ${meta.key} $e" })
//                .reset { _, _ ->
//                    setDirection(BukkitDirection.NORTH)
//                }
//                .display { _, _, _ ->
//                    getDirection().name
//                }.build()
    }

    override fun visible(viewer: Player, visible: Boolean) {
        if (visible) {
            spawn(viewer) {
                val clientId = UUID.randomUUID()
                AdyeshachAPI.clientEntityMap.computeIfAbsent(viewer.name) { ConcurrentHashMap() }[index] = ClientEntity(this, clientId)
                NMS.INSTANCE.spawnEntityPainting(viewer, index, clientId, position.toLocation(), direction, painting)
            }
        } else {
            destroy(viewer) {
                NMS.INSTANCE.destroyEntity(viewer, index)
                AdyeshachAPI.clientEntityMap[viewer.name]?.remove(index)
            }
        }
    }

    fun setDirection(direction: BukkitDirection) {
        this.direction = direction
        respawn()
    }

    fun getDirection(): BukkitDirection {
        return direction
    }

    fun setPainting(painting: BukkitPaintings) {
        this.painting = painting
        respawn()
    }

    fun getPainting(): BukkitPaintings {
        return painting
    }
}