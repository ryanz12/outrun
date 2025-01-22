package com.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

public class SettingsController {
    public static double volume = 1, fov = 0.94;

    @FXML
    public Slider volume_bar, fov_bar;

    @FXML
    public void initialize(){
        // If volume is already changed, set the volume
        if (Constants.volume != 1){
            volume_bar.setValue(Constants.volume);
        }

        if (Constants.CAM_DEPTH != 0.94){
            fov_bar.setValue(Constants.CAM_DEPTH);
        }

        // When volume is changed, change the volume
        volume_bar.valueProperty().addListener((obserable, old_value, new_value) -> {
            volume = new_value.doubleValue();
        });

        fov_bar.valueProperty().addListener((obserable, old_value, new_value) -> {
            fov = new_value.doubleValue();
        });
    }

    public void back(ActionEvent e) throws Exception{
        // Save volume
        Constants.volume = volume;
        Constants.CAM_DEPTH = (float)fov;

        // Back to menu
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/menu.fxml"));
        Parent new_root = loader.load();

        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene cur_scene = stage.getScene();

        cur_scene.setRoot(new_root);
    }
}