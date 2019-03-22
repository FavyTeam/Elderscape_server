package game.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.ServerConstants;
import game.container.ItemContainer;
import game.container.ItemContainerFromStringDecoder;
import game.container.ItemContainerToStringEncoder;
import game.content.miscellaneous.GameTimeSpent;
import game.entity.attributes.AttributeKey;
import game.entity.attributes.AttributeSerializationType;
import game.entity.attributes.PermanentAttributeKeyManager;
import game.player.pet.PlayerPetState;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.IllegalBlockSizeException;
import utility.AESencrp;
import utility.Misc;

public class PlayerSave {

	private static final Gson gson = new GsonBuilder().create();

	public static int loadGame(Player player, String playerName, String playerPass, boolean bot) {
		String line = "";
		String token = "";
		String token2 = "";
		String[] token3 = new String[3];
		String string = "";
		String[] parse = null;
		boolean EndOfFile = false;
		int ReadMode = 0;
		BufferedReader characterfile = null;
		boolean File1 = false;
		try {
			characterfile = new BufferedReader(new FileReader((bot ? "./backup/characters/bots/" : ServerConstants.getCharacterLocation()) + playerName.toLowerCase() + ".txt"));
			File1 = true;
		} catch (FileNotFoundException e) {
			// Not needed or every new player will print this error.
		}
		if (!File1) {
			return 0;
		}
		try {
			line = characterfile.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			Misc.print(playerName + ": error loading file.");
			try {
				characterfile.close();
			} catch (IOException a) {
				a.printStackTrace();
			}
			return 3;
		}
		while (EndOfFile == false && line != null) {
			line = line.trim();
			int spot = line.indexOf("=");
			if (spot > -1) {
				token = line.substring(0, spot);
				token = token.trim();
				token2 = line.substring(spot + 1);
				token2 = token2.trim();
				token3 = token2.split("\t");
				switch (ReadMode) {
					case 1:
						if (token.equals("Username")) {
							player.setPlayerName(token2);
						} else if (token.equals("Password")) {
							try {
								if (playerPass.equalsIgnoreCase(AESencrp.decrypt(token2))) {
									playerPass = AESencrp.decrypt(token2);
								} else {
									try {
										characterfile.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
									return 3;
								}
							} catch (IllegalBlockSizeException e) {
								e.printStackTrace();
								Misc.print("Wrong password exception1.");
								try {
									characterfile.close();
								} catch (IOException a) {
									a.printStackTrace();
								}
								return 3;
							} catch (Exception e) {
								e.printStackTrace();
								Misc.print("Wrong password exception2.");
								try {
									characterfile.close();
								} catch (IOException b) {
									b.printStackTrace();
								}
								return 3;
							}
						}
						break;
					case 2:
						AttributeKey<?> attributeForToken = PermanentAttributeKeyManager.getSingleton().getKey(token);

						if (attributeForToken != null) {
							try {
								Object decoded = gson.fromJson(token2, attributeForToken.defaultValue().getClass());

								if (decoded != null) {
									player.getAttributes().put(attributeForToken, decoded);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (token.equals("height")) {
							player.setHeight(Integer.parseInt(token2));
						} else if (token.equals("x")) {
							player.teleportToX = (Integer.parseInt(token2) <= 0 ? 3087 : Integer.parseInt(token2));
						} else if (token.equals("y")) {
							player.teleportToY = (Integer.parseInt(token2) <= 0 ? 3510 : Integer.parseInt(token2));
						} else if (token.equals("authority")) {
							player.playerRights = Integer.parseInt(token2);
						} else if (token.equals("lastSavedIpAddress")) {
							player.lastSavedIpAddress = token2;
						} else if (token.equals("addressMac")) {
							player.addressMac = token2;
						} else if (token.equals("addressUid")) {
							player.addressUid = token2;
						} else if (token.equals("skullTimer")) {
							player.skullTimer = Integer.parseInt(token2);
						} else if (token.equals("timeSkilled")) {
							player.timeSkilled = Long.parseLong(token2);
						} else if (token.equals("timeUsedHealthBoxSpecial")) {
							player.timeUsedHealthBoxSpecial = Long.parseLong(token2);
						} else if (token.equals("timeVoteNotificationAlerted")) {
							player.timeVoteNotificationAlerted = Long.parseLong(token2);
						} else if (token.equals("accountOfferNotificationPopUpTime")) {
							player.accountOfferNotificationPopUpTime = Long.parseLong(token2);
						} else if (token.equals("timeAccountOfferShown")) {
							player.timeAccountOfferShown = Long.parseLong(token2);
						} else if (token.equals("accountOffersSkippedStreak")) {
							player.accountOffersSkippedStreak = Integer.parseInt(token2);
						} else if (token.equals("accountOfferClaimTargetGoal")) {
							player.accountOfferClaimTargetGoal = Integer.parseInt(token2);
						} else if (token.equals("accountOfferClaimTargetProgress")) {
							player.accountOfferClaimTargetProgress = Double.parseDouble(token2);
						} else if (token.equals("accountOfferRewardItemAmount")) {
							player.accountOfferRewardItemAmount = Integer.parseInt(token2);
						} else if (token.equals("accountOfferRewardItemId")) {
							player.accountOfferRewardItemId = Integer.parseInt(token2);
						} else if (token.equals("tilesWalked")) {
							player.tilesWalked = Integer.parseInt(token2);
						} else if (token.equals("spellBook")) {
							player.spellBook = token2;
						} else if (token.equals("bankPin")) {
							player.bankPin = token2;
						} else if (token.equals("setPin")) {
							player.setPin = Boolean.parseBoolean(token2);
						} else if (token.equals("jailed")) {
							player.setJailed(Boolean.parseBoolean(token2));
						} else if (token.equals("chatEffects")) {
							player.chatEffects = Boolean.parseBoolean(token2);
						} else if (token.equals("tutorialComplete")) {
							player.setTutorialComplete(Boolean.parseBoolean(token2));
						} else if (token.equals("wildernessKills")) {
							player.setWildernessKills(Integer.parseInt(token2));
						} else if (token.equals("wildernessDeaths")) {
							player.setWildernessDeaths(Short.parseShort(token2));
						} else if (token.equals("safeKills")) {
							player.safeKills = Integer.parseInt(token2);
						} else if (token.equals("safeDeaths")) {
							player.safeDeaths = Integer.parseInt(token2);
						} else if (token.equals("yellMutes")) {
							player.yellMutes = Integer.parseInt(token2);
						} else if (token.equals("specialAttackAmount")) {
							player.setSpecialAttackAmount(Double.parseDouble(token2), false);
						} else if (token.equals("teleBlockEndTime")) {
							player.teleBlockEndTime = Long.parseLong(token2);
						} else if (token.equals("splitChat")) {
							player.splitChat = Boolean.parseBoolean(token2);
						} else if (token.equals("foodAte")) {
							player.foodAte = Integer.parseInt(token2);

						} else if (token.equals("summon-duration")) {
							player.getSummoning().setDuration(Integer.parseInt(token2));
						} else if (token.equals("logOutTime")) {
							player.logOutTime = Long.parseLong(token2);
						} else if (token.equals("chargeSpellTime")) {
							player.chargeSpellTime = Long.parseLong(token2);
						}
						else if (token.equals("imbuedHeartTime")) {
							player.imbuedHeartEndTime = Long.parseLong(token2);
						} else if (token.equals("timeLastClaimedDonation")) {
							player.setTimeLastClaimedDonation(Long.parseLong(token2));
						} else if (token.equals("yellTag")) {
							player.yellTag = token2;
						} else if (token.equals("biographyLine1")) {
							player.biographyLine1 = token2;
						} else if (token.equals("biographyLine2")) {
							player.biographyLine2 = token2;
						} else if (token.equals("biographyLine3")) {
							player.biographyLine3 = token2;
						} else if (token.equals("biographyLine4")) {
							player.biographyLine4 = token2;
						} else if (token.equals("biographyLine5")) {
							player.biographyLine5 = token2;
						} else if (token.equals("switches")) {
							player.switches = Integer.parseInt(token2);
						} else if (token.equals("potionDrank")) {
							player.potionDrank = Integer.parseInt(token2);
						} else if (token.equals("duelArenaStakes")) {
							player.duelArenaStakes = Integer.parseInt(token2);
						} else if (token.equals("tradesCompleted")) {
							player.tradesCompleted = Integer.parseInt(token2);
						} else if (token.equals("meleeTournamentsWon1")) {
							player.meleeTournamentsWon1 = Integer.parseInt(token2);
						} else if (token.equals("tribridTournamentsWon1")) {
							player.tribridTournamentsWon1 = Integer.parseInt(token2);
						} else if (token.equals("hybridTournamentsWon1")) {
							player.hybridTournamentsWon1 = Integer.parseInt(token2);
						} else if (token.equals("xpLock")) {
							player.xpLock = Boolean.parseBoolean(token2);
						} else if (token.equals("teleportsUsed")) {
							player.teleportsUsed = Integer.parseInt(token2);
						} else if (token.equals("sqlIndex")) {
							player.setSqlIndex(Integer.parseInt(token2));
						} else if (token.equals("barrowsPersonalRecord")) {
							player.barrowsPersonalRecord = Integer.parseInt(token2);
						} else if (token.equals("barrowsRunCompleted")) {
							player.setBarrowsRunCompleted(Integer.parseInt(token2));
						} else if (token.equals("poisonDamage")) {
							player.poisonDamage = Integer.parseInt(token2);
						} else if (token.equals("poisonTicksUntillDamage")) {
							player.poisonTicksUntillDamage = Integer.parseInt(token2);
						} else if (token.equals("poisonHitsplatsLeft")) {
							player.poisonHitsplatsLeft = Integer.parseInt(token2);
						} else if (token.equals("currentKillStreak")) {
							player.currentKillStreak = Integer.parseInt(token2);
						} else if (token.equals("killStreaksRecord")) {
							player.killStreaksRecord = Integer.parseInt(token2);
						} else if (token.equals("playerTitle")) {
							player.playerTitle = token2;
						} else if (token.equals("titleColour")) {
							player.titleColour = token2;
						} else if (token.equals("titleSwap")) {
							player.titleSwap = Integer.parseInt(token2);
						} else if (token.equals("petID")) {
							player.setPetId(Integer.parseInt(token2));
						} else if (token.equals("petSummoned")) {
							player.setPetSummoned(Boolean.parseBoolean(token2));
						} else if (token.equals("passwordChangeAlertedComplete1")) {
							player.passwordChangeAlertedComplete = Boolean.parseBoolean(token2);
						} else if (token.equals("secondPetId")) {
							player.setSecondPetId(Integer.parseInt(token2));
						} else if (token.equals("secondPetSummoned")) {
							player.setSecondPetSummoned(Boolean.parseBoolean(token2));
						} else if (token.equals("profilePrivacyOn")) {
							player.profilePrivacyOn = Boolean.parseBoolean(token2);
						} else if (token.equals("tank")) {
							player.setTank(Boolean.parseBoolean(token2));
						} else if (token.equals("whiteSkull")) {
							player.setWhiteSkull(Boolean.parseBoolean(token2));
						} else if (token.equals("redSkull")) {
							player.setRedSkull(Boolean.parseBoolean(token2));
						} else if (token.equals("infernalAndMaxCapesUnlockedScrollConsumed")) {
							player.setInfernalAndMaxCapesUnlockedScrollConsumed(Boolean.parseBoolean(token2));
						} else if (token.equals("canClaimPvpTaskReward")) {
							player.canClaimPvpTaskReward = Boolean.parseBoolean(token2);
						} else if (token.equals("pvpTasksCompleted")) {
							player.pvpTasksCompleted = Integer.parseInt(token2);
						} else if (token.equals("customPetPoints")) {
							player.setCustomPetPoints(Integer.parseInt(token2));
						} else if (token.equals("pvpTaskSize")) {
							player.pvpTaskSize = Integer.parseInt(token2);
						} else if (token.equals("headIconPk")) {
							player.headIconPk = Integer.parseInt(token2);
						} else if (token.equals("voteTotalPoints")) {
							player.voteTotalPoints = Integer.parseInt(token2);
						} else if (token.equals("toolUses")) {
							player.setToolUses(Integer.parseInt(token2));
						} else if (token.equals("qp")) {
							player.setQuestPoints(Integer.parseInt(token2));
						} else if (token.equals("randomCoffin")) {
							player.randomCoffin = Integer.parseInt(token2);
						} else if (token.equals("autoRetaliate")) {
							player.setAutoRetaliate(Integer.parseInt(token2));
						} else if (token.equals("runModeOn")) {
							player.runModeOn = Boolean.parseBoolean(token2);
						} else if (token.equals("accountOfferCompleted")) {
							player.accountOfferCompleted = Boolean.parseBoolean(token2);
						} else if (token.equals("autoBuyBack")) {
							player.autoBuyBack = Boolean.parseBoolean(token2);
						} else if (token.equals("bossKillCountMessage")) {
							player.bossKillCountMessage = Boolean.parseBoolean(token2);
						} else if (token.equals("displayBots")) {
							player.displayBots = Boolean.parseBoolean(token2);
						} else if (token.equals("smashVials")) {
							player.smashVials = Boolean.parseBoolean(token2);
						} else if (token.equals("toggleSeedPod")) {
							player.toggleSeedPod = Boolean.parseBoolean(token2);
						} else if (token.equals("toggleLootKey")) {
							player.toggleLootKey = Boolean.parseBoolean(token2);
						} else if (token.equals("ancientsInterfaceType")) {
							player.ancientsInterfaceType = Integer.parseInt(token2);
						} else if (token.equals("combatStyle")) {
							player.setCombatStyle(Integer.parseInt(token2));
						} else if (token.equals("highestRfdWave")) {
							player.highestRfdWave = Integer.parseInt(token2);
						} else if (token.equals("runEnergy")) {
							player.runEnergy = Double.parseDouble(token2);
						} else if (token.equals("totalPaymentAmount")) {
							player.totalPaymentAmount = Double.parseDouble(token2);
						} else if (token.equals("playerBotKills")) {
							player.playerBotKills = Integer.parseInt(token2);
						} else if (token.equals("playerBotDeaths")) {
							player.playerBotDeaths = Integer.parseInt(token2);
						} else if (token.equals("donatorTokensReceived")) {
							player.donatorTokensReceived = Integer.parseInt(token2);
						} else if (token.equals("playerBotHighestKillstreak")) {
							player.playerBotHighestKillstreak = Integer.parseInt(token2);
						} else if (token.equals("lastXAmount")) {
							player.lastXAmount = Integer.parseInt(token2);
						} else if (token.equals("valuableLoot")) {
							player.valuableLoot = Integer.parseInt(token2);
						} else if (token.equals("votesClaimed")) {
							player.votesClaimed = Integer.parseInt(token2);
						} else if (token.equals("venomDamage")) {
							player.venomDamage = Integer.parseInt(token2);
						} else if (token.equals("venomHits")) {
							player.venomHits = Integer.parseInt(token2);
						} else if (token.equals("venomTicksUntillDamage")) {
							player.venomTicksUntillDamage = Integer.parseInt(token2);
						} else if (token.equals("bloodMoneySpent")) {
							player.bloodMoneySpent = Integer.parseInt(token2);
						} else if (token.equals("playerBotCurrentKillstreak")) {
							player.playerBotCurrentKillstreak = Integer.parseInt(token2);
						} else if (token.equals("timeUsedPreset")) {
							player.timeUsedPreset = Long.parseLong(token2);
						} else if (token.equals("timeEarnedBloodMoneyInResourceWild")) {
							player.setTimeEarnedBloodMoneyInResourceWild(Long.parseLong(token2));
						} else if (token.equals("spellbookSwapUsedOnSameDateAmount")) {
							player.setSpellbookSwapUsedOnSameDateAmount(Integer.parseInt(token2));
						} else if (token.equals("dateUsedSpellbookSwap")) {
							player.setDateUsedSpellbookSwap(token2);
						} else if (token.equals("compColor1")) {
							player.compColor1 = Integer.parseInt(token2);
						} else if (token.equals("compColor2")) {
							player.compColor2 = Integer.parseInt(token2);
						} else if (token.equals("compColor3")) {
							player.compColor3 = Integer.parseInt(token2);
						} else if (token.equals("slayerPoints")) {
							player.setSlayerPoints(Integer.parseInt(token2));
						} else if (token.equals("compColor4")) {
							player.compColor4 = Integer.parseInt(token2);
						} else if (token.equals("yellMuteExpireTime")) {
							player.yellMuteExpireTime = Long.parseLong(token2);
						} else if (token.equals("xpBonusEndTime")) {
							player.setXpBonusEndTime(Long.parseLong(token2));
						} else if (token.equals("timeDiedInWilderness")) {
							player.timeDiedInWilderness = Long.parseLong(token2);
						} else if (token.equals("timeWeeklyGameTimeUsed")) {
							player.timeWeeklyGameTimeUsed = Long.parseLong(token2);
						} else if (token.equals("timeVoted")) {
							player.timeVoted = Long.parseLong(token2);
						} else if (token.equals("xpLampUsedTime")) {
							player.xpLampUsedTime = Long.parseLong(token2);
						} else if (token.equals("venomImmunityExpireTime")) {
							player.venomImmunityExpireTime = Long.parseLong(token2);
						} else if (token.equals("accountDateCreated")) {
							player.accountDateCreated = token2;
						} else if (token.equals("secondsBeenOnline")) {
							player.secondsBeenOnline = Integer.parseInt(token2);
						} else if (token.equals("meleeMainKills")) {
							player.setMeleeMainKills(Integer.parseInt(token2));
						} else if (token.equals("hybridKills")) {
							player.setHybridKills(Integer.parseInt(token2));
						} else if (token.equals("berserkerPureKills")) {
							player.setBerserkerPureKills(Integer.parseInt(token2));
						} else if (token.equals("pureKills")) {
							player.setPureKills(Integer.parseInt(token2));
						} else if (token.equals("rangedTankKills")) {
							player.setRangedTankKills(Integer.parseInt(token2));
						} else if (token.equals("f2pKills")) {
							player.setF2pKills(Integer.parseInt(token2));
						} else if (token.equals("killsInMulti")) {
							player.killsInMulti = Integer.parseInt(token2);
						} else if (token.equals("lastTeleport")) {
							player.lastTeleport = token2;
						} else if (token.equals("wildernessKillsReset")) {
							player.wildernessKillsReset = Integer.parseInt(token2);
						} else if (token.equals("wildernessDeathsReset")) {
							player.wildernessDeathsReset = Integer.parseInt(token2);
						} else if (token.equals("targetsKilled")) {
							player.targetsKilled = Integer.parseInt(token2);
						} else if (token.equals("targetDeaths")) {
							player.targetDeaths = Integer.parseInt(token2);
						} else if (token.equals("money-pouch")) {
							int coins = Integer.parseInt(token2);
							player.getMoneyPouch().setCoinsToAdd(coins);
						} else if (token.equals("targetActivityPoints")) {
							player.targetActivityPoints = Integer.parseInt(token2);
						} else if (token.equals("recoilCharges")) {
							player.setRecoilCharges(Integer.parseInt(token2));
						} else if (token.equals("braceletCharges")) {
							player.braceletCharges = Integer.parseInt(token2);
						} else if (token.equals("xpBarShowType")) {
							player.xpBarShowType = token2;
						} else if (token.equals("lastClanChatJoined")) {
							player.lastClanChatJoined = token2;
						} else if (token.equals("yellMode")) {
							player.yellMode = token2;
						} else if (token.equals("originalIp")) {
							player.originalIp = token2;
						} else if (token.equals("originalUid")) {
							player.originalUid = token2;
						} else if (token.equals("originalMac")) {
							player.originalMac = token2;
						} else if (token.equals("gameMode")) {
							player.setGameMode(token2);
						} else if (token.equals("difficultyChosen")) {
							player.setDifficultyChosen(token2);
						} else if (token.equals("blowpipeDartItemId")) {
							player.blowpipeDartItemId = Integer.parseInt(token2);
						} else if (token.equals("blowpipeDartItemAmount")) {
							player.blowpipeDartItemAmount = Integer.parseInt(token2);
						} else if (token.equals("blowpipeCharges")) {
							player.setBlowpipeCharges(Integer.parseInt(token2));
						} else if (token.equals("teleportWarning")) {
							player.teleportWarning = Boolean.parseBoolean(token2);
						} else if (token.equals("playerIsLeakedSourceClean")) {
							player.playerIsLeakedSourceClean = Boolean.parseBoolean(token2);
						} else if (token.equals("ableToEditCombat")) {
							player.setAbleToEditCombat(Boolean.parseBoolean(token2));
						} else if (token.equals("killScreenshots")) {
							player.killScreenshots = Boolean.parseBoolean(token2);
						} else if (token.equals("rigourUnlocked")) {
							player.rigourUnlocked = Boolean.parseBoolean(token2);
						} else if (token.equals("auguryUnlocked")) {
							player.auguryUnlocked = Boolean.parseBoolean(token2);
						} else if (token.equals("announceMaxLevel")) {
							player.announceMaxLevel = Boolean.parseBoolean(token2);
						} else if (token.equals("bloodKeysCollected")) {
							player.bloodKeysCollected = Integer.parseInt(token2);
						} else if (token.equals("snowBallsLandedOnMe")) {
							player.snowBallsLandedOnMe = Integer.parseInt(token2);
						} else if (token.equals("snowBallsThrownAtMe")) {
							player.snowBallsThrownAtMe = Integer.parseInt(token2);
						} else if (token.equals("throneId")) {
							player.throneId = Integer.parseInt(token2);
						} else if (token.equals("bossScoreCapped")) {
							player.bossScoreCapped = Integer.parseInt(token2);
						} else if (token.equals("bossScoreUnCapped")) {
							player.bossScoreUnCapped = Integer.parseInt(token2);
						} else if (token.equals("myTabs")) {
							player.myTabs = Integer.parseInt(token2);
						} else if (token.equals("enemyTabs")) {
							player.enemyTabs = Integer.parseInt(token2);
						} else if (token.equals("barragesCasted")) {
							player.barragesCasted = Integer.parseInt(token2);
						} else if (token.equals("randomEventIncorrectTries")) {
							player.randomEventIncorrectTries = Integer.parseInt(token2);
						} else if (token.equals("timeOfAccountCreation")) {
							player.timeOfAccountCreation = Long.parseLong(token2);
						} else if (token.equals("timeCraftedRunes")) {
							player.timeCraftedRunes = Long.parseLong(token2);
						} else if (token.equals("timeLoggedOff")) {
							player.timeLoggedOff = Long.parseLong(token2);
						} else if (token.equals("meritPoints")) {
							player.setMeritPoints(Integer.parseInt(token2));
						} else if (token.equals("deathsToNpc")) {
							player.deathsToNpc = Integer.parseInt(token2);
						} else if (token.equals("privateChat")) {
							player.privateChat = Integer.parseInt(token2);
						} else if (token.equals("publicChatMode")) {
							player.publicChatMode = Integer.parseInt(token2);
						} else if (token.equals("tradeChatMode")) {
							player.tradeChatMode = Integer.parseInt(token2);
						} else if (token.equals("donatorTokensRankUsed")) {
							player.donatorTokensRankUsed = Integer.parseInt(token2);
						} else if (token.equals("agilityPoints")) {
							player.setAgilityPoints(Integer.parseInt(token2));
						} else if (token.equals("antiFirePotionTimer")) {
							player.setAntiFirePotionTimer(Integer.parseInt(token2));
						} else if (token.equals("staminaPotionTimer")) {
							player.setStaminaPotionTimer(Integer.parseInt(token2));
						} else if (token.equals("slayerTaskNpcType")) {
							player.slayerTaskNpcType = Integer.parseInt(token2);
						} else if (token.equals("slayerTaskNpcAmount")) {
							player.slayerTaskNpcAmount = Integer.parseInt(token2);
						} else if (token.equals("overloadReboostTicks")) {
							player.overloadReboostTicks = Integer.parseInt(token2);
						} else if (token.equals("timeAcceptedTradeInDiceZone")) {
							player.timeAcceptedTradeInDiceZone = Long.parseLong(token2);
						} else if (token.equals("zulrah-lost-items")) {
							ItemContainer container = new ItemContainerFromStringDecoder().decode(token2);

							if (container != null) {
								container.forNonNull((index, item) -> player.getZulrahLostItems().add(item));
							}
						} else if (token.equals("vorkath-lost-items")) {
							ItemContainer container = new ItemContainerFromStringDecoder().decode(token2);

							if (container != null) {
								container.forNonNull((index, item) -> player.getVorkathLostItems().add(item));
							}
						} else if (token.equals("pvpTask")) {
							for (int j = 0; j < token3.length; j++) {
								player.pvpTask[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("pouchesPure")) {
							for (int j = 0; j < token3.length; j++) {
								player.pouchesPure[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("pouchesRune")) {
							for (int j = 0; j < token3.length; j++) {
								player.pouchesRune[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("boostedTime")) {
							for (int j = 0; j < token3.length; j++) {
								player.boostedTime[j] = Long.parseLong(token3[j]);
							}
						} else if (token.equals("timeSpent")) {
							for (int j = 0; j < token3.length; j++) {
								player.timeSpent[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("maximumSpecialAttack")) {
							for (int j = 0; j < token3.length; j++) {
								player.maximumSpecialAttack[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("maximumSpecialAttackNpc")) {
							for (int j = 0; j < token3.length; j++) {
								player.maximumSpecialAttackNpc[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("weaponAmountUsed")) {
							for (int j = 0; j < token3.length; j++) {
								player.weaponAmountUsed[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("weaponAmountUsedNpc")) {
							for (int j = 0; j < token3.length; j++) {
								player.weaponAmountUsedNpc[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("runePouchItemId")) {
							for (int j = 0; j < token3.length; j++) {
								player.runePouchItemId[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("runePouchItemAmount")) {
							for (int j = 0; j < token3.length; j++) {
								player.runePouchItemAmount[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("totalDamage")) {
							for (int j = 0; j < token3.length; j++) {
								player.totalDamage[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("QuickPrayers")) {
							for (int j = 0; j < token3.length; j++) {
								player.quickPrayers[j] = Boolean.parseBoolean(token3[j]);
							}
						} else if (token.equals("deathTypes")) {
							for (int j = 0; j < token3.length; j++) {
								player.deathTypes[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("skillingStatistics")) {
							for (int j = 0; j < token3.length; j++) {
								player.skillingStatistics[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("skillMilestone100mAnnounced")) {
							for (int j = 0; j < token3.length; j++) {
								player.skillMilestone100mAnnounced[j] = Boolean.parseBoolean(token3[j]);
							}
						} else if (token.equals("skillMilestone200mAnnounced")) {
							for (int j = 0; j < token3.length; j++) {
								player.skillMilestone200mAnnounced[j] = Boolean.parseBoolean(token3[j]);
							}
						} else if (token.equals("barrowsBrothersKilled")) {
							for (int j = 0; j < token3.length; j++) {
								player.barrowsBrothersKilled[j] = Boolean.parseBoolean(token3[j]);
							}
						} else if (token.equals("achievementDifficultyCompleted")) {
							for (int j = 0; j < token3.length; j++) {
								player.achievementDifficultyCompleted[j] = Boolean.parseBoolean(token3[j]);
							}
						} else if (token.equals("lootingBagStorageItemId")) {
							for (int j = 0; j < token3.length; j++) {
								player.lootingBagStorageItemId[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("achievementTotal")) {
							for (int j = 0; j < token3.length; j++) {
								player.achievementTotal[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("titleTotal")) {
							for (int j = 0; j < token3.length; j++) {
								player.titleTotal[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("questStages")) {
							for (int j = 0; j < token3.length; j++) {
								player.questStages[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("lootingBagStorageItemAmount")) {
							for (int j = 0; j < token3.length; j++) {
								player.lootingBagStorageItemAmount[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("combatExperienceGainedAfterMaxed")) {
							for (int j = 0; j < token3.length; j++) {
								player.combatExperienceGainedAfterMaxed[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("currentCombatSkillLevel")) {
							for (int j = 0; j < token3.length; j++) {
								player.currentCombatSkillLevel[j] = Integer.parseInt(token3[j]);
							}
						} else if (token.equals("pvpBlacklist")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.pvpBlacklist.add(token3[j]);
								}
							}
						} else if (token.equals("achievementProgress")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.achievementProgress.add(token3[j]);
								}
							}
						} else if (token.equals("killTimes")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.killTimes.add(token3[j]);
								}
							}
						} else if (token.equals("voteTimes")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.voteTimes.add(token3[j]);
								}
							}
						} else if (token.equals("gameTimeHistory")) {
							for (int j = 0; j < token3.length; j++) {
								string = token3[j];
								if (!string.isEmpty()) {
									parse = string.split("#");
									player.activePlayedTimeDates.add(new GameTimeSpent(Integer.parseInt(parse[0]), parse[1]));
								}
							}
						} else if (token.equals("itemsToShop")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.itemsToShop.add(token3[j]);
								}
							}
						} else if (token.equals("resourcesHarvested")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.resourcesHarvested.add(token3[j]);
								}
							}
						} else if (token.equals("preset1")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.preset1.add(token3[j]);
								}
							}
						} else if (token.equals("preset2")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.preset2.add(token3[j]);
								}
							}
						} else if (token.equals("preset3")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.preset3.add(token3[j]);
								}
							}
						} else if (token.equals("preset4")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.preset4.add(token3[j]);
								}
							}
						} else if (token.equals("preset5")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.preset5.add(token3[j]);
								}
							}
						} else if (token.equals("preset6")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.preset6.add(token3[j]);
								}
							}
						} else if (token.equals("preset7")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.preset7.add(token3[j]);
								}
							}
						} else if (token.equals("preset8")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.preset8.add(token3[j]);
								}
							}
						} else if (token.equals("preset9")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.preset9.add(token3[j]);
								}
							}
						} else if (token.equals("itemsCollected")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.itemsCollected.add(Integer.parseInt(token3[j]));
								}
							}
						} else if (token.equals("achievementsCompleted")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.achievementsCompleted.add(token3[j]);
								}
							}
						} else if (token.equals("titlesUnlocked")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.titlesUnlocked.add(token3[j]);
								}
							}
						} else if (token.equals("npcKills")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.npcKills.add(token3[j]);
								}
							}
						} else if (token.equals("singularUntradeableItemsOwned")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.singularUntradeableItemsOwned.add(token3[j]);
								}
							}
						} else if (token.equals("tradingPostHistory")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.tradingPostHistory.add(token3[j]);
								}
							}
						} else if (token.equals("donationPriceClaimedHistory")) {
							for (int j = 0; j < token3.length; j++) {
								if (!token3[j].isEmpty()) {
									player.donationPriceClaimedHistory.add(token3[j]);
								}
							}
						} else if (token.equals("player-pet-state1")) {
							try {
								player.setPlayerPetState(new PlayerPetState(token2));
							} catch (Exception e) {

							}
						}
						break;

					case 3:
						if (token.equals("character-skill")) {
							player.baseSkillLevel[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
							player.skillExperience[Integer.parseInt(token3[0])] = Integer.parseInt(token3[2]);
						}
						break;

					case 4:
						if (token.equals("inventory-slot")) {
							player.playerItems[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
							player.playerItemsN[Integer.parseInt(token3[0])] = Integer.parseInt(token3[2]);
						}
						break;
					case 5:
						if (token.equals("character-equip")) {
							player.playerEquipment[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
							player.playerEquipmentN[Integer.parseInt(token3[0])] = Integer.parseInt(token3[2]);
						}
						break;
					case 6:
						if (token.equals("character-look")) {
							player.playerAppearance[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
						}
						break;

					case 7:
						int index = Integer.parseInt(token3[0]);

						int itemId = Integer.parseInt(token3[1]);

						int itemAmount = Integer.parseInt(token3[2]);

						if (itemId < 0 || itemId >= ServerConstants.MAX_ITEM_ID) {
							itemId = 0;
							itemAmount = 0;
						}

						if (token.equals("character-bank")) {
							player.bankItems[index] = itemId;
							player.bankItemsN[index] = itemAmount;
						} else if (token.equals("character-bank1")) {
							player.bankItems1[index] = itemId;
							player.bankItems1N[index] = itemAmount;
						} else if (token.equals("character-bank2")) {
							player.bankItems2[index] = itemId;
							player.bankItems2N[index] = itemAmount;
						} else if (token.equals("character-bank3")) {
							player.bankItems3[index] = itemId;
							player.bankItems3N[index] = itemAmount;
						} else if (token.equals("character-bank4")) {
							player.bankItems4[index] = itemId;
							player.bankItems4N[index] = itemAmount;
						} else if (token.equals("character-bank5")) {
							player.bankItems5[index] = itemId;
							player.bankItems5N[index] = itemAmount;
						} else if (token.equals("character-bank6")) {
							player.bankItems6[index] = itemId;
							player.bankItems6N[index] = itemAmount;
						} else if (token.equals("character-bank7")) {
							player.bankItems7[index] = itemId;
							player.bankItems7N[index] = itemAmount;
						} else if (token.equals("character-bank8")) {
							player.bankItems8[index] = itemId;
							player.bankItems8N[index] = itemAmount;
						}

						break;
					case 8:
						if (token.equals("character-friend")) {
							player.friends[Integer.parseInt(token3[0])][0] = Long.parseLong(token3[1]);
							if (token3.length >= 3) {
								player.friends[Integer.parseInt(token3[0])][1] = Long.parseLong(token3[2]);
							} else {
								player.friends[Integer.parseInt(token3[0])][1] = System.currentTimeMillis() - Misc.getHoursToMilliseconds(24);
							}
						}
						break;
					case 9:
						if (token.equals("character-ignore")) {
							player.ignores[Integer.parseInt(token3[0])] = Long.parseLong(token3[1]);
						}
						break;

					case 10:
						if (token.equals("character-temp-tournament-skill")) {
							player.baseSkillLevelStoredBeforeTournament[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
							player.skillExperienceStoredBeforeTournament[Integer.parseInt(token3[0])] = Integer.parseInt(token3[2]);
						}
						break;
				}
			} else {
				if (line.equals("[CREDENTIALS]")) {
					ReadMode = 1;
				} else if (line.equals("[MAIN]")) {
					ReadMode = 2;
				}
				// Do not change order of [skills] because Profile search will use less resources
				// if [skills] is closer to the top of the character file.
				else if (line.equals("[SKILLS]")) {
					ReadMode = 3;
				} else if (line.equals("[INVENTORY]")) {
					ReadMode = 4;
				} else if (line.equals("[EQUIPMENT]")) {
					ReadMode = 5;
				} else if (line.equals("[APPEARANCE]")) {
					ReadMode = 6;
				} else if (line.equals("[BANK]")) {
					ReadMode = 7;
				} else if (line.equals("[FRIENDS]")) {
					ReadMode = 8;
				} else if (line.equals("[IGNORES]")) {
					ReadMode = 9;
				} else if (line.equals("[TEMP TOURNAMENT SKILL]")) {
					ReadMode = 10;
				} else if (line.equals("[EOF]")) {
					try {
						characterfile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return 1;
				}
			}
			try {
				line = characterfile.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				EndOfFile = true;
			}
		}
		try {
			characterfile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 13;
	}


	public static void saveGame(Player player) {
		if (player.getPlayerName() == null) {
			return;
		}
		if (player.doNotSaveCharacterFile) {
			return;
		}
		if (player.bot) {
			return;
		}
		if (!player.saveFile || !player.saveCharacter) {
			return;
		}
		BufferedWriter characterfile = null;
		try {

			String string = "";
			String string1 = "";
			int integer = 0;
			Double doubleNumber = 0.0;
			boolean boolean1;
			long long1;

			FileWriter writer = new FileWriter(ServerConstants.getCharacterLocation() + player.getPlayerName().toLowerCase() + ".txt");
			characterfile = new BufferedWriter(writer);
			characterfile.write("[CREDENTIALS]", 0, 13);
			characterfile.newLine();
			characterfile.write("Username = ", 0, 11);
			characterfile.write(player.getPlayerName(), 0, player.getPlayerName().length());
			characterfile.newLine();
			characterfile.write("Password = ", 0, 11);
			try {
				characterfile.write(AESencrp.encrypt(player.playerPass), 0, AESencrp.encrypt(player.playerPass).length());
			} catch (Exception e) {
				e.printStackTrace();
			}
			characterfile.newLine();
			characterfile.newLine();
			characterfile.write("[MAIN]", 0, 6);
			characterfile.newLine();

			string = "lastSavedIpAddress = ";
			string1 = player.lastSavedIpAddress;
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "addressMac = ";
			string1 = player.addressMac;
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "addressUid = ";
			string1 = player.addressUid;
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "originalIp = ";
			string1 = player.originalIp;
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "originalUid = ";
			string1 = player.originalUid;
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "originalMac = ";
			string1 = player.originalMac;
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			characterfile.write("accountDateCreated = ", 0, 21);
			characterfile.write(player.accountDateCreated, 0, player.accountDateCreated.length());
			characterfile.newLine();

			characterfile.write("secondsBeenOnline = ", 0, 20);
			characterfile.write(Integer.toString(player.secondsBeenOnline), 0, Integer.toString(player.secondsBeenOnline).length());
			characterfile.newLine();

			characterfile.write("authority = ", 0, 12);
			characterfile.write(Integer.toString(player.playerRights), 0, Integer.toString(player.playerRights).length());
			characterfile.newLine();

			string = "donatorTokensRankUsed = ";
			integer = player.donatorTokensRankUsed;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			characterfile.newLine();
			characterfile.write("[OTHER]", 0, 7);
			characterfile.newLine();
			string = "totalPaymentAmount = ";
			doubleNumber = player.totalPaymentAmount;
			characterfile.write(string, 0, string.length());
			characterfile.write(Double.toString(doubleNumber), 0, Double.toString(doubleNumber).length());
			characterfile.newLine();
			string = "donatorTokensReceived = ";
			integer = player.donatorTokensReceived;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();
			characterfile.write("height = ", 0, 9);
			characterfile.write(Integer.toString(player.getHeight()), 0, Integer.toString(player.getHeight()).length());
			characterfile.newLine();
			characterfile.write("x = ", 0, 4);
			characterfile.write(Integer.toString(player.getX()), 0, Integer.toString(player.getX()).length());
			characterfile.newLine();
			characterfile.write("y = ", 0, 4);
			characterfile.write(Integer.toString(player.getY()), 0, Integer.toString(player.getY()).length());
			characterfile.newLine();
			characterfile.write("playerTitle = ", 0, 14);
			characterfile.write(player.playerTitle, 0, player.playerTitle.length());
			characterfile.newLine();
			characterfile.write("titleSwap = ", 0, 12);
			characterfile.write(Integer.toString(player.titleSwap), 0, Integer.toString(player.titleSwap).length());
			characterfile.newLine();
			characterfile.write("tank = ", 0, 7);
			characterfile.write(Boolean.toString(player.getTank()), 0, Boolean.toString(player.getTank()).length());
			characterfile.newLine();
			characterfile.write("meleeMainKills = ", 0, 17);
			characterfile.write(Integer.toString(player.getMeleeMainKills()), 0, Integer.toString(player.getMeleeMainKills()).length());
			characterfile.newLine();
			characterfile.write("hybridKills = ", 0, 14);
			characterfile.write(Integer.toString(player.getHybridKills()), 0, Integer.toString(player.getHybridKills()).length());
			characterfile.newLine();
			characterfile.write("berserkerPureKills = ", 0, 21);
			characterfile.write(Integer.toString(player.getBerserkerPureKills()), 0, Integer.toString(player.getBerserkerPureKills()).length());
			characterfile.newLine();
			characterfile.write("pureKills = ", 0, 12);
			characterfile.write(Integer.toString(player.getPureKills()), 0, Integer.toString(player.getPureKills()).length());
			characterfile.newLine();
			characterfile.write("rangedTankKills = ", 0, 18);
			characterfile.write(Integer.toString(player.getRangedTankKills()), 0, Integer.toString(player.getRangedTankKills()).length());
			characterfile.newLine();
			characterfile.write("f2pKills = ", 0, 11);
			characterfile.write(Integer.toString(player.getF2pKills()), 0, Integer.toString(player.getF2pKills()).length());
			characterfile.newLine();
			characterfile.write("tutorialComplete = ", 0, 19);
			characterfile.write(Boolean.toString(player.isTutorialComplete()), 0, Boolean.toString(player.isTutorialComplete()).length());
			characterfile.newLine();
			characterfile.write("runModeOn = ", 0, 12);
			characterfile.write(Boolean.toString(player.runModeOn), 0, Boolean.toString(player.runModeOn).length());
			characterfile.newLine();
			characterfile.write("skullTimer = ", 0, 13);
			characterfile.write(Integer.toString(player.skullTimer), 0, Integer.toString(player.skullTimer).length());
			characterfile.newLine();

			string = "spellBook = ";
			string1 = player.spellBook;
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			characterfile.write("wildernessKills = ", 0, 18);
			characterfile.write(Integer.toString(player.getWildernessKills(false)), 0, Integer.toString(player.getWildernessKills(false)).length());
			characterfile.newLine();
			characterfile.write("wildernessDeaths = ", 0, 19);
			characterfile.write(Integer.toString(player.getWildernessDeaths(false)), 0, Integer.toString(player.getWildernessDeaths(false)).length());
			characterfile.newLine();
			characterfile.write("safeKills = ", 0, 12);
			characterfile.write(Integer.toString(player.safeKills), 0, Integer.toString(player.safeKills).length());
			characterfile.newLine();
			characterfile.write("safeDeaths = ", 0, 13);
			characterfile.write(Integer.toString(player.safeDeaths), 0, Integer.toString(player.safeDeaths).length());
			characterfile.newLine();
			characterfile.write("currentKillStreak = ", 0, 20);
			characterfile.write(Integer.toString(player.currentKillStreak), 0, Integer.toString(player.currentKillStreak).length());
			characterfile.newLine();
			characterfile.write("specialAttackAmount = ", 0, 22);
			characterfile.write(Double.toString(player.getSpecialAttackAmount()), 0, Double.toString(player.getSpecialAttackAmount()).length());
			characterfile.newLine();
			characterfile.write("splitChat = ", 0, 12);
			characterfile.write(Boolean.toString(player.splitChat), 0, Boolean.toString(player.splitChat).length());
			characterfile.newLine();
			characterfile.write("autoRetaliate = ", 0, 16);
			characterfile.write(Integer.toString(player.getAutoRetaliate()), 0, Integer.toString(player.getAutoRetaliate()).length());
			characterfile.newLine();
			characterfile.write("jailed = ", 0, 9);
			characterfile.write(Boolean.toString(player.isJailed()), 0, Boolean.toString(player.isJailed()).length());
			characterfile.newLine();
			characterfile.write("combatStyle = ", 0, 14);
			characterfile.write(Integer.toString(player.getCombatStyle()), 0, Integer.toString(player.getCombatStyle()).length());
			characterfile.newLine();
			characterfile.write("tilesWalked = ", 0, 14);
			characterfile.write(Integer.toString(player.tilesWalked), 0, Integer.toString(player.tilesWalked).length());
			characterfile.newLine();
			characterfile.write("switches = ", 0, 11);
			characterfile.write(Integer.toString(player.switches), 0, Integer.toString(player.switches).length());
			characterfile.newLine();
			characterfile.write("bankPin = ", 0, 10);
			characterfile.write(player.bankPin, 0, player.bankPin.length());
			characterfile.newLine();
			characterfile.write("setPin = ", 0, 9);
			characterfile.write(Boolean.toString(player.setPin), 0, Boolean.toString(player.setPin).length());
			characterfile.newLine();
			characterfile.write("voteTotalPoints = ", 0, 18);
			characterfile.write(Integer.toString(player.voteTotalPoints), 0, Integer.toString(player.voteTotalPoints).length());
			characterfile.newLine();
			characterfile.write("toolUses = ", 0, 11);
			characterfile.write(Integer.toString(player.getToolUses()), 0, Integer.toString(player.getToolUses()).length());
			characterfile.newLine();
			characterfile.write("qp = ", 0, 5);
			characterfile.write(Integer.toString(player.getQuestPoints()), 0, Integer.toString(player.getQuestPoints()).length());
			characterfile.newLine();
			characterfile.write("foodAte = ", 0, 10);
			characterfile.write(Integer.toString(player.foodAte), 0, Integer.toString(player.foodAte).length());
			characterfile.newLine();
			characterfile.write("potionDrank = ", 0, 14);
			characterfile.write(Integer.toString(player.potionDrank), 0, Integer.toString(player.potionDrank).length());
			characterfile.newLine();
			characterfile.write("killStreaksRecord = ", 0, 20);
			characterfile.write(Integer.toString(player.killStreaksRecord), 0, Integer.toString(player.killStreaksRecord).length());
			characterfile.newLine();
			characterfile.write("petSummoned = ", 0, 14);
			characterfile.write(Boolean.toString(player.getPetSummoned()), 0, Boolean.toString(player.getPetSummoned()).length());
			characterfile.newLine();
			characterfile.write("petID = ", 0, 8);
			characterfile.write(Integer.toString(player.getPetId()), 0, Integer.toString(player.getPetId()).length());
			characterfile.newLine();
			string = "secondPetSummoned = ";
			boolean1 = player.getSecondPetSummoned();
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();
			string = "secondPetId = ";
			integer = player.getSecondPetId();
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();
			string = "summon-duration = ";
			integer = player.getSummoning().getDuration();
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();
			characterfile.write("runEnergy = ", 0, 12);
			characterfile.write(Double.toString(player.runEnergy), 0, Double.toString(player.runEnergy).length());
			characterfile.newLine();
			characterfile.write("whiteSkull = ", 0, 13);
			characterfile.write(Boolean.toString(player.getWhiteSkull()), 0, Boolean.toString(player.getWhiteSkull()).length());
			characterfile.newLine();
			characterfile.write("redSkull = ", 0, 11);
			characterfile.write(Boolean.toString(player.getRedSkull()), 0, Boolean.toString(player.getRedSkull()).length());
			characterfile.newLine();
			characterfile.write("logOutTime = ", 0, 13);
			characterfile.write(Long.toString(player.logOutTime), 0, Long.toString(player.logOutTime).length());
			characterfile.newLine();
			characterfile.write("targetsKilled = ", 0, 16);
			characterfile.write(Integer.toString(player.targetsKilled), 0, Integer.toString(player.targetsKilled).length());
			characterfile.newLine();
			characterfile.write("lastClanChatJoined = ", 0, 21);
			characterfile.write(player.lastClanChatJoined, 0, player.lastClanChatJoined.length());
			characterfile.newLine();
			characterfile.write("ableToEditCombat = ", 0, 19);
			characterfile.write(Boolean.toString(player.getAbleToEditCombat()), 0, Boolean.toString(player.getAbleToEditCombat()).length());
			characterfile.newLine();
			characterfile.write("timeOfAccountCreation = ", 0, 24);
			characterfile.write(Long.toString(player.timeOfAccountCreation), 0, Long.toString(player.timeOfAccountCreation).length());
			characterfile.newLine();
			characterfile.write("meritPoints = ", 0, 14);
			characterfile.write(Integer.toString(player.getMeritPoints()), 0, Integer.toString(player.getMeritPoints()).length());
			characterfile.newLine();
			characterfile.write("recoilCharges = ", 0, 16);
			characterfile.write(Integer.toString(player.getRecoilCharges()), 0, Integer.toString(player.getRecoilCharges()).length());
			characterfile.newLine();
			characterfile.write("ancientsInterfaceType = ", 0, 24);
			characterfile.write(Integer.toString(player.ancientsInterfaceType), 0, Integer.toString(player.ancientsInterfaceType).length());
			characterfile.newLine();
			characterfile.write("agilityPoints = ", 0, 16);
			characterfile.write(Integer.toString(player.getAgilityPoints()), 0, Integer.toString(player.getAgilityPoints()).length());
			characterfile.newLine();
			characterfile.write("antiFirePotionTimer = ", 0, 22);
			characterfile.write(Integer.toString(player.getAntiFirePotionTimer()), 0, Integer.toString(player.getAntiFirePotionTimer()).length());
			characterfile.newLine();
			characterfile.write("staminaPotionTimer = ", 0, 21);
			characterfile.write(Integer.toString(player.getStaminaPotionTimer()), 0, Integer.toString(player.getStaminaPotionTimer()).length());
			characterfile.newLine();
			characterfile.write("slayerTaskNpcType = ", 0, 20);
			characterfile.write(Integer.toString(player.slayerTaskNpcType), 0, Integer.toString(player.slayerTaskNpcType).length());
			characterfile.newLine();
			characterfile.write("slayerTaskNpcAmount = ", 0, 22);
			characterfile.write(Integer.toString(player.slayerTaskNpcAmount), 0, Integer.toString(player.slayerTaskNpcAmount).length());
			characterfile.newLine();
			characterfile.write("braceletCharges = ", 0, 18);
			characterfile.write(Integer.toString(player.getBraceletCharges()), 0, Integer.toString(player.getBraceletCharges()).length());
			characterfile.newLine();
			characterfile.write("blowpipeCharges = ", 0, 18);
			characterfile.write(Integer.toString(player.getBlowpipeCharges()), 0, Integer.toString(player.getBlowpipeCharges()).length());
			characterfile.newLine();

			string = "barragesCasted = ";
			integer = player.barragesCasted;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "randomEventIncorrectTries = ";
			integer = player.randomEventIncorrectTries;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "randomCoffin = ";
			integer = player.randomCoffin;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "targetActivityPoints = ";
			integer = player.targetActivityPoints;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "overloadReboostTicks = ";
			integer = player.overloadReboostTicks;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "targetDeaths = ";
			integer = player.targetDeaths;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "teleportsUsed = ";
			integer = player.teleportsUsed;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			if (player.getSqlIndex() >= 0) {
				string = "sqlIndex = ";
				integer = player.getSqlIndex();
				characterfile.write(string, 0, string.length());
				characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
				characterfile.newLine();
			}

			string = "barrowsPersonalRecord = ";
			integer = player.barrowsPersonalRecord;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "barrowsRunCompleted = ";
			integer = player.getBarrowsRunCompleted();
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "pvpTasksCompleted = ";
			integer = player.pvpTasksCompleted;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "customPetPoints = ";
			integer = player.getCustomPetPoints();
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "pvpTaskSize = ";
			integer = player.pvpTaskSize;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "headIconPk = ";
			integer = player.headIconPk;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "tradesCompleted = ";
			integer = player.tradesCompleted;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "meleeTournamentsWon1 = ";
			integer = player.meleeTournamentsWon1;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "hybridTournamentsWon1 = ";
			integer = player.hybridTournamentsWon1;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "tribridTournamentsWon1 = ";
			integer = player.tribridTournamentsWon1;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "duelArenaStakes = ";
			integer = player.duelArenaStakes;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "wildernessKillsReset = ";
			integer = player.wildernessKillsReset;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "wildernessDeathsReset = ";
			integer = player.wildernessDeathsReset;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "poisonDamage = ";
			integer = player.poisonDamage;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "poisonTicksUntillDamage = ";
			integer = player.poisonTicksUntillDamage;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "poisonHitsplatsLeft = ";
			integer = player.poisonHitsplatsLeft;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "lastTeleport = ";
			string1 = player.lastTeleport;
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "deathsToNpc = ";
			integer = player.deathsToNpc;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "privateChat = ";
			integer = player.privateChat;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "publicChatMode = ";
			integer = player.publicChatMode;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "tradeChatMode = ";
			integer = player.tradeChatMode;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "killsInMulti = ";
			integer = player.killsInMulti;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "bloodKeysCollected = ";
			integer = player.bloodKeysCollected;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "snowBallsLandedOnMe = ";
			integer = player.snowBallsLandedOnMe;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "snowBallsThrownAtMe = ";
			integer = player.snowBallsThrownAtMe;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "yellTag = ";
			string1 = player.yellTag;
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "yellMode = ";
			string1 = player.yellMode;
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "gameMode = ";
			string1 = player.getGameMode();
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "difficultyChosen = ";
			string1 = player.getDifficultyChosen();
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "timeAccountSaved = ";
			string1 = Misc.getDateAndTime();
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "biographyLine1 = ";
			string1 = player.biographyLine1;
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "biographyLine2 = ";
			string1 = player.biographyLine2;
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "biographyLine3 = ";
			string1 = player.biographyLine3;
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "biographyLine4 = ";
			string1 = player.biographyLine4;
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "biographyLine5 = ";
			string1 = player.biographyLine5;
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "profilePrivacyOn = ";
			boolean1 = player.profilePrivacyOn;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "passwordChangeAlertedComplete1 = ";
			boolean1 = player.passwordChangeAlertedComplete;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "chatEffects = ";
			boolean1 = player.chatEffects;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "infernalAndMaxCapesUnlockedScrollConsumed = ";
			boolean1 = player.isInfernalAndMaxCapesUnlockedScrollConsumed();
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "autoBuyBack = ";
			boolean1 = player.autoBuyBack;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "titleColour = ";
			string1 = player.titleColour;
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "canClaimPvpTaskReward = ";
			boolean1 = player.canClaimPvpTaskReward;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "killScreenshots = ";
			boolean1 = player.killScreenshots;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "announceMaxLevel = ";
			boolean1 = player.announceMaxLevel;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "rigourUnlocked = ";
			boolean1 = player.rigourUnlocked;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "auguryUnlocked = ";
			boolean1 = player.auguryUnlocked;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "teleportWarning = ";
			boolean1 = player.teleportWarning;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "playerIsLeakedSourceClean = ";
			boolean1 = player.playerIsLeakedSourceClean;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "xpLock = ";
			boolean1 = player.xpLock;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "xpBarShowType = ";
			string1 = player.xpBarShowType;
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "bossKillCountMessage = ";
			boolean1 = player.bossKillCountMessage;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "playerBotKills = ";
			integer = player.playerBotKills;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "playerBotDeaths = ";
			integer = player.playerBotDeaths;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "playerBotHighestKillstreak = ";
			integer = player.playerBotHighestKillstreak;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "highestRfdWave = ";
			integer = player.highestRfdWave;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "playerBotCurrentKillstreak = ";
			integer = player.playerBotCurrentKillstreak;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "yellMutes = ";
			integer = player.yellMutes;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "yellMuteExpireTime = ";
			long1 = player.yellMuteExpireTime;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "xpBonusEndTime = ";
			long1 = player.getXpBonusEndTime();
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "accountOfferNotificationPopUpTime = ";
			long1 = player.accountOfferNotificationPopUpTime;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "timeAccountOfferShown = ";
			long1 = player.timeAccountOfferShown;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "timeCraftedRunes = ";
			long1 = player.timeCraftedRunes;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "timeLoggedOff = ";
			long1 = player.timeLoggedOff;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "timeUsedPreset = ";
			long1 = player.timeUsedPreset;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "timeEarnedBloodMoneyInResourceWild = ";
			long1 = player.getTimeEarnedBloodMoneyInResourceWild();
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "dateUsedSpellbookSwap = ";
			string1 = player.getDateUsedSpellbookSwap();
			characterfile.write(string, 0, string.length());
			characterfile.write(string1, 0, string1.length());
			characterfile.newLine();

			string = "spellbookSwapUsedOnSameDateAmount = ";
			integer = player.getSpellbookSwapUsedOnSameDateAmount();
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "compColor1 = ";
			integer = player.compColor1;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "compColor2 = ";
			integer = player.compColor2;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "compColor3 = ";
			integer = player.compColor3;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "compColor4 = ";
			integer = player.compColor4;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "slayerPoints = ";
			integer = player.getSlayerPoints();
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "accountOffersSkippedStreak = ";
			integer = player.accountOffersSkippedStreak;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "accountOfferClaimTargetGoal = ";
			integer = player.accountOfferClaimTargetGoal;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "accountOfferClaimTargetProgress = ";
			doubleNumber = player.accountOfferClaimTargetProgress;
			characterfile.write(string, 0, string.length());
			characterfile.write(Double.toString(doubleNumber), 0, Double.toString(doubleNumber).length());
			characterfile.newLine();

			string = "accountOfferRewardItemAmount = ";
			integer = player.accountOfferRewardItemAmount;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "money-pouch = ";
			integer = player.getMoneyPouch().amount(995);
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "accountOfferRewardItemId = ";
			integer = player.accountOfferRewardItemId;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "chargeSpellTime = ";
			long1 = player.chargeSpellTime;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "imbuedHeartTime = ";
			long1 = player.imbuedHeartEndTime;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "timeLastClaimedDonation = ";
			long1 = player.getTimeLastClaimedDonation();
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "timeAcceptedTradeInDiceZone = ";
			long1 = player.timeAcceptedTradeInDiceZone;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			if (!player.getZulrahLostItems().isEmpty()) {
				characterfile.write("zulrah-lost-items = " + new ItemContainerToStringEncoder().encode(player.getZulrahLostItems()));
				characterfile.newLine();
			}

			if (!player.getVorkathLostItems().isEmpty()) {
				characterfile.write("vorkath-lost-items = " + new ItemContainerToStringEncoder().encode(player.getVorkathLostItems()));
				characterfile.newLine();
			}

			string = "timeSkilled = ";
			long1 = player.timeSkilled;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "timeUsedHealthBoxSpecial = ";
			long1 = player.timeUsedHealthBoxSpecial;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "timeVoteNotificationAlerted = ";
			long1 = player.timeVoteNotificationAlerted;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "teleBlockEndTime = ";
			long1 = player.teleBlockEndTime;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "timeDiedInWilderness = ";
			long1 = player.timeDiedInWilderness;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "timeWeeklyGameTimeUsed = ";
			long1 = player.timeWeeklyGameTimeUsed;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "timeVoted = ";
			long1 = player.timeVoted;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "xpLampUsedTime = ";
			long1 = player.xpLampUsedTime;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "venomImmunityExpireTime = ";
			long1 = player.venomImmunityExpireTime;
			characterfile.write(string, 0, string.length());
			characterfile.write(Long.toString(long1), 0, Long.toString(long1).length());
			characterfile.newLine();

			string = "bossScoreCapped = ";
			integer = player.bossScoreCapped;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "bossScoreUnCapped = ";
			integer = player.bossScoreUnCapped;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "myTabs = ";
			integer = player.myTabs;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "enemyTabs = ";
			integer = player.enemyTabs;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "lastXAmount = ";
			integer = player.lastXAmount;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "bloodMoneySpent = ";
			integer = player.bloodMoneySpent;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "valuableLoot = ";
			integer = player.valuableLoot;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "votesClaimed = ";
			integer = player.votesClaimed;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "venomDamage = ";
			integer = player.venomDamage;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "venomHits = ";
			integer = player.venomHits;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "venomTicksUntillDamage = ";
			integer = player.venomTicksUntillDamage;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "displayBots = ";
			boolean1 = player.displayBots;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "accountOfferCompleted = ";
			boolean1 = player.accountOfferCompleted;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "smashVials = ";
			boolean1 = player.smashVials;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "toggleSeedPod = ";
			boolean1 = player.toggleSeedPod;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "toggleLootKey = ";
			boolean1 = player.toggleLootKey;
			characterfile.write(string, 0, string.length());
			characterfile.write(Boolean.toString(boolean1), 0, Boolean.toString(boolean1).length());
			characterfile.newLine();

			string = "throneId = ";
			integer = player.throneId;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "blowpipeDartItemId = ";
			integer = player.blowpipeDartItemId;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			string = "blowpipeDartItemAmount = ";
			integer = player.blowpipeDartItemAmount;
			characterfile.write(string, 0, string.length());
			characterfile.write(Integer.toString(integer), 0, Integer.toString(integer).length());
			characterfile.newLine();

			characterfile.write("boostedTime = ", 0, 14);
			String toWrite0 = "";
			for (int i1 = 0; i1 < player.boostedTime.length; i1++) {
				toWrite0 += player.boostedTime[i1] + "\t";
			}
			characterfile.write(toWrite0);
			characterfile.newLine();

			string = "maximumSpecialAttack = ";
			characterfile.write(string, 0, string.length());
			String toWrite6 = "";
			for (int i1 = 0; i1 < player.maximumSpecialAttack.length; i1++) {
				toWrite6 += player.maximumSpecialAttack[i1] + "\t";
			}
			characterfile.write(toWrite6);
			characterfile.newLine();

			string = "maximumSpecialAttackNpc = ";
			characterfile.write(string, 0, string.length());
			toWrite6 = "";
			for (int i1 = 0; i1 < player.maximumSpecialAttackNpc.length; i1++) {
				toWrite6 += player.maximumSpecialAttackNpc[i1] + "\t";
			}
			characterfile.write(toWrite6);
			characterfile.newLine();

			string = "weaponAmountUsed = ";
			characterfile.write(string, 0, string.length());
			String toWrite6Point1 = "";
			for (int i1 = 0; i1 < player.weaponAmountUsed.length; i1++) {
				toWrite6Point1 += player.weaponAmountUsed[i1] + "\t";
			}
			characterfile.write(toWrite6Point1);
			characterfile.newLine();

			string = "weaponAmountUsedNpc = ";
			characterfile.write(string, 0, string.length());
			toWrite6Point1 = "";
			for (int i1 = 0; i1 < player.weaponAmountUsedNpc.length; i1++) {
				toWrite6Point1 += player.weaponAmountUsedNpc[i1] + "\t";
			}
			characterfile.write(toWrite6Point1);
			characterfile.newLine();

			characterfile.write("pouchesPure = ", 0, 14);
			String toWrite8 = "";
			for (int i1 = 0; i1 < player.pouchesPure.length; i1++) {
				toWrite8 += player.pouchesPure[i1] + "\t";
			}
			characterfile.write(toWrite8);
			characterfile.newLine();

			characterfile.write("pouchesRune = ", 0, 14);
			toWrite8 = "";
			for (int i1 = 0; i1 < player.pouchesRune.length; i1++) {
				toWrite8 += player.pouchesRune[i1] + "\t";
			}
			characterfile.write(toWrite8);
			characterfile.newLine();

			characterfile.write("QuickPrayers = ", 0, 15);
			String toWrite4 = "";
			for (int i1 = 0; i1 < player.quickPrayers.length; i1++) {
				toWrite4 += player.quickPrayers[i1] + "\t";
			}
			characterfile.write(toWrite4);
			characterfile.newLine();

			characterfile.write("combatExperienceGainedAfterMaxed = ", 0, 35);
			String toWrite14 = "";
			for (int i1 = 0; i1 < player.combatExperienceGainedAfterMaxed.length; i1++) {
				toWrite14 += player.combatExperienceGainedAfterMaxed[i1] + "\t";
			}
			characterfile.write(toWrite14);
			characterfile.newLine();

			string = "deathTypes = ";
			characterfile.write(string, 0, string.length());
			String toWrite15 = "";
			for (int i1 = 0; i1 < player.deathTypes.length; i1++) {
				toWrite15 += player.deathTypes[i1] + "\t";
			}
			characterfile.write(toWrite15);
			characterfile.newLine();

			string = "totalDamage = ";
			characterfile.write(string, 0, string.length());
			String toWrite16 = "";
			for (int i1 = 0; i1 < player.totalDamage.length; i1++) {
				toWrite16 += player.totalDamage[i1] + "\t";
			}
			characterfile.write(toWrite16);
			characterfile.newLine();

			string = "skillingStatistics = ";
			characterfile.write(string, 0, string.length());
			String toWrite18 = "";
			for (int i1 = 0; i1 < player.skillingStatistics.length; i1++) {
				toWrite18 += player.skillingStatistics[i1] + "\t";
			}
			characterfile.write(toWrite18);
			characterfile.newLine();

			string = "timeSpent = ";
			characterfile.write(string, 0, string.length());
			String toWrite19 = "";
			for (int i1 = 0; i1 < player.timeSpent.length; i1++) {
				toWrite19 += player.timeSpent[i1] + "\t";
			}
			characterfile.write(toWrite19);
			characterfile.newLine();

			string = "currentCombatSkillLevel = ";
			characterfile.write(string, 0, string.length());
			String toWrite21 = "";
			for (int i1 = 0; i1 < player.currentCombatSkillLevel.length; i1++) {
				toWrite21 += player.currentCombatSkillLevel[i1] + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "skillMilestone100mAnnounced = ";
			characterfile.write(string, 0, string.length());
			String toWrite22 = "";
			for (int i1 = 0; i1 < player.skillMilestone100mAnnounced.length; i1++) {
				toWrite22 += player.skillMilestone100mAnnounced[i1] + "\t";
			}
			characterfile.write(toWrite22);
			characterfile.newLine();

			string = "skillMilestone200mAnnounced = ";
			characterfile.write(string, 0, string.length());
			toWrite22 = "";
			for (int i1 = 0; i1 < player.skillMilestone200mAnnounced.length; i1++) {
				toWrite22 += player.skillMilestone200mAnnounced[i1] + "\t";
			}
			characterfile.write(toWrite22);
			characterfile.newLine();

			string = "barrowsBrothersKilled = ";
			characterfile.write(string, 0, string.length());
			toWrite22 = "";
			for (int i1 = 0; i1 < player.barrowsBrothersKilled.length; i1++) {
				toWrite22 += player.barrowsBrothersKilled[i1] + "\t";
			}
			characterfile.write(toWrite22);
			characterfile.newLine();

			string = "itemsToShop = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.itemsToShop.size(); i1++) {
				toWrite21 += player.itemsToShop.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "resourcesHarvested = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.resourcesHarvested.size(); i1++) {
				toWrite21 += player.resourcesHarvested.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "achievementProgress = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.achievementProgress.size(); i1++) {
				toWrite21 += player.achievementProgress.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "itemsCollected = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.itemsCollected.size(); i1++) {
				toWrite21 += player.itemsCollected.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "achievementsCompleted = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.achievementsCompleted.size(); i1++) {
				toWrite21 += player.achievementsCompleted.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "titlesUnlocked = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.titlesUnlocked.size(); i1++) {
				toWrite21 += player.titlesUnlocked.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "npcKills = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.npcKills.size(); i1++) {
				toWrite21 += player.npcKills.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			toWrite22 = "";
			string = "lootingBagStorageItemId = ";
			characterfile.write(string, 0, string.length());
			for (int i1 = 0; i1 < player.lootingBagStorageItemId.length; i1++) {
				toWrite22 += player.lootingBagStorageItemId[i1] + "\t";
			}
			characterfile.write(toWrite22);
			characterfile.newLine();

			toWrite22 = "";
			string = "lootingBagStorageItemAmount = ";
			characterfile.write(string, 0, string.length());
			for (int i1 = 0; i1 < player.lootingBagStorageItemAmount.length; i1++) {
				toWrite22 += player.lootingBagStorageItemAmount[i1] + "\t";
			}
			characterfile.write(toWrite22);
			characterfile.newLine();

			toWrite22 = "";
			string = "achievementTotal = ";
			characterfile.write(string, 0, string.length());
			for (int i1 = 0; i1 < player.achievementTotal.length; i1++) {
				toWrite22 += player.achievementTotal[i1] + "\t";
			}
			characterfile.write(toWrite22);
			characterfile.newLine();

			if (player.getPlayerPetState() != null) {
				characterfile.write(String.format("player-pet-state1 = %s", player.getPlayerPetState().encode()));
				characterfile.newLine();
			}
			for (Map.Entry<AttributeKey<?>, Object> entry : player.getAttributes().entries()) {
				AttributeKey<?> key = entry.getKey();

				Object value = entry.getValue();

				if (value == null || key.serializationType() == AttributeSerializationType.TRANSIENT) {
					continue;
				}
				if (key.serializationType() == AttributeSerializationType.PERMANENT && key.serializationKeyOrNull() != null) {
					characterfile.write(String.format("%s = %s", key.serializationKeyOrNull(), gson.toJson(value)));
					characterfile.newLine();
				}
			}

			toWrite22 = "";
			string = "questStages = ";
			characterfile.write(string, 0, string.length());
			for (int i1 = 0; i1 < player.questStages.length; i1++) {
				toWrite22 += player.questStages[i1] + "\t";
			}
			characterfile.write(toWrite22);
			characterfile.newLine();

			toWrite22 = "";
			string = "titleTotal = ";
			characterfile.write(string, 0, string.length());
			for (int i1 = 0; i1 < player.titleTotal.length; i1++) {
				toWrite22 += player.titleTotal[i1] + "\t";
			}
			characterfile.write(toWrite22);
			characterfile.newLine();

			toWrite22 = "";
			string = "achievementDifficultyCompleted = ";
			characterfile.write(string, 0, string.length());
			for (int i1 = 0; i1 < player.achievementDifficultyCompleted.length; i1++) {
				toWrite22 += player.achievementDifficultyCompleted[i1] + "\t";
			}
			characterfile.write(toWrite22);
			characterfile.newLine();

			string = "singularUntradeableItemsOwned = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.singularUntradeableItemsOwned.size(); i1++) {
				toWrite21 += player.singularUntradeableItemsOwned.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "tradingPostHistory = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.tradingPostHistory.size(); i1++) {
				toWrite21 += player.tradingPostHistory.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "donationPriceClaimedHistory = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.donationPriceClaimedHistory.size(); i1++) {
				toWrite21 += player.donationPriceClaimedHistory.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "pvpBlacklist = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.pvpBlacklist.size(); i1++) {
				toWrite21 += player.pvpBlacklist.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "preset1 = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.preset1.size(); i1++) {
				toWrite21 += player.preset1.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "preset2 = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.preset2.size(); i1++) {
				toWrite21 += player.preset2.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "preset3 = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.preset3.size(); i1++) {
				toWrite21 += player.preset3.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "preset4 = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.preset4.size(); i1++) {
				toWrite21 += player.preset4.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "preset5 = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.preset5.size(); i1++) {
				toWrite21 += player.preset5.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "preset6 = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.preset6.size(); i1++) {
				toWrite21 += player.preset6.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "preset7 = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.preset7.size(); i1++) {
				toWrite21 += player.preset7.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "preset8 = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.preset8.size(); i1++) {
				toWrite21 += player.preset8.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "preset9 = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.preset9.size(); i1++) {
				toWrite21 += player.preset9.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			toWrite22 = "";
			string = "runePouchItemId = ";
			characterfile.write(string, 0, string.length());
			for (int i1 = 0; i1 < player.runePouchItemId.length; i1++) {
				toWrite22 += player.runePouchItemId[i1] + "\t";
			}
			characterfile.write(toWrite22);
			characterfile.newLine();

			toWrite22 = "";
			string = "runePouchItemAmount = ";
			characterfile.write(string, 0, string.length());
			for (int i1 = 0; i1 < player.runePouchItemAmount.length; i1++) {
				toWrite22 += player.runePouchItemAmount[i1] + "\t";
			}
			characterfile.write(toWrite22);
			characterfile.newLine();

			toWrite22 = "";
			string = "pvpTask = ";
			characterfile.write(string, 0, string.length());
			for (int i1 = 0; i1 < player.pvpTask.length; i1++) {
				toWrite22 += player.pvpTask[i1] + "\t";
			}
			characterfile.write(toWrite22);
			characterfile.newLine();

			string = "killTimes = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.killTimes.size(); i1++) {
				toWrite21 += player.killTimes.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "voteTimes = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.voteTimes.size(); i1++) {
				toWrite21 += player.voteTimes.get(i1) + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			string = "gameTimeHistory = ";
			characterfile.write(string, 0, string.length());
			toWrite21 = "";
			for (int i1 = 0; i1 < player.activePlayedTimeDates.size(); i1++) {
				toWrite21 += player.activePlayedTimeDates.get(i1).minutes + "#" + player.activePlayedTimeDates.get(i1).date + "\t";
			}
			characterfile.write(toWrite21);
			characterfile.newLine();

			characterfile.newLine();
			characterfile.write("[SKILLS]", 0, 8);
			characterfile.newLine();
			for (int i = 0; i < ServerConstants.getTotalSkillsAmount(); i++) {
				characterfile.write("character-skill = ", 0, 18);
				characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(player.baseSkillLevel[i]), 0, Integer.toString(player.baseSkillLevel[i]).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(player.skillExperience[i]), 0, Integer.toString(player.skillExperience[i]).length());
				characterfile.newLine();
			}
			characterfile.newLine();

			characterfile.write("[INVENTORY]", 0, 11);
			characterfile.newLine();
			for (int i = 0; i < player.playerItems.length; i++) {
				if (player.playerItems[i] > 0) {
					characterfile.write("inventory-slot = ", 0, 17);
					characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.playerItems[i]), 0, Integer.toString(player.playerItems[i]).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.playerItemsN[i]), 0, Integer.toString(player.playerItemsN[i]).length());
					characterfile.newLine();
				}
			}
			characterfile.newLine();
			characterfile.write("[EQUIPMENT]", 0, 11);
			characterfile.newLine();
			for (int i = 0; i < player.playerEquipment.length; i++) {
				if (player.playerEquipment[i] > 0) {
					characterfile.write("character-equip = ", 0, 18);
					characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.playerEquipment[i]), 0, Integer.toString(player.playerEquipment[i]).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.playerEquipmentN[i]), 0, Integer.toString(player.playerEquipmentN[i]).length());
					characterfile.write("	", 0, 1);
					characterfile.newLine();
				}
			}
			characterfile.newLine();
			characterfile.write("[APPEARANCE]", 0, 12);
			characterfile.newLine();
			for (int i = 0; i < player.playerAppearance.length; i++) {
				characterfile.write("character-look = ", 0, 17);
				characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(player.playerAppearance[i]), 0, Integer.toString(player.playerAppearance[i]).length());
				characterfile.newLine();
			}
			characterfile.newLine();

			characterfile.write("[BANK]", 0, 6);
			characterfile.newLine();
			for (int i = 0; i < player.bankItems.length; i++) {
				if (player.bankItems[i] > 0) {
					characterfile.write("character-bank = ", 0, 17);
					characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItems[i]), 0, Integer.toString(player.bankItems[i]).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItemsN[i]), 0, Integer.toString(player.bankItemsN[i]).length());
					characterfile.newLine();
				}
			}
			for (int i = 0; i < player.bankItems1.length; i++) {
				if (player.bankItems1[i] > 0) {
					characterfile.write("character-bank1 = ", 0, 18);
					characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItems1[i]), 0, Integer.toString(player.bankItems1[i]).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItems1N[i]), 0, Integer.toString(player.bankItems1N[i]).length());
					characterfile.newLine();
				}
			}
			for (int i = 0; i < player.bankItems2.length; i++) {
				if (player.bankItems2[i] > 0) {
					characterfile.write("character-bank2 = ", 0, 18);
					characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItems2[i]), 0, Integer.toString(player.bankItems2[i]).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItems2N[i]), 0, Integer.toString(player.bankItems2N[i]).length());
					characterfile.newLine();
				}
			}
			for (int i = 0; i < player.bankItems3.length; i++) {
				if (player.bankItems3[i] > 0) {
					characterfile.write("character-bank3 = ", 0, 18);
					characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItems3[i]), 0, Integer.toString(player.bankItems3[i]).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItems3N[i]), 0, Integer.toString(player.bankItems3N[i]).length());
					characterfile.newLine();
				}
			}
			for (int i = 0; i < player.bankItems4.length; i++) {
				if (player.bankItems4[i] > 0) {
					characterfile.write("character-bank4 = ", 0, 18);
					characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItems4[i]), 0, Integer.toString(player.bankItems4[i]).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItems4N[i]), 0, Integer.toString(player.bankItems4N[i]).length());
					characterfile.newLine();
				}
			}
			for (int i = 0; i < player.bankItems5.length; i++) {
				if (player.bankItems5[i] > 0) {
					characterfile.write("character-bank5 = ", 0, 18);
					characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItems5[i]), 0, Integer.toString(player.bankItems5[i]).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItems5N[i]), 0, Integer.toString(player.bankItems5N[i]).length());
					characterfile.newLine();
				}
			}
			for (int i = 0; i < player.bankItems6.length; i++) {
				if (player.bankItems6[i] > 0) {
					characterfile.write("character-bank6 = ", 0, 18);
					characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItems6[i]), 0, Integer.toString(player.bankItems6[i]).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItems6N[i]), 0, Integer.toString(player.bankItems6N[i]).length());
					characterfile.newLine();
				}
			}
			for (int i = 0; i < player.bankItems7.length; i++) {
				if (player.bankItems7[i] > 0) {
					characterfile.write("character-bank7 = ", 0, 18);
					characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItems7[i]), 0, Integer.toString(player.bankItems7[i]).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItems7N[i]), 0, Integer.toString(player.bankItems7N[i]).length());
					characterfile.newLine();
				}
			}
			for (int i = 0; i < player.bankItems8.length; i++) {
				if (player.bankItems8[i] > 0) {
					characterfile.write("character-bank8 = ", 0, 18);
					characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItems8[i]), 0, Integer.toString(player.bankItems8[i]).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.bankItems8N[i]), 0, Integer.toString(player.bankItems8N[i]).length());
					characterfile.newLine();
				}
			}
			characterfile.newLine();
			characterfile.write("[FRIENDS]", 0, 9);
			characterfile.newLine();
			for (int i = 0; i < player.friends.length; i++) {
				if (player.friends[i][0] > 0) {
					characterfile.write("character-friend = ", 0, 19);
					characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write("" + player.friends[i][0]);
					characterfile.write("	", 0, 1);
					characterfile.write("" + player.friends[i][1]);
					characterfile.newLine();
				}
			}
			characterfile.newLine();
			characterfile.write("[IGNORES]", 0, 9);
			characterfile.newLine();
			for (int i = 0; i < player.ignores.length; i++) {
				if (player.ignores[i] > 0) {
					characterfile.write("character-ignore = ", 0, 19);
					characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Long.toString(player.ignores[i]), 0, Long.toString(player.ignores[i]).length());
					characterfile.newLine();
				}
			}
			characterfile.newLine();

			string = "[TEMP TOURNAMENT SKILL]";
			characterfile.write(string, 0, string.length());
			characterfile.newLine();
			string = "character-temp-tournament-skill = ";
			for (int i = 0; i < player.baseSkillLevelStoredBeforeTournament.length; i++) {
				characterfile.write(string, 0, string.length());
				characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(player.baseSkillLevelStoredBeforeTournament[i]), 0, Integer.toString(player.baseSkillLevelStoredBeforeTournament[i]).length());
				characterfile.write("	", 0, 1);
				characterfile
						.write(Integer.toString(player.skillExperienceStoredBeforeTournament[i]), 0, Integer.toString(player.skillExperienceStoredBeforeTournament[i]).length());
				characterfile.newLine();
			}
			characterfile.newLine();
			characterfile.write("[EOF]", 0, 5);
			characterfile.close();
		} catch (IOException e) {
			e.printStackTrace();
			Misc.print(player.getPlayerName() + ": error writing file.");
		}
	}

	public static boolean isValidName(String text) {
		Pattern pattern = Pattern.compile(
				//@formatter:off
				"# Match a valid Windows filename (unspecified file system).          \n" +
				"^                                # Anchor to start of string.        \n" +
				"(?!                              # Assert filename is not: CON, PRN, \n" +
				"  (?:                            # AUX, NUL, COM1, COM2, COM3, COM4, \n" +
				"    CON|PRN|AUX|NUL|             # COM5, COM6, COM7, COM8, COM9,     \n" +
				"    COM[1-9]|LPT[1-9]            # LPT1, LPT2, LPT3, LPT4, LPT5,     \n" +
				"  )                              # LPT6, LPT7, LPT8, and LPT9...     \n" +
				"  (?:\\.[^.]*)?                  # followed by optional extension    \n" +
				"  $                              # and end of string                 \n" +
				")                                # End negative lookahead assertion. \n" +
				"[^<>:\"/\\\\|?*\\x00-\\x1F]*     # Zero or more valid filename chars.\n" +
				"[^<>:\"/\\\\|?*\\x00-\\x1F\\ .]  # Last char is not a space or dot.  \n" +
				"$                                # Anchor to end of string.            ",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);
		Matcher matcher = pattern.matcher(text);
		boolean isMatch = matcher.matches();
		return isMatch;
		//@formatter:on
	}
}
