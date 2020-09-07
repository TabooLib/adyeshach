package ink.ptms.adyeshach.internal.migrate

import com.isnakebuzz.servernpc.Main
import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.ai.expand.ControllerLookAtPlayer
import ink.ptms.adyeshach.common.entity.ai.general.GeneralSmoothLook
import ink.ptms.adyeshach.common.entity.type.AdyHuman
import io.izzel.taboolib.module.inject.TInject

/**
 * @Author sky
 * @Since 2020-08-14 18:18
 */
class MigrateServerNPC : Migrate() {

    @TInject("ServerNPC")
    lateinit var plugin: Main
        private set

    override fun depend(): String {
        return "ServerNPC"
    }

    override fun migrate() {
        plugin.npcManager.npcList.forEach {
            val entity = AdyeshachAPI.getEntityManagerPublic().create(EntityTypes.PLAYER, it.location) as AdyHuman
            entity.id = "servernpc_${it.name}"
            if (!it.gameProfile.properties.isEmpty) {
                val property = it.gameProfile.properties.entries().first().value
                entity.setName(property.name)
                entity.setTexture(property.value, property.signature)
            } else {
                entity.setName(it.name)
            }
            if (it.isLooking) {
                entity.registerController(GeneralSmoothLook(entity))
                entity.registerController(ControllerLookAtPlayer(entity))
            }
            if (it.npcSettings.isGlowing) {
                entity.setGlowing(true)
            }
            if (it.npcSettings.helmet != null) {
                entity.setHelmet(it.npcSettings.helmet)
            }
            if (it.npcSettings.chest != null) {
                entity.setChestplate(it.npcSettings.chest)
            }
            if (it.npcSettings.leggs != null) {
                entity.setChestplate(it.npcSettings.leggs)
            }
            if (it.npcSettings.boots != null) {
                entity.setChestplate(it.npcSettings.boots)
            }
            if (it.npcSettings.mainHand != null) {
                entity.setChestplate(it.npcSettings.mainHand)
            }
            if (it.npcSettings.secondHand != null) {
                entity.setChestplate(it.npcSettings.secondHand)
            }
            it.delete()
        }
    }
}