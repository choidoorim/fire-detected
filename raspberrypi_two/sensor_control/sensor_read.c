#ifndef __SMOKE_H__ 
#define __SMOKE_H__ 

#include <stdlib.h>
#include <string.h> 
#include <unistd.h>
#include <stdio.h> 
#include <wiringPi.h> 
#include <wiringPiSPI.h> 

#define CS_MCP3208_0 7   //GPIO 8 (SPI_CE0_N)
#define CS_MCP3208_1 8
#define SPI_CHANNEL 0   //SPI Channel 
#define SPI_SPEED 1000000   //spi speed 
#define ADC_CH_0 0 
#define ADC_CH_1 1 

#define WARNING_LEVEL 700

#endif /*__SMOKE_H__*/


int ReadMcp3208_0(unsigned char adcChannel);
int ReadMcp3208_1(unsigned char adcChannel);
int readMQ4(int nAdcChannel1,int nAdcChannel2);
void getSensorData(void);
int initMQ4(void);

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

void getSensorData(void) 
{ 
 int SensingVal1,SensingVal2; 
 
 readMQ4(ADC_CH_0,ADC_CH_1); 
 
 return ; 
}

 
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


int main(int argc, char *argv[]) 
{

initMQ4();

return 0;

}

