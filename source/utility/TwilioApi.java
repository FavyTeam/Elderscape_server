package utility;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import core.ServerConfiguration;
import java.net.URI;
import java.net.URISyntaxException;
import tools.discord.DiscordConstants;
import tools.discord.api.DiscordBot;
import tools.discord.content.DiscordCommands;

/**
 * Twilio api system to initiate phone calls and Sms for emergency contact to Administrators. https://www.twilio.com
 *
 * @author MGT Madness, created on 24-11-2017.
 */
public class TwilioApi {

	// Find your Account Sid and Token at twilio.com/user/account
	public static final String ACCOUNT_SID = "ACa64fcae52bf9b10a4dd4c967cd0b1948";

	public static final String AUTH_TOKEN = "3afa863204a4aec451356afff6a5d77b";

	public static final String TWILIO_PHONE_NUMBER = "+16313971154 ";

	// Egypt: +201128244191
	// Malaysia: +601113036849

	public static void main(String[] args) throws URISyntaxException {
		callAdmin("Test1", "Test");
	}

	public static void callAdmin(String smsReason, String discordMessageToAlertMods) {
		if (ServerConfiguration.DEBUG_MODE) {
			Misc.print("Attempted call: smsReason: " + smsReason + ", discordMessageToAlertMods: " + discordMessageToAlertMods);
			return;
		}
		if (!discordMessageToAlertMods.isEmpty()) {
			DiscordCommands.addOutputText("Mohamed being called for serious issue related to: " + discordMessageToAlertMods);
			DiscordBot.sendMessage(DiscordConstants.STAFF_CHAT_CHANNEL, DiscordCommands.queuedBotString, true);
		}
		try {
			smsReason = Misc.getDateAndTime() + ": " + smsReason;
			EmailSystem.addPendingEmail("Phone Call", smsReason, "mohamed_ffs25ffs@yahoo.com");
			Misc.print("Call Mohamed: " + smsReason);
			Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
			smsSent(smsReason);
			called(!discordMessageToAlertMods.isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	private static boolean smsSent(String text) {
		Message message = Message.creator(new PhoneNumber(WebsiteLogInDetails.ADMIN_MOBILE_NUMBER), new PhoneNumber(TWILIO_PHONE_NUMBER), text).create();
		Misc.print("SMS sid: " + message.getSid() + ", length: " + text.length());

		return true;
	}

	private static boolean called(boolean staffChatAlert) {
		try {
			Call call = Call.creator(new PhoneNumber(WebsiteLogInDetails.ADMIN_MOBILE_NUMBER), new PhoneNumber(TWILIO_PHONE_NUMBER),
			                         new URI("https://demo.twilio.com/welcome/voice.xml")).create();
			Misc.print("Phone call sid: " + call.getSid());
			if (staffChatAlert) {
				DiscordCommands.addOutputText("Successfully calling phone starting with " + WebsiteLogInDetails.ADMIN_MOBILE_NUMBER.substring(0, 5));
			}
			return true;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			if (staffChatAlert) {
				DiscordCommands.addOutputText("Call failed, try again in a minute.");
			}
			return false;
		}
	}


}
