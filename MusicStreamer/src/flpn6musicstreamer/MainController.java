/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flpn6musicstreamer;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

/**
 *
 * @author Fred
 */
public class MainController implements Initializable {
    private Stage stage;
    public MainModel mainSession;
    private HBox altHBox;
    private int lastPlaylistIndex;
    private int lastPlayedIndex;
    
    @FXML
    ImageView lastButton;
    @FXML
    ImageView playButton;
    @FXML
    ImageView nextButton;
    @FXML
    ListView allSongsListView;
    @FXML
    ListView playlistListView;
    @FXML
    ListView tracklistListView;
    @FXML
    HBox playlistControlHBox;
    @FXML
    VBox middleVBox;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    
    public void start(Stage stage, String username){
        mainSession = new MainModel(username);
        altHBox = new HBox();
        lastPlaylistIndex = 0;
        lastPlayedIndex = 0;
        playlistListView.getSelectionModel().select(lastPlaylistIndex);
        
        this.stage = stage;
        setupUI();
        stage.show();
    }
    
    public void setupUI(){
        lastButton.setImage(mainSession.getIcon("lastIcon"));
        playButton.setImage(mainSession.getIcon("playIcon"));
        nextButton.setImage(mainSession.getIcon("nextIcon"));
        
        mainSession.retrieveSongs();
        ObservableList<String> allSongs = FXCollections.observableArrayList(mainSession.getMasterSongs());
        allSongsListView.setItems(allSongs);
        
        refreshPlaylists();
    }
    
    @FXML
    public void lastButtonClick(MouseEvent event){
        playLastSong();
    }
    @FXML
    public void playButtonClick(MouseEvent event){
        playPauseSong();
    }
    @FXML
    public void nextButtonClick(MouseEvent event){
        playNextSong();
    }
    
    @FXML
    public void addToPlaylistButtonClick(ActionEvent event){
        addSongToPlaylist();
    }
    @FXML
    public void removeFromPlaylistButtonClick(ActionEvent event){
        removeSongFromPlaylist();
    }
    
    @FXML
    public void newPlaylistButtonClick(ActionEvent event){
        setupNamePrompt();
    }
    @FXML
    public void removePlaylistButtonClick(ActionEvent event){
        removePlaylist();
    }
    
    @FXML
    public void playlistSelected(MouseEvent event){
        lastPlaylistIndex = playlistListView.getSelectionModel().getSelectedIndex();
        refreshSongs();
    }
    @FXML
    public void trackSelected(MouseEvent event){
        if(tracklistListView.getSelectionModel().getSelectedIndex() != lastPlayedIndex && mainSession.getStatus() != Status.NEW){
            mainSession.stop();
            playButton.setImage(mainSession.getIcon("playIcon"));
        }
    }
    
    @FXML
    public void aboutButtonClick(ActionEvent event){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Kings of Leon Streamer");
        alert.setContentText("Author: Fred Luetkemeier\n\nTo get started, click the \"+\" to create a new playlist.\n\nFrom there, start filling it with songs.\n\nEnjoy!");
        alert.showAndWait();
    }
    
    public void setupNamePrompt(){
        middleVBox.getChildren().remove(playlistControlHBox);
        Label nameLabel = new Label("Name:");
        nameLabel.setMaxWidth(50);
        nameLabel.setTextFill(Paint.valueOf("#ffffff"));
        TextField nameField = new TextField();
        nameField.setMaxWidth(200);
        ImageView confirmImageView = new ImageView(mainSession.getIcon("checkmarkIcon"));
        confirmImageView.setFitHeight(15);
        confirmImageView.setFitWidth(15);
        ImageView cancelImageView = new ImageView(mainSession.getIcon("cancelIcon"));
        cancelImageView.setFitHeight(15);
        cancelImageView.setFitWidth(15);
        
        VBox confirmWrapper = new VBox();
        confirmWrapper.setAlignment(Pos.CENTER);
        confirmWrapper.getChildren().add(confirmImageView);
        confirmWrapper.setPrefHeight(20);
        confirmWrapper.setPrefWidth(20);
        
        VBox cancelWrapper = new VBox();
        cancelWrapper.setAlignment(Pos.CENTER);
        cancelWrapper.getChildren().add(cancelImageView);
        cancelWrapper.setPrefHeight(20);
        cancelWrapper.setPrefWidth(20);
        
        confirmWrapper.setOnMouseClicked((MouseEvent event) -> {
            createPlaylist(nameField.getText());
            middleVBox.getChildren().remove(altHBox);
            middleVBox.getChildren().add(playlistControlHBox);
        });
        
        cancelWrapper.setOnMouseClicked((MouseEvent event) -> {
            middleVBox.getChildren().remove(altHBox);
            middleVBox.getChildren().add(playlistControlHBox);
        });
        
        altHBox = new HBox();
        altHBox.setPrefHeight(100);
        altHBox.setSpacing(10);
        altHBox.setAlignment(Pos.CENTER_LEFT);
        
        altHBox.getChildren().add(0, cancelWrapper);
        altHBox.getChildren().add(0, confirmWrapper);
        altHBox.getChildren().add(0, nameField);
        altHBox.getChildren().add(0, nameLabel);
        
        middleVBox.getChildren().add(altHBox);
    }
    
