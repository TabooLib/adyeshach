package ink.ptms.adyeshach.internal.mirror

import com.google.common.collect.Maps

/**
 * @Author 坏黑
 * @Since 2018-12-24 16:32
 */
object Mirror {

    val dataMap = Maps.newConcurrentMap<String, MirrorData>()!!

    fun get(id: String, total: Boolean = true): MirrorData {
        return dataMap.computeIfAbsent(id) { MirrorData(total) }
    }
}