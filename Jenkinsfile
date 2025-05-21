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
                    withKubeConfig([credentialsId: 'gke-credentials', clusterName: 'flexshoes-cluster', namespace: 'flexshoes']) {
                        sh '''
                            kubectl apply -f flexshoes-all.yaml
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
        always {
            echo 'Cleaning up workspace'
            cleanWs()
        }
}