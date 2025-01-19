package com.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MapController {
    public static String map = null;

    @FXML
    public Label map_label;

    @FXML
    public Button play_button;

    @FXML
    public void initialize(){
        play_button.setDisable(true);
    }

    public void change_map(MouseEvent e){
        ImageView m = (ImageView)e.getSource();
        map = m.getId();
        map_label.setText("Map Selected: " + map);
        play_button.setDisable(false);
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

    public void back(ActionEvent e) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/menu.fxml"));
        Parent new_root = loader.load();

        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene cur_scene = stage.getScene();

        cur_scene.setRoot(new_root);
    }

}
