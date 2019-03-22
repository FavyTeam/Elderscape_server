package utility;

public class EncryptStrings {


	public final static String STRING_TO_USE = "noclip";

	public final static boolean ENCRYPT = true;

	static final String key = "MGTMadness"; // The key for 'encrypting' and 'decrypting'.

	private static String encryptString(String str) {
		StringBuffer sb = new StringBuffer(str);

		int lenStr = str.length();
		int lenKey = key.length();

		// For each character in our string, encrypt it...
		for (int i = 0, j = 0; i < lenStr; i++, j++) {
			if (j >= lenKey)
				j = 0; // Wrap 'round to beginning of key string.
			// XOR the chars together. Must cast back to char to avoid compile error.
			sb.setCharAt(i, (char) (str.charAt(i) ^ key.charAt(j)));
		}

		return sb.toString();
	}

	private static String decryptString(String str) {
		// To 'decrypt' the string, simply apply the same technique.
		return encryptString(str);
	}


	public static void main(String[] args) {
		new1();
	}

	private static void new1() {
		String s1 = STRING_TO_USE;
		String s2 = encryptString(s1);
		String s3 = decryptString(s1);
		if (ENCRYPT) {
			Misc.print("Original string:  ");
			Misc.print(s1);

			Misc.print("---");

			Misc.print("Encrypted string: ");
			Misc.print(s2);
		} else {
			Misc.print("Original string:  ");
			Misc.print(s1);

			Misc.print("---");

			Misc.print("Decrypted string: ");
			Misc.print(s3);
		}
	}
}
