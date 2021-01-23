package ink.ptms.adyeshach.common.script.action.npc

import com.google.common.base.Enums
import ink.ptms.adyeshach.common.bukkit.*
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityVillager
import ink.ptms.adyeshach.common.entity.type.*
import ink.ptms.adyeshach.common.script.ScriptContext
import ink.ptms.adyeshach.common.script.ScriptHandler
import ink.ptms.adyeshach.common.script.ScriptParser
import ink.ptms.cronus.Cronus
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.util.LocalizedException
import io.izzel.taboolib.module.nms.impl.Position
import io.izzel.taboolib.util.Coerce
import io.izzel.taboolib.util.chat.TextComponent
import io.izzel.taboolib.util.item.Items
import org.bukkit.Bukkit
import org.bukkit.DyeColor
import org.bukkit.entity.Horse
import org.bukkit.entity.TropicalFish
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionMeta(val key: String, val symbol: Symbol, val value: String?) : QuestAction<Void>() {

    val cronusHook = Bukkit.getPluginManager().isPluginEnabled("Cronus")

    enum class Symbol {

        SET, RESET
    }

    fun getItem(item: String): ItemStack {
        return if (cronusHook) {
            Cronus.getCronusService().itemStorage.getItem(item)
        } else {
            ItemStack(Items.asMaterial(item)!!)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val s = (context.context() as ScriptContext)
        if (s.manager == null) {
            throw RuntimeException("No manager selected.")
        }
        if (!s.entitySelected()) {
            throw RuntimeException("No entity selected.")
        }
        s.entities!!.filterNotNull().forEach {
            val meta = it.listMetadata().firstOrNull { m -> m.key.equals(key, true) } ?: throw RuntimeException("Metadata \"${key}\" not found.")
            val editor = Editor.getEditor(meta) ?: throw RuntimeException("Metadata is not editable. (0)")
            if (!editor.edit) {
                throw RuntimeException("Metadata is not editable. (1)")
            }
            when (symbol) {
                Symbol.SET -> {
                    when {
                        meta.key == "entityPose" -> {
                            it.setPose(Enums.getIfPresent(BukkitPose::class.java, value.toString().toUpperCase()).get())
                        }
                        meta.key == "visibleDistance" -> {
                            it.visibleDistance = Coerce.toDouble(value)
                        }
                        meta.key == "alwaysVisible" -> {
                            it.alwaysVisible = Coerce.toBoolean(value)
                        }
                        meta.key == "moveSpeed" -> {
                            it.moveSpeed = Coerce.toDouble(value)
                        }
                        meta.key == "isDie" && it is AdyEntityLiving -> {
                            it.die(Coerce.toBoolean(value))
                        }
                        meta.key == "isAngered" && it is AdyBee -> {
                            it.setAngered(Coerce.toBoolean(value))
                        }
                        meta.key == "isSleepingLegacy" && it is AdyHuman -> {
                            it.setSleeping(Coerce.toBoolean(value))
                        }
                        meta.key == "isHideFromTabList" && it is AdyHuman -> {
                            it.isHideFromTabList = Coerce.toBoolean(value)
                        }
                        meta.key == "playerName" && it is AdyHuman -> {
                            it.setName(value.toString())
                        }
                        meta.key == "playerPing" && it is AdyHuman -> {
                            it.setPing(Coerce.toInteger(value))
                        }
                        meta.key == "playerTexture" && it is AdyHuman -> {
                            it.setTexture(value.toString())
                        }
                        meta.key == "villagerType" && it is EntityVillager -> {
                            val data = it.getVillagerData()
                            it.setVillagerData(
                                VillagerData(
                                    Enums.getIfPresent(Villager.Type::class.java, value.toString().toUpperCase()).get(),
                                    data.profession
                                )
                            )
                        }
                        meta.key == "villagerProfession" && it is EntityVillager -> {
                            val data = it.getVillagerData()
                            it.setVillagerData(
                                VillagerData(
                                    data.type,
                                    Enums.getIfPresent(Villager.Profession::class.java, value.toString().toUpperCase()).get()
                                )
                            )
                        }
                        meta.key == "villagerProfessionLegacy" && it is EntityVillager -> {
                            it.setLegacyProfession(BukkitProfession.valueOf(value.toString().toUpperCase()))
                        }
                        meta.key == "paintingPainting" && it is AdyPainting -> {
                            it.setPainting(BukkitPaintings.valueOf(value.toString().toUpperCase()))
                        }
                        meta.key == "paintingDirection" && it is AdyPainting -> {
                            it.setDirection(BukkitDirection.valueOf(value.toString().toUpperCase()))
                        }
                        meta.key == "patternColor" && it is AdyTropicalFish -> {
                            it.setPatternColor(DyeColor.valueOf(value.toString().toUpperCase()))
                        }
                        meta.key == "bodyColor" && it is AdyTropicalFish -> {
                            it.setBodyColor(DyeColor.valueOf(value.toString().toUpperCase()))
                        }
                        meta.key == "pattern" && it is AdyTropicalFish -> {
                            it.setPattern(TropicalFish.Pattern.valueOf(value.toString().toUpperCase()))
                        }
                        meta.key == "horseColor" && it is AdyHorse -> {
                            it.setColor(Horse.Color.valueOf(value.toString().toUpperCase()))
                        }
                        meta.key == "horseStyle" && it is AdyHorse -> {
                            it.setStyle(Horse.Style.valueOf(value.toString().toUpperCase()))
                        }
                        meta.key == "equipmentHelmet" && it is AdyEntityLiving -> {
                            it.setHelmet(getItem(value.toString()))
                        }
                        meta.key == "equipmentChestplate" && it is AdyEntityLiving -> {
                            it.setChestplate(getItem(value.toString()))
                        }
                        meta.key == "equipmentLeggings" && it is AdyEntityLiving -> {
                            it.setLeggings(getItem(value.toString()))
                        }
                        meta.key == "equipmentBoots" && it is AdyEntityLiving -> {
                            it.setBoots(getItem(value.toString()))
                        }
                        meta.key == "equipmentHand" && it is AdyEntityLiving -> {
                            it.setItemInMainHand(getItem(value.toString()))
                        }
                        meta.key == "equipmentOffhand" && it is AdyEntityLiving -> {
                            it.setItemInOffHand(getItem(value.toString()))
                        }
                        meta.key == "amount" && it is AdyExperienceOrb -> {
                            it.amount = Coerce.toInteger(value)
                        }
                        meta.key == "block" && it is AdyFallingBlock -> {
                            val item = getItem(value.toString())
                            it.setMaterial(item.type, item.durability.toByte())
                        }
                        meta.key == "block" && it is AdyMinecart -> {
                            it.setCustomBlock(getItem(value.toString()).data!!)
                        }
                        editor.enum != null -> {
                            val enum = Editors.getEnums(editor.enum!!).firstOrNull { e -> e.toString().equals(value.toString(), true) }
                                ?: throw RuntimeException("Enum type \"${value}\" not found.")
                            it.setMetadata(meta.key, (enum as Enum<*>).ordinal)
                        }
                        else -> {
                            when (meta.def.javaClass.kotlin) {
                                Int::class, Byte::class, Float::class, Double::class, String::class, TextComponent::class -> it.setMetadata(
                                    meta.key,
                                    value.toString()
                                )
                                Boolean::class -> it.setMetadata(meta.key, Coerce.toBoolean(value))
                                Position::class -> it.setMetadata(meta.key, ScriptHandler.toPosition(value.toString()))
                                EulerAngle::class -> it.setMetadata(meta.key, ScriptHandler.toEulerAngle(value.toString()))
                                ItemStack::class -> it.setMetadata(meta.key, getItem(value.toString()))
                                MaterialData::class -> it.setMetadata(meta.key, getItem(value.toString()).data!!)
                                BukkitParticles::class -> it.setMetadata(
                                    meta.key,
                                    Enums.getIfPresent(BukkitParticles::class.java, value.toString().toUpperCase()).get()
                                )
                            }
                        }
                    }
                }
                Symbol.RESET -> {
                    if (meta.editor?.onReset != null) {
                        meta.editor!!.onReset!!.invoke(it, meta)
                    } else {
                        it.setMetadata(meta.key, meta.def)
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionMeta(key='$key', symbol=$symbol, value=$value)"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            val symbol = when (val type = it.nextToken()) {
                "set" -> Symbol.SET
                "reset" -> Symbol.RESET
                else -> throw LocalizedException.of("not-tag-method", type)
            }
            val key = it.nextToken()
            val value = if (symbol == Symbol.SET) {
                it.expect("to")
                it.nextToken()
            } else null
            ActionMeta(key, symbol, value)
        }
    }
}