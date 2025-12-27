package ink.ptms.adyeshach.compat.modelengine4

/**
 * 动画状态数据类
 * 用于保存和还原动画的播放状态
 *
 * @property animationId 动画 ID
 * @property priority 动画优先级
 * @property lerpIn 动画淡入时间（tick）
 * @property lerpOut 动画淡出时间（tick）
 * @property speed 动画播放速度
 * @property loopMode 动画循环模式
 * @property isForceOverride 是否强制覆盖
 */
data class AnimationState(
    val animationId: String,
    val priority: Int = 1,
    val lerpIn: Int = 0,
    val lerpOut: Int = 0,
    val speed: Double = 1.0,
    val loopMode: String = "ONCE",
    val isForceOverride: Boolean = false
) {

    /**
     * 序列化为字符串（格式：animationId:priority:lerpIn:lerpOut:speed:loopMode:isForceOverride）
     */
    fun serialize(): String {
        return "$animationId:$priority:$lerpIn:$lerpOut:$speed:$loopMode:$isForceOverride"
    }

    companion object {

        /**
         * 从字符串反序列化
         */
        fun deserialize(data: String): AnimationState? {
            val parts = data.split(":")
            if (parts.size < 7) return null
            return try {
                AnimationState(
                    animationId = parts[0],
                    priority = parts[1].toInt(),
                    lerpIn = parts[2].toInt(),
                    lerpOut = parts[3].toInt(),
                    speed = parts[4].toDouble(),
                    loopMode = parts[5],
                    isForceOverride = parts[6].toBoolean()
                )
            } catch (_: Exception) {
                null
            }
        }
    }
}