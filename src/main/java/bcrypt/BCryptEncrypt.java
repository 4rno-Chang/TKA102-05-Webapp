package bcrypt;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 產生 BCrypt 雜湊，貼進資料庫當測試帳號的密碼
public class BCryptEncrypt {

	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println(encoder.encode("12345678")); // 1 陳小心
		System.out.println(encoder.encode("910111213")); // 2 カイル
		System.out.println(encoder.encode("11111111")); // 3 張學長（離職）
		System.out.println(encoder.encode("abcdefgh")); // 4 李木子
		System.out.println(encoder.encode("poiuytrew")); // 5 強滾滾

	}
}
