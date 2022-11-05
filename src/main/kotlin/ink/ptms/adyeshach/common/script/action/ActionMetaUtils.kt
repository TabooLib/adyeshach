package ink.ptms.adyeshach.common.script.action

import com.google.common.base.Enums
import ink.ptms.adyeshach.common.bukkit.*
import ink.ptms.adyeshach.common.bukkit.data.VillagerData
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityVillager
import ink.ptms.adyeshach.common.entity.Meta
import ink.ptms.adyeshach.common.entity.type.*
import ink.ptms.adyeshach.common.script.ScriptHandler
import ink.ptms.adyeshach.internal.findEnum
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
import org.bukkit.util.Vector
import taboolib.common.util.unsafeLazy
import taboolib.common5.Coerce
import taboolib.library.xseries.XMaterial

fun EntityInstance.setMeta(meta: Meta<*>, value: String) {
    when {
        // 实体姿势
        meta.key.equals("entityPose", true) -> {
            setPose(Enums.getIfPresent(BukkitPose::class.java, value.uppercase()).get())
        }
        // 模型
        meta.key.equals("modelEngine", true) -> {
            modelEngineName = value
        }
        // 可视距离
        meta.key.equals("visibleDistance", true) -> {
            visibleDistance = Coerce.toDouble(value)
        }
        // 加载后可见
        meta.key.equals("visibleAfterLoaded", true) -> {
            visibleAfterLoaded = Coerce.toBoolean(value)
        }
        // 移动速度
        meta.key.equals("moveSpeed", true) -> {
            moveSpeed = Coerce.toDouble(value)
        }
        // 死亡
        meta.key.equals("isDie", true) && this is AdyEntityLiving -> {
            die(Coerce.toBoolean(value))
        }
        // 愤怒
        meta.key.equals("isAngered", true) && this is AdyBee -> {
            setAngered(Coerce.toBoolean(value))
        }
        // 睡眠
        meta.key.equals("isSleepingLegacy", true) && this is AdyHuman -> {
            setSleeping(Coerce.toBoolean(value))
        }
        // 从 Tab 中隐藏
        meta.key.equals("isHideFromTabList", true) && this is AdyHuman -> {
            isHideFromTabList = Coerce.toBoolean(value)
        }
        // 玩家名称
        meta.key.equals("playerName", true) && this is AdyHuman -> {
            setName(value)
        }
        // 玩家延迟
        meta.key.equals("playerPing", true) && this is AdyHuman -> {
            setPing(Coerce.toInteger(value))
        }
        // 玩家皮肤
        meta.key.equals("playerTexture", true) && this is AdyHuman -> {
            setTexture(value)
        }
        // 类型
        meta.key.equals("paintingPainting", true) && this is AdyPainting -> {
            setPainting(BukkitPaintings.valueOf(value.uppercase()))
        }
        // 朝向
        meta.key.equals("paintingDirection", true) && this is AdyPainting -> {
            setDirection(BukkitDirection.valueOf(value.uppercase()))
        }
        // 花纹
        meta.key.equals("patternColor", true) && this is AdyTropicalFish -> {
            setPatternColor(DyeColor.valueOf(value.uppercase()))
        }
        // 颜色
        meta.key.equals("bodyColor", true) && this is AdyTropicalFish -> {
            setBodyColor(DyeColor.valueOf(value.uppercase()))
        }
        // 花纹
        meta.key.equals("pattern", true) && this is AdyTropicalFish -> {
            setPattern(TropicalFish.Pattern.valueOf(value.uppercase()))
        }
        // 颜色
        meta.key.equals("horseColor", true) && this is AdyHorse -> {
            setColor(Horse.Color.valueOf(value.uppercase()))
        }
        // 样式
        meta.key.equals("horseStyle", true) && this is AdyHorse -> {
            setStyle(Horse.Style.valueOf(value.uppercase()))
        }
        // 头
        meta.key.equals("equipmentHelmet", true) && this is AdyEntityLiving -> {
            setHelmet(getItem(value))
        }
        // 胸甲
        meta.key.equals("equipmentChestplate", true) && this is AdyEntityLiving -> {
            setChestplate(getItem(value))
        }
        // 腿甲
        meta.key.equals("equipmentLeggings", true) && this is AdyEntityLiving -> {
            setLeggings(getItem(value))
        }
        // 靴子
        meta.key.equals("equipmentBoots", true) && this is AdyEntityLiving -> {
            setBoots(getItem(value))
        }
        // 主手
        meta.key.equals("equipmentHand", true) && this is AdyEntityLiving -> {
            setItemInMainHand(getItem(value))
        }
        // 副手
        meta.key.equals("equipmentOffhand", true) && this is AdyEntityLiving -> {
            setItemInOffHand(getItem(value))
        }
        // 数量
        meta.key.equals("amount", true) && this is AdyExperienceOrb -> {
            amount = Coerce.toInteger(value)
        }
        // 方块类型
        meta.key.equals("block", true) && this is AdyFallingBlock -> {
            val item = getItem(value)
            setMaterial(item.type, item.durability.toByte())
        }
        // 方块类型
        meta.key.equals("block", true) && this is AdyMinecart -> {
            setCustomBlock(getItem(value).data!!)
        }
        // 村民职业
        meta.key.equals("villagerProfessionLegacy", true) && this is EntityVillager -> {
            setLegacyProfession(BukkitProfession.valueOf(value.uppercase()))
        }
        // 村民类型
        meta.key.equals("villagerType", true) && this is EntityVillager -> {
            val data = getVillagerData()
            setVillagerData(VillagerData(Enums.getIfPresent(Villager.Type::class.java, value.uppercase()).get(), data.profession))
        }
        // 村民职业
        meta.key.equals("villagerProfession", true) && this is EntityVillager -> {
            val data = getVillagerData()
            setVillagerData(VillagerData(data.type, Enums.getIfPresent(Villager.Profession::class.java, value.uppercase()).get()))
        }
        // 枚举
        meta.editor!!.enumType != null -> {
            setMetadata(meta.key, meta.editor!!.enumType!!.findEnum(value).ordinal)
        }
        // 自动类型转换
        else -> {
            when (meta.def.javaClass.kotlin) {
                Int::class, Byte::class, Float::class, Double::class, String::class, TextComponent::class -> setMetadata(meta.key, value)
                Boolean::class -> setMetadata(meta.key, Coerce.toBoolean(value))
                Vector::class -> setMetadata(meta.key, ScriptHandler.toVector(value))
                EulerAngle::class -> setMetadata(meta.key, ScriptHandler.toEulerAngle(value))
                ItemStack::class -> setMetadata(meta.key, getItem(value))
                MaterialData::class -> setMetadata(meta.key, getItem(value).data!!)
                BukkitParticles::class -> setMetadata(meta.key, Enums.getIfPresent(BukkitParticles::class.java, value.uppercase()).get())
            }
        }
    }
}

private val zaphkielHook by unsafeLazy { Bukkit.getPluginManager().isPluginEnabled("Zaphkiel") }

private fun getItem(item: String): ItemStack {
    return if (zaphkielHook) {
        ZaphkielAPI.getItemStack(item) ?: ItemStack(Material.STONE)
    } else {
        XMaterial.matchXMaterial(item).orElse(XMaterial.STONE).parseItem()!!
    }
}