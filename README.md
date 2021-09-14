# 🔥 fire-detected System
## 딥러닝을 활용한 화재감지 시스템
## :trophy: 프라임 경진대회 탑 엔지니어링 부문 대상 수상 :trophy:
## 🔗 시연영상 : https://youtu.be/1-66k5U4veQ
## 프로젝트 구상도
![1](https://user-images.githubusercontent.com/63203480/133270209-cd8e6308-2c40-44ba-b5f2-4317361e1b27.png)

## 프로젝트 흐름도
![2](https://user-images.githubusercontent.com/63203480/133270216-6674f578-ad9d-4f30-9926-5be8c338d7ee.PNG)

## 소개 
기존 화재감지기의 부식으로 인한 오류와, 빈번히 발생하는 오류로 인해 사람들이 화재경보가
발생하여도 화재가 아닐 것이라고 판단하고 대처하지 않아 큰 피해로 이어지는 경우가 많습니다.  

영상인식을 통한 화재감지기는 머신러닝을 구축하여 딥러닝을 통한 학습된 모델을 가지고
재학습하여 생성된 pb, label.txt 파일을 가지고 openCV와 연동한 영상을 통해 1차적으로 화재를 인식하고,   
연결된 2개의 가스 센서를 통해 화재를 2차적으로 인식합니다.

영상 인식과 가스 센서를 통해 화재라고 인식이되면 관리자에게 Pushetta 를 통해 화재가 발생하였다는 알림을 전송합니다.  
관리자가 알림을 받게 되면 앱을 통해 실시간으로 Mjpg-streamer 를 이용하여 구축한 스트리밍 서버를 통해 화재가 발생한 것이
맞는지 실시간 영상을 통해 확인 할 수 있고, 가스 센서 수치를 직접 확인하여 화재로 인한 피해를 최고화 할 수 있습니다.

## ❗ Role
- 📱 관리자 어플리케이션 개발
- 🎏 가스센서 값 읽어오기
- :hammer_and_wrench: 라즈베리파이 셋업

<hr>

### :nut_and_bolt: Sensor Main Code
#### 📝 PinMode 지정과 센서 값 읽어오기 위한 Code
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
#### 📝 센서 값 읽어오는 Code
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
#### 📝 센서 값들을 어플리케이션에 실시간으로 전송하기 위한 Code
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
#### 📝 디지털 값 ->아날로그 값으로 변경
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
#### 📝 Raspberry Pi 에 연결된 센서 값을 실시간으로 읽어오기 위한 Code
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
#### 📝 실시간 스트리밍 Code
- ServerIP 변수에 IP 주소가 들어가도록 구현
```java
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
```

<hr/>
