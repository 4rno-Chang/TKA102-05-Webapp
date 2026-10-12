-- =========================================================
-- 入座管理 demo 資料（05 專用；只有 INSERT，不建表、不刪表）
--
-- 使用方式：先執行 G1 的 createTable_v1_4.sql，再執行這份。
--   編號（mem_no、rsv_dt_no、orders_no）直接寫死，接在建表 SQL 的原始資料後面：
--   原始資料是 member 1~3、reservation_datetime 1~4、orders 1~3。
--   要重跑的話，請先重跑建表 SQL（直接重跑這份會重複）。
--
-- 時間用「執行這份 SQL 的當下」（NOW()）推算，所以請在 demo 前才執行：
--   · 訂位時段固定是 11:00、12:30、17:00、18:30（和 CJ 的 ReservationService 一樣）：
--       「下一個時段」放還沒到的訂位（要保留桌子）
--       「上一個時段」放實到的兩組和一筆沒來的（逾時未到）；
--         時段開始還不到 90 分鐘的話，實到的兩組還坐在 A02、A03，超過就當作吃完走了，A02、A03 改成現場客
--   · 其他正在用餐的桌子：入座時間＝現在往前推 15～100 分鐘（其中兩桌超過 90 分鐘）
--   · 今天中午用過餐的訂單（已結帳，給時間軸當歷史）：固定在 11:05～13:00，只有 14:30 以後執行才會新增
--   ⚠ 保留桌數只看「之後 90 分鐘內」的訂位，要在 09:30～12:30、15:30～18:30 執行並 demo 才看得到保留效果。
--   要調整 Windows 時間來 demo 的話：先改時間，再執行這份。
--
-- 測試帳號（密碼都是 {bcrypt} 雜湊，下面是登入時輸入的明碼）
--   後台（員工編號／密碼）：員工 1～5 的密碼是各自的電話（G1 的設定）
--     員工 1 陳小心  1／0987878787  ← G1 的建表 SQL 已經給他 1～7 全部權限（3 = 入座管理），測試用這個帳號
--     員工 4 李木子  4／0900111222
--   前台會員（電話／密碼）：
--     會員 1～3（G1）：0912345678／Test1234、0985510798／Test5678、0911111111／Test9012
--     會員 4～7（這份）：0911000004～0911000007／Test0004～Test0007
-- =========================================================
USE project;

-- 以 Java 時鐘為主：NOW()、CURDATE() 一律用台灣時間，跟入座管理（Java）的「現在」一致
SET time_zone = '+08:00';

-- D0. 【li 補上後刪除】G1 的建表 SQL 還沒有 orders_details.od_served_at，但 OrdersDetailsVO 有對應這個欄位，
--     少了它所有讀訂單的頁面都會出錯。這裡只在「還沒有這個欄位」時補上，已經有了就跳過。
SET @has_col = (SELECT COUNT(*) FROM information_schema.columns
                WHERE table_schema = 'project' AND table_name = 'orders_details' AND column_name = 'od_served_at');
SET @ddl = IF(@has_col = 0, 'ALTER TABLE orders_details ADD COLUMN od_served_at DATETIME NULL COMMENT ''送達時間''', 'DO 0');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- D1. 會員：多 4 位，給今天的訂位用（mem_no 4~7）
--   密碼照 G1 的規則存成 {bcrypt} 雜湊（MemberService 用 DelegatingPasswordEncoder 比對，沒有前綴會出錯）
--   明碼是 Test0004～Test0007；載具條碼照 G1 的格式「/ + 7 碼」
INSERT INTO member (mem_no, mem_tel, mem_name, mem_account, mem_password, mem_mail, mem_barcode, mem_tag, mem_birth, mem_fb, mem_line, mem_goo, mem_x, mem_status) VALUES
(4, '0911000004', '黃大偉', 'huangdw04', '{bcrypt}$2a$10$UCU/PUmq7rIyr0fLtWD2teTjzAuaS1DMu.6JKqV83FsHPGsmclcrG', 'huangdw04@example.com', '/GH45678', NULL, '1990-01-20', NULL, NULL, NULL, NULL, '已驗證'),
(5, '0911000005', '吳佩琪', 'wupq05', '{bcrypt}$2a$10$ci7JLbzH5afudwz2Piy2KOqIBDoxI1obRbiAiOl9gcxf.L/DOLEK2', 'wupq05@example.com', '/IJ56789', '對海鮮過敏', '1993-05-02', NULL, NULL, NULL, NULL, '已驗證'),
(6, '0911000006', '周子翔', 'zhouzx06', '{bcrypt}$2a$10$/9NX/QXbmZBSV1S56p746O/TXDSFSkRCkBLbd14UWRAyWSwRwjR5y', 'zhouzx06@example.com', '/KL67890', NULL, '1988-09-14', NULL, NULL, NULL, NULL, '已驗證'),
(7, '0911000007', '鄭雅文', 'zhengyw07', '{bcrypt}$2a$10$2s04GB/YAa11ZX6YbzVarOG8v5RR7TAKzJi89meyjQ7i2dagviWGa', 'zhengyw07@example.com', '/MN78901', '喜歡安靜的位子', '1996-12-30', NULL, NULL, NULL, NULL, '已驗證');

