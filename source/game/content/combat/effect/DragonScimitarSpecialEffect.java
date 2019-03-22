package game.content.combat.effect;

import core.ServerConstants;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.player.Player;

public class DragonScimitarSpecialEffect implements EntityDamageEffect {

	@Override
	public EntityDamage onCalculation(EntityDamage damage) {
		Player attacker = (Player) damage.getSender();
		Player victim = (Player) damage.getTarget();
		if (damage.getDamage() > 0) {
			if (victim.prayerActive[ServerConstants.PROTECT_FROM_MAGIC] || victim.prayerActive[ServerConstants.PROTECT_FROM_RANGED] || victim.prayerActive[ServerConstants.PROTECT_FROM_MELEE]) {
				victim.headIcon = -1;
				victim.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[16], 0, false);
				victim.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[17], 0, false);
				victim.getPA().sendFrame36(ServerConstants.PRAYER_GLOW[18], 0, false);
			}
			victim.playerAssistant.sendMessage("You have been injured!");
			victim.stopPrayerDelay = System.currentTimeMillis();
			victim.setPrayerActive(ServerConstants.PROTECT_FROM_MAGIC, false);
			victim.setPrayerActive(ServerConstants.PROTECT_FROM_RANGED, false);
			victim.setPrayerActive(ServerConstants.PROTECT_FROM_MELEE, false);
			victim.getPA().requestUpdates();
		}
		return damage;
	}

	@Override
	public void onApply(EntityDamage damage) {
	}
}
