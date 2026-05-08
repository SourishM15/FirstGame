package com.main;

import java.awt.*;
import java.util.Random;
import java.awt.image.BufferStrategy;
import java.io.Serial;


public class Game extends Canvas implements Runnable {

    @Serial
    private static final long serialVersionUID = 1550691097823471818L;
    public static final int WIDTH = 640, HEIGHT = WIDTH / 12 * 9;
    private static final double TICKS_PER_SECOND = 60.0;
    private static final double MAX_FRAMES_PER_SECOND = 120.0;

    public enum State {
        Menu,
        Game,
        Paused,
        GameOver
    }

    public static State gameState = State.Menu;
    private Thread thread;
    private boolean running = false;

    private Handler handler;
    private Random r;
    private HUD hud;
    private Spawn spawner;
    private int shootCooldown = 0;
    private double[] starWorldX;
    private double[] starWorldY;
    private double[] starWorldZ;
    private int[] starTint;
    private boolean shooting = false;
    private GradientPaint spacePaint;
    private RadialGradientPaint nebulaOne;
    private RadialGradientPaint nebulaTwo;
    private static final Color GRID_COLOR = new Color(54, 97, 153, 92);
    private static final Color MENU_OVERLAY = new Color(0, 0, 0, 165);
    private static final Color[][] STAR_COLORS = createStarColors();

    public Game() {
        r = new Random();
        handler = new Handler();
        hud = new HUD();
        spawner = new Spawn(handler, hud);
        createStars();
        createBackgroundPaints();
        this.addKeyListener(new KeyInput(handler, this));

        new Window(WIDTH, HEIGHT, "Starfall Defender", this);
    }

    public void startGame() {
        resetGame();
        gameState = State.Game;
    }

    public void resetGame() {
        HUD.HEALTH = 100;
        handler.clearObjects();
        hud.reset();
        spawner.reset();
        shootCooldown = 0;
        shooting = false;
        handler.addObject(new Player(WIDTH/2-32,HEIGHT/2-3, ID.Player, handler));
        handler.addObject(new BasicEnemy(r.nextInt(WIDTH - 50), r.nextInt(HEIGHT - 50), ID.BasicEnemy, handler));
    }

    public void fireBullet() {
        if(gameState != State.Game || shootCooldown > 0) {
            return;
        }

        for(int i = 0; i < handler.ob.size(); i++) {
            GameObject tempObject = handler.ob.get(i);
            if(tempObject.getID() == ID.Player) {
                handler.addObject(new Bullet(tempObject.getX() + 14, tempObject.getY() - 12, ID.Bullet, handler, hud));
                shootCooldown = 12;
                return;
            }
        }
    }

    public void setShooting(boolean shooting) {
        this.shooting = shooting;
    }

