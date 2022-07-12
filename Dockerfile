FROM openjdk:8-jdk-oraclelinux7

COPY dumb-init /usr/local/bin/dumb-init
RUN chmod +x /usr/local/bin/dumb-init

WORKDIR /app

RUN useradd -rm -d /home/concierge -s /bin/bash -g root -G users -u 1001 concierge
RUN chown -R concierge:root /app

USER concierge

ENTRYPOINT ["/usr/local/bin/dumb-init", "--"]

COPY ./target/*.jar app.jar

CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]
