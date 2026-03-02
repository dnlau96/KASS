#!/bin/bash

# 1. Archivos de contraseñas
echo "AS_ADMIN_PASSWORD=glassfish" > /tmp/glassfishpw
echo "AS_ADMIN_NEWPASSWORD=admin123" >> /tmp/glassfishpw
echo "AS_ADMIN_PASSWORD=admin123" > /tmp/new_pw_file

# 2. Iniciar Payara
asadmin start-domain
echo "Esperando a que Payara levante..."
sleep 20

# 3. TRUCO: Enviamos una 'y' automática para aceptar el certificado SSL
echo "Cambiando contraseña..."
yes y | asadmin --user admin --passwordfile /tmp/glassfishpw change-admin-password

# 4. Habilitar secure-admin enviando también una 'y' por si acaso
echo "Habilitando Secure Admin..."
yes y | asadmin --user admin --passwordfile /tmp/new_pw_file enable-secure-admin

# 5. Reinicio necesario
asadmin stop-domain
asadmin start-domain
sleep 15

echo "Configurando recursos JDBC..."
# Creamos el pool
asadmin --user admin --passwordfile /tmp/new_pw_file --terse=true create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGSimpleDataSource --restype javax.sql.DataSource --property user=admin:password=password123:databaseName=kass2_db:serverName=postgres-kass:portNumber=5432 PostgresPool

# Creamos el recurso JNDI (Separado para asegurar éxito)
asadmin --user admin --passwordfile /tmp/new_pw_file --terse=true create-jdbc-resource --connectionpoolid PostgresPool jdbc/PostgresPool

echo "Desplegando la aplicación..."
asadmin --user admin --passwordfile /tmp/new_pw_file deploy --force /opt/payara/deployments/KASS2.war

tail -f /opt/payara/appserver/glassfish/domains/domain1/logs/server.log