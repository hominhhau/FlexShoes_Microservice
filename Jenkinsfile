pipeline {
        agent any
    environment {
        DOCKER_REGISTRY = 'ctmyname'
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'
        PATH = "/var/jenkins_home/bin:$PATH"
        K8S_NAMESPACE = 'flexshoes'
    }
    stages {

      stage('Setup Tools') {
          steps {
              script {
                  sh '''
                      mkdir -p /var/jenkins_home/bin
                      # Cài đặt docker-compose nếu chưa có
                      if ! command -v docker-compose >/dev/null 2>&1; then
                          curl -L "https://github.com/docker/compose/releases/download/v2.24.6/docker-compose-$(uname -s)-$(uname -m)" -o /var/jenkins_home/bin/docker-compose || { echo "Tải docker-compose thất bại"; exit 1; }
                          chmod +x /var/jenkins_home/bin/docker-compose
                      fi
                      docker-compose --version || { echo "Cài đặt Docker Compose thất bại"; exit 1; }

                      # Cài đặt kubectl nếu chưa có
                      if ! command -v kubectl >/dev/null 2>&1; then
                          curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" || { echo "Tải kubectl thất bại"; exit 1; }
                          chmod +x kubectl
                          mv kubectl /var/jenkins_home/bin/ || { echo "Di chuyển kubectl thất bại"; exit 1; }
                      fi
                      kubectl version --client || { echo "Cài đặt kubectl thất bại"; exit 1; }

                      # Cài đặt Google Cloud SDK nếu chưa có
                      if ! command -v gcloud >/dev/null 2>&1; then
                          echo "Bắt đầu cài đặt Google Cloud SDK..."
                          curl -O https://dl.google.com/dl/cloudsdk/channels/rapid/downloads/google-cloud-sdk-450.0.0-linux-x86_64.tar.gz || { echo "Tải Google Cloud SDK thất bại"; exit 1; }
                          tar -xvf google-cloud-sdk-450.0.0-linux-x86_64.tar.gz || { echo "Giải nén Google Cloud SDK thất bại"; exit 1; }
                          # Xóa thư mục google-cloud-sdk cũ nếu tồn tại
                          rm -rf /var/jenkins_home/google-cloud-sdk || { echo "Xóa thư mục google-cloud-sdk cũ thất bại"; exit 1; }
                          mv google-cloud-sdk /var/jenkins_home/ || { echo "Di chuyển Google Cloud SDK thất bại"; exit 1; }
                          /var/jenkins_home/google-cloud-sdk/install.sh --quiet || { echo "Cài đặt Google Cloud SDK thất bại"; exit 1; }
                      fi
                      # Cập nhật PATH trực tiếp
                      export PATH=$PATH:/var/jenkins_home/google-cloud-sdk/bin
                      # Lưu PATH vào env.sh
                      echo "export PATH=$PATH:/var/jenkins_home/google-cloud-sdk/bin" > /var/jenkins_home/env.sh
                      # Kiểm tra cài đặt gcloud
                      gcloud --version || { echo "Google Cloud SDK không hoạt động"; exit 1; }
                      echo "Google Cloud SDK đã được cài đặt thành công: $(gcloud --version)"

                      # Kiểm tra kết nối Docker
                      docker ps || { echo "Không thể kết nối với Docker daemon"; exit 1; }
                  '''
              }
          }
      }

        stage('Checkout') {
            steps {
                git branch: 'deploy_gke', url: 'https://github.com/hominhhau/FlexShoes_Microservice.git'
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
                    sh '''
                        docker-compose build
                        echo "=== Danh sách images sau khi build ==="
                        docker images
                    '''
                }
            }
        }

        stage('Push Docker Images') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKER_CREDENTIALS_ID) {
                        sh '''
                            docker-compose push
                            echo "=== Xác nhận images đã đẩy ==="
                            docker images | grep ctmyname
                        '''
                    }
                }
            }
        }

       stage('Deploy to Kubernetes') {
           steps {
               script {
                   withCredentials([file(credentialsId: 'gke-credentials', variable: 'KUBECONFIG_FILE')]) {
                       sh '''
                           # Áp dụng PATH từ env.sh (sử dụng . thay vì source)
                           . /var/jenkins_home/env.sh || { echo "Không thể áp dụng PATH từ env.sh"; exit 1; }
                           gcloud auth activate-service-account --key-file=$KUBECONFIG_FILE || { echo "Xác thực Service Account thất bại"; exit 1; }
                           gcloud container clusters get-credentials flexshoes-cluster --region asia-southeast1-b --project flexshoes-project || { echo "Lấy thông tin GKE cluster thất bại"; exit 1; }
                           kubectl apply -f flexshoes-all.yaml || { echo "Áp dụng flexshoes-all.yaml thất bại"; exit 1; }
                           echo "=== Kiểm tra trạng thái deployment ==="
                           kubectl get deployments -n flexshoes
                           kubectl get pods -n flexshoes
                       '''
                   }
               }
           }
       }
    }
    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the logs for details.'
        }
    }
}