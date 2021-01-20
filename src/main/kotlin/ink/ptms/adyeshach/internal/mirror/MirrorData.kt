package ink.ptms.adyeshach.internal.mirror

import java.math.BigDecimal
import java.math.RoundingMode

class MirrorData {

    private var startTime = 0L
    private var stopTime = 0L

    lateinit var timeTotal: BigDecimal
        private set
    lateinit var timeLatest: BigDecimal
        private set
    lateinit var timeLowest: BigDecimal
        private set
    lateinit var timeHighest: BigDecimal
        private set

    var count = 0L
        private set

    init {
        reset()
    }

    fun define(): MirrorData {
        startTime = System.nanoTime()
        return this
    }

    fun finish(): MirrorData {
        stopTime = System.nanoTime()
        // 当前
        timeLatest = BigDecimal((stopTime - startTime) / 1000000.0).setScale(2, RoundingMode.HALF_UP)
        // 总计
        timeTotal = timeTotal.add(timeLatest)
        // 最高值
        if (timeLatest.compareTo(timeHighest) == 1) {
            timeHighest = timeLatest
        }
        // 最低值
        if (timeLatest.compareTo(timeLowest) == -1) {
            timeLowest = timeLatest
        }
        count++
        return this
    }

    fun check(func: () -> Unit) {
        define()
        try {
            func.invoke()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        finish()
    }

    fun reset(): MirrorData {
        timeTotal = BigDecimal.ZERO
        timeLatest = BigDecimal.ZERO
        timeLowest = BigDecimal.ZERO
        timeHighest = BigDecimal.ZERO
        count = 0
        return this
    }

    fun getTotal(): Double {
        return timeTotal.toDouble()
    }

    fun getLatest(): Double {
        return timeLatest.toDouble()
    }

    fun getHighest(): Double {
        return timeHighest.toDouble()
    }

    fun getLowest(): Double {
        return timeLowest.toDouble()
    }

    fun getAverage(): Double {
        return timeTotal.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP).toDouble()
    }
}