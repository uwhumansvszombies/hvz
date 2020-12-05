package ca.uwhvz.hvz.data;

import ca.uwhvz.hvz.HvZ;
import ca.uwhvz.hvz.Util.Util;
import net.minecraft.server.v1_16_R2.AttributeModifiable;
import net.minecraft.server.v1_16_R2.EntityInsentient;
import net.minecraft.server.v1_16_R2.GenericAttributes;
import net.minecraft.server.v1_16_R2.PathEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

/**
 * These are events that will be "run" during the game ever 5-10 minutes
 */

public class Events {

	//Basic settings
	private final HvZ plugin;
	private static File eventFile;
	private static FileConfiguration events;
	public boolean escort_event;
	public boolean supply_drop_event;
	public boolean point_hold_event;

	public Events(HvZ plugin) {
		this.plugin = plugin;
		loadEventFile();
	}

	private void loadEventFile() {
		if (eventFile == null) {
			eventFile = new File(plugin.getDataFolder(), "Events.yml");
		}
		if (!eventFile.exists()) {
			plugin.saveResource("Events.yml", false);
			Util.log("&7New Events.yml created");
		}
		events = YamlConfiguration.loadConfiguration(eventFile);
		loadEvents();
		Util.log("&7Events.yml loaded");
	}


	private void loadEvents() {
		escort_event = events.getBoolean("escort");
		supply_drop_event = events.getBoolean("supply_drop");
		point_hold_event = events.getBoolean("pont_hold");
		}

	public Configuration getEvents() {
		return events;
	}

	public static void escort(Plugin plugin, Map<String, String> locations) {
		List<Map.Entry<String, String>> vals = new ArrayList<>(locations.entrySet());
		Collections.shuffle(vals);
		Map.Entry<String, String> rSet1 = vals.get(0);
		Map.Entry<String, String> rSet2 = vals.get(1);
		World w = null;
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if (Util.inGame(p)) {
				w = p.getWorld();
				break;
			}
		}
		if (w == null) {
			return;
		}
		assert w != null;
		String start_loc = rSet1.getValue();
		String end_loc = rSet2.getValue();
		//Util.broadcast(start_loc);
		Location start = new Location(w,Integer.parseInt(start_loc.split(",")[0]),
				Integer.parseInt(start_loc.split(",")[1]),
				Integer.parseInt(start_loc.split(",")[2]));
		Location end = new Location(w,Integer.parseInt(end_loc.split(",")[0]),
				Integer.parseInt(end_loc.split(",")[1]),
				Integer.parseInt(end_loc.split(",")[2]));

