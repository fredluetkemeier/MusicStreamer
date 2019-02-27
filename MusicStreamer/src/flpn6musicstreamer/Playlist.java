/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flpn6musicstreamer;

import java.util.ArrayList;

/**
 *
 * @author Fred
 */
public class Playlist implements PlaylistInterface {
    private ArrayList<String> songs;
    private String name;
    
    public Playlist(String name, ArrayList<String> songs){
        this.songs = new ArrayList<>();
        this.name = name;
        this.songs = songs;
    }
    
    public int getLastSong(int currentIndex){
        if(currentIndex != -1){
            if(currentIndex != 0){
                return currentIndex - 1;
            } else {
                return songs.size()-1;
            }
        } else {
            return 0;
        }
    }
    
    public int getNextSong(int currentIndex){
        if(currentIndex != -1){
            if(currentIndex != songs.size() - 1){
                return currentIndex + 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
    
    public String getName(){
        return name;
    }
    
    public ArrayList<String> getSongs(){
        return songs;
    }
}
