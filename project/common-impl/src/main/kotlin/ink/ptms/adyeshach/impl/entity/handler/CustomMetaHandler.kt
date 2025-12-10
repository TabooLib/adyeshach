package ink.ptms.adyeshach.impl.entity.handler

import ink.ptms.adyeshach.core.AdyeshachSettings
import ink.ptms.adyeshach.core.bukkit.BukkitPose
import ink.ptms.adyeshach.core.util.getEnum
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import org.bukkit.ChatColor
import taboolib.common5.cbool
import taboolib.common5.cdouble

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.handler.CustomMetaHandler
 *
 * 负责实体的自定义元数据处理
 *
 * @author 坏黑
 * @since 2022/6/19
 */
class CustomMetaHandler(private val self: DefaultEntityInstance) {

    /**
     * 设置自定义元数据
     * @param key 键名
     * @param value 值（null 表示重置为默认值）
     * @return 是否成功处理
     */
    fun setCustomMeta(key: String, value: String?): Boolean {
        return when (key) {
            // 实体姿态
            "pose" -> {
                self.setPose(if (value != null) BukkitPose::class.java.getEnum(value) else BukkitPose.STANDING)
                true
            }
            // 是否傻逼
            "nitwit" -> {
                self.isNitwit = value?.cbool ?: false
                true
            }
            // 移动速度
            "movespeed", "move_speed" -> {
                self.moveSpeed = value?.cdouble ?: 0.2
                true
            }
            // 是否可见名称
            "nametagvisible", "name_tag_visible" -> {
                self.isNameTagVisible = value?.cbool ?: true
                true
            }
            // 是否存在碰撞体积
            "collision", "is_collision" -> {
                self.isCollision = value?.cbool ?: true
                true
            }
            // 发光颜色
            "glowingcolor", "glowing_color" -> {
                self.glowingColor = if (value != null) {
                    if (value.startsWith('§')) {
                        ChatColor.getByChar(value) ?: ChatColor.WHITE
                    } else {
                        ChatColor::class.java.getEnum(value)
                    }
                } else {
                    ChatColor.WHITE
                }
                true
            }
            // 是否可见隐形单位
            "canseeinvisible", "can_see_invisible" -> {
                self.canSeeInvisible = value?.cbool ?: false
                true
            }
            // 可见距离
            "visibledistance", "visible_distance" -> {
                self.visibleDistance = value?.cdouble ?: AdyeshachSettings.visibleDistance
                true
            }
            // 加载后自动显示
            "visibleafterloaded", "visible_after_loaded" -> {
                self.visibleAfterLoaded = value?.cbool ?: true
                true
            }
            // 模型引擎
            "modelenginename", "modelengine_name", "modelengine", "model_engine" -> {
                self.modelEngineName = value ?: ""
                true
            }
            // 冻结
            "freeze", "isfreeze", "is_freeze" -> {
                self.isFreeze = value?.cbool ?: false
                true
            }
            // 其他
            else -> false
        }
    }
}
