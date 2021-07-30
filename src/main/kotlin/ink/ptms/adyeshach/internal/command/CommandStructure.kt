package ink.ptms.adyeshach.internal.command

import com.google.common.base.Enums
import com.google.gson.GsonBuilder
import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.editor.Editor
import ink.ptms.adyeshach.common.editor.Picker
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.script.KnownController
import ink.ptms.adyeshach.common.util.error
import ink.ptms.adyeshach.common.util.info
import ink.ptms.adyeshach.common.util.mojang.Model
import ink.ptms.adyeshach.common.util.mojang.MojangAPI
import ink.ptms.adyeshach.internal.trait.TraitFactory
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.adaptCommandSender
import taboolib.common.platform.submit
import taboolib.common.util.Vector
import taboolib.common5.Coerce
import taboolib.library.xseries.XMaterial
import taboolib.module.chat.TellrawJson
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.asLangText
import taboolib.platform.util.buildItem
import taboolib.platform.util.inventoryCenterSlots
import taboolib.platform.util.sendLang
import java.io.File

internal fun Player.suggestEntityId(): List<String> {
    return AdyeshachAPI.getEntities { it.getWorld().name == world.name }.map { it.id }
}

internal fun commandCreate(argument: String, sender: Player, name: String) {
    val entityType = Enums.getIfPresent(EntityTypes::class.java, argument.uppercase()).orNull()
    if (entityType == null) {
        sender.sendLang("command-main-entity-not-support", argument)
        return
    }
    val entity = try {
        AdyeshachAPI.getEntityManagerPublic().create(entityType, sender.location)
    } catch (t: Throwable) {
        t.printStackTrace()
        sender.error("Error: &8${t.message}")
        return
    }
    entity.id = name
    sender.sendLang("command-main-entity-create")
    Editor.open(sender, entity)
}

internal fun commandCopy(entity: EntityInstance?, sender: Player, name: String) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        entity.clone(name, sender.location)
        sender.sendLang("command-main-entity-create")
        Editor.open(sender, entity)
    }
}

internal fun commandCopy(entity: EntityInstance?, sender: CommandSender, name: String, world: String, pos: Vector, yaw: Float, pitch: Float) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        val bukkitWorld = Bukkit.getWorld(world)
        if (bukkitWorld == null) {
            sender.sendLang("command-main-world-not-found")
        } else {
            entity.clone(name, Location(bukkitWorld, pos.x, pos.y, pos.z, yaw, pitch))
            sender.sendLang("command-main-entity-create")
        }
    }
}

internal fun commandMove(entity: EntityInstance?, sender: Player) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        if (entity.getController().isNotEmpty()) {
            sender.sendLang("command-main-move-cancel")
        } else {
            Picker.select(sender, entity)
        }
    }
}

internal fun commandMove(entity: EntityInstance?, sender: CommandSender, world: String, pos: Vector, yaw: Float, pitch: Float) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        val bukkitWorld = Bukkit.getWorld(world)
        if (bukkitWorld == null) {
            sender.sendLang("command-main-world-not-found")
        } else {
            entity.teleport(Location(bukkitWorld, pos.x, pos.y, pos.z, yaw, pitch))
            submit(delay = 20) {
                entity.setHeadRotation(yaw, pitch)
            }
            sender.sendLang("command-main-success")
        }
    }
}

internal fun commandMovehere(entity: EntityInstance?, sender: Player) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        entity.teleport(sender.location)
        submit(delay = 20) {
            entity.setHeadRotation(sender.location.yaw, sender.location.pitch)
        }
        sender.sendLang("command-main-success")
    }
}

internal fun commandLook(entity: EntityInstance?, sender: CommandSender, world: String, pos: Vector) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        val bukkitWorld = Bukkit.getWorld(world)
        if (bukkitWorld == null) {
            sender.sendLang("command-main-world-not-found")
        } else {
            entity.controllerLook(Location(bukkitWorld, pos.x, pos.y, pos.z))
            sender.sendLang("command-main-success")
        }
    }
}

internal fun commandTeleport(entity: EntityInstance?, sender: Player) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        sender.teleport(entity.getLocation())
    }
}

internal fun commandEdit(entity: EntityInstance?, sender: Player) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        Editor.open(sender, entity)
    }
}

internal fun commandRemove(entity: EntityInstance?, sender: CommandSender) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        entity.delete()
        sender.sendLang("command-main-entity-delete")
    }
}

internal fun commandRename(entity: EntityInstance?, sender: CommandSender, name: String) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        entity.id = name
        sender.sendLang("command-main-success")
    }
}

internal fun commandPassengerAdd(entity: EntityInstance?, sender: CommandSender, name: String) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        val target = AdyeshachAPI.getEntityFromUniqueIdOrId(name, sender as? Player)
        if (target == null || target.manager != entity.manager) {
            sender.sendLang("command-main-entity-not-found")
            return
        }
        if (entity == target) {
            sender.sendLang("command-main-passenger-cancel")
            return
        }
        entity.addPassenger(target)
        sender.sendLang("command-main-success")
    }
}

internal fun commandPassengerRemove(entity: EntityInstance?, sender: CommandSender, name: String) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        entity.removePassenger(name)
        sender.sendLang("command-main-success")
    }
}