		Creature entity = (Creature) w.spawnEntity(start, EntityType.VILLAGER);
		entity.setMaxHealth(250.0);
		entity.setHealth(250.0);
		entity.setTicksLived(20 * 20);
		entity.setRemoveWhenFarAway(false);
		entity.setCanPickupItems(false);
		entity.setGlowing(true);
		entity.setCustomName("EscortMod");
		entity.getEquipment().setHelmet(new ItemStack(Material.TURTLE_HELMET, 1));
		entity.getEquipment().setHelmetDropChance(0.0F);
		entity.getEntityId();
		entity.getTarget();
		w.getBlockAt(end).setType(Material.EMERALD_BLOCK);
		Util.broadcast("An Escort Mod has spawned at the <name>!".replace("<name>",rSet1.getKey()));
		Util.broadcast("Humans should deliver them to the <name>!".replace("<name>", rSet2.getKey()));
		Events.EntityFollow(plugin, entity, end,0.25F);

	}

	public static void EntityFollow(final Plugin plugin, final Entity entity , final Location end, final double speed)
	{
		new BukkitRunnable()
		{
			public void run()
			{
				if ((!entity.isValid()))
				{
					this.cancel();
				}
				PotionEffect pot = Util.random_potion();
				if (entity.getLocation().distance(end) < 5) {
					for (Player p : plugin.getServer().getOnlinePlayers()) {
						if (Util.inTeam(p,"human")) {
							p.addPotionEffect(pot);
						}
					}
					entity.getWorld().getBlockAt(end).setType(Material.AIR);
					this.cancel();
				}
				if (entity.isDead()){
					for (Player p : plugin.getServer().getOnlinePlayers()) {
						if (Util.inTeam(p,"zombie") || Util.inTeam(p,"stunned_zombie")) {
							p.addPotionEffect(pot);
						}
					}
					this.cancel();
				}
				Player player = null;
				double distance = Double.MAX_VALUE;
				for(Player p : Bukkit.getServer().getOnlinePlayers())
				{
					if(entity.getLocation().distance(p.getLocation()) < distance)
					{
						distance = entity.getLocation().distance(p.getLocation());
						player = p;
					}
				}
				if (player != null)
				{
					net.minecraft.server.v1_16_R2.Entity entity2 = ((CraftEntity) entity).getHandle();
					((EntityInsentient) entity2).getNavigation().a(2);
					Object entityObject = ((CraftEntity) entity).getHandle();
					Location targetLocation = player.getLocation();
					PathEntity path;
					path = ((EntityInsentient) entityObject).getNavigation().a(targetLocation.getX() + 1, targetLocation.getY(), targetLocation.getZ() + 1,1);
					if (path != null)
					{
						((EntityInsentient) entityObject).getNavigation().a(path, 1.0D);
						((EntityInsentient) entityObject).getNavigation().a(2.0D);
					}
					AttributeModifiable attributes = ((EntityInsentient)((CraftEntity)entity).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
					attributes.setValue(speed);
				}
				entity.getWorld().spawnEntity(entity.getLocation(),EntityType.FIREWORK);
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	public static void capture(Plugin plugin, Map<String, String> locations) {
		List<Map.Entry<String, String>> vals = new ArrayList<>(locations.entrySet());
		Collections.shuffle(vals);
		Map.Entry<String, String> rSet1 = vals.get(0);
		World w = null;
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if (Util.inGame(p)) {
				w = p.getWorld();
				break;
			}
		}
		if (w == null) {
			return;
		}
		assert w != null;
		String start_loc = rSet1.getValue()	;
		//Util.broadcast(start_loc);
		Location start = new Location(w,Integer.parseInt(start_loc.split(",")[0]),
				Integer.parseInt(start_loc.split(",")[1]),
				Integer.parseInt(start_loc.split(",")[2]));

		Creature entity = (Creature) w.spawnEntity(start, EntityType.VILLAGER);
		entity.setMaxHealth(50.0);
		entity.setHealth(50.0);
		entity.setTicksLived(1);
		entity.setRemoveWhenFarAway(false);
		entity.setCanPickupItems(false);
		entity.setGlowing(true);
		entity.setCustomName("PointCaptureMod");
		entity.getEquipment().setHelmet(new ItemStack(Material.TURTLE_HELMET, 1));
		entity.getEquipment().setHelmetDropChance(0.0F);
		entity.getEntityId();
		entity.getTarget();
		entity.setAI(false);

		Util.broadcast("A Point Capture Mod has spawned at the <name>!".replace("<name>",rSet1.getKey()));
		Util.broadcast("The coordinates are <coords>!".replace("<coords>",rSet1.getValue()));
		Util.broadcast("Humans win this mission if the mod survives for 3 minutes!");

		new BukkitRunnable()
		{
			public void run()
			{
				if (!entity.isValid())
				{
					this.cancel();
				}
				PotionEffect pot = Util.random_potion();
				if (entity.getTicksLived() > 3600) { // Human win condition
					for (Player p : plugin.getServer().getOnlinePlayers()) {
						if (Util.inTeam(p,"human")) {
							p.addPotionEffect(pot);
						}
					}
					Util.broadcast("Humans have succesfully protected the mod at the <name>!".replace("<name",rSet1.getKey()));
					this.cancel();
				}
				if (entity.isDead() || entity.getHealth() == 0){ // Zombie win condition
					for (Player p : plugin.getServer().getOnlinePlayers()) {
						if (Util.inTeam(p,"zombie") || Util.inTeam(p,"stunned_zombie")) {
							p.addPotionEffect(pot);
						}
					}
					Util.broadcast("Zombies have succesfully slain the mod at the <name>!".replace("<name",rSet1.getKey()));
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 0L, 20L);

	}

	/** Run a supply drop event
	 * @param plugin the plugin HvZ itself
	 * @param locations a map of the location names to the location as a string (x,y,z)
	 * @return Nothing
	 */

	public static void supply_drop(Plugin plugin, Map<String, String> locations) {
		List<Map.Entry<String, String>> vals = new ArrayList<>(locations.entrySet());
		Collections.shuffle(vals);
		Map.Entry<String, String> rSet1 = vals.get(0);
		World w = null;
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if (Util.inGame(p)) {
				w = p.getWorld();
				break;
			}
		}
		if (w == null) {
			return;
		}
		assert w != null;
		String start_loc = rSet1.getValue()	;
		//Util.broadcast(start_loc);
		Location start = new Location(w,Integer.parseInt(start_loc.split(",")[0]),
				Integer.parseInt(start_loc.split(",")[1]),
				Integer.parseInt(start_loc.split(",")[2]));

		Block b = w.getBlockAt(start);
		b.setType(Material.CHEST);
		Chest c = (Chest) b.getState();
		Inventory i = c.getBlockInventory();
		double p = Math.random();
		if (p < 0.5) i.addItem(new ItemStack(Material.GOLDEN_APPLE, (int) (p * 10)));
		else i.addItem(new ItemStack(Material.GOLDEN_APPLE, 2));
		p = Math.random();
		if (p < 0.5) i.addItem(new ItemStack(Material.STONE_SWORD, 1));
		else if (p < 0.7) i.addItem(new ItemStack(Material.GOLDEN_SWORD, 1));
		else if (p < 0.8) i.addItem(new ItemStack(Material.IRON_AXE, 1));
		p = Math.random();
		if (p < 0.2) i.addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE,1));
		else if (p < 0.5) i.addItem(new ItemStack(Material.ROTTEN_FLESH,(int) (p*10)));
		p = Math.random();
		if (p < 0.4) i.addItem(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
		else if (p < 0.7) i.addItem(new ItemStack(Material.IRON_LEGGINGS, 1));
		else i.addItem(new ItemStack(Material.GOLDEN_LEGGINGS, 1));
		p = Math.random();
		if (p < 0.3) i.addItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
		else if (p < 0.6) i.addItem(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
		else if (p < 0.8) i.addItem(new ItemStack(Material.IRON_CHESTPLATE, 1));
		else i.addItem(new ItemStack(Material.GOLDEN_CHESTPLATE, 1));
		p = Math.random();
		i.addItem(new ItemStack(Material.EMERALD, (int) p * 10));
		Util.broadcast("A Supply Drop has spawned at the <name>!".replace("<name>",rSet1.getKey()));
		Util.broadcast("The coordinates are <coords>!".replace("<coords>",rSet1.getValue()));
		Util.broadcast("Rewards are first come first served!");



		new BukkitRunnable()
		{
			public void run() {
				if (c.getBlockInventory().isEmpty()) {
					b.setType(Material.AIR);
					Util.broadcast("The Supply Drop at the <name> has been completely looted!".replace("<name>", rSet1.getKey()));
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 0L, 20L);

	}
}