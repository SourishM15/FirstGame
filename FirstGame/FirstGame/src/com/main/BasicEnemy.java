package com.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class BasicEnemy extends GameObject {

    private Handler handler;
    private double worldX;
    private double worldY;
    private double worldZ;
    private double driftX;
    private double driftY;
    private double spin = 0;
    private static final Color SHADOW_COLOR = new Color(94, 51, 64, 140);
    private static final Color RIM_COLOR = new Color(255, 188, 118, 130);
    private static final double[][] ASTEROID_VERTICES = {
            {0, -24, 3},
            {22, -8, 12},
            {16, 14, -10},
            {-7, 23, 13},
            {-24, 4, -6},
            {-15, -18, -14},
            {7, -4, 27},
            {-4, 5, -27},
            {25, 5, -3},
            {-18, 15, 25}
    };
    private static final int[][] ASTEROID_FACES = {
            {0, 1, 6},
            {1, 8, 6},
            {8, 2, 6},
            {2, 3, 9, 6},
            {3, 4, 9},
            {4, 5, 0},
            {5, 1, 0},
            {0, 7, 1},
            {1, 7, 8},
            {8, 7, 2},
            {2, 7, 3},
            {3, 7, 4},
            {4, 7, 5},
            {5, 7, 0}
    };
    private static final Color[] ASTEROID_COLORS = {
            new Color(202, 111, 91),
            new Color(130, 65, 76),
            new Color(92, 53, 69),
            new Color(171, 86, 79),
            new Color(222, 144, 104),
            new Color(111, 71, 86),
            new Color(239, 167, 115)
    };

    public BasicEnemy(int x, int y, ID id, Handler handler) {
        super(x, y, id);

        this.handler = handler;
        worldZ = Renderer3D.FAR_DEPTH;
        worldX = Renderer3D.screenToWorldX(x, 380) * 1.3;
        worldY = Renderer3D.screenToWorldY(y, 380) * 0.85;
        driftX = x % 2 == 0 ? 1.25 : -1.25;
        driftY = y % 2 == 0 ? 0.65 : -0.65;
    }

    public Rectangle getBounds() {
        updateProjection();
        int size = Renderer3D.projectedSize(32, worldZ);
        return new Rectangle(x - size / 2, y - size / 2, size, size);
    }

    public void tick() {
        worldZ -= 5.2;
        worldX += driftX;
        worldY += driftY;
        spin += 0.035;

        if(Math.abs(worldX) > 280) driftX *= -1;
        if(Math.abs(worldY) > 160) driftY *= -1;

        updateProjection();
        if(worldZ < 90) {
            resetDepth();
        }
    }

    public void render(Graphics g) {

        updateProjection();
        Graphics2D g2d = (Graphics2D) g;
        int size = Renderer3D.projectedSize(38, worldZ);
        g.setColor(SHADOW_COLOR);
        g.fillOval(x - size / 2, y + size / 3, size, size / 3);
        Mesh3D.draw(g2d, worldX, worldY, worldZ, ASTEROID_VERTICES, ASTEROID_FACES, ASTEROID_COLORS, spin, spin * 0.6, spin * 0.35);
        g.setColor(RIM_COLOR);
        g.drawArc(x - size / 3, y - size / 4, size / 2, size / 2, 20, 110);

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
        worldX = -260 + (Math.random() * 520);
        worldY = -150 + (Math.random() * 260);
    }
}
