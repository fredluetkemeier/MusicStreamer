/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flpn6musicstreamer;

import java.net.URL;

/**
 *
 * @author Fred
 */
public abstract class HttpAbstract {
    public abstract void openConnection(URL url);
    public abstract String transmit();
    public abstract String transmit(String outputString);
}
