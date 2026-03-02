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
                // Usamos las credenciales de la llave que guardaste en Jenkins
                withCredentials([sshUserPrivateKey(credentialsId: 'aws-kass-key', keyFileVariable: 'KEY')]) {
                    sh "ansible-playbook -i hosts.ini deploy_app.yml --private-key=${KEY}"
                }
            }
        }
    }
}
