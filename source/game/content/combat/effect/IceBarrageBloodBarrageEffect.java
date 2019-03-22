package game.content.combat.effect;

import core.ServerConstants;
import game.content.combat.Combat;
import game.content.combat.CombatConstants;
import game.content.combat.damage.EntityDamage;
import game.content.combat.damage.EntityDamageEffect;
import game.content.combat.damage.EntityDamageType;
import game.content.combat.vsplayer.AttackPlayer;
import game.content.combat.vsplayer.magic.MagicFormula;
import game.player.Area;
import game.player.Player;
import game.player.PlayerHandler;
import java.util.ArrayList;

public class IceBarrageBloodBarrageEffect implements EntityDamageEffect {

	public ArrayList<Integer> playerId = new ArrayList<>();
	public ArrayList<String> playerName = new ArrayList<>();
	public ArrayList<Boolean> splash = new ArrayList<>();
	public ArrayList<Integer> damage = new ArrayList<>();

	public IceBarrageBloodBarrageEffect() {
	}

	@Override
	public EntityDamage onCalculation(EntityDamage damageInstance) {
		Player attacker = (Player) damageInstance.getSender();
		Player victim = (Player) damageInstance.getTarget();
		if (attacker.isMagicSplash()) {
			return damageInstance;
		}
		if (!Area.inMulti(attacker.getX(), attacker.getY())) {
			return damageInstance;
		}
		
		// Used to cap the amount of players tagged to 9.
		int multiHits = 0;
		for (Player loop : victim.getLocalPlayers()) {
			if (multiHits == 9) {
				break;
			}
			if (loop == null) {
				continue;
			}
			if (victim.getPlayerId() == loop.getPlayerId()) {
				continue;
			}
			if (loop.getPlayerId() == attacker.getPlayerId()) {
				continue;
			}
			if (!Area.inMulti(loop.getX(), loop.getY())) {
				continue;
			}
			if (!Area.inDangerousPvpAreaOrClanWars(loop)) {
				continue;
			}
			// if loop is not close to the player i am initially attacking, which is the victim.
			if (!loop.playerAssistant.withInDistance(loop.getX(), loop.getY(), victim.getX(), victim.getY(), 1)) {
				continue;
			}
			if (!AttackPlayer.hasSubAttackRequirements(attacker, loop, false, false)) {
				continue;
			}
			multiHits++;
			if (MagicFormula.isSplash(attacker, loop)) {
				playerId.add(loop.getPlayerId());
				damage.add(0);
				splash.add(true);
				playerName.add(loop.getPlayerName());
				continue;
			}
			int damageAmount = MagicFormula.calculateMagicDamage(attacker, loop);
			playerId.add(loop.getPlayerId());
			damage.add(damageAmount);
			splash.add(false);
			playerName.add(loop.getPlayerName());
			Combat.addCombatExperience(attacker, ServerConstants.MAGIC_ICON, damageAmount);
			Combat.multiSpellEffect(attacker, loop, damageAmount);
			AttackPlayer.successfulAttackInitiated(attacker, loop);
		}
		return damageInstance;
	}

	@Override
	public void onApply(EntityDamage damageInstance) {
		Player attacker = (Player) damageInstance.getSender();
		for (int index = 0; index < playerId.size(); index++) {
			Player loop = PlayerHandler.players[playerId.get(index)];
			if (loop == null) {
				continue;
			}
			if (loop.getPlayerName().equals(playerName.get(index)))
			{
				if (splash.get(index)) {
					loop.gfx100(85);
					continue;
				}
				if (Combat.getEndGfxHeight(attacker, CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][0]) == 100) { // end GFX
					loop.gfx100(CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][5]);
				} else {
					loop.gfx0(CombatConstants.MAGIC_SPELLS[attacker.getOldSpellId()][5]);
				}
				attacker.getIncomingDamageOnVictim().add(new EntityDamage<Player, Player>(loop, attacker, damage.get(index), 1, EntityDamageType.MAGIC, attacker.getMaximumDamageMagic(), false, false));
			}
		}
	}
}