-- D2. 今天的四個訂位時段（rsv_dt_no 5~8）
INSERT INTO reservation_datetime (rsv_dt_no, rsv_dt_datetime) VALUES
(5, TIMESTAMP(CURDATE(), '11:00:00')),
(6, TIMESTAMP(CURDATE(), '12:30:00')),
(7, TIMESTAMP(CURDATE(), '17:00:00')),
(8, TIMESTAMP(CURDATE(), '18:30:00'));

-- 找出「上一個時段」和「下一個時段」（沒有的話是 NULL）
SET @prev_slot = (SELECT rsv_dt_no FROM reservation_datetime
                  WHERE rsv_dt_no BETWEEN 5 AND 8 AND rsv_dt_datetime <= NOW()
                  ORDER BY rsv_dt_datetime DESC LIMIT 1);
SET @prev_time = (SELECT rsv_dt_datetime FROM reservation_datetime WHERE rsv_dt_no = @prev_slot);
-- 上一個時段的訂位客人還坐著嗎？（時段開始還不到 90 分鐘）不是的話 @seated_time 是 NULL
SET @seated_time = IF(@prev_time > NOW() - INTERVAL 90 MINUTE, @prev_time, NULL);
SET @next_slot = (SELECT rsv_dt_no FROM reservation_datetime
                  WHERE rsv_dt_no BETWEEN 5 AND 8 AND rsv_dt_datetime > NOW()
                  ORDER BY rsv_dt_datetime LIMIT 1);

-- D3. 今天的訂位（每個時段的上限：2 人桌 5 組、4 人桌 3 組、6 人桌 1 組；同一位會員同一個時段只有一筆）
-- 上一個時段：兩組實到，一組沒來（還是「預約」，入座管理顯示「逾時未到」）
INSERT INTO reservation (mem_no, rsv_dt_no, seat_type_no, rsv_create_time, rsv_status, rsv_comment)
SELECT r.mem_no, @prev_slot, r.seat_type_no, NOW() - INTERVAL r.days DAY, r.rsv_status, r.rsv_comment
FROM (
            SELECT 3 AS mem_no, 2 AS seat_type_no, 1 AS days, '實到' AS rsv_status, '2人約會' AS rsv_comment
  UNION ALL SELECT 7, 2, 1, '實到', '2人用餐'
  UNION ALL SELECT 5, 2, 2, '預約', '2人用餐'
) AS r
WHERE @prev_slot IS NOT NULL;

-- 下一個時段：還沒到，要保留 2 人桌 3 桌、4 人桌 2 桌、6 人桌 1 桌；另有一筆已取消（不算）
INSERT INTO reservation (mem_no, rsv_dt_no, seat_type_no, rsv_create_time, rsv_status, rsv_comment)
SELECT r.mem_no, @next_slot, r.seat_type_no, NOW() - INTERVAL r.days DAY, r.rsv_status, r.rsv_comment
FROM (
            SELECT 1 AS mem_no, 2 AS seat_type_no, 1 AS days, '預約' AS rsv_status, '2人用餐' AS rsv_comment
  UNION ALL SELECT 4, 2, 0, '預約', '2人用餐'
  UNION ALL SELECT 6, 2, 0, '預約', '2人用餐'
  UNION ALL SELECT 2, 4, 2, '預約', '4人聚餐'
  UNION ALL SELECT 5, 4, 0, '預約', '3人用餐'
  UNION ALL SELECT 7, 6, 4, '預約', '6人慶生'
  UNION ALL SELECT 3, 4, 0, '取消', '臨時有事'
) AS r
WHERE @next_slot IS NOT NULL;

