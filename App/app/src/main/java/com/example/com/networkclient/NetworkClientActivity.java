package com.example.com.networkclient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
//import java.io.IOException;
//import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;


public class NetworkClientActivity extends Activity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_network_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final String TAG = "SensorQuery";

    final static int SERVER_SELECT = 2001;
    final static int SERVER_PORT = 8888;

    final static String  PREF_FILE_NAME = "ServerInfo";
    final static String  PREF_KEY_SERVERIP = "ServerIp";

    int  phoneID = 1; // phone id 1~128
    SharedPreferences prefs;

    String   ServerIP;

    Timer QuerySensorTimer;
    byte[]  packet;

    NetManager NetMgr;

    TextView NetStatus;

    boolean  connectingActionDoneFlag = false;

    int nSensingVal = 0;
    int nSensingVal1 = 0;
    int nSensingVal2 = 0;

//    private OutputStream outs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_client);

        NetMgr = new NetManager();
        NetMgr.setRxHandler(mNetHandler);

        packet = new byte[100];

        // get server ip , port
        prefs = getSharedPreferences(PREF_FILE_NAME,MODE_PRIVATE);
        ServerIP = prefs.getString(PREF_KEY_SERVERIP, ServerIP+":8080");

        // Server IP와 Server Port를 Activity에 표시
        disSeverSet(ServerIP,SERVER_PORT);
        Log.d(TAG,"ServerIP:" + ServerIP);
        Log.d(TAG, "ServerPort:" + SERVER_PORT);


        NetStatus = (TextView)findViewById(R.id.textViewNetState);

        displaySensingVal(nSensingVal);

        Button btn = (Button)findViewById(R.id.buttonServiceStart);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // UI 버튼 클릭 가능
                arg0.setClickable(false);
                // 버튼 활성화
                arg0.setEnabled(false);

                NetMgr.setIpAndPort(ServerIP, SERVER_PORT);
                NetMgr.startThread();

                connectingActionDoneFlag = true;
                // set Timer 4초후 실행, 1초마다 반복, SendCmd()실행
                startQuerySensorTimer();

                WebView webView = (WebView)findViewById(R.id.activity_main_webview);
                webView.setPadding(0,0,0,0);
                webView.getSettings().setBuiltInZoomControls(false);
                webView.getSettings().setJavaScriptEnabled(true);

                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                //webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

                String url ="http://"+ServerIP+":8080/javascript_simple.html";
                webView.loadUrl(url);
                //String imgSrcHtml = "<html><img src='" + url + "' /></html>";
                // String imgSrcHtml = url;
                //webView.loadData(imgSrcHtml, "text/html", "UTF-8");


            }
        });

        btn = (Button)findViewById(R.id.buttonServerSet);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                serverSel();
            }
        });

    }

    public void serverSel()
    {
        // Intent로 다른 Activity(AnotherActivity.class)를 실행시킬 때,
        Intent intent = new Intent(this, ServerSetActivity.class);

        // AnotherActivity에 데이터를 전달할 때 사용하는 putExtra
        // AnotherActivity에서는 getExtras()를 이용 데이터를 수신 - 보통 onCreate() 매서드에 구현
        intent.putExtra(ServerSetActivity.SERVER_IP, ServerIP);
        startActivityForResult(intent, SERVER_SELECT);
    }

    private void startQuerySensorTimer()
    {
        QuerySensorTimer = new Timer();
        QuerySensorTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                SendCmd();
                Log.d(TAG, "Send SensorQuery1");
            }

        }, 4000, 1000); // schedule(TimerTask task , long delay , long period)
    }
    private void stopQuerySensorTimer()
    {
        QuerySensorTimer.cancel();
        QuerySensorTimer.purge();
    }

    final static int PKT_INDEX_STX = 0;
    final static int PKT_INDEX_CMD = 1;
    //    final static int PKT_INDEX_DATA = 2;
    final static int PKT_INDEX_DATA1 = 2;
    final static int PKT_INDEX_DATA2 = 3;
    final static int PKT_INDEX_ETX = 4;

    final static byte PKT_STX = 0x01;
    final static byte PKT_ETX = 0x05;

    // data cmd
    final static int CMD_SENSOR_REQ = 0x10;
    final static int CMD_SENSOR_RES = 0x90;

    private int unSignedByteToInt(byte value)
    {
        int nTemp;
        if ( value >= 0)
            nTemp = (int)value;
        else
            nTemp = (int)value + 256;
        return nTemp;
    }
    private int SendCmd()
    {
        if ( NetMgr.getNetStatus() != NetManager.NET_CONNECTED)
        {
            return -1;
        }
        packet[PKT_INDEX_STX] = PKT_STX;
        packet[PKT_INDEX_CMD] = CMD_SENSOR_REQ;
//        packet[PKT_INDEX_DATA] = 0x00;
        packet[PKT_INDEX_DATA1] = 0x00;
        packet[PKT_INDEX_DATA2] = 0x00;
        packet[PKT_INDEX_ETX] = PKT_ETX;

        return NetMgr.SendData(packet,  5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if ( requestCode == SERVER_SELECT)
        {
            if (resultCode == RESULT_OK)
            {
                ServerIP = data.getStringExtra(ServerSetActivity.SERVER_IP);//ServerSetActivity 에 있는 ip 주소를 가져오는것

                Log.d(TAG,"setting ServerIP:" + ServerIP);
                phoneID = data.getIntExtra(ServerSetActivity.PHONEID,1);

                disSeverSet(ServerIP,SERVER_PORT);
                // save
                SharedPreferences.Editor ed = prefs.edit();
                ed.putString(PREF_KEY_SERVERIP, ServerIP);

                ed.commit();

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void disSeverSet(String ip , int port) //activity_network_client 에서 변경된 ip주소와 port 번호를 표시해주는것
    {
        TextView tv = (TextView)findViewById(R.id.textViewServerIP);
        tv.setText("Server IP:" + ip + ", PORT:" + Integer.toString(port));
    }

    private Handler mNetHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);

            switch(msg.what)
            {
                case NetManager.HANDLE_RXCMD:
                    doRxCmd(msg.getData());
                    break;
                case NetManager.HANDLE_NETSTATUS:
                    doNetStatus(msg.arg1);
                    break;
            }
        }
    };

    private void displaySensingVal(int value)
    {
        //textViewSensorValue
        TextView sensor = (TextView)findViewById(R.id.textViewSensingVal);
        sensor.setText("Sensing Value : " + value);
    }

    private void doRxCmd(Bundle data)
    {
        int len = data.getInt(NetManager.RX_LENGHT);
        if ( len < 5)
            return;
        byte[] dataArr = data.getByteArray(NetManager.RX_DATA);

        if (unSignedByteToInt(dataArr[PKT_INDEX_STX]) != PKT_STX)
        {
            Log.d(TAG,"doRxCmd - PKT_STX fail");
            return ;
        }
        Log.d(TAG,"dataArr[PKT_INDEX_CMD] : " + unSignedByteToInt(dataArr[PKT_INDEX_CMD]));
        Log.d(TAG,"CMD_SENSOR_RES : " + CMD_SENSOR_RES);
        if (unSignedByteToInt(dataArr[PKT_INDEX_CMD]) != CMD_SENSOR_RES)
        {
            Log.d(TAG,"doRxCmd - CMD_SENSOR_RES fail");
            return ;
        }
        if (unSignedByteToInt(dataArr[PKT_INDEX_ETX]) != PKT_ETX)
        {
            Log.d(TAG,"doRxCmd - PKT_ETX fail");
            return;
        }

        // SensingVal query
//        nSensingVal1 = dataArr[PKT_INDEX_DATA];
        nSensingVal1 = unSignedByteToInt(dataArr[PKT_INDEX_DATA1]);
        nSensingVal2 = unSignedByteToInt(dataArr[PKT_INDEX_DATA2]);

        Log.d(TAG, "nSensingVal1" + nSensingVal1);
        Log.d(TAG, "nSensingVal2" + nSensingVal2);

        nSensingVal = (nSensingVal2*256)+nSensingVal1;
        Log.d(TAG, "nSensingVal" + nSensingVal);

//        displaySensingVal(nSensingVal1);
        displaySensingVal(nSensingVal);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Log.d(TAG,"OnDestroy");
        if (connectingActionDoneFlag) {
            NetMgr.stopThread();
        }
        super.onDestroy();
    }
    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        Log.d(TAG, "onRestart");
        if (connectingActionDoneFlag)
        {
            startQuerySensorTimer();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG,"onStop");
        // TODO Auto-generated method stub
        if (connectingActionDoneFlag)
        {
            stopQuerySensorTimer();
        }
        super.onStop();
    }

    private void doNetStatus(int status)
    {
        switch(status)
        {
            case NetManager.NET_NONE:
                NetStatus.setText("Unkwown Network Status");
                break;
            case NetManager.NET_DISCONNECT:
                NetStatus.setText("Disconnected");
                break;
            case NetManager.NET_CONNECTING:
                NetStatus.setText("Connecting ...");
                break;
            case NetManager.NET_CONNECTED:
                NetStatus.setText("Connected");
                break;
        }
    }

}
