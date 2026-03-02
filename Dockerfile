# Usamos directamente Payara, sin la etapa de Maven
FROM payara/server-full:6.2024.11-jdk17
USER root

# Instalamos dos2unix y descargamos el driver de Postgres
RUN apt-get update && apt-get install -y wget dos2unix && rm -rf /var/lib/apt/lists/*
RUN wget https://jdbc.postgresql.org/download/postgresql-42.7.2.jar -P /opt/payara/appserver/glassfish/domains/domain1/lib/

RUN mkdir -p /opt/payara/scripts
COPY setup.asadmin /opt/payara/scripts/setup.asadmin
COPY entrypoint.sh /opt/payara/entrypoint.sh

# CAMBIO CLAVE: Copiamos el WAR que Ansible subió a la carpeta de despliegue
COPY KASS2.war /opt/payara/deployments/KASS2.war

# Limpiamos el archivo y damos permisos
RUN dos2unix /opt/payara/entrypoint.sh && \
    chmod +x /opt/payara/entrypoint.sh && \
    chown -R payara:payara /opt/payara/

USER payara
ENTRYPOINT ["/opt/payara/entrypoint.sh"]