package ink.ptms.adyeshach.common.api

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.api.Adyeshach
 *
 * @author 坏黑
 * @since 2022/6/16 16:29
 */
object Adyeshach {

    private var api: AdyeshachAPI? = null

    /**
     * 获取开发者接口
     */
    fun api(): AdyeshachAPI {
        return api ?: error("AdyeshachAPI has not finished loading, or failed to load!")
    }

    /**
     * 注册开发者接口
     */
    fun register(api: AdyeshachAPI) {
        Adyeshach.api = api
    }
}