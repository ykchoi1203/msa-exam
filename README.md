# MSA EXAMPLE
- 목표 : MSA 구성, Eureka, Ribbon 을 이용해서 분산처리 시스템을 구성, Redis 캐싱

## 기술 스택
- Spring Boot
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Token)
- Spring Cloud Config
- Spring Actuator
- Spring Data Redis
- Micrometer
- OpenFeign
- Resilience4j
- QueryDSL


| **서비스 이름**   | **패키지 이름**                  | **포트 번호**    |
|-------------------|--------------------------------|-----------------|
| **유레카 서버**    | `com.sparta.msa_exam`           | `19090`         |
| **게이트웨이**     | `com.sparta.msa_exam.gateway`   | `19091`         |
| **상품 서비스**    | `com.sparta.msa_exam.product`   | `19093`, `19094` |
| **주문 서비스**    | `com.sparta.msa_exam.order`     | `19092`         |
| **인증 서비스**    | `com.sparta.msa_exam.auth`      | `19095`         |

