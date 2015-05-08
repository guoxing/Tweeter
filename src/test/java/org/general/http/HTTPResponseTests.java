package org.general.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.general.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for HTTPResponse.
 *
 * @author Guoxing Li
 *
 */
public class HTTPResponseTests {

    private static final String TEST_HOST = "localhost";
    private static final int TEST_PORT = 8889;

    private ServerSocket ss;

    @Before
    public void setup() {
        try {
            ss = new ServerSocket(TEST_PORT);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception found.");
        }
    }

    @After
    public void teardown() {
        try {
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception found.");
        }
    }

    private JSONObject genJSONList() {
        List<Number> list = new ArrayList<>();
        list.add(100);
        list.add(200);
        list.add(300);
        return JSONObject.fromNumbers(list);
    }

    @Test
    public void testSendSuccess() {
        try {
            Socket clientSocket = new Socket(TEST_HOST, TEST_PORT);
            Socket receivedSocket = ss.accept();
            HTTPResponse res = new HTTPResponse(
                    receivedSocket.getOutputStream(), "TestServer");
            String defaultVersion = "HTTP/1.1";
            res.setVersion(defaultVersion);
            JSONObject list = genJSONList();
            res.send(HTTPResponse.StatusCode.OK, list.toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));

            String line = reader.readLine();
            assertEquals(line, defaultVersion + " "
                    + HTTPResponse.StatusCode.OK.getNum() + " "
                    + HTTPResponse.StatusCode.OK.getMessage());
            while (!line.isEmpty()) {
                line = reader.readLine();
            }
            line = reader.readLine();
            assertEquals(line, list.toString());
            receivedSocket.close();
            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception found.");
        }
    }

    @Test
    public void testSendError() {
        try {
            Socket clientSocket = new Socket(TEST_HOST, TEST_PORT);
            Socket receivedSocket = ss.accept();
            HTTPResponse res = new HTTPResponse(
                    receivedSocket.getOutputStream(), "TestServer");
            String defaultVersion = "HTTP/1.1";
            res.setVersion(defaultVersion);
            Map<String, JSONObject> map = new HashMap<>();
            map.put("ErrorMsg", new JSONObject("Params Error"));
            JSONObject jsonMap = new JSONObject(map);
            
            res.send(HTTPResponse.StatusCode.BAD_REQUEST, jsonMap.toJson());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));

            String line = reader.readLine();
            assertEquals(line, defaultVersion + " "
                    + +HTTPResponse.StatusCode.BAD_REQUEST.getNum() + " "
                    + HTTPResponse.StatusCode.BAD_REQUEST.getMessage());
            while (!line.isEmpty()) {
                line = reader.readLine();
            }
            line = reader.readLine();

            assertEquals(line, jsonMap.toJson());
            receivedSocket.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception found.");
        }
    }

}
