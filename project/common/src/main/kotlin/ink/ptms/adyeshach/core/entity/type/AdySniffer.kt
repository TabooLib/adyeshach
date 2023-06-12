package ink.ptms.adyeshach.core.entity.type

/**
 * @author sky
 * @since 2020-08-04 19:30
 */
interface AdySniffer : AdyEntityAgeable {

    fun setState(state: State) {
        setMetadata("snifferState", state)
    }

    fun getState(): State {
        return getMetadata("snifferState")
    }

    fun setDropSeedAtTick(dropSeedAtTick: Int) {
        setMetadata("dropSeedAtTick", dropSeedAtTick)
    }

    fun getDropSeedAtTick(): Int {
        return getMetadata("dropSeedAtTick")
    }

    enum class State {

        IDLING, // 空闲

        FELLING_HAPPY, // 开心

        SCENTING, // 嗅探

        SNIFFING, // 嗅探

        SEARCHING, // 搜索

        DIGGING, // 挖掘

        RISING, // 上升
    }
}