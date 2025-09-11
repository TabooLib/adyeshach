package ink.ptms.adyeshach.impl.manager

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.TickService
import ink.ptms.adyeshach.core.entity.manager.Manager
import ink.ptms.adyeshach.core.entity.manager.ManagerService
import ink.ptms.adyeshach.core.event.AdyeshachEntityCreateEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityLoadedEvent
import ink.ptms.adyeshach.core.util.safeDistanceIgnoreY
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.function.warning
import taboolib.platform.util.onlinePlayers
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.function.Consumer
import java.util.function.Predicate

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.manager.BaseManager
 *
 * @author 坏黑
 * @since 2022/6/28 00:19
 */
open class BaseManager : Manager, ManagerService, TickService {

    override fun isValid(): Boolean {
        return true
    }

    open fun getPlayers(): List<Player> {
        return onlinePlayers
    }

    override fun create(entityTypes: EntityTypes, location: Location): EntityInstance {
        return create(entityTypes, location, getPlayers()) { }
    }

    override fun create(entityTypes: EntityTypes, location: Location, callback: Consumer<EntityInstance>): EntityInstance {
        return create(entityTypes, location, getPlayers(), callback)
    }

    override fun create(entityTypes: EntityTypes, location: Location, player: List<Player>): EntityInstance {
        return create(entityTypes, location, player) { }
    }

    override fun create(entityTypes: EntityTypes, location: Location, player: List<Player>, function: Consumer<EntityInstance>): EntityInstance {
        val typeHandler = Adyeshach.api().getEntityTypeRegistry()
        // 检查实体类型是否支持
        typeHandler.getBukkitEntityType(entityTypes)
        // 创建实体类
        val entityInstance = typeHandler.getEntityInstance(entityTypes)
        // 注册管理器
        entityInstance.manager = this
        // 添加观察者
        entityInstance.viewPlayers.viewers.addAll(player.map { it.name })
        entityInstance.viewPlayers.visible.addAll(player.filter { it.location.safeDistanceIgnoreY(location) < entityInstance.visibleDistance }.map { it.name })
        function.accept(entityInstance)
        // 唤起事件
        val event = AdyeshachEntityCreateEvent(entityInstance, location)
        if (event.call()) {
            // 生成实体
            entityInstance.spawn(event.location)
            // 注册实体
            add(entityInstance)
        }
        return entityInstance
    }

    override fun add(entity: EntityInstance) {
    }

    override fun remove(entityInstance: EntityInstance) {
    }

    override fun getEntities(): List<EntityInstance> {
        return emptyList()
    }

    override fun getEntities(filter: Predicate<EntityInstance>): List<EntityInstance> {
        return emptyList()
    }

    override fun getEntity(match: Predicate<EntityInstance>): EntityInstance? {
        return null
    }

    override fun getEntityById(id: String): List<EntityInstance> {
        return emptyList()
    }

    override fun getEntityById(id: String, filter: Predicate<EntityInstance>): List<EntityInstance> {
        return emptyList()
    }

    override fun getEntityByUniqueId(id: String): EntityInstance? {
        return null
    }

    override fun isPublic(): Boolean {
        return true
    }

    override fun isTemporary(): Boolean {
        return true
    }

    override fun onEnable() {
    }

    override fun onDisable() {
    }

    override fun onSave() {
    }

    override fun checkVisible() {
    }

    override fun onTick() {
    }

    override fun loadEntityFromFile(file: File): EntityInstance {
        val entity = Adyeshach.api().getEntitySerializer().fromJson(file.readText(StandardCharsets.UTF_8))
        if (Adyeshach.api().getEntityTypeRegistry().getBukkitEntityTypeOrNull(entity.entityType) == null) {
            warning("Entity \"${entity.entityType.name}\" not supported this minecraft version.")
        } else {
            entity.manager = this
            add(entity)
            AdyeshachEntityLoadedEvent(entity).call()
        }
        return entity
    }
}