-- D4. 補齊桌位到 17 桌（原本只有 A01、B01、C01）：2 人桌 A01~A10、4 人桌 B01~B05、6 人桌 C01~C02
--   A02、A03：上一個時段的訂位客人還坐著的話，入座時間＝時段開始後 2、5 分鐘；否則是現場客
--   注意：原始資料的 B01（18:30 入座）、C01（19:00 入座）比執行時間還晚的話，入座管理會標成「時間異常」
INSERT INTO seat (seat_no, seat_type_no, seat_status, seat_time)
SELECT s.seat_no, s.seat_type_no,
       CASE WHEN s.after_slot IS NOT NULL AND @seated_time IS NULL THEN '其它者使用' ELSE s.seat_status END,
       CASE
         WHEN s.after_slot IS NOT NULL AND @seated_time IS NOT NULL
           THEN TIME(LEAST(@seated_time + INTERVAL s.after_slot MINUTE, NOW()))
         WHEN s.mins IS NULL THEN NULL
         ELSE TIME(NOW() - INTERVAL s.mins MINUTE)
       END
FROM (
            SELECT 'A02' AS seat_no, 2 AS seat_type_no, '訂位者使用' AS seat_status, 78 AS mins, 2 AS after_slot
  UNION ALL SELECT 'A03', 2, '訂位者使用', 75,   5
  UNION ALL SELECT 'A04', 2, '其它者使用', 100,  NULL   -- 超時，而且已結帳
  UNION ALL SELECT 'A05', 2, '其它者使用', 30,   NULL
  UNION ALL SELECT 'A06', 2, '可使用',     NULL, NULL
  UNION ALL SELECT 'A07', 2, '可使用',     NULL, NULL
  UNION ALL SELECT 'A08', 2, '其它者使用', 15,   NULL
  UNION ALL SELECT 'A09', 2, '可使用',     NULL, NULL
  UNION ALL SELECT 'A10', 2, '停用',       NULL, NULL
  UNION ALL SELECT 'B02', 4, '其它者使用', 95,   NULL   -- 超時，還沒結帳
  UNION ALL SELECT 'B03', 4, '可使用',     NULL, NULL
  UNION ALL SELECT 'B04', 4, '可使用',     NULL, NULL
  UNION ALL SELECT 'B05', 4, '其它者使用', 20,   NULL
  UNION ALL SELECT 'C02', 6, '可使用',     NULL, NULL
) AS s;

-- D5. 今天的訂單（orders_no 4~16）
--   orders_pay 是 NULL＝還沒結帳；會員客人帶 member_no（入座管理用它顯示客人資訊），現場客是 NULL
--   金額先填 0，等明細新增完再用 D7 從明細加總
-- 中午用過餐（已結帳）：時間軸上「今天稍早誰坐過」；只有 14:30 以後執行才新增
INSERT INTO orders (orders_no, seat_no, member_no, orders_time, orders_total, orders_discount_total, orders_actual_price, orders_pay)
SELECT o.orders_no, o.seat_no, o.member_no, TIMESTAMP(CURDATE(), o.t), 0, 0, 0, o.pay
FROM (
            SELECT 4 AS orders_no, 'A02' AS seat_no, 1 AS member_no, '11:05:00' AS t, '刷卡' AS pay
  UNION ALL SELECT 5, 'B03', 4,    '11:10:00', '現金'
  UNION ALL SELECT 6, 'A06', NULL, '11:40:00', '現金'
  UNION ALL SELECT 7, 'A04', 2,    '11:30:00', '刷卡'
  UNION ALL SELECT 8, 'C02', 6,    '12:40:00', '刷卡'
  UNION ALL SELECT 9, 'B04', NULL, '13:00:00', '現金'
) AS o
WHERE NOW() >= TIMESTAMP(CURDATE(), '14:30:00');

-- 目前在座：下單時間＝這桌的入座時間 + N 分鐘（不晚於現在）
--   A02、A03 是訂位客人才帶會員編號；是現場客的話，會員編號就是 NULL（入座管理顯示「非會員」）
INSERT INTO orders (orders_no, seat_no, member_no, orders_time, orders_total, orders_discount_total, orders_actual_price, orders_pay)
SELECT o.orders_no, o.seat_no,
       CASE WHEN s.seat_status = '訂位者使用' THEN o.member_no ELSE NULL END,
       LEAST(TIMESTAMP(CURDATE(), s.seat_time) + INTERVAL o.after_seat MINUTE, NOW()),
       0, 0, 0, o.pay
