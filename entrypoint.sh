#!/bin/bash

# 1. Crear un archivo de contraseña real en memoria (temporal)
echo "AS_ADMIN_PASSWORD=" > /tmp/glassfishpw

# 2. Iniciar el dominio
asadmin start-domain

echo "Esperando a que Payara levante..."
sleep 20

echo "Configurando recursos JDBC..."
# Usamos el archivo temporal en lugar de /dev/null
asadmin --user admin --passwordfile /tmp/glassfishpw multimode --file /opt/payara/scripts/setup.asadmin

echo "Desplegando la aplicación..."
# Añadimos --force para que si ya existe, lo sobrescriba sin errores
asadmin --user admin --passwordfile /tmp/glassfishpw deploy --force /opt/payara/deployments/KASS2.war

# Mantener el contenedor vivo
tail -f /opt/payara/appserver/glassfish/domains/domain1/logs/server.log