package ink.ptms.adyeshach.common.entity.ai.general

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.ai.Pathfinder
import io.izzel.taboolib.module.db.local.Local
import io.izzel.taboolib.module.lite.SimpleCounter
import io.izzel.taboolib.util.lite.Numbers
import org.bukkit.Location
import org.bukkit.block.Block

/**
 * @Author sky
 * @Since 2020-08-19 22:09
 */
class GeneralGravity(entity: EntityInstance) : Pathfinder(entity) {

    private var p = 0.1
    private var x = 0.0
    private var z = 0.0
    private var b: Block? = null
    private var by = 0.0
    private var c = SimpleCounter(5)

    var isOnGround = true
        private set

    override fun shouldExecute(): Boolean {
        return true
    }

    override fun onTick() {
        val locEntity = entity.position.toLocation()
        if (locEntity.x != x || locEntity.z != z) {
            x = locEntity.x
            z = locEntity.z
            b = getHeightBlock(locEntity.clone())
            by = b?.boundingBox?.maxY ?: 0.0
        }
        if (b == null) {
            return
        }
        if (locEntity.y - by > p) {
            isOnGround = false
            entity.teleport(locEntity.clone().subtract(0.0, p, 0.0))
            p += 0.1
        } else if (!isOnGround) {
            isOnGround = true
            entity.teleport(locEntity.clone().run {
                this.y = by
                this
            })
            p = 0.1
        } else if (c.next()) {
            x = 0.0
            z = 0.0
        }
    }

    fun getHeightBlock(loc: Location): Block? {
        if (loc.y < 1) {
            return null
        }
        val block = loc.subtract(0.0, 1.0, 0.0).block
        return if (block.isPassable) getHeightBlock(block.location) else block
    }
}