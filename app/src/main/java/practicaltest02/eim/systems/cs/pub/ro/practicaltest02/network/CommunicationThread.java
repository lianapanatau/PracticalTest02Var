package practicaltest02.eim.systems.cs.pub.ro.practicaltest02.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.Duration;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import practicaltest02.eim.systems.cs.pub.ro.practicaltest02.util.Constants;
import practicaltest02.eim.systems.cs.pub.ro.practicaltest02.util.Utilities;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client");
            String operation = bufferedReader.readLine();
            if (operation.equalsIgnoreCase("get")) {
                String getKey = bufferedReader.readLine();
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] GET " + getKey);

                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("http://worldclockapi.com/api/json/est/now");
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpGet, responseHandler);
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }

                try {
                    JSONObject content = new JSONObject(pageSourceCode);
                    String dateString = (String) content.get("currentDateTime");
                    String currentTime = dateString.substring(dateString.indexOf("T") + 1).split("-")[0];

                    String getValue = serverThread.getData().get(getKey);
                    if (getValue == null) {
                        printWriter.println("na");
                        printWriter.flush();
                        return;
                    }
                    String keyProcessTime = serverThread.getProcessTime().get(getKey);

                    int minutes1 = Integer.valueOf(keyProcessTime.split(":")[1]);
                    int minutes2 = Integer.valueOf(currentTime.split(":")[1]);

                    if (minutes2 - minutes1 < 1) {
                        printWriter.println(getValue);
                        printWriter.flush();
                    } else {
                        printWriter.println("Expired");
                        printWriter.flush();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                String putKey = bufferedReader.readLine();
                String putValue = bufferedReader.readLine();

                Log.i(Constants.TAG, "[COMMUNICATION THREAD] PUT " + putKey + "  " + putValue);

                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("http://worldclockapi.com/api/json/est/now");
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpGet, responseHandler);
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }

                try {
                    JSONObject content = new JSONObject(pageSourceCode);
                    String dateString = (String) content.get("currentDateTime");

                    String time = dateString.substring(dateString.indexOf("T") + 1).split("-")[0];
                    serverThread.setData(putKey, putValue, time);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
