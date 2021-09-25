package ink.ptms.adyeshach.common.script.action

import com.google.common.base.Enums
import ink.ptms.adyeshach.common.bukkit.*
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.editor.Editors
import ink.ptms.adyeshach.common.entity.EntityVillager
import ink.ptms.adyeshach.common.entity.type.*
import ink.ptms.adyeshach.common.script.ScriptHandler
import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import ink.ptms.adyeshach.common.script.ScriptHandler.loadError
import ink.ptms.zaphkiel.ZaphkielAPI
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.entity.Horse
import org.bukkit.entity.TropicalFish
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.EulerAngle
import taboolib.common.util.Vector
import taboolib.common5.Coerce
import taboolib.library.xseries.XMaterial
import taboolib.module.kether.*
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionMeta(val key: String, val symbol: Symbol, val value: String?): ScriptAction<Void>() {

    val zaphkielHook = Bukkit.getPluginManager().isPluginEnabled("Zaphkiel")

    enum class Symbol {

        SET, RESET
    }

    fun getItem(item: String): ItemStack {
        return if (zaphkielHook) {
            ZaphkielAPI.getItemStack(item) ?: ItemStack(Material.STONE)
        } else {
            XMaterial.matchXMaterial(item).orElse(XMaterial.STONE).parseItem()!!
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val s = frame.script()
        if (s.getManager() == null) {
            error("No manager selected.")
        }
        if (!s.entitySelected()) {
            error("No entity selected.")
        }
        s.getEntities()!!.filterNotNull().forEach {
            val meta = it.listMetadata().firstOrNull { m -> m.key.equals(key, true) } ?: error("Metadata \"${key}\" not found.")
            val editor = Editor.getEditor(meta) ?: error("Metadata is not editable. (0)")
            if (!editor.edit) {
                error("Metadata is not editable. (1)")
            }
            when (symbol) {
                Symbol.SET -> {
                    when {
                        meta.key == "entityPose" -> {
                            it.setPose(Enums.getIfPresent(BukkitPose::class.java, value.toString().uppercase(Locale.getDefault())).get())
                        }
                        meta.key == "modelEngineName" -> {
                            it.modelEngineName = value.toString()
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
                                    Enums.getIfPresent(Villager.Type::class.java, value.toString().uppercase(Locale.getDefault())).get(),
                                    data.profession
                                )
                            )
                        }
                        meta.key == "villagerProfession" && it is EntityVillager -> {
                            val data = it.getVillagerData()
                            it.setVillagerData(
                                VillagerData(
                                    data.type,
                                    Enums.getIfPresent(Villager.Profession::class.java, value.toString().uppercase(Locale.getDefault())).get()
                                )
                            )
                        }
                        meta.key == "villagerProfessionLegacy" && it is EntityVillager -> {
                            it.setLegacyProfession(BukkitProfession.valueOf(value.toString().uppercase(Locale.getDefault())))
                        }
                        meta.key == "paintingPainting" && it is AdyPainting -> {
                            it.setPainting(BukkitPaintings.valueOf(value.toString().uppercase(Locale.getDefault())))
                        }
                        meta.key == "paintingDirection" && it is AdyPainting -> {
                            it.setDirection(BukkitDirection.valueOf(value.toString().uppercase(Locale.getDefault())))
                        }
                        meta.key == "patternColor" && it is AdyTropicalFish -> {
                            it.setPatternColor(DyeColor.valueOf(value.toString().uppercase(Locale.getDefault())))
                        }
                        meta.key == "bodyColor" && it is AdyTropicalFish -> {
                            it.setBodyColor(DyeColor.valueOf(value.toString().uppercase(Locale.getDefault())))
                        }
                        meta.key == "pattern" && it is AdyTropicalFish -> {
                            it.setPattern(TropicalFish.Pattern.valueOf(value.toString().uppercase(Locale.getDefault())))
                        }
                        meta.key == "horseColor" && it is AdyHorse -> {
                            it.setColor(Horse.Color.valueOf(value.toString().uppercase(Locale.getDefault())))
                        }
                        meta.key == "horseStyle" && it is AdyHorse -> {
                            it.setStyle(Horse.Style.valueOf(value.toString().uppercase(Locale.getDefault())))
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
                                ?: error("Enum type \"${value}\" not found.")
                            it.setMetadata(meta.key, (enum as Enum<*>).ordinal)
                        }
                        else -> {
                            when (meta.def.javaClass.kotlin) {
                                Int::class, Byte::class, Float::class, Double::class, String::class, TextComponent::class -> it.setMetadata(
                                    meta.key,
                                    value.toString()
                                )
                                Boolean::class -> it.setMetadata(meta.key, Coerce.toBoolean(value))
                                Vector::class -> it.setMetadata(meta.key, ScriptHandler.toVector(value.toString()))
                                EulerAngle::class -> it.setMetadata(meta.key, ScriptHandler.toEulerAngle(value.toString()))
                                ItemStack::class -> it.setMetadata(meta.key, getItem(value.toString()))
                                MaterialData::class -> it.setMetadata(meta.key, getItem(value.toString()).data!!)
                                BukkitParticles::class -> it.setMetadata(
                                    meta.key,
                                    Enums.getIfPresent(BukkitParticles::class.java, value.toString().uppercase(Locale.getDefault())).get()
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

    internal object Parser {

        @KetherParser(["meta"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            val symbol = when (val type = it.nextToken()) {
                "set" -> Symbol.SET
                "reset" -> Symbol.RESET
                else -> throw loadError("Unknown meta operator $type")
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