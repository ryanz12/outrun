package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author: Ryan Zhou
 * @date: Fri Jan  3 19:41:02 2025
 * @description:
 * @resources:
 * https://www.youtube.com/watch?v=N60lBZDEwJ8
 * http://www.extentofthejam.com/pseudo/#intro
 * https://www.youtube.com/watch?v=FVo1fm52hz0
 * https://github.com/buntine/SwervinMervin/tree/master/lib
 * https://github.com/baraltech/Menu-System-PyGame
 * https://stackoverflow.com/questions/33336542/javafx-custom-fonts
 * https://stackoverflow.com/questions/43557722/javafx-border-radius-background-color
 * https://stackoverflow.com/questions/22512319/what-is-aabb-collision-detection
 * 
 * @note - run the program by entering "mvn javafx:run" in the terminal
 */

/* TODO
 * - collision detecion - no
 * - audio - yes
 * - ai -no
 * - more maps -yes
 * - menu - yes
 * - game ui -yes
 * - multiple roads (branching)?? - no
 * - updaet trailer - yes
 * - credits page? -no
 * */

public class App extends Application {

    @Override
    public void start(Stage main_stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/menu.fxml"));

        Scene menu_scene = new Scene(root);
        main_stage.setScene(menu_scene);
        main_stage.setTitle("Forza Horizon 7");
        main_stage.setResizable(false);
        main_stage.show();
    }
}
