package ink.ptms.adyeshach.compat.modelengine2

object ModelEngineEvents {

//    @SubscribeEvent
//    private fun onMetaUpdate(e: AdyeshachMetaUpdateEvent) {
//        if ((e.key == "customName" || e.key == "isCustomNameVisible") && e.entity is EntityInstance) {
//            submit(delay = 1) { e.entity.updateModelEngineNameTag() }
//        }
//    }

//    @SubscribeEvent
//    private fun onTagUpdate(e: AdyeshachTagUpdateEvent) {
//        if (e.key == "isMoving" && e.entity is EntityInstance) {
//            if (AdyeshachAPI.modelEngineHooked) {
//                val modelManager = ModelEngineAPI.api.modelManager
//                if (e.entity.modelEngineUniqueId != null) {
//                    val modeledEntity = modelManager.getModeledEntity(e.entity.modelEngineUniqueId) ?: return
//                    modeledEntity.isWalking = e.value != null
//                }
//            }
//        }
//    }

//    @SubscribeEvent
//    private fun onInteract(e: PlayerInteractEvent) {
//        if (AdyeshachAPI.modelEngineHooked && e.action != Action.PHYSICAL) {
//            val modelManager = ModelEngineAPI.api.modelManager
//            val entities = ArrayList<Pair<EntityInstance, BoundingBox>>()
//            AdyeshachAPI.getEntities(e.player) { it.getLocation().safeDistance(e.player.location) <= 5 }.forEach {
//                if (it.modelEngineUniqueId != null) {
//                    val modeledEntity = modelManager.getModeledEntity(it.modelEngineUniqueId) ?: return@forEach
//                    val blueprint = modeledEntity.getActiveModel(it.modelEngineName).blueprint ?: return@forEach
//                    val boundingBoxHeight = blueprint.boundingBoxHeight
//                    val boundingBoxWidth = blueprint.boundingBoxWidth / 2
//                    val location = it.getLocation()
//                    entities += it to BoundingBox(
//                        location.x - boundingBoxWidth,
//                        location.y,
//                        location.z - boundingBoxWidth,
//                        location.x + boundingBoxWidth,
//                        location.y + boundingBoxHeight,
//                        location.z + boundingBoxWidth,
//                    )
//                }
//            }
//            RayTrace(e.player).traces(5.0, 0.2).forEach { vec ->
//                entities.filter { it.second.contains(vec) && it.first.isVisibleViewer(e.player) }.forEach {
//                    val result = if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
//                        AdyeshachEntityDamageEvent(it.first, e.player).call()
//                    } else {
//                        AdyeshachEntityInteractEvent(it.first, e.player, e.hand == EquipmentSlot.HAND, Vector(vec.x, vec.y, vec.z)).call()
//                    }
//                    if (result) {
//                        return
//                    }
//                }
//            }
//        }
//    }
}