package game.content.consumable;

import core.GameType;
import core.ServerConstants;
import game.player.Player;

/**
 * Handles the potion flask
 * 
 * @author 2012
 *
 */
public class PotionFlask {

	/**
	 * The empty flask
	 */
	private static final int EMPTY_FLASK = 23_191;

	/**
	 * Drinking flask
	 * 
	 * @param player the player
	 * @param id the id
	 * @param slot the slot
	 */
	public static void drink(Player player, int id, int slot) {
		if (!GameType.isPreEoc()) {
			return;
		}
		switch (id) {
			case 23_195:
			case 23_197:
			case 23_199:
			case 23_201:
			case 23_203:
			case 23_205: // attack flask
				Potions.drinkStatPotion(player, id, id == 23_205 ? EMPTY_FLASK : id + 2, slot,
						ServerConstants.ATTACK, false);
				break;

			case 23_207:
			case 23_209:
			case 23_211:
			case 23_213:
			case 23_215:
			case 23_217: // strength flask
				Potions.drinkStatPotion(player, id, id == 23_217 ? EMPTY_FLASK : id + 2, slot,
						ServerConstants.STRENGTH, false);
				break;

			case 23_219:
			case 23_221:
			case 23_223:
			case 23_225:
			case 23_227:
			case 23_229: // restore flask
				Potions.drinkPrayerPotion(player, id, id == 23_229 ? EMPTY_FLASK : id + 2, slot, false);
				break;

			case 23_231:
			case 23_233:
			case 23_235:
			case 23_237:
			case 23_239:
			case 23_241: // defence flask
				Potions.drinkStatPotion(player, id, id == 23_241 ? EMPTY_FLASK : id + 2, slot,
						ServerConstants.DEFENCE, false);
				break;

			case 23_243:
			case 23_245:
			case 23_247:
			case 23_249:
			case 23_251:
			case 23_253: // prayer flask
				Potions.drinkPrayerPotion(player, id, id == 23_253 ? EMPTY_FLASK : id + 2, slot, false);
				break;


			case 23_255:
			case 23_257:
			case 23_259:
			case 23_261:
			case 23_263:
			case 23_265: // super attack flask
				Potions.drinkStatPotion(player, id, id == 23_265 ? EMPTY_FLASK : id + 2, slot,
						ServerConstants.ATTACK, true);
				break;

			case 23_279:
			case 23_281:
			case 23_283:
			case 23_285:
			case 23_287:
			case 23_289: // super strength flask
				Potions.drinkStatPotion(player, id, id == 23_289 ? EMPTY_FLASK : id + 2, slot,
						ServerConstants.STRENGTH, true);
				break;

			case 23_291:
			case 23_293:
			case 23_295:
			case 23_297:
			case 23_299:
			case 23_301: // super defence flask
				Potions.drinkStatPotion(player, id, id == 23_301 ? EMPTY_FLASK : id + 2, slot,
						ServerConstants.DEFENCE, true);
				break;

			case 23_303:
			case 23_305:
			case 23_307:
			case 23_309:
			case 23_311:
			case 23_313: // ranging flask
				Potions.drinkStatPotion(player, id, id == 23_313 ? EMPTY_FLASK : id + 2, slot,
						ServerConstants.RANGED, true);
				break;

			case 23_315:
			case 23_317:
			case 23_319:
			case 23_321:
			case 23_323:
			case 23_325: // antipoison flask
				Potions.drinkAntiPoison(player, id, id == 23_325 ? EMPTY_FLASK : id + 2, slot, 30_000);
				break;

			case 23_327:
			case 23_329:
			case 23_331:
			case 23_333:
			case 23_335:
			case 23_337: // super antipoison flask
				Potions.drinkAntiPoison(player, id, id == 23_337 ? EMPTY_FLASK : id + 2, slot, 300_000);
				break;

			case 23_351:
			case 23_353:
			case 23_355:
			case 23_357:
			case 23_359:
			case 23_361: // sara brew flask
				Potions.doTheBrew(player, id, id == 23_361 ? EMPTY_FLASK : id + 2, slot);
				break;

			case 23_363:
			case 23_365:
			case 23_367:
			case 23_369:
			case 23_371:
			case 23_373: // antifire flask
				Potions.drinkAntiFirePotion(player, id == 23_373 ? EMPTY_FLASK : id + 2, slot, 6);
				break;

			case 23_375:
			case 23_377:
			case 23_379:
			case 23_381:
			case 23_383:
			case 23_385: // energy flask
				Potions.energyPotion(player, id, id == 23_385 ? EMPTY_FLASK : id + 2, slot, 10);
				break;

			case 23_387:
			case 23_389:
			case 23_391:
			case 23_393:
			case 23_395:
			case 23_397: // super energy flask
				Potions.energyPotion(player, id, id == 23_397 ? EMPTY_FLASK : id + 2, slot, 20);
				break;

			case 23_399:
			case 23_401:
			case 23_403:
			case 23_405:
			case 23_407:
			case 23_409: // super restore flask
				Potions.drinkSuperRestore(player, id, id == 23_409 ? EMPTY_FLASK : id + 2, slot);
				break;

			case 23_423:
			case 23_425:
			case 23_427:
			case 23_429:
			case 23_431:
			case 23_433: // magic flask
				Potions.drinkMagicPotion(player, id, id == 23_433 ? EMPTY_FLASK : id + 2, slot,
						ServerConstants.MAGIC, false);
				break;

			case 23_447:
			case 23_449:
			case 23_451:
			case 23_453:
			case 23_455:
			case 23_457: // combat flask
				Potions.drinkStatPotion(player, id, id == 23_457 ? EMPTY_FLASK : id + 2, slot,
						ServerConstants.ATTACK, true);
				Potions.drinkStatPotion(player, id, 0, slot, ServerConstants.STRENGTH, true);
				Potions.drinkStatPotion(player, id, 0, slot, ServerConstants.DEFENCE, true);
				break;
		}
	}
}
