FROM concierge-base:1.0.0

COPY --chown=concierge:root ./target/*.jar app.jar

ENTRYPOINT ["/usr/local/bin/dumb-init", "--"]

CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]

