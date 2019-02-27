/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flpn6musicstreamer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Fred
 */
public class CredentialStore implements Serializable {
    private ArrayList<String> credentials;
    
    public CredentialStore(String username, String password){
        credentials = new ArrayList<>();
        credentials.add(username);
        credentials.add(password);
    }
    
    public ArrayList<String> getCredentials(){
        return credentials;
    }
    
    public String getUsername(){
        return credentials.get(0);
    }
    public void setUsername(String username){
        credentials.set(0, username);
    }
    
    public String getPassword(){
        return credentials.get(1);
    }
    public void setPassword(String password){
        credentials.set(1, password);
    }
}
