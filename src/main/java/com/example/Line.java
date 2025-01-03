package com.example;

import static com.example.Constants.*;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Line {
    float x, y, z, X, Y, W;
    float scale, curve, sprite_x, clip;
    Image sprite;

    public Line(){
        sprite_x = curve = x = y = z = 0;
    }

    public void project(float cam_x, float cam_y, float cam_z){
        scale = CAM_DEPTH / (z-cam_z);
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

        System.out.println("dest_x: " + dest_X + ", dest_y: " + dest_Y);

        double clip_H = dest_Y + dest_H - clip;
        System.out.println("Clip: " + clip + " Clip_H: " + clip_H + " Dest_H: " + dest_H);

        if (clip_H < 0) clip_H = 0;
        if (clip_H >= dest_H) return;

        gc.drawImage(
            sprite,
            0, 0, w, h - h * clip_H / dest_H,
            dest_X, dest_Y, dest_W, dest_H
        );
    }
}

        // gc.drawImage(
        //     background,
        //     0+bg_offset, 0, 600+bg_offset, 300,
        //     0, 0, WIDTH, HEIGHT/1.75
        // );
