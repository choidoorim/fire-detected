#include "smoke.h"

int setupWiringPiGpio(void);

int setupWiringPiGpio(void) 
{  
	if(wiringPiSetupGpio() == -1)  
	return 1; 
} 

int ReadMcp3208ADC(unsigned char adcChannel)
{
	unsigned char buff[3];
	int nAdcValue = 0;
	
	buff[0] = 0x06 | ((adcChannel & 0x07) >> 2);
	buff[1] = ((adcChannel & 0x07)<<6);
	buff[2] = 0x00;
	
	digitalWrite(CS_MCP3208,0);		//low cs Active
	
	wiringPiSPIDataRW(SPI_CHANNEL, buff, 3);
	
	//8bit data
	buff[1] = 0x0F & buff[1];
	nAdcValue = (buff[1]<<8) | buff[2];
	
	//spi chip Select command
	digitalWrite(CS_MCP3208, 1);
	
	return nAdcValue;
}

int initMQ5(void)
{
#if 0
	if(wiringPiSetupGpio() == -1){
		return 1;
	}
#endif
	if(wiringPiSPISetup(SPI_CHANNEL, SPI_SPEED) == -1) {
		return 1;
	}
	printf("MQ-5 Initialization\n");
	// SPI Init	
	pinMode(CS_MCP3208, OUTPUT);

	return 0;
}

int readMQ5(int nAdcChannel)
{
	int nAdcValue = 0;
	
	nAdcValue = ReadMcp3208ADC(nAdcChannel);		//sensorRead
	printf("Smoke Sensor Value = %u\n", nAdcValue);
	
	return nAdcValue;
}

int getSensorData(void) 
{
	int SensingVal;
	SensingVal = readMQ5(ADC_CHANNEL);
	return SensingVal;
}
