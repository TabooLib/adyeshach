package ink.ptms.adyeshach.compat.modelengine4

import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.model.ActiveModel
import com.ticxo.modelengine.core.animation.handler.PriorityHandler
import com.ticxo.modelengine.core.animation.handler.StateMachineHandler
import com.ticxo.modelengine.v1_20_R3.NMSHandler_v1_20_R3
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachEntityTypeRegistry
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.ModelEngine
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
import taboolib.common.util.unsafeLazy


/**
 * Adyeshach
 * ink.ptms.adyeshach.compat.modelengine2.DefaultModelEngine
 *
 * @author 坏黑
 * @since 2022/6/19 21:58
 */
@Suppress("SpellCheckingInspection")
interface DefaultModelEngine : ModelEngine {

    override fun showModelEngine(viewer: Player): Boolean {
        if (isModelEngineHooked) {
            if (modelEngineName.isNotBlank()) {
                refreshModelEngine()
                return true
            }
        }
        return false
    }

    override fun hideModelEngine(viewer: Player): Boolean {
        return false
    }

    override fun refreshModelEngine(): Boolean {
        if (isModelEngineHooked) {
            this as DefaultEntityInstance

            // 创建模型
            if (modelEngineName.isNotBlank()) {
                modelEngineUniqueId = normalizeUniqueId
                val entityModeled = EntityModeled(this)
                val model = ModelEngineAPI.getOrCreateModeledEntity(normalizeUniqueId) { entityModeled }
                model.queuePostInitTask {
                    model.isBaseEntityVisible = false
                    model.base.maxStepHeight = 0.5

                    // syncBodyYaw
                    model.base.bodyRotationController.yBodyRot = getLocation().yaw

                    // 没有模型
                    if (!model.getModel(modelEngineName).isPresent) {
                        val useStateMachine = false
                        val scale = 1.0
                        val hitboxScale = 1.0
                        val doDamageTint = true
                        val lockPitch = false
                        val lockYaw = false
                        val initRenderer = true
                        val showHitbox = true
                        val showShadow = true
                        val hitbox = true
                        val canRide = false
                        val canDrive = false

                        val activeModel: ActiveModel = ModelEngineAPI.createActiveModel(modelEngineName, null) {
                            if (useStateMachine) StateMachineHandler(it) else PriorityHandler(it)
                        }
                        activeModel.setScale(scale)
                        activeModel.setHitboxScale(hitboxScale)
                        activeModel.setCanHurt(doDamageTint)
                        activeModel.isLockPitch = lockPitch
                        activeModel.isLockYaw = lockYaw
                        activeModel.setAutoRendererInitialization(initRenderer)
                        activeModel.isHitboxVisible = showHitbox
                        activeModel.isShadowVisible = showShadow
                        model.addModel(activeModel, hitbox)
//                        activeModel.getMountManager<MountManager>().ifPresent { mountManager ->
//                            (mountManager as MountManager).setCanRide(canRide)
//                            (mountManager as MountManager).setCanDrive(canDrive)
//                            (model.getMountData() as MountData).setMainMountManager(mountManager)
//                        }
//                        val nametag = MythicUtils.getOrNullLowercase(this.nametag, meta, target)
//                        if (nametag != null) {
//                            activeModel.getBone(nametag).flatMap { modelBone -> modelBone.getBoneBehavior(BoneBehaviorTypes.NAMETAG) }.ifPresent { nameTag ->
//                                (nameTag as NameTag).setComponentSupplier {
//                                    if (ServerInfo.IS_PAPER) {
//                                        return@setComponentSupplier bukkitTarget.customName()
//                                    } else {
//                                        val name: String = bukkitTarget.getCustomName()
//                                        return@setComponentSupplier if (name == null) null else LegacyComponentSerializer.legacyAmpersand().deserialize(name)
//                                    }
//                                }
//                                (nameTag as NameTag).setVisible(true)
//                            }
//                        }
                    }
                }
            }
        }
        return false
    }

    override fun updateModelEngineNameTag() {
    }

    override fun hurt() {
    }

    companion object {

        val isModelEngineHooked by unsafeLazy {
            (Bukkit.getPluginManager().getPlugin("ModelEngine") != null) && kotlin.runCatching { NMSHandler_v1_20_R3::class.java }.isSuccess
        }

        // @Awake(LifeCycle.LOAD)
        fun init() {
            // 注册生成回调
            Adyeshach.api().getEntityTypeRegistry().prepareGenerate(object : AdyeshachEntityTypeRegistry.GenerateCallback {

                override fun invoke(entityType: EntityTypes, interfaces: List<String>): List<String> {
                    val array = ArrayList<String>()
                    // 是否安装 ModelEngine 扩展
                    info("isModelEngineHooked $isModelEngineHooked")
                    if (isModelEngineHooked) {
                        array += DefaultModelEngine::class.java.name
                    }
                    return array
                }
            })
        }
    }
}