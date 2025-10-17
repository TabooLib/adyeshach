package ink.ptms.adyeshach.impl.script

import ink.ptms.adyeshach.core.util.errorBy
import ink.ptms.adyeshach.impl.getEntities
import ink.ptms.adyeshach.impl.getManager
import ink.ptms.adyeshach.impl.isEntitySelected
import ink.ptms.adyeshach.impl.loadError
import ink.ptms.adyeshach.impl.throwUndefinedError
import taboolib.common5.cbool
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author sky
 */
class ActionMeta(val key: String, val symbol: Symbol, val value: ParsedAction<*>?) : ScriptAction<Void>() {

    enum class Symbol {

        SET, RESET
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val script = frame.script()
        if (script.getManager() == null || !script.isEntitySelected()) {
            script.throwUndefinedError()
        }
        // 判定操作符
        when (symbol) {
            // 设置
            Symbol.SET -> {
                frame.run(value!!).str { value ->
                    script.getEntities().forEach { entity ->
                        // 设置自定义元数据
                        if (!entity.setCustomMeta(key.replace("-", "_").lowercase(), if (value != "@RESET") value else null)) {
                            // 获取有效的实体元数据
                            val metaFirst = entity.getAvailableEntityMeta().firstOrNull { it.key.equals(key, true) }
                            if (metaFirst != null) {
                                // 这里需要对 Masked 类型的元数据进行特殊兼容
                                // 因为 Masked 类型的元数据使用 ByteMetadataParser
                                if (metaFirst.def is Boolean) {
                                    entity.setMetadata(metaFirst.key, value.cbool)
                                } else {
                                    entity.setMetadata(metaFirst.key, metaFirst.getMetadataParser().parse(value))
                                }
                            } else {
                                errorBy("command-meta-not-found", key)
                            }
                        }
                    }
                }
            }
            // 重置
            Symbol.RESET -> {
                script.getEntities().forEach { entity ->
                    // 获取有效的实体元数据
                    val metaFirst = entity.getAvailableEntityMeta().firstOrNull { it.key.equals(key, true) }
                    if (metaFirst != null) {
                        entity.setMetadata(metaFirst.key, metaFirst.def)
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