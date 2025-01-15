package com.example.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.controllers.Player.PLAYER_DIRECTIONS;
import static com.example.controllers.Constants.*;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class GameController{
    // ---------- INITALIZE VARIAVLES ----------
    public ArrayList <Line> lines = new ArrayList<>();
    public int N = 16000;
    public double pos = 0;
    public double vel = 0;
    public double d_vel = 0;
    public int turn_vel = 200;
    public int player_x = 0;

    public Map <String, Image> game_objects;
    public Set<KeyCode> active_keys = new HashSet<>();
    public Player player;

    public double bg_offset = 0;
    public Image background = new Image("backgrounds/night_sky.png");

    public boolean game_started = false;
    public int countdown = 3;
    public long start_time, seconds_elapsed;

    @FXML
    public Canvas canvas;

    @FXML
    public AnchorPane anchor_pane;

    @FXML
    public Label speed_label, cd_label, time_label;

    // ---------- 2D TO 3D PROJECTION (QUADRILATERAL) ----------
    //
    //                             w1
    //                ----------------------------
    //               /              *             \
    //              /            (x2,y2)           \
    //             /                                \
    //            /                                  \
    //           /                                    \
    //          /                                      \
    //         /                                        \
    //        /                                          \
    //       /                   (x1,y1)                  \
    //      /                       *                      \
    //     --------------------------------------------------
    //                             w2
    //
    public void draw_quad(GraphicsContext gc, Color color, int x1, int y1, int w1, int x2, int y2, int w2){
        gc.beginPath();
        gc.setFill(color);

        gc.lineTo(x1-w1, y1);
        gc.lineTo(x2-w2, y2);
        gc.lineTo(x2+w2, y2);
        gc.lineTo(x1+w1, y1);

        gc.fill();
        gc.closePath();
    }

    public void listen(Scene scene){
        scene.setOnKeyPressed(e -> active_keys.add(e.getCode()));
        scene.setOnKeyReleased(e -> active_keys.remove(e.getCode()));

        countdown();
    }

    public void countdown(){
        game_started = false;

        Timeline cd_timer = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                if (countdown > 0){
                    cd_label.setText(String.valueOf(countdown));
                    countdown--;
                }
                else {
                    game_started = true;
                    cd_label.setVisible(false);
                    start_time = System.currentTimeMillis();
                }
            })
        );
        cd_timer.setCycleCount(countdown + 1);
        cd_timer.play();
    }

    // public void elapsed_timer(){
    //     start_time = System.currentTimeMillis();

    //     new AnimationTimer(){
    //         @Override
    //         public void handle(long now){
    //             long elapsed_millis = System.currentTimeMillis() - start_time;
    //             seconds_elapsed = (elapsed_millis / 1000) % 60;
    //             time_label.setText("Time: " + seconds_elapsed);
    //             System.out.println(seconds_elapsed);
    //         }
    //     }.start();
    // }

    // ---------- INITIALIZE GAME ----------
    @FXML
    public void initialize(){

        game_objects = new HashMap<>() {{
            put("tree", new Image("objects/tree.png"));
            put("palm_tree", new Image("objects/palm_tree.png"));
            put("bush", new Image("objects/bush.png"));
            put("finish", new Image("objects/finish_line.png"));
            put("start", new Image("objects/start.png"));
            put("lamp_left", new Image("objects/light_post1.png"));
            put("lamp_right", new Image("objects/light_post2.png"));

        }};

        GraphicsContext gc = canvas.getGraphicsContext2D();

        player = new Player(gc);

        // Create the road
        for (int i=0; i<N; i++){
            Line line = new Line();
            line.z = i * SEG_LENGTH;


            // curve
            if (i > 300 && i < 700) line.curve = .5f;

            // hill
            if (i > 750 && i < 1000) line.y = (float)(Math.sin(i / 30.0) * 1500);

            if (i > 1000 && i < 1600) line.curve = -2.5f;

            // obects
            if (i % 20 == 0) {
                line.sprite_x = -2.5f;
                line.sprite = game_objects.get("tree");
            }

            if (i > 300 && i % 19 == 0){
                line.sprite_x = 2.5f;
                line.sprite = game_objects.get("palm_tree");
            }

            if (i == 150){
                line.sprite_x = -0.5f;
                line.sprite = game_objects.get("start");
            }

            if (i==1200){
                line.sprite_x = -.5f;
                line.sprite = game_objects.get("finish");
            }

            lines.add(line);
        }

        // main game loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now){
                update();
                render(gc);
            }
        };

        timer.start();
    }

    // ---------- RENDERING ROAD ----------
    public void render_road(GraphicsContext gc){
        int start_pos = (int)(pos/SEG_LENGTH);
        float cam_height = 1500 + lines.get(start_pos).y;
        float x = 0, dx = 0;
        float max_y = HEIGHT;

        for (int n=start_pos; n<start_pos+500; n++){
            Line cur_line = lines.get(n % N);

            // Calculate 2d to 3d projection
            cur_line.project(player_x - x, cam_height, (int)pos);

            // Calculate curvature of curve
            x += dx;
            dx += cur_line.curve;
            cur_line.clip = max_y;

            if (cur_line.Y >= max_y) continue;
            max_y = cur_line.Y;

            Color grass = (n/3)%2==1?Color.rgb(16,200,16):Color.rgb(0,154,0);
            Color rumble = (n/3)%2==1?Color.rgb(255,255,255):Color.rgb(0,0,0);
            Color road = (n/3)%2==1?Color.rgb(107,107,107):Color.rgb(105,105,105);
            Color road_lines = (n/3)%2==1?Color.rgb(255, 255, 255):Color.rgb(106, 106, 106);

            Line prev_line = lines.get((n-1+N)%N);

            draw_quad(gc, grass, 0, (int)prev_line.Y, WIDTH, 0, (int)cur_line.Y, WIDTH);
            draw_quad(gc, rumble, (int)prev_line.X, (int)prev_line.Y, (int)(prev_line.W*SEG_WIDTH_M), (int)cur_line.X, (int)cur_line.Y, (int)(cur_line.W*SEG_WIDTH_M));
            draw_quad(gc, road, (int)prev_line.X, (int)prev_line.Y, (int)prev_line.W, (int)cur_line.X, (int)cur_line.Y, (int)cur_line.W);
            draw_quad(gc, road_lines, (int)prev_line.X, (int)prev_line.Y, (int)prev_line.W/3, (int)cur_line.X, (int)cur_line.Y, (int)cur_line.W/3);
            draw_quad(gc, road, (int)prev_line.X, (int)prev_line.Y, (int)(prev_line.W/3.4), (int)cur_line.X, (int)cur_line.Y, (int)(cur_line.W/3.4));
        }


        for (int n=start_pos + 500; n>start_pos; n--){
            lines.get(n%N).draw_sprite(gc);
        }

    }

    // ---------- rendering game components ----------
    public void render(GraphicsContext gc) {
        // Clear screen
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        double adjusted_bg_offset = bg_offset % background.getWidth();
        if (adjusted_bg_offset < 0) {
            adjusted_bg_offset += background.getWidth(); // Fix for negative offsets
        }

        // Draw the first background
        gc.drawImage(
            background,
            0, 0, 800, 300,
            -adjusted_bg_offset, 0, WIDTH, HEIGHT / 1.75
        );

        // Draw the second background if the first one has gone off-screen
        if (adjusted_bg_offset > 0) {
            gc.drawImage(
                background,
                0, 0, 800, 300,
                background.getWidth() - adjusted_bg_offset, 0, WIDTH, HEIGHT / 1.75
            );
        }

        render_road(gc);
        player.render(WIDTH / 2, HEIGHT - 200);

    }

    public void update() {
        // skip game logic if game not started
        if (!game_started) return;

        // Calc time passed
        long elapsed_millis = System.currentTimeMillis() - start_time;
        seconds_elapsed = (elapsed_millis / 1000) % 60;
        time_label.setText("Time: " + seconds_elapsed);

        int seg_index = (int)(pos/SEG_LENGTH);
        boolean uphill = is_uphill(seg_index);
        boolean downhill = is_downhill(seg_index);
        boolean offroad = player_x <= -ROAD_WIDTH || player_x + player.get_width() >= ROAD_WIDTH;

        if (active_keys.contains(KeyCode.W)) {

            d_vel += 0.1;

            // cap acceleration limit
            if (d_vel > MAX_ACCEL) d_vel = MAX_ACCEL;
            vel += d_vel;

            // cap speed limit
            if (vel > MAX_VEL) vel = MAX_VEL;

            // outside road
            if (offroad){
                vel *= 0.27;
                if (vel < 50) vel = 50;
            }

            // centrifrugal force
            Line c_line = lines.get(seg_index % N);
            if (c_line.curve != 0){
                player_x -= c_line.curve*CENTRIFRUGAL_FORCE_M;
            }

            if (uphill) player.set_direction(PLAYER_DIRECTIONS.UPHILL_STRAIGHT);
            else if (downhill) player.set_direction(PLAYER_DIRECTIONS.DOWNHILL_STRAIGHT);
            else player.set_direction(PLAYER_DIRECTIONS.STRAIGHT);
        }
        else {
            // decel naturally
            d_vel -= 0.15;
            if (d_vel < 0 ) d_vel = 0;

            // when d_vel reaches 0 but the car is still going
            if (d_vel == 0 && vel > 0){
                vel -= 5;
            }

            vel -= d_vel;
            if (vel < 0) vel = 0;
        }


        System.out.println("vel: " + vel + " d_vel: " + d_vel);

        if (active_keys.contains(KeyCode.A)) {
            player_x -= vel * 0.2;
            handle_turning_dur(uphill, downhill, "l", seg_index);
        }
        if (active_keys.contains(KeyCode.D)) {
            player_x += vel * 0.2;
            handle_turning_dur(uphill, downhill, "", seg_index);
        }

        //debuggin
        if (active_keys.contains(KeyCode.S)) {
            vel = -400;
        }

        // Background moving effect
        pos += vel;
        if (vel > 0) {
            bg_offset += lines.get(seg_index).curve * SCROLL_SPEED;
        }
        if (vel < 0) {
            bg_offset -= lines.get(seg_index).curve * SCROLL_SPEED;
        }


        // Speed label
        speed_label.setText(round(vel*0.25, 1) + " KPH");
    }

    private void handle_turning_dur(boolean up, boolean down, String dir, int seg_index){

        // use smoke animation when vel over 300 and road is not straight
        if (vel > 300 && lines.get(seg_index % N).curve != 0){
            // turning left
            if (dir.equals("l")){
                if (up) player.set_direction(PLAYER_DIRECTIONS.UH_LEFT_SMOKE);
                else if (down) player.set_direction(PLAYER_DIRECTIONS.DH_LEFT_SMOKE);
                else player.set_direction(PLAYER_DIRECTIONS.LEFT_SMOKE);
            }
            // turning right
            else {
                if (up) player.set_direction(PLAYER_DIRECTIONS.UH_RIGHT_SMOKE);
                else if (down) player.set_direction(PLAYER_DIRECTIONS.DH_RIGHT_SMOKE);
                else player.set_direction(PLAYER_DIRECTIONS.RIGHT_SMOKE);
            }
        }
        // normal turn animations
        else {
            if (dir.equals("l")){
                if (up) player.set_direction(PLAYER_DIRECTIONS.UPHILL_LEFT);
                else if (down) player.set_direction(PLAYER_DIRECTIONS.DOWNHILL_LEFT);
                else player.set_direction(PLAYER_DIRECTIONS.LEFT);
            }
            // turning right
            else {
                if (up) player.set_direction(PLAYER_DIRECTIONS.UPHILL_RIGHT);
                else if (down) player.set_direction(PLAYER_DIRECTIONS.DOWNHILL_RIGHT);
                else player.set_direction(PLAYER_DIRECTIONS.RIGHT);
            }
        }
    }


    private double round(double num, int decimals){
        return Math.round(num * Math.pow(10, decimals))/Math.pow(10,decimals);
    }

    private boolean is_uphill(int seg_index){
        return lines.get(seg_index).y > lines.get((seg_index - 1 + N) % N).y;
    }

    private boolean is_downhill(int seg_index){
        return lines.get(seg_index).y < lines.get((seg_index - 1 + N) % N).y;
    }
}
