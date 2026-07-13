pipeline {
    agent any

    environment {
        IMAGE_NAME = "rmscaibot"
        CONTAINER_NAME = "rmscaibot"
        REGISTRY_CONTAINER_NAME = "adminserviceregistry"
        DOCKER_NETWORK = "updated_orgadmin_rmscadminnetwork"
        HOST_PORT = "8080"
        CONTAINER_PORT = "8080"
        DOCKER_BUILDKIT = "0"
        OPENROUTER_API_KEY = credentials('openrouter-api-key')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Docker Version') {
            steps {
                sh 'docker --version'
            }
        }

        stage('Check Registry') {
            steps {
                script {
                    def isRegistryRunning = sh(
                        script: "docker ps -q -f name=${REGISTRY_CONTAINER_NAME}",
                        returnStdout: true
                    ).trim()

                    if (!isRegistryRunning) {
                        error "${REGISTRY_CONTAINER_NAME} is not running. Aborting deployment of ${CONTAINER_NAME}."
                    }

                    echo "${REGISTRY_CONTAINER_NAME} is running. Proceeding..."
                }
            }
        }

        stage('Ensure Docker Network') {
            steps {
                sh """
                    docker network inspect ${DOCKER_NETWORK} >/dev/null 2>&1 || \
                    docker network create ${DOCKER_NETWORK}
                """
            }
        }

        stage('Clean Old Container and Image') {
            steps {
                sh """
                    docker rm -f ${CONTAINER_NAME} || true
                    docker rmi -f ${IMAGE_NAME}:latest || true
                """
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                    DOCKER_BUILDKIT=0 docker build --no-cache -t ${IMAGE_NAME}:latest .
                """
            }
        }

        stage('Run Container') {
            steps {
                sh """
                    docker run -d --name ${CONTAINER_NAME} \
                        --restart unless-stopped \
                        -p ${HOST_PORT}:${CONTAINER_PORT} \
                        --network ${DOCKER_NETWORK} \
                        -e SPRING_DATASOURCE_URL=jdbc:postgresql://rmsc-pgvector:5432/rmsc_ai_db \
                        -e DB_PASSWORD=postgres \
                        -e SPRING_RABBITMQ_HOST=rabbitmq \
                        -e OPENROUTER_API_KEY \
                        ${IMAGE_NAME}:latest
                """
            }
        }
    }

    post {
        always {
            echo '✅ Pipeline execution completed.'
        }
    }
}
