pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = 'ctmyname'
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        KUBECONFIG_CREDENTIALS_ID = 'kubeconfig-credentials'  // Đã đổi tên biến cho rõ ràng
        PATH = "/var/jenkins_home/bin:$PATH"
        MINIKUBE_IP = '192.168.49.2'
    }
    stages {
        stage('Setup Tools') {
            steps {
                script {
                    sh '''
                        mkdir -p /var/jenkins_home/bin
                        # Cài đặt docker-compose nếu chưa có
                        if ! command -v docker-compose &> /dev/null; then
                            curl -L "https://github.com/docker/compose/releases/download/v2.24.6/docker-compose-$(uname -s)-$(uname -m)" -o /var/jenkins_home/bin/docker-compose
                            chmod +x /var/jenkins_home/bin/docker-compose
                        fi
                        docker-compose --version || { echo "Cài đặt Docker Compose thất bại"; exit 1; }

                        # Cài đặt kubectl nếu chưa có
                        if ! command -v kubectl &> /dev/null; then
                            curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
                            chmod +x kubectl
                            mv kubectl /var/jenkins_home/bin/
                        fi
                        kubectl version --client || { echo "Cài đặt kubectl thất bại"; exit 1; }

                        # Kiểm tra kết nối Docker
                        docker ps || { echo "Không thể kết nối với Docker daemon"; exit 1; }
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
                    sh '''
                        cp .env.example .env
                        cp chat-service/.env.example chat-service/.env
                        cp inventory-service/.env.example inventory-service/.env
                    '''
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
                            sed -i "s|SENDINBLUE_API_KEY=placeholder|SENDINBLUE_API_KEY=$SENDINBLUE_API_KEY|" .env
                            sed -i "s|PORT=placeholder|PORT=$CHAT_PORT|" chat-service/.env
                            sed -i "s|REACT_URL=placeholder|REACT_URL=$REACT_URL|" chat-service/.env
                            sed -i "s|DB_SSL=placeholder|DB_SSL=$DB_SSL|" chat-service/.env
                            sed -i "s|DB_USERNAME=placeholder|DB_USERNAME=$DB_USERNAME|" chat-service/.env
                            sed -i "s|DB_PASSWORD=placeholder|DB_PASSWORD=$DB_PASSWORD|" chat-service/.env
                            sed -i "s|DB_DATABASE_NAME=placeholder|DB_DATABASE_NAME=$DB_DATABASE_NAME|" chat-service/.env
                            sed -i "s|DB_HOST=placeholder|DB_HOST=$DB_HOST|" chat-service/.env
                            sed -i "s|DB_PORT=placeholder|DB_PORT=$DB_PORT|" chat-service/.env
                            sed -i "s|DB_DIALECT=placeholder|DB_DIALECT=$DB_DIALECT|" chat-service/.env
                            sed -i "s|OPENAI_API_KEY=placeholder|OPENAI_API_KEY=$OPENAI_API_KEY|" chat-service/.env
                            sed -i "s|PORT=8085|PORT=8085|" inventory-service/.env
                            sed -i "s|MONGO_URI=placeholder|MONGO_URI=$MONGO_URI|" inventory-service/.env
                            sed -i "s|ACCESSKEYID=placeholder|ACCESSKEYID=$ACCESSKEYID|" inventory-service/.env
                            sed -i "s|SECRETACCESSKEY=placeholder|SECRETACCESSKEY=$SECRETACCESSKEY|" inventory-service/.env
                            sed -i "s|REGION=placeholder|REGION=$REGION|" inventory-service/.env
                            sed -i "s|BUCKET_NAME=placeholder|BUCKET_NAME=$BUCKET_NAME|" inventory-service/.env
                            sed -i "s|OPENAI_API_KEY=placeholder|OPENAI_API_KEY=$OPENAI_API_KEY|" inventory-service/.env
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
                    '''
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

        stage('Prepare Kubeconfig') {
            steps {
                withCredentials([file(credentialsId: KUBECONFIG_CREDENTIALS_ID, variable: 'KUBECONFIG_FILE')]) {
                    script {
                        sh '''
                            # Tạo thư mục .kube và copy file config
                            mkdir -p ${WORKSPACE}/.kube
                            cp $KUBECONFIG_FILE ${WORKSPACE}/.kube/config-original

                            # Sửa đường dẫn certificate từ Windows sang Linux
                            sed -i 's|C:\\\\Users\\\\[^\\\\]*|/var/jenkins_home|g' ${WORKSPACE}/.kube/config-original
                            sed -i 's|\\\\|/|g' ${WORKSPACE}/.kube/config-original

                            # Cập nhật địa chỉ server Minikube
                            sed -i "s|server:.*|server: https://${MINIKUBE_IP}:8443|" ${WORKSPACE}/.kube/config-original

                            # Copy sang file config chính thức
                            cp ${WORKSPACE}/.kube/config-original ${WORKSPACE}/.kube/config
                            chmod 600 ${WORKSPACE}/.kube/config

                            # Kiểm tra kubeconfig
                            export KUBECONFIG=${WORKSPACE}/.kube/config
                            kubectl config view
                        '''
                    }
                }
            }
        }

        stage('Verify Kubernetes Connection') {
            steps {
                script {
                    sh '''
                        export KUBECONFIG=${WORKSPACE}/.kube/config

                        # Kiểm tra kết nối mạng tới Minikube
                        if ! curl -k --connect-timeout 5 https://${MINIKUBE_IP}:8443; then
                            echo "Không thể kết nối tới Minikube tại ${MINIKUBE_IP}:8443"
                            exit 1
                        fi

                        # Kiểm tra kết nối kubectl
                        if ! kubectl cluster-info; then
                            echo "ERROR: Không thể kết nối tới Kubernetes cluster"
                            exit 1
                        fi

                        kubectl get nodes
                    '''
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    sh '''
                        export KUBECONFIG=${WORKSPACE}/.kube/config

                        # Tạo namespace nếu chưa tồn tại
                        if ! kubectl get namespace flexshoes &> /dev/null; then
                            kubectl create namespace flexshoes
                        fi

                        # Áp dụng cấu hình Kubernetes
                        kubectl apply -f flexshoes-all.yaml -n flexshoes
                    '''
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                script {
                    sh '''
                        export KUBECONFIG=${WORKSPACE}/.kube/config
                        kubectl get pods -n flexshoes
                        kubectl rollout status deployment -n flexshoes
                    '''
                }
            }
        }
    }

    post {
        always {
            withCredentials([file(credentialsId: KUBECONFIG_CREDENTIALS_ID, variable: 'KUBECONFIG_FILE')]) {
                sh '''
                    export KUBECONFIG=${WORKSPACE}/.kube/config

                    # Lấy logs từ tất cả các pod
                    echo "===== Logs từ các pod ====="
                    for pod in $(kubectl get pods -n flexshoes -o name 2>/dev/null || echo ""); do
                        echo "Logs từ $pod:"
                        kubectl logs -n flexshoes $pod --tail=100 || true
                        echo ""
                    done

                    # Lấy thông tin tổng quan về cluster
                    echo "===== Thông tin cluster ====="
                    kubectl get all -n flexshoes
                '''
            }
        }

        failure {
            withCredentials([file(credentialsId: KUBECONFIG_CREDENTIALS_ID, variable: 'KUBECONFIG_FILE')]) {
                echo 'Triển khai Kubernetes thất bại!'
                sh '''
                    export KUBECONFIG=${WORKSPACE}/.kube/config

                    # Lấy thông tin chi tiết về lỗi
                    echo "===== Mô tả các pod bị lỗi ====="
                    kubectl describe pods -n flexshoes || true

                    echo "===== Events namespace flexshoes ====="
                    kubectl get events -n flexshoes --sort-by='.metadata.creationTimestamp' || true

                    echo "===== Logs từ các container bị lỗi ====="
                    for pod in $(kubectl get pods -n flexshoes --field-selector=status.phase!=Running -o name 2>/dev/null || echo ""); do
                        echo "Logs từ $pod:"
                        kubectl logs -n flexshoes $pod --all-containers=true --tail=100 || true
                        echo ""
                    done
                '''
            }
        }
    }
}