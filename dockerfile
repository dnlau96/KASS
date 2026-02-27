FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM payara/server-full:6.2024.11-jdk17
USER root
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*
RUN wget https://jdbc.postgresql.org/download/postgresql-42.7.2.jar -P /opt/payara/appserver/glassfish/domains/domain1/lib/

RUN mkdir -p /opt/payara/scripts
COPY setup.asadmin /opt/payara/scripts/setup.asadmin
COPY entrypoint.sh /opt/payara/entrypoint.sh
COPY --from=build /app/target/*.war /opt/payara/deployments/KASS2.war

RUN chmod +x /opt/payara/entrypoint.sh && chown -R payara:payara /opt/payara/
USER payara
ENTRYPOINT ["/opt/payara/entrypoint.sh"]