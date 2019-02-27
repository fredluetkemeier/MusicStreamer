/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flpn6musicstreamer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Fred
 */
public class LoginModel {
    private HttpSimple connection;
    private URL loginURL;
    private String username, password;
    
    public LoginModel() {
        try {
            loginURL = new URL("https://kingsofleon.club/login.php");
        } catch (MalformedURLException ex) {
            System.out.println(ex);
        }
        
        username = password = "";
    }
    
    public boolean login(String username, String password){
        this.username = username;
        this.password = password;
        
        connection = new HttpSimple(loginURL);
        
        String outputString = "username=" + username + "&password=" + password;

        String response = connection.transmit(outputString);
        
        if(response.contains("0")){
            return false;
        } else {
            return true;
        }
    }
    
    public boolean writeCredentials(CredentialStore credentials){
        try {
            FileOutputStream file = new FileOutputStream("credentials.kol");
            ObjectOutputStream output = new ObjectOutputStream(file);
            
            output.writeObject(credentials);
            
            output.close();
            file.close();
            
            return true;
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
            return false;
        } catch (IOException ex) {
            System.out.println(ex);
            return false;
        }
    }
    
    public CredentialStore readCredentials(){
        CredentialStore credentials = null;
        
        try {
            FileInputStream file = new FileInputStream("credentials.kol");
            ObjectInputStream input = new ObjectInputStream(file);     
            
            credentials = (CredentialStore)input.readObject();
            
            input.close();
            file.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } finally {
            return credentials;
        }
    }
}