    public synchronized void start() {
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        if(Thread.currentThread() == thread) {
            return;
        }
        try {
            thread.join();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        long lastTime = System.nanoTime();
        long lastRenderTime = lastTime;
        double tickNs = 1000000000 / TICKS_PER_SECOND;
        double renderNs = 1000000000 / MAX_FRAMES_PER_SECOND;
        double tickDelta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        running = true;
        while(running){
            long now = System.nanoTime();
            tickDelta += (now - lastTime) / tickNs;
            lastTime = now;

            while(tickDelta >= 1) {
                tick();
                tickDelta--;
            }

            boolean rendered = false;
            if(running && now - lastRenderTime >= renderNs) {
                render();
                frames++;
                lastRenderTime = now;
                rendered = true;
            }

            if(!rendered) {
                sleep();
            }

            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames + " | Objects: " + handler.getObjectCount() + " | Enemies: " + handler.getEnemyCount());
                frames = 0;
            }

        }
        stop();
    }

    private void sleep() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void tick() {
        if(gameState == State.Game) {
            tickStars();
            if(shootCooldown > 0) {
                shootCooldown--;
            }
            if(shooting) {
                fireBullet();
            }
            handler.tick();
            hud.tick();
            spawner.tick();

            if(HUD.HEALTH <= 0) {
                HUD.HEALTH = 0;
                hud.saveHighScore();
                gameState = State.GameOver;
            }
        }
    }

    private void render() {

        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        renderSpace(g);

        if(gameState == State.Game || gameState == State.Paused || gameState == State.GameOver) {
            handler.render(g);
        }

        if(gameState != State.Menu) {
            hud.render(g);
        }

        if(gameState == State.Menu) {
            renderMenu(g);
        } else if(gameState == State.Paused) {
            renderCenteredText(g, "Paused", "Press P to resume", "Press R to restart");
        } else if(gameState == State.GameOver) {
            renderCenteredText(g, "Game Over", "Press R to play again", "Score: " + hud.getScore());
        }

        g.dispose();
        bs.show();

    }

    private void renderMenu(Graphics g) {
        renderCenteredText(g, "Starfall Defender", "Press Enter to launch", "WASD to move - Space to fire");
    }

    private void renderCenteredText(Graphics g, String title, String lineOne, String lineTwo) {
        Font titleFont = new Font("Arial", Font.BOLD, 42);
        Font bodyFont = new Font("Arial", Font.PLAIN, 18);
        FontMetrics titleMetrics = g.getFontMetrics(titleFont);
        FontMetrics bodyMetrics = g.getFontMetrics(bodyFont);

        int titleY = HEIGHT / 2 - 45;
        g.setColor(MENU_OVERLAY);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.white);
        g.setFont(titleFont);
        g.drawString(title, (WIDTH - titleMetrics.stringWidth(title)) / 2, titleY);
        g.setFont(bodyFont);
        g.drawString(lineOne, (WIDTH - bodyMetrics.stringWidth(lineOne)) / 2, titleY + 45);
        g.drawString(lineTwo, (WIDTH - bodyMetrics.stringWidth(lineTwo)) / 2, titleY + 72);
    }

    private void createStars() {
        starWorldX = new double[180];
        starWorldY = new double[180];
        starWorldZ = new double[180];
        starTint = new int[180];

        for(int i = 0; i < starWorldX.length; i++) {
            resetStar(i, r.nextInt((int) Renderer3D.FAR_DEPTH));
        }
    }

    private void tickStars() {
        for(int i = 0; i < starWorldZ.length; i++) {
            starWorldZ[i] -= 12;
            if(starWorldZ[i] < 70) {
                resetStar(i, (int) Renderer3D.FAR_DEPTH);
            }
        }
    }

    private void renderSpace(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(spacePaint);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        renderNebula(g2d);

        g.setColor(GRID_COLOR);
        for(int i = 0; i < 12; i++) {
            int y = HEIGHT / 2 + i * 24;
            g.drawLine(WIDTH / 2 - 14 - i * 45, y, WIDTH / 2 + 14 + i * 45, y);
        }
        for(int i = -6; i <= 6; i++) {
            g.drawLine(WIDTH / 2, HEIGHT / 2, WIDTH / 2 + i * 72, HEIGHT);
        }

        for(int i = 0; i < starWorldX.length; i++) {
            double scale = Renderer3D.scale(starWorldZ[i]);
            int pointX = Renderer3D.projectX(starWorldX[i], starWorldZ[i]);
            int pointY = Renderer3D.projectY(starWorldY[i], starWorldZ[i]);
            int brightness = Game.clamp((int) (95 + scale * 120), 95, 255);
            int size = Game.clamp((int) Math.round(scale * 2.6), 1, 6);
            g.setColor(starColor(brightness, starTint[i]));
            g.fillOval(pointX, pointY, size, size);
            if(scale > 1.6) {
                g.drawLine(pointX, pointY - size * 4, pointX, pointY + size);
            }
        }
    }

    private void resetStar(int index, int depthOffset) {
        starWorldX[index] = -420 + r.nextInt(840);
        starWorldY[index] = -260 + r.nextInt(520);
        starWorldZ[index] = 120 + depthOffset + r.nextInt(420);
        starTint[index] = r.nextInt(4);
    }

    private void renderNebula(Graphics2D g2d) {
        g2d.setPaint(nebulaOne);
        g2d.fillOval(-110, -120, 520, 420);
        g2d.setPaint(nebulaTwo);
        g2d.fillOval(285, 155, 430, 330);
    }

    private void createBackgroundPaints() {
        spacePaint = new GradientPaint(0, 0, new Color(3, 6, 19), 0, HEIGHT, new Color(17, 8, 31));
        nebulaOne = new RadialGradientPaint(
                new Point(145, 115),
                240,
                new float[]{0f, 0.52f, 1f},
                new Color[]{new Color(70, 38, 118, 72), new Color(22, 53, 104, 34), new Color(0, 0, 0, 0)}
        );
        nebulaTwo = new RadialGradientPaint(
                new Point(500, 335),
                210,
                new float[]{0f, 0.58f, 1f},
                new Color[]{new Color(20, 112, 132, 58), new Color(58, 22, 88, 28), new Color(0, 0, 0, 0)}
        );
    }

    private Color starColor(int brightness, int tint) {
        return STAR_COLORS[tint][brightness];
    }

    private static Color[][] createStarColors() {
        Color[][] colors = new Color[4][256];
        for(int brightness = 0; brightness < colors[0].length; brightness++) {
            colors[0][brightness] = new Color(brightness, brightness, 255);
            colors[1][brightness] = new Color(170, brightness, 255);
            colors[2][brightness] = new Color(brightness, 235, 205);
            colors[3][brightness] = new Color(210, brightness, brightness);
        }
        return colors;
    }

    public static int clamp(int var, int min, int max) {
        if(var >= max)
            return var = max;
        else if(var <= min)
            return var = min;
        else
            return var;
    }

    public static void main(String[] args) {
        new Game();
    }
}


