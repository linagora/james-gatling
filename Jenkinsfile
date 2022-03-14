pipeline {
    agent any
    stages {
        stage('Compile') {
            steps {
                sh 'sbt clean compile'
            }
        }
        stage('Test') {
            steps {
                sh 'sbt GatlingIt/test'
            }
            post {
                always {
                    deleteDir() /* clean up our workspace */
                }
            }
        }
    }
}