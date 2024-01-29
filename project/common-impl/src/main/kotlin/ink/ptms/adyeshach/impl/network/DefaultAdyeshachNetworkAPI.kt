package ink.ptms.adyeshach.impl.network

import ink.ptms.adyeshach.core.AdyeshachNetworkAPI
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.DefaultAdyeshachNetworkAPI
 *
 * @author 坏黑
 * @since 2022/6/18 23:44
 */
class DefaultAdyeshachNetworkAPI : AdyeshachNetworkAPI {

    /** Ashcon 玩家数据获取工具 **/
    val networkAshcon = NetworkAshcon()

    /** 皮肤上传工具 **/
    val networkSkin = NetworkMineskin()

    override fun getAshcon(): AdyeshachNetworkAPI.Ashcon {
        return networkAshcon
    }

    override fun getSkin(): AdyeshachNetworkAPI.Skin {
        return networkSkin
    }

    companion object {

        @Awake(LifeCycle.CONST)
        fun init() {
            PlatformFactory.registerAPI<AdyeshachNetworkAPI>(DefaultAdyeshachNetworkAPI())
        }
    }
}