# Bistroops — ThymeToJS 分支

把「常見問題」從 **Thymeleaf 伺服器端渲染**改成 **後端回 JSON、前端 JS 產生畫面**的練習分支。

- 基底：`master`（c7b3891，專案定稿版）
- 範圍：只改我負責的 QA 模組（前台常見問題頁、首頁「常見問題」區塊）；其他頁面、後台、組員模組都沒動
- 主要 commit：`4411bf8 ThymeToJS: QA lists rendered by JS from JSON API`

## Before／After

| | master（原本） | ThymeToJS（這個分支） |
|---|---|---|
| 資料怎麼到畫面 | Controller `model.addAttribute("qaList", …)` → Thymeleaf `th:each` 產生 HTML | 頁面先送出空的清單 → `qa-render.js` 用 `fetch` 呼叫 API → 用 DOM 產生每一題 |
| 後端 | `FrontQaController`、`IndexController_inSpringBoot` 放 Model | 新增 `QaApiController`（`@RestController`）回 JSON；兩個 Controller 不再放 QA 資料 |
| 畫面 | `th:each`、`th:text` | 空容器＋`data-*` 屬性，由 JS 填入 |
| 展開效果、點擊次數 | `frontend_index.js`、`qa-click.js` | **不用改**（listener 本來就用事件委派掛在外層） |

## API

| 方法 | 網址 | 回傳 |
|---|---|---|
| GET | `/bistroops/api/qa` | 全部問答，點擊多的在前 |
| GET | `/bistroops/api/qa?limit=5` | 點擊最多的 5 題（首頁用）；`limit` 小於 1 回 400 |
| POST | `/bistroops/qa/{qaNo}/click` | 點擊次數 +1，回 204（原本就有，沒改） |

回傳格式（只放畫面需要的欄位，不把整個 `QaVO` 送出去）：

```json
[
  { "qaNo": 3, "qaTitle": "可以帶寵物嗎？", "qaContent": "…" }
]
```

## 改了哪些檔案

| 檔案 | 改了什麼 |
|---|---|
| `qa/controller/QaApiController.java`（新增） | `@RestController`；用 `record QaItem` 決定回傳欄位；沿用 `QaService.getPopular()`／`getAllPopularFirst()`，Service、Repository 都沒改 |
| `static/js/front/qa-render.js`（新增） | `fetch` → 檢查 `res.ok` → `res.json()` → `createElement`＋`textContent` 產生題目；空資料、載入失敗都有對應顯示 |
| `templates/front/qa/listAll.html`、`frontend_index.html` | 拿掉 `th:each`，留空容器；API 網址用 `data-qa-api="@{/bistroops/api/qa}"` 交給 JS |
| `FrontQaController.java`、`IndexController_inSpringBoot.java` | 不再把 `qaList`、`popularQaList` 放進 Model |
| `static/css/front/qa.css` | 補 `[data-qa-when][hidden] { display: none; }`（原本 `.qa-empty` 的 `display: flex` 會蓋掉 `hidden`） |

## 設計重點

- **防 XSS**：問答內容是後台輸入的，一律用 `textContent` 放進畫面，不用 `innerHTML`。Thymeleaf 的 `th:text` 原本會自動跳脫，改成 JS 後要自己注意。
- **fetch 的錯誤要自己檢查**：fetch 只有網路斷線才會丟錯，404、500 要看 `res.ok`。
- **context path**：JS 不寫死網址，由 Thymeleaf 的 `@{...}` 產生後放在 `data-*` 屬性。
- **事件委派**：手風琴和點擊次數的 listener 掛在外層容器，JS 之後才產生的題目一樣有效，所以原本的 JS 不用改。
- **API 只回需要的欄位**：用 `record` 當回傳格式，`QaVO` 之後加欄位也不會不小心送到前端。

## 取捨

| | Thymeleaf（伺服器端渲染） | JS（前端渲染） |
|---|---|---|
| 第一次看到內容 | 快，HTML 一到就有內容 | 多等一次 API |
| SEO | 好 | 較差 |
| 局部更新 | 要整頁重新載入 | 只更新那一塊 |
| API 重用 | — | 同一個 API 可以給 App、其他頁面用 |
| 錯誤處理 | 出錯導到 500 錯誤頁 | 要自己處理空資料、載入失敗 |

結論：常見問題這種以內容為主的頁面，其實留在 Thymeleaf 比較合適；這個分支是為了練習前後端分離的寫法。真正適合改成前端渲染的，是入座管理這種需要即時更新的畫面（目前靠每 60 秒整頁重新整理）。

## 怎麼驗證

1. 啟動專案後打開首頁 `/bistroops`：「常見問題」顯示點擊最多的 5 題，點了可以展開。
2. 打開 `/bistroops/qa`：顯示全部問答。
3. 瀏覽器開發者工具 → Network：看到 `api/qa?limit=5`、`api/qa` 回傳 JSON；展開一題時 `click` 回 204。
4. 直接開 `/bistroops/api/qa?limit=5` 會看到 JSON。

## 開發方式

這個分支是我和 AI 程式助手（Claude Code）一起完成的：我決定範圍（只改自己的模組）和 API 格式，AI 產生程式，我逐檔看過 diff、在本機 Eclipse 實際執行並用開發者工具確認後才 commit。只看 HTML 和 JS 會漏掉的地方要對照其他檔案確認，例如原本 CSS 的 `display: flex` 會讓「沒有資料」區塊的 `hidden` 失效，所以另外補了一行 CSS。

## 還沒做

- 公告前台（最新消息列表、首頁輪播）還是 Thymeleaf。
