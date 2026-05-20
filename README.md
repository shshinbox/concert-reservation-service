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

---

### 4. 아키텍처 (현재 프로젝트 기반)

```mermaid
graph LR
    User["사용자 (Client)"]

    subgraph AppLayer ["Application Layer"]
        direction TB
        Producer["Queue Producer"]
        Svc["Ticketing Service"]
        Consumer["Queue Consumer"]
    end

    subgraph KafkaQueue ["Waiting: Kafka"]
        K["Kafka Partitions (P1, P2, P3)"]
        K --- Note1(대기열 & 순번 소스)
    end

    subgraph RedisStore ["Status & Lock: Redis"]
        direction TB
        Status[("입장 가능 상태 (Active)")]
        Lock[("좌석 분산 락 (Lock)")]
    end

    %% 프로세스 순서
    User -->|"1. 입장 요청"| Producer
    Producer -->|"2. 대기열 삽입 (메시지 발행)"| K
    
    %% 폴링 루프: Kafka에서 순번 정보 참조
    User -.->|"3. 순번 조회 폴링"| Svc
    Svc -.->|"4. 오프셋/로그 기반 순번 추출"| K

    %% 컨슈머 처리 및 입장 허가
    K -->|"5. 순차적 소비"| Consumer
    Consumer -->|"6. 입장권 발급 (상태 업데이트)"| Status

    %% 좌석 선점 및 저장
    User -->|"7. 좌석 선택"| Svc
    Svc -->|"8. 입장권 검증"| Status
    Svc -->|"9. 즉시 선점 (Atomic)"| Lock
    Svc -->|"10. 최종 저장"| DB[(MariaDB)]

    style KafkaQueue fill:#fff5f5,stroke:#cc0000
    style RedisStore fill:#fff9c4,stroke:#fbc02d
```


### 5.1. 아키텍처 고도화 안내
본 모놀리식 구조에서 발생한 Kafka 대기열의 추측성 순번 및 오버헤드 한계를 인지하고, 
이를 해결하기 위해 **Kotlin + WebFlux + Redis ZSet** 기반의 고성능 분산 대기열 서비스로 분리 및 고도화를 완료했습니다.

👉 [Redis 기반 분산 대기열 시스템 저장소 바로가기](https://github.com/shshinbox/waiting-queue-service)


### 5.2. 고도화 아키텍처

```mermaid
graph LR
    %% 사용자 영역
    User["사용자 (Client)"]

    subgraph WaitingZone ["1. 대기 및 폴링 구역"]
        direction TB
        ZSET[("Redis ZSET<br/>(대기 순번 정렬)")]
        PollSvc["Polling API"]
    end

    subgraph ActiveZone ["2. 활성 및 비즈니스 구역"]
        direction TB
        Svc["Main Service"]
        Active[("Redis: 입장권 토큰")]
        Lock[("Redis: 좌석 락 (Lock)")]
    end

    subgraph StorageZone ["3. 영속화 구역"]
        direction TB
        Kafka{{"Kafka (Event Bus)"}}
        DB[(MariaDB)]
    end

    %% 프로세스 순서
    User -->|"1. 입장 요청 (ZADD)"| ZSET
    
    %% 폴링 루프
    User <-->|"2. 순례 확인 (Polling / ZRANK)"| PollSvc
    PollSvc <--> ZSET

    %% 입장 전환
    ZSET -- "3. 순차적 활성화 (Worker)" --> Active

    %% 메인 로직
    User -->|"4. 입장 토큰 검증"| Svc
    Svc <--> Active
    
    Svc -->|"5. 좌석 선택하기 (Distributed Lock)"| Lock
    Svc -->|"6. 예약 확정 요청"| Kafka
    
    Kafka -->|"7. 비동기 DB 반영"| DB

    %% 스타일링
    style WaitingZone fill:#f0f4ff,stroke:#0052cc
    style ActiveZone fill:#fff9e6,stroke:#ffcc00
    style Lock fill:#ffeb3b,stroke:#fbc02d
    style Kafka fill:#faf5ff,stroke:#7b1fa2
```

### 5.3. 아키텍처 개선 요약

| 구분 | 현재 | TODO |
| :--- | :--- | :--- |
| **대기 순번** | 오프셋 기반 **추측** | `ZRANK` 기반 **정밀 순번** |
| **중복 클릭** | 서버 로직에서 별도 처리 필요 | 자료구조 자체에서 **자동 중복 제거** |
| **조회 성능** | 폴링 시 연산 복잡도 높음 | 메모리 기반 **실시간 조회 최적화** |
| **DB 저장** | 로직 완료 후 즉시 DB 저장 | **Kafka 버퍼링** 후 비동기 저장 |

#### 변경 사유
1. **정확한 순번**: 분산된 오프셋을 계산하던 방식에서 정확한 순번 제공
2. **데이터 정합성**: Redis ZSET을 활용해 '광클'로 인한 중복 진입을 인프라 단에서 차단
3. **시스템 안정성**: Kafka를 DB 앞단의 버퍼(Event Bus)로 재배치하여, 트래픽 폭주 시에도 DB 장애를 방지하고 서비스 연속성 유지

---

### 6. ERD

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
