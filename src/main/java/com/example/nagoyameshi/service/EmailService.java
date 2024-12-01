package com.example.nagoyameshi.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private final JavaMailSender mailSender;; // SpringのMailSenderを使う場合

	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendEmail(String to, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);
		mailSender.send(message);
	}

	// パスワードリセットメールを送信するメソッド
	public void sendPasswordResetEmail(String to, String resetToken) {
		String subject = "パスワードリセットのご案内";
		String body = "以下のリンクをクリックして、パスワードをリセットしてください:\n"
				+ "http://yourdomain.com/reset-password?token=" + resetToken;

		// sendEmailメソッドを利用して、リセットメールを送信
		sendEmail(to, subject, body);
	}
}

/*import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendPasswordResetEmail(String email, String resetToken) {
        // メール送信処理の実装
        String resetLink = "https://example.com/reset-password?token=" + resetToken;
        // メール送信ロジック
    }
}*/