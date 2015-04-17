package org.general.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.general.json.JSONList;
import org.general.json.JSONMap;
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

    private JSONList genJSONList() {
        JSONList list = new JSONList();
        list.add(100);
        list.add(200);
        list.add(300);
        return list;
    }

    @Test
    public void testSendSuccess() {
        try {
            Socket clientSocket = new Socket(TEST_HOST, TEST_PORT);
            Socket receivedSocket = ss.accept();
            HTTPResponse res = new HTTPResponse(
                    receivedSocket.getOutputStream(), "TestServer");
            JSONList list = genJSONList();
            res.send(HTTPResponse.StatusCode.OK, list.toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));

            String line = reader.readLine();
            assertEquals(
                    line,
                    "HTTP/1.1 "
                            + HTTPResponse.StatusCode.OK.getNum()
                            + " "
                            + HTTPResponse.StatusMessage
                                    .get(HTTPResponse.StatusCode.OK));
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
            JSONMap map = new JSONMap();
            
            String errorMsg = "Params Error";
            map.put("ErrorMsg", errorMsg);
            res.send(HTTPResponse.StatusCode.BAD_REQUEST, map.toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));

            String line = reader.readLine();
            assertEquals(
                    line,
                    "HTTP/1.1 "
                            + HTTPResponse.StatusCode.BAD_REQUEST.getNum()
                            + " "
                            + HTTPResponse.StatusMessage
                                    .get(HTTPResponse.StatusCode.BAD_REQUEST));
            while (!line.isEmpty()) {
                line = reader.readLine();
            }
            line = reader.readLine();
            
            assertEquals(line, map.toString());
            receivedSocket.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception found.");
        }
    }

}
