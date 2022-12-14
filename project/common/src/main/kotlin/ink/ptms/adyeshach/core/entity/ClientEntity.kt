package ink.ptms.adyeshach.core.entity

import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.entity.ClientEntity
 *
 * @author sky
 * @since 2021/9/25 1:16 上午
 */
class ClientEntity(val entity: EntityInstance) {

    val entityId: Int
        get() = entity.index

    val clientId: UUID
        get() = entity.normalizeUniqueId
}