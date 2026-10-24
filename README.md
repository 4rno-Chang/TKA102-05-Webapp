# Bistroops 餐廳管理系統

TibaMe TKA102 第一組專題「Bistroops」的個人版 repo：顧客端可以看菜單、訂位、線上候位、掃碼點餐和結帳；店員端有入座管理、訂單、菜單、活動、公告、員工權限等後台。

- 團隊 repo：[4rno-Chang/TKA102G1](https://github.com/4rno-Chang/TKA102G1)（定稿版 master `e7b099b`）
- 這個 repo：我先在這裡開發、驗證自己負責的模組，再移到團隊 repo；組員的程式和團隊 repo 保持一致

## 技術

| 類別 | 使用 |
|---|---|
| 後端 | Java 25、Spring Boot 4.0.6（Spring MVC、Spring Data JPA／Hibernate、Spring Security、WebSocket） |
| 畫面 | Thymeleaf（伺服器端渲染）、原生 JavaScript、CSS |
| 資料 | MySQL 8、Redis 7（候位號碼牌、購物車、登入 Session） |
| 金流／通知 | 綠界 ECPay 測試環境、Email |
| 部署 | Docker Compose（MySQL、Redis、Adminer、Web）於 GCP VM |

## 我負責的模組

| 模組 | 前台（顧客） | 後台（店員） | 程式 |
|---|---|---|---|
| 入座管理 | — | 平面圖、時間軸；訂位者／候位／其它者入座、離席、停用、打烊清桌；計算各桌型「現場還能坐幾桌」 | `seattype/`、`templates/staff/seating.html` |
| 公告 | 首頁輪播（公告＋活動）、最新消息列表與內文 | 新增、修改、刪除、查詢；圖片上傳、預約上架 | `announcement/`、`announcementandqa/` |
| 常見問題 | 首頁熱門 5 題、常見問題頁、點擊次數統計 | 新增、修改、刪除 | `qa/` |
| 錯誤頁 | 400、404、405、413、4xx、500、5xx | 同左 | `error/`、`templates/error/` |

幾個實作重點：

- **入座管理**：每次請求建立一份快照（桌子、今日訂單、訂位、Redis 候位一次讀進來）再計算；改桌位前用悲觀鎖（`PESSIMISTIC_WRITE`），兩位店員同時操作同一桌不會重複入座；訂位者入座與訂位狀態在同一個交易。
- **常見問題**：點擊次數用一條 `UPDATE … SET qa_clicks = qa_clicks + 1`，同時點擊不會少算；熱門 5 題用 `PageRequest` 讓資料庫直接排序取前 5 筆。前台清單由 `QaApiController` 回 JSON、`qa-render.js` 產生畫面（PR #58，詳見 [ThymeToJS 分支 README](https://github.com/4rno-Chang/TKA102-05-Webapp/tree/ThymeToJS)）。
- **公告**：依開始時間在查詢時過濾，到時間自動上架；前後端都檢查不能設定過去的時間；上傳大小超過上限顯示 413 頁。
- **錯誤頁**：使用 Spring Boot 內建的 `BasicErrorController`，依狀態碼對應 `templates/error/*.html`；上傳過大另外用 `@ExceptionHandler` 處理。

## 專案結構

```
src/main/java/com/bistroops/
├── announcement/ qa/ seattype/ error/     我負責的模組
├── member/ employee/ permission/          會員、員工登入與權限
├── reservation/ reservationdatetime/      訂位
├── waiting/                               線上候位（Redis＋Lua）
├── meal/ mealtype/ promote/ promotemeal/  菜單、活動
├── orders/ ordersdetails/ orderscart/     點餐、購物車（Redis）、結帳（ECPay）
├── config/ interceptor/                   Spring Security、權限檢查
└── 資料庫表格建立/                         建表＋假資料 SQL
src/main/resources/
├── templates/front/   顧客端頁面          templates/staff/   店員端頁面
├── templates/error/   錯誤頁              static/            CSS、JS、圖片
└── application-docker.properties          Docker 用設定（值都從環境變數來）
```

每個模組都是同樣的分層：`controller`（收請求）→ `model` 裡的 Service（商業邏輯）→ Repository（JPA）→ VO（`@Entity`）。

## 本機執行（Eclipse）

1. 建立資料庫 `project`，執行 `src/main/java/資料庫表格建立/` 裡的其中一份：
   - `createTable_v1_9.sql`：訂位時段照營業時間
   - `createTable_v1_9_demo.sql`：任何時間都能訂位、候位（展示用）
2. 啟動本機 Redis（預設 `localhost:6379`）。
3. 在 `src/main/resources/` 建立 `application.properties`（不在 git 裡），至少設定：
   - `spring.datasource.url`、`spring.datasource.username`、`spring.datasource.password`
   - `spring.data.redis.host`、`spring.data.redis.port`
   - 綠界、Email 相關設定可參考 `application-docker.properties` 的屬性名稱
4. 匯入餐點、活動圖片：對 `ImageImporter.java` → Run As → Java Application（讀 `static/images/` 寫進資料庫）。
5. 對 `Application.java` → Run As → Spring Boot App，打開 `/bistroops`（顧客端）、`/staff/login`（店員端）。

## Docker 部署

```bash
cp .env.example .env      # 填入資料庫密碼、ECPay、Email 等值（.env 不進 git）
docker compose up -d --build
```

| 服務 | 對外 port（可在 .env 改） |
|---|---|
| Web | 18088 |
| MySQL | 13306 |
| Redis | 16379 |
| Adminer | 18080 |

容器啟動時設定 `SPRING_PROFILES_ACTIVE=docker`，讀 `application-docker.properties`，連線資訊與金鑰都由環境變數傳入。

## 分支

| 分支 | 用途 |
|---|---|
| `SpringBootMVC` | 開發用，沒有 Docker 檔案 |
| `SpringBootMVCDocker` | 加上 Docker 設定，GCP VM 從這個分支部署 |
| `master` | 合併後的版本 |
| `ThymeToJS` | 練習：常見問題從 Thymeleaf 改成 JSON API＋JS 渲染，含取捨說明 |
| `Hibernate_version`、`JDBC_version` | 早期 Servlet＋JSP 版本（JDBC、Hibernate） |
