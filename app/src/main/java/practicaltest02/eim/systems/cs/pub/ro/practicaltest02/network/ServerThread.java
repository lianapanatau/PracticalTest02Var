package practicaltest02.eim.systems.cs.pub.ro.practicaltest02.network;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.client.ClientProtocolException;
import practicaltest02.eim.systems.cs.pub.ro.practicaltest02.util.Constants;

public class ServerThread extends Thread {

    private int port = 0;
    private ServerSocket serverSocket = null;
    private HashMap<String, String> data;
    private HashMap<String, String> processTime;

    public ServerThread(int port) {
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
        this.data = new HashMap<>();
        this.processTime = new HashMap<>();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public synchronized void setData(String key, String value, String time) {
        this.data.put(key, value);
        this.processTime.put(key, time);
    }

    public synchronized HashMap<String, String> getData() {
        return data;
    }

    public synchronized HashMap<String, String> getProcessTime() {
        return processTime;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER THREAD] Waiting for a client invocation...");
                Socket socket = serverSocket.accept();
                Log.i(Constants.TAG, "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (ClientProtocolException clientProtocolException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + clientProtocolException.getMessage());
            if (Constants.DEBUG) {
                clientProtocolException.printStackTrace();
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }

}
