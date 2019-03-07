package util;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.Authenticator;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;
import java.util.Properties;

/**
 * 发邮件工具类
 * <br>
 * created on 2019/03/07
 *
 * @author 巽
 **/
public class MailUtil implements Runnable {
	private String email;// 收件人邮箱
	private String code;// 激活码

	public MailUtil(String email, String code) {
		this.email = email;
		this.code = code;
	}

	@Override
	public void run() {
		// 1.创建连接对象javax.mail.Session
		// 2.创建邮件对象 javax.mail.Message
		// 3.发送一封激活邮件
		String from = "812375418@qq.com";   // 发件人电子邮箱
		String host = "smtp.qq.com";    // 指定发送邮件的主机smtp.qq.com(QQ)

		Properties properties = new Properties();

		properties.setProperty("mail.smtp.host", host); // 设置邮件服务器
		properties.setProperty("mail.smtp.auth", "true");   // 打开认证

		try {
			// QQ邮箱需要下面这段代码，163邮箱不需要
			MailSSLSocketFactory sf = new MailSSLSocketFactory();
			sf.setTrustAllHosts(true);
			properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.ssl.socketFactory", sf);

			// 1.获取默认session对象
			Session session = Session.getDefaultInstance(properties, new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("812375418@qq.com", "vimfdhenzlrrbcec"); // 发件人邮箱账号、授权码
				}
			});

			// 2.创建邮件对象
			Message message = new MimeMessage(session);
			// 2.1设置发件人
			message.setFrom(new InternetAddress(from));
			// 2.2设置接收人
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			// 2.3设置邮件主题
			message.setSubject("账号激活");
			// 2.4设置邮件内容
			message.setContent("<html><head></head><body><h1>这是一封来自MyCourses的新用户激活验证邮件，若非您本人操作，请忽略此邮件。<br>点击以下链接以激活账户：</h1><br>"
					+ "<h3><a href='http://localhost:8080/MyCourses/activate/" + code + "'>http://localhost:8080/MyCourses/activate/" + code
					+ "</a></h3></body></html>", "text/html;charset=UTF-8");
			// 3.发送邮件
			Transport.send(message);
			System.out.println("邮件成功发送!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
