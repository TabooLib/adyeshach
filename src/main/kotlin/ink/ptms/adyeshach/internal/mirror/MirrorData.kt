package ink.ptms.adyeshach.internal.mirror

import java.util.concurrent.TimeUnit

class MirrorData {

    var timeTotal = 0.0
        private set
    var timeLatest = 0.0
        private set
    var times: Long = 0
        private set

    private var runtime: Long = 0

    fun start(): MirrorData {
        runtime = System.nanoTime()
        return this
    }

    fun stop(): MirrorData {
        timeLatest = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - runtime).toDouble()
        timeTotal += timeLatest
        times += 1
        return this
    }

    fun reset(): MirrorData {
        timeTotal = 0.0
        timeLatest = 0.0
        times = 0
        return this
    }

    fun eval(runnable: Runnable): MirrorData {
        start()
        try {
            runnable.run()
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