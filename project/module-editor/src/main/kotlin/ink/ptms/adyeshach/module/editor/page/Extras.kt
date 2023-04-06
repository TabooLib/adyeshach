@file:Suppress("DuplicatedCode")

package ink.ptms.adyeshach.module.editor.page

import ink.ptms.adyeshach.core.bukkit.BukkitRotation
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.type.AdyArmorStand
import ink.ptms.adyeshach.core.entity.type.AdyDisplay
import ink.ptms.adyeshach.module.editor.action.Action
import ink.ptms.adyeshach.module.editor.action.SimpleAction
import ink.ptms.adyeshach.module.editor.format
import org.bukkit.entity.Player
import taboolib.common5.format
import taboolib.module.chat.colored

/** 生成多个 Action */
fun BukkitRotation.toActions(): List<Action> {
    return listOf(
        Angle.Type.X.actions(this),
        Angle.Type.Y.actions(this),
        Angle.Type.Z.actions(this)
    ).flatten()
}

/** 生成多个 Action */
fun Vector3.Meta.toActions(entity: EntityInstance): List<Action> {
    return listOf(
        Vector3.Type.X.actions(this, entity),
        Vector3.Type.Y.actions(this, entity),
        Vector3.Type.Z.actions(this, entity)
    ).flatten()
}

/** 生成多个 Action */
fun Quat.Meta.toActions(entity: EntityInstance): List<Action> {
    return listOf(
        Quat.Type.X.actions(this, entity),
        Quat.Type.Y.actions(this, entity),
        Quat.Type.Z.actions(this, entity),
        Quat.Type.W.actions(this, entity)
    ).flatten()
}

/** 盔甲架 */
class Angle(val value: Double, val type: Type, val rotation: BukkitRotation) : SimpleAction.Literal() {

    enum class Type {

        X, Y, Z;

        fun actions(rotation: BukkitRotation): List<Action> {
            return listOf(10.0, 1.0, 0.5, 0.1, -10.0, -1.0, -0.5, -0.1).map { Angle(it, this, rotation) }
        }
    }

    override fun display(player: Player): String {
        return if (value > 0) "§a+${value.format()}" else "§c${value.format()}"
    }

    override fun isCustomCommand(): Boolean {
        return true
    }

    override fun clickCommand(player: Player, entity: EntityInstance, page: Page, index: Int): String {
        entity as AdyArmorStand
        val eulerAngle = entity.getRotation(rotation)
        return when (type) {
            Type.X -> "adyeshach edit ${entity.uniqueId} m:${rotation.metaName}->${eulerAngle.x + value},${eulerAngle.y},${eulerAngle.z}"
            Type.Y -> "adyeshach edit ${entity.uniqueId} m:${rotation.metaName}->${eulerAngle.x},${eulerAngle.y + value},${eulerAngle.z}"
            Type.Z -> "adyeshach edit ${entity.uniqueId} m:${rotation.metaName}->${eulerAngle.x},${eulerAngle.y},${eulerAngle.z + value}"
        }
    }
}

/** 展示实体 */
class Vector3(val value: Double, val type: Type, val meta: Meta, val entity: EntityInstance) : SimpleAction.Literal() {

    enum class Meta {

        TRANSLATION, SCALE
    }

    enum class Type {

        X, Y, Z;

        fun actions(meta: Meta, entity: EntityInstance): List<Action> {
            return listOf(10.0, 1.0, 0.5, 0.1, -10.0, -1.0, -0.5, -0.1).map { Vector3(it, this, meta, entity) }
        }
    }

    override fun display(player: Player): String {
        return if (value > 0) "§a+${value.format()}" else "§c${value.format()}"
    }

    override fun description(player: Player): String {
        entity as AdyDisplay
        val vec3 = when (meta) {
            Meta.TRANSLATION -> entity.getTranslation()
            Meta.SCALE -> entity.getScale()
        }
        return "$type " + when (type) {
            Type.X -> vec3.x.format()
            Type.Y -> vec3.y.format()
            Type.Z -> vec3.z.format()
        }
    }

    override fun isCustomCommand(): Boolean {
        return true
    }

    override fun clickCommand(player: Player, entity: EntityInstance, page: Page, index: Int): String {
        entity as AdyDisplay
        val vec3 = when (meta) {
            Meta.TRANSLATION -> entity.getTranslation()
            Meta.SCALE -> entity.getScale()
        }
        return when (type) {
            Type.X -> "adyeshach edit ${entity.uniqueId} m:${meta.name}->${vec3.x + value},${vec3.y},${vec3.z}"
            Type.Y -> "adyeshach edit ${entity.uniqueId} m:${meta.name}->${vec3.x},${vec3.y + value},${vec3.z}"
            Type.Z -> "adyeshach edit ${entity.uniqueId} m:${meta.name}->${vec3.x},${vec3.y},${vec3.z + value}"
        }
    }
}

/** 展示实体 */
class Quat(val value: Double, val type: Type, val meta: Meta, val entity: EntityInstance) : SimpleAction.Literal() {

    enum class Meta(val id: String) {

        ROTATION_LEFT("rotationLeft"), ROTATION_RIGHT("rotationRight");
    }

    enum class Type {

        X, Y, Z, W;

        fun actions(meta: Meta, entity: EntityInstance): List<Action> {
            return listOf(10.0, 1.0, 0.5, 0.1, -10.0, -1.0, -0.5, -0.1).map { Quat(it, this, meta, entity) }
        }
    }

    override fun display(player: Player): String {
        return if (value > 0) "§a+${value.format()}" else "§c${value.format()}"
    }

    override fun description(player: Player): String {
        entity as AdyDisplay
        val quat = when (meta) {
            Meta.ROTATION_LEFT -> entity.getRotationLeft()
            Meta.ROTATION_RIGHT -> entity.getRotationRight()
        }
        return "$type " + when (type) {
            Type.X -> quat.x().format()
            Type.Y -> quat.y().format()
            Type.Z -> quat.z().format()
            Type.W -> quat.w().format()
        }
    }

    override fun isCustomCommand(): Boolean {
        return true
    }

    override fun clickCommand(player: Player, entity: EntityInstance, page: Page, index: Int): String {
        entity as AdyDisplay
        val quat = when (meta) {
            Meta.ROTATION_LEFT -> entity.getRotationLeft()
            Meta.ROTATION_RIGHT -> entity.getRotationRight()
        }
        return when (type) {
            Type.X -> "adyeshach edit ${entity.uniqueId} m:${meta.id}->${quat.x() + value},${quat.y()},${quat.z()},${quat.w()}"
            Type.Y -> "adyeshach edit ${entity.uniqueId} m:${meta.id}->${quat.x()},${quat.y() + value},${quat.z()},${quat.w()}"
            Type.Z -> "adyeshach edit ${entity.uniqueId} m:${meta.id}->${quat.x()},${quat.y()},${quat.z() + value},${quat.w()}"
            Type.W -> "adyeshach edit ${entity.uniqueId} m:${meta.id}->${quat.x()},${quat.y()},${quat.z()},${quat.w() + value}"
        }
    }
}