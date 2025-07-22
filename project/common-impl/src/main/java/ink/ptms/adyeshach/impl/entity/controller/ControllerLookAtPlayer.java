package ink.ptms.adyeshach.impl.entity.controller;

import com.google.gson.annotations.Expose;
import ink.ptms.adyeshach.core.entity.EntityInstance;
import ink.ptms.adyeshach.core.entity.StandardTags;
import ink.ptms.adyeshach.core.entity.controller.Controller;
import ink.ptms.adyeshach.core.entity.manager.Manager;
import ink.ptms.adyeshach.core.entity.manager.PlayerManager;
import ink.ptms.adyeshach.core.entity.manager.event.ControllerLookEvent;
import ink.ptms.adyeshach.impl.DefaultAdyeshachAPI;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
}
