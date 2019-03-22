package network.login;

import core.ServerConfiguration;
import network.HTTPRequest;
import utility.Misc;

public class Verification {

	public static Boolean verify(String USER, String MAC, int CLIVER) {
		if (ServerConfiguration.VERIFICATION_MODE) {
			String VERIFY = "K3KM0N32ggF2";
			String reqParam = "?server=" + VERIFY + "&username=" + USER + "&mac=" + MAC + "&version=" + CLIVER;
			Misc.print("Username: " + USER + " MAC: " + MAC + " Version: " + CLIVER);
			String url = "http://94.23.249.25/auth/";
			//String url = "http://192.168.1.72:8888/";
			String response;

			try {
				response = HTTPRequest.sendGet(url + reqParam);
			} catch (Exception e) {
				response = e.getMessage();
			}

			Misc.print("Response: " + response);

			if (response != "" || response != "Error") {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
}
