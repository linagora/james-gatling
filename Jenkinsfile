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
                sh 'sbt -Dapi.version=1.43 GatlingIt/test'
            }
            post {
                always {
                    deleteDir() /* clean up our workspace */
                }
            }
        }
    }

    post {
        failure {
            script {
                if (env.BRANCH_NAME == "master") {
                    emailext(
                        subject: "[BUILD-FAILURE]: Job '${env.JOB_NAME} [${env.BRANCH_NAME}] [${env.BUILD_NUMBER}]'",
                        body: """
                        BUILD-FAILURE: Job '${env.JOB_NAME} [${env.BRANCH_NAME}] [${env.BUILD_NUMBER}]'. Check console output at "<a href="${env.BUILD_URL}">${env.JOB_NAME} [${env.BRANCH_NAME}] [${env.BUILD_NUMBER}]</a>".
                        """,
                        to: "openpaas-james@linagora.com"
                    )
                }
            }
        }
    }
}