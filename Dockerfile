FROM alpine/git
WORKDIR /app
RUN git clone https://github.com/IvanMTD/sportdata.git

FROM maven
WORKDIR /app
COPY --from=0 /app/sportdata /app
RUN mvn clean package -DskipTests

FROM bellsoft/liberica-openjdk-alpine
EXPOSE 8060 8080
WORKDIR /app
COPY --from=1 /app/target/sportdata-1.0.0.jar /app
#CMD ["java","-Xms64m","-Xmx900m","-jar","infosec-1.0.0.jar"]
CMD ["java","-jar","sportdata-1.0.0.jar"]
