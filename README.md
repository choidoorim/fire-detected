# π₯ fire-detected System
## λ₯λ¬λμ νμ©ν νμ¬κ°μ§ μμ€ν
## :trophy: νλΌμ κ²½μ§λν ν μμ§λμ΄λ§ λΆλ¬Έ λμ μμ :trophy:
## π μμ°μμ : https://youtu.be/1-66k5U4veQ
## νλ‘μ νΈ κ΅¬μλ
![1](https://user-images.githubusercontent.com/63203480/133270209-cd8e6308-2c40-44ba-b5f2-4317361e1b27.png)

## νλ‘μ νΈ νλ¦λ
![2](https://user-images.githubusercontent.com/63203480/133270216-6674f578-ad9d-4f30-9926-5be8c338d7ee.PNG)

## μκ° 
κΈ°μ‘΄ νμ¬κ°μ§κΈ°μ λΆμμΌλ‘ μΈν μ€λ₯μ, λΉλ²ν λ°μνλ μ€λ₯λ‘ μΈν΄ μ¬λλ€μ΄ νμ¬κ²½λ³΄κ°
λ°μνμ¬λ νμ¬κ° μλ κ²μ΄λΌκ³  νλ¨νκ³  λμ²νμ§ μμ ν° νΌν΄λ‘ μ΄μ΄μ§λ κ²½μ°κ° λ§μ΅λλ€.  

μμμΈμμ ν΅ν νμ¬κ°μ§κΈ°λ λ¨Έμ λ¬λμ κ΅¬μΆνμ¬ λ₯λ¬λμ ν΅ν νμ΅λ λͺ¨λΈμ κ°μ§κ³ 
μ¬νμ΅νμ¬ μμ±λ pb, label.txt νμΌμ κ°μ§κ³  openCVμ μ°λν μμμ ν΅ν΄ 1μ°¨μ μΌλ‘ νμ¬λ₯Ό μΈμνκ³ ,   
μ°κ²°λ 2κ°μ κ°μ€ μΌμλ₯Ό ν΅ν΄ νμ¬λ₯Ό 2μ°¨μ μΌλ‘ μΈμν©λλ€.

μμ μΈμκ³Ό κ°μ€ μΌμλ₯Ό ν΅ν΄ νμ¬λΌκ³  μΈμμ΄λλ©΄ κ΄λ¦¬μμκ² Pushetta λ₯Ό ν΅ν΄ νμ¬κ° λ°μνμλ€λ μλ¦Όμ μ μ‘ν©λλ€.  
κ΄λ¦¬μκ° μλ¦Όμ λ°κ² λλ©΄ μ±μ ν΅ν΄ μ€μκ°μΌλ‘ Mjpg-streamer λ₯Ό μ΄μ©νμ¬ κ΅¬μΆν μ€νΈλ¦¬λ° μλ²λ₯Ό ν΅ν΄ νμ¬κ° λ°μν κ²μ΄
λ§λμ§ μ€μκ° μμμ ν΅ν΄ νμΈ ν  μ μκ³ , κ°μ€ μΌμ μμΉλ₯Ό μ§μ  νμΈνμ¬ νμ¬λ‘ μΈν νΌν΄λ₯Ό μ΅μν ν  μ μμ΅λλ€.

## β Role
- π± κ΄λ¦¬μ μ΄νλ¦¬μΌμ΄μ κ°λ°
- π κ°μ€μΌμ κ° μΈ‘μ  κΈ°λ₯ κ°λ°
- π μ΄νλ¦¬μΌμ΄μκ³Ό λΌμ¦λ² λ¦¬νμ΄ ν΅μ  κΈ°λ₯ κ°λ°
- :hammer_and_wrench: λΌμ¦λ² λ¦¬νμ΄ μμ

<hr>

### :nut_and_bolt: Sensor Main Code
#### π PinMode μ§μ κ³Ό μΌμ κ° μ½μ΄μ€κΈ° μν Code
```cs
int initMQ4(void) {
    wiringPiSetupGpio(); // Initalize Pi GPIO 
    if(wiringPiSPISetup(SPI_CHANNEL, SPI_SPEED) == -1) { 
    return 1;
    
    }
    printf("MQ-4,5 Initialization\n"); 
    pinMode(CS_MCP3208_0, OUTPUT);  // SPI Init
    pinMode(CS_MCP3208_1, OUTPUT);  // SPI Init
    
    getSensorData();
    
    return 0;
}
```
#### π μΌμ κ° μ½μ΄μ€λ Code
```cs
int readMQ4(int nAdcChannel1,int nAdcChannel2)
{
    int nAdcValuel1,nAdcValuel2;
    wiringPiSetupGpio(); // Initalize Pi GPIO
    while(1)
    {
        nAdcValuel1 = ReadMcp3208_0(nAdcChannel1);  //sensorRead
        nAdcValuel2 = ReadMcp3208_1(nAdcChannel2);

            printf("Smoke Sensor one Value = %u\n", nAdcValuel1);
            printf("Smoke Sensor two Value = %u\n", nAdcValuel2);

            if(nAdcValuel1>1000)
            {
                    printf("smoke detection from sensor one");

            }
            if(nAdcValuel2>1000)
            {
                    printf("smoke detection from sensor two");

            }

            delay(1000);
    }

    return nAdcValuel1,nAdcValuel2;
}  
```
#### π μΌμ κ°λ€μ μ΄νλ¦¬μΌμ΄μμ μ€μκ°μΌλ‘ μ μ‘νκΈ° μν Code
```cs
while(1)
{
	nAdcValuel1 = ReadMcp3208_0(nAdcChannel1);  //sensorRead
	nAdcValuel2 = ReadMcp3208_1(nAdcChannel2);

        printf("Smoke Sensor one Value = %u\n", nAdcValuel1);
		printf("Smoke Sensor two Value = %u\n", nAdcValuel2); 	

        if(nAdcValuel1>1000)
        {
                printf("smoke detection from sensor one");
                
        }
	if(nAdcValuel2>1000)
        {
                printf("smoke detection from sensor two");

        }

        delay(1000);
}
```
#### π λμ§νΈ κ° ->μλ λ‘κ·Έ κ°μΌλ‘ λ³κ²½
```cs
int ReadMcp3208_0(unsigned char adcChannel) 
{
unsigned char buff[3];
int nAdcValue = 0;

buff[0] = 0x06 | ((adcChannel & 0x07) >> 2);  
buff[1] = ((adcChannel & 0x07)<<6);  
buff[2] = 0x00;

digitalWrite(CS_MCP3208_0,0);  //low cs Active

wiringPiSPIDataRW(SPI_CHANNEL, buff, 3); 

//8bit data  
buff[1] = 0x0F & buff[1];  
nAdcValue = (buff[1] << 8) | buff[2];

//spi chip Select command  
digitalWrite(CS_MCP3208_0, 1);  

return nAdcValue;
}

int ReadMcp3208_1(unsigned char adcChannel) 
{
unsigned char buff[3];
int nAdcValue = 0;

buff[0] = 0x06 | ((adcChannel & 0x07) >> 2);  
buff[1] = ((adcChannel & 0x07)<<6);  
buff[2] = 0x00;

digitalWrite(CS_MCP3208_1,0);  //low cs Active

wiringPiSPIDataRW(SPI_CHANNEL, buff, 3); 

//8bit data  
buff[1] = 0x0F & buff[1];  
nAdcValue = (buff[1] << 8) | buff[2];

//spi chip Select command  
digitalWrite(CS_MCP3208_1, 1);  

return nAdcValue;
}
```
### :calling: Application Main Code
#### π Raspberry Pi μ μ°κ²°λ μΌμ κ°μ μ€μκ°μΌλ‘ μ½μ΄μ€κΈ° μν Code
```java
private void doRxCmd(Bundle data){
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
    // nSensingVal1 = dataArr[PKT_INDEX_DATA];
    nSensingVal1 = unSignedByteToInt(dataArr[PKT_INDEX_DATA1]);
    nSensingVal2 = unSignedByteToInt(dataArr[PKT_INDEX_DATA2]);
    
            Log.d(TAG, "nSensingVal1" + nSensingVal1);
            Log.d(TAG, "nSensingVal2" + nSensingVal2);
    
            nSensingVal = (nSensingVal2*256)+nSensingVal1;
            Log.d(TAG, "nSensingVal" + nSensingVal);
    
    // displaySensingVal(nSensingVal1);
    displaySensingVal(nSensingVal);
}
```
#### π μ€μκ° μ€νΈλ¦¬λ° Code
- ServerIP λ³μμ IP μ£Όμκ° λ€μ΄κ°λλ‘ κ΅¬ν
```java
@Override
public void onClick(View arg0) {
    // TODO Auto-generated method stub
    // UI λ²νΌ ν΄λ¦­ κ°λ₯
    arg0.setClickable(false);
    // λ²νΌ νμ±ν
    arg0.setEnabled(false);

    NetMgr.setIpAndPort(ServerIP, SERVER_PORT);
    NetMgr.startThread();

    connectingActionDoneFlag = true;
    // set Timer 4μ΄ν μ€ν, 1μ΄λ§λ€ λ°λ³΅, SendCmd()μ€ν
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
```

<hr/>
