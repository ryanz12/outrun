package com.example.controllers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Enemy {
    // private Image enemy_car;
    // private final int SCALE = 3;
    // private GraphicsContext gc;
    // private double x, z;

    // public Enemy(GraphicsContext gc, Image enemy_car, double x, double z){
    //     this.gc = gc;
    //     this.enemy_car = enemy_car;
    //     this.x = x;
    // }
    public double x, z;
    public double speed;
    public Image sprite;

    public Enemy(double x, double z, double speed, Image sprite){
        this.x = x;
        this.z = z;
        this.speed = speed;
        this.sprite = sprite;
    }

    public void render(GraphicsContext gc, Line line){
        line.sprite = sprite;
        line.sprite_x = (float)x;
        line.draw_sprite(gc);
    }

    public void update(double player_speed){
        z -= player_speed - speed;

        if (Math.random() < 0.05){
            x += (Math.random() > 0.5 ? 1 : -1) * 0.2;
        }
    }
}
