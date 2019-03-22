package tools.discord.api;

public class DiscordSettings {

	private String clientToken;

	public static DiscordSettings build(String client_token) {
		return new DiscordSettings(client_token);
	}

	private DiscordSettings(String client_token) {
		this.clientToken = client_token;
	}

	public String getClientToken() {
		return clientToken;
	}

}
