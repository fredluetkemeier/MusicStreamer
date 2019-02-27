/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flpn6musicstreamer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 *
 * @author Fred
 */
public class LoginController implements Initializable {
    private Stage stage;
    public LoginModel loginSession;
    private Thread loginThread;
    
    @FXML
    CheckBox rememberMeCheckBox;
    @FXML
    Label loginFailedLabel;
    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    Button loginSubmitButton;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginThread = null;
    }
    
    public void start(Stage stage){
        this.stage = stage;
        stage.show();
        
        loginSession = new LoginModel();
        loginThread = new Thread(() -> {
            File temp = new File("credentials.kol");
            if(temp.exists()){
               CredentialStore credentials = loginSession.readCredentials();
               Platform.runLater(() ->{
                   loginSubmitButton.setText("Logging in...");
                   loginSubmitButton.setDisable(true);
                   rememberMeCheckBox.setDisable(true);
                   usernameField.setDisable(true);
                   passwordField.setDisable(true);
               });
               if(loginSession.login(credentials.getUsername(), credentials.getPassword())){
                   Platform.runLater(() ->{
                       switchToMain(credentials.getUsername());
                   });
               } else {
                    Platform.runLater(() ->{
                        loginSubmitButton.setText("Submit");
                        loginSubmitButton.setDisable(false);
                        rememberMeCheckBox.setDisable(false);
                        usernameField.setDisable(false);
                        passwordField.setDisable(false);
                        loginFailedLabel.setText("Unable to log in!");
                    });
               }
            }
        });
        loginThread.setDaemon(true);            
        loginThread.start();
    }
    
    @FXML
    public void enterKeyPressed(KeyEvent event){
        if(event.getCode().equals(KeyCode.ENTER)){
            loginSubmit();
        }
    }
    
    @FXML
    public void loginSubmitButtonClick(ActionEvent event){
        loginSubmit();
    }
    
    public void loginSubmit(){
        loginThread = new Thread(() ->{
            if(usernameField.getText().isEmpty()){
                Platform.runLater(() ->{
                    loginFailedLabel.setText("Please provide a username!");
                });
            } else if (passwordField.getText().isEmpty()){
                Platform.runLater(() ->{
                    loginFailedLabel.setText("Please provide a password!");
                });
            } else {
                Platform.runLater(() ->{
                   loginSubmitButton.setText("Logging in...");
                   loginSubmitButton.setDisable(true);
                   rememberMeCheckBox.setDisable(true);
                   usernameField.setDisable(true);
                   passwordField.setDisable(true);
                });
                if(loginSession.login(usernameField.getText(), passwordField.getText())){
                    if(rememberMeCheckBox.isSelected()){
                        CredentialStore credentials = new CredentialStore(usernameField.getText(), passwordField.getText());
                        loginSession.writeCredentials(credentials);
                    }
                    Platform.runLater(() ->{
                        switchToMain(usernameField.getText());
                    });
                } else {
                    Platform.runLater(() ->{
                        loginSubmitButton.setText("Submit");
                        loginSubmitButton.setDisable(false);
                        rememberMeCheckBox.setDisable(false);
                        usernameField.setDisable(false);
                        passwordField.setDisable(false);
                        loginFailedLabel.setText("Unable to log in!");
                    });
                }
            }
        });
        loginThread.setDaemon(true);
        loginThread.start();
    }
    
    public void switchToMain(String username){
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("flpn6musicstreamer/fxml/MainView.fxml"));
        
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        
        if(root != null){
            MainController main = loader.getController();
        
            Scene scene = new Scene(root);
        
            stage.setScene(scene);
        
            main.start(stage, username);
        }
    }
}
