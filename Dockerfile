FROM eclipse-temurin:11-jre-jammy
RUN groupadd -r -g 999 ukulele && useradd -rd /opt/ukulele -g ukulele -u 999 -ms /bin/bash ukulele
COPY --chown=ukulele:ukulele build/libs/ukulele.jar /opt/ukulele/ukulele.jar
USER ukulele
WORKDIR /opt/ukulele/
ENTRYPOINT ["java", "-jar", "/opt/ukulele/ukulele.jar"]