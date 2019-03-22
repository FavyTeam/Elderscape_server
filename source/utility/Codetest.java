package utility;

import core.ServerConstants;
import java.util.ArrayList;

public class Codetest {

	static ArrayList<String> all = new ArrayList<String>();
	static ArrayList<String> matches = new ArrayList<String>();
	public static void main(String[] args) {
		
		boolean runFileStoreUidMatcher = false;
		if (!runFileStoreUidMatcher) {
			return;
		}
		 // Duplicate UID finder.
		//id, date, username, ip_address, os, serial, windows_uid_basic, windows_sn_different, windows_c_drive_uid, base_board_serial_id, hard_disk_serial, file_store_uuid, uuids
		ArrayList<String> line = FileUtility.readFile(System.getProperty("user.home") + "/Desktop/uid.txt");
		
		for (int index = 0; index < line.size(); index++) {
			String[] parse = line.get(index).split(", ");
			String combined = "";
			for (int i = 0; i < parse.length; i++) {
				if (i < 6) {
					continue;
				}
				String extra = ServerConstants.UUID_SEPERATOR;
				if (i == parse.length - 1) {
					extra = "";
				}
				if (!parse[i].isEmpty() && !parse[i].equals("invalid")) {
					combined = combined + parse[i] + extra;
				}
			}
			all.add(combined);
		}
		
		for (int index = 0; index < line.size(); index++) {
			String[] parse = line.get(index).split(", ");
			String serial = Misc.formatUid(parse[6]);
			String windows_uid_basic = Misc.formatUid(parse[7]);
			String windows_sn_different = Misc.formatUid(parse[8]);
			String windows_c_drive_uid = Misc.formatUid(parse[9]);
			String base_board_serial_id = Misc.formatUid(parse[10]);
			
			String[] hard_disk_serial_parse = parse[11].split("#!#");
			ArrayList<String> hard_disk_serial = new ArrayList<String>();
			for (int i = 0; i < hard_disk_serial_parse.length; i++) {
				hard_disk_serial.add(hard_disk_serial_parse[i]);
			}
			for (int i = 0; i < hard_disk_serial.size(); i++) {
				String old = hard_disk_serial.get(i);
				hard_disk_serial.remove(i);
				hard_disk_serial.add(i, Misc.formatUid(old));
			}
		
			String[] file_store_uuid_parse = parse[12].split("#!#");
			ArrayList<String> file_store_uuid = new ArrayList<String>();
			for (int i = 0; i < file_store_uuid_parse.length; i++) {
				file_store_uuid.add(file_store_uuid_parse[i]);
			}
			for (int i = 0; i < file_store_uuid.size(); i++) {
				String old = file_store_uuid.get(i);
				file_store_uuid.remove(i);
				file_store_uuid.add(i, Misc.formatUid(old));
				if (matches(Misc.formatUid(old), index)) {
					continue;
				}
			}
		
			String[] uuids_parse = parse[13].split("#!#");
			ArrayList<String> uuids = new ArrayList<String>();
			for (int i = 0; i < uuids_parse.length; i++) {
				uuids.add(uuids_parse[i]);
			}
			for (int i = 0; i < uuids.size(); i++) {
				String old = uuids.get(i);
				uuids.remove(i);
				uuids.add(i, Misc.formatUid(old));
			}
		}
		
	}

	private static boolean matches(String serial, int originalIndex) {
		for (int index = 0; index < all.size(); index++) {
			if (index == originalIndex) {
				continue;
			}
			if (Misc.uidMatches(serial, all.get(index))) {
				if (!matches.contains(serial)) {
					Misc.print(serial + ", matches");
					matches.add(serial);
				}
				return true;
			}
		}
		return false;
	}
}
