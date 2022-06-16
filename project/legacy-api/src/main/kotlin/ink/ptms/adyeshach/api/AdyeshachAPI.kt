package ink.ptms.adyeshach.api

import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.manager.Manager
import org.bukkit.entity.Player
import taboolib.library.configuration.ConfigurationSection
import java.io.File
import java.io.InputStream
import java.util.function.Function

/**
 * 获取 Adyeshach 中的所有 API 入口
 *
 * @author 坏黑
 * @since 2022/6/16 16:09
 */
object AdyeshachAPI {

    private var api: API? = null

    /**
     * 注册开发者接口
     */
    fun register(api: API) {
        this.api = api
    }

    /**
     * 获取公共单位管理器
     */
    fun getEntityManagerPublic(): Manager {
        return api!!.getEntityManagerPublic()
    }

    /**
     * 获取临时的公共单位管理器
     */
    fun getEntityManagerPublicTemporary(): Manager {
        return api!!.getEntityManagerPublic()
    }

    /**
     * 获取私有单位管理器
     *
     * @param player 玩家
     */
    fun getEntityManagerPrivate(player: Player): Manager {
        return api!!.getEntityManagerPrivate(player)
    }

    /**
     * 获取临时的私有单位管理器
     *
     * @param player 玩家
     */
    fun getEntityManagerPrivateTemporary(player: Player): Manager {
        return api!!.getEntityManagerPrivateTemporary(player)
    }

    /**
     * 从 Yaml 中读取单位
     *
     * @param section 原始 yaml 实例
     * @param transfer 节点名称转换函数
     */
    fun fromYaml(section: ConfigurationSection, transfer: Function<String, String> = Function { it }): EntityInstance? {
        return api!!.fromYaml(section, transfer)
    }

    /**
     * 从 Yaml 读取单位
     *
     * @param source 序列化后的 yaml 文件
     */
    fun fromYaml(source: String): EntityInstance? {
        return api!!.fromYaml(source)
    }

    /**
     * 从 Json 输入流中读取单位
     */
    fun fromJson(inputStream: InputStream): EntityInstance? {
        return api!!.fromJson(inputStream)
    }

    /**
     * 从 Json 文件中读取单位
     */
    fun fromJson(file: File): EntityInstance? {
        return api!!.fromJson(file)
    }

    /**
     * 从 Json 序列化后文件中读取单位
     */
    fun fromJson(source: String): EntityInstance? {
        return api!!.fromJson(source)
    }

    /**
     * 获取玩家就近的单位（包含公共、私有单位管理器中的单位）
     */
    fun getEntityNearly(player: Player): EntityInstance? {
        return api!!.getEntityNearly(player)
    }

    /**
     * 内部实现接口，为了兼容老版本做出的妥协设计
     */
    interface API {

        fun getEntityManagerPublic(): Manager

        fun getEntityManagerPublicTemporary(): Manager

        fun getEntityManagerPrivate(player: Player): Manager

        fun getEntityManagerPrivateTemporary(player: Player): Manager

        fun fromYaml(section: ConfigurationSection, transfer: Function<String, String> = Function { it }): EntityInstance?

        fun fromYaml(source: String): EntityInstance?

        fun fromJson(inputStream: InputStream): EntityInstance?

        fun fromJson(file: File): EntityInstance?

        fun fromJson(source: String): EntityInstance?

        fun getEntityNearly(player: Player): EntityInstance?
    }
}