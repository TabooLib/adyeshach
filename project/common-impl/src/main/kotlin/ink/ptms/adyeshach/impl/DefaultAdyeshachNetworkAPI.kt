package ink.ptms.adyeshach.impl

import ink.ptms.adyeshach.common.api.AdyeshachNetworkAPI
import ink.ptms.adyeshach.impl.network.NetworkAshcon
import ink.ptms.adyeshach.impl.network.NetworkMineskin

/**
 * Adyeshach
 * ink.ptms.adyeshach.internal.DefaultAdyeshachNetworkAPI
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
}