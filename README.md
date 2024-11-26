# concert-reservation-service

## 가. 개요

### 1. 프로젝트 요약

- 대기실 시스템을 통한 공정한 예매 기회 제공과 안정적인 좌석 선점 처리를 보장하며, 모의 결제 프로세스를 통해 실제 티켓 예매 사이트의 핵심 기능을 구현한 시스템

### 2. 개발 기간

- 1차 기간: 2024-11-14 ~ 2024-11-21

### 3. 기술 스택

- backend: Java 21, Spring Boot 3.3
- db: Redis, MariaDB
- messaging: Kafka
- ORM: JPA, Hibernate
- build: Maven
- test: JUnit 5, Mockito

### 4. ERD

![ERD Diagram](src/main/resources/static/erd.png)

## 나. 기능 설명

### **주요 구현 내용**

#### 1. JWT 인증
- OncePerRequestFilter를 활용해 JWT 인증 시스템 설계
- HandlerMethodArgumentResolver와 결합하여 사용자 정보를 Controller에서 유연하게 활용할 수 있는 구조 구축

#### 2. 대기열 처리
- 대기 상태(pending)는 Kafka로 트래픽 분산 처리
- polling으로 상태 확인 유도

#### 3. 좌석 선점
- Redis를 사용하여 좌석 선점 시 유일성 보장

#### 4. 에러 처리
- 예외는 @ExceptionHandler를 통해 일괄적으로 처리, 가독성 및 유지보수성 강화

### **주요 API 요약**

| Method   | Endpoint                                              | 설명                           |
|----------|-------------------------------------------------------|------------------------------|
| `POST`   | `/reservation/pending`                                | 예약 진입                        |
| `GET`    | `/reservation/pending/request-id/{requestId}/details` | 예약 진입 상태 및 결과 조회             |
| `POST`   | `/reservation/preoccupy`                              | 좌석 선점                        |
| `PATCH`  | `/reservation/progress`                               | 예약 확정                        |
| `GET`    | `/reservation/id/{id}`                                | id로 예약 조회                    |
| `GET`    | `/reservation/my`                                     | 로그인 된 유저의 예약 조회              |
| `GET`    | `/reservation/my/concert-id/{concertId}`              | 로그인 된 유저의 특정 concert 예약 조회   |
| `GET`    | `/api/concert/all`                                    | 모든 공연 조회                     |
| `GET`    | `/api/concert/id/{id}`                                | id로 공연 조회                    |
| `POST`   | `/api/concert`                                        | 공연 생성                        |
| `PUT`    | `/api/concert/id/{id}`                                | 공연 정보 수정                     |
| `DELETE` | `/api/concert/id/{id}`                                | 공연 삭제                        |
| `GET`    | `/api/venue/all`                                      | 모든 공연장 조회                    |
| `GET`    | `/api/venue/venue-name/{name}`                        | 이름으로 공연장 조회                  |
| `GET`    | `/api/venue/id/{id}`                                  | id로 공연장 조회                   |
| `POST`   | `/api/venue`                                          | 공연장 생성                       |
| `PUT`    | `/api/venue/id/{id}`                                  | 공연장 정보 수정                    |
| `DELETE` | `/api/venue/id/{id}`                                  | 공연장 삭제                       |

---

### **주요 기능의 flow 예시**

#### 1. 예매 신청 진입 요청

```bash
curl -X POST http://localhost:8080/api/reservation/pending \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <jwt-token>" \
-d '{
    "concertId": 1
}'
```

```bash
Responese: {
    "requestId": "RES321-1732101253406",
    "message": "Reservation request added to the queue."
}
```

#### 2. 예매 신청 진입 상태값 반환 요청

```bash
curl -X GET http://localhost:8080/reservation/pending/request-id/RES567-20241127013759-832/details \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <jwt-token>"
```

```bash
Responese: {
    "requestId":"RES567-20241127013759-832",
    "status": "PROCESSING",
    "reservationId": "86"
}
```

#### 3. 좌석 사전 점유 요청

```bash
curl -X POST http://localhost:8080/api/reservation/preoccupy \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <jwt-token>" \
-d '{
    "concertId": 1,
    "requestId":"RES567-20241127013759-832",
    "seatNumbers": ["A1", "A2"]
}'
```

#### 4. 예약 확정

```bash
curl -X PATCH http://localhost:8080/api/reservation/progress \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <jwt-token>" \
-d '{
    "reservationId":86,
    "concertId":1,
    "seatNumbers":["A1", "A2"]
}'
```

```bash
Responese: {
    "seatNumbers":["A1", "A2"],
    "status": "CONFIRMED",
    "message": "Reservation process has been completed."
}
```
