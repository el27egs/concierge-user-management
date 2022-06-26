FROM openjdk:8-jdk-oraclelinux7
WORKDIR /app
COPY dumb-init /usr/local/bin/dumb-init
RUN chmod +x /usr/local/bin/dumb-init
RUN useradd -rm -d /home/concierge -s /bin/bash -g root -G users -u 1001 concierge
RUN chown -R concierge:root /app
USER concierge
COPY ./target/*.jar /app/app.jar
ENTRYPOINT ["/usr/bin/dumb-init", "--"]
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]



