FROM gradle:7.4.0-jdk17

RUN ./gradlew installDist

CMD ./build/install/app/bin/app