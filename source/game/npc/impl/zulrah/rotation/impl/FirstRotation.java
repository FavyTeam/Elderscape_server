package game.npc.impl.zulrah.rotation.impl;

import game.npc.impl.zulrah.ZulrahDangerousLocation;
import game.npc.impl.zulrah.ZulrahLocation;
import game.npc.impl.zulrah.ZulrahTransformation;
import game.npc.impl.zulrah.attack.impl.*;
import game.npc.impl.zulrah.rotation.ZulrahRotation;
import game.npc.impl.zulrah.sequence.ZulrahSequence;
import game.npc.impl.zulrah.sequence.ZulrahSequenceAdapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jason MacKeigan on 2018-04-10 at 12:34 PM
 */
public class FirstRotation implements ZulrahRotation {

	@Override
	public List<ZulrahSequence> getSequences() {
		return Arrays.asList(
				new ZulrahSequenceAdapter(ZulrahTransformation.RANGE, ZulrahLocation.NORTH,
				                          Collections.singletonList(new VenomousAttackStrategy(
						                          Arrays.asList(ZulrahDangerousLocation.values())))),

				new ZulrahSequenceAdapter(ZulrahTransformation.MELEE, ZulrahLocation.NORTH,
				                          Collections.singletonList(new MeleeAttackStrategy())),

				new ZulrahSequenceAdapter(ZulrahTransformation.MAGIC, ZulrahLocation.NORTH,
				                          Collections.singletonList(new LongrangeMagicAttackStrategy())),

				new ZulrahSequenceAdapter(ZulrahTransformation.RANGE, ZulrahLocation.SOUTH,
				                          Arrays.asList(new LongrangeRangeAttackStrategy(),
				                                        new SpawnAttackStrategy(Collections.singletonList(ZulrahDangerousLocation.WEST)),
				                                        new VenomousAttackStrategy(Arrays.asList(
						                                        ZulrahDangerousLocation.SOUTH_WEST,
						                                        ZulrahDangerousLocation.SOUTH_EAST
				                                                                                )),
				                                        new SpawnAttackStrategy(Collections.singletonList(ZulrahDangerousLocation.EAST)))),

				new ZulrahSequenceAdapter(ZulrahTransformation.MELEE, ZulrahLocation.NORTH,
				                          Collections.singletonList(new MeleeAttackStrategy())),

				new ZulrahSequenceAdapter(ZulrahTransformation.MAGIC, ZulrahLocation.WEST,
				                          Collections.singletonList(new LongrangeMagicAttackStrategy())),

				new ZulrahSequenceAdapter(ZulrahTransformation.RANGE, ZulrahLocation.SOUTH,
				                          Arrays.asList(
						                          new VenomousAttackStrategy(Arrays.asList(
								                          ZulrahDangerousLocation.SOUTH_EAST,
								                          ZulrahDangerousLocation.SOUTH_WEST,
								                          ZulrahDangerousLocation.WEST)),

						                          new SpawnAttackStrategy(Arrays.asList(
								                          ZulrahDangerousLocation.EAST,
								                          ZulrahDangerousLocation.SOUTH_EAST)))),

				new ZulrahSequenceAdapter(ZulrahTransformation.MAGIC, ZulrahLocation.SOUTH,
				                          Arrays.asList(
						                          new LongrangeMagicAttackStrategy(),
						                          new SpawnAttackStrategy(Collections.singletonList(ZulrahDangerousLocation.SOUTH_WEST)),
						                          new VenomousAttackStrategy(Arrays.asList(
								                          ZulrahDangerousLocation.SOUTH_WEST,
								                          ZulrahDangerousLocation.SOUTH_EAST
						                                                                  )))),

				new ZulrahSequenceAdapter(ZulrahTransformation.MAGIC, ZulrahLocation.WEST,
				                          Collections.singletonList(new LongrangeComboAttackStrategy(
						                          Arrays.asList(new LongrangeMagicAttackStrategy(), new LongrangeRangeAttackStrategy())))),

				new ZulrahSequenceAdapter(ZulrahTransformation.MELEE, ZulrahLocation.NORTH,
				                          Collections.singletonList(new MeleeAttackStrategy())),

				new ZulrahSequenceAdapter(ZulrahTransformation.RANGE, ZulrahLocation.SOUTH,
				                          Arrays.asList(new LongrangeRangeAttackStrategy(),
				                                        new SpawnAttackStrategy(Arrays.asList(ZulrahDangerousLocation.values()))))
		                    );
	}
}
