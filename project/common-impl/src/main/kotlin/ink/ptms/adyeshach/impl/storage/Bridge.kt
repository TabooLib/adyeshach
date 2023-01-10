package ink.ptms.adyeshach.impl.storage

import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.Schedule
import taboolib.library.configuration.ConfigurationSection
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

private val databaseMap = ConcurrentHashMap<String, BridgeDatabase>()

fun createBridgeDatabase(client: String, database: String): BridgeDatabase {
    return databaseMap.computeIfAbsent("$client:$database") { BridgeDatabase(client, database) }
}

fun createBridgeCollection(client: String, database: String, collection: String, indexType: Index = Index.NONE): BridgeCollection {
    return createBridgeDatabase(client, database).getCollection(collection, indexType)
}

fun ProxyPlayer.releaseBridge() {
    databaseMap.values.forEach { database ->
        database.collectionMap.forEach {
            if (it.value.index == Index.USERNAME) {
                it.value.release(name)
            } else if (it.value.index == Index.UUID) {
                it.value.release(uniqueId.toString())
            }
        }
    }
}

fun ConfigurationSection.getValues(): Map<String, Any?> {
    val map = getValues(true).toMutableMap()
    map.entries.removeIf { (_, value) -> value is ConfigurationSection }
    return map
}

@Schedule(period = 1200, async = true)
private fun release() {
    databaseMap.values.forEach { database ->
        database.collectionMap.forEach {
            it.value.dataMap.forEach { (key, value) ->
                if (System.currentTimeMillis() - value.lastVisit > TimeUnit.MINUTES.toMillis(5)) {
                    it.value.release(key)
                }
            }
        }
    }
}