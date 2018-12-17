FROM openjdk:8-jdk-slim
RUN mkdir -p /opt/image-service
COPY . /opt/image-service

RUN apt-get update && apt-get install -y wget imagemagick

WORKDIR /opt/image-service/
RUN convert -version
#RUN cat /etc/ImageMagick-6/policy.xml
RUN sed -i 's|name=\"memory\" value=\"256MiB\"|name=\"memory\" value=\"4GiB\" |g' /etc/ImageMagick-6/policy.xml
RUN sed -i 's|name=\"map\" value=\"512MiB\"|name=\"map\" value=\"8GiB\" |g' /etc/ImageMagick-6/policy.xml
RUN sed -i 's|name=\"width\" value=\"16KP\"|name=\"width\" value=\"107.374MP\" |g' /etc/ImageMagick-6/policy.xml
RUN sed -i 's|name=\"height\" value=\"16KP\"|name=\"height\" value=\"107.374MP\" |g' /etc/ImageMagick-6/policy.xml
RUN sed -i 's|name=\"area\" value=\"128MB\"|name=\"area\" value=\"34.3597GP\" |g' /etc/ImageMagick-6/policy.xml
RUN sed -i 's|name=\"disk\" value=\"1GiB\"|name=\"disk\" value=\"4GiB\" |g' /etc/ImageMagick-6/policy.xml
#RUN cat /etc/ImageMagick-6/policy.xml
#RUN identify -list resource
RUN ./gradlew build -i

VOLUME /tmp
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "./build/libs/image-service-0.0.1-SNAPSHOT.jar"]
