package ink.ptms.adyeshach.common.entity.path

/**
 * @author sky
 * @since 2020-08-13 16:54
 */
@Deprecated("Outdated but usable")
enum class PathType(val height: Double, val width: Double) {

    FLY(1.0, 1.0),

    WALK_1(1.0, 1.0),

    WALK_2(2.0, 1.0),

    WALK_3(3.0, 1.0);

    fun v2(): ink.ptms.adyeshach.core.entity.path.PathType {
        return ink.ptms.adyeshach.core.entity.path.PathType.values()[ordinal]
    }
}