package com.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class FastEnemy extends GameObject {

    private Handler handler;

    public FastEnemy(int x, int y, ID id, Handler handler) {
        super(x, y, id);

        this.handler = handler;
        velX = 2;
        velY = 8;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 16, 16);
    }

    public void tick() {
        x += velX;
        y += velY;

        if(y <= 0 || y >= Game.HEIGHT - 32) velY *= -1;
        if(x <= 0 || x >= Game.WIDTH - 16) velX *= -1;

        handler.addObject(new TrailEffect(x, y, ID.TrailEffect, handler, Color.blue, 16, 16, 0.04f));
    }

    public void render(Graphics g) {

        g.setColor(Color.blue);
        g.fillRect(x, y, 16, 16);

    }
}
