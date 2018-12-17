image-service
=
### Description:

A prototype service to fetch and process this satellite data, with the goal of the initial spike being to turn it into 
standard RGB images that a human surveyor can look over.
Documentation is provided in */docs* directory.

### Important requirements

This app uses *ImageMagick* utility. It suppose to be installed (Would be great to create a docker container for it).

### Run service:
```
./gradlew clean build && java -jar build/libs/image-service-0.0.1-SNAPSHOT.jar
```

### Notes (TODO)

* Add docker compose with 2 containers for *image-service* and *ImageMagick*
* Some parallelism can be be added (splitting large files on chunks)

### Environment
macOS Sierra (version 10.12.6)  
java version "1.8.0_172"  
Java(TM) SE Runtime Environment (build 1.8.0_172-b11)  
Java HotSpot(TM) 64-Bit Server VM (build 25.172-b11, mixed mode)