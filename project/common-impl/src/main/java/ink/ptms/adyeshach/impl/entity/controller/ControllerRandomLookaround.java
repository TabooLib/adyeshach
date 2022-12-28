package ink.ptms.adyeshach.impl.entity.controller;

import ink.ptms.adyeshach.core.entity.EntityInstance;
import ink.ptms.adyeshach.core.entity.controller.Controller;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround
 *
 * @author mojang
 */
public class ControllerRandomLookaround extends Controller {

    protected final EntityInstance entity;
    protected double probability;
    protected double relX;
    protected double relZ;
    protected int lookTime;

    public ControllerRandomLookaround(EntityInstance entity) {
        this(entity, 0.01f);
    }

    public ControllerRandomLookaround(EntityInstance entity, double probability) {
        this.entity = entity;
        this.probability = probability;
    }

    @NotNull
    @Override
    public String id() {
        return "RANDOM_LOOKAROUND";
    }

    @NotNull
    @Override
    public String key() {
        return "LOOK";
    }

    @Override
    public int priority() {
        return 9;
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.random().nextFloat() < this.probability;
    }

    @Override
    public boolean continueExecute() {
        return this.lookTime >= 0;
    }

    @Override
    public void start() {
        double r = 6.283185307179586 * entity.random().nextDouble();
        this.relX = Math.cos(r);
        this.relZ = Math.sin(r);
        this.lookTime = 20 + entity.random().nextInt(20);
    }

    @Override
    public void tick() {
        this.lookTime--;
        this.entity.controllerLookAt(this.entity.getX() + this.relX, this.entity.getEyeLocation().getY(), this.entity.getZ() + this.relZ);
    }

    @Override
    public String toString() {
        return "RANDOM_LOOKAROUND:" + probability;
    }
}
