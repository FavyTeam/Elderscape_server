package game.npc.impl.zulrah.sequence;

import game.npc.impl.zulrah.ZulrahLocation;
import game.npc.impl.zulrah.ZulrahTransformation;
import game.npc.impl.zulrah.attack.ZulrahAttackStrategy;

import java.util.List;

/**
 * Created by Jason MacKeigan on 2018-04-10 at 12:35 PM
 */
public class ZulrahSequenceAdapter implements ZulrahSequence {

	private final ZulrahTransformation transformation;

	private final ZulrahLocation location;

	private final List<ZulrahAttackStrategy> attacks;

	public ZulrahSequenceAdapter(ZulrahTransformation transformation, ZulrahLocation location, List<ZulrahAttackStrategy> attacks) {
		this.transformation = transformation;
		this.location = location;
		this.attacks = attacks;
	}

	@Override
	public ZulrahTransformation getTransformation() {
		return transformation;
	}

	@Override
	public List<ZulrahAttackStrategy> getAttacks() {
		return attacks;
	}

	@Override
	public ZulrahLocation getLocation() {
		return location;
	}
}
