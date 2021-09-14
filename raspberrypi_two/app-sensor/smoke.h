#ifndef __SMOKE_H__
#define __SMOKE_H__

#include <stdio.h>
#include <wiringPi.h>
#include <wiringPiSPI.h>

#define CS_MCP3208	8			//GPIO 8 (SPI_CE0_N)
#define SPI_CHANNEL	0			//SPI Channel
#define SPI_SPEED	1000000		//spi speed
#define ADC_CHANNEL	0

#define WARNING_LEVEL	700

int ReadMcp3208ADC(unsigned char adcChannel);
int initMQ5(void);
int readMQ5(int nAdcChannel);

#endif  /* __SMOKE_H__ */