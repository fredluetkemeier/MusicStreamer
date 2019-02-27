/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flpn6musicstreamer;

/**
 *
 * @author Fred
 */
public interface PlaylistInterface {
    public int getLastSong(int currentIndex);
    public int getNextSong(int currentIndex);
}
