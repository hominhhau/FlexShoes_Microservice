pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = 'ctmyname'
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        KUBECONFIG_CREDENTIALS_ID = 'kubeconfig-credentials'
        PATH = "/var/jenkins_home/bin:$PATH"
        MINIKUBE_IP = '192.168.49.2'
        CERTS_DIR = "/var/jenkins_home/minikube-certs"
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

        stage('Configure Kubeconfig') {
            steps {
                withCredentials([file(credentialsId: KUBECONFIG_CREDENTIALS_ID, variable: 'KUBECONFIG_FILE')]) {
                    script {
                        sh '''
                            mkdir -p ${WORKSPACE}/.kube
                            cp ${KUBECONFIG_FILE} ${WORKSPACE}/.kube/config

                            # Mã hóa certificate files thành base64
                            CA_DATA=$(base64 -w 0 ${CERTS_DIR}/ca.crt)
                            CLIENT_CERT_DATA=$(base64 -w 0 ${CERTS_DIR}/profiles/minikube/client.crt)
                            CLIENT_KEY_DATA=$(base64 -w 0 ${CERTS_DIR}/profiles/minikube/client.key)

                            # Cập nhật kubeconfig
                            sed -i "s|certificate-authority:.*|certificate-authority-data: ${CA_DATA}|g" ${WORKSPACE}/.kube/config
                            sed -i "s|client-certificate:.*|client-certificate-data: ${CLIENT_CERT_DATA}|g" ${WORKSPACE}/.kube/config
                            sed -i "s|client-key:.*|client-key-data: ${CLIENT_KEY_DATA}|g" ${WORKSPACE}/.kube/config
                            sed -i "s|server:.*|server: https://${MINIKUBE_IP}:8443|g" ${WORKSPACE}/.kube/config

                            chmod 600 ${WORKSPACE}/.kube/config
                            export KUBECONFIG=${WORKSPACE}/.kube/config

                            # Kiểm tra kubeconfig
                            echo "=== Kubeconfig ==="
                            cat ${WORKSPACE}/.kube/config
                            kubectl config current-context

                            # Kiểm tra file certs
                            echo "=== Certificates ==="
                            ls -la ${CERTS_DIR}/ca.crt
                            ls -la ${CERTS_DIR}/profiles/minikube/client.crt
                            ls -la ${CERTS_DIR}/profiles/minikube/client.key
                        '''
                    }
                }
            }
        }

        stage('Verify Kubernetes Connection') {
            steps {
                script {
                    sh """
                        export KUBECONFIG=${WORKSPACE}/.kube/config

                        # Kiểm tra kết nối mạng
                        echo "Kiểm tra kết nối tới Minikube API..."
                        curl -k --connect-timeout 5 https://${MINIKUBE_IP}:8443 || {
                            echo "WARNING: Không thể kết nối trực tiếp tới Minikube API (có thể do RBAC)"
                        }

                        # Kiểm tra quyền kubectl
                        echo "Kiểm tra quyền kubectl..."
                        kubectl auth can-i create deployment -n flexshoes || {
                            echo "ERROR: Không có quyền tạo deployment trong namespace flexshoes"
                            exit 1
                        }
                        kubectl auth can-i create secret -n flexshoes || {
                            echo "ERROR: Không có quyền tạo secret trong namespace flexshoes"
                            exit 1
                        }
                        kubectl auth can-i create service -n flexshoes || {
                            echo "ERROR: Không có quyền tạo service trong namespace flexshoes"
                            exit 1
                        }

                        # Kiểm tra kết nối kubectl
                        echo "Kiểm tra kết nối kubectl..."
                        kubectl cluster-info || {
                            echo "ERROR: Không thể xác thực với Kubernetes cluster"
                            echo "Thông tin kubeconfig:"
                            kubectl config view
                            echo "Kiểm tra certificates:"
                            ls -la ${CERTS_DIR}/
                            exit 1
                        }

                        echo "Kết nối Kubernetes thành công!"
                        kubectl get nodes
                    """
                }
            }
        }

        stage('Prepare Kubernetes Manifests') {
            steps {
                withCredentials([
                    string(credentialsId: 'openai-api-key', variable: 'OPENAI_API_KEY'),
                    string(credentialsId: 'sendinblue-api-key', variable: 'SENDINBLUE_API_KEY'),
                    string(credentialsId: 'access-key-id', variable: 'ACCESSKEYID'),
                    string(credentialsId: 'secret-access-key', variable: 'SECRETACCESSKEY')
                ]) {
                    script {
                        sh '''
                            # Mã hóa các giá trị bí mật thành base64
                            OPENAI_API_KEY_B64=$(echo -n "${OPENAI_API_KEY}" | base64 -w 0)
                            SENDINBLUE_API_KEY_B64=$(echo -n "${SENDINBLUE_API_KEY}" | base64 -w 0)
                            ACCESSKEYID_B64=$(echo -n "${ACCESSKEYID}" | base64 -w 0)
                            SECRETACCESSKEY_B64=$(echo -n "${SECRETACCESSKEY}" | base64 -w 0)

                            # Kiểm tra giá trị base64
                            echo "OPENAI_API_KEY_B64: ${OPENAI_API_KEY_B64}"
                            echo "SENDINBLUE_API_KEY_B64: ${SENDINBLUE_API_KEY_B64}"
                            echo "ACCESSKEYID_B64: ${ACCESSKEYID_B64}"
                            echo "SECRETACCESSKEY_B64: ${SECRETACCESSKEY_B64}"

                            # Tách secrets và config maps vào file riêng
                            grep -B 1000 -A 1000 -E 'kind: (Secret|ConfigMap)' flexshoes-all.yaml > flexshoes-secrets-configmaps.yaml
                            grep -v -E 'kind: (Secret|ConfigMap)' flexshoes-all.yaml > flexshoes-others.yaml

                            # Cập nhật secrets với các giá trị bí mật
                            sed -i "s#OPENAI_API_KEY: cGxhY2Vob2xkZXI=#OPENAI_API_KEY: ${OPENAI_API_KEY_B64}#g" flexshoes-secrets-configmaps.yaml
                            sed -i "s#SENDINBLUE_API_KEY: cGxhY2Vob2xkZXI=#SENDINBLUE_API_KEY: ${SENDINBLUE_API_KEY_B64}#g" flexshoes-secrets-configmaps.yaml
                            sed -i "s#ACCESSKEYID: cGxhY2Vob2xkZXI=#ACCESSKEYID: ${ACCESSKEYID_B64}#g" flexshoes-secrets-configmaps.yaml
                            sed -i "s#SECRETACCESSKEY: cGxhY2Vob2xkZXI=#SECRETACCESSKEY: ${SECRETACCESSKEY_B64}#g" flexshoes-secrets-configmaps.yaml

                            # Sửa volume mount path
                            sed -i "s#mountPath: /path/to/data#mountPath: /app/data#g" flexshoes-others.yaml

                            # Kiểm tra nội dung file
                            echo "=== Nội dung flexshoes-secrets-configmaps.yaml ==="
                            cat flexshoes-secrets-configmaps.yaml
                            echo "=== Nội dung flexshoes-others.yaml ==="
                            cat flexshoes-others.yaml
                        '''
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    sh """
                        export KUBECONFIG=${WORKSPACE}/.kube/config

                        # Kiểm tra các file trong workspace
                        echo "=== Nội dung workspace ==="
                        ls -la ${WORKSPACE}

                        # Kiểm tra file YAML
                        for file in flexshoes-secrets-configmaps.yaml flexshoes-others.yaml; do
                            if [ ! -f \$file ]; then
                                echo "ERROR: \$file không tồn tại"
                                exit 1
                            fi
                        done

                        # Validate file YAML
                        echo "=== Kiểm tra cú pháp YAML ==="
                        for file in flexshoes-secrets-configmaps.yaml flexshoes-others.yaml; do
                            kubectl apply -f \$file -n flexshoes --dry-run=client || {
                                echo "ERROR: Kiểm tra cú pháp \$file thất bại"
                                exit 1
                            }
                        done

                        # Tạo namespace nếu chưa tồn tại
                        echo "=== Kiểm tra namespace flexshoes ==="
                        if ! kubectl get namespace flexshoes &> /dev/null; then
                            echo "Tạo namespace flexshoes..."
                            kubectl create namespace flexshoes || {
                                echo "ERROR: Tạo namespace flexshoes thất bại"
                                exit 1
                            }
                        else
                            echo "Namespace flexshoes đã tồn tại"
                        fi

                        # Áp dụng secrets và config maps trước
                        echo "=== Áp dụng flexshoes-secrets-configmaps.yaml ==="
                        kubectl apply -f flexshoes-secrets-configmaps.yaml -n flexshoes --v=8 || {
                            echo "ERROR: Áp dụng flexshoes-secrets-configmaps.yaml thất bại"
                            exit 1
                        }

                        # Áp dụng các tài nguyên còn lại
                        echo "=== Áp dụng flexshoes-others.yaml ==="
                        kubectl apply -f flexshoes-others.yaml -n flexshoes --v=8 || {
                            echo "ERROR: Áp dụng flexshoes-others.yaml thất bại"
                            echo "=== Trạng thái namespace sau khi apply ==="
                            kubectl get all -n flexshoes
                            kubectl get secrets -n flexshoes
                            kubectl get configmaps -n flexshoes
                            echo "=== Mô tả pods ==="
                            kubectl describe pods -n flexshoes || true
                            echo "=== Events namespace flexshoes ==="
                            kubectl get events -n flexshoes --sort-by='.metadata.creationTimestamp' || true
                            echo "=== Logs từ các pod lỗi ==="
                            for pod in \$(kubectl get pods -n flexshoes --field-selector=status.phase!=Running -o name 2>/dev/null || echo ""); do
                                echo "Logs từ \$pod:"
                                kubectl logs -n flexshoes \$pod --all-containers=true --tail=100 || true
                                echo ""
                            done
                            exit 1
                        }

                        echo "=== Áp dụng YAML thành công ==="
                        kubectl get all -n flexshoes
                    """
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                script {
                    sh """
                        export KUBECONFIG=${WORKSPACE}/.kube/config
                        echo "Kiểm tra các pod..."
                        kubectl get pods -n flexshoes -o wide

                        echo "Kiểm tra trạng thái deployment..."
                        kubectl rollout status deployment -n flexshoes
                    """
                }
            }
        }
    }
    post {
        always {
            withCredentials([file(credentialsId: KUBECONFIG_CREDENTIALS_ID, variable: 'KUBECONFIG_FILE')]) {
                sh '''
                    export KUBECONFIG=${WORKSPACE}/.kube/config

                    # Kiểm tra workspace
                    echo "=== Nội dung workspace ==="
                    ls -la ${WORKSPACE}

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
                    kubectl get secrets -n flexshoes
                    kubectl get configmaps -n flexshoes
                '''
            }
        }
        failure {
            withCredentials([file(credentialsId: KUBECONFIG_CREDENTIALS_ID, variable: 'KUBECONFIG_FILE')]) {
                echo 'Triển khai Kubernetes thất bại!'
                sh '''
                    export KUBECONFIG=${WORKSPACE}/.kube/config

                    # Kiểm tra file flexshoes-all.yaml
                    echo "=== Kiểm tra flexshoes-all.yaml ==="
                    if [ -f flexshoes-all.yaml ]; then
                        echo "flexshoes-all.yaml found"
                        cat flexshoes-all.yaml
                    else
                        echo "ERROR: flexshoes-all.yaml không tồn tại"
                    fi

                    # Kiểm tra trạng thái namespace
                    echo "===== Trạng thái namespace flexshoes ====="
                    kubectl get all -n flexshoes
                    kubectl get secrets -n flexshoes
                    kubectl get configmaps -n flexshoes

                    # Lấy thông tin chi tiết về lỗi
                    echo "===== Mô tả các pod bị lỗi ====="
                    kubectl describe pods -n flexshoes || true

                    # Sửa lỗi thiếu dấu nháy kép
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