    public void createPlaylist(String playlistName){
        mainSession.createPlaylist(playlistName);
        refreshPlaylists();
    }
    
    public void removePlaylist(){
        int index = playlistListView.getSelectionModel().getSelectedIndex();
        if(index != -1){
            String playlistName = mainSession.getPlaylists().get(index).getName();
            mainSession.deletePlaylist(playlistName);
            refreshPlaylists();
        }
    }
    
    public void addSongToPlaylist(){
        int allSongIndex = allSongsListView.getSelectionModel().getSelectedIndex();
        if(allSongIndex != -1){
            String playlistName = mainSession.getPlaylists().get(lastPlaylistIndex).getName();
            String songName = mainSession.getMasterSongs().get(allSongIndex);
            
            mainSession.addSongToPlaylist(songName, playlistName);
        }
        
        refreshPlaylists();
    }
    
    public void removeSongFromPlaylist(){
        int songIndex = tracklistListView.getSelectionModel().getSelectedIndex();
        if(songIndex != -1){
            String playlistName = mainSession.getPlaylists().get(lastPlaylistIndex).getName();
            String songName = mainSession.getPlaylists().get(lastPlaylistIndex).getSongs().get(songIndex);
            
            mainSession.removeSongFromPlaylist(songName, playlistName);
        }
        
        refreshPlaylists();
    }
    
    public void refreshPlaylists(){
        mainSession.retrievePlaylists();
        
        ArrayList<String> formattedNames = new ArrayList<>();
        for(String name : mainSession.getPlaylistNames()){
            String formattedName = name.replaceAll(mainSession.getUsername(), "");
            formattedNames.add(formattedName);
        }
        playlistListView.getItems().setAll(formattedNames);
        
        refreshSongs();
    }
    
    public void refreshSongs(){
        int selectedIndex = lastPlaylistIndex;
        if(selectedIndex == -1){
            selectedIndex = 0;
            playlistListView.getSelectionModel().select(selectedIndex);
        }
        
        ArrayList<String> formattedNames = new ArrayList<>();
        if(mainSession.getPlaylists().get(selectedIndex).getSongs() != null){
            for(String songName : mainSession.getPlaylists().get(selectedIndex).getSongs()){
                String formattedName = songName.replace("_", " ");
                formattedNames.add(formattedName);
            }
        }
        tracklistListView.getItems().setAll(formattedNames);
    }
    
    public void playPauseSong(){
        if(mainSession.getStatus().equals(Status.NEW)){
            int selectedIndex = tracklistListView.getSelectionModel().getSelectedIndex();
            if(selectedIndex == -1){
                selectedIndex = 0;
                tracklistListView.getSelectionModel().select(selectedIndex);
            }
            MediaPlayer mediaPlayer = mainSession.play(mainSession.getPlaylists().get(lastPlaylistIndex).getSongs().get(selectedIndex));
            mediaPlayer.setOnEndOfMedia(() -> {
                playNextSong();
            });
            playButton.setImage(mainSession.getIcon("pauseIcon"));
            lastPlayedIndex = selectedIndex;
        } else if(mainSession.getStatus().equals(Status.PAUSE)){
            mainSession.resume();
            playButton.setImage(mainSession.getIcon("pauseIcon"));
        } else if(mainSession.getStatus().equals(Status.PLAY)){
            mainSession.pause();
            playButton.setImage(mainSession.getIcon("playIcon"));
        }
    }
    
    public void playNextSong(){
        mainSession.stop();
        int nextIndex = mainSession.getPlaylists().get(lastPlaylistIndex).getNextSong(lastPlayedIndex);
        MediaPlayer mediaPlayer = mainSession.play(mainSession.getPlaylists().get(lastPlaylistIndex).getSongs().get(nextIndex));
        mediaPlayer.setOnEndOfMedia(() -> {
            playNextSong();
        });
        playButton.setImage(mainSession.getIcon("pauseIcon"));
        lastPlayedIndex = nextIndex;
        tracklistListView.getSelectionModel().select(nextIndex);
    }
    
    public void playLastSong(){
        mainSession.stop();
        int lastIndex = mainSession.getPlaylists().get(lastPlaylistIndex).getLastSong(lastPlayedIndex);
        MediaPlayer mediaPlayer = mainSession.play(mainSession.getPlaylists().get(lastPlaylistIndex).getSongs().get(lastIndex));
        mediaPlayer.setOnEndOfMedia(() -> {
            playNextSong();
        });
        playButton.setImage(mainSession.getIcon("pauseIcon"));
        lastPlayedIndex = lastIndex;
        tracklistListView.getSelectionModel().select(lastIndex);
    }
}
