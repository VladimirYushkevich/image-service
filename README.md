image-service [![Build Status](https://travis-ci.org/VladimirYushkevich/image-service.svg?branch=master)](https://travis-ci.org/VladimirYushkevich/image-service)[![codecov](https://codecov.io/gh/VladimirYushkevich/image-service/branch/master/graph/badge.svg)](https://codecov.io/gh/VladimirYushkevich/image-service)
=
### Description:

A prototype service to fetch and process this satellite data, with the goal of the initial spike being to turn it into 
standard RGB images that a human surveyor can look over.
Documentation is provided in */docs* directory.

### Run service:
```
docker-compose up
```

### Usage:
```
curl -X POST \
  http://localhost:8888/images \
  -H 'Content-Type: application/json' \
  -d '{
"granuleImages": "/33/U/UP/S2A_MSIL1C_20150704T101337_N0202_R022_T33UUP_20160606T205155.SAFE/GRANULE/S2A_OPER_MSI_L1C_TL_EPA__20160605T113933_A000162_T33UUP_N02.02/IMG_DATA",
"channelMap": "waterVapor"
}'
```

### Environment
macOS Sierra (version 10.12.6)  
java version "1.8.0_172"  
Java(TM) SE Runtime Environment (build 1.8.0_172-b11)  
Java HotSpot(TM) 64-Bit Server VM (build 25.172-b11, mixed mode)