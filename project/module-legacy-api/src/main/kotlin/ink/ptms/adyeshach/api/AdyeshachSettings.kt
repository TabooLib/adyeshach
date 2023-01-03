package ink.ptms.adyeshach.api

@Deprecated("Outdated but usable")
object AdyeshachSettings {

//    val editorMode = EditorMode.CHAT

    val spawnTrigger: SpawnTrigger = SpawnTrigger.JOIN

    val debug: Boolean
        get() = ink.ptms.adyeshach.core.AdyeshachSettings.debug

    val visibleDistance: Double
        get() = ink.ptms.adyeshach.core.AdyeshachSettings.visibleDistance

    val pathfinderSync: Boolean
        get() = ink.ptms.adyeshach.core.AdyeshachSettings.pathfinderSync

    val deleteFileInUnknownWorld: List<String>
        get() = ink.ptms.adyeshach.core.AdyeshachSettings.deleteFileInUnknownWorld

    val viewConditionInterval: Int
        get() = ink.ptms.adyeshach.core.AdyeshachSettings.viewConditionInterval

    var ashconAPI = false

    fun isSpecifiedWorld(world: String): Boolean {
        return ink.ptms.adyeshach.core.AdyeshachSettings.isAutoDeleteWorld(world)
    }

    enum class SpawnTrigger {

        KEEP_ALIVE, JOIN
    }
}