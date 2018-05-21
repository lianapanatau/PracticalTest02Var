package practicaltest02.eim.systems.cs.pub.ro.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import practicaltest02.eim.systems.cs.pub.ro.practicaltest02.network.ClientThread;
import practicaltest02.eim.systems.cs.pub.ro.practicaltest02.network.ServerThread;
import practicaltest02.eim.systems.cs.pub.ro.practicaltest02.util.Constants;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText serverPort = null;
    private Button connectBtn= null;
    private EditText keyPut = null;
    private EditText valuePut = null;
    private Button putBtn = null;
    private EditText keyGet = null;
    private Button getBtn = null;
    private TextView result = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPort = (EditText)findViewById(R.id.server_port_edit_text);
        connectBtn = (Button)findViewById(R.id.connect_button);
        keyPut = (EditText)findViewById(R.id.keyPut);
        valuePut = (EditText)findViewById(R.id.valuePut);
        putBtn = (Button) findViewById(R.id.putBtn);
        keyGet = (EditText)findViewById(R.id.keyGet);
        getBtn = (Button) findViewById(R.id.getBtn);
        result = (TextView) findViewById(R.id.result);


        connectBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String server = serverPort.getText().toString();
                if (server.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                }
                serverThread = new ServerThread(Integer.parseInt(server));
                serverThread.start();
            }
        });

        putBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String putValue = valuePut.getText().toString();
                String putKey = keyPut.getText().toString();
                if (putValue.isEmpty() || putKey.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Key/Value should be filled!", Toast.LENGTH_SHORT).show();
                }

                clientThread = new ClientThread("put", putKey, putValue, result);
                clientThread.start();

                result.setText(Constants.EMPTY_STRING);
            }
        });

        getBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getKey = keyGet.getText().toString();
                if (getKey.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Key should be filled!", Toast.LENGTH_SHORT).show();
                }

                clientThread = new ClientThread("get", getKey, null, result);
                clientThread.start();

                result.setText(Constants.EMPTY_STRING);
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
