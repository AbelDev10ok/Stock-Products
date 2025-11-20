FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app


# copiamos pom.xml y descargamos las dependencias
COPY pom.xml .
RUN mvn -q dependency:go-offline

# copiamos el codigo fuente
COPY src ./src

# compilamos el JAR generado en /app
RUN mvn -q package -DskipTests

# Etapa 2: contenedor final
FROM eclipse-temurin:17-jdk
WORKDIR /app

# copiamos el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# exponemos el puerto 8080
EXPOSE 8080

# comando para ejecutar la aplicacion
ENTRYPOINT ["java", "-jar", "app.jar"]




