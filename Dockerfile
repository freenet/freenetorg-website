FROM gradle:7.5.1-jdk17 AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon


FROM openjdk:17
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/freenetorg-website-1.2-SNAPSHOT-all.jar /app/Freenet.jar
ENTRYPOINT ["java", "-server", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/Freenet.jar"]
