package ink.ptms.adyeshach.module.editor.meta

import ink.ptms.adyeshach.core.bukkit.*
import ink.ptms.adyeshach.core.bukkit.data.VillagerData
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.Meta
import ink.ptms.adyeshach.core.entity.type.*
import ink.ptms.adyeshach.module.editor.EditType
import ink.ptms.adyeshach.module.editor.meta.impl.*
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.meta.MetaEditor
 *
 * @author 坏黑
 * @since 2022/12/27 03:49
 */
interface MetaEditor {

    fun open(entity: EntityInstance, player: Player, def: String)

    companion object {

        /**
         * 获取固定类型的编辑器
         */
        fun getMetaEditor(type: EditType, key: String): MetaEditor {
            return when (type) {
                EditType.SIGN -> MetaPrimitive(key)
                EditType.EQUIPMENT -> MetaEquipment()
                EditType.FALLING_BLOCK -> MetaItem.FallingBlock()
                EditType.CHAT_COLOR -> MetaEnum(key, ChatColor::class.java).nameToKey()
                else -> error("Unsupported type: $type")
            }
        }

        /**
         * 通过元数据获取编辑器
         */
        fun getMetaEditor(meta: Meta<*>): MetaEditor? {
            return when (meta.def) {
                // 基础类型
                is Int, is Byte, is Float, is String -> MetaPrimitive(meta.key)
                // 文本类型
                is TextComponent -> MetaText(meta.key)
                // 材质相关
                is ItemStack -> MetaItem(meta.key)
                is MaterialData -> MetaItem.Mat(meta.key)
                // 村民
                is VillagerData -> MetaVillager()
                // 不支持
                else -> null
            }
        }

        /**
         * 获取自定义元数据编辑器
         */
        fun getCustomMetaEditor(entity: EntityInstance, key: String): MetaEditor? {
            return when {
                // 兔子
                entity is AdyRabbit && key == "type" -> MetaEnum(key, Rabbit.Type::class.java)
                // 鹦鹉
                entity is AdyParrot && key == "color" -> MetaEnum(key, Parrot.Variant::class.java)
                // 咒术师
                entity is AdySpellcasterIllager && key == "spell" -> MetaEnum(key, Spellcaster.Spell::class.java)
                // 末影龙
                entity is AdyEndDragon && key == "dragonPhase" -> MetaEnum(key, EnderDragon.Phase::class.java)
                // 狼
                entity is AdyWolf && key == "collarColor" -> MetaEnum(key, DyeColor::class.java)
                // 爬行者
                entity is AdyCreeper && key == "state" -> MetaEnum(key, BukkitCreeperState::class.java)
                // 草泥马
                entity is AdyLlama && key == "carpetColor" -> MetaEnum(key, DyeColor::class.java)
                entity is AdyLlama && key == "color" -> MetaEnum(key, Llama.Color::class.java)
                // 猫
                entity is AdyCat && key == "type" -> MetaEnum(key, Cat.Type::class.java).nameToKey()
                entity is AdyCat && key == "color" -> MetaEnum(key, DyeColor::class.java)
                // 船
                entity is AdyBoat && key == "type" -> MetaEnum(key, BukkitBoat::class.java)
                // 潜影壳
                entity is AdyShulker && key == "attachPosition" -> MetaVector(key)
                entity is AdyShulker && key == "attachFace" -> MetaEnum(key, BukkitDirection::class.java)
                entity is AdyShulker && key == "color" -> MetaEnum(key, DyeColor::class.java)
                // 羊
                entity is AdySheep && key == "dyeColor" -> MetaEnum(key, DyeColor::class.java).nameToKey()
                // 豹猫
                entity is AdyOcelot && key == "type" -> MetaEnum(key, BukkitOcelotType::class.java)
                // 狐狸
                entity is AdyFox && key == "type" -> MetaEnum(key, Fox.Type::class.java)
                // 村民
                entity is AdyVillager && key == "profession" -> MetaEnum(key, BukkitProfession::class.java)
                // 蘑菇
                entity is AdyMushroom && key == "type" -> MetaEnum(key, MushroomCow.Variant::class.java, lowercase = true).nameToKey()
                // 热带鱼
                entity is AdyTropicalFish && key == "bodyColor" -> MetaEnum(key, DyeColor::class.java).nameToKey()
                entity is AdyTropicalFish && key == "patternColor" -> MetaEnum(key, DyeColor::class.java).nameToKey()
                entity is AdyTropicalFish && key == "pattern" -> MetaEnum(key, TropicalFish.Pattern::class.java).nameToKey()
                // 药水云
                entity is AdyAreaEffectCloud && key == "particle" -> MetaEnum(key, BukkitParticles::class.java).nameToKey()
                entity is AdyAreaEffectCloud && key == "color" -> MetaColor(key)
                // 画
                entity is AdyPainting && key == "painting" -> MetaEnum(key, BukkitPaintings::class.java).nameToKey()
                entity is AdyPainting && key == "direction" -> MetaEnum(key, BukkitDirection::class.java).nameToKey()
                // 马
                entity is AdyHorse && key == "color" -> MetaEnum(key, Horse.Color::class.java).nameToKey()
                entity is AdyHorse && key == "style" -> MetaEnum(key, Horse.Style::class.java).nameToKey()
                // 箭
                entity is AdyArrow && key == "color" -> MetaColor(key)
                // 生物
                entity is AdyEntityLiving && key == "potionEffectColor" -> MetaColor(key)
                // 矿车
                entity is AdyMinecart && key == "customBlock" -> MetaItem.Minecart()
                // 水晶
                entity is AdyEndCrystal && key == "beamTarget" -> MetaVector(key)
                // 青蛙
                entity is AdyFrog && key == "frogVariant" -> MetaEnum(key, Frog.Variant::class.java).nameToKey()
                // 展示实体
                entity is AdyDisplay && key == "glowColorOverride" -> MetaColor(key)
                entity is AdyTextDisplay && key == "backgroundColor" -> MetaColor(key)
                // 嗅探兽
                entity is AdySniffer && key == "snifferState" -> MetaEnum(key, AdySniffer.State::class.java).nameToKey()
                // 实体
                key == "pose" -> MetaEnum(key, BukkitPose::class.java).nameToKey()
                // 不支持
                else -> null
            }
        }
    }
}