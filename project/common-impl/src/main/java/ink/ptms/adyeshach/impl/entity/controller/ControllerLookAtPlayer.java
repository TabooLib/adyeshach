package ink.ptms.adyeshach.impl.entity.controller;

import ink.ptms.adyeshach.core.entity.EntityInstance;
import ink.ptms.adyeshach.core.entity.controller.Controller;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer
 *
 * @author mojang
 */
public class ControllerLookAtPlayer extends Controller {

    protected final EntityInstance entity;
    protected float lookDistance;
    protected int lookTime;
    protected float probability;
    protected boolean onlyHorizontal;

    @Nullable
    protected LivingEntity lookAt;

    public ControllerLookAtPlayer(EntityInstance entity, float lookDistance) {
        this(entity, lookDistance, 0.01F);
    }

    public ControllerLookAtPlayer(EntityInstance entity, float lookDistance, float probability) {
        this(entity, lookDistance, probability, false);
    }

    public ControllerLookAtPlayer(EntityInstance entity, float lookDistance, float probability, boolean onlyHorizontal) {
        this.entity = entity;
        this.lookDistance = lookDistance;
        this.probability = probability;
        this.onlyHorizontal = onlyHorizontal;
    }

    @NotNull
    @Override
    public String id() {
        return "LOOK_AT_PLAYER";
    }

    @NotNull
    @Override
    public String key() {
        return "LOOK";
    }

    @Override
    public int priority() {
        return 8;
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.random().nextFloat() >= this.probability) {
            return false;
        } else {
            this.entity.getWorld().getPlayers().stream()
                    .filter(player -> player.getLocation().distanceSquared(entity.getLocation()) <= lookDistance * lookDistance)
                    .min((o1, o2) -> {
                        double d1 = o1.getLocation().distanceSquared(entity.getLocation());
                        double d2 = o2.getLocation().distanceSquared(entity.getLocation());
                        return Double.compare(d1, d2);
                    }).ifPresent(player -> {
                        this.lookAt = player;
                    });
            return this.lookAt != null;
        }
    }

    @Override
    public boolean continueExecute() {
        if (this.lookAt == null || !this.lookAt.isValid()) {
            return false;
        } else if (this.entity.getWorld() != this.lookAt.getWorld() || this.entity.getEyeLocation().distanceSquared(this.lookAt.getLocation()) > (double) (this.lookDistance * this.lookDistance)) {
            return false;
        } else {
            return this.lookTime > 0;
        }
    }

    @Override
    public void start() {
        this.lookTime = this.adjustedTickDelay(40 + this.entity.random().nextInt(40));
    }

    @Override
    public void stop() {
        this.lookAt = null;
    }

    @Override
    public void tick() {
        if (this.lookAt != null && this.lookAt.isValid()) {
            double y = this.onlyHorizontal ? this.entity.getEyeLocation().getY() : this.lookAt.getEyeLocation().getY();
            this.entity.controllerLookAt(this.lookAt.getLocation().getX(), y, this.lookAt.getLocation().getZ());
            this.lookTime--;
        }
    }
}
