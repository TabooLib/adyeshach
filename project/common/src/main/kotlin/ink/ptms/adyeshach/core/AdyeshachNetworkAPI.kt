package ink.ptms.adyeshach.core

import org.bukkit.command.CommandSender
import taboolib.library.configuration.ConfigurationSection
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * Adyeshach
 * ink.ptms.adyeshach.core.AdyeshachNetworkAPI
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
         * 获取皮肤
         */
        fun getTexture(name: String): CompletableFuture<SkinTexture>
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
        fun uploadTexture(file: File, model: SkinModel, sender: CommandSender): ConfigurationSection?

        /**
         * 本次是否存在皮肤缓存文件
         */
        fun hasTexture(name: String): Boolean

        /**
         * 获取皮肤
         */
        fun getTexture(name: String): CompletableFuture<SkinTexture>
    }

    /**
     * 皮肤数据
     */
    interface SkinTexture {

        /** 是否为网络皮肤 **/
        fun isNetwork(): Boolean

        /** 皮肤签名 **/
        fun signature(): String

        /** 皮肤数据 **/
        fun value(): String
    }

    /**
     * 模型类型
     */
    enum class SkinModel(val namespace: String) {

        DEFAULT("steve"), SLIM("slim");
    }
}