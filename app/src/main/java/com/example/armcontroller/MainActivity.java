package com.example.armcontroller;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.slider.Slider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private String deviceName = null;
    private String deviceAddress;
    public static Handler handler;
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;

    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button buttonConnect = findViewById(R.id.buttonConnect);
        final Button buttonToggle = findViewById(R.id.buttonToggle);

        final Slider continuousSlider1 = findViewById(R.id.continuousSlider1);
        final Slider continuousSlider2 = findViewById(R.id.continuousSlider2);
        final Slider continuousSlider3 = findViewById(R.id.continuousSlider3);
        final Slider continuousSlider4 = findViewById(R.id.continuousSlider4);
        final Slider continuousSlider5 = findViewById(R.id.continuousSlider5);
        final Slider continuousSlider6 = findViewById(R.id.continuousSlider6);

        // Disable all control buttons unless connected to the BT module
        buttonToggle.setEnabled(false);
        continuousSlider1.setEnabled(false);
        continuousSlider2.setEnabled(false);
        continuousSlider3.setEnabled(false);
        continuousSlider4.setEnabled(false);
        continuousSlider5.setEnabled(false);
        continuousSlider6.setEnabled(false);

        deviceName = getIntent().getStringExtra("deviceName");
        if(deviceName != null){
            deviceAddress = getIntent().getStringExtra("deviceAddress");
            buttonConnect.setEnabled(false);

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter,deviceAddress);
            createConnectThread.start();
        }

        /*
        Second most important piece of Code. GUI Handler
         */
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        switch(msg.arg1){
                            case 1:
                                buttonConnect.setEnabled(true);
                                buttonToggle.setEnabled(true);
                                continuousSlider1.setEnabled(true);
                                continuousSlider2.setEnabled(true);
                                continuousSlider3.setEnabled(true);
                                continuousSlider4.setEnabled(true);
                                continuousSlider5.setEnabled(true);
                                continuousSlider6.setEnabled(true);
                                break;
                            case -1:
                                buttonConnect.setEnabled(true);
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
                        switch (arduinoMsg.toLowerCase()){
                            case "led is turned on":
                                break;
                            case "led is turned off":
                                break;
                        }
                        break;
                }
            }
        };

        // Select Bluetooth Device
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to adapter list
                Intent intent = new Intent(MainActivity.this, SelectDeviceActivity.class);
                startActivity(intent);
            }
        });
        // Button to ON/OFF RC car
        buttonToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmdText = null;
                String btnState = buttonToggle.getText().toString().toLowerCase();
                switch (btnState){
                    case "turn on":
                        buttonToggle.setText("Turn Off");
                        // Command to turn on car. Must match with the command in Arduino code
                        cmdText = "01on"+"\n";
                        break;
                    case "turn off":
                        buttonToggle.setText("Turn On");
                        // Command to turn off car. Must match with the command in Arduino code
                        cmdText = "01off"+"\n";
                        break;
                }
                // Send command to Arduino board
                connectedThread.write(cmdText);
            }
        });

        //Slider to control the PWM value
        continuousSlider1.addOnChangeListener(new Slider.OnChangeListener(){
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                String cmdText = null;
                cmdText = "01"+Float.toString(value)+"\n";
                // Send command to Arduino board
                connectedThread.write(cmdText);
            }
        });
        continuousSlider2.addOnChangeListener(new Slider.OnChangeListener(){
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                String cmdText = null;
                cmdText = "02"+Float.toString(value)+"\n";
                // Send command to Arduino board
                connectedThread.write(cmdText);
            }
        });
        continuousSlider3.addOnChangeListener(new Slider.OnChangeListener(){
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                String cmdText = null;
                cmdText = "03"+Float.toString(value)+"\n";
                // Send command to Arduino board
                connectedThread.write(cmdText);
            }
        });
        continuousSlider4.addOnChangeListener(new Slider.OnChangeListener(){
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                String cmdText = null;
                cmdText = "04"+Float.toString(value)+"\n";
                // Send command to Arduino board
                connectedThread.write(cmdText);
            }
        });
        continuousSlider5.addOnChangeListener(new Slider.OnChangeListener(){
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                String cmdText = null;
                cmdText = "05"+Float.toString(value)+"\n";
                // Send command to Arduino board
                connectedThread.write(cmdText);
            }
        });
        continuousSlider6.addOnChangeListener(new Slider.OnChangeListener(){
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                String cmdText = null;
                cmdText = "06"+Float.toString(value)+"\n";
                // Send command to Arduino board
                connectedThread.write(cmdText);
            }
        });
    }
    /* ============================ Thread to Create Bluetooth Connection =================================== */
    public static class CreateConnectThread extends Thread {

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

            try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.e("Status", "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.run();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }
    /* =============================== Thread for Data Transfer =========================================== */
    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                    buffer[bytes] = (byte) mmInStream.read();
                    String readMessage;
                    if (buffer[bytes] == '\n'){
                        readMessage = new String(buffer,0,bytes);
                        Log.e("Arduino Message",readMessage);
                        handler.obtainMessage(MESSAGE_READ,readMessage).sendToTarget();
                        bytes = 0;
                    } else {
                        bytes++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Send Error","Unable to send message",e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    /* ============================ Terminate Connection at BackPress ====================== */
    @Override
    public void onBackPressed() {
        // Terminate Bluetooth Connection and close app
        if (createConnectThread != null){
            createConnectThread.cancel();
        }
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}