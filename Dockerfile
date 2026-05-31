FROM azul/zulu-openjdk:25-jre-latest
RUN groupadd -r -g 999 ukulele && useradd -rd /opt/ukulele -g ukulele -u 999 -ms /bin/bash ukulele && mkdir -p /opt/ukulele/tmp
COPY --chown=ukulele:ukulele build/libs/ukulele.jar /opt/ukulele/ukulele.jar
USER ukulele
WORKDIR /opt/ukulele/
ENTRYPOINT ["java", "--enable-native-access=ALL-UNNAMED", "-Djava.io.tmpdir=/opt/ukulele/tmp", "-jar", "/opt/ukulele/ukulele.jar"]
