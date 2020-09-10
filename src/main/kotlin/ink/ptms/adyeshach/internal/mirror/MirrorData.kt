package ink.ptms.adyeshach.internal.mirror

import java.util.concurrent.TimeUnit

class MirrorData(val total: Boolean) {

    var timeTotal = 0L
        private set
    var timeLatest = 0L
        private set
    var times = 0L
        private set

    var lowest = 0L
        private set
    var highest = 0L
        private set

    private var runtime: Long = 0

    fun start(): MirrorData {
        runtime = System.nanoTime()
        return this
    }

    fun stop(): MirrorData {
        timeLatest = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - runtime)
        timeTotal += timeLatest
        if (total) {
            times += 1
        }
        if (timeLatest > highest) {
            highest = timeLatest
        }
        if (timeLatest < lowest) {
            lowest = timeLatest
        }
        return this
    }

    fun reset(): MirrorData {
        timeTotal = 0L
        timeLatest = 0L
        times = 0
        return this
    }

    fun eval(runnable: () -> (Any?)): MirrorData {
        start()
        try {
            runnable.invoke()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        stop()
        return this
    }

    override fun toString(): String {
        return "MirrorData(timeTotal=$timeTotal, timeLatest=$timeLatest, times=$times, runtime=$runtime)"
    }
}