package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.api.nms.NMS
import ink.ptms.adyeshach.common.bukkit.BukkitAnimation
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.editor.Editor.toDisplay
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.element.GameProfile
import ink.ptms.adyeshach.common.util.Tasks
import ink.ptms.adyeshach.common.util.mojang.MojangAPI
import io.izzel.taboolib.internal.gson.annotations.Expose
import io.izzel.taboolib.util.lite.Signs
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.util.NumberConversions
import java.util.*

/**
 * @Author sky
 * @Since 2020-08-04 15:44
 */
class AdyHuman() : AdyEntityLiving(EntityTypes.PLAYER) {

    private val playerUUID = UUID.randomUUID()

    @Expose
    private val gameProfile = GameProfile()

    @Expose
    private var isSleepingLegacy = false

    @Expose
    var isHideFromTabList = true
        set(value) {
            if (value) {
                forViewers { removePlayerInfo(it) }
            } else {
                forViewers { addPlayerInfo(it) }
            }
            field = value
        }

    init {
        /**
         * 1.15 -> 16
         * 1.14 -> 15
         * 1.10 -> 13
         * 1.9 -> 12
         */
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinCape", 0x01, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinJacket", 0x02, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinLeftSleeve", 0x04, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinRightSleeve", 0x08, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinLeftPants", 0x10, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinRightPants", 0x20, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinHat", 0x40, true)
        registerEditor("isSleepingLegacy")
                .reset { player, entity, meta ->
                    setSleeping(false)
                }
                .modify { player, entity, meta ->
                    setSleeping(!isSleeping())
                    Editor.open(player, entity)
                }
                .display { _, entity, meta ->
                    isSleeping().toDisplay()
                }
        registerEditor("isHideFromTabList")
                .reset { player, entity, meta ->
                    isHideFromTabList = true
                }
                .modify { player, entity, meta ->
                    isHideFromTabList = !isHideFromTabList
                    Editor.open(player, entity)
                }
                .display { _, entity, meta ->
                    isHideFromTabList.toDisplay()
                }
        registerEditor("playerName")
                .reset { player, entity, meta ->
                    setName("AdyHuman")
                }
                .modify { player, entity, meta ->
                    Signs.fakeSign(player, arrayOf(getName(), "", "请在第一行输入内容")) {
                        if (it[0].isNotEmpty()) {
                            setName(if (it[0].length > 16) it[0].substring(0, 16) else it[0])
                        }
                        Editor.open(player, entity)
                    }
                }
                .display { _, entity, meta ->
                    if (getName().isEmpty()) "§7_" else Editor.toSimple(getName())
                }
        registerEditor("playerPing")
                .reset { player, entity, meta ->
                    setPing(60)
                }
                .modify { player, entity, meta ->
                    Signs.fakeSign(player, arrayOf("${getPing()}", "", "请在第一行输入内容")) {
                        if (it[0].isNotEmpty()) {
                            setPing(NumberConversions.toInt(it[0]))
                        }
                        Editor.open(player, entity)
                    }
                }
                .display { _, entity, meta ->
                    getPing().toString()
                }
        registerEditor("playerTexture")
                .reset { player, entity, meta ->
                    resetTexture()
                }
                .modify { player, entity, meta ->
                    Signs.fakeSign(player, arrayOf(getTextureName(), "", "请在第一行输入内容")) {
                        if (it[0].isNotEmpty()) {
                            setTexture(it[0])
                        }
                        Editor.open(player, entity)
                    }
                }
                .display { _, entity, meta ->
                    if (gameProfile.textureName.isEmpty()) "§7_" else Editor.toSimple(gameProfile.textureName)
                }
    }

    override fun visible(viewer: Player, visible: Boolean) {
        if (visible) {
            addPlayerInfo(viewer)
            spawn(viewer) {
                NMS.INSTANCE.spawnNamedEntity(viewer, index, playerUUID, position.toLocation())
            }
            Tasks.delay(1) {
                if (isHideFromTabList) {
                    removePlayerInfo(viewer)
                }
            }
            Tasks.delay(5) {
                if (isSleepingLegacy) {
                    setSleeping(true)
                }
            }
        } else {
            removePlayerInfo(viewer)
            destroy(viewer) {
                NMS.INSTANCE.destroyEntity(viewer, index)
            }
        }
    }

