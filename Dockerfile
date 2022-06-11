FROM openjdk:8-jdk-oraclelinux7
WORKDIR /app
COPY wait-for-it.sh /app
RUN chmod +x /app/wait-for-it.sh
COPY dumb-init /usr/local/bin/dumb-init
RUN chmod +x /usr/local/bin/dumb-init
RUN useradd -rm -d /home/javauser -s /bin/bash -g root -G users -u 1001 javauser
RUN chown -R javauser:root /app
USER javauser
COPY ./target/*.jar /app/app.jar
ENTRYPOINT ["/app/wait-for-it.sh","-t","30", "auth-server:8080", "--","dumb-init","java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]



