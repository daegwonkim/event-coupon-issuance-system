DELETE FROM coupon_issuances;
DELETE FROM coupons;
DELETE FROM events;
DELETE FROM users;

-- Event 및 Coupon 데이터
INSERT INTO events (id, name, created_at, modified_at)
VALUES (1, '테스트이벤트', NOW(), NOW());

INSERT INTO coupons (id, event_id, stock, created_at, modified_at)
VALUES (1, 1, 10, NOW(), NOW());

-- 재귀 CTE로 User 100명 생성
INSERT INTO users (username, created_at, modified_at)
WITH RECURSIVE numbers AS (
    SELECT 0 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 99
)
SELECT CONCAT('테스트사용자', n), NOW(), NOW()
FROM numbers;