pipeline {
    agent any

    environment {
        DOCKER_HUB_CREDENTIAL = credentials('dockerHub')
    }

    options {
        // Configure an overall timeout for the build.
        timeout(time: 1, unit: 'HOURS')
        disableConcurrentBuilds()
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
        }
        stage('Deliver Docker images') {
            when {
                anyOf {
                    branch 'master'
                    buildingTag()
                }
            }
            steps {
                script {
                    env.DOCKER_TAG = 'branch-master'
                    if (env.TAG_NAME) {
                        env.DOCKER_TAG = env.TAG_NAME
                    }

                    echo "Docker tag: ${env.DOCKER_TAG}"

                    sh 'echo $DOCKER_HUB_CREDENTIAL_PSW | docker login -u $DOCKER_HUB_CREDENTIAL_USR --password-stdin'
                    sh 'docker build -f dockerfiles/docker-runner/Dockerfile -t linagora/james-gatling-runner:$DOCKER_TAG .'
                    sh 'docker push linagora/james-gatling-runner:$DOCKER_TAG'
                }
            }
            post {
                always {
                    sh 'docker logout || true'
                    sh 'docker rmi linagora/james-gatling-runner:$DOCKER_TAG || true'
                }
            }
        }
    }

    post {
        always {
            deleteDir() /* clean up our workspace */
        }
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
