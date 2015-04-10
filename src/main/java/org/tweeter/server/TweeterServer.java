package org.tweeter.server;

import java.io.IOException;

import org.general.data.InvalidDataFormattingException;
import org.general.http.HTTPServer;
import org.tweeter.config.HTTPLayer;
import org.tweeter.data.FriendshipData;
import org.tweeter.data.StatusData;

/**
 * Tweeter server class
 *
 * @author Guoxing Li
 *
 */
public class TweeterServer {

    private static HTTPServer server;

    public static void main(String[] args) throws IOException,
            InvalidDataFormattingException {
        server = new HTTPServer("Tweeter/1.0", new HTTPLayer());
        // spin up data modules
        FriendshipData.getInstance();
        StatusData.getInstance();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
            server.shutdown();
        }
    }

}
