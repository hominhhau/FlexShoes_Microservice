# 🧩 Microservices System with Spring Boot, NodeJS, MongoDB, SQL & Jenkins CI/CD

## 🚀 Giới thiệu

Hệ thống bao gồm nhiều **microservices** được phát triển bằng Spring Boot và NodeJS, sử dụng các công nghệ cơ sở dữ liệu khác nhau (MongoDB, PostgreSQL, MSSQL, MariaDB). Tích hợp với **Eureka Discovery**, **Spring Config Server**, và **API Gateway** để quản lý cấu hình và định tuyến dịch vụ.

Ngoài ra, hệ thống còn tích hợp **Jenkins CI/CD** để tự động hóa việc build, test và deploy.

---

## 📦 Cấu trúc dịch vụ chính

| Dịch vụ               | Cổng       | Công nghệ              |
|-----------------------|------------|------------------------|
| Eureka Server         | `8761`     | Spring Boot            |
| Config Server         | `8088`     | Spring Boot            |
| API Gateway           | `8888`     | Spring Cloud Gateway   |
| User Service          | `8080`     | Spring Boot + MariaDB  |
| Profile Service       | `8081`     | Spring Boot + MariaDB  |
| Notification Service  | `8082`     | Spring Boot + MongoDB  |
| Order Service         | `8083`     | Spring Boot + MSSQL    |
| Payment Service       | `8084`     | Spring Boot + MSSQL    |
| Inventory Service     | `8085`     | NodeJS + MongoDB Atlas |
| Chat Service          | `8089`     | NodeJS + PostgreSQL    |
| Jenkins CI/CD         | `8090`     | Jenkins LTS            |

---

## 🧰 Yêu cầu hệ thống

- Docker & Docker Compose
- Internet để pull image
- Git

---

## 🛠️ Hướng dẫn sử dụng

### 1. Build hệ thống

```bash

docker-compose build

docker-compose down -v

docker-compose up -d

### Jenkins
docker run -d -p 8090:8080 -p 50000:50000 -v /var/run/docker.sock:/var/run/docker.sock -v jenkins_home:/var/jenkins_home --name jenkins jenkins/jenkins:lts

### Jenkins pass
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword

docker exec -it jenkins bash



kubectl apply -f jenkins-rbac.yaml

```

### Docker permission

getent group docker

docker run --rm -it -v /var/run/docker.sock:/var/run/docker.sock alpine

chgrp 103 /var/run/docker.sock
chmod g+rw /var/run/docker.sock

ls -l /var/run/docker.sock

docker exec -it jenkins bash
docker ps

### GKE

### Triển khai

gcloud container clusters create flexshoes-cluster --machine-type e2-micro --num-nodes 1 --region asia-southeast1 --project flexshoes-project --disk-size 50

gcloud container clusters create flexshoes-cluster --machine-type e2-medium --num-nodes 1 --region asia-southeast1 --project flexshoes-project --disk-size 50

gcloud container clusters list --project flexshoes-project

kubectl get nodes

kubectl apply -f flexshoes-all.yaml -n flexshoes

### e2-standard-4 sẽ tiêu tốn ~$73.66/1 tuần

gcloud container node-pools update default-pool --cluster flexshoes-cluster --machine-type e2-standard-4 --region asia-southeast1 --project flexshoes-project --disk-size 50

gcloud container clusters delete flexshoes-cluster --region asia-southeast1 --project flexshoes-project

gcloud container clusters create flexshoes-cluster --machine-type e2-standard-2 --num-nodes 1 --region asia-southeast1 --project flexshoes-project --disk-size 50 --enable-ip-alias 

gcloud container clusters create flexshoes-cluster --machine-type e2-standard-4 --num-nodes 1 --region asia-southeast1 --project flexshoes-project --disk-size 50 --enable-ip-alias

### Tắt cluster
gcloud container clusters update flexshoes-cluster --region asia-southeast1 --project flexshoes-project --enable-autoscaling --min-nodes 0 --max-nodes 3

gcloud container clusters stop flexshoes-cluster --region asia-southeast1 --project flexshoes-project

gcloud container clusters start flexshoes-cluster --region asia-southeast1 --project flexshoes-project

### Xóa các Job hiện có
kubectl delete job create-order-db -n flexshoes

kubectl delete job create-payment-db -n flexshoes


# Pull tất cả:
kubectl rollout restart deployment -n flexshoes

# Eureka Server
kubectl port-forward svc/eureka-server 8761:8761 -n flexshoes 





kubectl logs -n flexshoes api-gateway-7df4dd9764-nz9f9  --tail=100