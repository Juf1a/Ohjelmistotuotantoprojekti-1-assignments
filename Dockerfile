FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q package -DskipTests dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target/lib \
    && mkdir target/fx && mv target/lib/javafx-*.jar target/fx/

FROM eclipse-temurin:17-jre-jammy
RUN apt-get update && apt-get install -y --no-install-recommends \
        libgtk-3-0 libglib2.0-0 libgl1 libxtst6 libxxf86vm1 libxrender1 libxi6 fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/classes ./classes
COPY --from=build /app/target/lib ./lib
COPY --from=build /app/target/fx ./fx

ENV DISPLAY=host.docker.internal:0.0
ENV DB_URL=jdbc:mariadb://host.docker.internal:3306/temperature_db

CMD ["java", "-Dprism.order=sw", "--module-path", "fx", "--add-modules", "javafx.controls", "-cp", "classes:lib/*", "com.example.Main"]
