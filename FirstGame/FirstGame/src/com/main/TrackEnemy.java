package com.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class TrackEnemy extends GameObject {

    private Handler handler;
    private GameObject player;
    private double worldX;
    private double worldY;
    private double worldZ;
    private double pulse = 0;
    private static final Color SHADOW_COLOR = new Color(32, 86, 62, 150);
    private static final Color CORE_COLOR = new Color(180, 255, 211, 180);
    private static final Color SCAN_RING_COLOR = new Color(98, 255, 185, 90);
    private static final double[][] DRONE_VERTICES = {
            {0, -20, 0},
            {20, 0, 0},
            {0, 20, 0},
            {-20, 0, 0},
            {0, 0, 24},
            {0, 0, -24},
            {-36, 3, -6},
            {36, 3, -6},
            {-18, -16, -8},
            {18, -16, -8},
            {-18, 16, -8},
            {18, 16, -8}
    };
    private static final int[][] DRONE_FACES = {
            {0, 1, 4},
            {1, 2, 4},
            {2, 3, 4},
            {3, 0, 4},
            {1, 0, 5},
            {2, 1, 5},
            {3, 2, 5},
            {0, 3, 5},
            {3, 6, 2},
            {1, 2, 7},
            {0, 8, 3},
            {0, 1, 9},
            {3, 10, 2},
            {1, 2, 11}
    };
    private static final Color[] DRONE_COLORS = {
            new Color(132, 255, 190),
            new Color(67, 224, 145),
            new Color(35, 166, 116),
            new Color(96, 243, 176),
            new Color(25, 104, 86),
            new Color(35, 148, 110),
            new Color(201, 255, 226),
            new Color(56, 200, 158)
    };

    public TrackEnemy(int x, int y, ID id, Handler handler) {
        super(x, y, id);

        this.handler = handler;
        worldZ = Renderer3D.FAR_DEPTH;
        worldX = Renderer3D.screenToWorldX(x, 360);
        worldY = Renderer3D.screenToWorldY(y, 360) * 0.85;

        for(int i = 0; i < handler.ob.size(); i++) {
            if(handler.ob.get(i).getID() == ID.Player) {
                player = handler.ob.get(i);
            }
        }
        /*
        velX = 5;
        velY = 5;
         */
    }

    public Rectangle getBounds() {
        updateProjection();
        int size = Renderer3D.projectedSize(31, worldZ);
        return new Rectangle(x - size / 2, y - size / 2, size, size);
    }

    public void tick() {
        if(player == null) {
            return;
        }

        double targetX = Renderer3D.screenToWorldX(player.getX() + 16, Renderer3D.PLAYER_DEPTH);
        double targetY = Renderer3D.screenToWorldY(player.getY() + 16, Renderer3D.PLAYER_DEPTH);
        worldX += (targetX - worldX) * 0.018;
        worldY += (targetY - worldY) * 0.018;
        worldZ -= 5.8;
        pulse += 0.08;
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
        Mesh3D.draw(g2d, worldX, worldY, worldZ, DRONE_VERTICES, DRONE_FACES, DRONE_COLORS, pulse, Math.sin(pulse) * 0.18, pulse * 0.45);
        g.setColor(CORE_COLOR);
        g.fillOval(x - size / 8, y - size / 8, size / 4, size / 4);
        g.setColor(SCAN_RING_COLOR);
        g.drawOval(x - size / 2, y - size / 2, size, size);

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
        worldX = -250 + (Math.random() * 500);
        worldY = -130 + (Math.random() * 230);
    }
}
