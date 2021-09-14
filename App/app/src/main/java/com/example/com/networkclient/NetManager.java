package com.example.com.networkclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

/**
 * Created by com on 2015-07-10.
 */
public class NetManager implements Runnable {
    private static final String TAG = "NetManager";
    private String ServerIP;
    private int 	ServerPort;
    private Socket curSocket;
    private android.os.Handler mHandler;

    public final static int HANDLE_NETSTATUS = 1;
    public final static int HANDLE_RXCMD = 2;

    public static final String RX_LENGHT = "rxLength";
    public static final String RX_DATA   = "rxData";


    private int  netStatus;
    public final static int NET_NONE = 0;
    public final static int NET_DISCONNECT = 1;
    public final static int NET_CONNECTING = 2;
    public final static int NET_CONNECTED = 4;


    private BufferedInputStream ServerReader = null;
    private BufferedOutputStream ServerWriter = null;


    private boolean stopThreadflag = false;

    private boolean startedflag = false;

    public final static int CMD_ALIV= 0x01;
    Thread thread;

    public void setIpAndPort(String str , int port)
    {
        ServerIP = str ;
        ServerPort = port;

        netStatus = NET_NONE;
    }
    public int getNetStatus()
    {
        return netStatus;
    }
    public void setRxHandler(Handler handler)
    {
        mHandler = handler;
    }

    private Socket getSocket()
    {
        if ( curSocket == null)
            return null;

        if (curSocket.isConnected())
            return curSocket;
        else
            return null;
    }

    public int SendData(byte[] buff, int length)
    {
        if (curSocket == null)
            return 0;
        if (!curSocket.isConnected())
            return 0;

        int ret = length;
        try{
            ServerWriter.write(buff, 0, length);
            ServerWriter.flush();
        }
        catch(IOException e)
        {
            return 0;
        }

        return ret;
    }

    public int startThread()
    {
        if (startedflag)
            return 0;

        thread = new Thread(null, this, "CarNetManager");
        thread.start();
        startedflag = true;
        return 0;
    }

    public int stopThread()
    {
        if (!startedflag)
            return 0;

        Log.d(TAG,"stopThread enter");
        stopThreadflag = true;
        if (curSocket != null)
        {
            if(curSocket.isConnected())
            {
                try{
                    curSocket.close();
                }catch(IOException e){}
            }
        }

        try{
            thread.join();
            startedflag = false;
            Log.d(TAG,"Thread end");
        }
        catch(InterruptedException e){}
        return 0;
    }

    private void rxCmdProcess(Bundle data)
    {
        Message msg = new Message();
        msg.what= HANDLE_RXCMD;
        msg.setData(data);
        mHandler.sendMessage(msg);
    }

    private void netStatusSend(int Status)
    {
        if (netStatus == Status)
            return;

        netStatus  = Status;

        Message msg = new Message();
        msg.what = HANDLE_NETSTATUS;
        msg.arg1 = Status;

        mHandler.sendMessage(msg);

    }
    /*
     * ???????  Netowrk  ???? check  ??  connect
     *
     *
     */
    @Override
    public void run() {
        // TODO Auto-generated method stub
        byte[] rxBuff = new byte[128];
        int  readLenght = 0;
        while(true)
        {
            if ( stopThreadflag)
            {
                break;
            }

            if ( curSocket != null)
            {
                if (curSocket.isConnected())
                {
                    netStatusSend(NET_CONNECTED);

                    try
                    {
                        readLenght = ServerReader.read(rxBuff,0,128);
                        Bundle bundleObj = new Bundle();
                        bundleObj.putByteArray(RX_DATA, rxBuff);
                        bundleObj.putInt(RX_LENGHT, readLenght);

                        rxCmdProcess(bundleObj);
                        Log.d(TAG,"Rx:");
                        String temp = new String();
                        for (int i= 0; i < readLenght ;i++)
                        {
                            Log.d(TAG,String.format("%02X", rxBuff[i]));
                        }
                    }
                    catch(IOException e){
                        curSocket = null;
                    }

                }
                else
                {
                    curSocket = null;
                }

            }
            else
            {

                try{
                    netStatusSend(NET_CONNECTING);
                    Log.d(TAG, "enter new socket ");
                    //InetAddress serverAddr = InetAddress.getByName("192.168.10.57");
                    curSocket = new Socket(ServerIP,ServerPort);
                    ServerReader = new BufferedInputStream(curSocket.getInputStream());
                    ServerWriter = new BufferedOutputStream(curSocket.getOutputStream());
                    Log.d(TAG, "socket success ");
                }
                catch(IOException e)
                {
                    netStatusSend(NET_DISCONNECT);
                    curSocket = null;
                    System.out.println(e);
                }

                try{
                    Thread.sleep(10000);
                }
                catch(InterruptedException e){}

            }
        }
    }
}
