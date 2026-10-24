/* =========================================================
   Bistroops 前台：常見問題的清單由 JS 產生（ThymeToJS 分支）
   原本：Controller 查好放進 Model，Thymeleaf 用 th:each 產生
   現在：頁面先送出空的清單，這支用 fetch 呼叫 QaApiController 拿 JSON，再用 DOM 產生每一題

   用在兩個地方，HTML 上的 data-* 說明要怎麼做：
     data-qa-api      API 網址（Thymeleaf 的 @{...} 產生，含 context path）
     data-qa-style    faq ＝ 首頁的手風琴（按鈕＋答案）；page ＝ 常見問題頁（原生 <details>）
     data-click-base  點擊次數網址的前半段，後面接「編號/click」，給 qa-click.js 用
   同一個區塊裡 data-qa-when="loaded" 的元素載入成功才顯示，data-qa-when="empty" 沒有資料才顯示

   展開的效果、點擊次數不用改：frontend_index.js 和 qa-click.js 的 listener 掛在外層（事件委派），
   之後才加進來的題目一樣有效。
   ========================================================= */

document.querySelectorAll('[data-qa-api]').forEach(loadQaList);

async function loadQaList(list) {
  const box = list.parentElement;
  try {
    const res = await fetch(list.dataset.qaApi, { headers: { Accept: 'application/json' } });
    if (!res.ok) throw new Error('HTTP ' + res.status);   // fetch 只有網路斷線才會丟錯，404、500 要自己檢查
    const qaList = await res.json();

    if (qaList.length === 0) {
      show(box, 'empty');
      return;
    }
    const build = list.dataset.qaStyle === 'faq' ? faqItem : pageItem;
    qaList.forEach(qa => list.appendChild(build(qa, list.dataset.clickBase)));
    show(box, 'loaded');
  } catch (error) {
    console.error('常見問題載入失敗', error);
    list.appendChild(el('p', 'qa-load-error', '常見問題載入失敗，請重新整理頁面'));
  }
}

function show(box, when) {
  box.querySelectorAll(`[data-qa-when="${when}"]`).forEach(item => { item.hidden = false; });
}

/* 首頁：和原本 frontend_index.html 的 th:each 產生一樣的結構 */
function faqItem(qa, clickBase) {
  const item = el('div', 'faq-item');
  item.dataset.clickUrl = clickBase + qa.qaNo + '/click';

  const button = el('button', 'faq-question');
  button.setAttribute('aria-expanded', 'false');
  button.append(el('span', '', 'Q：' + qa.qaTitle), el('span', 'faq-icon', '+'));

  const answer = el('div', 'faq-answer');
  answer.appendChild(el('p', '', 'A：' + qa.qaContent));

  item.append(button, answer);
  return item;
}

/* 常見問題頁：和原本 qa/listAll.html 的 th:each 產生一樣的結構 */
function pageItem(qa, clickBase) {
  const item = el('details', 'qa-item');
  item.dataset.clickUrl = clickBase + qa.qaNo + '/click';

  const summary = el('summary', 'qa-question');
  const icon = el('span', 'qa-icon', '+');
  icon.setAttribute('aria-hidden', 'true');
  summary.append(el('span', '', 'Q：' + qa.qaTitle), icon);

  item.append(summary, el('p', 'qa-answer', 'A：' + qa.qaContent));
  return item;
}

// 文字一律用 textContent 放進去，不用 innerHTML：
// 問答內容是後台輸入的，如果有 <script> 之類的字，用 innerHTML 會被當成程式執行（XSS）
function el(tag, className, text) {
  const node = document.createElement(tag);
  if (className) node.className = className;
  if (text != null) node.textContent = text;
  return node;
}
