package com.example.com.networkclient;

import android.app.Activity;
import android.content.Intent;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class ServerSetActivity extends Activity {

    EditText etServerIp;

    public final static String SERVER_IP = "com.example.sensorquery.SERVER_IP";
    public final static String PHONEID = "com.example.sensorquery.PHONEID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_set);

        // putExtra�� ���� ���޵� ���� getIntent�� ���� ����
        Intent ci = getIntent();
        String cServerIp = ci.getStringExtra(SERVER_IP);

        etServerIp = (EditText)findViewById(R.id.editTextServerIP);
        etServerIp.setText(cServerIp);

        //String set_SERVERIP = etServerIp.getText().toString();

        Button btn = (Button)findViewById(R.id.buttonSetOk);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // AnotherActivity���� �����͸� �����Ͽ� �ִ� �κ�
                Intent intentData = new Intent();
                intentData.putExtra(SERVER_IP, etServerIp.getText().toString());
                // ���� Activity�� ����

                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_server_set, menu);
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
}
