package utility;

import core.ServerConfiguration;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

/**
 * Emails now are pended where it is sent every 60 seconds to prevent Yahoo from banning it for spam.
 *
 * @author MGT Madness, created on 11-08-2017
 */
public class EmailSystem {
	private String text = "";

	private String getText() {
		return text;
	}

	private String title = "";

	private String getTitle() {
		return title;
	}

	private String receiverEmail = "";

	private String getReceiverEmail() {
		return receiverEmail;
	}

	public EmailSystem(String title, String text, String receiverEmail) {
		this.text = text;
		this.title = title;
		this.receiverEmail = receiverEmail;
	}

	public static List<EmailSystem> emailSystemList = new ArrayList<EmailSystem>();

	public static Timer timer = new Timer();

	public static TimerTask myTask = new TimerTask() {
		@Override
		public void run() {
			// Send 1 email every 60 seconds if there are pending emails to be sent.
			if (ServerConfiguration.DEBUG_MODE) {
				return;
			}
			loopContent();
		}
	};

	private static void loopContent() {
		if (emailSystemList.size() == 0) {
			return;
		}
		boolean sent = sendEmail(emailSystemList.get(0).getTitle(), emailSystemList.get(0).getText(), emailSystemList.get(0).getReceiverEmail());
		if (!sent) {
			return;
		}
		emailSystemList.remove(0);
	}

	private final static String SENDING_EMAIL_USERNAME = "busujimasaeko1337@yahoo.com";

	private final static String SENDING_EMAIL_PASSWORD = "bakabaka56741";

	private static boolean sendEmail(String title, String text, String receiverEmail) {
		// Sender's email ID needs to be mentioned
		String from = SENDING_EMAIL_USERNAME;
		String pass = SENDING_EMAIL_PASSWORD;
		// Recipient's email ID needs to be mentioned.
		String to = receiverEmail;
		String host = "smtp.mail.yahoo.com";

		// Get system properties
		Properties properties = System.getProperties();
		// Setup mail server
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.user", from);
		properties.put("mail.smtp.password", pass);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.auth", "true");

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);
		boolean sent = false;

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set Subject: header field
			message.setSubject(title);

			// Now set the actual message
			message.setText(text);

			// Send message
			Transport transport = session.getTransport("smtp");
			transport.connect(host, from, pass);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			ArrayList<String> list = new ArrayList<String>();
			list.add(Misc.getDateAndTimeLog());
			list.add("Title: " + title);
			list.add(text);
			//FileUtility.saveArrayContentsSilent("backup/logs/email_log.txt", list);
			sent = true;
		} catch (javax.mail.MessagingException mex) {
			Misc.print("Email failed to send, pending: " + EmailSystem.emailSystemList.size());
			sent = false;
		}
		return sent;
	}

	private static String getPreTitleText() {
		return "[DT " + Misc.getDateAndTime() + "] ";
	}

	public static void addPendingEmail(String title, ArrayList<String> content, String receiverEmail) {
		title = getPreTitleText() + title;
		String contentString = "";
		for (int index = 0; index < content.size(); index++) {
			contentString = contentString + content.get(index) + "\n";
		}
		EmailSystem.emailSystemList.add(new EmailSystem(title, contentString, receiverEmail));
	}

	public static void addPendingEmail(String title, String emailText, String receiverEmail) {
		title = getPreTitleText() + title;
		EmailSystem.emailSystemList.add(new EmailSystem(title, emailText, receiverEmail));
	}
}
