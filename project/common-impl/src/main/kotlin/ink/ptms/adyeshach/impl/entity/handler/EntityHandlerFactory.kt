package ink.ptms.adyeshach.impl.entity.handler

import ink.ptms.adyeshach.core.entity.Brain
import ink.ptms.adyeshach.core.entity.ViewPlayers
import ink.ptms.adyeshach.impl.entity.DefaultEntityInstance
import ink.ptms.adyeshach.impl.entity.DefaultViewPlayers
import ink.ptms.adyeshach.impl.entity.SimpleBrain
import ink.ptms.adyeshach.impl.entity.controller.BionicSight
import java.util.function.Function

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.entity.handler.EntityHandlerFactory
 *
 * Handler 工厂类，允许第三方覆盖重写 handler
 *
 * @author sky
 */
object EntityHandlerFactory {

    private var movementHandlerFactory: Function<DefaultEntityInstance, MovementHandler> = Function { MovementHandler(it) }
    private var visibilityHandlerFactory: Function<DefaultEntityInstance, VisibilityHandler> = Function { VisibilityHandler(it) }
    private var passengerHandlerFactory: Function<DefaultEntityInstance, PassengerHandler> = Function { PassengerHandler(it) }
    private var controllerHandlerFactory: Function<DefaultEntityInstance, ControllerHandler> = Function { ControllerHandler(it) }
    private var positionHandlerFactory: Function<DefaultEntityInstance, PositionHandler> = Function { PositionHandler(it) }
    private var lifecycleHandlerFactory: Function<DefaultEntityInstance, LifecycleHandler> = Function { LifecycleHandler(it) }
    private var customMetaHandlerFactory: Function<DefaultEntityInstance, CustomMetaHandler> = Function { CustomMetaHandler(it) }
    private var genericEntityHandlerFactory: Function<DefaultEntityInstance, GenericEntityHandler> = Function { GenericEntityHandler(it) }
    private var companionHandlerFactory: Function<DefaultEntityInstance, CompanionHandler> = Function { CompanionHandler(it) }
    private var metaHandlerFactory: Function<DefaultEntityInstance, MetaHandler> = Function { MetaHandler(it) }
    private var tagHandlerFactory: Function<DefaultEntityInstance, TagHandler> = Function { TagHandler(it) }
    private var serializationHandlerFactory: Function<DefaultEntityInstance, SerializationHandler> = Function { SerializationHandler(it) }
    private var bionicSightFactory: Function<DefaultEntityInstance, BionicSight> = Function { BionicSight(it) }
    private var viewPlayersFactory: Function<DefaultEntityInstance, ViewPlayers> = Function { DefaultViewPlayers(it) }
    private var brainFactory: Function<DefaultEntityInstance, Brain> = Function { SimpleBrain(it) }

    // ═══════════════════════════════════════════════════════════════════════════════
    // 创建方法
    // ═══════════════════════════════════════════════════════════════════════════════

    fun createMovementHandler(entity: DefaultEntityInstance): MovementHandler {
        return movementHandlerFactory.apply(entity)
    }

    fun createVisibilityHandler(entity: DefaultEntityInstance): VisibilityHandler {
        return visibilityHandlerFactory.apply(entity)
    }

    fun createPassengerHandler(entity: DefaultEntityInstance): PassengerHandler {
        return passengerHandlerFactory.apply(entity)
    }

    fun createControllerHandler(entity: DefaultEntityInstance): ControllerHandler {
        return controllerHandlerFactory.apply(entity)
    }

    fun createPositionHandler(entity: DefaultEntityInstance): PositionHandler {
        return positionHandlerFactory.apply(entity)
    }

    fun createLifecycleHandler(entity: DefaultEntityInstance): LifecycleHandler {
        return lifecycleHandlerFactory.apply(entity)
    }

    fun createCustomMetaHandler(entity: DefaultEntityInstance): CustomMetaHandler {
        return customMetaHandlerFactory.apply(entity)
    }

    fun createGenericEntityHandler(entity: DefaultEntityInstance): GenericEntityHandler {
        return genericEntityHandlerFactory.apply(entity)
    }

    fun createCompanionHandler(entity: DefaultEntityInstance): CompanionHandler {
        return companionHandlerFactory.apply(entity)
    }

    fun createMetaHandler(entity: DefaultEntityInstance): MetaHandler {
        return metaHandlerFactory.apply(entity)
    }

    fun createTagHandler(entity: DefaultEntityInstance): TagHandler {
        return tagHandlerFactory.apply(entity)
    }

    fun createSerializationHandler(entity: DefaultEntityInstance): SerializationHandler {
        return serializationHandlerFactory.apply(entity)
    }

    fun createBionicSight(entity: DefaultEntityInstance): BionicSight {
        return bionicSightFactory.apply(entity)
    }

    fun createViewPlayers(entity: DefaultEntityInstance): ViewPlayers {
        return viewPlayersFactory.apply(entity)
    }

    fun createBrain(entity: DefaultEntityInstance): Brain {
        return brainFactory.apply(entity)
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    // 注册方法
    // ═══════════════════════════════════════════════════════════════════════════════

    fun registerMovementHandler(factory: Function<DefaultEntityInstance, MovementHandler>) {
        movementHandlerFactory = factory
    }

    fun registerVisibilityHandler(factory: Function<DefaultEntityInstance, VisibilityHandler>) {
        visibilityHandlerFactory = factory
    }

    fun registerPassengerHandler(factory: Function<DefaultEntityInstance, PassengerHandler>) {
        passengerHandlerFactory = factory
    }

    fun registerControllerHandler(factory: Function<DefaultEntityInstance, ControllerHandler>) {
        controllerHandlerFactory = factory
    }

    fun registerPositionHandler(factory: Function<DefaultEntityInstance, PositionHandler>) {
        positionHandlerFactory = factory
    }

    fun registerLifecycleHandler(factory: Function<DefaultEntityInstance, LifecycleHandler>) {
        lifecycleHandlerFactory = factory
    }

    fun registerCustomMetaHandler(factory: Function<DefaultEntityInstance, CustomMetaHandler>) {
        customMetaHandlerFactory = factory
    }

    fun registerGenericEntityHandler(factory: Function<DefaultEntityInstance, GenericEntityHandler>) {
        genericEntityHandlerFactory = factory
    }

    fun registerCompanionHandler(factory: Function<DefaultEntityInstance, CompanionHandler>) {
        companionHandlerFactory = factory
    }

    fun registerMetaHandler(factory: Function<DefaultEntityInstance, MetaHandler>) {
        metaHandlerFactory = factory
    }

    fun registerTagHandler(factory: Function<DefaultEntityInstance, TagHandler>) {
        tagHandlerFactory = factory
    }

    fun registerSerializationHandler(factory: Function<DefaultEntityInstance, SerializationHandler>) {
        serializationHandlerFactory = factory
    }

    fun registerBionicSight(factory: Function<DefaultEntityInstance, BionicSight>) {
        bionicSightFactory = factory
    }

    fun registerViewPlayers(factory: Function<DefaultEntityInstance, ViewPlayers>) {
        viewPlayersFactory = factory
    }

    fun registerBrain(factory: Function<DefaultEntityInstance, Brain>) {
        brainFactory = factory
    }
}
