package com.example.controllers;

import static com.example.controllers.Constants.*;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Line {
    public float x, y, z, X, Y, W;
    public float scale, curve, sprite_x, clip;
    public Image sprite;

    public Line(){
        sprite_x = curve = x = y = z = 0;
    }

    public void project(float cam_x, float cam_y, float cam_z){
        scale = CAM_DEPTH / Math.max(0.1f, z - cam_z); //avoid dividing by 0
        X = (1 + scale * (x - cam_x)) * WIDTH / 2;
        Y = (1 - scale * (y - cam_y)) * HEIGHT / 2;
        W = scale * ROAD_WIDTH * WIDTH / 2;
    }

    public void draw_sprite(GraphicsContext gc){
        if (sprite == null) return;

        double w = sprite.getWidth();
        double h = sprite.getHeight();

        double dest_X = X + scale * sprite_x * WIDTH / 2;
        double dest_Y = Y + 2;
        double dest_W = w * W / 266;
        double dest_H = h * W / 266;

        dest_X += dest_W * sprite_x;
        dest_Y += dest_H * (-1);

        // System.out.println("dest_x: " + dest_X + ", dest_y: " + dest_Y);

        double clip_H = dest_Y + dest_H - clip;
        // System.out.println("Clip: " + clip + " Clip_H: " + clip_H + " Dest_H: " + dest_H);

        if (clip_H < 0) clip_H = 0;
        if (clip_H >= dest_H) return;

        gc.drawImage(
            sprite,
            0, 0, w, h - h * clip_H / dest_H,
            dest_X, dest_Y, dest_W, dest_H
        );
    }
}