FROM (
            SELECT 10 AS orders_no, 'A02' AS seat_no, 3 AS member_no, 8 AS after_seat, NULL AS pay
  UNION ALL SELECT 11, 'A03', 7,    7,  NULL
  UNION ALL SELECT 12, 'A04', NULL, 10, '刷卡'   -- 已結帳但還坐著 → 桌角綠色 ✓
  UNION ALL SELECT 13, 'A05', NULL, 8,  NULL
  UNION ALL SELECT 14, 'A08', NULL, 5,  NULL
  UNION ALL SELECT 15, 'B02', NULL, 10, NULL
  UNION ALL SELECT 16, 'B05', NULL, 8,  NULL
) AS o
JOIN seat s ON s.seat_no = o.seat_no;

-- D6. 今天訂單的明細：單價從 meal 表讀（只新增「訂單有建立」的明細）
INSERT INTO orders_details (orders_no, meal_no, promote_no, od_meal_num, od_discount_price, od_discount_total, od_actual_price, od_comment, od_status)
SELECT d.orders_no, m.meal_no, NULL, d.qty, m.meal_price, 0, m.meal_price * d.qty, d.cmt, d.st
FROM (
            SELECT 4 AS orders_no, 11 AS meal_no, 1 AS qty, '' AS cmt, '已送達' AS st
  UNION ALL SELECT 4,  36, 2, '', '已送達'
  UNION ALL SELECT 5,  10, 2, '', '已送達'
  UNION ALL SELECT 5,  2,  1, '', '已送達'
  UNION ALL SELECT 6,  9,  1, '', '已送達'
  UNION ALL SELECT 6,  37, 1, '', '已送達'
  UNION ALL SELECT 7,  15, 1, '', '已送達'
  UNION ALL SELECT 7,  28, 1, '', '已送達'
  UNION ALL SELECT 8,  21, 2, '', '已送達'
  UNION ALL SELECT 8,  6,  2, '', '已送達'
  UNION ALL SELECT 9,  13, 2, '', '已送達'
  UNION ALL SELECT 9,  1,  1, '', '已送達'
  UNION ALL SELECT 10, 7,  1, '', '已送達'
  UNION ALL SELECT 10, 30, 1, '', '等待送餐'
  UNION ALL SELECT 11, 12, 1, '不要辣', '已送達'
  UNION ALL SELECT 11, 36, 1, '', '已送達'
  UNION ALL SELECT 12, 19, 1, '', '已送達'
  UNION ALL SELECT 12, 32, 1, '', '已送達'
  UNION ALL SELECT 13, 14, 1, '', '準備中'
  UNION ALL SELECT 13, 3,  1, '', '已送達'
  UNION ALL SELECT 14, 8,  1, '', '準備中'
  UNION ALL SELECT 14, 37, 1, '', '未備料'
  UNION ALL SELECT 15, 22, 2, '', '已送達'
  UNION ALL SELECT 15, 4,  1, '', '已送達'
  UNION ALL SELECT 16, 18, 2, '', '等待送餐'
  UNION ALL SELECT 16, 5,  1, '', '準備中'
) AS d
JOIN meal m ON m.meal_no = d.meal_no
JOIN orders o ON o.orders_no = d.orders_no
ORDER BY d.orders_no, d.meal_no;

-- D7. 今天訂單的金額：從明細加總（只更新這份檔案新增的訂單 4~16）
UPDATE orders o
JOIN (
  SELECT orders_no, SUM(od_actual_price) AS total
  FROM orders_details
  WHERE orders_no BETWEEN 4 AND 16
  GROUP BY orders_no
) t ON t.orders_no = o.orders_no
SET o.orders_total = t.total,
    o.orders_discount_total = 0,
    o.orders_actual_price = t.total
WHERE o.orders_no BETWEEN 4 AND 16;

-- D8. 正在候位的客人（waiting 沒有日期欄位，候位中、已通知就算目前在等）
INSERT INTO waiting (seat_type_no, mem_no, waiting_tel, waiting_name, waiting_comment, waiting_status, waiting_notify_time) VALUES
(2, NULL, '0912345678', '張先生', '2位', '已通知', TIME(NOW() - INTERVAL 5 MINUTE)),
(2, NULL, '0922333444', '李小姐', '2位', '候位中', NULL),
(4, NULL, '0933555666', '趙先生', '4位', '候位中', NULL),
(6, NULL, '0955777888', '何小姐', '5位慶生', '候位中', NULL);

