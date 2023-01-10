package ink.ptms.adyeshach.impl.entity.controller;

import com.google.gson.annotations.Expose;
import ink.ptms.adyeshach.core.entity.EntityInstance;
import ink.ptms.adyeshach.core.entity.controller.Controller;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

/**
 * net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround
 *
 * @author mojang
 */
public class ControllerRandomLookaround extends Controller {

    @Expose
    protected final double probability;

    protected double relX;

    protected double relZ;

    protected int lookTime;

    public ControllerRandomLookaround(EntityInstance entity) {
        this(entity, 0.01f);
    }

    public ControllerRandomLookaround(EntityInstance entity, double probability) {
        super(entity);
        this.probability = probability;
    }

    @NotNull
    @Override
    public String id() {
        return "RANDOM_LOOKAROUND";
    }

    @NotNull
    @Override
    public String group() {
        return "LOOK";
    }

    @Override
    public int priority() {
        return 9;
    }

    @Override
    public boolean shouldExecute() {
        return Objects.requireNonNull(getEntity()).random().nextFloat() < this.probability;
    }

    @Override
    public boolean continueExecute() {
        return this.lookTime >= 0;
    }

    @Override
    public void start() {
        double r = 6.283185307179586 * Objects.requireNonNull(getEntity()).random().nextDouble();
        this.relX = Math.cos(r);
        this.relZ = Math.sin(r);
        this.lookTime = 20 + getEntity().random().nextInt(20);
    }

    @Override
    public void tick() {
        this.lookTime--;
        Objects.requireNonNull(getEntity()).controllerLookAt(getEntity().getX() + this.relX, getEntity().getEyeLocation().getY(), getEntity().getZ() + this.relZ);
    }

    @Override
    public String toString() {
        return "RANDOM_LOOKAROUND:" + probability;
    }
}
