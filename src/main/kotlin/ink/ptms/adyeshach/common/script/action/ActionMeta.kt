package ink.ptms.adyeshach.common.script.action

import ink.ptms.adyeshach.common.bukkit.*
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.Meta
import ink.ptms.adyeshach.common.entity.editor.MetaEditor
import ink.ptms.adyeshach.common.entity.type.*
import ink.ptms.adyeshach.common.script.ScriptHandler.entitySelected
import ink.ptms.adyeshach.common.script.ScriptHandler.getEntities
import ink.ptms.adyeshach.common.script.ScriptHandler.getManager
import ink.ptms.adyeshach.common.script.ScriptHandler.loadError
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionMeta(val key: String, val symbol: Symbol, val value: ParsedAction<*>?) : ScriptAction<Void>() {

    enum class Symbol {

        SET, RESET
    }

    @Suppress("UNCHECKED_CAST")
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (script.getManager() == null || !script.entitySelected()) {
            error("Manager or Entity is not selected")
        }
        // 获取所有实体以及 Meta
        val entities = LinkedHashMap<EntityInstance, Meta<*>>()
        script.getEntities()?.forEach { entity ->
            val meta = entity?.getEditableEntityMeta()?.firstOrNull { meta -> meta.key.equals(key, true) } ?: error("Metadata \"${key}\" not found.")
            val editor = meta.editor ?: error("Metadata is not editable. (0)")
            if (editor.editable) {
                entities[entity] = meta
            } else {
                error("Metadata is not editable. (1)")
            }
        }
        // 判定操作符
        when (symbol) {
            // 设置
            Symbol.SET -> {
                frame.run(value!!).str { value -> entities.forEach { (entity, meta) -> entity.setMeta(meta, value) } }
            }
            // 重置
            Symbol.RESET -> {
                entities.forEach { (entity, meta) ->
                    val resetMethod = meta.editor as? MetaEditor<EntityInstance>
                    if (resetMethod != null) {
                        resetMethod.resetMethod!!.invoke(script.sender!!.cast(), entity)
                    } else {
                        entity.setMetadata(meta.key, meta.def)
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["meta"], namespace = "adyeshach", shared = true)
        fun parser() = scriptParser {
            val symbol = when (val type = it.nextToken()) {
                "set" -> Symbol.SET
                "reset" -> Symbol.RESET
                else -> throw loadError("Unknown meta operator $type")
            }
            val key = it.nextToken()
            val value = if (symbol == Symbol.SET) {
                it.expect("to")
                it.nextParsedAction()
            } else null
            ActionMeta(key, symbol, value)
        }
    }
}