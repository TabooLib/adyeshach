package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.manager.ManagerType
import ink.ptms.adyeshach.core.event.AdyeshachEntityCreateEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.metrics.Metrics
import taboolib.module.metrics.charts.AdvancedPie
import taboolib.module.metrics.charts.SingleLineChart
import taboolib.platform.BukkitPlugin

object PluginMetrics {

    lateinit var metrics: Metrics
        private set

    private var createdEntities = 0

    @Awake(LifeCycle.ACTIVE)
    private fun init() {
        metrics = Metrics(8827, BukkitPlugin.getInstance().description.version, Platform.BUKKIT)
        metrics.addCustomChart(SingleLineChart("entities") {
            val sizePublic = Adyeshach.api().getPublicEntityManager(ManagerType.PERSISTENT).getEntities().size
            val sizePublicTemporary = Adyeshach.api().getPublicEntityManager(ManagerType.TEMPORARY).getEntities().size
            sizePublic + sizePublicTemporary
        })
        metrics.addCustomChart(SingleLineChart("scripts") {
            DefaultScriptManager.workspace.scripts.size
        })
        metrics.addCustomChart(SingleLineChart("entity_created") {
            createdEntities
        })
        metrics.addCustomChart(AdvancedPie("entity_types") {
            val map = HashMap<String, Int>()
            Adyeshach.api().getPublicEntityManager(ManagerType.PERSISTENT).getEntities().forEach {
                map[it.entityType.name] = (map[it.entityType.name] ?: 0) + 1
            }
            Adyeshach.api().getPublicEntityManager(ManagerType.TEMPORARY).getEntities().forEach {
                map[it.entityType.name] = (map[it.entityType.name] ?: 0) + 1
            }
            map
        })
    }

    @SubscribeEvent
    private fun onCreate(e: AdyeshachEntityCreateEvent) {
        createdEntities++
    }
}