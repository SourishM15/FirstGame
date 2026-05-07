package com.main;

import java.util.Random;

public class Spawn {

    private static final int MAX_ENEMIES = 18;
    private static final int MAX_TRACKERS = 4;
    private static final int MAX_FAST_ENEMIES = 6;
    private Handler handler;
    private HUD hud;
    private int scoreKeep = 0;
    private Random r = new Random();

    public Spawn(Handler handler, HUD hud) {
        this.handler = handler;
        this.hud = hud;
    }

    public void reset() {
        scoreKeep = 0;
    }

    public void tick() {
        scoreKeep++;

        if(scoreKeep >= 500) {
            scoreKeep = 0;
            hud.setLevel(hud.getLevel() + 1);
            if(handler.getEnemyCount() >= MAX_ENEMIES) {
                return;
            }
            if(hud.getLevel() == 2) {
                spawnBasicEnemy();
            } else if(hud.getLevel() == 3) {
                spawnFastEnemy();
            } else if(hud.getLevel() == 4) {
                spawnTrackEnemy();
            } else if(hud.getLevel() % 3 == 0) {
                spawnTrackEnemy();
            } else if(hud.getLevel() % 2 == 0) {
                spawnFastEnemy();
            } else {
                spawnBasicEnemy();
            }
        }
    }

    private void spawnBasicEnemy() {
        handler.addObject(new BasicEnemy(r.nextInt(Game.WIDTH - 50), r.nextInt(Game.HEIGHT - 50), ID.BasicEnemy, handler));
    }

    private void spawnFastEnemy() {
        if(handler.getObjectCountByID(ID.FastEnemy) >= MAX_FAST_ENEMIES) {
            spawnBasicEnemy();
            return;
        }
        handler.addObject(new FastEnemy(r.nextInt(Game.WIDTH - 50), r.nextInt(Game.HEIGHT - 50), ID.FastEnemy, handler));
    }

    private void spawnTrackEnemy() {
        if(handler.getObjectCountByID(ID.TrackEnemy) >= MAX_TRACKERS) {
            spawnFastEnemy();
            return;
        }
        handler.addObject(new TrackEnemy(r.nextInt(Game.WIDTH - 50), r.nextInt(Game.HEIGHT - 50), ID.TrackEnemy, handler));
    }
}
