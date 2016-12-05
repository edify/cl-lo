FROM openjdk:8

RUN mkdir -p /usr/share/cl-lo

ADD ./ssl-key /usr/share/cl-lo/ssl-key
ADD ./docker-entry.sh /usr/share/cl-lo/docker-entry.sh
ADD ./build/libs /usr/share/cl-lo/build/libs

WORKDIR /usr/share/cl-lo

RUN mv ./build/libs/cl-lo*.jar /usr/share/cl-lo/cl-lo.jar && \
    touch /cl-lo.jar && \
    chmod +x ./docker-entry.sh

ENV JAVA_OPTS=""

ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE

EXPOSE 8080

ENTRYPOINT [ "./docker-entry.sh" ]