internal fun commandPassengerReset(entity: EntityInstance?, sender: CommandSender) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        entity.clearPassengers()
        sender.sendLang("command-main-success")
    }
}

internal fun commandControllerAdd(entity: EntityInstance?, sender: CommandSender, name: String) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        val controller = Adyeshach.scriptHandler.getKnownController(name)
        if (controller == null) {
            sender.sendLang("command-main-controller-not-found", name)
            return
        }
        entity.registerController(controller.get(entity))
        sender.sendLang("command-main-success")
    }
}

internal fun commandControllerRemove(entity: EntityInstance?, sender: CommandSender, name: String) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        val controller = Adyeshach.scriptHandler.getKnownController(name)
        if (controller == null) {
            sender.sendLang("command-main-controller-not-found", name)
            return
        }
        entity.unregisterController(controller.controllerClass)
        sender.sendLang("command-main-success")
    }
}

internal fun commandControllerReset(entity: EntityInstance?, sender: CommandSender) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        entity.resetController()
        sender.sendLang("command-main-success")
    }
}

internal fun commandControllerEditor(entity: EntityInstance?, sender: Player) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        val slots = HashMap<Int, String>()
        fun build(id: String, controller: KnownController): ItemStack {
            val en = entity.getController(controller.controllerClass) != null
            return buildItem(XMaterial.PAPER) {
                name = "&7$id ${if (en) "&a&lENABLE" else "&c&lDISABLE"}"
                lore += "&8CLICK TO SELECT"
                if (en) {
                    shiny()
                }
                colored()
            }
        }
        sender.openMenu<Basic>("Controller") {
            rows(6)
            onBuild { _, inv ->
                AdyeshachAPI.getKnownController().keys.forEachIndexed { index, id ->
                    slots[inventoryCenterSlots[index]] = id
                    inv.setItem(inventoryCenterSlots[index], build(id, AdyeshachAPI.getKnownController(id)!!))
                }
            }
            onClick(lock = true) {
                if (slots.containsKey(it.rawSlot)) {
                    val controller = AdyeshachAPI.getKnownController(slots[it.rawSlot]!!)!!
                    if (entity.getController(controller.controllerClass) == null) {
                        entity.registerController(controller.get(entity))
                        it.inventory.setItem(it.rawSlot, build(slots[it.rawSlot]!!, controller))
                    } else {
                        entity.unregisterController(controller.controllerClass)
                        it.inventory.setItem(it.rawSlot, build(slots[it.rawSlot]!!, controller))
                    }
                    sender.playSound(sender.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
                }
            }
            onClose {
                Editor.open(sender, entity)
            }
        }
    }
}

internal fun commandTrait(entity: EntityInstance?, sender: Player, name: String) {
    if (entity == null) {
        sender.sendLang("command-main-entity-not-found")
    } else {
        val trait = TraitFactory.traits.firstOrNull { it.getName().equals(name, true) }
        if (trait == null) {
            sender.sendLang("command-main-trait-not-found", name)
            return
        }
        trait.edit(sender, entity)
    }
}

internal fun commandNear(sender: Player) {
    sender.sendLang("command-main-near-header")
    mapOf(
        sender.asLangText("command-main-near-1") to AdyeshachAPI.getEntityManagerPublic(),
        sender.asLangText("command-main-near-2") to AdyeshachAPI.getEntityManagerPublicTemporary(),
        sender.asLangText("command-main-near-3") to AdyeshachAPI.getEntityManagerPrivate(sender),
        sender.asLangText("command-main-near-4") to AdyeshachAPI.getEntityManagerPrivateTemporary(sender),
    ).forEach { (k, v) ->
        v.getEntities().mapNotNull {
            if (it.getWorld().name == sender.world.name && it.getLocation().distance(sender.location) < 64) {
                it to it.getLocation().distance(sender.location)
            } else {
                null
            }
        }.sortedBy {
            it.second
        }.also { result ->
            if (result.isNotEmpty()) {
                sender.info("  §f$k:")
                result.forEach {
                    TellrawJson()
                        .append("§c[Adyeshach]  §8- §a${Coerce.format(it.second)}m §7${it.first.id} §8(${it.first.getDisplayName()}§8)")
                        .hoverText("CLICK TO TELEPORT")
                        .runCommand("/anpc tp ${it.first.uniqueId}")
                        .sendTo(adaptCommandSender(sender))
                }
            }
        }
    }
}

internal fun commandUploadSign(sender: CommandSender, fileName: String, modelName: String) {
    val model = Enums.getIfPresent(Model::class.java, modelName.uppercase()).or(Model.DEFAULT)
    val file = File(Adyeshach.plugin.dataFolder, "skin/upload/$fileName")
    if (file.exists() && file.name.endsWith(".png")) {
        submit(async = true) {
            sender.sendLang("command-api-skin-header")
            val repose = MojangAPI.upload(file, model, sender)
            if (repose != null) {
                val value = GsonBuilder().setPrettyPrinting().create().toJson(repose)
                File(Adyeshach.plugin.dataFolder, "skin/${file.nameWithoutExtension}").writeText(value)
                sender.sendLang("command-api-skin-success", file.nameWithoutExtension, model)
            }
        }
    } else {
        sender.sendLang("command-api-skin-invalid")
    }
}