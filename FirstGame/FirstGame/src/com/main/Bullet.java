package com.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.Rectangle;

public class Bullet extends GameObject {

    private Handler handler;
    private HUD hud;
    private double worldX;
    private double worldY;
    private double worldZ;
    private double spin = 0;
    private static final Color EXPLOSION_OUTER = new Color(255, 117, 64);
    private static final Color EXPLOSION_INNER = new Color(255, 238, 154);
    private static final Color BOLT_GLOW = new Color(126, 239, 255);
    private static final Color BOLT_STREAK = new Color(255, 255, 255, 115);
    private static final double[][] BOLT_VERTICES = {
            {0, -4, 24},
            {6, 0, 0},
            {0, 4, 0},
            {-6, 0, 0},
            {0, 0, -18}
    };
    private static final int[][] BOLT_FACES = {
            {0, 1, 2},
            {0, 2, 3},
            {0, 3, 1},
            {1, 4, 2},
            {2, 4, 3},
            {3, 4, 1}
    };
    private static final Color[] BOLT_COLORS = {
            new Color(186, 250, 255),
            new Color(105, 230, 255),
            new Color(255, 255, 255),
            new Color(64, 182, 255),
            new Color(42, 119, 214),
            new Color(84, 213, 255)
    };

    public Bullet(int x, int y, ID id, Handler handler, HUD hud) {
        super(x, y, id);
        this.handler = handler;
        this.hud = hud;
        worldZ = Renderer3D.PLAYER_DEPTH;
        worldX = Renderer3D.screenToWorldX(x, worldZ);
        worldY = Renderer3D.screenToWorldY(y, worldZ);
    }

    public Rectangle getBounds() {
        int size = Renderer3D.projectedSize(10, worldZ);
        return new Rectangle(x - size / 2, y - size / 2, size, size);
    }

    public void tick() {
        worldZ += 34;
        spin += 0.22;
        updateProjection();

        if(worldZ > Renderer3D.FAR_DEPTH + 140) {
            handler.removeObject(this);
            return;
        }

        for(int i = 0; i < handler.ob.size(); i++) {
            GameObject tempObject = handler.ob.get(i);
            if(isEnemy(tempObject) && Math.abs(tempObject.getDepth() - worldZ) < 75 && getBounds().intersects(tempObject.getBounds())) {
                handler.addObject(new TrailEffect(tempObject.getX() - 14, tempObject.getY() - 14, ID.TrailEffect, handler, EXPLOSION_OUTER, 28, 28, 0.09f));
                handler.addObject(new TrailEffect(tempObject.getX() - 7, tempObject.getY() - 7, ID.TrailEffect, handler, EXPLOSION_INNER, 14, 14, 0.12f));
                handler.removeObject(tempObject);
                handler.removeObject(this);
                hud.addScore(150);
                return;
            }
        }
    }

    public void render(Graphics g) {
        updateProjection();
        Graphics2D g2d = (Graphics2D) g;
        int boltWidth = Renderer3D.projectedSize(7, worldZ);
        int boltHeight = Renderer3D.projectedSize(26, worldZ);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
        g.setColor(BOLT_GLOW);
        g.fillOval(x - boltWidth, y, boltWidth * 2, boltHeight);
        g.setColor(BOLT_STREAK);
        g.drawLine(x, y + boltHeight / 2, x, y + boltHeight + 8);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        Mesh3D.draw(g2d, worldX, worldY, worldZ, BOLT_VERTICES, BOLT_FACES, BOLT_COLORS, spin, 0, spin * 0.5);
    }

    private boolean isEnemy(GameObject object) {
        return object.getID() == ID.BasicEnemy || object.getID() == ID.FastEnemy || object.getID() == ID.TrackEnemy;
    }

    public double getDepth() {
        return worldZ;
    }

    private void updateProjection() {
        x = Renderer3D.projectX(worldX, worldZ);
        y = Renderer3D.projectY(worldY, worldZ);
    }
}
