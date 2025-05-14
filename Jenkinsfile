pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = 'ctmyname'
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }
    stages {
        stage('Setup Tools') {
            steps {
                script {
                    // Cài đặt Docker Compose mà không dùng sudo
                    bat '''
                        where docker-compose >nul 2>&1
                        if %ERRORLEVEL% neq 0 (
                            curl -L "https://github.com/docker/compose/releases/download/v2.24.6/docker-compose-Windows-x86_64.exe" -o %USERPROFILE%\\docker-compose.exe
                            move %USERPROFILE%\\docker-compose.exe C:\\ProgramData\\Docker\\docker-compose.exe
                        )
                        docker-compose --version || echo "Docker Compose installation may have failed, proceeding anyway"
                    '''
                    // Kiểm tra và cài đặt cho Linux container (nếu chạy trong WSL/Linux)
                    sh '''
                        if ! command -v docker-compose &> /dev/null; then
                            curl -L "https://github.com/docker/compose/releases/download/v2.24.6/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
                            chmod +x /usr/local/bin/docker-compose
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
                    // Sao chép file .env.example thành .env bằng PowerShell
                    bat '''
                        copy .env.example .env || exit 0
                        copy chat-service\\.env.example chat-service\\.env || exit 0
                        copy inventory-service\\.env.example inventory-service\\.env || exit 0
                    '''
                    withCredentials([
                        // Biến cho .env (notification-service)
                        string(credentialsId: 'db-user', variable: 'DB_USER'),
                        string(credentialsId: 'db-password', variable: 'DB_PASSWORD'),
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
                        // Thay thế giá trị trong file .env bằng PowerShell
                        bat '''
                            (Get-Content .env) -replace 'SENDINBLUE_API_KEY=placeholder', 'SENDINBLUE_API_KEY=%SENDINBLUE_API_KEY%' | Set-Content .env
                            (Get-Content .env) -replace 'DB_USER=placeholder', 'DB_USER=%DB_USER%' | Set-Content .env
                            (Get-Content .env) -replace 'DB_PASSWORD=placeholder', 'DB_PASSWORD=%DB_PASSWORD%' | Set-Content .env

                            (Get-Content chat-service\\.env) -replace 'PORT=placeholder', 'PORT=%CHAT_PORT%' | Set-Content chat-service\\.env
                            (Get-Content chat-service\\.env) -replace 'REACT_URL=placeholder', 'REACT_URL=%REACT_URL%' | Set-Content chat-service\\.env
                            (Get-Content chat-service\\.env) -replace 'DB_SSL=placeholder', 'DB_SSL=%DB_SSL%' | Set-Content chat-service\\.env
                            (Get-Content chat-service\\.env) -replace 'DB_USERNAME=placeholder', 'DB_USERNAME=%DB_USERNAME%' | Set-Content chat-service\\.env
                            (Get-Content chat-service\\.env) -replace 'DB_PASSWORD=placeholder', 'DB_PASSWORD=%DB_PASSWORD%' | Set-Content chat-service\\.env
                            (Get-Content chat-service\\.env) -replace 'DB_DATABASE_NAME=placeholder', 'DB_DATABASE_NAME=%DB_DATABASE_NAME%' | Set-Content chat-service\\.env
                            (Get-Content chat-service\\.env) -replace 'DB_HOST=placeholder', 'DB_HOST=%DB_HOST%' | Set-Content chat-service\\.env
                            (Get-Content chat-service\\.env) -replace 'DB_PORT=placeholder', 'DB_PORT=%DB_PORT%' | Set-Content chat-service\\.env
                            (Get-Content chat-service\\.env) -replace 'DB_DIALECT=placeholder', 'DB_DIALECT=%DB_DIALECT%' | Set-Content chat-service\\.env
                            (Get-Content chat-service\\.env) -replace 'OPENAI_API_KEY=placeholder', 'OPENAI_API_KEY=%OPENAI_API_KEY%' | Set-Content chat-service\\.env

                            (Get-Content inventory-service\\.env) -replace 'PORT=8085', 'PORT=8085' | Set-Content inventory-service\\.env
                            (Get-Content inventory-service\\.env) -replace 'MONGO_URI=placeholder', 'MONGO_URI=%MONGO_URI%' | Set-Content inventory-service\\.env
                            (Get-Content inventory-service\\.env) -replace 'ACCESSKEYID=placeholder', 'ACCESSKEYID=%ACCESSKEYID%' | Set-Content inventory-service\\.env
                            (Get-Content inventory-service\\.env) -replace 'SECRETACCESSKEY=placeholder', 'SECRETACCESSKEY=%SECRETACCESSKEY%' | Set-Content inventory-service\\.env
                            (Get-Content inventory-service\\.env) -replace 'REGION=placeholder', 'REGION=%REGION%' | Set-Content inventory-service\\.env
                            (Get-Content inventory-service\\.env) -replace 'BUCKET_NAME=placeholder', 'BUCKET_NAME=%BUCKET_NAME%' | Set-Content inventory-service\\.env
                            (Get-Content inventory-service\\.env) -replace 'OPENAI_API_KEY=placeholder', 'OPENAI_API_KEY=%OPENAI_API_KEY%' | Set-Content inventory-service\\.env
                        '''
                    }
                }
            }
        }
        stage('Build Docker Images') {
            steps {
                script {
                    bat 'docker-compose build'  // Sử dụng bat cho Windows
                    // Fallback cho Linux container
                    sh 'docker-compose build || exit 0'
                }
            }
        }
        stage('Push Docker Images') {
            steps {
                script {
                    bat 'docker-compose push'  // Sử dụng bat cho Windows
                    // Fallback cho Linux container
                    sh 'docker-compose push || exit 0'
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    bat 'docker-compose down'  // Sử dụng bat cho Windows
                    bat 'docker-compose up -d' // Sử dụng bat cho Windows
                    // Fallback cho Linux container
                    sh 'docker-compose down || exit 0'
                    sh 'docker-compose up -d || exit 0'
                }
            }
        }
    }
    post {
        always {
            bat 'docker-compose logs || exit 0'  // Sử dụng bat cho Windows
            sh 'docker-compose logs || exit 0'   // Fallback cho Linux container
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}