package ca.uwhvz.hvz.Util;

import ca.uwhvz.hvz.data.Config;
import ca.uwhvz.hvz.data.Language;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import ca.uwhvz.hvz.HvZ;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Generalized utility class for shortcut methods
 */
@SuppressWarnings("WeakerAccess")
public class Util {

	public static final BlockFace[] faces = new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};

	/** Log a message to console prefixed with the plugin's name
	 * @param s Message to log to console
	 */
	public static void log(String s) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3&lHvZ&7] " + s));
	}

	/** Send a warning to console prefixed with the plugin's name
	 * @param s Message to log to console
	 */
	public static void warning(String s) {
		String warnPrefix = "&7[&e&lHvZ&7] ";
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
				warnPrefix + "&eWARNING: " + s));
	}

	/** Send a colored message to a player or console
	 * @param sender Receiver of message
	 * @param s Message to send
	 * @deprecated Use {@link #scm(CommandSender, String)} instead
	 */
	@Deprecated
	public static void msg(CommandSender sender, String s) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
	}

	/** Send a colored message to a player or console
	 * @param sender Receiver of message
	 * @param s Message to send
	 */
	public static void scm(CommandSender sender, String s) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
	}

	/** Broadcast a message prefixed with plugin name
	 * @param s Message to send
	 */
	public static void broadcast(String s) {
		Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', HvZ.getPlugin().getLang().prefix + " " + s));
	}

	/** Shortcut for adding color to a string
	 * @param string String including color codes
	 * @return Formatted string
	 */
	public static String getColString(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

    /** Check if a string is an Integer
     * @param string String to get
     * @return True if string is an Integer
     */
	public static boolean isInt(String string) {
		try {
			Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	public static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static BlockFace getSignFace(BlockFace face) {
		switch (face) {
			case WEST:
				return BlockFace.SOUTH;
			case SOUTH:
				return BlockFace.EAST;
			case EAST:
				return BlockFace.NORTH;
			default:
				return BlockFace.WEST;
		}
	}

	/** Clear the inventory of a player including equipment
	 * @param player Player to clear inventory
	 */
	public static void clearInv(Player player) {
		player.getInventory().clear();
		player.getEquipment().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		player.updateInventory();
	}

	/** Convert a list of UUIDs to a string of player names
	 * @param uuid UUID list to convert
	 * @return String of player names
	 */
	public static List<String> convertUUIDListToStringList(List<UUID> uuid) {
		List<String> winners = new ArrayList<>();
		for (UUID id : uuid) {
			winners.add(Objects.requireNonNull(Bukkit.getPlayer(id)).getName());
		}
		return winners;
	}

	public static String translateStop(List<String> win) {
		StringBuilder bc = null;
		int count = 0;
		for (String s : win) {
			count++;
			if (count == 1) bc = new StringBuilder(s);
			else if (count == win.size()) {
				assert bc != null;
				bc.append(", and ").append(s);
			} else {
				assert bc != null;
				bc.append(", ").append(s);
			}
		}
		if (bc != null)
			return bc.toString();
		else
			return "No one";
	}

	public static void shootFirework(Location l) {
		assert l.getWorld() != null;
		Firework fw = l.getWorld().spawn(l, Firework.class);
		FireworkMeta fm = fw.getFireworkMeta();
		List<Color> c = new ArrayList<>();
		c.add(Color.GREEN);
		c.add(Color.BLUE);
		FireworkEffect e = FireworkEffect.builder().flicker(true).withColor(c).withFade(c).with(Type.BALL_LARGE).trail(true).build();
		fm.addEffect(e);
		fm.setPower(2);
		fw.setFireworkMeta(fm);
	}

	public static void harmlessLightning(Player p) {
		Location l = p.getLocation();
		assert l.getWorld() != null;
		p.setInvulnerable(true);
		l.getWorld().spawn(l, LightningStrike.class);
		p.setInvulnerable(false);
	}

	@SuppressWarnings("deprecation")
    public static boolean isAttached(Block base, Block attached) {
	    if (attached.getType() == Material.AIR) return false;

		MaterialData bs = attached.getState().getData();
		//BlockData bs = attached.getBlockData();

		if (!(bs instanceof Attachable)) return false;

		Attachable at = (Attachable) bs;
		BlockFace face = at.getAttachedFace();

		return attached.getRelative(face).equals(base);
	}

    /** Check if running a specific version of Minecraft or higher.
     * @param major Major version of Minecraft to check (Will most likely always be 1)
     * @param minor Minor version of Minecraft to check
     * @return True if the server is running this version or higher
     */
	@SuppressWarnings("SameParameterValue")
	public static boolean isRunningMinecraft(int major, int minor) {
		int maj = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].split("_")[0].replace("v", ""));
		int min = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].split("_")[1]);
		return maj >= major && min >= minor;
	}

	/** Check if a material is a wall sign
	 * <p>Due to sign material changes in 1.14 this method checks for both 1.13 and 1.14+</p>
	 * @param item Material to check
	 * @return True if material is a wall sign
	 */
	public static boolean isWallSign(Material item) {
		if (isRunningMinecraft(1, 16)) {
			switch (item) {
				case CRIMSON_WALL_SIGN:
				case WARPED_WALL_SIGN:
					return true;
			}
		} if (isRunningMinecraft(1, 14)) {
			switch (item) {
				case ACACIA_WALL_SIGN:
				case BIRCH_WALL_SIGN:
				case DARK_OAK_WALL_SIGN:
				case JUNGLE_WALL_SIGN:
				case OAK_WALL_SIGN:
				case SPRUCE_WALL_SIGN:
					return true;
			}
		} else {
			return item == Material.getMaterial("WALL_SIGN");
		}
		return false;
	}

    /** Check if a material is a wall sign
     * <p>Due to sign material changes in 1.14 this method checks for both 1.13 and 1.14+</p>
     * @param block Block to check
     * @return True if block is a wall sign
     */
	public static boolean isWallSign(Block block) {
	    return isWallSign(block.getType());
    }

    /** Check if a method exists
     * @param c Class that contains this method
     * @param methodName Method to check
     * @param parameterTypes Parameter types if the method contains any
     * @return True if this method exists
     */
    public static boolean methodExists(final Class<?> c, final String methodName, final Class<?>... parameterTypes) {
        try {
            c.getDeclaredMethod(methodName, parameterTypes);
            return true;
        } catch (final NoSuchMethodException | SecurityException e) {
            return false;
        }
    }

    /** Check if a class exists
     * @param className Class to check for existence
     * @return True if this class exists
     */
    public static boolean classExists(final String className) {
        try {
            Class.forName(className);
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

	/** Check if player is on a team
	 * @param player player to check
	 * @param team team name
	 * @return True if player is in team
	 */
    public static boolean inTeam(Player player, String team){
		return Objects.requireNonNull(Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getTeam(team)).hasPlayer(player);
	}

	/** Change a player's team
	 * @param player player to change
	 * @param team team to change to
	 */
	public static void changeTeam(Player player, String team){
		Bukkit.getScoreboardManager().getMainScoreboard().getTeam(team).addPlayer(player);
	}

	/** Check if player is in the game
	 * @param player player to check
	 * @return True if player is in game
	 */
	public static boolean inGame(Player player) {
		return inTeam(player,"player") ||
				inTeam(player, "human") ||
				inTeam(player, "zombie") ||
				inTeam(player,"stunned_zombie") ||
				inTeam(player, "mod") ||
				inTeam(player, "spectator");
	}

	/** Check if a location is in the arena
	 * @param l Location to check
	 * @return True if this location is in the arena
	 */
	public static boolean inArena(final Location l) {
		Integer x_1 = Integer.parseInt(Config.corner1.split(",")[0]);
		Integer z_1 = Integer.parseInt(Config.corner1.split(",")[1]);
		Integer x_2 = Integer.parseInt(Config.corner2.split(",")[0]);
		Integer z_2 = Integer.parseInt(Config.corner2.split(",")[1]);

		if (l.getX() < Math.max(x_1,x_2) && l.getX() > Math.min(x_1,x_2) &&
				l.getZ() < Math.max(z_1,z_2) && l.getZ() > Math.min(z_1,z_2)) {
			return true;
		}
		return false;
	}

	/** Tp a player to the arena if they're outside
	 * @param p Player to tp
	 * @return True if this location is in the arena
	 */
	public static void tpArena(final Player p) {
		Location l = p.getLocation();
		Integer x_1 = Integer.parseInt(Config.corner1.split(",")[0]);
		Integer z_1 = Integer.parseInt(Config.corner1.split(",")[1]);
		Integer x_2 = Integer.parseInt(Config.corner2.split(",")[0]);
		Integer z_2 = Integer.parseInt(Config.corner2.split(",")[1]);

		if (l.getX() > Math.max(x_1,x_2)) {
			l.setX(Math.max(x_1,x_2));
		}
		if (l.getX() > Math.min(x_1,x_2)) {
			l.setX(Math.min(x_1,x_2));
		}
		if (l.getZ() < Math.max(z_1,z_2)) {
			l.setZ(Math.max(z_1,z_2));
		}
		if (l.getZ() > Math.min(z_1,z_2)) {
			l.setZ(Math.min(z_1, z_2));
		}
		l.setY(p.getWorld().getHighestBlockAt(l).getY() +1);
		if (l.getX() != p.getLocation().getX() && l.getZ() != p.getLocation().getZ()) {
			p.teleport(l);
		}
	}

	public static List<String> getzoms() {
		List <String> zoms = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers())
		{
			if (Util.inGame(p) && Util.inTeam(p,"zombie")) {
				zoms.add(p.getName());
			}}
		return zoms;
	}

	/** Change the status of a player to be that of a stunned zombie
	 * NOTE: Does not change their team
	 * @param player player to toggle
	 * @param b true if stunned, false otherwise
	 * @param plugin the plugin itself needs to be passed
	 * @return nothing
	 */
	public static void stunned(Player player, boolean b, Plugin plugin) {
		if (b) {
			/*for (Player player2 : Bukkit.getOnlinePlayers()) {
				if (player2 == null || ! Util.inGame(player2) || Util.inTeam(player2,"mod") ||
						Util.inTeam(player2, "spectator")) continue; //mods and specs see all
				player.hidePlayer(plugin, player2);
				player2.hidePlayer(plugin, player); // Stunned zombies can't see or be seen
			}*/
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
			player.setInvulnerable(true);
			player.setCanPickupItems(false);
			player.setSaturation(10);
			player.setCollidable(false);
			player.setGlowing(true);
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE,1));
		}
		else {
			/*for (Player player2 : Bukkit.getOnlinePlayers()) {
				if (player2 == null || ! Util.inGame(player2) || Util.inTeam(player2, "stunned_zombie")) continue;
				player.showPlayer(plugin, player2);
				player2.showPlayer(plugin, player); // Revert to visible zoms
			}*/
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			player.setInvulnerable(false);
			player.setCanPickupItems(true);
			player.setSaturation(10F);
			player.setCollidable(true);
			player.setGlowing(false);
			player.removePotionEffect(PotionEffectType.BLINDNESS);}
	}

	public static boolean is_wool(Block b) {
		switch(b.getType()) {
			case BLACK_WOOL:
			case BLUE_WOOL:
			case BROWN_WOOL:
			case CYAN_WOOL:
			case GRAY_WOOL:
			case GREEN_WOOL:
			case LIGHT_BLUE_WOOL:
			case LIGHT_GRAY_WOOL:
			case LIME_WOOL:
			case MAGENTA_WOOL:
			case ORANGE_WOOL:
			case PINK_WOOL:
			case PURPLE_WOOL:
			case RED_WOOL:
			case WHITE_WOOL:
			case YELLOW_WOOL:
				return true;
		}
		return false;

	}

	public static void setGlowing(boolean b) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (Util.inTeam(p,"human") || Util.inTeam(p,"zombie")) {
				p.setGlowing(b);
			}
		}
	}

	public static void freeze(Player player, boolean b) {
		if (b) {
			player.setWalkSpeed(0.0001F);
			player.setSwimming(false);
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 23423525, -10, false, false));
			player.setFoodLevel(20);
			player.setAllowFlight(false);
			player.setFlying(false);
			player.setInvulnerable(true);
		}
		else {
			player.setWalkSpeed(0.2F);
			player.removePotionEffect(PotionEffectType.JUMP);
			player.setInvulnerable(false);
		}
	}

	// int time is in seconds
	public static void tempBeacon(Player p, int time, Plugin plugin) {
		Map<Location, BlockState> to_rebuild = new HashMap<>();
        Map<Location,ItemStack[]> to_rebuild_data = new HashMap<>();
		int x = p.getLocation().getBlockX();
		int y = p.getLocation().getBlockY()-2; //builds the beacons right under them
		int z = p.getLocation().getBlockZ();
		if (p.isInsideVehicle()) {x--; z--;};
		World w = p.getWorld();
		to_rebuild.put(new Location(w,x,y,z),w.getBlockAt(x,y,z).getState());
		w.getBlockAt(x,y,z).setType(Material.BEACON);
		for (int i = 0; i <= 29; ++i) {
			Block b = w.getBlockAt(x, (y+1) + i, z);
			if (b.getType().equals(Material.AIR)) continue;
			to_rebuild.put(new Location(w,x,(y+1) + i,z),b.getState());
			if (b instanceof Chest) {
            to_rebuild_data.put(new Location(w,x,(y+1) + i,z),((Chest) b).getBlockInventory().getStorageContents());}
			if (Util.inTeam(p,"human")) {
				b.setType(Material.LIGHT_BLUE_STAINED_GLASS);
			}
			else if (Util.inTeam(p,"zombie")) {
				b.setType(Material.GREEN_STAINED_GLASS);
			}
			else {
				b.setType(Material.GLASS);
			}
		}
		for (int xPoint = x-1; xPoint <= x+1; xPoint++) {
			for (int zPoint = z-1; zPoint <= z+1; zPoint++) {
				Block b = w.getBlockAt(xPoint,y-1,zPoint);
				to_rebuild.put(new Location(w,xPoint,y-1,zPoint),b.getState());
				if (b instanceof Chest) {
                to_rebuild_data.put(new Location(w,xPoint,y-1,zPoint),((Chest) b).getBlockInventory().getStorageContents());}
				b.setType(Material.IRON_BLOCK);
			}
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				for (Map.Entry<Location,BlockState> entry : to_rebuild.entrySet()){
					w.getBlockAt(entry.getKey()).setType(entry.getValue().getType());
					w.getBlockAt(entry.getKey()).setBlockData(entry.getValue().getBlockData());
				}
                for (Map.Entry<Location,ItemStack[]> entry : to_rebuild_data.entrySet()){
                	Chest c = (Chest) w.getBlockAt(entry.getKey());
                    c.getBlockInventory().setStorageContents(entry.getValue());
                }
			}
		},time*20);

