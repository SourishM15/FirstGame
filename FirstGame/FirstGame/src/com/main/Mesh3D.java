package com.main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class Mesh3D {

    private static final int MAX_VERTICES = 48;
    private static final int MAX_FACES = 64;
    private static final int MAX_FACE_POINTS = 8;
    private static final int[] projectedX = new int[MAX_VERTICES];
    private static final int[] projectedY = new int[MAX_VERTICES];
    private static final double[] transformedX = new double[MAX_VERTICES];
    private static final double[] transformedY = new double[MAX_VERTICES];
    private static final double[] transformedZ = new double[MAX_VERTICES];
    private static final int[] faceOrder = new int[MAX_FACES];
    private static final double[] faceDepth = new double[MAX_FACES];
    private static final int[] polygonX = new int[MAX_FACE_POINTS];
    private static final int[] polygonY = new int[MAX_FACE_POINTS];
    private static final Color OUTLINE_COLOR = new Color(235, 248, 255, 95);

    public static void draw(
            Graphics2D g,
            double centerX,
            double centerY,
            double centerZ,
            double[][] vertices,
            int[][] faces,
            Color[] colors,
            double yaw,
            double pitch,
            double roll
    ) {
        double cosY = Math.cos(yaw);
        double sinY = Math.sin(yaw);
        double cosP = Math.cos(pitch);
        double sinP = Math.sin(pitch);
        double cosR = Math.cos(roll);
        double sinR = Math.sin(roll);

        for(int i = 0; i < vertices.length; i++) {
            double x = vertices[i][0];
            double y = vertices[i][1];
            double z = vertices[i][2];
            double yawX = x * cosY + z * sinY;
            double yawZ = -x * sinY + z * cosY;
            double pitchY = y * cosP - yawZ * sinP;
            double pitchZ = y * sinP + yawZ * cosP;
            double rollX = yawX * cosR - pitchY * sinR;
            double rollY = yawX * sinR + pitchY * cosR;

            transformedX[i] = centerX + rollX;
            transformedY[i] = centerY + rollY;
            transformedZ[i] = centerZ + pitchZ;
            projectedX[i] = Renderer3D.projectX(transformedX[i], transformedZ[i]);
            projectedY[i] = Renderer3D.projectY(transformedY[i], transformedZ[i]);
        }

        for(int i = 0; i < faces.length; i++) {
            faceOrder[i] = i;
            faceDepth[i] = averageDepth(faces[i]);
        }
        sortFaces(faces.length);

        Object oldAntialias = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for(int i = 0; i < faces.length; i++) {
            int faceIndex = faceOrder[i];
            int[] face = faces[faceIndex];

            for(int j = 0; j < face.length; j++) {
                polygonX[j] = projectedX[face[j]];
                polygonY[j] = projectedY[face[j]];
            }

            g.setColor(shade(colors[faceIndex % colors.length], face));
            g.fillPolygon(polygonX, polygonY, face.length);
            g.setColor(OUTLINE_COLOR);
            g.drawPolygon(polygonX, polygonY, face.length);
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntialias);
    }

    private static double averageDepth(int[] face) {
        double total = 0;
        for(int i = 0; i < face.length; i++) {
            total += transformedZ[face[i]];
        }
        return total / face.length;
    }

    private static void sortFaces(int count) {
        for(int i = 1; i < count; i++) {
            int order = faceOrder[i];
            double depth = faceDepth[i];
            int j = i - 1;
            while(j >= 0 && faceDepth[j] < depth) {
                faceOrder[j + 1] = faceOrder[j];
                faceDepth[j + 1] = faceDepth[j];
                j--;
            }
            faceOrder[j + 1] = order;
            faceDepth[j + 1] = depth;
        }
    }

    private static Color shade(Color base, int[] face) {
        if(face.length < 3) {
            return base;
        }

        int a = face[0];
        int b = face[1];
        int c = face[2];
        double ux = transformedX[b] - transformedX[a];
        double uy = transformedY[b] - transformedY[a];
        double uz = transformedZ[b] - transformedZ[a];
        double vx = transformedX[c] - transformedX[a];
        double vy = transformedY[c] - transformedY[a];
        double vz = transformedZ[c] - transformedZ[a];

        double nx = uy * vz - uz * vy;
        double ny = uz * vx - ux * vz;
        double nz = ux * vy - uy * vx;
        double length = Math.sqrt(nx * nx + ny * ny + nz * nz);
        if(length == 0) {
            return base;
        }

        nx /= length;
        ny /= length;
        nz /= length;
        double light = Math.max(0, nx * -0.28 + ny * -0.52 + nz * -0.8);
        double depthGlow = Math.max(0, Math.min(1, (Renderer3D.FAR_DEPTH - averageDepth(face)) / Renderer3D.FAR_DEPTH));
        double factor = 0.52 + light * 0.55 + depthGlow * 0.16;

        return new Color(
                clampColor((int) (base.getRed() * factor)),
                clampColor((int) (base.getGreen() * factor)),
                clampColor((int) (base.getBlue() * factor)),
                base.getAlpha()
        );
    }

    private static int clampColor(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
