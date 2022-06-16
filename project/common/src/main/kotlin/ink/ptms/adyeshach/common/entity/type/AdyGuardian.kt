package ink.ptms.adyeshach.common.entity.type

/**
 * @author sky
 * @date 2020/8/4 23:15
 */
abstract class AdyGuardian : AdyMob() {

    fun setRetractingSpikes(value: Boolean) {
        setMetadata("isRetractingSpikes", value)
    }

    fun isRetractingSpikes(): Boolean {
        return getMetadata("isRetractingSpikes")
    }

    fun setTargetEntityId(id: Int) {
        setMetadata("targetEntityId", id)
    }

    fun getTargetEntityId(): Int {
        return getMetadata("targetEntityId")
    }

    /**
     * 低版本方法
     */
    fun setElderly(value: Boolean) {
        assert(minecraftVersion >= 11100, "setElderly", "ELDER_GUARDIAN")
        setMetadata("isElderly", value)
    }

    /**
     * 低版本方法
     */
    fun isElderly(): Boolean {
        assert(minecraftVersion >= 11100, "isElderly", "ELDER_GUARDIAN")
        return getMetadata("isElderly")
    }
}