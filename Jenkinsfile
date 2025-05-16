pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = 'ctmyname'
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        KUBE_CONFIG = credentials('kubeconfig-credentials')
    }
    stages {
        stage('Setup Tools') {
            steps {
                script {
                    sh '''
                        if ! command -v docker-compose &> /dev/null; then
                                        curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
                                        chmod +x /usr/local/bin/docker-compose
                        fi
                        docker-compose --version
                        mkdir -p /var/jenkins_home/bin
                        if ! command -v kubectl &> /dev/null; then
                            curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
                            chmod +x kubectl
                            mv kubectl /var/jenkins_home/bin/
                        fi
                        kubectl version --client || echo "kubectl installation may have failed"
                    '''
                }
            }
        }
        stage('Checkout') {
            steps {
                git branch: 'release_4', url: 'https://github.com/hominhhau/FlexShoes_Microservice.git'
            }
        }
        stage('Prepare Environment') {
            steps {
                script {
                    sh 'cp .env.example .env || true'
                    sh 'cp chat-service/.env.example chat-service/.env || true'
                    sh 'cp inventory-service/.env.example inventory-service/.env || true'
                    withCredentials([
                        string(credentialsId: 'sendinblue-api-key', variable: 'SENDINBLUE_API_KEY'),
                        string(credentialsId: 'chat-port', variable: 'CHAT_PORT'),
                        string(credentialsId: 'react-url', variable: 'REACT_URL'),
                        string(credentialsId: 'db-ssl', variable: 'DB_SSL'),
                        string(credentialsId: 'db-username', variable: 'DB_USERNAME'),
                        string(credentialsId: 'db-password', variable: 'DB_PASSWORD'),
                        string(credentialsId: 'db-database-name', variable: 'DB_DATABASE_NAME'),
                        string(credentialsId: 'db-host', variable: 'DB_HOST'),
                        string(credentialsId: 'db-port', variable: 'DB_PORT'),
                        string(credentialsId: 'db-dialect', variable: 'DB_DIALECT'),
                        string(credentialsId: 'openai-api-key', variable: 'OPENAI_API_KEY'),
                        string(credentialsId: 'mongo-uri', variable: 'MONGO_URI'),
                        string(credentialsId: 'access-key-id', variable: 'ACCESSKEYID'),
                        string(credentialsId: 'secret-access-key', variable: 'SECRETACCESSKEY'),
                        string(credentialsId: 'region', variable: 'REGION'),
                        string(credentialsId: 'bucket-name', variable: 'BUCKET_NAME')
                    ]) {
                        sh '''
                            sed -i "s|SENDINBLUE_API_KEY=placeholder|SENDINBLUE_API_KEY=$SENDINBLUE_API_KEY|" .env || true
                            sed -i "s|PORT=placeholder|PORT=$CHAT_PORT|" chat-service/.env || true
                            sed -i "s|REACT_URL=placeholder|REACT_URL=$REACT_URL|" chat-service/.env || true
                            sed -i "s|DB_SSL=placeholder|DB_SSL=$DB_SSL|" chat-service/.env || true
                            sed -i "s|DB_USERNAME=placeholder|DB_USERNAME=$DB_USERNAME|" chat-service/.env || true
                            sed -i "s|DB_PASSWORD=placeholder|DB_PASSWORD=$DB_PASSWORD|" chat-service/.env || true
                            sed -i "s|DB_DATABASE_NAME=placeholder|DB_DATABASE_NAME=$DB_DATABASE_NAME|" chat-service/.env || true
                            sed -i "s|DB_HOST=placeholder|DB_HOST=$DB_HOST|" chat-service/.env || true
                            sed -i "s|DB_PORT=placeholder|DB_PORT=$DB_PORT|" chat-service/.env || true
                            sed -i "s|DB_DIALECT=placeholder|DB_DIALECT=$DB_DIALECT|" chat-service/.env || true
                            sed -i "s|OPENAI_API_KEY=placeholder|OPENAI_API_KEY=$OPENAI_API_KEY|" chat-service/.env || true
                            sed -i "s|PORT=8085|PORT=8085|" inventory-service/.env || true
                            sed -i "s|MONGO_URI=placeholder|MONGO_URI=$MONGO_URI|" inventory-service/.env || true
                            sed -i "s|ACCESSKEYID=placeholder|ACCESSKEYID=$ACCESSKEYID|" inventory-service/.env || true
                            sed -i "s|SECRETACCESSKEY=placeholder|SECRETACCESSKEY=$SECRETACCESSKEY|" inventory-service/.env || true
                            sed -i "s|REGION=placeholder|REGION=$REGION|" inventory-service/.env || true
                            sed -i "s|BUCKET_NAME=placeholder|BUCKET_NAME=$BUCKET_NAME|" inventory-service/.env || true
                            sed -i "s|OPENAI_API_KEY=placeholder|OPENAI_API_KEY=$OPENAI_API_KEY|" inventory-service/.env || true
                        '''
                    }
                }
            }
        }
        stage('Build Docker Images') {
            steps {
                script {
                    sh 'docker-compose build'
                }
            }
        }
        stage('Push Docker Images') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKER_CREDENTIALS_ID) {
                        sh 'docker-compose push'
                    }
                }
            }
        }
        stage('Deploy to Kubernetes') {
            steps {
                script {
                    sh '''
                        export KUBECONFIG=$KUBE_CONFIG
                        kubectl create namespace flexshoes || true
                        kubectl apply -f k8s/flexshoes-all.yaml -n flexshoes
                    '''
                }
            }
        }
        stage('Verify Deployment') {
            steps {
                script {
                    sh '''
                        export KUBECONFIG=$KUBE_CONFIG
                        kubectl get pods -n flexshoes -o wide
                        kubectl get services -n flexshoes
                        kubectl get ingress -n flexshoes
                    '''
                }
            }
        }
    }
    post {
        always {
            sh '''
                export KUBECONFIG=$KUBE_CONFIG
                kubectl logs -n flexshoes --all-pods --tail=100 || true
            '''
        }
        success {
            echo 'Kubernetes deployment completed successfully!'
        }
        failure {
            echo 'Kubernetes deployment failed!'
            sh '''
                export KUBECONFIG=$KUBE_CONFIG
                kubectl describe pods -n flexshoes || true
            '''
        }
    }
}