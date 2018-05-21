package practicaltest02.eim.systems.cs.pub.ro.practicaltest02.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import practicaltest02.eim.systems.cs.pub.ro.practicaltest02.util.Constants;
import practicaltest02.eim.systems.cs.pub.ro.practicaltest02.util.Utilities;

public class ClientThread extends Thread {


    private String operation;
    private String key;
    private String value;

    private Socket socket;

    private TextView textView;

    public ClientThread(String operation, String key, String value, TextView textView) {
        this.operation = operation;
        this.key = key;
        this.value = value;
        this.textView = textView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket("localhost", 2007);

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            printWriter.println(operation);
            printWriter.flush();
            if (operation.equalsIgnoreCase("get")) {
                printWriter.println(key);
                printWriter.flush();
            } else {
                printWriter.println(key);
                printWriter.flush();
                printWriter.println(value);
                printWriter.flush();
            }

            String result;
            while ((result = bufferedReader.readLine()) != null) {
                final String  finalResult = result;
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(finalResult);
                    }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
