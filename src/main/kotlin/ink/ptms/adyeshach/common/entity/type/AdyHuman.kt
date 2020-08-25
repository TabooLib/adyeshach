package ink.ptms.adyeshach.common.entity.type

import ink.ptms.adyeshach.api.nms.NMS
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
    private var hideFromTabList = true
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
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinCapeEnabled", 0x01, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinJacketEnabled", 0x02, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinLeftSleeveEnabled", 0x04, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinRightSleeveEnabled", 0x08, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinLeftPantsEnabled", 0x10, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinRightPantsEnabled", 0x20, true)
        registerMetaByteMask(at(11500 to 16, 11400 to 15, 11000 to 13, 10900 to 12), "skinHatEnabled", 0x40, true)
        registerEditor("isHideFromTabList")
                .reset { player, entity, meta ->
                    hideFromTabList = true
                }
                .modify { player, entity, meta ->
                    hideFromTabList = !hideFromTabList
                    Editor.open(player, entity)
                }
                .display { _, entity, meta ->
                    hideFromTabList.toDisplay()
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
                    Signs.fakeSign(player, arrayOf(getName(), "", "请在第一行输入内容")) {
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
            if (hideFromTabList) {
                removePlayerInfo(viewer)
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

    private fun addPlayerInfo(viewer: Player) {
        NMS.INSTANCE.addPlayerInfo(viewer, playerUUID, gameProfile.name, gameProfile.ping, gameProfile.gameMode, gameProfile.texture)
    }

    private fun removePlayerInfo(viewer: Player) {
        NMS.INSTANCE.removePlayerInfo(viewer, playerUUID)
    }
}