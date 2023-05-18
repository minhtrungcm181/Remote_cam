package com.example.remotecamera;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentManager;

import com.example.remotecamera.mqtt.MQTTHelper;
import com.example.remotecamera.view.Fragment1;
import com.example.remotecamera.view.Fragment2;
import com.google.android.material.tabs.TabLayout;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class  MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;
    SwitchCompat cameraSwitch;
    TabLayout tabLayout;
    ProgressDialog progressDialog;
    String httpLink = "http://10.128.138.94:8880/";
    WebView webview;

    TextView status;
    TextView quantityText;
    TextView quantityText2;
    TextView dateText;
    GraphView graphViewQuantity;
    LineGraphSeries quantity = new LineGraphSeries<DataPoint>();
//    GraphView graphViewQuantity2;
    LineGraphSeries quantity2 = new LineGraphSeries<DataPoint>();
    Button buttonCamera1;
    Button buttonCamera2;
    SwitchCompat camera1;
    SwitchCompat camera2;
    Notification notification;
    NotificationManager notificationManager;
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment1 fragment1 = (Fragment1) fragmentManager.findFragmentById(R.id.fragmentContainerView2);
    Fragment2 fragment2 = (Fragment2) fragmentManager.findFragmentById(R.id.fragmentContainerView2);
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        graphViewQuantity = (GraphView) findViewById(R.id.graph1);
        graphViewQuantity.getGridLabelRenderer().setGridColor(Color.WHITE);
        graphViewQuantity.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graphViewQuantity.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graphViewQuantity.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graphViewQuantity.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);

//        graphViewQuantity2 = (GraphView) findViewById(R.id.graph2);
//        graphViewQuantity2.getGridLabelRenderer().setGridColor(Color.WHITE);
//        graphViewQuantity2.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
//        graphViewQuantity2.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
//        graphViewQuantity2.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
//        graphViewQuantity2.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        quantityText = (TextView) findViewById(R.id.textQuantity);
        quantityText2 = (TextView) findViewById(R.id.textQuantity2);
        dateText = (TextView) findViewById(R.id.textDate);
        Date date = new Date();  // Assuming you have a Date object

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  // Format pattern
        String dateString = dateFormat.format(date);  // Convert Date to String
        dateText.setText("Date: " + dateString);
        camera1 = findViewById(R.id.buttonCamera1);
        camera1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Do something when the switch state changes
                if(camera1.isChecked() == true) {
                    sendDataMQTT("minhtrung181/feeds/switchcamerafront", "1");
                }
                else if(camera1.isChecked() == false) {
                    sendDataMQTT("minhtrung181/feeds/switchcamerafront", "0");
                }
            }
        });


        camera2 = findViewById(R.id.buttonCamera2);
        camera2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Do something when the switch state changes
                if(camera2.isChecked() == true) {
                    sendDataMQTT("minhtrung181/feeds/switchcameraback", "1");
                }
                else if(camera2.isChecked() == false) {
                    sendDataMQTT("minhtrung181/feeds/switchcameraback", "0");
                }
            }
        });



        buttonCamera1 = findViewById(R.id.button1);

        buttonCamera1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView2, Fragment1.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name")
                        .commit();
            }
        });

        buttonCamera2 = findViewById(R.id.button2);

        buttonCamera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView2, Fragment2.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name")
                        .commit();
            }
        });

//        status = findViewById(R.id.textViewNaming);
//        quantityText = findViewById(R.id.textQuantity);
//        dateText = findViewById(R.id.textDate);
//        LocalDateTime currentDateTime = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
//        String formattedDateTime = currentDateTime.format(formatter);
//
//        dateText.setText("Date: " + formattedDateTime+"h");

        startMQTT();
    }


    private int getNotificationId() {
        return (int) new Date().getTime();
    }
    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){
        }
    }

    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @SuppressLint("ServiceCast")
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST",topic+ "***" +message.toString());


                if (topic.contains("switchcamera1")){
                    // on off camera 1

                    if (message.toString().equals("1")) {
                        camera1.setChecked(true);
                    }
                    else {
                        camera1.setChecked(false);
                    }
                }
                else if (topic.contains("switchcamera2")){
                    // on off camera 2

                    if (message.toString().equals("1")) {
                        camera2.setChecked(true);
                    }
                    else {
                        camera2.setChecked(false);
                    }
                }


                else if(topic.contains("ainoti") || topic.contains("ainoti2")){
                    PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
                        // create a notification for detecting people
                    notification = new Notification.Builder(MainActivity.this)
                            .setContentTitle("Detect People !")
                            .setContentText("Tap to open Remote Camera !")
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.drawable.first_logo).build();

                    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(getNotificationId(), notification);

                }
                else if (topic.contains("numfront"))  {
                    double x = Double.parseDouble(message.toString());
                    LocalTime currentTime = LocalTime.now();
                    int second = currentTime.getSecond();
                    int minute = currentTime.getMinute();
                    double y = minute + (second / 60.0);
                    quantity.appendData(new DataPoint(y,x), true, 60, true);
                    quantity.setColor(Color.GREEN);
                    graphViewQuantity.addSeries(quantity);
                    graphViewQuantity.onDataChanged(true, true);
                    quantityText.setText("Quantity of people current detect in front: " + message.toString());

                }
                else if (topic.contains("numback")) {
                    double x = Double.parseDouble(message.toString());
                    LocalTime currentTime = LocalTime.now();
                    int second = currentTime.getSecond();
                    int minute = currentTime.getMinute();
                    double y = minute + (second / 60.0);
                    quantity2.appendData(new DataPoint(y,x), true, 60, true);
                    quantity2.setColor(Color.BLUE);
                    graphViewQuantity.addSeries(quantity2);
                    graphViewQuantity.onDataChanged(true, true);
                    quantityText2.setText("Quantity of people current detect in back: " + message.toString());
                }


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });

    }
}
