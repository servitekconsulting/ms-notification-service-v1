
# Etapa de build: compila el JAR con Maven y JDK 17
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos pom y resolvemos dependencias en caché
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline

# Copiamos el código y compilamos
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# Etapa de runtime: imagen ligera con JRE 17
FROM eclipse-temurin:17-jre-jammy
WORKDIR /
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
