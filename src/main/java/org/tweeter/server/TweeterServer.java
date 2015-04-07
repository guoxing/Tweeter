package org.tweeter.server;

import java.io.IOException;

import org.general.http.HTTPServer;
import org.tweeter.config.HTTPLayer;

/**
 * Tweeter server class
 *
 * @author Guoxing Li
 *
 */
public class TweeterServer {

    private static HTTPServer server;

    public static void main(String[] args) throws IOException {
        server = new HTTPServer("Tweeter/1.0", new HTTPLayer());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
            server.shutdown();
        }
    }

}
