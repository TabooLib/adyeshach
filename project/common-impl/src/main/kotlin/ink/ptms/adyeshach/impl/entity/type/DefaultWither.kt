package ink.ptms.adyeshach.impl.entity.type

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyWither
import org.bukkit.entity.Entity

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultWither
 *
 * @author 坏黑
 * @since 2022/6/29 19:16
 */
abstract class DefaultWither(entityTypes: EntityTypes) : DefaultEntityLiving(entityTypes), AdyWither {

    override fun getFirstHeadTarget(): Entity? {
        val helper = Adyeshach.api().getMinecraftAPI().getHelper()
        return helper.getEntity(position.toLocation().world!!, getMetadata("firstHeadTarget"))
    }

    override fun getSecondHeadTarget(): Entity? {
        val helper = Adyeshach.api().getMinecraftAPI().getHelper()
        return helper.getEntity(position.toLocation().world!!, getMetadata("secondHeadTarget"))
    }

    override fun getThirdHeadTarget(): Entity? {
        val helper = Adyeshach.api().getMinecraftAPI().getHelper()
        return helper.getEntity(position.toLocation().world!!, getMetadata("thirdHeadTarget"))
    }
}