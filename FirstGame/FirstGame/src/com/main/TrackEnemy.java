package com.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class TrackEnemy extends GameObject {

    private Handler handler;
    private GameObject player;

    public TrackEnemy(int x, int y, ID id, Handler handler) {
        super(x, y, id);

        this.handler = handler;

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
        return new Rectangle(x, y, 16, 16);
    }

    public void tick() {
        x += velX;
        y += velY;

        float diffX = player.getX() - 16;
        float diffY = player.getY() - 16;
        float distance = (float) Math.sqrt((x-player.getX()) * (x-player.getX()) + (y-player.getY()) * (y-player.getY()));

        velX = (int) ((-1/distance) * diffX * 3);
        velY = (int) ((-1/distance) * diffY * 3);

        if(y <= 0 || y >= Game.HEIGHT - 32) velY *= -1;
        if(x <= 0 || x >= Game.WIDTH - 16) velX *= -1;

        handler.addObject(new TrailEffect(x, y, ID.TrailEffect, handler, Color.green, 16, 16, 0.04f));
    }

    public void render(Graphics g) {

        g.setColor(Color.green);
        g.fillRect(x, y, 16, 16);

    }
}