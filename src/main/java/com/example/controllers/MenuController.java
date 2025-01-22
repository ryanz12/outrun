package com.example.controllers;

import static com.example.controllers.Constants.first_time;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    public Label trailer_label;

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
        // If it's the user's first time, play the trailer
        if (first_time){
            media = new Media(video_path);
            media_player = new MediaPlayer(media);
            media_view.setMediaPlayer(media_player);
            media_player.play();

            // Remove the FXML when done
            media_player.setOnEndOfMedia(() -> {
                media_player.pause();
                root_pane.getChildren().remove(media_view);
                loading_prog.setVisible(false);
                trailer_label.setVisible(false);
            });

            root_pane.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE){
                    media_player.pause();
                    root_pane.getChildren().remove(media_view);
                    loading_prog.setVisible(false);
                    trailer_label.setVisible(false);
                }
            });

            // Progress bar sync with video duration
            media_player.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                double progress = newValue.toSeconds() / media.getDuration().toSeconds();
                loading_prog.setProgress(progress);
            });
        }
        else {
            // Remove the trailer if it's not their first time
            root_pane.getChildren().remove(media_view);
            loading_prog.setVisible(false);
            trailer_label.setVisible(false);
        }
        Constants.first_time = false;
    }

    public void map(ActionEvent e) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/map.fxml"));
        Parent new_root = loader.load();

        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene cur_scene = stage.getScene();

        cur_scene.setRoot(new_root);
    }

    public void manual(ActionEvent e) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/manual.fxml"));
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
