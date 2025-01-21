package com.example.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.example.controllers.Player.PLAYER_DIRECTIONS;
import static com.example.controllers.Constants.*;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
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
    public Image background = null;

    public boolean game_started = false;
    public int countdown = 3;
    public long start_time, seconds_elapsed;

    public long total_time = 120;

    public Random rand = new Random();

    private MediaPlayer theme_player, mp;

    public boolean paused = false;

    @FXML
    public Canvas canvas;

    @FXML
    public AnchorPane anchor_pane, pause_ui;

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
        scene.setOnKeyPressed(e -> {
            active_keys.add(e.getCode());
            if (e.getCode() == KeyCode.ESCAPE){
                paused = !paused;
            }
        });
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

    // ---------- INITIALIZE GAME ----------
    @FXML
    public void initialize(){
        pause_ui.setVisible(false);
        String map = MapController.map;
        System.out.println(map);

        Media m = new Media(getClass().getResource("/sounds/theme.mp3").toExternalForm());
        theme_player = new MediaPlayer(m);
        theme_player.setVolume(SettingsController.volume);
        theme_player.play();


        if (map.equals("Shanghai")){
            background = new Image("backgrounds/night_sky.png");
        }

        else if (map.equals("Cairo")){
            background = new Image("backgrounds/egypt.png");
        }

        game_objects = new HashMap<>() {{
            put("tree", new Image("objects/tree.png"));
            put("palm_tree", new Image("objects/palm_tree.png"));
            put("bush", new Image("objects/bush.png"));
            put("finish", new Image("objects/finish_line.png"));
            put("start", new Image("objects/start.png"));
            put("lamp_left", new Image("objects/light_post1.png"));
            put("lamp_right", new Image("objects/light_post2.png"));
            put("bush2", new Image("objects/bush2.png"));
            put("bush3", new Image("objects/bush3.png"));
            put("palm_tree2", new Image("objects/palm-tree2.png"));
            put("boulder1", new Image("objects/boulder1.png"));
            put("boulder2", new Image("objects/boulder2.png"));
            put("bill1", new Image("objects/billboard02.png"));
            put("bill2", new Image("objects/billboard03.png"));
            put("bill3", new Image("objects/billboard04.png"));
            put("house", new Image("objects/boat_house.png"));
        }};

        GraphicsContext gc = canvas.getGraphicsContext2D();

        player = new Player(gc);

        // Create the road
        for (int i=0; i<N; i++){
            Line line = new Line();
            line.z = i * SEG_LENGTH;

            // Check and create different maps

            if (map.equals("Shanghai")){

                // Curve
                if (i > 200 && i < 500){
                    line.curve = 2.5f;
                }

                if (i > 500 && i < 630){
                    line.y = (float)(Math.sin(i / 40.0) * 2500);
                }

                if (i > 630 && i < 750){
                    line.curve = -3f;
                }

                if (i > 750 && i < 1010){
                    line.y = (float)(Math.sin(i / 40.0) * -2500);
                    line.curve = .5f;
                }

                if (i > 1010 && i < 1200){
                    line.curve = 3f;
                }

                if (i > 1200 && i < 1500){
                    line.curve = -5f;
                }

                if (i > 1500 && i < 2010){
                    line.y = (float)(Math.sin(i / 40.0) * 4000);
                }

                if (i > 2500 && i < 2700){
                    line.curve = -3f;
                }

                if (i > 2700 && i < 3000){
                    line.curve = 3f;
                }

                if (i > 3000 && i < 3600){
                    line.curve = (float)(Math.sin(i / 50.0)*7);
                    line.y = (float)(Math.sin(i / 50.0) * 1500);
                }

                if (i > 3600 && i < 4000){
                    line.curve = (float)(Math.cos(i / 30.0) * rand.nextInt(1, 6));
                }

                if (i > 4000 && i < 4400){
                    line.curve = -3f;
                }

                if (i > 4500 && i < 4800){
                    line.y = (float)(Math.sin(i / 40.0) * 1500);
                }

                if (i > 4900 && i < 5400){
                    line.y = (float)(Math.sin(i / 40.0) * 1500);
                    line.curve = (float)(-Math.sin(i / 100.0) * rand.nextInt(4, 9));
                }

                if (i > 6000 && i < 6400){
                    line.curve = 5f;
                }
                
                if (i > 7000 && i < 7400){
                    line.y = (float)(Math.sin(i / 40.0) * 1500);
                    line.curve = 2;
                }

                if (i > 7500 && i < 7900){
                    line.curve = (float)(-Math.cos(i / 30) * rand.nextInt(2, 6));
                }

                if (i > 8000 && i < 8500){
                    line.y = (float)(Math.abs(Math.cos(i / 30.0) * 1500));
                }
                
                if (i > 8500){
                    line.y = (float)(Math.sin(i / 30.0) * 1500); 
                }
                

                if (i % 10 == 0){
                    line.sprite_x = -rand.nextFloat(3,9);
                    line.sprite = game_objects.get("tree");
                }

                if (i % 15 == 0){
                    line.sprite_x = rand.nextFloat(3,9);
                    line.sprite = game_objects.get("tree");
                }

                if (i % 19 == 0){
                    line.sprite_x = -rand.nextFloat(3, 9);
                    line.sprite = game_objects.get("bush");
                }

                if (i % 200 == 0){
                    line.sprite_x = -rand.nextFloat(3, 9);
                    line.sprite = game_objects.get("palm_tree");
                }

                if (i % 18 == 0){
                    line.sprite_x = rand.nextFloat(5, 10);
                    line.sprite = game_objects.get("boulder1");
                }

                if (i % 21 == 0){
                    line.sprite_x = rand.nextFloat(8, 13);
                    line.sprite = game_objects.get("boulder2");
                }

                if (i % 22 == 0){
                    line.sprite_x = 3f;
                    line.sprite = game_objects.get("lamp_right");
                }

                if (i % 23 == 0){
                    line.sprite_x = -3f;
                    line.sprite = game_objects.get("lamp_left");
                }

                if (i % 600 == 0){
                    line.sprite_x = -5f;
                    line.sprite = game_objects.get("house");
                }

                if (i % 500 == 0){
                    line.sprite_x = 5f;
                    line.sprite = game_objects.get("bill1");
                }
            }
            else if (map.equals("Cairo")){
                
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

            Color grass, rumble, road, road_lines;
            grass = rumble = road = road_lines = Color.rgb(0,0,0);

            if (cur_line.Y >= max_y) continue;
            max_y = cur_line.Y;
            
            if(MapController.map.equals("Shanghai")){
                grass = (n/3)%2==1?Color.rgb(0, 17, 61):Color.rgb(1, 29, 71);
                rumble = (n/3)%2==1?Color.rgb(66, 83, 97):Color.rgb(82, 96, 115);
                road = (n/3)%2==1?Color.rgb(49, 64, 76):Color.rgb(34, 54, 56);
                road_lines = (n/3)%2==1?Color.rgb(161, 178, 186):Color.rgb(34, 54, 56);
            }
            else if (MapController.map.equals("Cairo")){
                grass = (n/3)%2==1?Color.rgb(202, 134, 45):Color.rgb(238, 197, 100);
                rumble = (n/3)%2==1?Color.rgb(152, 110, 25):Color.rgb(212, 185, 113);
                road = (n/3)%2==1?Color.rgb(49, 64, 76):Color.rgb(34, 54, 56);
                road_lines = (n/3)%2==1?Color.rgb(161, 178, 186):Color.rgb(34, 54, 56);
            }

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
        
        // game paused
        if (paused){
            pause_ui.setVisible(true);
            return;
        }
        else {
            pause_ui.setVisible(false);
        }

        // Calc time passed
        long elapsed_millis = System.currentTimeMillis();
        seconds_elapsed = (elapsed_millis - start_time) / 1000;
        time_label.setText("Time: " + (total_time - seconds_elapsed) + " s");

        int seg_index = (int)(pos/SEG_LENGTH);
        boolean uphill = is_uphill(seg_index);
        boolean downhill = is_downhill(seg_index);
        boolean offroad = player_x <= -ROAD_WIDTH || player_x + player.get_width() >= ROAD_WIDTH;
        System.out.println(lines.get(seg_index % N).sprite);


        if (active_keys.contains(KeyCode.W)) {

            // play a engine sound once in a short while
            if (Math.random() < 0.1){
                play_sound("engine.mp3", SettingsController.volume * 0.8);
            }

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
            if (c_line.curve != 0 && !offroad){
                player_x -= c_line.curve*vel*CENTRIFRUGAL_FORCE_M;
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


        // System.out.println("vel: " + vel + " d_vel: " + d_vel);

        if (active_keys.contains(KeyCode.A)) {
            player_x -= vel * 0.2;
            handle_turning_dur(uphill, downhill, "l", seg_index);
        }
        if (active_keys.contains(KeyCode.D)) {
            player_x += vel * 0.2;
            handle_turning_dur(uphill, downhill, "", seg_index);
        }

        if (active_keys.contains(KeyCode.R)){
            player_x = 0;
            vel = 0;
            d_vel = 0;
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
            play_sound("drift.mp3", SettingsController.volume * 0.9);

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

    private void play_sound(String path, double volume){
        String p = getClass().getResource("/sounds/" + path).toExternalForm();
        Media sound = new Media(p);
        mp = new MediaPlayer(sound);
        mp.setVolume(volume);
        mp.play();
    }

    public void resume(){
        paused = false;
    }

    public void menu(ActionEvent e) throws Exception{
        theme_player.stop();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/menu.fxml"));
        Parent new_root = loader.load();

        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene cur_scene = stage.getScene();

        cur_scene.setRoot(new_root);
    }
}
