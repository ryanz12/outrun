package com.example.controllers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Enemy{
    public GraphicsContext gc;
    public double x, z; // z representing depth
    public double speed;
    public Image sprite;

    public Enemy(GraphicsContext gc, double x, double z, double speed, Image sprite){
        this.gc = gc;
        this.x = x;
        this.z = z;
        this.speed = speed;
        this.sprite = sprite;
   }

}
