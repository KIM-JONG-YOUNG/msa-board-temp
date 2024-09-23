# msa-board
> 멀티 모듈 구조의 게시판 구현 

## 개발 환경 
 - JDK 1.8
 - Spring Boot 2.7.18
 - Spring Cloud 2021.0.9
 - Spring Doc OpenAPI UI 1.7.0
 - Json Web Token 0.9.1
 - Docker 24.0.6
 - Docker Compose 2.21.0
 
## 프로젝트 구조 
```
./msa-board
├── msa-board-clients
│   ├── msa-board-client-core    : WEB/Feign Client/Security 설정     
│   ├── msa-board-client-member  : 회원 마이크로서비스 요청/응답 DTO 및 Feign Client 
│   ├── msa-board-client-post    : 게시글 마이크로서비스 요청/응답 DTO 및 Feign Client
│   └── msa-board-client-search  : 검색 마이크로서비스 요청/응답 DTO 및 Feign Client
├── msa-board-clouds
│   ├── msa-board-cloud-config   : Config 어플리케이션 
│   └── msa-board-cloud-eureka   : Eureka 어플리케이션 
├── msa-board-common             : 공통 상수/Enum 및 유틸리티 
├── msa-board-configs            : 어플리케이션 설정 파일 
├── msa-board-containers         
│   ├── msa-board-container-cloud          : Config/Eureka 어플리케이션 Docker Compose
│   ├── msa-board-container-endpoint       : 관리자/사용자 엔드포인트 Docker Compose
│   ├── msa-board-container-kafka          : Kafka Cluster Docker Compose
│   ├── msa-board-container-mariadb        : 마이크로서비스 MariaDB
│   ├── msa-board-container-microservice   : 회원/게시글/검색 마이크로서비스 Docker Compose
│   └── msa-board-container-redis          : Redis Cluster Docker Compose
├── msa-board-cores
│   ├── msa-board-core-kafka    : Kafka 설정 및 오류 처리 
│   └── msa-board-core-redis    : Redis 설정 및 어노테이션 
├── msa-board-domains
│   ├── msa-board-domain-core    : DB 연결 설정 및 Query Dsl 유틸리티, 추상 Entity 및 Repository
│   ├── msa-board-domain-member  : 회원 도메인 Entity 및 Repository, 테이블 생성 SQL
│   └── msa-board-domain-post    : 게시글 도메인 Entity 및 Repository, 테이블 생성 SQL
├── msa-board-endpoints
│   ├── msa-board-endpoint-admin  : 관리자 엔드포인트 어플리케이션 
│   └── msa-board-endpoint-user   : 사용자 엔드포인트 어플리케이션 
├── msa-board-microservices
│   ├── msa-board-microservice-member  : 회원 마이크로서비스 어플리케이션 
│   ├── msa-board-microservice-post    : 게시글 마이크로서비스 어플리케이션 
│   └── msa-board-microservice-search  : 검색 마이크로서비스 어플리케이션 
└── target
    ├── excutable-jar  : Spring Boot Excutable Jar 
    └── maven-jar      : Maven Jar
```

## 프로젝트 의존성 
```
msa-board-common

msa-board-core-redis
└── msa-board-common:compile

msa-board-core-kafka
└── msa-board-common:compile

msa-board-client-core
├── msa-board-core-redis:provided
└── msa-board-common:compile

msa-board-client-member
├── msa-board-client-core:compile
└── msa-board-common:compile

msa-board-client-post
├── msa-board-client-core:compile
└── msa-board-common:compile

msa-board-client-search
├── msa-board-client-core:compile
└── msa-board-common:compile

msa-board-domains:pom
└── msa-board-common:compile

msa-board-domain-core
├── msa-board-core-redis:provided
└── msa-board-common:compile

msa-board-domain-member
├── msa-board-domain-core:compile
└── msa-board-common:compile

msa-board-domain-post
├── msa-board-domain-core:compile
└── msa-board-common:compile

msa-board-cloud-config

msa-board-cloud-eureka

msa-board-microservice-member
├── msa-board-domain-member:compile
|   ├── msa-board-domain-core:compile
|   └── msa-board-common:compile
├── msa-board-client-member:compile
|   └── msa-board-client-core:compile
├── msa-board-core-redis:compile
└── msa-board-core-kafka:compile

msa-board-microservice-post
├── msa-board-domain-post:compile
|   ├── msa-board-domain-core:compile
|   └── msa-board-common:compile
├── msa-board-client-post:compile
|   └── msa-board-client-core:compile
├── msa-board-client-member:compile
├── msa-board-core-redis:compile
└── msa-board-core-kafka:compile

msa-board-microservice-search
├── msa-board-domain-member:compile
|   ├── msa-board-domain-core:compile
|   └── msa-board-common:compile
├── msa-board-domain-post:compile
├── msa-board-client-search:compile
|   └── msa-board-client-core:compile
├── msa-board-core-redis:compile
└── msa-board-core-kafka:compile

msa-board-endpoint-admin
├── msa-board-core-redis:compile
|   └── msa-board-common:compile
├── msa-board-client-member:compile
|   └── msa-board-client-core:compile
├── msa-board-client-post:compile
└── msa-board-client-search:compile

msa-board-endpoint-user
├── msa-board-core-redis:compile
|   └── msa-board-common:compile
├── msa-board-client-member:compile
|   └── msa-board-client-core:compile
├── msa-board-client-post:compile
└── msa-board-client-search:compile
```

## Docker Compose 실행 명령어
> Docker Compose 별 network를 생성하며 하위의 Docker Compose는 상위의 Docker Compose의 network를 사용하므로 순서대로 실행 
> Application Docker Compose를 실행 시 각 어플리케이션 프로젝트 내의 Dockerfile이 필요하며 target 폴더 상의 빌드 jar 필요  
 - Maria DB Docker Compose
```
cd ./msa-board-containers/msa-board-container-mariadb
docker-compose up -d 
```
 - Redis Cluster Docker Compose
```
cd ./msa-board-containers/msa-board-container-redis
docker-compose up -d 
```
 - Kafka Cluster Docker Compose
```
cd ./msa-board-containers/msa-board-container-kafka
docker-compose up -d 
```
 - Config/Eureka Application Docker Compose
```
cd ./msa-board-containers/msa-board-container-cloud
docker-compose up -d 
```
 - Microservice Application Docker Compose
```
cd ./msa-board-containers/msa-board-container-microservice
docker-compose up -d --build
```
 - Endpoint Application Docker Compose
```
cd ./msa-board-containers/msa-board-container-endpoint
docker-compose up -d --build
```

## 참고사항
 - msa-board-client-core의 FeignClientCondition <br>
   => spring.application.name 과 @FeignClient 의 이름이 다른 경우에만 Feign Client Bean 생성 
 - msa-board-client-core 모듈은 Spring Security 및 JWT, msa-board-core-redis 의존성을 provided로 제공 <br>
   => Spring Security의 클래스가 런타임에 존재할 경우 토큰 기반 인증 필터 및 설정 활성화 
 - msa-board-domain-core 모듈은 msa-board-core-redis 의존성을 provided로 제공 <br>
   => msa-board-core-redis의 클래스가 런타임에 존재할 경우 분산 트랜잭션 사용 가능  

