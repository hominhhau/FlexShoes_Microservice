pipeline {
         agent any
         environment {
             DOCKER_REGISTRY = 'ctmyname'
             DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'
             KUBECONFIG_CREDENTIALS_ID = 'kubeconfig-credentials'
         }
         stages {
             stage('Setup Tools') {
                 steps {
                     sh '''
                         apt-get update
                         apt-get install -y yamllint
                         yamllint --version
                     '''
                 }
             }
             stage('Validate Kubeconfig') {
                 steps {
                     withCredentials([file(credentialsId: env.KUBECONFIG_CREDENTIALS_ID, variable: 'KUBECONFIG_FILE')]) {
                         sh '''
                             echo "Validating kubeconfig content"
                             cat $KUBECONFIG_FILE
                             grep -E "apiVersion:|clusters:|contexts:|users:" $KUBECONFIG_FILE
                             grep -A 3 "cluster:" $KUBECONFIG_FILE
                             yamllint $KUBECONFIG_FILE || {
                                 echo "Invalid YAML in kubeconfig file"
                                 exit 1
                             }
                         '''
                     }
                 }
             }
             stage('Verify Kubernetes Connection') {
                 steps {
                     withCredentials([file(credentialsId: env.KUBECONFIG_CREDENTIALS_ID, variable: 'KUBECONFIG_FILE')]) {
                         script {
                             String kubeconfigContent = readFile(KUBECONFIG_FILE)
                             writeFile file: 'kubeconfig-temp', text: kubeconfigContent
                             sh '''
                                 echo "Checking network connectivity to Minikube"
                                 curl -k --connect-timeout 5 https://192.168.49.2:8443 || {
                                     echo "Cannot connect to Minikube at 192.168.49.2:8443"
                                     exit 1
                                 }
                                 echo "Modifying kubeconfig server URL"
                                 sed 's|server: https://[^ ]*|server: https://192.168.49.2:8443|g' kubeconfig-temp > kubeconfig-modified
                                 echo "Validating kubeconfig YAML"
                                 yamllint kubeconfig-modified || {
                                     echo "Invalid YAML in kubeconfig-modified"
                                     cat kubeconfig-modified
                                     exit 1
                                 }
                                 echo "Kubeconfig content:"
                                 cat kubeconfig-modified
                                 export KUBECONFIG=$(pwd)/kubeconfig-modified
                                 kubectl cluster-info || {
                                     echo "ERROR: Không thể kết nối tới Kubernetes cluster"
                                     cat $(pwd)/kubeconfig-modified
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
                     withCredentials([file(credentialsId: env.KUBECONFIG_CREDENTIALS_ID, variable: 'KUBECONFIG_FILE')]) {
                         sh '''
                             export KUBECONFIG=$(pwd)/kubeconfig-modified
                             kubectl apply -f flexshoes-all.yaml -n flexshoes
                         '''
                     }
                 }
             }
             stage('Verify Deployment') {
                 steps {
                     withCredentials([file(credentialsId: env.KUBECONFIG_CREDENTIALS_ID, variable: 'KUBECONFIG_FILE')]) {
                         sh '''
                             export KUBECONFIG=$(pwd)/kubeconfig-modified
                             kubectl get pods -n flexshoes -o name
                             kubectl rollout status deployment -n flexshoes
                         '''
                     }
                 }
             }
         }
         post {
             failure {
                 sh '''
                     export KUBECONFIG=$(pwd)/kubeconfig-modified
                     echo "Triển khai Kubernetes thất bại!"
                     kubectl describe pods -n flexshoes || true
                 '''
             }
         }
     }