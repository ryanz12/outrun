package com.example.controllers;

import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

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
        }};

        max_song = songs.size();

        media = new Media(songs.get(song_index)[2]);
        media_player = new MediaPlayer(media);
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

        media_player.pause();

        media = new Media(songs.get(index)[2]);
        media_player = new MediaPlayer(media);

        if (playing){
            media_player.play();
        }

        // Update title and author labels
        title_label.setText(songs.get(song_index)[0]);
        author_label.setText(songs.get(song_index)[1]);
        cover.setImage(new Image(songs.get(song_index)[3]));
    }

    public void previous(){
        if (song_index - 1 == 0){
            song_index = max_song;
        }
        else{
            song_index--;
        }

        new_song(song_index);
    }

    public void next(){
        if (song_index + 1 > max_song){
            song_index = 1;
        }
        else {
            song_index++;
        }

        new_song(song_index);
    }
}