package ink.ptms.adyeshach.impl.entity.controller;

import com.google.gson.annotations.Expose;
import ink.ptms.adyeshach.core.entity.EntityInstance;
import ink.ptms.adyeshach.core.entity.controller.Controller;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import taboolib.library.xseries.XMaterial;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

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
        if (Objects.requireNonNull(getEntity()).random().nextFloat() >= this.probability) {
            return false;
        } else {
            getEntity().getWorld().getPlayers().stream()
                    .filter(player -> player.getLocation().distanceSquared(getEntity().getLocation()) <= lookDistance * lookDistance)
                    .min((o1, o2) -> {
                        double d1 = o1.getLocation().distanceSquared(getEntity().getLocation());
                        double d2 = o2.getLocation().distanceSquared(getEntity().getLocation());
                        return Double.compare(d1, d2);
                    }).ifPresent(player -> {
                        this.lookAt = player;
                    });
            return this.lookAt != null;
        }
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
        if (this.lookAt != null && this.lookAt.isValid()) {
            Objects.requireNonNull(getEntity());
            double y = this.onlyHorizontal ? getEntity().getEyeLocation().getY() : this.lookAt.getEyeLocation().getY();
            getEntity().controllerLookAt(this.lookAt.getLocation().getX(), y, this.lookAt.getLocation().getZ());
            this.lookTime--;
        }
    }

    @Override
    public String toString() {
        return id() + ":" + String.format("%.2f", lookDistance) + "," + String.format("%.2f", probability) + "," + onlyHorizontal + "," + baseLookTime;
    }
}
