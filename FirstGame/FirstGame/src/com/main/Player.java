package com.main;

import java.awt.*;
import java.util.Random;

public class Player extends GameObject{

    Random r = new Random();
    Handler handler;
    private int damageCooldown = 0;
    private int trailCooldown = 0;
    private static final Color ENGINE_TRAIL = new Color(78, 205, 255);
    private static final Color COCKPIT_RING = new Color(125, 244, 255, 105);
    private static final Color ENGINE_GLOW = new Color(60, 210, 255, 135);
    private static final Color COCKPIT_GLINT = new Color(255, 255, 255, 155);
    private static final double[][] SHIP_VERTICES = {
            {0, -24, 42},
            {-16, 12, 12},
            {16, 12, 12},
            {0, 26, -32},
            {-36, 14, -12},
            {36, 14, -12},
            {0, -1, 24},
            {0, 9, -22},
            {-9, 2, 31},
            {9, 2, 31},
            {-15, 19, -18},
            {15, 19, -18}
    };
    private static final int[][] SHIP_FACES = {
            {0, 1, 6},
            {0, 6, 2},
            {1, 8, 6},
            {2, 6, 9},
            {1, 3, 7, 6},
            {6, 7, 3, 2},
            {1, 4, 3},
            {2, 3, 5},
            {3, 4, 10},
            {3, 11, 5},
            {3, 10, 7, 11},
            {8, 9, 6}
    };
    private static final Color[] SHIP_COLORS = {
            new Color(235, 253, 255),
            new Color(136, 226, 255),
            new Color(62, 153, 219),
            new Color(75, 191, 240),
            new Color(49, 101, 171),
            new Color(77, 150, 216),
            new Color(33, 67, 128),
            new Color(43, 91, 160),
            new Color(90, 210, 255),
            new Color(57, 132, 202),
            new Color(213, 250, 255)
    };
    private static final Color[] HIT_COLORS = {
            new Color(255, 190, 190),
            new Color(255, 95, 95),
            new Color(150, 52, 74),
            new Color(210, 80, 100),
            new Color(115, 37, 60),
            new Color(160, 56, 82),
            new Color(80, 28, 48)
    };

    public Player(int x, int y, ID id, Handler handler){
        super(x, y, id);
        this.handler = handler;

    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }

    public void tick() {
        x += velX;
        y += velY;
        if(damageCooldown > 0) {
            damageCooldown--;
        }

        x = Game.clamp(x, 0, Game.WIDTH - 37);
        y = Game.clamp(y, 0, Game.HEIGHT - 60);
        trailCooldown++;
        if(trailCooldown >= 2) {
            trailCooldown = 0;
            handler.addObject(new TrailEffect(x + 11, y + 26, ID.TrailEffect, handler, ENGINE_TRAIL, 10, 18, 0.08f));
        }

        collision();
    }

    private void collision() {
        for(int i = 0; i < handler.ob.size(); i++) {
            GameObject tempObject = handler.ob.get(i);
                if(tempObject.getID() == ID.BasicEnemy || tempObject.getID() == ID.FastEnemy || tempObject.getID() == ID.TrackEnemy) {
                    if(damageCooldown == 0 && tempObject.getDepth() < 230 && getBounds().intersects(tempObject.getBounds())) {
                        //collision damage
                        HUD.HEALTH -= 10;
                        damageCooldown = 25;
                    }
            }
        }
    }


    public void render(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        double worldX = Renderer3D.screenToWorldX(x + 16, Renderer3D.PLAYER_DEPTH);
        double worldY = Renderer3D.screenToWorldY(y + 16, Renderer3D.PLAYER_DEPTH);
        double roll = velX * 0.055;
        Color[] colors = damageCooldown > 0 ? HIT_COLORS : SHIP_COLORS;

        Mesh3D.draw(g2d, worldX, worldY, Renderer3D.PLAYER_DEPTH, SHIP_VERTICES, SHIP_FACES, colors, 0, -0.08, roll);

        g.setColor(COCKPIT_RING);
        g.drawOval(x + 6, y + 8, 20, 16);
        g.setColor(ENGINE_GLOW);
        g.fillOval(x + 10, y + 27, 12, 18);
        g.setColor(COCKPIT_GLINT);
        g.fillOval(x + 14, y + 13, 5, 4);

    }

    public double getDepth() {
        return Renderer3D.PLAYER_DEPTH;
    }

}
