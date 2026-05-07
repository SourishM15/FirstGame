package com.main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {

    private Handler handler;
    private Game game;
    private boolean[] keyDown = new boolean[4];

    public KeyInput(Handler handler, Game game) {
        this.handler = handler;
        this.game = game;

        keyDown[0] = false;
        keyDown[1] = false;
        keyDown[2] = false;
        keyDown[3] = false;

    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if(key == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
        if(key == KeyEvent.VK_ENTER && Game.gameState == Game.State.Menu) {
            game.startGame();
            return;
        }
        if(key == KeyEvent.VK_R && Game.gameState != Game.State.Menu) {
            game.startGame();
            return;
        }
        if(key == KeyEvent.VK_P) {
            if(Game.gameState == Game.State.Game) {
                stopMovement();
                game.setShooting(false);
                Game.gameState = Game.State.Paused;
            } else if(Game.gameState == Game.State.Paused) {
                Game.gameState = Game.State.Game;
            }
            return;
        }
        if(Game.gameState != Game.State.Game) {
            return;
        }
        if(key == KeyEvent.VK_SPACE) {
            game.setShooting(true);
            return;
        }

        for(int i = 0; i < handler.ob.size(); i++) {
            GameObject tempObject = handler.ob.get(i);

            if(tempObject.getID() == ID.Player) {
                //Key event for Player 1

                if(key == KeyEvent.VK_W) {
                    tempObject.setVelY(-5);
                    keyDown[0] = true;
                }
                if(key == KeyEvent.VK_S) {
                    tempObject.setVelY(5);
                    keyDown[1] = true;
                }
                if(key == KeyEvent.VK_D) {
                    tempObject.setVelX(5);
                    keyDown[2] = true;
                }
                if(key == KeyEvent.VK_A) {
                    tempObject.setVelX(-5);
                    keyDown[3] = true;
                }
            }

        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if(key == KeyEvent.VK_SPACE) {
            game.setShooting(false);
            return;
        }
        if(Game.gameState != Game.State.Game) {
            return;
        }

        for(int i = 0; i < handler.ob.size(); i++) {
            GameObject tempObject = handler.ob.get(i);

            if(tempObject.getID() == ID.Player) {
                //Key event for Player 1

                if(key == KeyEvent.VK_W) {
                    keyDown[0] = false;
                }
                if(key == KeyEvent.VK_S) {
                    keyDown[1] = false;
                }
                if(key == KeyEvent.VK_D) {
                    keyDown[2] = false;
                }
                if(key == KeyEvent.VK_A) {
                    keyDown[3] = false;
                }

                if(!keyDown[0] && !keyDown[1]) {
                    tempObject.setVelY(0);
                }
                if(!keyDown[2] && !keyDown[3]) {
                    tempObject.setVelX(0);
                }
            }
        }
    }

    private void stopMovement() {
        for(int i = 0; i < keyDown.length; i++) {
            keyDown[i] = false;
        }
        for(int i = 0; i < handler.ob.size(); i++) {
            GameObject tempObject = handler.ob.get(i);
            if(tempObject.getID() == ID.Player) {
                tempObject.setVelX(0);
                tempObject.setVelY(0);
            }
        }
    }

}
