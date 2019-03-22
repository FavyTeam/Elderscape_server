package game.npc.pet;

/**
 * Handle large pet data.
 *
 * @author MGT Madness, created on 12-12-2013.
 */
public class PetData {

	/**
	 * NPC spawn identity, Pet identity in inventory, 1 means pick-up up through first click/2 means second click pickup, 1 means it is an item pet which does not turn to face owner.
	 */
	public static int[][] petData =
			{
					//@formatter:off
					// Pet kitten.
					{
							5591, 1555, 1
					},
					// Pet kitten.
					{
							5592, 1556, 1
					},
					// Pet kitten.
					{
							5593, 1557, 1
					},
					// Pet kitten.
					{
							5594, 1558, 1
					},
					// Pet kitten.
					{
							5595, 1559, 1
					},
					// Pet kitten.
					{
							5596, 1560, 1
					},
					// Pet cat.
					{
							5598, 1561, 1
					},
					// Pet cat.
					{
							5599, 1562, 1
					},
					// Pet cat.
					{
							5600, 1563, 1
					},
					// Pet cat.
					{
							5601, 1564, 1
					},
					// Pet cat.
					{
							5602, 1565, 1
					},
					// Pet cat.
					{
							5603, 1566, 1
					},
					// Hell-kitten.
					{
							5597, 7583, 1
					},
					// Lazy hellcat.
					{
							5604, 7584, 1
					},
					// Wily hellcat.
					{
							5590, 7585, 1
					},
					// Pet commander zilyana.
					{
							6633, 12651, 2
					},
					// Pet general graardor.
					{
							6632, 12650, 2
					},
					// Prince black dragon.
					{
							6636, 12653, 2
					},
					// K'ril Tsutsaroth.
					{
							6634, 12652, 2
					},
					// Chaos elemental.
					{
							2055, 11995, 2
					},
					// Kree arra.
					{
							6631, 12649, 2
					},
					// Dagannoth Rex.
					{
							6630, 12645, 2
					},
					// Dagannoth Prime.
					{
							6629, 12644, 2
					},
					// Dagannoth Supreme.
					{
							6628, 12643, 2
					},
					// Tormented demon.
					{
							11013, 16007, 1
					},
					// Bearded camel pet.
					{
							2835, 7001, 1
					},
					// Callisto cub.
					{
							5558, 13178, 2
					},

					// Ice strykewyrm pet.
					{
							11012, 16008, 1
					},

					// Venenatis spiderling.
					{
							5557, 13177, 2
					},
					// Hellpuppy/Cerberus boss.
					{
							964, 13247, 2
					},
					// Olmlet.
					{
							7520, 20851, 2
					},
					// Pet penance queen.
					{
							6642, 12703, 2
					},
					// Beaver (woodcutting)
					{
							6717, 13322, 2
					},
					// Heron (fishing)
					{
							6722, 13320, 2
					},
					// Rock golem (Mining)
					{
							7439, 13321, 2
					},
					// Rock golem (Mining)
					{
							7440, 21187, 2
					},
					// Rock golem (Mining)
					{
							7441, 21188, 2
					},
					// Rock golem (Mining)
					{
							7442, 21189, 2
					},
					// Rock golem (Mining)
					{
							7443, 21190, 2
					},
					// Rock golem (Mining)
					{
							7444, 21191, 2
					},
					// Rock golem (Mining)
					{
							7445, 21192, 2
					},
					// Rock golem (Mining)
					{
							7446, 21193, 2
					},
					// Rock golem (Mining)
					{
							7447, 21194, 2
					},
					// Rock golem (Mining)
					{
							7448, 21195, 2
					},
					// Rock golem (Mining)
					{
							7449, 21196, 2
					},
					// Rock golem (Mining)
					{
							7450, 21197, 2
					},
					// Giant Squirrel (Agility)
					{
							7334, 20659, 2
					},
					// Phoenix (Firemaking)
					{
							7368, 20693, 2
					},
					// Tangleroot (Farming)
					{
							7335, 20661, 2
					},
					// Rift guardian (Runecrafting)
					{
							7337, 20665, 2
					},
					// Rift guardian (Runecrafting)
					{
							7338, 20667, 2
					},
					// Rift guardian (Runecrafting)
					{
							7339, 20669, 2
					},
					// Rift guardian (Runecrafting)
					{
							7340, 20671, 2
					},
					// Rift guardian (Runecrafting)
					{
							7341, 20673, 2
					},
					// Rift guardian (Runecrafting)
					{
							7342, 20675, 2
					},
					// Rift guardian (Runecrafting)
					{
							7343, 20677, 2
					},
					// Rift guardian (Runecrafting)
					{
							7344, 20679, 2
					},
					// Rift guardian (Runecrafting)
					{
							7345, 20681, 2
					},
					// Rift guardian (Runecrafting)
					{
							7346, 20683, 2
					},
					// Rift guardian (Runecrafting)
					{
							7347, 20685, 2
					},
					// Rift guardian (Runecrafting)
					{
							7348, 20687, 2
					},
					// Rift guardian (Runecrafting)
					{
							7349, 20689, 2
					},
					// Rift guardian (Runecrafting)
					{
							7350, 20691, 2
					},
					// Rocky (Thieving)
					{
							7336, 20663, 2
					},
					// Bloodhound
					{
							6296, 19730, 2
					},
					// Tzrek-jad
					{
							5892, 13225, 2
					},
					// Chompy chick
					{
							4002, 13071, 2
					},
					// Jal-nib-rek
					{
							7675, 21291, 2
					},
					// Vet'ion pet (orange version)
					{
							5537, 13180, 2
					},
					// Vet'ion pet (purple version)
					{
							5536, 13179, 2
					},
					// Midnight
					{
							7890, 21750, 2
					},
					// Karil pet
					{
							11095, 16123, 1
					},
					// White chaos elemental pet
					{
							11117, 16157, 1
					},
					// Dharok pet
					{
							11020, 16015, 1
					},
					// Cave Kraken pet
					{
							6640, 12655, 2
					},
					// Revenant ork pet
					{
							11164, 16195, 1
					},
					// Stray dog pet
					{
							11173, 16205, 1
					},
					// Revenant hellhound pet
					{
							11165, 16196, 1
					},
					// Revenant dark beast pet
					{
							11166, 16197, 1
					},
					// Revenant dragon pet
					{
							11167, 16198, 1
					},
					// Revenant knight pet
					{
							11171, 16203, 1
					},
					// Revenant demon pet
					{
							11172, 16204, 1
					},
					// Herbi pet (Herblore)
					{
							7760, 21509, 2
					},
					// Giant rat pet
					{
							11198, 16250, 1
					},
					// Scorpia's offspring
					{
							5561, 13181, 2
					},
					// Spawn pet (Lizard shaman spawn pet)
					{
							11199, 16261, 1
					},
					// Vorki pet
					{
							8025, 21992, 2
					},
					// Kalphite princess (orange)
					{
							6637, 12654, 1
					},
					// Kalphite princess (green)
					{
							6638, 12647, 1
					},
					// Baby mole
					{
							6635, 12646, 2
					},
					// Snakeling green
					{
							2130, 12921, 2
					},
					// Snakeling Red
					{
							2131, 12939, 2
					},
					// Snakeling Blue
					{
							2132, 12940, 2
					},
					// Pet corporeal critter
					{
						8010, 22318, 2
					},
					// Lil' zik
					{
						8336, 22473, 2
					},
					// Custom item pets
					// Dragon claws pet for Ex Machina
					{
							11114, 16153, 1, 1
					},
					// Elder maul pet for Bluezia
					{
							11089, 16116, 1, 1
					},
					// Plank pet for Platform
					{
							11085, 16111, 1, 1
					},
					// Elysian spirit shield pet for (no one has it right now)
					{
							11051, 16050, 1, 1
					},
					// Armadyl godsword pet for I Solo
					{
							11038, 16034, 1, 1
					},
					// Rainbow Partyhat pet for Vitalzz
					{
							11034, 16030, 1, 1
					},
					// Blood money pet for Idsp
					{
							7315, 16018, 1, 1
					},
					// Twisted bow pet for Bluezia
					{
							11137, 16175, 1, 1
					},
					// Green partyhat pet for V17111771177
					{
							11174, 16211, 1, 1
					},
					// Yellow partyhat pet for V17111771177
					{
							11175, 16212, 1, 1
					},
					// Blue partyhat pet for Iraq4life
					{
							11157, 16188, 1, 1
					},
					// Kodai wand pet for Detroit
					{
							11158, 16189, 1, 1
					},

					// Custom npc pets below
					// Night beast/Superior Dark Beast for Josh
					{
							11015, 16010, 1
					},
					// Vasa Nistirio for Austin W
					{
							11016, 16011, 1
					},
					// Vespula for I Solo
					{
							11017, 16012, 1
					},
					// Camel for Mohammed
					{
							11018, 16013, 1
					},
					// Skotizo pet for Hvid
					{
							11019, 16014, 1
					},
					// Corporeal beast pet for Buying Gf69, he got banned, so given to Apetamer
					{
							11021, 16016, 1
					},
					// Cerberus pet for Afrozilla
					{
							11022, 16017, 1
					},
					// Tekton pet for Actiun, he quit so now given to Draco
					{
							11023, 16019, 1
					},
					// K'klic pet for Youtube, he got banned, so now it belongs to wespoonedyou
					{
							11024, 16020, 1
					},
					// Schoolgirl pet for 0nly
					{
							1915, 16021, 1
					},
					// Banker pet for Togiejj
					{
							11026, 16022, 1
					},
					// Death pet for Monk
					{
							11027, 16023, 1
					},
					// Nuclear smokedevil pet for Harry
					{
							11028, 16024, 1
					},
					// Wise Old Man for Trump Train
					{
							11029, 16025, 1
					},
					// Flaming pyrelord pet for Fuck Anxiety
					{
							11030, 16026, 1
					},
					// JalTok-Jad pet for Laith
					{
							11031, 16027, 1
					},
					// Demonic gorilla pet for Austin W
					{
							11032, 16028, 1
					},
					// Party Pete pet for Oshe
					{
							11033, 16029, 1
					},
					// Dad pet for I Am Groot
					{
							11035, 16031, 1
					},
					// Mounted terrorbird for Laith
					{
							11036, 16032, 1
					},
					// Mutant tarn pet for Ebk, he got banned, now Madiii owns it
					{
							11037, 16033, 1
					},
					// The Kendal pet for Dickbutt
					{
							11039, 16035, 1
					},
					// Icefiend pet for Fuck Anxiety
					{
							11040, 16036, 1
					},
					// The Inadequacy pet for Thuggahhh
					{
							11041, 16037, 1
					},
					// Donator/Hatius pet for Atrox, he's inactive so given to Obbyblitz
					{
							11042, 16038, 1
					},
					// Warped Jelly pet for Epara
					{
							11043, 16039, 1
					},
					// Mr. Mordaut pet for Donging
					{
							11044, 16040, 1
					},
					// Kalphite Princess pet for C0nz0le
					{
							11045, 16043, 1
					},
					// Mutated Glough pet for Trenbolone E
					{
							11046, 16044, 1
					},
					// The Everlasting pet for Thuggahhh
					{
							11047, 16045, 1
					},
					// Lucky impling pet for Trenbolone E
					{
							11048, 16046, 1
					},
					// Muttadile for Locals
					{
							11049, 16048, 1
					},
					// War tortoise for  (no one has it right now)
					{
							11050, 16049, 1
					},
					// TzKal-Zuk for Ilysian, he got banned, so Draco now owns it.
					{
							11052, 16051, 1
					},
					// Cow calf for Verticle
					{
							11053, 16053, 1
					},
					// Goblin for Verticle
					{
							11054, 16054, 1
					},
					// Hans pet for Frogger12345
					{
							11055, 16057, 1
					},
					// Treus Dayth pet for Bites
					{
							11056, 16058, 1
					},
					// The Untouchable pet for  (no one has it right now)
					{
							11058, 16059, 1
					},
					// Eve pet for Dela
					{
							11059, 16061, 1
					},
					// Giant roc pet for Russman887
					{
							11060, 16062, 1
					},
					// Genie pet for Yum
					{
							11062, 16073, 1
					},
					// Jal-xil pet for Sleds
					{
							11063, 16075, 1
					},
					// Lava dragon pet for Shattered
					{
							11064, 16077, 1
					},
					// Desourt pet for Shattered
					{
							11065, 16078, 1
					},
					// Demon butler pet for Zionist
					{
							11066, 16079, 1
					},
					// Penguin pet for Legends K0
					{
							11067, 16080, 1
					},
					// Black demon pet for Tankthee
					{
							11069, 16083, 1
					},
					// Anti-santa pet for Oatrix
					{
							11068, 16081, 1
					},
					// Fear reaper pet for Oatrix
					{
							11070, 16082, 1
					},
					// Durial321 pet for Phillip
					{
							11071, 16092, 1
					},
					// 50% Luke pet for Pewdipie
					{
							11072, 16094, 1
					},
					// Dark core pet for Buying Gf69, he got banned, so now belongs to Flame
					{
							318, 12816, 2
					},
					// Fareed pet for Trauhmatic
					{
							11078, 16101, 1
					},
					// Ent pet for K Trap
					{
							11079, 16102, 1
					},
					// Cave abomination pet for Locals
					{
							11080, 16105, 1
					},
					// Shifter pet for Trauhmatic
					{
							11081, 16106, 1
					},
					// Echned Zekin pet for Pontiff
					{
							11082, 16107, 1
					},
					// Black Skeletal wyvern pet for Shattered
					{
							11083, 16108, 1
					},
					// Delrith for Defiant
					{
							11084, 16110, 1
					},
					// Prospector percy pet for Subarashii
					{
							11087, 16114, 1
					},
					// Mi-Gor pet for Love Cats
					{
							11088, 16115, 1
					},
					// Ice Queen pet for Vitalzz
					{
							11090, 16117, 1
					},
					// Cow31337Killer pet for Nutinurchick
					{
							11091, 16118, 1
					},
					// Tortoise pet for Techno
					{
							11092, 16119, 1
					},
					// Jal-ImKot pet for Lose
					{
							11093, 16120, 1
					},
					// Cyan Corpoeal beast pet for Buying Gf69
					{
							11094, 16121, 1
					},
					// Glod pet for Pulled pork
					{
							11096, 16125, 1
					},
					// Skeletal wyvern pet for Wespoonedyou
					{
							11097, 16126, 1
					},
					// Growler pet for 69Isadinner2
					{
							11098, 16127, 1
					},
					// Guard pet for Im Superior
					{
							11099, 16128, 1
					},
					// Gnome child pet for Lose
					{
							11100, 16129, 1
					},
					// Voice of Yama for Loadin W8
					{
							11101, 16130, 1
					},
					// Broken Handz for Loadin W8
					{
							11102, 16131, 1
					},
					// Gargoyle pet for MansNotHot/Mr99 Skills
					{
							11103, 16132, 1
					},
					// Mcribb pet black for Shattered
					{
							11104, 16135, 1
					},
					// Hopleez pet for Samsoniiee
					{
							11105, 16137, 1
					},
					// Chair pet for Believe
					{
							11106, 16139, 1
					},
					// Nieve pet for Josh
					{
							11107, 16146, 1
					},
					// Fairy Godfather pet for Flame
					{
							11108, 16147, 1
					},
					// Clerris pet for YouWonderwho
					{
							11109, 16148, 1
					},
					// Tree spirit pet for Cat pewp
					{
							11110, 16149, 1
					},
					// Sea snake pet for 4fmp
					{
							11111, 16152, 1
					},
					// Abyssal orphan pet for Dox
					{
							5883, 13262, 2
					},
					// Troll child pet for Cat pewp
					{
							11115, 16154, 1
					},
					// Skeletal hellhound pet for Bloodsport
					{
							11116, 16156, 1
					},
					// Ankou pet for Muppets
					{
							11119, 16158, 1
					},
					// Nurse Tafani pet for Smack U
					{
							11120, 16159, 1
					},
					// Pig pet for Redbullkush
					{
							11121, 16160, 1
					},
					// Ping pet for X E D I U M
					{
							11122, 16161, 1
					},
					// Abyssal Sire pet M2the Achine
					{
							11123, 16162, 1
					},
					// Dragonkin pet for Pain
					{
							11126, 16164, 1
					},
					// Zanaris choir pet for Next Joke
					{
							11127, 16165, 1
					},
					// Ali the Snake Charmer pet for King Nick
					{
							11128, 16166, 1
					},
					// Army Commander pet for Bury
					{
							11129, 16167, 1
					},
					// Steve pet for Redbullkush
					{
							11130, 16168, 1
					},
					// Novice pet for Muppets
					{
							11131, 16169, 1
					},
					// Zombie protester pet for Dox
					{
							11132, 16170, 1
					},
					// Witch's experiment pet for (no one)
					{
							11133, 16171, 1
					},
					// Summoned Soul pet for Muppets
					{
							11135, 16173, 1
					},
					// Barbarian guard for Ralo
					{
							11136, 16174, 1
					},
					// Aviansie pet for Techno
					{
							11138, 16177, 1
					},
					// Dairy cow pet for Poor
					{
							11139, 16178, 1
					},
					// Pauline Polaris for Tv
					{
							11140, 16179, 1
					},
					// Bear pet for Samsoniiee
					{
							11141, 16180, 1
					},
					// Gnosi pet for Max Zeros
					{
							11142, 16181, 1
					},
					// Homunculus for Bigg Tim
					{
							11143, 16182, 1
					},
					// Vannaka pet for Coto
					{
							11155, 16184, 1
					},
					// Mogre pet for Chip
					{
							11156, 16185, 1
					},
					// Ancient Wyvern pet for Yeha
					{
							11159, 16190, 1
					},
					// Terror dog for Chip
					{
							11160, 16191, 1
					},
					// Ducklings for Vippy
					{
							11161, 16192, 1
					},
					// Lamb for Gold Ownze
					{
							11162, 16193, 1
					},
					// Entrana firebird for M2the Achine
					{
							11163, 16194, 1
					},
					// Zambo pet for Dirty Specz
					{
							11168, 16200, 1
					},
					// Ignisia pet for Dirty Specz
					{
							11169, 16201, 1
					},
					// Nezikchened pet for Askeladden
					{
							11170, 16202, 1
					},
					// Noon for Eat My Taint
					{
							7891, 21748, 2
					},
					// Ogre chieftain pet for Pot Noodle
					{
							11176, 16213, 1
					},
					// R4ng3rNo0b889 pet for Tattoo Snob
					{
							11177, 16214, 1
					},
					// Leprechaun pet for Inb4 Krazy P
					{
							11178, 16215, 1
					},
					// Yak pet for Cookie
					{
							11179, 16216, 1
					},
					// Pyromancer for Verkys
					{
							11180, 16217, 1
					},
					// Long-tailed Wyvern for Hashtag Hate
					{
							11181, 16218, 1
					},
					// Wolf pet for Criboli
					{
							11182, 16219, 1
					},
					// Winter Elemental pet for Mini Frost
					{
							11183, 16220, 1
					},
					// Wise Old Man pet for Tatt0o Snob
					{
							11184, 16221, 1
					},
					// Cosmic Being for Karmas love
					{
							11185, 16222, 1
					},
					// Loar Shade for Rekt Rsps
					{
							11186, 16223, 1
					},
					// Blue Tekton for Locals
					{
							11187, 16233, 1
					},
					// Reanimated dragon for Hashtag Hate
					{
							11188, 16234, 1
					},
					// Catablepon for Ipker
					{
							11189, 16235, 1
					},
					// Evil creature for 41067
					{
							11190, 16236, 1
					},
					// Baba Yaga for Verkys
					{
							11191, 16237, 1
					},
					// Chaeldar for No one
					{
							11192, 16238, 1
					},
					// Tip Jar for Beef
					{
							11193, 16239, 1, 1
					},
					// Palm tree for Beef
					{
							11194, 16240, 1, 1
					},
					// Puffin for Lewtz
					{
							11195, 16241, 1
					},
					// Baby Roc for Lewtz
					{
							11196, 16242, 1
					},
					// Red prince black dragon for Askeladden
					{
							11197, 16243, 1
					},
					// Purple K'klic pet for Wespoonedyou
					{
							11200, 16262, 1
					},
					// Red skotizo pet for Red
					{
							11202, 16314, 1
					},
					// Golden plank pet for Crip Walkin
					{
							11203, 16318, 1, 1
					},
					// Pink palm tree for Chamo
					{
							11204, 16319, 1, 1
					},
					// K'klic pet, green for 4hed
					{
							11205, 16327, 1
					},
					// Death pet with lime robes for Syntaxed
					{
							11206, 16328, 1
					},
					// Pink Mogre pet for Tag
					{
							11207, 16332, 1
					},
					// TzKal-Zuk purple for Thuggahhh
					{
						11208, 16344, 1
					},
					// Black Corporeal beast for Tom Bilotti
					{
						11209, 16350, 1
					},
					// Schoolgirl (brown skin) for Youll Lose
					{
						11210, 16362, 1
					},
					// Death pet for Foul
					{
						11211, 16398, 1
					},
					// Purple K'klic pet for Half life
					{
						11212, 16399, 1
					},
					// White Noon with red eyes for Middle
					{
						11213, 16414, 1
					},
					// Vespula pet, purple and gold for Dameon
					{
						11214, 16429, 1
					}
					//@formatter:on
			};
}
