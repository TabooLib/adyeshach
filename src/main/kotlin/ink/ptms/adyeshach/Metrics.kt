package ink.ptms.adyeshach

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.api.event.AdyeshachEntityCreateEvent
import ink.ptms.adyeshach.common.script.ScriptHandler
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.metrics.Metrics
import taboolib.module.metrics.charts.AdvancedPie
import taboolib.module.metrics.charts.SingleLineChart
import taboolib.platform.BukkitPlugin

object Metrics {

    lateinit var metrics: Metrics
        private set

    private var createdEntities = 0

    @Awake(LifeCycle.ACTIVE)
    fun init() {
        metrics = Metrics(8827, BukkitPlugin.getInstance().description.version, Platform.BUKKIT)
        metrics.addCustomChart(SingleLineChart("entities") {
            val sizePublic = AdyeshachAPI.getEntityManagerPublic().getEntities().size
            val sizePublicTemporary = AdyeshachAPI.getEntityManagerPublicTemporary().getEntities().size
            sizePublic + sizePublicTemporary
        })
        metrics.addCustomChart(SingleLineChart("scripts") {
            ScriptHandler.workspace.scripts.size
        })
        metrics.addCustomChart(SingleLineChart("entity_created") {
            createdEntities
        })
        metrics.addCustomChart(AdvancedPie("entity_types") {
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

    internal object MetricsListener {

        @SubscribeEvent
        fun e(e: AdyeshachEntityCreateEvent) {
            createdEntities++
        }
    }
}