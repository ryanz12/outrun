package com.example.controllers;

import java.util.HashMap;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class MusicController {

    private Media media;
    private MediaPlayer media_player;
    private boolean playing = false;

    private HashMap<Integer, String[]> songs;
    private int song_index = 1;
    private int max_song;

    @FXML
    public Button play_button;

    @FXML
    public Label author_label, title_label;

    @FXML
    public ImageView cover;

    @FXML
    public void initialize(){

        // Add songs to the HashMap to access song info quickly
        songs = new HashMap<>() {{
            put(1, new String[]{
                "Tokyo Drift",
                "Teriyaki Boyz",
                getClass().getResource("/music/tokyo_drift.mp3").toExternalForm(),
                getClass().getResource("/music/covers/tokyo_drift.png").toExternalForm(),
            });
            put(2, new String[]{
                "My Eyes",
                "Travis Scott",
                getClass().getResource("/music/my_eyes.mp3").toExternalForm(),
                getClass().getResource("/music/covers/utopia.png").toExternalForm()
            });
            put(3, new String[]{
                "See You Again",
                "Wiz Khalifa",
                getClass().getResource("/music/see_you_again.mp3").toExternalForm(),
                getClass().getResource("/music/covers/sya.png").toExternalForm()
            });
            put(4, new String[]{
                "Red sun in the sky",
                "Chairman Mao",
                getClass().getResource("/music/red_sun.mp3").toExternalForm(),
                getClass().getResource("/music/covers/red_sun.png").toExternalForm()
            });
        }};

        max_song = songs.size();

        // Initalize the media player 
        media = new Media(songs.get(song_index)[2]);
        media_player = new MediaPlayer(media);
        media_player.setVolume(SettingsController.volume);

        media_player.setOnEndOfMedia(() -> {
            song_index = (song_index + 1) % max_song;
            new_song(song_index);
        });
    }

    public void plause(){
        if (!playing){
            media_player.play();
            play_button.setText("||");
        }
        else {
            media_player.pause();
            play_button.setText("▶");
        }
        playing = !playing;
    }

    public void new_song(int index){
        // Pause the current song then play a new one
        media_player.pause();

        media = new Media(songs.get(index)[2]);
        media_player = new MediaPlayer(media);
        // System.out.println(SettingsController.volume);

        media_player.setVolume(SettingsController.volume);

        if (playing){
            media_player.play();
        }

        // Update title and author labels
        title_label.setText(songs.get(song_index)[0]);
        author_label.setText(songs.get(song_index)[1]);
        cover.setImage(new Image(songs.get(song_index)[3]));

        media_player.setOnEndOfMedia(() -> {
            song_index = (song_index + 1) % max_song;
            new_song(song_index);
        });
    }

    public void previous(){
        // Wrap around 
        if (song_index - 1 == 0){
            song_index = max_song;
        }
        else{
            song_index--;
        }

        new_song(song_index);
    }

    public void next(){
        // Wrap around
        if (song_index + 1 > max_song){
            song_index = 1;
        }
        else {
            song_index++;
        }

        new_song(song_index);
    }


    public void back(ActionEvent e) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/menu.fxml"));
        Parent new_root = loader.load();

        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene cur_scene = stage.getScene();

        cur_scene.setRoot(new_root);
    }
}
