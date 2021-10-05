package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.api.event.AdyeshachNaturalMetaGenerateEvent
import ink.ptms.adyeshach.common.bukkit.BukkitParticles
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.bukkit.data.DataWatcher
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector

/**
 * @param index -1 = 不可用，-2 = 伪造
 */
open class MetaNatural<T>(index: Int, key: String, def: T) : Meta(index, key, def!!) {

    init {
        dataWatcher = when (def) {
            is Int -> DataWatcher.DataInt
            is Byte -> DataWatcher.DataByte
            is Float -> DataWatcher.DataFloat
            is String -> DataWatcher.DataString
            is Boolean -> DataWatcher.DataBoolean
            is Vector -> DataWatcher.DataPosition
            is ItemStack -> DataWatcher.DataItemStack
            is EulerAngle -> DataWatcher.DataVector
            is MaterialData -> DataWatcher.DataBlockData
            is VillagerData -> DataWatcher.DataVillagerData
            is TextComponent -> DataWatcher.DataIChatBaseComponent
            is BukkitParticles -> DataWatcher.DataParticle
            is BukkitPose -> DataWatcher.DataPose
            else -> error("Unsupported meta $def")
        }
    }

    override fun generateMetadata(player: Player, entityInstance: EntityInstance): Any? {
        if (index == -1) {
            return null
        }
        val obj = entityInstance.metadata[key] ?: return null
        val event = AdyeshachNaturalMetaGenerateEvent(entityInstance, player, this, obj)
        event.call()
        return dataWatcher.createMetadata(index, event.value)
    }

    override fun toString(): String {
        return "MetaNatural(dataWatcher=$dataWatcher) ${super.toString()}"
    }
}