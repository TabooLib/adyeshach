package ink.ptms.adyeshach.common.api

import com.google.gson.JsonObject
import org.bukkit.command.CommandSender
import java.io.File

/**
 * Adyeshach
 * ink.ptms.adyeshach.common.api.AdyeshachNetworkAPI
 *
 * @author 坏黑
 * @since 2022/6/18 16:31
 */
@Suppress("SpellCheckingInspection")
interface AdyeshachNetworkAPI {

    /**
     * 获取 Ashcon 接口
     */
    fun getAshcon(): Ashcon

    /**
     * 获取 Skin 接口
     */
    fun getSkin(): Skin

    /**
     * ashcon.app 玩家信息接口
     */
    interface Ashcon {

        /**
         * 获取玩家皮肤信息
         */
        fun getTextureValue(name: String): String?

        /**
         * 获取玩家皮肤信息
         */
        fun getTextureSignature(name: String): String?

        /**
         * 获取玩家信息
         */
        fun getProfile(name: String): JsonObject?
    }

    /**
     * mineskin.org 皮肤上传接口
     */
    interface Skin {

        /**
         * 上传皮肤
         *
         * @param file 皮肤文件
         * @param model 皮肤模型类型
         * @param sender 汇报接收者
         */
        fun uploadTexture(file: File, model: SkinModel, sender: CommandSender): JsonObject?

        /**
         * 获取皮肤
         */
        fun getTexture(name: String): SkinTexture?
    }

    /**
     * 皮肤数据
     */
    interface SkinTexture {

        fun isNetwork(): Boolean

        fun signature(): String

        fun value(): String
    }

    /**
     * 模型类型
     */
    enum class SkinModel(val namespace: String) {

        DEFAULT("steve"), SLIM("slim");
    }
}