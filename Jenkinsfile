pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = 'ctmyname'
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        PATH = "/var/jenkins_home/bin:$PATH"  // Thêm thư mục vào PATH
    }
    stages {
        stage('Setup Tools') {
            steps {
                script {
                    // Cài đặt Docker Compose vào thư mục user có quyền ghi
                    sh '''
                        # Tạo thư mục bin trong /var/jenkins_home nếu chưa có
                        mkdir -p /var/jenkins_home/bin

                        # Kiểm tra xem docker-compose đã tồn tại chưa
                        if ! command -v docker-compose &> /dev/null; then
                            curl -L "https://github.com/docker/compose/releases/download/v2.24.6/docker-compose-$(uname -s)-$(uname -m)" -o /var/jenkins_home/bin/docker-compose
                            chmod +x /var/jenkins_home/bin/docker-compose
                        fi
                        docker-compose --version || echo "Docker Compose installation may have failed, proceeding anyway"
                    '''
                }
            }
        }
        stage('Checkout') {
            steps {
                git branch: 'release_3', url: 'https://github.com/hominhhau/FlexShoes_Microservice.git'
            }
        }
        stage('Prepare Environment') {
            steps {
                script {
                    sh 'cp .env.example .env || true'
                    sh 'cp chat-service/.env.example chat-service/.env || true'
                    sh 'cp inventory-service/.env.example inventory-service/.env || true'
                    withCredentials([
                        // Biến cho .env (notification-service)
                        string(credentialsId: 'sendinblue-api-key', variable: 'SENDINBLUE_API_KEY'),
                        // Biến cho chat-service
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
                        // Biến cho inventory-service
                        string(credentialsId: 'mongo-uri', variable: 'MONGO_URI'),
                        string(credentialsId: 'access-key-id', variable: 'ACCESSKEYID'),
                        string(credentialsId: 'secret-access-key', variable: 'SECRETACCESSKEY'),
                        string(credentialsId: 'region', variable: 'REGION'),
                        string(credentialsId: 'bucket-name', variable: 'BUCKET_NAME')
                    ]) {
                        sh '''
                            # Cập nhật .env (notification-service)
                            sed -i "s|SENDINBLUE_API_KEY=placeholder|SENDINBLUE_API_KEY=$SENDINBLUE_API_KEY|" .env || true

                            # Cập nhật chat-service/.env
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

                            # Cập nhật inventory-service/.env
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
        stage('Deploy') {
            steps {
                script {
                    sh 'docker-compose down'
                    sh 'docker-compose up -d'
                }
            }
        }
        stage('Verify Workspace and Files') {
            steps {
                sh 'pwd'
                sh 'ls -l'
                sh 'ls -l chat-service || true'
                sh 'ls -l inventory-service || true'
                sh 'test -f chat-service/package.json || { echo "chat-service/package.json missing"; exit 1; }'
                sh 'test -f chat-service/src/server.js || { echo "chat-service/src/server.js missing"; exit 1; }'
                sh 'test -f inventory-service/package.json || { echo "inventory-service/package.json missing"; exit 1; }'
                sh 'test -f inventory-service/app.js || { echo "inventory-service/app.js missing"; exit 1; }'
            }
        }

        stage('Debug Workspace Chat') {
            steps {
                sh 'ls -l'
                sh 'ls -l chat-service'
                sh 'cat chat-service/package.json || true'
                sh 'ls -l chat-service/src/server.js || true'
                sh 'docker-compose up -d chat-service || true'
                sh '''
                    for i in {1..30}; do
                        if docker-compose ps chat-service | grep -q "Up"; then
                            docker-compose exec -T chat-service ls -l /app || true
                            docker-compose exec -T chat-service cat /app/package.json || true
                            docker-compose exec -T chat-service ls -l /app/src/server.js || true
                            exit 0
                        fi
                        echo "Waiting for chat-service to be up..."
                        sleep 2
                    done
                    echo "chat-service did not start in time"
                    docker-compose logs chat-service
                    exit 1
                '''
            }
        }

        stage('Debug Workspace Inventory') {
            steps {
                sh 'ls -l'
                sh 'ls -l inventory-service'
                sh 'cat inventory-service/package.json || true'
                sh 'ls -l inventory-service/app.js || true'
                sh 'docker-compose up -d inventory-service || true'
                sh '''
                    for i in {1..30}; do
                        if docker-compose ps inventory-service | grep -q "Up"; then
                            docker-compose exec -T inventory-service ls -l /app || true
                            docker-compose exec -T inventory-service cat /app/package.json || true
                            docker-compose exec -T inventory-service ls -l /app/app.js || true
                            exit 0
                        fi
                        echo "Waiting for inventory-service to be up..."
                        sleep 2
                    done
                    echo "inventory-service did not start in time"
                    docker-compose logs inventory-service
                    exit 1
                '''
            }
        }

    }
    post {
        always {
            sh 'docker-compose logs || true'
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}