/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flpn6musicstreamer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author Fred
 */
public class MainModel {
    private Image lastIcon, playIcon, pauseIcon, nextIcon;
    private Image cancelIcon, checkmarkIcon;
    private ArrayList<Playlist> playlists;
    private Media currentMedia;
    private MediaPlayer mediaPlayer;
    private ArrayList<String> masterSongs;
    private Status currentStatus;
    private String username;
    
    public MainModel(String username) {
        this.username = username;
        masterSongs = new ArrayList<>();
        playlists = new ArrayList<>();
        
        lastIcon = new Image("https://kingsofleon.club/resources/icons/lastIcon.png");
        playIcon = new Image("https://kingsofleon.club/resources/icons/playIcon.png");
        pauseIcon = new Image("https://kingsofleon.club/resources/icons/pauseIcon.png");
        nextIcon = new Image("https://kingsofleon.club/resources/icons/nextIcon.png");
        
        cancelIcon = new Image("https://kingsofleon.club/resources/icons/cancelIcon.png");
        checkmarkIcon = new Image("https://kingsofleon.club/resources/icons/checkmarkIcon.png");
        
        currentStatus = Status.NEW;
        currentMedia = null;
    }
    
    public MediaPlayer play(String songName){
        String formattedName = songName;
        
        formattedName = formattedName.replaceAll("_", "");
        formattedName = formattedName.substring(0, 1).toLowerCase() + formattedName.substring(1) + ".mp3";
        
        System.out.println(formattedName);
        
        currentMedia = new Media("https://kingsofleon.club/resources/songs/" + formattedName);
        mediaPlayer = new MediaPlayer(currentMedia);
        mediaPlayer.play();
        mediaPlayer.setVolume(0.5);
        currentStatus = Status.PLAY;
        return mediaPlayer;
    }
    
    public void pause(){
        mediaPlayer.pause();
        currentStatus = Status.PAUSE;
    }
    
    public void resume(){
        mediaPlayer.play();
        currentStatus = Status.PLAY;
    }
    
    public void stop(){
        mediaPlayer.stop();
        currentStatus = Status.NEW;
    }
    
    public void createPlaylist(String playlistName){
        HttpSimple connection = null;
        try {
            connection = new HttpSimple(new URL("https://kingsofleon.club/createPlaylist.php"));
        } catch (MalformedURLException ex) {
            System.out.println(ex);
        }
        if(connection != null){
            String parsedPlaylistName = playlistName.replaceAll(" ", "_");
            String outputString = "username=" + username + "&playlistName=" + parsedPlaylistName;
            connection.transmit(outputString);
        }
    }
    
    public void retrievePlaylists(){
        playlists.clear();
        
        HttpSimple connection = null;
        try {
            connection = new HttpSimple(new URL("https://kingsofleon.club/getPlaylists.php"));
        } catch (MalformedURLException ex) {
            System.out.println(ex);
        }
        if(connection != null){
            String outputString = "username=" + username;
            String response = connection.transmit(outputString);
            
            if(!response.isEmpty()){
                response = response.substring(0, (response.length()-1));
            }
            String playlistNames[] = response.split(",");
            
            for(String playlistName : playlistNames){
                connection = null;
                try {
                    connection = new HttpSimple(new URL("https://kingsofleon.club/getPlaylistContent.php"));
                } catch (MalformedURLException ex) {
                    System.out.println(ex);
                }
                if(connection != null){
                    outputString = "playlistName=" + playlistName;
                    response = connection.transmit(outputString);
                    
                    response = response.trim();
                    
                    ArrayList<String> songNameList = null;
                    if(!response.isEmpty()){
                        response = response.substring(0, (response.length()-1));
                        String songNames[] = response.split(",");
                        
                        songNameList = new ArrayList<>();
                        for(String songName : songNames){
                            songNameList.add(songName);
                        }

                        playlists.add(new Playlist(playlistName, songNameList));
                    } else {
                        playlists.add(new Playlist(playlistName, songNameList));
                    }
                }
            }
        }
    }
    
    public Image getIcon(String name){
        switch (name){
            case "lastIcon":
                return lastIcon;
            case "playIcon":
                return playIcon;
            case "pauseIcon":
                return pauseIcon;
            case "nextIcon":
                return nextIcon;
            case "cancelIcon":
                return cancelIcon;
            case "checkmarkIcon":
                return checkmarkIcon;
            default:
                return null;
        }
    }
    
    public void retrieveSongs(){
        HttpSimple connection = null;
        try {
            connection = new HttpSimple(new URL("https://kingsofleon.club/getSongs.php"));
        } catch (MalformedURLException ex) {
            System.out.println(ex);
        }
        if(connection != null){
            String response = connection.transmit();
            String[] responses = response.split("\\n");
            
            masterSongs.clear();
            
            for(String song : responses){
                char temp[] = song.toCharArray();
                ArrayList<String> tempList = new ArrayList<>();
                for(char character : temp){
                    tempList.add(String.valueOf(character));
                }
                for(int i = temp.length-1; i >= 0; i--){
                    if(Character.isUpperCase(temp[i])){
                        tempList.add(i, " ");
                    }
                }
                tempList.add(0, tempList.get(0).toUpperCase());
                tempList.remove(1);
                song = "";
                for(String character : tempList){
                    song += character;
                }
                song = song.substring(0, song.length()-4);
                masterSongs.add(song);
            }
        }
    }
    
    public void addSongToPlaylist(String songName, String playlistName){
        HttpSimple connection = null;
        try {
            connection = new HttpSimple(new URL("https://kingsofleon.club/addToPlaylist.php"));
        } catch (MalformedURLException ex) {
            System.out.println(ex);
        }
        if(connection != null){
            songName = songName.replaceAll(" ", "_");
            String outputString = "playlistName=" + playlistName + "&songName=" + songName;
            connection.transmit(outputString);
        }
    }
    
    public void removeSongFromPlaylist(String songName, String playlistName){
        HttpSimple connection = null;
        try {
            connection = new HttpSimple(new URL("https://kingsofleon.club/removeFromPlaylist.php"));
        } catch (MalformedURLException ex) {
            System.out.println(ex);
        }
        if(connection != null){
            songName = songName.replaceAll(" ", "_");
            String outputString = "playlistName=" + playlistName + "&songName=" + songName;
            connection.transmit(outputString);
        }
    }
    
    public void deletePlaylist(String playlistName){
        HttpSimple connection = null;
        try {
            connection = new HttpSimple(new URL("https://kingsofleon.club/deletePlaylist.php"));
        } catch (MalformedURLException ex) {
            System.out.println(ex);
        }
        if(connection != null){
            String outputString = "playlistName=" + playlistName;
            connection.transmit(outputString);
        }
    }
    
    public ArrayList<String> getMasterSongs(){
        return masterSongs;
    }
    
    public ArrayList<Playlist> getPlaylists(){
        return playlists;
    }
    
    public ArrayList<String> getPlaylistNames(){
        ArrayList<String> temp = new ArrayList<>();
        for(Playlist playlist : playlists){
            temp.add(playlist.getName().replaceAll("_", " "));
        }
        return temp;
    }
    
    public String getUsername(){
        return username;
    }
    
    public void setStatus(Status status){
        currentStatus = status;
    }
    public Status getStatus(){
        return currentStatus;
    }
    
    public MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }
}
