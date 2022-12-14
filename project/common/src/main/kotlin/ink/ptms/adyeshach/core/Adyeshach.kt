package ink.ptms.adyeshach.core

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.Adyeshach
 *
 * @author 坏黑
 * @since 2022/6/16 16:29
 */
object Adyeshach {

    private var api: AdyeshachAPI? = null
    private var editor: AdyeshachEditor? = null

    /**
     * 获取开发者接口
     */
    fun api(): AdyeshachAPI {
        return api ?: error("AdyeshachAPI has not finished loading, or failed to load!")
    }

    /**
     * 获取编辑器接口
     */
    fun editor(): AdyeshachEditor? {
        return editor
    }

    /**
     * 注册开发者接口
     */
    fun register(api: AdyeshachAPI) {
        Adyeshach.api = api
    }

    /**
     * 注册编辑器接口
     */
    fun register(editor: AdyeshachEditor) {
        Adyeshach.editor = editor
    }
}