package com.main;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Handler {

    ArrayList<GameObject> ob = new ArrayList<GameObject>();
    private ArrayList<GameObject> pendingAdd = new ArrayList<GameObject>();
    private Set<GameObject> pendingRemove = new HashSet<GameObject>();
    private boolean ticking = false;

    public void tick() {
        ticking = true;
        int count = ob.size();
        for(int i = 0; i < count; i++) {
            GameObject tempObject = ob.get(i);
            if(!pendingRemove.contains(tempObject)) {
                tempObject.tick();
            }
        }
        ticking = false;
        flushChanges();
    }

    public void render(Graphics g) {
        sortByDepth();
        for(int i = 0; i < ob.size(); i++) {
            ob.get(i).render(g);
        }
    }

    public void addObject(GameObject object) {
        if(ticking) {
            pendingAdd.add(object);
        } else {
            this.ob.add(object);
        }
    }

    public void removeObject(GameObject object) {
        if(ticking) {
            pendingRemove.add(object);
        } else {
            this.ob.remove(object);
        }
    }

    public void clearObjects() {
        this.ob.clear();
        pendingAdd.clear();
        pendingRemove.clear();
    }

    private void flushChanges() {
        if(!pendingRemove.isEmpty()) {
            ob.removeIf(pendingRemove::contains);
            pendingRemove.clear();
        }
        if(!pendingAdd.isEmpty()) {
            ob.addAll(pendingAdd);
            pendingAdd.clear();
        }
    }

    private void sortByDepth() {
        for(int i = 1; i < ob.size(); i++) {
            GameObject current = ob.get(i);
            double depth = current.getDepth();
            int j = i - 1;
            while(j >= 0 && ob.get(j).getDepth() < depth) {
                ob.set(j + 1, ob.get(j));
                j--;
            }
            ob.set(j + 1, current);
        }
    }

}