//		int task1 = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
//			@Override
//			public void run() {
//				Location l = p.getLocation();
//				while (l.getY() < 150) {
//					p.getWorld().spawnParticle(Particle.DRAGON_BREATH, l, 1);
//					l.setY(l.getY() + 1);
//				}
//
//			}
//		}, 0L, 20L);
//		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//			public void run() {
//				Bukkit.getScheduler().cancelTask(task1);
//			}
//		},time*20);
	}

	/** Get the death message when a player dies of natural causes (non-entity involved deaths)
	 * @param dc Cause of the damage
	 * @param name Name of the player
	 * @return Message that will be sent when the player dies
	 */
	public static String getDeathString(EntityDamageEvent.DamageCause dc, String name, Language lang) {
		switch (dc) {
			case ENTITY_EXPLOSION:
			case BLOCK_EXPLOSION:
				return (lang.death_explosion.replace("<player>", name));
			case CUSTOM:
				return (lang.death_custom.replace("<player>", name));
			case FALL:
				return (lang.death_fall.replace("<player>", name));
			case FALLING_BLOCK:
				return (lang.death_falling_block.replace("<player>", name));
			case FIRE:
			case FIRE_TICK:
				return (lang.death_fire.replace("<player>", name));
			case PROJECTILE:
				return (lang.death_projectile.replace("<player>", name));
			case LAVA:
				return (lang.death_lava.replace("<player>", name));
			case MAGIC:
				return (lang.death_magic.replace("<player>", name));
			case SUICIDE:
				return (lang.death_suicide.replace("<player>", name));
			default:
				return (lang.death_other_cause.replace("<player>", name).replace("<cause>", dc.toString().toLowerCase()));
		}
	}

	/** Get the death message when a player is killed by an entity
	 * @param name Name of player whom died
	 * @param entity Entity that killed this player
	 * @return Death string including the victim's name and the killer
	 */
	public static String getKillString(String name, Entity entity, Language lang) {
		switch (entity.getType()) {
			case ARROW:
				if (!isShotByPlayer(entity)) {
					return (lang.death_skeleton.replace("<player>", name));
				} else {
					return getPlayerKillString(name, getShooter(entity), true, lang);
				}
			case PLAYER:
				return getPlayerKillString(name, ((Player) entity), false, lang);
			case ZOMBIE:
				return (lang.death_zombie.replace("<player>", name));
			case SKELETON:
			case SPIDER:
				return (lang.death_spider.replace("<player>", name));
			case DROWNED:
				return (lang.death_drowned.replace("<player>", name));
			case TRIDENT:
				return (lang.death_trident.replace("<player>", name));
			case STRAY:
				return (lang.death_stray.replace("<player>", name));
			default:
				return (lang.death_other_entity.replace("<player>", name));
		}
	}

	private static String getPlayerKillString(String victimName, Player killer, boolean projectile, Language lang) {
		String weapon;
		//killer.sendMessage("here in getPlayerKillString1");
		if (projectile) {
			weapon = "bow and arrow";
		} else if (killer.getInventory().getItemInMainHand().getType() == Material.AIR) {
			weapon = "fist";
		} else {
			weapon = killer.getInventory().getItemInMainHand().getType().name().toLowerCase();
		}
		//killer.sendMessage("here in getPlayerKillString2");
		return (lang.death_player.replace("<player>", victimName)
				.replace("<killer>", killer.getName())
				.replace("<weapon>", weapon));
	}

	/** Check if the shooter was a player
	 * @param projectile The arrow which hit the player
	 * @return True if the arrow was shot by a player
	 */
	public static boolean isShotByPlayer(Entity projectile) {
		return projectile instanceof Projectile && ((Projectile) projectile).getShooter() instanceof Player;
	}

	/** Get the shooter of this arrow
	 * @param projectile The arrow in question
	 * @return The player which shot the arrow
	 */
	public static Player getShooter(Entity projectile) {
		return (Player) ((Projectile) projectile).getShooter();
	}

	public static void register_teams() {
		Scoreboard board = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
		List<String> names = new ArrayList<String>();
		for (Team team : board.getTeams()) {
			names.add(team.getName());
		}
		if (! names.contains("player")) {
			Team pteam = board.registerNewTeam("player");
			pteam.setPrefix("[Player]");
		}
		if (! names.contains("zombie")) {
			Team zteam = board.registerNewTeam("zombie");
			zteam.setAllowFriendlyFire(false);
			zteam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
			//zteam.setColor(ChatColor.GREEN);
		}
		if (! names.contains("stunned_zombie")) {
			Team szteam = board.registerNewTeam("stunned_zombie");
			szteam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
			szteam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
		}
		if (! names.contains("human")) {
			Team hteam = board.registerNewTeam("human");
			hteam.setAllowFriendlyFire(false);
			hteam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
			//hteam.setColor(ChatColor.AQUA);
		}
		if (! names.contains("mod")) {
			Team mteam = board.registerNewTeam("mod");
			mteam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
			mteam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
			mteam.setPrefix("[Mod]");
			mteam.setColor(ChatColor.GOLD);
		}
		if (! names.contains("spectator")) {
			Team steam = board.registerNewTeam("spectator");
			steam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
			steam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
			steam.setPrefix("[Spec]");
			steam.setColor(ChatColor.LIGHT_PURPLE);
		}
		if (! (Arrays.asList(board.getObjectives().stream().map(x -> x.getName()).toArray())).contains("showhealth")) {
			Objective objective = (Objective) board.registerNewObjective("showhealth", "health", "/20");
		}
	}

	public static void unregister_teams() {
		Scoreboard board = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
		List<String> names = new ArrayList<String>();
		for (Team team : board.getTeams()) {
			names.add(team.getName());
		}
		if (names.contains("player")) {
			board.getTeam("player").unregister();
		}
		if (names.contains("zombie")) {
			board.getTeam("zombie").unregister();
		}
		if (names.contains("stunned_zombie")) {
			board.getTeam("stunned_zombie").unregister();
		}
		if (names.contains("human")) {
			board.getTeam("human").unregister();
		}
		if (names.contains("mod")) {
			board.getTeam("mod").unregister();
		}
		if (names.contains("spectator")) {
			board.getTeam("spectator").unregister();
		}

	}

	public static PotionEffect random_potion() {
		List<PotionEffect> potions = null;
		potions.add(new PotionEffect(PotionEffectType.REGENERATION,90,1));
		potions.add(new PotionEffect(PotionEffectType.REGENERATION,45,2));
		potions.add(new PotionEffect(PotionEffectType.SPEED,90,1));
		potions.add(new PotionEffect(PotionEffectType.SPEED,30,2));
		potions.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,90,1));
		potions.add(new PotionEffect(PotionEffectType.NIGHT_VISION,300,1));
		potions.add(new PotionEffect(PotionEffectType.DOLPHINS_GRACE,120,1));
		potions.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,120,1));

		Collections.shuffle(potions);
		return potions.get(0);

	}



}
