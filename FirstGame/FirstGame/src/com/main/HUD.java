package com.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class HUD {

    public static int HEALTH = 100;
    private int greenValue = 255;

    private int score = 0;
    private int level = 1;
    private int highScore = 0;

    public void tick() {
        //HEALTH--;

        HEALTH = Game.clamp(HEALTH,0, 100);
        greenValue = Game.clamp(greenValue, 0 , 255);

        greenValue = HEALTH * 2;

        score++;
    }

    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        greenValue = Game.clamp(HEALTH * 2, 0, 255);
        g.setColor(new Color(3, 8, 22, 180));
        g.fillRoundRect(10, 10, 225, 92, 8, 8);
        g.setColor(new Color(108, 211, 255, 130));
        g.drawRoundRect(10, 10, 225, 92, 8, 8);

        g.setColor(new Color(64, 17, 37));
        g.fillRect(23, 24, 190, 16);
        g2d.setPaint(new java.awt.GradientPaint(23, 24, new Color(255, 72, 112), 213, 24, new Color(75, greenValue, 142)));
        g.fillRect(23, 24, HEALTH * 190 / 100, 16);
        g.setColor(new Color(226, 247, 255));
        g.drawRect(23, 24, 190, 16);

        g.setColor(new Color(210, 244, 255));
        g.drawString("SCORE " + score, 23, 60);
        g.drawString("LEVEL " + level, 23, 76);
        g.drawString("BEST  " + highScore, 23, 92);
    }

    public void reset() {
        HEALTH = 100;
        greenValue = 255;
        score = 0;
        level = 1;
    }

    public void saveHighScore() {
        if(score > highScore) {
            highScore = score;
        }
    }

    public void addScore(int amount) {
        score += amount;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
