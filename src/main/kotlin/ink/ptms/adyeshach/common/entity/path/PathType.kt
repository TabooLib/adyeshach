package ink.ptms.adyeshach.common.entity.path

import org.bukkit.entity.*
import taboolib.module.nms.MinecraftVersion

/**
 * @Author sky
 * @Since 2020-08-13 16:54
 */
enum class PathType(val height: Double, val width: Double) {

    /**
     * 飞行单位（以 Bee 为代表，需要 1.15+ 版本以上）
     */
    FLY(1.0, 1.0),

    /**
     * 高度接近一格（以 Chicken 为代表）
     */
    WALK_1(1.0, 1.0),

    /**
     * 高度接近两格（以 Creeper 为代表）
     */
    WALK_2(2.0, 1.0),

    /**
     * 高度接近三格（以 Enderman 为代表）
     */
    WALK_3(3.0, 1.0);
}