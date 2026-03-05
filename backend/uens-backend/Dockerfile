# ============================================================
# UENS Backend - Dockerfile multi-stage
# ============================================================
# Este Dockerfile separa build y runtime para:
# - reducir tamano de imagen final
# - no incluir herramientas de compilacion en produccion
# - mantener un flujo reproducible de empaquetado

# =========================
# 1) BUILD STAGE
# =========================
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Copiamos wrapper y pom primero para aprovechar cache de Docker
# cuando cambia codigo pero no cambian dependencias.
COPY .mvn/ .mvn/
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd
COPY pom.xml pom.xml

# Toolchains.xml para alinear plugin maven-toolchains con JDK 21 del contenedor.
RUN mkdir -p /root/.m2 && \
    printf '%s\n' \
    '<?xml version="1.0" encoding="UTF-8"?>' \
    '<toolchains>' \
    '  <toolchain>' \
    '    <type>jdk</type>' \
    '    <provides>' \
    '      <version>21</version>' \
    '      <vendor>temurin</vendor>' \
    '    </provides>' \
    '    <configuration>' \
    '      <jdkHome>/opt/java/openjdk</jdkHome>' \
    '    </configuration>' \
    '  </toolchain>' \
    '</toolchains>' > /root/.m2/toolchains.xml

# En Linux normalmente mvnw ya es ejecutable, pero este guard rail evita fallos
# en repositorios clonados desde Windows.
RUN chmod +x mvnw || true

# Descarga anticipada de dependencias para acelerar builds posteriores.
RUN ./mvnw -DskipTests dependency:go-offline

# Ahora copiamos codigo de aplicacion y compilamos.
COPY src/ src/
RUN ./mvnw -DskipTests package

# =========================
# 2) RUN STAGE
# =========================
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos solo el JAR final desde etapa build.
COPY --from=build /workspace/target/*.jar /app/app.jar

# Puerto de escucha de Spring Boot.
EXPOSE 8080

# Entry point minimalista.
ENTRYPOINT ["java","-jar","/app/app.jar"]
