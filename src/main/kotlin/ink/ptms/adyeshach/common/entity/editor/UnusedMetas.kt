package ink.ptms.adyeshach.common.entity.editor

import ink.ptms.adyeshach.common.entity.EntityMetaable
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.Meta
import ink.ptms.adyeshach.common.entity.type.*
import taboolib.common.LifeCycle
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common5.Coerce
import taboolib.module.kether.KetherShell
import taboolib.module.kether.runKether
import taboolib.module.nms.MinecraftVersion

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.editor.UnusedMetas
 *
 * @author sky
 * @since 2021/1/14 11:50 上午
 */
object UnusedMetas {

    private val unusedMeta = HashMap<Class<*>, MutableList<String>>()

    @Awake(LifeCycle.ACTIVE)
    fun init() {
        unusedMeta.clear()
        val type = ArrayList<String>()
        releaseResourceFile("unused.meta").readLines().forEach {
            if (it.trim().startsWith("!!")) {
                return@forEach
            }
            if (it.isBlank()) {
                return@forEach
            }
            if (it.startsWith("    ")) {
                if (it.contains('%')) {
                    runKether {
                        type.forEach { t ->
                            val clazz = runningClasses.first { r -> r.simpleName == t }
                            val cond = Coerce.toBoolean(KetherShell.eval(it.substringAfter('%')) {
                                rootFrame().variables()["type"] = EntityTypes.fromClass(clazz)?.name
                                rootFrame().variables()["major"] = MinecraftVersion.major
                                rootFrame().variables()["majorLegacy"] = MinecraftVersion.majorLegacy
                            }.getNow(false))
                            if (cond) {
                                unusedMeta.computeIfAbsent(clazz) { ArrayList() } += it.substringBefore('%').trim()
                            }
                        }
                    }
                } else {
                    type.forEach { t ->
                        val clazz = runningClasses.first { r -> r.simpleName == t }
                        unusedMeta.computeIfAbsent(clazz) { ArrayList() } += it.trim()
                    }
                }
            } else {
                type += if (it.startsWith('+')) {
                    it.substring(1).split(',')
                } else {
                    type.clear()
                    it.split(',')
                }
            }
        }
    }

    fun isUnusedMeta(entity: EntityMetaable, meta: Meta<*>): Boolean {
        // 目前已知只有铁傀儡会因 Health 元数据而改变外貌
        if (meta.key == "health" && entity !is AdyIronGolem) {
            return true
        }
        return unusedMeta.any { it.key.isInstance(entity) && it.value.contains(meta.key) }
    }
}