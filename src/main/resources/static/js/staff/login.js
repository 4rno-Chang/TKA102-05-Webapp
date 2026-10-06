/* =========================================================
   Bistroops 員工後台：登入頁程式
   內容：1. 員工編號只能輸入數字　2. 顯示／隱藏密碼　3. 防止重複送出

   對應的 CSS：static/css/staff/login.css
   帳密比對由 Spring Security 處理（StaffSecurityConfig），這裡只負責畫面上的小功能。
   ========================================================= */


/* ========== 1. 員工編號只能輸入數字 ========== */
const empNo = document.getElementById('empNo');

empNo.addEventListener('input', () => {
  const digits = empNo.value.replace(/\D/g, '');   // 去掉數字以外的字元（含全形）
  if (digits !== empNo.value) empNo.value = digits;
});


/* ========== 2. 顯示／隱藏密碼 ========== */
const password = document.getElementById('empPassword');
const toggle = document.getElementById('togglePassword');

toggle.addEventListener('click', () => {
  const show = password.type === 'password';
  password.type = show ? 'text' : 'password';
  toggle.textContent = show ? '隱藏' : '顯示';
  toggle.setAttribute('aria-label', show ? '隱藏密碼' : '顯示密碼');
  toggle.setAttribute('aria-pressed', show);
  password.focus();
});


/* ========== 3. 防止重複送出 ========== */
const form = document.getElementById('loginForm');
const submit = document.getElementById('loginSubmit');

form.addEventListener('submit', () => {
  // 送出前把密碼欄改回隱藏，避免瀏覽器記住「顯示中」的狀態
  password.type = 'password';
  submit.disabled = true;
  submit.textContent = '登入中…';
});

// 按瀏覽器「上一頁」回到這頁時（頁面從快取還原），把按鈕恢復可以點
window.addEventListener('pageshow', () => {
  submit.disabled = false;
  submit.textContent = '登入';
});
