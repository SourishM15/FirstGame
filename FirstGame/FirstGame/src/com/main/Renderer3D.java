package com.main;

import java.awt.Point;

public class Renderer3D {

    public static final double FOCAL_LENGTH = 420.0;
    public static final double PLAYER_DEPTH = 160.0;
    public static final double FAR_DEPTH = 980.0;
    private static final double MIN_DEPTH = 45.0;

    public static Point project(double worldX, double worldY, double worldZ) {
        double scale = scale(worldZ);
        return new Point(projectScaledX(worldX, scale), projectScaledY(worldY, scale));
    }

    public static int projectX(double worldX, double worldZ) {
        return projectScaledX(worldX, scale(worldZ));
    }

    public static int projectY(double worldY, double worldZ) {
        return projectScaledY(worldY, scale(worldZ));
    }

    public static double scale(double worldZ) {
        return FOCAL_LENGTH / Math.max(MIN_DEPTH, worldZ);
    }

    public static double screenToWorldX(int screenX, double worldZ) {
        return (screenX - Game.WIDTH / 2.0) / scale(worldZ);
    }

    public static double screenToWorldY(int screenY, double worldZ) {
        return (screenY - Game.HEIGHT / 2.0) / scale(worldZ);
    }

    public static int projectedSize(double size, double worldZ) {
        return Math.max(2, (int) Math.round(size * scale(worldZ)));
    }

    private static int projectScaledX(double worldX, double scale) {
        return (int) Math.round(Game.WIDTH / 2.0 + worldX * scale);
    }

    private static int projectScaledY(double worldY, double scale) {
        return (int) Math.round(Game.HEIGHT / 2.0 + worldY * scale);
    }
}