    fun setName(name: String) {
        gameProfile.name = name
        respawn()
    }

    fun getName(): String {
        return gameProfile.name
    }

    fun setPing(ping: Int) {
        gameProfile.ping = ping
        respawn()
    }

    fun getPing(): Int {
        return gameProfile.ping
    }

    fun setGameMode(gameMode: GameMode) {
        gameProfile.gameMode = gameMode
        respawn()
    }

    fun getGameMode(): GameMode {
        return gameProfile.gameMode
    }

    fun setTexture(name: String) {
        gameProfile.textureName = name
        Tasks.task(true) {
            try {
                MojangAPI.get(name)?.run {
                    setTexture(value, signature)
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    fun setTexture(texture: String, signature: String) {
        gameProfile.texture = arrayOf(texture, signature)
        respawn()
    }

    fun getTexture(): Array<String> {
        return gameProfile.texture
    }

    fun getTextureName(): String {
        return gameProfile.textureName
    }

    fun resetTexture() {
        gameProfile.texture = arrayOf("")
        respawn()
    }

    fun setSkinCapeEnabled(value: Boolean) {
        setMetadata("skinCapeEnabled", value)
    }

    fun isSkinCapeEnabled() {
        return getMetadata("skinCapeEnabled")
    }

    fun setSkinJacketEnabled(value: Boolean) {
        setMetadata("skinJacketEnabled", value)
    }

    fun isSkinJacketEnabled() {
        return getMetadata("skinJacketEnabled")
    }

    fun setSkinLeftSleeveEnabled(value: Boolean) {
        setMetadata("skinLeftSleeveEnabled", value)
    }

    fun isSkinLeftSleeveEnabled() {
        return getMetadata("skinLeftSleeveEnabled")
    }

    fun setSkinRightSleeveEnabled(value: Boolean) {
        setMetadata("skinRightSleeveEnabled", value)
    }

    fun isSkinRightSleeveEnabled() {
        return getMetadata("skinRightSleeveEnabled")
    }

    fun setSkinLeftPantsEnabled(value: Boolean) {
        setMetadata("skinLeftPantsEnabled", value)
    }

    fun isSkinLeftPantsEnabled() {
        return getMetadata("skinLeftPantsEnabled")
    }

    fun setSkinRightPantsEnabled(value: Boolean) {
        setMetadata("skinRightPantsEnabled", value)
    }

    fun isSkinRightPantsEnabled() {
        return getMetadata("skinRightPantsEnabled")
    }

    fun setSkinHatEnabled(value: Boolean) {
        setMetadata("skinHatEnabled", value)
    }

    fun isSkinHatEnabled() {
        return getMetadata("skinHatEnabled")
    }

    fun setSleeping(value: Boolean) {
        if (value) {
            if (version >= 11400) {
                setPose(BukkitPose.SLEEPING)
            } else {
                forViewers {
                    NMS.INSTANCE.sendPlayerSleeping(it, index, position.toLocation())
                }
            }
        } else {
            if (version >= 11400) {
                setPose(BukkitPose.STANDING)
            } else {
                displayAnimation(BukkitAnimation.LEAVE_BED)
            }
            teleport(position)
        }
        isSleepingLegacy = value
    }

    fun isSleeping(): Boolean {
        return if (version >= 11400) {
            getPose() == BukkitPose.SLEEPING
        } else {
            isSleepingLegacy
        }
    }

    private fun addPlayerInfo(viewer: Player) {
        NMS.INSTANCE.addPlayerInfo(viewer, playerUUID, gameProfile.name, gameProfile.ping, gameProfile.gameMode, gameProfile.texture)
    }

    private fun removePlayerInfo(viewer: Player) {
        NMS.INSTANCE.removePlayerInfo(viewer, playerUUID)
    }
}