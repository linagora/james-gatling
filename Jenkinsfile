pipeline {
    agent any

    options {
        // Configure an overall timeout for the build.
        timeout(time: 1, unit: 'HOURS')
    }

    stages {
        stage('Compile') {
            steps {
                sh 'rm -rf /home/jenkins/.sbt/1.0/staging/'
                sh 'sbt reload'
                sh 'sbt clean compile'
            }
        }
        stage('Test') {
            steps {
                sh 'docker pull linagora/tmail-backend:memory-branch-master'
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