package io.github.mrbuggi.cockroach.client;

import io.github.mrbuggi.cockroach.CockroachMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.Random;

public final class Cockroach {
    public static final Cockroach INSTANCE = new Cockroach();

    public static final Identifier TEXTURE = CockroachMod.id("textures/gui/cockroach.png");
    private static final int TEX_SIZE = 32;
    private static final int DRAW_SIZE = 16;

    private static final double WANDER_SPEED = 130.0;
    private static final double HUNT_SPEED = 165.0;
    private static final double FLEE_SPEED = 430.0;
    private static final double FLEE_RADIUS = 42.0;
    private static final double EAT_RADIUS = 11.0;
    private static final double SATIATED_MIN = 20.0;
    private static final double SATIATED_MAX = 30.0;

    private static final Random RANDOM = new Random();

    private double x = Double.NaN;
    private double y = Double.NaN;
    private double vx = 1.0;
    private double vy = 0.0;
    private double angle;

    private double timeToTurn;
    private double hungerCooldown;
    private long lastNanos;

    private Cockroach() {
    }

    public double getCenterX() {
        return x;
    }

    public double getCenterY() {
        return y;
    }

    public boolean tickAndRender(DrawContext context, int panelX, int panelY, int panelW, int panelH,
                                 double mouseX, double mouseY, double targetX, double targetY) {
        double half = DRAW_SIZE / 2.0;
        double minX = panelX + half;
        double maxX = panelX + panelW - half;
        double minY = panelY + half;
        double maxY = panelY + panelH - half;

        if (Double.isNaN(x) || x < minX || x > maxX || y < minY || y > maxY) {
            x = clamp(Double.isNaN(x) ? (minX + maxX) / 2.0 : x, minX, maxX);
            y = clamp(Double.isNaN(y) ? (minY + maxY) / 2.0 : y, minY, maxY);
        }

        double dt = computeDelta();
        boolean ate = update(dt, minX, maxX, minY, maxY, mouseX, mouseY, targetX, targetY);
        render(context);
        return ate;
    }

    private double computeDelta() {
        long now = System.nanoTime();
        double dt = lastNanos == 0 ? 0.0 : (now - lastNanos) / 1_000_000_000.0;
        lastNanos = now;
        return Math.min(dt, 0.1);
    }

    private boolean update(double dt, double minX, double maxX, double minY, double maxY,
                           double mouseX, double mouseY, double targetX, double targetY) {
        if (hungerCooldown > 0.0) {
            hungerCooldown = Math.max(0.0, hungerCooldown - dt);
        }

        double dxm = x - mouseX;
        double dym = y - mouseY;
        double distToCursor = Math.sqrt(dxm * dxm + dym * dym);
        boolean hasTarget = hungerCooldown <= 0.0 && !Double.isNaN(targetX) && !Double.isNaN(targetY);

        double speed;
        if (distToCursor < FLEE_RADIUS) {
            double len = distToCursor < 1.0e-4 ? 1.0 : distToCursor;
            vx = dxm / len;
            vy = dym / len;
            speed = FLEE_SPEED;
            timeToTurn = 0.15 + RANDOM.nextDouble() * 0.2;
        } else if (hasTarget) {
            double tx = targetX - x;
            double ty = targetY - y;
            double tl = Math.sqrt(tx * tx + ty * ty);
            if (tl > 1.0e-4) {
                vx = tx / tl;
                vy = ty / tl;
            }
            speed = HUNT_SPEED;
            timeToTurn = 0.0;
        } else {
            timeToTurn -= dt;
            if (timeToTurn <= 0.0) {
                double a = RANDOM.nextDouble() * Math.PI * 2.0;
                vx = Math.cos(a);
                vy = Math.sin(a);
                timeToTurn = 0.4 + RANDOM.nextDouble() * 0.9;
            }
            speed = WANDER_SPEED;
        }

        double vlen = Math.sqrt(vx * vx + vy * vy);
        if (vlen > 1.0e-4) {
            vx /= vlen;
            vy /= vlen;
        }

        x += vx * speed * dt;
        y += vy * speed * dt;

        if (x < minX) { x = minX; vx = Math.abs(vx); }
        if (x > maxX) { x = maxX; vx = -Math.abs(vx); }
        if (y < minY) { y = minY; vy = Math.abs(vy); }
        if (y > maxY) { y = maxY; vy = -Math.abs(vy); }

        angle = Math.toDegrees(Math.atan2(vy, vx)) + 90.0;

        if (hasTarget && distToCursor >= FLEE_RADIUS) {
            double ex = targetX - x;
            double ey = targetY - y;
            if (ex * ex + ey * ey <= EAT_RADIUS * EAT_RADIUS) {
                hungerCooldown = SATIATED_MIN + RANDOM.nextDouble() * (SATIATED_MAX - SATIATED_MIN);
                return true;
            }
        }
        return false;
    }

    private void render(DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x, y, 300.0);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) angle));
        matrices.translate(-DRAW_SIZE / 2.0, -DRAW_SIZE / 2.0, 0.0);
        context.drawTexture(TEXTURE, 0, 0, DRAW_SIZE, DRAW_SIZE, 0.0F, 0.0F,
                TEX_SIZE, TEX_SIZE, TEX_SIZE, TEX_SIZE);
        matrices.pop();
    }

    private static double clamp(double v, double min, double max) {
        return v < min ? min : Math.min(v, max);
    }
}
