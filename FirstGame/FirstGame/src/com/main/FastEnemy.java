package com.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class FastEnemy extends GameObject {

    private Handler handler;
    private double worldX;
    private double worldY;
    private double worldZ;
    private double wave = 0;
    private static final Color SHADOW_COLOR = new Color(45, 45, 110, 150);
    private static final Color CORE_COLOR = new Color(255, 92, 124, 150);
    private static final double[][] INTERCEPTOR_VERTICES = {
            {0, 20, -34},
            {-14, -13, 16},
            {14, -13, 16},
            {0, -5, 40},
            {-42, -4, -10},
            {42, -4, -10},
            {0, 8, -10},
            {-10, 7, -30},
            {10, 7, -30},
            {-24, -10, -4},
            {24, -10, -4}
    };
    private static final int[][] INTERCEPTOR_FACES = {
            {0, 1, 6},
            {0, 6, 2},
            {1, 3, 6},
            {6, 3, 2},
            {1, 4, 0},
            {2, 0, 5},
            {0, 4, 7},
            {0, 8, 5},
            {1, 9, 4},
            {2, 5, 10},
            {7, 6, 8}
    };
    private static final Color[] INTERCEPTOR_COLORS = {
            new Color(166, 176, 255),
            new Color(83, 104, 224),
            new Color(225, 232, 255),
            new Color(112, 138, 255),
            new Color(42, 48, 141),
            new Color(68, 83, 188),
            new Color(30, 38, 108),
            new Color(255, 99, 134),
            new Color(43, 52, 130)
    };

    public FastEnemy(int x, int y, ID id, Handler handler) {
        super(x, y, id);

        this.handler = handler;
        worldZ = Renderer3D.FAR_DEPTH;
        worldX = Renderer3D.screenToWorldX(x, 360);
        worldY = Renderer3D.screenToWorldY(y, 360) * 0.8;
    }

    public Rectangle getBounds() {
        updateProjection();
        int size = Renderer3D.projectedSize(30, worldZ);
        return new Rectangle(x - size / 2, y - size / 2, size, size);
    }

    public void tick() {
        wave += 0.09;
        worldZ -= 7.6;
        worldX += Math.sin(wave) * 2.6;
        updateProjection();
        if(worldZ < 90) {
            resetDepth();
        }
    }

    public void render(Graphics g) {

        updateProjection();
        Graphics2D g2d = (Graphics2D) g;
        int size = Renderer3D.projectedSize(34, worldZ);
        g.setColor(SHADOW_COLOR);
        g.fillOval(x - size / 2, y + size / 3, size, size / 3);
        Mesh3D.draw(g2d, worldX, worldY, worldZ, INTERCEPTOR_VERTICES, INTERCEPTOR_FACES, INTERCEPTOR_COLORS, Math.sin(wave) * 0.25, 0.1, Math.sin(wave) * 0.22);
        g.setColor(CORE_COLOR);
        g.fillOval(x - size / 9, y + size / 7, size / 5, size / 4);

    }

    public double getDepth() {
        return worldZ;
    }

    private void updateProjection() {
        x = Renderer3D.projectX(worldX, worldZ);
        y = Renderer3D.projectY(worldY, worldZ);
    }

    private void resetDepth() {
        worldZ = Renderer3D.FAR_DEPTH;
        worldX = -240 + (Math.random() * 480);
        worldY = -130 + (Math.random() * 230);
    }
}
