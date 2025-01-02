package com.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Player {
    public GraphicsContext gc;
    public Image player_car;
    public enum PLAYER_DIRECTIONS {STRAIGHT, LEFT, RIGHT, UPHILL_STRAIGHT, UPHILL_LEFT, UPHILL_RIGHT, DOWNHILL_STRAIGHT, DOWNHILL_LEFT, DOWNHILL_RIGHT};
    public PLAYER_DIRECTIONS player_direction;
    public Map<PLAYER_DIRECTIONS, List<Image>> car_images;
    public int current_frame = 0;
    private final int SCALE = 3;

    public Player(GraphicsContext gc){
       this.gc = gc;

       // initalize car images
       car_images = new HashMap<>() {{
            put(PLAYER_DIRECTIONS.STRAIGHT, List.of(
                new Image("/player/straight1.png"),
                new Image("/player/straight2.png")
            ));
            put(PLAYER_DIRECTIONS.LEFT, List.of(
                new Image("/player/left_smoke1.png"),
                new Image("/player/left_smoke2.png")
            ));
            put(PLAYER_DIRECTIONS.RIGHT, List.of(
                new Image("/player/right_smoke1.png"),
                new Image("/player/right_smoke2.png")
            ));
            put(PLAYER_DIRECTIONS.UPHILL_STRAIGHT, List.of(
                new Image("/player/uphill_straight1.png"),
                new Image("/player/uphill_straight2.png")
            ));
            put(PLAYER_DIRECTIONS.UPHILL_LEFT, List.of(
                new Image("/player/uphill_left_smoke1.png"),
                new Image("/player/uphill_left_smoke2.png")
            ));
            put(PLAYER_DIRECTIONS.UPHILL_RIGHT, List.of(
                new Image("/player/uphill_right_smoke1.png"),
                new Image("/player/uphill_right_smoke2.png")
            ));
            put(PLAYER_DIRECTIONS.DOWNHILL_STRAIGHT, List.of(
                new Image("/player/downhill_straight1.png"),
                new Image("/player/downhill_straight2.png")
            ));
            put(PLAYER_DIRECTIONS.DOWNHILL_LEFT, List.of(
                new Image("/player/downhill_left_smoke1.png"),
                new Image("/player/downhill_left_smoke2.png")
            ));
            put(PLAYER_DIRECTIONS.DOWNHILL_RIGHT, List.of(
                new Image("/player/downhill_right_smoke1.png"),
                new Image("/player/downhill_right_smoke2.png")
            ));
       }};

       player_car = new Image("/player/straight1.png");
       player_direction = PLAYER_DIRECTIONS.STRAIGHT;
    }

    public void render(double x, double y){
        List <Image> frames = car_images.get(player_direction);
        Image frame = frames.get(current_frame % frames.size());
        gc.drawImage(frame, x, y, frame.getWidth() * SCALE, frame.getHeight() * SCALE);

        current_frame++;
    }

    public void set_direction(PLAYER_DIRECTIONS dir){
        if (player_direction != dir) this.player_direction = dir;
        else return;

        current_frame = 0;
    }
}
