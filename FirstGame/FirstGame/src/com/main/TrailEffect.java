package com.main;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Color;

public class TrailEffect extends GameObject {

    private float alpha = 1;
    private Handler handler;
    private Color color;
    private int width, height;
    private float life;

    public TrailEffect(int x, int y, ID id, Handler handler, Color color, int width, int height, float life) {
        super(x, y, id);
        this.color = color;
        this.width = width;
        this.height = height;
        this.life = life;
        this.handler = handler;
    }

    public void tick() {
        if(alpha > life) {
            alpha -= (life - 0.001f);
        } else {
            handler.removeObject(this);
        }
    }

    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(makeTransparent(alpha));
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g2d.setComposite(makeTransparent(1));
    }

    private AlphaComposite makeTransparent(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return(AlphaComposite.getInstance(type, alpha));
    }

    public Rectangle getBounds() {
        return null;
    }
}
