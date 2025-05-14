pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = 'ctmyname'  // Tên tài khoản Docker Hub
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'  // ID thông tin đăng nhập Docker Hub
        IMAGE_TAG = "${env.BUILD_NUMBER}"  // Sử dụng số build làm tag phiên bản
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'release_3', url: 'https://github.com/hominhhau/FlexShoes_Microservice.git'
            }
        }
        stage('Prepare Environment') {
            steps {
                script {
                    // Copy các file .env.example thành .env
                    sh 'cp .env.example .env || true'  // .env ở thư mục gốc (notification-service)
                    sh 'cp chat-service/.env.example chat-service/.env || true'
                    sh 'cp inventory-service/.env.example inventory-service/.env || true'

                    // Thay thế giá trị placeholder bằng giá trị từ Jenkins Credentials
                    withCredentials([
                        // Biến cho .env (notification-service)
                        string(credentialsId: 'sendinblue-api-key', variable: 'SENDINBLUE_API_KEY'),
                        // Biến cho chat-service
                        string(credentialsId: 'chat-port', variable: 'CHAT_PORT'),
                        string(credentialsId: 'react-url', variable: 'REACT_URL'),
                        string(credentialsId: 'db-ssl', variable: 'DB_SSL'),
                        string(credentialsId: 'db-username', variable: 'DB_USERNAME'),
                        string(credentialsId: 'db-database-name', variable: 'DB_DATABASE_NAME'),
                        string(credentialsId: 'db-host', variable: 'DB_HOST'),
                        string(credentialsId: 'db-port', variable: 'DB_PORT'),
                        string(credentialsId: 'db-dialect', variable: 'DB_DIALECT'),
                        string(credentialsId: 'openai-api-key', variable: 'OPENAI_API_KEY'),
                        // Biến cho inventory-service
                        string(credentialsId: 'mongo-uri', variable: 'MONGO_URI'),
                        string(credentialsId: 'access-key-id', variable: 'ACCESSKEYID'),
                        string(credentialsId: 'secret-access-key', variable: 'SECRETACCESSKEY'),
                        string(credentialsId: 'region', variable: 'REGION'),
                        string(credentialsId: 'bucket-name', variable: 'BUCKET_NAME')
                    ]) {
                        sh '''
                            # Cập nhật .env (notification-service)
                            sed -i "s/SENDINBLUE_API_KEY=placeholder/SENDINBLUE_API_KEY=$SENDINBLUE_API_KEY/" .env || true

                            # Cập nhật chat-service/.env
                            sed -i "s/PORT=placeholder/PORT=$CHAT_PORT/" chat-service/.env || true
                            sed -i "s/REACT_URL=placeholder/REACT_URL=$REACT_URL/" chat-service/.env || true
                            sed -i "s/DB_SSL=placeholder/DB_SSL=$DB_SSL/" chat-service/.env || true
                            sed -i "s/DB_USERNAME=placeholder/DB_USERNAME=$DB_USERNAME/" chat-service/.env || true
                            sed -i "s/DB_PASSWORD=placeholder/DB_PASSWORD=$DB_PASSWORD/" chat-service/.env || true
                            sed -i "s/DB_DATABASE_NAME=placeholder/DB_DATABASE_NAME=$DB_DATABASE_NAME/" chat-service/.env || true
                            sed -i "s/DB_HOST=placeholder/DB_HOST=$DB_HOST/" chat-service/.env || true
                            sed -i "s/DB_PORT=placeholder/DB_PORT=$DB_PORT/" chat-service/.env || true
                            sed -i "s/DB_DIALECT=placeholder/DB_DIALECT=$DB_DIALECT/" chat-service/.env || true
                            sed -i "s/OPENAI_API_KEY=placeholder/OPENAI_API_KEY=$OPENAI_API_KEY/" chat-service/.env || true

                            # Cập nhật inventory-service/.env
                            sed -i "s/PORT=8085/PORT=8085/" inventory-service/.env || true
                            sed -i "s/MONGO_URI=placeholder/MONGO_URI=$MONGO_URI/" inventory-service/.env || true
                            sed -i "s/ACCESSKEYID=placeholder/ACCESSKEYID=$ACCESSKEYID/" inventory-service/.env || true
                            sed -i "s/SECRETACCESSKEY=placeholder/SECRETACCESSKEY=$SECRETACCESSKEY/" inventory-service/.env || true
                            sed -i "s/REGION=placeholder/REGION=$REGION/" inventory-service/.env || true
                            sed -i "s/BUCKET_NAME=placeholder/BUCKET_NAME=$BUCKET_NAME/" inventory-service/.env || true
                            sed -i "s/OPENAI_API_KEY=placeholder/OPENAI_API_KEY=$OPENAI_API_KEY/" inventory-service/.env || true
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
        stage('Deploy') {
            steps {
                script {
                    sh 'docker-compose down'
                    sh 'docker-compose up -d'
                }
            }
        }
    }
    post {
        always {
            sh 'docker-compose logs'
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}