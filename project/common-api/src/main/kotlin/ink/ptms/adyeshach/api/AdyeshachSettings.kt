package ink.ptms.adyeshach.api

import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration

object AdyeshachSettings {

    @Config
    lateinit var conf: Configuration
        private set

    enum class SpawnTrigger {

        KEEP_ALIVE, JOIN
    }

    /**
     * 单位生成时机
     * JOIN 表示玩家进入游戏时
     * KEEP_ALIVE 表示当玩家向服务端发送第一个心跳包时
     */
    val spawnTrigger: SpawnTrigger
        get() = try {
            SpawnTrigger.valueOf(conf.getString("Settings.spawn-trigger")!!.uppercase())
        } catch (ignored: Exception) {
            SpawnTrigger.KEEP_ALIVE
        }

    /**
     * 调试模式
     */
    @ConfigNode("Settings.debug")
    var debug = false
        private set

    /**
     * 单位可视距离
     */
    @ConfigNode("Settings.visible-distance")
    var visibleDistance = 64.0
        private set

    /**
     * 主线程寻路
     */
    @ConfigNode("Settings.pathfinder-sync")
    var pathfinderSync = true
        private set

    /**
     * 在未知世界下删除单位
     */
    @ConfigNode("Settings.delete-file-in-unknown-world")
    var deleteFileInUnknownWorld = emptyList<String>()

    @ConfigNode("view-condition-interval")
    var viewConditionInterval = 40
        get() = if (field == 0) 40 else field

    @ConfigNode("Settings.ashcon-api")
    var ashconAPI = true
        private set

    fun isSpecifiedWorld(world: String): Boolean {
        return deleteFileInUnknownWorld.any { if (it.endsWith("?")) world.contains(it.substring(0, it.length - 1)) else world == it }
    }
}