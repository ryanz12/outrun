package com.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.example.Player.PLAYER_DIRECTIONS;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static com.example.Constants.*;

/**
 * @resources
 * https://www.youtube.com/watch?v=N60lBZDEwJ8
 * http://www.extentofthejam.com/pseudo/#intro
 * https://www.youtube.com/watch?v=FVo1fm52hz0
 */


// TODO:

/**
 * @core
 * <Implement Opponent AI>
 *   - Create bot class similar to player
 *   - basic ai features (e.g staying on track, accel, decel)
 *   - path following
 *   - difficulty
 *  <Collision Detection>
 *   - detection with ai/obstacles
 *   - penalize player (reduce speed, reset)
 *  <Track>
 *   - multiple tracks
 *   - track selection menu
 *   - checkpoints
 *  <Nitro>
 *   - make temp speed boosts
 *   - nitro meter
 *   - visuals
 *   - track element
 *  <Dynamic Obstacles>
 *   - coins/coin System
 *  <Steering>
 *   - centrifugal force
 *   - make car actually turn
 *   - slow down
 *   - uphill, downhill, left, right
 */

/**
 * @visuals
 * <Graphics>
 *   - background elements (e.g background, buildings, trees, etc)
 *   - road lines
 *   - particle effects
 * <Camera>
 *   - dynamic camera
 *   - shake effects
 * <Sound effects and Music>
 *   - sound effects for steering, nitro, revs, etc
 *   - background music (user can choose)
 *   - announcer sounds?
 */

/**
 * @ui
 * <Menu>
 *   - options: play, settings, manual, exit
 *   - css
 * <HUD>
 *   - speedometer
 *   - minimap
 *   - time elapsed
 *   - lap info
 * <Pause Menu>
 *   - allow pausing
 *   - options: resume, restart, settings, exit
 * <Post Race Menu>
 *   - leaderboard, score, rewards
 *   - exit
 */

/**
 * @gameplay
 * <Racing Modes>
 *   - tourneys
 * <Player Progression>
 *   - currency system
 *   - car upgrades
 *   - new cars
 */

/**
 * @extra
 * <Customization>
 *   - paint car, wheels
 * <Weather>
 *   - dynamic weather and time of day
 * <Achivements>
 *   - hall of fame, achivements
 */

public class App extends Application {
    // ---------- INITALIZE VARIAVLES ----------
    public ArrayList <Line> lines = new ArrayList<>();
    public int N;
    public int pos = 0;
    public int speed = 400;
    public int player_x = 0;

    public Set<KeyCode> active_keys = new HashSet<>();
    public Player player;

    public double bg_offset_x = 0;
    public Image background = new Image("backgrounds/night_sky.png");
    public Image tree = new Image("objects/tree.png");

    // ---------- 2D TO 3D PROJECTION (QUADRILATERAL) ----------
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

