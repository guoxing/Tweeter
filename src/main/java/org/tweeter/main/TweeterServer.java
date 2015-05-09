package org.tweeter.main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.general.data.DataStorage;
import org.general.http.HTTPRequest;
import org.general.http.HTTPResponse;
import org.general.http.HTTPResponse.HeaderField;
import org.general.http.HTTPServer;
import org.tweeter.api.Router;

public class TweeterServer extends HTTPServer {

    private static final String HELP_OPTION = "-help";
    private static final String PORT_OPTION = "-port";
    private static final String WORKSPACE_OPTION = "-workspace";
    private static final String DEFAULT_SERVER_NAME = "Tweeter/1.0";
    private static int DEFAULT_PORT = 8080;
    private static final String DEFAULT_RESPONSE_VERSION = "HTTP/1.1";
    private static final String DEFAULT_RESPONSE_CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String RESPONSE_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

    private TweeterServer(String name) {
        super(DEFAULT_PORT, name);
    }

    private TweeterServer(int port, String name) {
        super(port, name);
    }

    private static HTTPServer server;

    /**
     * Parses argument options and then starts up server.
     * 
     * @param args
     *            can include a port option, help option, and workspace option
     * @throws IOException
     *             Thrown if there is a problem
     */
    public static void main(String[] args) throws IOException {

        Map<String, String> argOptions = parseArgumentOptions(args);
        if (argOptions.containsKey(HELP_OPTION)) {
            System.out.println("-port port_number\n-workspace path");
            return;
        }
        if (argOptions.containsKey(WORKSPACE_OPTION)) {
            DataStorage.setPathToWorkspace(argOptions.get(WORKSPACE_OPTION));
        }
        if (argOptions.containsKey(PORT_OPTION)) {
            server = new TweeterServer(Integer.parseInt(argOptions
                    .get(PORT_OPTION)), DEFAULT_SERVER_NAME);
        } else {
            server = new TweeterServer("Tweeter/1.0");
        }

        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
            server.shutdown();
        }
    }

    private static Map<String, String> parseArgumentOptions(String[] args) {
        Map<String, String> argOptions = new HashMap<String, String>();
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals(PORT_OPTION) && i + 1 < args.length) {
                argOptions.put(PORT_OPTION, args[i + 1]);
            }
            if (args[i].equals(WORKSPACE_OPTION) && i + 1 < args.length) {
                // End workspace path with a slash
                argOptions.put(WORKSPACE_OPTION, args[i + 1] + "/");
            }
            if (args[i].equals(HELP_OPTION)) {
                argOptions.put(HELP_OPTION, "");
            }
        }
        return argOptions;
    }

    protected void handle(HTTPRequest req, HTTPResponse res) {
        setDefaultsOnResponse(res);
        Router.route(req, res);
    }
    
    private void setDefaultsOnResponse(HTTPResponse res) {
        res.setVersion(DEFAULT_RESPONSE_VERSION);
        res.setHeader(HeaderField.CONTENT_TYPE, DEFAULT_RESPONSE_CONTENT_TYPE);
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
                RESPONSE_DATE_FORMAT);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        res.setHeader(HeaderField.DATE, dateFormatGmt.format(new Date()));
    }

}
