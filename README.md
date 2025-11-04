<div align="center">
    <a href="https://git.io/typing-svg"><img src="https://readme-typing-svg.demolab.com?font=Black+Han+Sans&size=40&duration=2000&pause=1000&color=F79871&center=true&vCenter=true&width=435&lines=%EC%84%A0%EC%B0%A9%EC%88%9C+%EC%BF%A0%ED%8F%B0+%EB%B0%9C%EA%B8%89+%EC%8B%9C%EC%8A%A4%ED%85%9C" alt="Typing SVG" /></a>
</div>

## 0. 프로젝트 개요
**멀티스레드 환경에서 발생하는 Race Condition, 데이터 무결성 등의 동시성 이슈**를 다양한 방법으로 해결하고, 각 방법의 성능과 특징을 비교 분석합니다.

## 1. 기술 스택
- **Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Database**: MySQL 8.0
- **Infra**: Redis 7.x, Kafka
- **Test**: Mokito, JMeter

## 2. 동시성 제어 방법
- V1: 동시성 제어를 하지 않은 버전
- V2: `synchronized`를 활용한 동시성 제어
- V3: ReentrantLock을 활용한 동시성 제어
- V4: 낙관적 락을 활용한 동시성 제어
- V5: 비관적 락을 활용한 동시성 제어
- V6: Redis 분산 락을 활용한 동시성 제어
- V7: Redis Sorted Set(w. Lua Script)을 활용한 동시성 제어
- V8: 메시지 큐(Kafka)를 활용한 동시성 제어

## 3. 테스트 시나리오
**Thread Properties**
- Number of Threads(users): 1000
- Ramp-up period(seconds): 60
- Loop Count: 3

1분 안에 1000명의 사용자가 동시에 요청을 보내는 상황

## 4. 성능 비교 결과
로컬 환경을 고려하여 인위적 지연(50ms)을 발생시킨 후 테스트 하였음

| 동시성 제어 방법                       | TPS  | 응답시간(Avg) | 응답시간(Max) | 에러율    |
|---------------------------------|------|-----------|-----------|--------|
| synchronized                    | 15.7 | 34,982ms  | 67,155ms  | 0.00%  |
| ReentrantLock                   | 47.4 | 0,806ms   | 2,896ms   | 0.00%  |
| Optimistic Lock                 | 49.5 | 0,216ms   | 0,509ms   | 24.73% |
| Pessimistic Lock                | 46.0 | 1,505ms   | 4,744ms   | 0.00%  |
| Redis Distributed Lock          | 45.5 | 1,561ms   | 10,047ms  | 1.70%  |
| Redis Sorted Set(w. Lua Script) | 50.0 | 0,007ms   | 0,143ms   | 0.00%  |
| Message Queue(Kafka)            | 50.0 | 0,002ms   | 0,141ms   | 0.00%  |

## 5. 결론
**synchronized**
- 가장 낮은 성능을 보임 (TPS가 다른 방법의 1/3 수준)
- 평균 응답시간과 최대 응답시간 모두 사용 불가능한 수준
- 분산 환경에서 사용 불가능

**ReentrantLock**
- synchronized 대비 향상된 TPS를 보임
  - synchronized처럼 모든 쿠폰 발급에 대해 하나의 락을 적용한 것이 아니라, 쿠폰 별로 락을 걸도록 하여 성능이 향상된 것으로 보임
- 평균 응답시간과 최대 응답시간 모두 준수한 수준
- 분산 환경에서 사용 불가능

**Optimistic Lock**
- 빠른 응답시간과 높은 TPS로 처리량은 우수
- 단 24.73%의 높은 에러율을 보임
  - 충돌이 많이 발생하는 환경에서는 적합하지 않음

**Pessimistic Lock**
- 안정적인 TPS
- 에러율은 0%로, 충돌 가능성이 높은 상황에서 안정적인 성능을 보임
- 단, 락이 필요없는 경우에도 적용되어 성능에 영향을 줄 수 있음
- 평균 응답시간은 준수, 최대 응답시간은 가끔 지연이 발생하는 수준

**Redis Distributed Lock**
- 안정적인 TPS
- 평균 응답시간은 준수, 최대 응답시간은 간헐적으로 지연이 발생하는 수준
- 다중 서버 환경에서 동시성 제어 가능
- 락 획득 실패로 인한 소폭 에러율 존재

**Redis Sorted Set**
- 최고 수준의 TPS
- 압도적으로 빠른 응답시간
- Lua Script의 원자성으로 데이터 정합성 보장
- 자료구조 특성상 순위/랭킹 시스템, 선착순 이벤트 등에 최적

**Message Queue**
- 최고 수준의 TPS
- 비동기 처리 방식으로 즉각적인 응답을 보낼 수 있어 매우 빠른 응답시간을 보임
- 메시지 순서 보장 및 재처리 가능
