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

    /**
     * 插值配置类
     * 用于明确指定插值的起始角度和插值系数
     */
    public static class InterpolationConfig {
        private final double startYaw;
        private final double startPitch;
        private final double factor;

        /**
         * 创建插值配置
         *
         * @param startYaw 起始 yaw 角度
         * @param startPitch 起始 pitch 角度
         * @param factor 插值系数 (0.0 - 1.0)
         */
        public InterpolationConfig(double startYaw, double startPitch, double factor) {
            this.startYaw = startYaw;
            this.startPitch = startPitch;
            this.factor = Math.max(0.0, Math.min(1.0, factor));
        }

        /**
         * 从现有角度创建插值配置
         *
         * @param angle 起始角度
         * @param factor 插值系数 (0.0 - 1.0)
         */
        public InterpolationConfig(Angle angle, double factor) {
            this(angle.yaw, angle.pitch, factor);
        }

        public double getStartYaw() {
            return startYaw;
        }

        public double getStartPitch() {
            return startPitch;
        }

        public double getFactor() {
            return factor;
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

    /**
     * 计算目标位置
     *
     * @param entity 实体实例
     * @param player 玩家
     * @return 目标位置
     */
    public static Location calculateTargetLocation(EntityInstance entity, Player player, boolean isVillagerWithPassenger) {
        Location target = player.getEyeLocation();
        // 检查是否为村民且有乘客
        if (isVillagerWithPassenger) {
            target = ControllerLookAtPlayer.fixVillagerTarget(entity, target);
        }
        return target;
    }

    /**
     * 计算朝向并向玩家发送更新包
     *
     * @param player 目标玩家
     * @param entity 实体实例
     * @param lookTarget 看向目标位置
     * @param onlyHorizontal 是否只考虑水平方向
     */
    public static void updatePlayerLook(Player player, EntityInstance entity, Location lookTarget, boolean onlyHorizontal) {
        Location entityLoc = entity.getEyeLocation();
        // 计算朝向向量
        Vector direction = lookTarget.toVector().subtract(entityLoc.toVector());

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

    /**
     * 计算朝向并向玩家发送更新包（使用 InterpolationConfig 进行精确插值）
     * 当插值系数达到 1.0 时会自动更新 LAST_LOOK_ANGLES
     *
     * @param player 目标玩家
     * @param entity 实体实例
     * @param lookTarget 看向目标位置
     * @param onlyHorizontal 是否只考虑水平方向
     * @param config 插值配置，包含起始角度和插值系数
     */
    public static void updatePlayerLook(Player player, EntityInstance entity, Location lookTarget, boolean onlyHorizontal, InterpolationConfig config) {
        Location entityLoc = entity.getEyeLocation();
        // 计算朝向向量
        Vector direction = lookTarget.toVector().subtract(entityLoc.toVector());

        // 计算 yaw 和 pitch
        double x = direction.getX();
        double y = direction.getY();
        double z = direction.getZ();

        // 计算 yaw (水平角度)
        double targetYaw = Math.toDegrees(Math.atan2(-x, z));
        // 计算 pitch (垂直角度)
        double targetPitch = Math.toDegrees(Math.asin(-y / direction.length()));

        // 如果只需要水平视角，将 pitch 设为 0
        if (onlyHorizontal) {
            targetPitch = 0;
        }

        // 使用配置中的起始角度进行插值
        double yaw = interpolateAngle(config.getStartYaw(), targetYaw, config.getFactor());
        double pitch = config.getStartPitch() + (targetPitch - config.getStartPitch()) * config.getFactor();

        float fixYaw = YawFixerKt.fixYaw(entity.getEntityType(), (float) yaw);
        // 发送朝向更新包
        INSTANCE.api().getMinecraftAPI().getEntityOperator().updateEntityLook(
                player,
                entity.getIndex(),
                fixYaw,
                (float) pitch,
                true
        );
        // 当插值完成时（factor >= 1.0），更新 LAST_LOOK_ANGLES
        if (config.getFactor() >= 1.0) {
            LAST_LOOK_ANGLES.computeIfAbsent(player.getName(), k -> new ConcurrentHashMap<>()).put(entity.getUniqueId(), new Angle(yaw, pitch));
        }
    }

    /**
     * 插值两个角度（考虑角度的循环特性）
     *
     * @param from 起始角度
     * @param to 目标角度
     * @param factor 插值系数 (0.0 - 1.0)
     * @return 插值后的角度
     */
    private static double interpolateAngle(double from, double to, double factor) {
        // 计算最短角度差
        double diff = ((to - from + 180) % 360 + 360) % 360 - 180;
        // 进行插值
        return from + diff * factor;
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
                    Location target = calculateTargetLocation(entity, player, isVillagerWithPassenger);
                    ControllerLookEvent event = new ControllerLookEvent(getEntity(), player, target);
                    if (DefaultAdyeshachAPI.Companion.getLocalEventBus().callControllerLook(event)) {
                        updatePlayerLook(player, entity, event.getLookTarget(), onlyHorizontal);
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

