package ink.ptms.adyeshach.impl.entity.controller;

import com.google.gson.annotations.Expose;
import ink.ptms.adyeshach.core.entity.EntityInstance;
import ink.ptms.adyeshach.core.entity.EntityTypes;
import ink.ptms.adyeshach.core.entity.StandardTags;
import ink.ptms.adyeshach.core.entity.controller.Controller;
import ink.ptms.adyeshach.core.entity.manager.Manager;
import ink.ptms.adyeshach.core.entity.manager.PlayerManager;
import ink.ptms.adyeshach.core.entity.manager.event.ControllerLookEvent;
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer
 *
 * @author mojang
 */
public class ControllerLookAtPlayer extends Controller {

    @Expose
    protected final double lookDistance;

    @Expose
    protected final double probability;

    @Expose
    protected final boolean onlyHorizontal;

    @Expose
    protected final int baseLookTime;

    protected int lookTime;

    @Nullable
    protected LivingEntity lookAt;

    public ControllerLookAtPlayer(EntityInstance entity) {
        this(entity, 8f, 0.01F);
    }

    public ControllerLookAtPlayer(EntityInstance entity, double lookDistance) {
        this(entity, lookDistance, 0.01F);
    }

    public ControllerLookAtPlayer(EntityInstance entity, double lookDistance, double probability) {
        this(entity, lookDistance, probability, false, 40);
    }

    public ControllerLookAtPlayer(EntityInstance entity, double lookDistance, double probability, boolean onlyHorizontal) {
        this(entity, lookDistance, probability, onlyHorizontal, 40);
    }

    public ControllerLookAtPlayer(EntityInstance entity, double lookDistance, double probability, boolean onlyHorizontal, int baseLookTime) {
        super(entity);
        this.lookDistance = lookDistance;
        this.probability = probability;
        this.onlyHorizontal = onlyHorizontal;
        this.baseLookTime = baseLookTime;
    }

    @NotNull
    @Override
    public String id() {
        return "LOOK_AT_PLAYER";
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
        if (getEntity() == null || getEntity().random().nextFloat() >= this.probability) {
            return false;
        }
        Player owner;
        // 优先看向管理器持有者
        Manager manager = getEntity().getManager();
        if (manager instanceof PlayerManager) {
            owner = ((PlayerManager) manager).getOwner();
        } else {
            owner = null;
        }
        Player lookAt = null;
        double distance = 0;
        for (Player player : getEntity().getWorld().getPlayers()) {
            // 有效玩家
            if (owner == null || owner.getName().equals(player.getName())) {
                // 获取距离
                double d = player.getLocation().distanceSquared(getEntity().getLocation());
                // 判定距离并选择最近的玩家
                if (d <= lookDistance * lookDistance && (lookAt == null || d < distance)) {
                    lookAt = player;
                    distance = d;
                }
            }
        }
        if (lookAt != null) {
            this.lookAt = lookAt;
            return true;
        }
        return false;
    }

    @Override
    public boolean continueExecute() {
        Objects.requireNonNull(getEntity());
        if (this.lookAt == null || !this.lookAt.isValid()) {
            return false;
        } else if (getEntity().getWorld() != this.lookAt.getWorld() || getEntity().getEyeLocation().distanceSquared(this.lookAt.getLocation()) > (this.lookDistance * this.lookDistance)) {
            return false;
        } else {
            return this.lookTime > 0;
        }
    }

    @Override
    public void start() {
        this.lookTime = this.adjustedTickDelay(this.baseLookTime + Objects.requireNonNull(getEntity()).random().nextInt(this.baseLookTime));
    }

    @Override
    public void stop() {
        this.lookAt = null;
    }

    @Override
    public void tick() {
        if (getEntity() == null || getEntity().hasTag(StandardTags.IS_MOVING, StandardTags.IS_MOVING_START, StandardTags.IS_PATHFINDING)) {
            return;
        }
        if (this.lookAt != null && this.lookAt.isValid()) {
            double y = this.onlyHorizontal ? getEntity().getEyeLocation().getY() : this.lookAt.getEyeLocation().getY();
            Location target = new Location(
                    this.lookAt.getWorld(),
                    this.lookAt.getLocation().getX(),
                    y,
                    this.lookAt.getLocation().getZ()
            );
            
            // 检查是否为村民且有乘客
            if (getEntity().getEntityType() == EntityTypes.VILLAGER && getEntity().hasPassengers()) {
                target = fixVillagerTarget(getEntity(), target);
            }
            
            ControllerLookEvent event = new ControllerLookEvent(getEntity(), this.lookAt, target);
            if (DefaultAdyeshachAPI.Companion.getLocalEventBus().callControllerLook(event)) {
                getEntity().controllerLookAt(event.getLookTarget().getX(), event.getLookTarget().getY(), event.getLookTarget().getZ());
                this.lookTime--;
            }
        }
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public String status() {
        if (lookAt != null) {
            return lookAt.getName() + " (" + lookTime + ")";
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return id() + ":" + String.format("%.2f", lookDistance) + "," + String.format("%.2f", probability) + "," + onlyHorizontal + "," + baseLookTime;
    }

    public static Location fixVillagerTarget(EntityInstance entity, Location target) {
        // 获取实体的原始位置和朝向
        Location entityLoc = entity.getBodyLocation().clone();
        float entityYaw = entityLoc.getYaw();
        // 计算目标位置相对于实体的方向向量
        Vector direction = target.toVector().subtract(entityLoc.toVector()).normalize();
        // 计算目标朝向角度
        double targetYaw = Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));
        // 计算角度差，确保在 -180 到 180 度之间
        double yawDiff = ((targetYaw - entityYaw) % 360 + 540) % 360 - 180;
        // 如果角度差超过 90 度，则看向原始位置的正前方
        if (Math.abs(yawDiff) > 90) {
            entity.getWorld().spawnParticle(Particle.FLAME, entity.getEyeLocation(), 10, 0.0, 0.0, 0.0, 0.0);
            // 使用 body location 的 yaw 值来确定正前方方向
            Location newTarget = entityLoc.clone();
            newTarget.setY(entity.getEyeLocation().getY());
            // 设置 pitch 为 0 以确保看向水平方向
            newTarget.setPitch(0f);
            // 根据 yaw 值计算正前方的位置
            Vector forward = newTarget.getDirection().multiply(5.0); // 向前 5 个单位
            newTarget.add(forward);
            entity.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, entityLoc, 10, 0.0, 0.0, 0.0, 0.0);
            return newTarget;
        }
        return target;
    }
}