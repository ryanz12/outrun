package com.example.controllers;

import static com.example.controllers.Constants.first_time;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class MenuController {
    private String video_path = getClass().getResource("/videos/trailer.mp4").toExternalForm();
    private Media media;
    private MediaPlayer media_player;

    @FXML
    private AnchorPane root_pane;

    @FXML
    private Button play_button;

    @FXML
    private MediaView media_view;

    @FXML
    private ProgressBar loading_prog;

    @FXML
    public void initialize(){
        if (first_time){
            media = new Media(video_path);
            media_player = new MediaPlayer(media);
            media_view.setMediaPlayer(media_player);
            media_player.play();

            media_player.setOnEndOfMedia(() -> {
                media_player.pause();
                root_pane.getChildren().remove(media_view);
                loading_prog.setVisible(false);
            });

            root_pane.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE){
                    media_player.pause();
                    root_pane.getChildren().remove(media_view);
                    loading_prog.setVisible(false);
                }
            });

            // Progress bar sync with video duration
            media_player.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                double progress = newValue.toSeconds() / media.getDuration().toSeconds();
                loading_prog.setProgress(progress);
            });

        }
        else {
            root_pane.getChildren().remove(media_view);
            loading_prog.setVisible(false);
        }
        Constants.first_time = false;
    }

    public void play_game(ActionEvent e) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/game.fxml"));
        Parent newRoot = loader.load(); // Load the new FXML file

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow(); // Get the current stage
        Scene currentScene = stage.getScene(); // Get the current scene

        // Replace the root of the current scene with the new root
        currentScene.setRoot(newRoot);

        // Optionally, set up the controller for the new view
        GameController controller = loader.getController();
        controller.listen(currentScene);

        // Request focus for the new root
        newRoot.requestFocus();
    }

    public void map(ActionEvent e) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/map.fxml"));
        Parent new_root = loader.load();

        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene cur_scene = stage.getScene();

        cur_scene.setRoot(new_root);
    }

    public void music(ActionEvent e) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/music.fxml"));
        Parent new_root = loader.load();

        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene cur_scene = stage.getScene();

        cur_scene.setRoot(new_root);
    }
    
    public void settings(ActionEvent e) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/settings.fxml"));
        Parent new_root = loader.load();

        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene cur_scene = stage.getScene();

        cur_scene.setRoot(new_root);
    }

    public void quit(){
        System.exit(0);
    }
}
