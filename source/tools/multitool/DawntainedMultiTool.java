package tools.multitool;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import core.GameType;
import core.ServerConstants;
import game.player.punishment.Ban;
import game.player.punishment.Blacklist;
import tools.EconomyScan;
import utility.FileUtility;
import utility.Logger;
import utility.Misc;

/**
 * An easy Administrative tool to check alts of players and its wealth.
 *
 * @author MGT Madness, created on 19-08-2017
 */
public class DawntainedMultiTool {

	/**
	 * The JFrame instance.
	 */
	public static JFrame frame;

	/**
	 * The input field where commands are executed.
	 */
	private static JTextField commandInputArea;

	/**
	 * Store the size of the error file. So when it increases, it means an error was printed to it, then execute a pop-up.
	 */
	private static long errorFileSize = 0;

	/**
	 * The scroll bar.
	 */
	public static JScrollPane scrollPane = new JScrollPane();

	/**
	 * Store scanned data of flagged players to print out.
	 */
	public static ArrayList<String> altData = new ArrayList<String>();

	/**
	 * Store only the names of the alts, used for trade history.
	 */
	public static ArrayList<String> altNames = new ArrayList<String>();

	/**
	 * Store names of flagged players to later scan them.
	 */
	public static ArrayList<String> flaggedPlayers = new ArrayList<String>();

	/**
	 * Amount of scan accounts for alts threads alive.
	 */
	public static int scanThreadsActive;

	/**
	 * True if the scan has been executed and is still on-going.
	 */
	public static boolean scanOngoing;

	/**
	 * Total character files scanned.
	 */
	public static int totalScans;

	/**
	 * True if the scan threads have been executed. This is to prevent scanning banks for wealth to occur before scanning for alts is completed.
	 */
	public static boolean scanThreadOnline;

	private static final JLabel commandLabel = new JLabel("Command:");

	private static final JLabel executeCommand = new JLabel("Execute command:");

	private static final JLabel statusLabel = new JLabel("Status:");

	private static final JLabel ipAddressLabel = new JLabel("IP Address:");

	private static final JLabel uidFormattedLabel = new JLabel("UID Formatted:");

	private static final JLabel uidRawLabel = new JLabel("UID Raw:");

	private static final JTextField commandOutput = new JTextField();

	private static final JTextField statusOutput = new JTextField();

	private static final JTextField ipAddressOutput = new JTextField();

	private static final JTextField uidFormattedOutput = new JTextField();

	private static final JTextField uidRawOutput = new JTextField();

	/**
	 * Runs the program
	 */
	public static void main(String[] args) {
		// Get the error file size on start-up.
		//updateErrorFileSize();

		// Start the error system loop on a new thread.
		timer.schedule(myTask, 500, 500);

		// Redirect error output to a text file.
		//redirectErrorOutput();

		// Draw the JFrame.
		frame = new JFrame("Dawntained multi-tool");
		setTheme();

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]
				                              {0.0, 1.0};
		frame.getContentPane().setLayout(gridBagLayout);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 1;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;

		scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, constraints);

		scrollPane.setViewportView(textPane);

		GridBagConstraints gbc_updateBannedButton = new GridBagConstraints();
		gbc_updateBannedButton.insets = new Insets(0, 11, 5, 5);
		gbc_updateBannedButton.gridx = 0;
		gbc_updateBannedButton.gridy = 2;
		updateBannedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateBannedButtonAction();
			}
		});
		frame.getContentPane().add(updateBannedButton, gbc_updateBannedButton);

		GridBagConstraints gbc_executeCommand = new GridBagConstraints();
		gbc_executeCommand.anchor = GridBagConstraints.WEST;
		gbc_executeCommand.insets = new Insets(0, 11, 5, 5);
		gbc_executeCommand.gridx = 0;
		gbc_executeCommand.gridy = 3;
		frame.getContentPane().add(executeCommand, gbc_executeCommand);

		GridBagConstraints gbc_txtCommand = new GridBagConstraints();
		gbc_txtCommand.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtCommand.insets = new Insets(0, 0, 5, 10);
		gbc_txtCommand.gridx = 1;
		gbc_txtCommand.gridy = 3;

		commandInputArea = new JTextField();
		frame.getContentPane().add(commandInputArea, gbc_txtCommand);
		commandInputArea.setColumns(10);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				commandInputArea.requestFocus(); // Focus on the command input text field so you can start typing straight away.
			}
		});
		commandInputArea.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				executeCommand(command);
			}
		});

		GridBagConstraints gbc_commandOutput = new GridBagConstraints();
		gbc_commandOutput.fill = GridBagConstraints.HORIZONTAL;
		gbc_commandOutput.insets = new Insets(0, 0, 5, 10);
		gbc_commandOutput.gridx = 1;
		gbc_commandOutput.gridy = 5;
		frame.getContentPane().add(commandOutput, gbc_commandOutput);
		commandOutput.setEditable(false);
		commandOutput.setColumns(10);

		GridBagConstraints gbc_statusLabel = new GridBagConstraints();
		gbc_statusLabel.anchor = GridBagConstraints.WEST;
		gbc_statusLabel.insets = new Insets(0, 11, 5, 5);
		gbc_statusLabel.gridx = 0;
		gbc_statusLabel.gridy = 6;
		frame.getContentPane().add(statusLabel, gbc_statusLabel);

		GridBagConstraints gbc_statusOutput = new GridBagConstraints();
		gbc_statusOutput.insets = new Insets(0, 0, 5, 10);
		gbc_statusOutput.fill = GridBagConstraints.HORIZONTAL;
		gbc_statusOutput.gridx = 1;
		gbc_statusOutput.gridy = 6;
		statusOutput.setEditable(false);
		statusOutput.setColumns(10);
		frame.getContentPane().add(statusOutput, gbc_statusOutput);

		GridBagConstraints gbc_ipAddressOutput = new GridBagConstraints();
		gbc_ipAddressOutput.insets = new Insets(0, 0, 5, 10);
		gbc_ipAddressOutput.fill = GridBagConstraints.HORIZONTAL;
		gbc_ipAddressOutput.gridx = 1;
		gbc_ipAddressOutput.gridy = 7;
		ipAddressOutput.setEditable(false);
		ipAddressOutput.setColumns(10);
		frame.getContentPane().add(ipAddressOutput, gbc_ipAddressOutput);

		GridBagConstraints gbc_commandLabel = new GridBagConstraints();
		gbc_commandLabel.anchor = GridBagConstraints.WEST;
		gbc_commandLabel.insets = new Insets(0, 11, 5, 5);
		gbc_commandLabel.gridx = 0;
		gbc_commandLabel.gridy = 5;
		frame.getContentPane().add(commandLabel, gbc_commandLabel);

		GridBagConstraints gbc_ipAddressLabel = new GridBagConstraints();
		gbc_ipAddressLabel.anchor = GridBagConstraints.WEST;
		gbc_ipAddressLabel.insets = new Insets(0, 11, 5, 5);
		gbc_ipAddressLabel.gridx = 0;
		gbc_ipAddressLabel.gridy = 7;
		frame.getContentPane().add(ipAddressLabel, gbc_ipAddressLabel);

		GridBagConstraints gbc_uidFormattedLabel = new GridBagConstraints();
		gbc_uidFormattedLabel.anchor = GridBagConstraints.WEST;
		gbc_uidFormattedLabel.insets = new Insets(0, 11, 5, 5);
		gbc_uidFormattedLabel.gridx = 0;
		gbc_uidFormattedLabel.gridy = 8;
		frame.getContentPane().add(uidFormattedLabel, gbc_uidFormattedLabel);

		GridBagConstraints gbc_uidFormattedOutput = new GridBagConstraints();
		gbc_uidFormattedOutput.fill = GridBagConstraints.HORIZONTAL;
		gbc_uidFormattedOutput.insets = new Insets(0, 0, 5, 10);
		gbc_uidFormattedOutput.gridx = 1;
		gbc_uidFormattedOutput.gridy = 8;
		uidFormattedOutput.setEditable(false);
		uidFormattedOutput.setColumns(10);
		frame.getContentPane().add(uidFormattedOutput, gbc_uidFormattedOutput);

		GridBagConstraints gbc_uidRawLabel = new GridBagConstraints();
		gbc_uidRawLabel.insets = new Insets(0, 11, 8, 5);
		gbc_uidRawLabel.anchor = GridBagConstraints.WEST;
		gbc_uidRawLabel.gridx = 0;
		gbc_uidRawLabel.gridy = 9;
		frame.getContentPane().add(uidRawLabel, gbc_uidRawLabel);

		GridBagConstraints gbc_uidRawOutpupt = new GridBagConstraints();
		gbc_uidRawOutpupt.insets = new Insets(0, 0, 0, 10);
		gbc_uidRawOutpupt.fill = GridBagConstraints.HORIZONTAL;
		gbc_uidRawOutpupt.gridx = 1;
		gbc_uidRawOutpupt.gridy = 9;
		uidRawOutput.setEditable(false);
		uidRawOutput.setColumns(10);
		frame.getContentPane().add(uidRawOutput, gbc_uidRawOutpupt);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(900, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		EconomyScan.loadStartupFiles(false);

		// Checks if scanning all accounts to gather the alts names is done. To then print out the alt bank wealth.
		scanBankTimer.schedule(scanBankTask, 250, 250);
	}


	private static void setTheme() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// UIManager.setLookAndFeel("com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

	}



	private static String currentCommand = "";

	private static void updateBannedButtonAction() {
		readBanList();
	}

	public static boolean isBannedOrIpBannedName(String name) {
		for (int index = 0; index < Blacklist.blacklistedAccounts.size(); index++) {
			if (name.equalsIgnoreCase(Blacklist.blacklistedAccounts.get(index))) {
				return true;
			}
		}
		for (int index = 0; index < Ban.bannedList.size(); index++) {
			if (name.equalsIgnoreCase(Ban.bannedList.get(index))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isIpBannedName(String name) {
		for (int index = 0; index < Blacklist.blacklistedAccounts.size(); index++) {
			if (name.equalsIgnoreCase(Blacklist.blacklistedAccounts.get(index))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Print out all the alts and wealth of the given name.
	 */
	private static void grabAlts(String name, String forceIpSearchOnly, String forceUidSearchOnly) {
		altNames.clear();
		boolean searchAccountOnly = forceIpSearchOnly.isEmpty() && forceUidSearchOnly.isEmpty();
		if (!FileUtility.accountExists(name) && searchAccountOnly) {
			updateOutputText("The account '" + name + "' does not exist!", Color.BLACK, true);
			scanOngoing = false;
			return;
		}
		readBanList();
		EconomyScan.loadStartupFiles(true);

		String ip = "NOT DEFINED IP ADDRESS";
		String uid = "NOT DEFINED UID ADDRESS";
		if (searchAccountOnly) {
			ip = Misc.getOfflineIpAddress(name);
			uid = Misc.getOfflineUid(name);
		}
		else if (!forceIpSearchOnly.isEmpty()) {
			ip = forceIpSearchOnly.substring(5);
		} else if (!forceUidSearchOnly.isEmpty()) {
			uid = forceUidSearchOnly.substring(6);
		}
		setIpAddressText(ip);
		setUidFormattedText(Misc.formatUid(uid));
		setUidRawText(uid);

		getTotalCharacters();
		setStatusText("Accounts scanned: 0/" + getTotalCharacters());
		scanAllCharacters(ip, uid, "GRAB ALTS");

	}

	private static void readBanList() {
		Ban.bannedList.clear();
		Blacklist.loadBlacklistedData();
		Ban.readBanLog();
	}

	/**
	 * @return Total number of character files.
	 */
	public static int getTotalCharacters() {
		final File folder = new File(ServerConstants.getCharacterLocationWithoutLastSlash());
		return folder.listFiles().length;
	}

	public static Timer scanBankTimer = new Timer();

	public static TimerTask scanBankTask = new TimerTask() {
		@Override
		public void run() {
			threadLoop();
		}
	};

	/**
	 * Scan all character files by opening 1 thread per character, so a-z is already 20+ threads.
	 */
	private static void scanAllCharacters(String ip, String uid, String action) {
		for (int index = -1; index < ALPHABET.length; index++) {
			scanThreadsActive++;
			startNewScanThread(ip, uid, index, action);
		}

	}

	public final static String[] ALPHABET =
			{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};


	/**
	 * Add text to the output area.
	 */
	public static void updateOutputText(String text, Color colour, boolean newLine) {
		appendToPane(textPane, text + (newLine ? "\n" : ""), colour);
	}

	private static void appendToPane(JTextPane tp, String msg, Color c) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Arial");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		int len = tp.getDocument().getLength();
		tp.setCaretPosition(len);
		tp.setCharacterAttributes(aset, false);
		tp.replaceSelection(msg);
	}

	/**
	 * Set the error prints to a text file.
	 */
	private static void redirectErrorOutput() {
		try {
			FileOutputStream file = new FileOutputStream("alt_scanner_error.txt");
			Logger logger = new Logger(file, System.out);
			System.setErr(logger);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Change the Command text.
	 */
	private static void setCommandText(String text) {
		commandOutput.setText(text);
	}

	/**
	 * Change the Status text.
	 */
	public static void setStatusText(String text) {
		statusOutput.setText(text);
	}

	/**
	 * Change the IP Address text.
	 */
	private static void setIpAddressText(String text) {
		ipAddressOutput.setText(text);
	}

	/**
	 * Change the Uid formatted text.
	 */
	private static void setUidFormattedText(String text) {
		uidFormattedOutput.setText(text);
	}

	/**
	 * Change the Uid raw text.
	 */
	private static void setUidRawText(String text) {
		uidRawOutput.setText(text);
	}

	/**
	 * Timer instance.
	 */
	public static Timer timer = new Timer();

	/**
	 * Execute this task on a seperate thread every 0.5 seconds.
	 */
	public static TimerTask myTask = new TimerTask() {
		@Override
		public void run() {
			processErrorSystem();
		}
	};

	private static final JButton updateBannedButton = new JButton("Update banned");

	private static JTextPane textPane = new JTextPane();

	/**
	 * Check weather error file size changed to notify client. Executed every 0.5 seconds.
	 */
	private static void processErrorSystem() {
		if (getErrorFileSize() != errorFileSize) {
			JOptionPane.showMessageDialog(frame, "Check alt_scanner_error.txt for a new error.", "Error!", JOptionPane.OK_OPTION);
		}
		updateErrorFileSize();

	}

	/**
	 * Update the error file size variable.
	 */
	private static void updateErrorFileSize() {
		errorFileSize = getErrorFileSize();
	}

	/**
	 * @return The size of the error file.
	 */
	private static long getErrorFileSize() {
		File location = new File("alt_scanner_error.txt");
		return location.length();
	}



	/**
	 * Execute the command entered.
	 */
	private static void executeCommand(String command) {
		command = command.toLowerCase();
		if (command.isEmpty()) {
			return;
		}
		if (command.startsWith(" ")) {
			commandInputArea.setText("");
			return;
		}
		if (scanOngoing) {
			return;
		}

		// Reset text.
		textPane.setText("");
		commandInputArea.setText("");
		setStatusText("");
		setIpAddressText("");
		setUidFormattedText("");
		setUidRawText("");
		flaggedPlayers.clear();
		altData.clear();
		totalScans = 0;
		EconomyScan.lastTimeDonated = 0;
		EconomyScan.lastDonationName = "";


		currentCommand = command;
		scanOngoing = true;
		executeCommandStage1(command);

		// Update text.
		setCommandText(command);

	}



	/**
	 * This will execute every 0.25 seconds. This will check when the scanning for alts threads are complete, then it will execute the bank wealth checks.
	 */
	private static void threadLoop() {
		if (scanThreadsActive != 0) {
			return;
		}
		if (!scanOngoing) {
			return;
		}
		if (!scanThreadOnline) {
			return;
		}
		executeFinalToolAction();
		scrollPane.getVerticalScrollBar().setValue(0);
		scanOngoing = false;
		scanThreadOnline = false;

	}

	/**
	 * Stage 1 of a command.
	 */
	private static void executeCommandStage1(String command) {

		if (command.startsWith("::ip")) {
			grabAlts(command, command, "");
			return;
		}
		if (command.startsWith("::uid")) {
			grabAlts(command, "", command);
			return;
		}

		switch (command) {
			case "::donatorrankstotal":
				scanAllCharacters("", "", "");
				break;

			case "::donatorspentorsold":
				scanAllCharacters("", "", "");
				break;

			case "::commands":
				DawntainedMultiTool.updateOutputText("::donatorrankstotal", Color.BLACK, true);
				DawntainedMultiTool.updateOutputText("::donatorspentorsold", Color.BLACK, true);
				DawntainedMultiTool.updateOutputText("::referral", Color.BLACK, true);
				DawntainedMultiTool.updateOutputText("::trade", Color.BLACK, true);
				scanOngoing = false;
				break;

			case "::trade":
				if (altNames.isEmpty()) {
					DawntainedMultiTool.updateOutputText("Alt names is empty.", Color.BLACK, true);
					scanOngoing = false;
				} else {
					TradeHistoryScan.tradeHistoryScan();
				}
				break;

			default:
				grabAlts(command, "", "");
				break;
		}

	}

	/**
	 * Stage 2 which is scanning characters files on a multi-threaded system.
	 */
	private static void startNewScanThread(String ip, String uid, int index, String action) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (action.equals("GRAB ALTS")) {
					flaggedPlayers.addAll(PlayerAltScan.grabAltsStage2ScanCharacters(ip, uid, index));
				} else if (currentCommand.equals("::donatorrankstotal")) {
					DonatorRankScan.scanDonatorsAmounts(index);
				} else if (currentCommand.equals("::donatorspentorsold")) {
					DonatorTokensScan.scanDonatorSpendOrSoldAmount(index);
				}
			}
		}).start();

	}

	/**
	 * Stage 3 is executed after multi-threaded scanning is finished.
	 */
	private static void executeFinalToolAction() {
		if (currentCommand.equals("::donatorrankstotal")) {
			DonatorRankScan.scanDonatorAmountsEnd();
		} else if (currentCommand.equals("::donatorspentorsold")) {
			DonatorTokensScan.scanDonatorSpendOrSoldAmountEnd();
		} else {
			PlayerAltScan.playerAltScanEnd();
		}

	}
}
