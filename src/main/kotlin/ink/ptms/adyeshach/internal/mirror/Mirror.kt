package ink.ptms.adyeshach.internal.mirror

import com.google.common.collect.Maps
import org.bukkit.command.CommandSender
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 * @Author 坏黑
 * @Since 2018-12-24 16:32
 */
object Mirror {

    val dataMap = Maps.newConcurrentMap<String, MirrorData>()!!
    var isEnable = true

    private val disabled = MirrorData()

    fun define(id: String) {
        if (isEnable) {
            dataMap.computeIfAbsent(id) { MirrorData() }.define()
        }
    }

    fun finish(id: String) {
        if (isEnable) {
            dataMap[id]?.finish()
        }
    }

    fun get(id: String): MirrorData {
        return if (isEnable) {
            dataMap.computeIfAbsent(id) { MirrorData() }
        } else {
            disabled
        }
    }

    fun collect(): MirrorCollect {
        val collect = MirrorCollect("/", "/")
        dataMap.entries.forEach { e ->
            var p = collect
            e.key.split(":").forEach {
                p = p.sub.computeIfAbsent(it) { _ -> MirrorCollect(e.key, it) }
            }
        }
        return collect
    }

    class MirrorCollect(val key: String, val path: String, val sub: MutableMap<String, MirrorCollect> = TreeMap()) {

        fun getTotal(): BigDecimal {
            var total = dataMap[key]?.timeTotal ?: BigDecimal.ZERO
            sub.values.forEach {
                total = total.add(it.getTotal())
            }
            return total
        }

        fun print(sender: CommandSender, all: BigDecimal, space: Int) {
            val spaceStr = (1..space).joinToString("") { "···" }
            val total = getTotal()
            val avg = dataMap[key]?.getAverage() ?: 0.0
            if (sub.isEmpty()) {
                sender.sendMessage("§c[System] §8${spaceStr}§f${path} §8[$total ms] §c[$avg ms] §8········· §7${percent(all, total)}%")
            } else {
                sender.sendMessage("§c[System] §8${spaceStr}§7${path} §8[$total ms] §c[$avg ms] §8········· §7${percent(all, total)}%")
            }
            sub.values.map {
                it to percent(all, it.getTotal())
            }.sortedByDescending { it.second }.forEach {
                it.first.print(sender, all, space + 1)
            }
        }

        fun percent(all: BigDecimal, total: BigDecimal): Double {
            return if (all.toDouble() == 0.0) 0.0 else total.divide(all, 2, RoundingMode.HALF_UP).multiply(BigDecimal("100")).toDouble()
        }
    }
}