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
    public static double volume = 1;

    @FXML
    public Slider volume_bar;

    @FXML
    public void initialize(){
        if (Constants.volume != 1){
            volume_bar.setValue(Constants.volume);
        }
        volume_bar.valueProperty().addListener((obserable, old_value, new_value) -> {
            volume = new_value.doubleValue();
        });
    }

    public void back(ActionEvent e) throws Exception{
        Constants.volume = volume;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/menu.fxml"));
        Parent new_root = loader.load();

        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene cur_scene = stage.getScene();

        cur_scene.setRoot(new_root);
    }
}