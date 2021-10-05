package ink.ptms.adyeshach.common.entity

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.bukkit.BukkitPose
import ink.ptms.adyeshach.common.entity.editor.MetaEditor
import ink.ptms.adyeshach.common.entity.editor.at
import ink.ptms.adyeshach.common.entity.editor.edit
import net.md_5.bungee.api.chat.TextComponent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common5.Coerce
import taboolib.module.nms.MinecraftVersion
import java.util.function.Consumer

@Suppress("UNCHECKED_CAST")
object EntityMetas {

    private val minecraftVersion = MinecraftVersion.majorLegacy

    @Awake(LifeCycle.ENABLE)
    private fun init() {
        // 基本类型
        from<EntityInstance> {
            mask(0, "onFire", 0x01)
            mask(0, "isCrouched", 0x02)
            mask(0, "unUsedRiding", 0x04)
            mask(0, "isSprinting", 0x08)
            mask(0, "isSwimming", 0x010)
            mask(0, "isInvisible", 0x20)
            mask(0, "isGlowing", 0x40)
            mask(0, "isFlyingElytra", 0x80.toByte())
            natural(2, "customName", TextComponent(""))
            natural(3, "isCustomNameVisible", false)
            natural(at(11000 to 5), "noGravity", false)
            natural(at(11400 to 6), "pose", BukkitPose.STANDING) {
                it.reset { _, entity -> entity.setPose(BukkitPose.STANDING) }
                it.display { _, entity -> entity.getPose() }
            }
            natural(at(11700 to 7), "ticksFrozenInPowderedSnow", 0)
            // 可视距离
            naturalEditor("visibleDistance") {
                it.reset { _, entity -> entity.visibleDistance = -1.0 }
                it.modify { player, entity -> player.edit(entity, entity.visibleDistance) { value -> entity.visibleDistance = Coerce.toDouble(value) } }
                it.display { _, entity -> entity.visibleDistance }
            }
            // 移动速度
            naturalEditor("moveSpeed") {
                it.reset { _, entity -> entity.moveSpeed = 0.2 }
                it.modify { player, entity -> player.edit(entity, entity.moveSpeed) { value -> entity.moveSpeed = Coerce.toDouble(value) } }
                it.display { _, entity -> entity.moveSpeed }
            }
            // ModelEngine
            if (AdyeshachAPI.modelEngineHooked) {
                naturalEditor("modelEngine") {
                    it.reset { _, entity -> entity.modelEngineName = "" }
                    it.modify { player, entity -> player.edit(entity, entity.modelEngineName) { value -> entity.modelEngineName = value } }
                    it.display { _, entity -> entity.modelEngineName.ifEmpty { "§7_" } }
                }
            }
        }
    }

    private inline fun <reified T> from(reg: IMetaRegister.() -> Unit) {
        IMetaRegister(T::class.java).also(reg)
    }

    private class IMetaRegister(val useType: Class<*>) {

        fun mask(index: Int, key: String, mask: Byte, def: Boolean = false) {
            AdyeshachAPI.registerEntityMetaMask(useType, index, key, mask, def)
        }

        fun natural(index: Int, key: String, def: Any, editor: Consumer<MetaEditor>? = null) {
            AdyeshachAPI.registerEntityMetaNatural(useType, index, key, def, editor)
        }

        fun naturalEditor(key: String, editor: Consumer<MetaEditor>) {
            AdyeshachAPI.registerEntityMetaNaturalEditor(useType, key, editor)
        }
    }
}