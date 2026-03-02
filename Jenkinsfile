pipeline {
    agent any
    stages {
        stage('Compilar Local') {
            steps {
                // Opcional: Probar que Maven compila antes de mandarlo a AWS
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('Desplegar a AWS con Ansible') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'aws-kass-key', keyFileVariable: 'KEY')]) {
                    // Eliminamos la referencia manual a /var/jenkins_home/.ssh/KASS.pem
                    // y usamos ${KEY} que es donde Jenkins pone la llave temporalmente
                    sh "ansible-playbook -i hosts.ini deploy_app.yml --private-key=${KEY} -u ubuntu"
                }
            }
        }
    }
}
