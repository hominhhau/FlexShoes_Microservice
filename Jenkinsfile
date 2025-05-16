pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = 'ctmyname'
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        KUBE_CONFIG = credentials('kubeconfig-credentials')
        PATH = "/var/jenkins_home/bin:$PATH"
    }
    stages {
        stage('Setup Tools') {
            steps {
                script {
                    sh '''
                        mkdir -p /var/jenkins_home/bin
                        if ! command -v docker-compose &> /dev/null; then
                            curl -L "https://github.com/docker/compose/releases/download/v2.24.6/docker-compose-$(uname -s)-$(uname -m)" -o /var/jenkins_home/bin/docker-compose
                            chmod +x /var/jenkins_home/bin/docker-compose
                        fi
                        docker-compose --version || { echo "Cài đặt Docker Compose thất bại"; exit 1; }
                        if ! command -v kubectl &> /dev/null; then
                            curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
                            chmod +x kubectl
                            mv kubectl /var/jenkins_home/bin/
                        fi
                        kubectl version --client || { echo "Cài đặt kubectl thất bại"; exit 1; }
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
        stage('Validate Kubeconfig') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig-credentials', variable: 'KUBECONFIG_FILE')]) {
                    sh '''
                        grep -E "apiVersion:|clusters:|contexts:|users:" ${KUBECONFIG_FILE} || {
                            echo "File kubeconfig không hợp lệ"
                            exit 1
                        }
                        grep -A 3 "cluster:" ${KUBECONFIG_FILE}
                    '''
                }
            }
        }
        stage('Verify Kubernetes Connection') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig-credentials', variable: 'KUBECONFIG_FILE')]) {
                    script {
                        String kubeconfigContent = readFile(KUBECONFIG_FILE)
                        // Ưu tiên kubeconfig nhúng dữ liệu
                        if (kubeconfigContent.contains("certificate-authority-data") && kubeconfigContent.contains("client-certificate-data") && kubeconfigContent.contains("client-key-data")) {
                            echo "Using embedded kubeconfig data"
                            writeFile file: 'kubeconfig-modified', text: kubeconfigContent
                        } else {
                            echo "Using file-based kubeconfig, replacing Windows paths"
                            kubeconfigContent = kubeconfigContent.replaceAll('C:\\\\Users\\\\[^\\\\]+\\\\.minikube', '/var/jenkins_home/minikube-certs').replaceAll('\\\\', '/')
                            writeFile file: 'kubeconfig-modified', text: kubeconfigContent
                        }
                        sh '''
                            mkdir -p /var/jenkins_home/minikube-certs/profiles/minikube
                            if [ -f /var/jenkins_home/minikube-certs/profiles/minikube/client.crt ] && [ -f /var/jenkins_home/minikube-certs/profiles/minikube/client.key ] && [ -f /var/jenkins_home/minikube-certs/ca.crt ]; then
                                echo "Certificates found at expected paths:"
                                ls -l /var/jenkins_home/minikube-certs /var/jenkins_home/minikube-certs/profiles/minikube
                            else
                                echo "Warning: Minikube certificates not found, relying on embedded kubeconfig data"
                            fi
                            cat kubeconfig-modified
                            export KUBECONFIG=$(pwd)/kubeconfig-modified
                            kubectl cluster-info || {
                                echo "ERROR: Không thể kết nối tới Kubernetes cluster"
                                cat kubeconfig-modified
                                exit 1
                            }
                            kubectl get nodes
                        '''
                    }
                }
            }
        }
        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig-credentials', variable: 'KUBECONFIG_FILE')]) {
                    script {
                        String kubeconfigContent = readFile(KUBECONFIG_FILE)
                        if (kubeconfigContent.contains("certificate-authority-data") && kubeconfigContent.contains("client-certificate-data") && kubeconfigContent.contains("client-key-data")) {
                            echo "Using embedded kubeconfig data"
                            writeFile file: 'kubeconfig-modified', text: kubeconfigContent
                        } else {
                            echo "Using file-based kubeconfig, replacing Windows paths"
                            kubeconfigContent = kubeconfigContent.replaceAll('C:\\\\Users\\\\[^\\\\]+\\\\.minikube', '/var/jenkins_home/minikube-certs').replaceAll('\\\\', '/')
                            writeFile file: 'kubeconfig-modified', text: kubeconfigContent
                        }
                        sh '''
                            mkdir -p /var/jenkins_home/minikube-certs/profiles/minikube
                            if [ -f /var/jenkins_home/minikube-certs/profiles/minikube/client.crt ] && [ -f /var/jenkins_home/minikube-certs/profiles/minikube/client.key ] && [ -f /var/jenkins_home/minikube-certs/ca.crt ]; then
                                echo "Certificates found at expected paths:"
                                ls -l /var/jenkins_home/minikube-certs /var/jenkins_home/minikube-certs/profiles/minikube
                            else
                                echo "Warning: Minikube certificates not found, relying on embedded kubeconfig data"
                            fi
                            export KUBECONFIG=$(pwd)/kubeconfig-modified
                            kubectl cluster-info
                            kubectl create namespace flexshoes || true
                            kubectl apply -f k8s/jenkins-rbac.yaml -n flexshoes
                            kubectl apply -f k8s/flexshoes-all.yaml -n flexshoes
                        '''
                    }
                }
            }
        }
        stage('Verify Deployment') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig-credentials', variable: 'KUBECONFIG_FILE')]) {
                    sh '''
                        export KUBECONFIG=$(pwd)/kubeconfig-modified
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
                export KUBECONFIG=$(pwd)/kubeconfig-modified
                for pod in $(kubectl get pods -n flexshoes -o name 2>/dev/null || echo ""); do
                    kubectl logs -n flexshoes $pod --tail=100 || true
                done
            '''
        }
        failure {
            echo 'Triển khai Kubernetes thất bại!'
            sh '''
                export KUBECONFIG=$(pwd)/kubeconfig-modified
                kubectl describe pods -n flexshoes || true
            '''
        }
    }
}