    // ---------- INITIALIZE GAME ----------
    @Override
    public void start(Stage main_stage) {

        // initalize javafx containers/wrappers/canvas
        Canvas canvas = new Canvas(WIDTH, HEIGHT);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        AnchorPane root = new AnchorPane();
        root.getChildren().add(canvas);

        Scene scene = new Scene(root, WIDTH, HEIGHT);

        scene.setOnKeyPressed(e -> active_keys.add(e.getCode()));
        scene.setOnKeyReleased(e -> active_keys.remove(e.getCode()));

        main_stage.setScene(scene);
        main_stage.setResizable(false);
        main_stage.setTitle("Forza Horizon 7");
        main_stage.show();

        player = new Player(gc);

        for (int i=0; i<1600; i++){
            Line line = new Line();
            line.z = i * SEG_LENGTH;

            // curve
            if (i > 300 && i < 700) line.curve = 1.5f;

            // hill
            if (i > 750 && i < 1000) line.y = (float)(Math.sin(i / 30.0) * 3000);

            if (i > 1000) line.curve = 2.5f;

            if (i % 20 == 0){
                line.sprite_x = -2.5f;
                line.sprite = tree;
            }

            lines.add(line);
        }

        N = lines.size();

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
        int start_pos = pos/SEG_LENGTH;
        float cam_height = 4000 + lines.get(start_pos).y;
        float x = 0, dx = 0;
        float max_y = HEIGHT;

        for (int n=start_pos; n<start_pos+500; n++){
            Line cur_line = lines.get(n % N);
            cur_line.project(player_x - x, cam_height, pos);

            x += dx;
            dx += cur_line.curve;

            if (cur_line.Y >= max_y) continue;
            max_y = cur_line.Y;

            Color grass = (n/3)%2==1?Color.rgb(16,200,16):Color.rgb(0,154,0);
            Color rumble = (n/3)%2==1?Color.rgb(255,255,255):Color.rgb(0,0,0);
            Color road = (n/3)%2==1?Color.rgb(107,107,107):Color.rgb(105,105,105);

            Line prev_line = lines.get((n-1+N)%N);

            draw_quad(gc, grass, 0, (int)prev_line.Y, WIDTH, 0, (int)cur_line.Y, WIDTH);
            draw_quad(gc, rumble, (int)prev_line.X, (int)prev_line.Y, (int)(prev_line.W*1.2), (int)cur_line.X, (int)cur_line.Y, (int)(cur_line.W*1.2));
            draw_quad(gc, road, (int)prev_line.X, (int)prev_line.Y, (int)prev_line.W, (int)cur_line.X, (int)cur_line.Y, (int)cur_line.W);
        }

        for (int n=start_pos + 500; n>start_pos; n--){
            lines.get(n%N).draw_sprite(gc);
        }
    }

    // ---------- RENDERING GAME COMPONENTS ----------
    public void render(GraphicsContext gc){
        // clear screen
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        // Draw background
        // gc.setFill(Color.LIGHTBLUE);
        // gc.fillRect(0, 0, WIDTH, HEIGHT);

        render_road(gc);

        player.render(WIDTH/2, HEIGHT-200);
    }

    // ---------- UPDATING GAME LOGIC ----------
    public void update(){
        int seg_index = pos / SEG_LENGTH;
        boolean uphill = is_uphill(seg_index);
        boolean downhill = is_downhill(seg_index);

        if (active_keys.contains(KeyCode.W)){
            pos += speed;

            if (uphill) player.set_direction(PLAYER_DIRECTIONS.UPHILL_STRAIGHT);
            else if (downhill) player.set_direction(PLAYER_DIRECTIONS.DOWNHILL_STRAIGHT);
            else player.set_direction(PLAYER_DIRECTIONS.STRAIGHT);
        }

        if (active_keys.contains(KeyCode.A)){
            player_x -= speed;

            if (uphill) player.set_direction(PLAYER_DIRECTIONS.UPHILL_LEFT);
            else if (downhill) player.set_direction(PLAYER_DIRECTIONS.DOWNHILL_LEFT);
            else player.set_direction(PLAYER_DIRECTIONS.LEFT);
        }
        if (active_keys.contains(KeyCode.D)){
            player_x += speed;

            if (uphill) player.set_direction(PLAYER_DIRECTIONS.UPHILL_RIGHT);
            else if (downhill) player.set_direction(PLAYER_DIRECTIONS.DOWNHILL_RIGHT);
            else player.set_direction(PLAYER_DIRECTIONS.RIGHT);
        }

        //debuggin
        if (active_keys.contains(KeyCode.S)){
            pos -= speed;
        }
    }

    private boolean is_uphill(int seg_index){
        return lines.get(seg_index).y > lines.get((seg_index - 1 + N) % N).y;
    }

    private boolean is_downhill(int seg_index){
        return lines.get(seg_index).y < lines.get((seg_index - 1 + N) % N).y;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
