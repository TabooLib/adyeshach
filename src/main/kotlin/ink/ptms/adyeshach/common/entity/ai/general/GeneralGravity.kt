package ink.ptms.adyeshach.common.entity.ai.general

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Controller
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.FallingBlock
import taboolib.common5.Baffle
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author sky
 * @since 2020-08-19 22:09
 */
class GeneralGravity(entity: EntityInstance) : Controller(entity) {

    private var p = 0.1
    private var x = 0.99
    private var z = 0.99
    private var b: Block? = null
    private var by = 0.0
    private var c = Baffle.of(5)

    var isGravity = true
    var isOnGround = true
        private set

    override fun shouldExecute(): Boolean {
        return isGravity
    }

    override fun onTick() {
        val locEntity = entity!!.position.toLocation()
        if (locEntity.x != x || locEntity.z != z) {
            x = locEntity.x
            z = locEntity.z
            b = getHeightBlock(locEntity.clone())
            if (b != null) {
                by = NMS.INSTANCE.getBlockHeight(b!!) + b!!.y
            }
        }
        if (b == null) {
            return
        }
        if (locEntity.y - by > p) {
            isOnGround = false
            entity.teleportFuture(locEntity.clone().subtract(0.0, p, 0.0))
            if (entity !is FallingBlock) {
                p += 0.1
            }
        } else if (!isOnGround) {
            isOnGround = true
            entity.teleportFuture(locEntity.clone().run {
                this.y = by
                this
            })
            p = 0.1
        } else if (c.hasNext()) {
            x = 0.99
            z = 0.99
        }
    }

    fun getHeightBlock(loc: Location): Block? {
        if (loc.y < 1) {
            return null
        }
        val block = loc.subtract(0.0, 0.5, 0.0).block
        return if (NMS.INSTANCE.getBlockHeight(block) == 0.0) getHeightBlock(block.location) else block
    }
}