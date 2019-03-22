package game.content.item.chargeable;

import java.util.Collection;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

import game.item.GameItem;

public enum Chargeable {
	VIGGORAS_CHAINMACE(0, 22542, 22545,
			false,
			Sets.newHashSet(new GameItem(21820, 16_000)),
			Sets.newHashSet(new GameItem(21820, 1)),
			Sets.newHashSet(new GameItem(21820, 1_000))),

	THAMMARONS_SCEPTRE(1, 22552, 22555,
			false,
			Sets.newHashSet(new GameItem(21820, 16_000)),
			Sets.newHashSet(new GameItem(21820, 1)),
			Sets.newHashSet(new GameItem(21820, 1_000))),

	CRAWS_BOW(2, 22547, 22550,
			false,
			Sets.newHashSet(new GameItem(21820, 16_000)),
			Sets.newHashSet(new GameItem(21820, 1)),
			Sets.newHashSet(new GameItem(21820, 1_000)));

	private final int id;

	private final int unchargedId;

	private final int chargedId;

	private final boolean resourcesDroppedOnDeath;

	private final Collection<GameItem> maximumResources;

	private final Collection<GameItem> resources;

	private final Collection<GameItem> requiredResources;

	Chargeable(final int id, final int unchargedId, final int chargedId, boolean resourcesDroppedOnDeath, Collection<GameItem> maximumResources,
			   Collection<GameItem> resources, Collection<GameItem> requireResources) {
		this.id = id;
		this.unchargedId = unchargedId;
		this.chargedId = chargedId;
		this.resourcesDroppedOnDeath = resourcesDroppedOnDeath;
		this.maximumResources = maximumResources;
		this.resources = resources;
		this.requiredResources = requireResources;
	}

	public static Chargeable valueOfUncharged(int unchargedId) {
		return Stream.of(Chargeable.values()).filter(chargeable -> chargeable.unchargedId == unchargedId).findAny().orElse(null);
	}

	public static Chargeable valueOfCharged(int chargedId) {
		return Stream.of(Chargeable.values()).filter(chargeable -> chargeable.chargedId == chargedId).findAny().orElse(null);
	}

	public int getId() {
		return id;
	}

	public int getUnchargedId() {
		return unchargedId;
	}

	public int getChargedId() {
		return chargedId;
	}

	public boolean isResourcesDroppedOnDeath() {
		return resourcesDroppedOnDeath;
	}

	public Collection<GameItem> getMaximumResources() {
		return maximumResources;
	}

	public Collection<GameItem> getResources() {
		return resources;
	}

	public Collection<GameItem> getRequiredResources() {
		return requiredResources;
	}
}