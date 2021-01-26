package ink.ptms.adyeshach

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.event.AdyeshachEntityCreateEvent
import ink.ptms.adyeshach.common.script.ScriptHandler
import io.izzel.taboolib.metrics.BStats
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object Metrics {

    lateinit var metrics: BStats
        private set

    private var createdEntities = 0

    @TSchedule
    fun init() {
        metrics = BStats(Adyeshach.plugin)
        metrics.addCustomChart(BStats.SingleLineChart("entities") {
            val sizePublic = AdyeshachAPI.getEntityManagerPublic().getEntities().size
            val sizePublicTemporary = AdyeshachAPI.getEntityManagerPublicTemporary().getEntities().size
            sizePublic + sizePublicTemporary
        })
        metrics.addCustomChart(BStats.SingleLineChart("scripts") {
            ScriptHandler.workspace.scripts.size
        })
        metrics.addCustomChart(BStats.SingleLineChart("entity_created") {
            createdEntities
        })
        metrics.addCustomChart(BStats.AdvancedPie("entity_types") {
            val map = HashMap<String, Int>()
            AdyeshachAPI.getEntityManagerPublic().getEntities().forEach {
                map[it.entityType.name] = (map[it.entityType.name] ?: 0) + 1
            }
            AdyeshachAPI.getEntityManagerPublicTemporary().getEntities().forEach {
                map[it.entityType.name] = (map[it.entityType.name] ?: 0) + 1
            }
            map
        })
    }

    @TListener
    class MetricsListener : Listener {

        @EventHandler
        fun e(e: AdyeshachEntityCreateEvent) {
            createdEntities++
        }
    }
}