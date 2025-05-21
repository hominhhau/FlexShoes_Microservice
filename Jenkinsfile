pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = 'ctmyname'
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        KUBECONFIG_CREDENTIALS_ID = 'kubeconfig-credentials'
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
                git branch: 'deploy_gke', url: 'https://github.com/hominhhau/FlexShoes_Microservice.git'
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
                    withCredentials([file(credentialsId: KUBECONFIG_CREDENTIALS_ID, variable: 'KUBECONFIG')]) {
                        sh '''
                            # Kiểm tra kết nối tới Kubernetes cluster
                            kubectl --kubeconfig=$KUBECONFIG version || { echo "Không thể kết nối tới Kubernetes cluster"; exit 1; }

                            # Tạo namespace nếu chưa tồn tại
                            kubectl --kubeconfig=$KUBECONFIG get namespace $K8S_NAMESPACE || kubectl --kubeconfig=$KUBECONFIG create namespace $K8S_NAMESPACE

                            # Áp dụng file manifest
                            kubectl --kubeconfig=$KUBECONFIG apply -f flexshoes-all.yaml -n $K8S_NAMESPACE

                            # Kiểm tra trạng thái triển khai
                            kubectl --kubeconfig=$KUBECONFIG rollout status deployment/eureka-server -n $K8S_NAMESPACE
                            kubectl --kubeconfig=$KUBECONFIG rollout status deployment/config-server -n $K8S_NAMESPACE
                            kubectl --kubeconfig=$KUBECONFIG rollout status deployment/mongodb -n $K8S_NAMESPACE
                            kubectl --kubeconfig=$KUBECONFIG rollout status deployment/postgresdb -n $K8S_NAMESPACE
                            kubectl --kubeconfig=$KUBECONFIG rollout status deployment/mariadb -n $K8S_NAMESPACE
                            kubectl --kubeconfig=$KUBECONFIG rollout status deployment/mssql -n $K8S_NAMESPACE
                            kubectl --kubeconfig=$KUBECONFIG rollout status deployment/chat-service -n $K8S_NAMESPACE
                            kubectl --kubeconfig=$KUBECONFIG rollout status deployment/payment-service -n $K8S_NAMESPACE
                            kubectl --kubeconfig=$KUBECONFIG rollout status deployment/order-service -n $K8S_NAMESPACE
                            kubectl --kubeconfig=$KUBECONFIG rollout status deployment/inventory-service -n $K8S_NAMESPACE
                            kubectl --kubeconfig=$KUBECONFIG rollout status deployment/notification-service -n $K8S_NAMESPACE
                            kubectl --kubeconfig=$KUBECONFIG rollout status deployment/api-gateway -n $K8S_NAMESPACE
                            kubectl --kubeconfig=$KUBECONFIG rollout status deployment/user-service -n $K8S_NAMESPACE
                            kubectl --kubeconfig=$KUBECONFIG rollout status deployment/profile-service -n $K8S_NAMESPACE

                            # Kiểm tra trạng thái Jobs
                            kubectl --kubeconfig=$KUBECONFIG wait --for=condition=complete --timeout=300s job/create-order-db -n $K8S_NAMESPACE
                            kubectl --kubeconfig=$KUBECONFIG wait --for=condition=complete --timeout=300s job/create-payment-db -n $K8S_NAMESPACE

                            echo "Triển khai lên Kubernetes cluster hoàn tất!"
                        '''
                    }
                }
            }
        }
    }
    post {
        always {
            sh 'rm -f $KUBECONFIG'
        }
    }
}