package com.main;

import java.awt.Graphics;
import java.util.LinkedList;

public class Handler {

    LinkedList<GameObject> ob = new LinkedList<GameObject>();

    public void tick() {
        for(int i = 0; i < ob.size(); i++) {
            GameObject tempObject = ob.get(i);

            tempObject.tick();
        }
    }

    public void render(Graphics g) {
        for(int i = 0; i < ob.size(); i++) {
            GameObject tempObject = ob.get(i);

            tempObject.render(g);
        }
    }

    public void addObject(GameObject object) {
        this.ob.add(object);
    }

    public void removeObject(GameObject object) {
        this.ob.remove(object);
    }

}
