package com.example.androiddatadatabinding.Activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.androiddatadatabinding.R;
import com.example.androiddatadatabinding.Util.PrinterCommands;
import com.example.androiddatadatabinding.Util.Utils;

import org.opencv.android.OpenCVLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

public class PrintBloetooth extends AppCompatActivity {
    private static final String TAG = PrintBloetooth.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT=0;
    BluetoothAdapter bluetoothAdapter  =BluetoothAdapter.getDefaultAdapter();
    ;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;

    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    TextView lblPrinterName;
    EditText textBox;

    public static byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33, -128, 0};
    BitSet dots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_bloetooth);

        // Create object of controls
        Button btnConnect = (Button) findViewById(R.id.btnConnect);
        Button btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        Button btnPrint = (Button) findViewById(R.id.btnPrint);

        textBox = (EditText) findViewById(R.id.txtText);

        lblPrinterName = (TextView) findViewById(R.id.lblPrinterName);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    FindBluetoothDevice();
                    openBluetoothPrinter();

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    disconnectBT();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    printData();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        if(bluetoothAdapter == null){
            //show upsupport bluetooth
        }else {
            if(!bluetoothAdapter.isEnabled()){
                //showdialogiDisable
            }else {
                //showdialogEnable

            }
        }
    }

    void FindBluetoothDevice() {

        try {

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                lblPrinterName.setText("No Bluetooth Adapter found");
            }
            if (bluetoothAdapter.isEnabled()) {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, 0);
            }

            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

            if (pairedDevice.size() > 0) {
                for (BluetoothDevice pairedDev : pairedDevice) {

                    // My Bluetoth printer name is BTP_F09F1A
                    if (pairedDev.getName().equals("BlueTooth Printer")) {
                        bluetoothDevice = pairedDev;
                        lblPrinterName.setText("Bluetooth Printer Attached: " + pairedDev.getName());
                        break;
                    }
                }
            }

            lblPrinterName.setText("Bluetooth Printer Attached");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    void openBluetoothPrinter() throws IOException {
        try {

            //Standard uuid from string //
            UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();

            beginListenData();

        } catch (Exception ex) {

        }
    }

    void beginListenData() {
        try {

            final Handler handler = new Handler();
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int byteAvailable = inputStream.available();
                            if (byteAvailable > 0) {
                                byte[] packetByte = new byte[byteAvailable];
                                inputStream.read(packetByte);

                                for (int i = 0; i < byteAvailable; i++) {
                                    byte b = packetByte[i];
                                    if (b == delimiter) {
                                        byte[] encodedByte = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedByte, 0,
                                                encodedByte.length
                                        );
                                        final String data = new String(encodedByte, "US-ASCII");
                                        readBufferPosition = 0;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                lblPrinterName.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            stopWorker = true;
                        }
                    }

                }
            });

            thread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @TargetApi(Build.VERSION_CODES.O)
    void printData( ) throws IOException {
        try {
            String msg = textBox.getText().toString();
            msg += "\n";
            outputStream.write(msg.getBytes());
            lblPrinterName.setText("Printing Text...");

            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                    R.drawable.logolakupanda);
            byte[] command = Utils.decodeBitmap(bmp);
            outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
            outputStream.write(command);
            outputStream.write(PrinterCommands.SET_LINE_SPACING_24);

            outputStream.write(PrinterCommands.ESC_ALIGN_LEFT );
            StringBuilder contentSbheader	= new StringBuilder();
            contentSbheader.append("Agen......: Fathurrohman Haikal" + "\n");
            contentSbheader.append("ID Agen...: Haikal_Ganteng" + "\n");
            outputStream.write(contentSbheader.toString().getBytes());
            outputStream.write(PrinterCommands.FEED_LINE);

            StringBuilder contentSb	= new StringBuilder();
            contentSb.append("Ref.......: 1901928119929" + "\n");
            contentSb.append("No HP.....: 08961238723" + "\n");
            contentSb.append("Penerima..: 001221234757" + "\n");
            contentSb.append("Nama......: Fulan Setia Putra" + "\n");
            contentSb.append("Nominal...: Rp10.000,-" + "\n");
            contentSb.append("Admin.....: Rp10.500,-" + "\n");
            contentSb.append("Status....: Sukses" + "\n");
            outputStream.write(contentSb.toString().getBytes());
            outputStream.write(PrinterCommands.FEED_LINE);

            outputStream.write(PrinterCommands.ESC_ALIGN_CENTER );

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
            String strTime =  mdformat.format(calendar.getTime());
            SimpleDateFormat mhformat = new SimpleDateFormat("yyyy / MM / dd ");
            String strDate = mhformat.format(calendar.getTime());

            StringBuilder contentSbFooter	= new StringBuilder();
            contentSbFooter.append("Simpan Resi ini Sebagai Bukti Transaksi Yang Sah" + "\n");
            contentSbFooter.append("Tgl Cetak : "+strDate+" "+strTime+"\n");
            outputStream.write(contentSbFooter.toString().getBytes());
            outputStream.write(PrinterCommands.FEED_LINE);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        OpenCVLoader.initDebug();
    }

    // Disconnect Printer //
    void disconnectBT() throws IOException {
        try {
            stopWorker = true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
            lblPrinterName.setText("Printer Disconnected.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the
        getMenuInflater().inflate(R.menu.menu_print, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_scan) {
            if(!bluetoothAdapter.enable()){

                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                startActivityForResult(intent, 1000);
            }
            bluetoothAdapter.startDiscovery();

        }

        return super.onOptionsItemSelected(item);
    }
    private void requestBluetooth() {

    }
}
