package ink.ptms.adyeshach.impl.entity.controller;

import ink.ptms.adyeshach.core.entity.EntityInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * net.minecraft.world.entity.ai.control.ControllerLook
 *
 * @author mojang
 */
public class BionicSight {

    protected final EntityInstance entity;
    protected float yMaxRotSpeed;
    protected float xMaxRotAngle;
    protected int lookAtCooldown;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;

    protected float xRot;
    protected float yHeadRot;

    public BionicSight(EntityInstance entity) {
        this.entity = entity;
    }

    public void setLookAt(Vector vector) {
        this.setLookAt(vector.getX(), vector.getY(), vector.getZ());
    }

    public void setLookAt(@NotNull Entity entity) {
        this.setLookAt(entity.getLocation().getX(), getWantedY(entity), entity.getLocation().getZ());
    }

    public void setLookAt(Entity entity, float yMaxRotSpeed, float xMaxRotAngle) {
        this.setLookAt(entity.getLocation().getX(), getWantedY(entity), entity.getLocation().getZ(), yMaxRotSpeed, xMaxRotAngle);
    }

    public void setLookAt(double wantedX, double wantedY, double wantedZ) {
        this.setLookAt(wantedX, wantedY, wantedZ, 10f, 40f);
    }

    public void setLookAt(double wantedX, double wantedY, double wantedZ, float yMaxRotSpeed, float xMaxRotAngle) {
        this.wantedX = wantedX;
        this.wantedY = wantedY;
        this.wantedZ = wantedZ;
        this.yMaxRotSpeed = yMaxRotSpeed;
        this.xMaxRotAngle = xMaxRotAngle;
        this.lookAtCooldown = 2;
    }

    public void tick() {
        if (this.lookAtCooldown > 0) {
            this.lookAtCooldown--;
            this.xRot = this.entity.getPitch();
            this.yHeadRot = this.entity.getYaw();
            this.getYRotD().ifPresent((yrd) -> {
                yHeadRot = rotateTowards(yHeadRot, yrd, this.yMaxRotSpeed);
            });
            this.getXRotD().ifPresent((var0) -> {
                xRot = rotateTowards(xRot, var0, this.xMaxRotAngle);
            });
            this.entity.setHeadRotation(yHeadRot, xRot, false);
        }
    }

    public double getWantedX() {
        return this.wantedX;
    }

    public double getWantedY() {
        return this.wantedY;
    }

    public double getWantedZ() {
        return this.wantedZ;
    }

    protected Optional<Float> getXRotD() {
        double x = this.wantedX - this.entity.getX();
        double y = this.wantedY - this.entity.getEyeLocation().getY();
        double z = this.wantedZ - this.entity.getZ();
        double len = Math.sqrt(x * x + z * z);
        return !(Math.abs(y) > 10E-6) && !(Math.abs(len) > 10E-6) ? Optional.empty() : Optional.of((float) (-(atan2(y, len) * 58)));
    }

    protected Optional<Float> getYRotD() {
        double x = this.wantedX - this.entity.getX();
        double z = this.wantedZ - this.entity.getZ();
        return !(Math.abs(z) > 10E-6) && !(Math.abs(x) > 10E-6) ? Optional.empty() : Optional.of((float) (atan2(z, x) * 58) - 90.0F);
    }

    private static float rotateTowards(float currentAngle, float targetAngle, float step) {
        float diff = wrapDegrees(targetAngle - currentAngle);
        float resultAngle = clamp(diff, -step, step);
        return currentAngle + resultAngle;
    }

    private static float wrapDegrees(float angle) {
        float resultAngle = angle % 360.0F;
        if (resultAngle >= 180.0F) {
            resultAngle -= 360.0F;
        }
        if (resultAngle < -180.0F) {
            resultAngle += 360.0F;
        }
        return resultAngle;
    }

    private static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        } else {
            return Math.min(value, max);
        }
    }

    private static double atan2(double y, double x) {
        return Math.atan2(y, x);
    }

    private static double getWantedY(Entity entity) {
        return entity instanceof LivingEntity ? ((LivingEntity) entity).getEyeLocation().getY() : entity.getLocation().getY() + 1.0;
    }
}
