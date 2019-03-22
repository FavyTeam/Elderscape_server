package game.content.skilling.summoning.pet;

/**
 * Represents a pet
 * 
 * @author 2012
 *
 */
public class GrowingPet {

	/**
	 * The id
	 */
	private int id;

	/**
	 * The growth
	 */
	private double growth;

	/**
	 * The hunger
	 */
	private double hunger;

	/**
	 * Represents a pet
	 * 
	 * @param id the id
	 * @param growth the growth
	 * @param hunger the hunger
	 */
	public GrowingPet(int id) {
		this.setId(id);
		this.setGrowth(0);
		this.setHunger(0);
	}

	/**
	 * Gets the id
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id
	 * 
	 * @param id the id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the growth
	 *
	 * @return the growth
	 */
	public double getGrowth() {
		return growth;
	}

	/**
	 * Sets the growth
	 * 
	 * @param growth the growth
	 */
	public void setGrowth(double growth) {
		this.growth = growth;
	}

	/**
	 * Gets the hunger
	 *
	 * @return the hunger
	 */
	public double getHunger() {
		return hunger;
	}

	/**
	 * Sets the hunger
	 * 
	 * @param hunger the hunger
	 */
	public void setHunger(double hunger) {
		this.hunger = hunger;
	}
}
