package game.content.miscellaneous;

import game.item.ItemAssistant;
import game.player.Player;

public class ItemPacks {
	public static enum PackData {
		AIR_RUNE_PACK(12728, new int[][]
				                     {
						                     {556, 100}
				                     }),
		WATER_RUNE_PACK(12730, new int[][]
				                       {
						                       {555, 100}
				                       }),
		EARTH_RUNE_PACK(12732, new int[][]
				                       {
						                       {557, 100}
				                       }),
		FIRE_RUNE_PACK(12734, new int[][]
				                      {
						                      {554, 100}
				                      }),
		MIND_RUNE_PACK(12736, new int[][]
				                      {
						                      {558, 100}
				                      }),
		CHAOS_RUNE_PACK(12738, new int[][]
				                       {
						                       {562, 100}
				                       }),

		BIRD_SNARE_PACK(12740, new int[][]
				                       {
						                       {10007, 100}
				                       }),
		BOX_TRAP_PACK(12742, new int[][]
				                     {
						                     {10009, 100}
				                     }),

		EYE_OF_NEWT_PACK(12859, new int[][]
				                        {
						                        {222, 100}
				                        }),

		STARTER_PACK(12955, new int[][]
				                    {
						                    {222, 100}
				                    }),

		BONE_BOLT_PACK(13193, new int[][]
				                      {
						                      {8882, 100}
				                      }),

		PLANT_POT_PACK(13250, new int[][]
				                      {
						                      {5357, 100}
				                      }),
		SACK_PACK(13252, new int[][]
				                 {
						                 {5419, 100}
				                 }),
		BASKET_PACK(13254, new int[][]
				                   {
						                   {5377, 100}
				                   }),
		COMPOST_PACK(19704, new int[][]
				                    {
						                    {6033, 100}
				                    }),

		SANDWORMS_PACK(13432, new int[][]
				                      {
						                      {13431, 100}
				                      }),

		EMPTY_JUG_PACK(20742, new int[][]
				                      {
						                      {1936, 100}
				                      }),

		EMPTY_VIAL_PACK(11877, new int[][]
				                       {
						                       {230, 100}
				                       }),
		VIAL_OF_WATER_PACK(11879, new int[][]
				                          {
						                          {228, 100}
				                          }),
		FEATHER_PACK(11881, new int[][]
				                    {
						                    {314, 100}
				                    }),
		BAIT_PACK(11883, new int[][]
				                 {
						                 {313, 100}
				                 }),
		SOFT_CLAY_PACK(12009, new int[][]
				                      {
						                      {1762, 100}
				                      }),

		AMYLASE_PACK(12641, new int[][]
				                    {
						                    {12640, 100}
				                    }),

		IRON_SET_LG(12972, new int[][]
				                   {
						                   {1153, 1},
						                   {1115, 1},
						                   {1191, 1},
						                   {1067, 1}
				                   }),

		IRON_SET_SK(12974, new int[][]
				                   {
						                   {1153, 1},
						                   {1115, 1},
						                   {1191, 1},
						                   {1081, 1}
				                   }),

		IRON_SET_LG_TRIMMED(12976, new int[][]
				                           {
						                           {12231, 1},
						                           {12225, 1},
						                           {12233, 1},
						                           {12227, 1}
				                           }),

		IRON_SET_SK_TRIMMED(12978, new int[][]
				                           {
						                           {12231, 1},
						                           {12225, 1},
						                           {12233, 1},
						                           {12229, 1}
				                           }),

		IRON_SET_LG_GOLD_TRIMMED(12980, new int[][]
				                                {
						                                {12241, 1},
						                                {12235, 1},
						                                {12243, 1},
						                                {12237, 1}
				                                }),

		IRON_SET_SK_GOLD_TRIMMED(12982, new int[][]
				                                {
						                                {12241, 1},
						                                {12235, 1},
						                                {12243, 1},
						                                {12239, 1}
				                                }),

		GUTHANS_SET(12873, new int[][]
				                   {
						                   {4724, 1},
						                   {4726, 1},
						                   {4728, 1},
						                   {4730, 1}
				                   }),
		VERACS_SET(12875, new int[][]
				                  {
						                  {4753, 1},
						                  {4755, 1},
						                  {4757, 1},
						                  {4759, 1}
				                  }),
		DHAROKS_SET(12877, new int[][]
				                   {
						                   {4716, 1},
						                   {4718, 1},
						                   {4720, 1},
						                   {4722, 1}
				                   }),
		TORAGS_SET(12879, new int[][]
				                  {
						                  {4745, 1},
						                  {4747, 1},
						                  {4749, 1},
						                  {4751, 1}
				                  }),
		AHRIMS_SET(12881, new int[][]
				                  {
						                  {4708, 1},
						                  {4710, 1},
						                  {4712, 1},
						                  {4714, 1}
				                  }),
		KARILS_SET(12883, new int[][]
				                  {
						                  {4732, 1},
						                  {4734, 1},
						                  {4736, 1},
						                  {4738, 1}
				                  });

		private int PackID;

		private int[][] ItemsGiven;

		private PackData(int PackID, int[][] ItemsGiven) {
			this.PackID = PackID;
			this.ItemsGiven = ItemsGiven;
		}

		public int getPackID() {
			return PackID;
		}

		public int[][] getItemsGiven() {
			return ItemsGiven;
		}
	}

	public static void OpenPack(Player player, int itemSlot, PackData data) {
		ItemAssistant.deleteItemFromInventory(player, data.getPackID(), itemSlot, 1);
		for (int index = 0; index < data.getItemsGiven().length; index++) {
			ItemAssistant.addItemToInventoryOrDrop(player, data.getItemsGiven()[index][0], data.getItemsGiven()[index][1]);
		}
	}

	public static boolean isPack(Player player, int itemId, int itemSlot) {
		for (PackData data : PackData.values()) {
			if (data.getPackID() == itemId) {
				OpenPack(player, itemSlot, data);
				return true;
			}
		}
		return false;
	}
}

