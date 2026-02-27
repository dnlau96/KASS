#!/bin/bash
asadmin start-domain
echo "Esperando a que Payara levante..."
sleep 15
echo "Configurando recursos JDBC..."
asadmin --user admin --passwordfile /dev/null multimode --file /opt/payara/scripts/setup.asadmin
echo "Desplegando la aplicación..."
asadmin --user admin --passwordfile /dev/null deploy --force /opt/payara/deployments/KASS2.war
tail -f /opt/payara/appserver/glassfish/domains/domain1/logs/server.log