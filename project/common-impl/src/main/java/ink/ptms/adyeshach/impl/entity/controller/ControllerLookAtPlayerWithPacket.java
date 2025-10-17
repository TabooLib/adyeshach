package ink.ptms.adyeshach.impl.entity.controller;

import com.google.gson.annotations.Expose;
import ink.ptms.adyeshach.core.entity.EntityInstance;
import ink.ptms.adyeshach.core.entity.EntityTypes;
import ink.ptms.adyeshach.core.entity.StandardTags;
import ink.ptms.adyeshach.core.entity.controller.Controller;
import ink.ptms.adyeshach.core.entity.manager.event.ControllerLookEvent;
import ink.ptms.adyeshach.core.util.YawFixerKt;
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ink.ptms.adyeshach.core.Adyeshach.*;

/**
 * 让 NPC 通过数据包看向 "所有玩家"
 */
public class ControllerLookAtPlayerWithPacket extends Controller {

    public static final Map<String, Map<String, Angle>> LAST_LOOK_ANGLES = new ConcurrentHashMap<>();

    public static class Angle {

        public final double yaw;
        public final double pitch;

        public Angle(double yaw, double pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public double getYaw() {
            return yaw;
        }

        public double getPitch() {
            return pitch;
        }
    }

    @Expose
    protected final double lookDistance;

    @Expose
    protected final boolean onlyHorizontal;

    public ControllerLookAtPlayerWithPacket(EntityInstance entity) {
        this(entity, 8f);
    }

    public ControllerLookAtPlayerWithPacket(EntityInstance entity, double lookDistance) {
        this(entity, lookDistance, false);
    }

    public ControllerLookAtPlayerWithPacket(EntityInstance entity, double lookDistance, boolean onlyHorizontal) {
        super(entity);
        this.lookDistance = lookDistance;
        this.onlyHorizontal = onlyHorizontal;
    }

    /**
     * 获取上次看向角度
     * 
     * @param entity 实体实例
     * @param player 玩家
     * @return 上次的角度，如果不存在则返回 null
     */
    @Nullable
    public static Angle getLastLookAngle(EntityInstance entity, Player player) {
        if (entity == null || player == null) {
            return null;
        }
        Map<String, Angle> angles = LAST_LOOK_ANGLES.computeIfAbsent(player.getName(), k -> new ConcurrentHashMap<>());
        return angles.get(entity.getUniqueId());
    }

    @NotNull
    @Override
    public String id() {
        return "LOOK_AT_PLAYER_WITH_PACKET";
    }

    @NotNull
    @Override
    public String group() {
        return "LOOK";
    }

    @Override
    public int priority() {
        return 8;
    }

    @Override
    public boolean shouldExecute() {
        EntityInstance entity = getEntity();
        if (entity == null) {
            return false;
        }
        // 检查是否有任何可见玩家在范围内
        for (Player player : entity.getVisiblePlayers()) {
            if (player.isValid() && player.getWorld().equals(entity.getWorld())) {
                double distance = player.getLocation().distanceSquared(entity.getLocation());
                if (distance <= lookDistance * lookDistance) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean continueExecute() {
        EntityInstance entity = getEntity();
        if (entity == null) {
            return false;
        }
        return !entity.getVisiblePlayers().isEmpty();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        // 不需要清理任何状态
    }

    @Override
    public void tick() {
        EntityInstance entity = getEntity();
        if (entity == null || entity.hasTag(StandardTags.IS_MOVING, StandardTags.IS_MOVING_START, StandardTags.IS_PATHFINDING)) {
            return;
        }
        boolean isVillagerWithPassenger = entity.getEntityType() == EntityTypes.VILLAGER && entity.hasPassengers();

        // 向所有可见玩家发送朝向更新
        for (Player player : entity.getVisiblePlayers()) {
            if (player.isValid() && player.getWorld().equals(entity.getWorld())) {
                double distance = player.getLocation().distanceSquared(entity.getLocation());
                if (distance <= lookDistance * lookDistance) {
                    Location entityLoc = entity.getEyeLocation();
                    Location target = player.getEyeLocation();
                    
                    // 检查是否为村民且有乘客
                    if (isVillagerWithPassenger) {
                        target = ControllerLookAtPlayer.fixVillagerTarget(entity, target);
                    }
                    
                    ControllerLookEvent event = new ControllerLookEvent(getEntity(), player, target);
                    if (DefaultAdyeshachAPI.Companion.getLocalEventBus().callControllerLook(event)) {
                        // 计算朝向向量
                        Vector direction = event.getLookTarget().toVector().subtract(entityLoc.toVector());

                        // 计算 yaw 和 pitch
                        double x = direction.getX();
                        double y = direction.getY();
                        double z = direction.getZ();

                        // 计算 yaw (水平角度)
                        double yaw = Math.toDegrees(Math.atan2(-x, z));
                        // 计算 pitch (垂直角度)
                        double pitch = Math.toDegrees(Math.asin(-y / direction.length()));

                        // 如果只需要水平视角，将 pitch 设为 0
                        if (onlyHorizontal) {
                            pitch = 0;
                        }
                        float fixYaw = YawFixerKt.fixYaw(entity.getEntityType(), (float) yaw);
                        // 发送朝向更新包
                        INSTANCE.api().getMinecraftAPI().getEntityOperator().updateEntityLook(
                                player,
                                entity.getIndex(),
                                fixYaw,
                                (float) pitch,
                                true
                        );
                        // 记录最后的朝向角度
                        LAST_LOOK_ANGLES.computeIfAbsent(player.getName(), k -> new ConcurrentHashMap<>()).put(entity.getUniqueId(), new Angle(yaw, pitch));
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public String status() {
        return "Looking at all player";
    }

    @Override
    @NotNull
    public String toString() {
        return id() + ":" + String.format("%.2f", lookDistance) + "," + onlyHorizontal;
    }
}