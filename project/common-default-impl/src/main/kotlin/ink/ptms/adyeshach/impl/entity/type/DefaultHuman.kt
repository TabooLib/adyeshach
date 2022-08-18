package ink.ptms.adyeshach.impl.entity.type

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.api.event.AdyeshachGameProfileGenerateEvent
import ink.ptms.adyeshach.common.api.Adyeshach
import ink.ptms.adyeshach.common.bukkit.BukkitAnimation
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.bukkit.data.GameProfile
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyHuman
import ink.ptms.adyeshach.common.entity.type.minecraftVersion
import org.bukkit.entity.Player
import taboolib.common.platform.Schedule
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.module.chat.colored
import taboolib.platform.util.onlinePlayers

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.type.DefaultHuman
 *
 * @author 坏黑
 * @since 2022/6/29 19:05
 */
abstract class DefaultHuman(entityTypes: EntityTypes) : DefaultEntityLiving(entityTypes), AdyHuman {

    private val playerUUID by lazy { normalizeUniqueId }

    @Expose
    internal val gameProfile = GameProfile()

    @Expose
    internal var isSleepingLegacy = false

    @Expose
    override var isHideFromTabList = true
        set(value) {
            if (value) {
                forViewers { removePlayerInfo(it) }
            } else {
                forViewers { addPlayerInfo(it) }
            }
            field = value
        }

    override fun visible(viewer: Player, visible: Boolean): Boolean {
        return if (visible) {
            spawn(viewer) {
                addPlayerInfo(viewer)
                registerClientEntity(viewer)
                Adyeshach.api().getMinecraftAPI().getEntitySpawner().spawnNamedEntity(viewer, index, playerUUID, position.toLocation())
            }
        } else {
            destroy(viewer) {
                removePlayerInfo(viewer)
                Adyeshach.api().getMinecraftAPI().getEntityOperator().destroyEntity(viewer, index)
                unregisterClientEntity(viewer)
            }
        }
    }

    override fun setName(name: String) {
        gameProfile.name = name.colored()
        respawn()
    }

    override fun getName(): String {
        return gameProfile.name
    }

    override fun setPing(ping: Int) {
        gameProfile.ping = ping
        respawn()
    }

    override fun getPing(): Int {
        return gameProfile.ping
    }

    override fun setTexture(name: String) {
        gameProfile.textureName = name
        // 延迟加载皮肤
        submitAsync {
            try {
                Adyeshach.api().getNetworkAPI().getSkin().getTexture(name)?.also {
                    setTexture(it.value(), it.signature())
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    override fun setTexture(texture: String, signature: String) {
        gameProfile.texture = arrayOf(texture, signature)
        respawn()
    }

    override fun getTexture(): Array<String> {
        return gameProfile.texture
    }

    override fun getTextureName(): String {
        return gameProfile.textureName
    }

    override fun resetTexture() {
        gameProfile.texture = arrayOf("")
        respawn()
    }

    override fun setSleeping(value: Boolean) {
        if (value) {
            if (minecraftVersion >= 11400) {
                setPose(BukkitPose.SLEEPING)
            } else {
                val operator = Adyeshach.api().getMinecraftAPI().getEntityOperator()
                forViewers { operator.updatePlayerSleeping(it, index, position.toLocation()) }
            }
        } else {
            if (minecraftVersion >= 11400) {
                setPose(BukkitPose.STANDING)
            } else {
                setAnimation(BukkitAnimation.LEAVE_BED)
            }
            teleport(position)
        }
        isSleepingLegacy = value
    }

    override fun isSleeping(): Boolean {
        return if (minecraftVersion >= 11400) {
            getPose() == BukkitPose.SLEEPING
        } else {
            isSleepingLegacy
        }
    }

    override fun refreshPlayerInfo(viewer: Player) {
        removePlayerInfo(viewer)
        addPlayerInfo(viewer)
        submit(delay = 5) {
            if (isHideFromTabList) {
                removePlayerInfo(viewer)
            }
        }
    }

    protected fun addPlayerInfo(viewer: Player) {
        val event = AdyeshachGameProfileGenerateEvent(this, viewer, gameProfile.clone())
        event.call()
        val handler = Adyeshach.api().getMinecraftAPI().getEntityPlayerHandler()
        handler.addPlayerInfo(viewer, playerUUID, event.gameProfile.name, event.gameProfile.ping, event.gameProfile.texture)
    }

    protected fun removePlayerInfo(viewer: Player) {
        Adyeshach.api().getMinecraftAPI().getEntityPlayerHandler().removePlayerInfo(viewer, playerUUID)
    }

    companion object {

        @Schedule(delay = 200, period = 200)
        internal fun playerTextureRefresh200() {
            val finder = Adyeshach.api().getEntityFinder()
            var i = 0L
            onlinePlayers.forEach {
                submitAsync(delay = i++) {
                    finder.getVisibleEntities(it).filterIsInstance<AdyHuman>().forEach { human ->
                        human.refreshPlayerInfo(it)
                    }
                }
            }
        }
    }
}