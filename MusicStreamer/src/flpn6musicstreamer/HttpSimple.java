/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flpn6musicstreamer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author fredluetkemeier
 */
public class HttpSimple extends HttpAbstract{
    private URL url;
    private URLConnection connection;
    
    public HttpSimple(URL url){
        this.url = url;
        
        openConnection(url);
    }
    
    public void openConnection(URL url){
        try {
            connection = url.openConnection();
        } catch (IOException ex) {
            System.out.println("ERROR: Could not open connection to specified URL.");
        }
    }
    
    public String transmit(){
        return transmit("");
    }
    
    public String transmit(String outputString){
        HttpsURLConnection https = (HttpsURLConnection) connection;
        
        try {
            https.setRequestMethod("POST");
        } catch (ProtocolException ex) {
            System.out.println(ex);
            return "0";
        }
        
        if(!outputString.isEmpty()){
            https.setDoOutput(true);
        
            byte[] out = outputString.getBytes(StandardCharsets.UTF_8);
            int length = out.length;
        
            https.setFixedLengthStreamingMode(length);
            https.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            
            try {
                https.connect();
            } catch (IOException ex) {
                System.out.println(ex);
                return "0";
            }
            
            try (OutputStream outputStream = https.getOutputStream()) {
                outputStream.write(out);
                outputStream.flush();
                outputStream.close();
            } catch (IOException ex) {
                System.out.println(ex);
                return "0";
            }
        } else {
            https.setDoInput(true);
            
            try {
                https.connect();
            } catch (IOException ex) {
                System.out.println(ex);
                return "0";
            }
        }
        
        ArrayList<byte[]> inputList = new ArrayList<>();
        
        byte input[] = new byte[1000];
        try (InputStream inputStream = https.getInputStream()) {
            int byteCount;
            do {
                byteCount = inputStream.read(input);
                inputList.add(input);
                input = new byte[1000];
            } while(byteCount == 1000 || byteCount != -1);
        } catch (IOException ex) {
            System.out.println(ex);
            return "0";
        }
        
        String httpsResponse = "";
        
        for(int i = 0; i < inputList.size()-1; i++){
            for(byte readByte : inputList.get(i)){
                httpsResponse += (char) readByte;
            }
        }
        
        https.disconnect();
        
        httpsResponse = httpsResponse.trim();
        
        return httpsResponse;
    } 
}
