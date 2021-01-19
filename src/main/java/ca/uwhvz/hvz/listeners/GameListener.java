package ca.uwhvz.hvz.listeners;

import ca.uwhvz.hvz.HvZ;
import ca.uwhvz.hvz.data.*;
import ca.uwhvz.hvz.Util.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.List;
import java.util.UUID;

/**
 * Internal event listener
 */
public class GameListener implements Listener {

	private HvZ plugin;
	private Language lang;
	private String tsn = ChatColor.GOLD + "TrackingStick " + ChatColor.GREEN + "Uses: ";
	private ItemStack trackingStick;
	private ItemStack head = new ItemStack(Material.ZOMBIE_HEAD,1);

	public GameListener(HvZ plugin) {
		this.plugin = plugin;
		this.lang = plugin.getLang();
		ItemStack it = new ItemStack(Material.STICK, 1);
		ItemMeta im = it.getItemMeta();
		assert im != null;
		im.setDisplayName(tsn + Config.trackingstick_uses);
		it.setItemMeta(im);
		trackingStick = it;

		ItemMeta hmeta = head.getItemMeta();
		hmeta.addEnchant(Enchantment.BINDING_CURSE,1,true);
		head.setItemMeta(hmeta);
	}

	@SuppressWarnings("deprecation") // setPersistent() is DRAFT API
    private void dropInv(Player p) {
		PlayerInventory inv = p.getInventory();
		Location l = p.getLocation();
		for (ItemStack i : inv.getContents()) {
			if (i != null && i.getType() != Material.AIR) {
				assert l.getWorld() != null;
				l.getWorld().dropItemNaturally(l, i).setPersistent(false);
			}
		}
		for (ItemStack i : inv.getArmorContents()) {
			if (i != null && i.getType() != Material.AIR) {
				assert l.getWorld() != null;
				l.getWorld().dropItemNaturally(l, i).setPersistent(false);
			}
		}
		inv.clear();
	}

	private void giveStick(Player p) {
		if (p != null) {
			Util.scm(p, lang.track_bar);
			Util.scm(p, lang.track_new1);
			Util.scm(p, lang.track_new2);
			Util.scm(p, lang.track_bar);
			p.getInventory().addItem(trackingStick);
		}
	}

	@EventHandler
	private void itemDespawn(ItemDespawnEvent event) {
		ItemStack i = event.getEntity().getItemStack();
		List<String> l = i.getItemMeta().getLore();
		if (l != null && ! l.isEmpty() && l.contains("NoDespawn")) {
			event.setCancelled(true);
		}
	}

	// Regens health up to whatever the player is missing
	@EventHandler
	private void healthregen(EntityRegainHealthEvent event) {
		Player player = (Player) event.getEntity();
		if (Config.running && Util.inGame(player) && (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED ||
				event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN)){
			event.setCancelled(true);
			long regen_time = 1;
			if (Util.inTeam(player,"human")) regen_time = (long) Config.regen_time_humans;
			else regen_time = (long) Config.regen_time_zombies;
			Bukkit.getScheduler().scheduleAsyncDelayedTask(HvZ.getPlugin(), new BukkitRunnable() {
				double health_left = player.getHealth();
				@Override
				public void run() {
					if (health_left <= 0) this.cancel();
					if (Config.running) {
						player.setHealth(player.getHealth() + Config.regen_amount);
						health_left = health_left - Config.regen_amount;
					};
				}},
					(long) (20 * regen_time));
		}
	}

	@EventHandler
	private void mobSpawn(CreatureSpawnEvent event) {
		if (Config.running && ! event.getSpawnReason().equals(SpawnReason.SPAWNER_EGG)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	private void arenaleave(PlayerMoveEvent event) { // re-write this to TP them back in to the arena
		if (Config.running && Util.inGame(event.getPlayer()) && ! Util.inArena(event.getTo())) {
			Util.tpArena(event.getPlayer());
			event.setCancelled(true);
			event.getPlayer().sendMessage("You can't leave the arena while the game is running!");
		}
	}


	@EventHandler
	private void onItemPickup(PlayerPickupItemEvent event) {
		if (Util.inGame(event.getPlayer()) && event.getItem().getItemStack().getType() == Material.WHITE_WOOL &&
				event.getPlayer().getInventory().contains(new ItemStack(Material.WHITE_WOOL,64))) {
			event.setCancelled(true);
		}
		if (Util.inTeam(event.getPlayer(),"zombie") || Util.inTeam(event.getPlayer(),"stunned_zombie")) {
			if (event.getItem().getItemStack().getType() == Material.WHITE_WOOL) {
				return;
			}
			event.setCancelled(true);
		}
	}

	@EventHandler
	private void MobTargetting(EntityTargetEvent event) {
		if (event.getTarget() instanceof Player) {
			Player p = (Player) event.getTarget();
			if (Util.inTeam(p,"mod") || Util.inTeam(p,"spectator") ||
					Util.inTeam(p,"stunned_zombie") || Util.inTeam(p,"zombie")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void onTrackingStick(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction().equals(Action.LEFT_CLICK_AIR) && Util.inGame(p)) {
			if (p.getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
				useTrackStick(p);
			}
		}
	}

	@EventHandler
	private void onBlockBreak(BlockBreakEvent event) {
		if (Util.inTeam(event.getPlayer(), "zombie") ||
				Util.inTeam(event.getPlayer(), "human")) {
			if  (Util.is_wool(event.getBlock())) {
				event.getBlock().setType(Material.WHITE_WOOL);
			}
			else {event.setCancelled(true);}
		}
		if (Util.inTeam(event.getPlayer(), "spectator") ||
				Util.inTeam(event.getPlayer(), "stunned_zombie")) {
			event.setCancelled(true);
		}}

	@EventHandler
	private void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlock().getType().equals(Material.WHITE_WOOL)) {
			BlockState previous = event.getBlockReplacedState();
				if (Util.inTeam(event.getPlayer(), "zombie")) {
					if (event.getBlock().isLiquid()) {
						event.setCancelled(true);
						return;
					}
					event.getBlock().setType(Material.GREEN_WOOL);
				} else if (Util.inTeam(event.getPlayer(), "human")) {
					if (event.getBlock().isLiquid()) {
						event.setCancelled(true);
						return;
					}
					event.getBlock().setType(Material.LIGHT_BLUE_WOOL);
				}
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				event.getBlock().setType(previous.getType());
				event.getBlock().setBlockData(previous.getBlockData());
				if (! event.getPlayer().getInventory().contains(new ItemStack(Material.WHITE_WOOL,64))) {
				event.getPlayer().getInventory().addItem(new ItemStack(Material.WHITE_WOOL,1));};},
					Config.wool_replace_time*20);
				return;
			}
		if (Util.inTeam(event.getPlayer(), "spectator") ||
			Util.inTeam(event.getPlayer(), "stunned_zombie")) {
				event.setCancelled(true);
			}}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onAttack(EntityDamageByEntityEvent event) {

		Entity defender = event.getEntity();
		Entity damager = event.getDamager();

		if (damager instanceof Player && defender instanceof Player) {
			if (Util.inTeam((Player) damager, "stunned_zombie") || Util.inTeam((Player) defender, "stunned_zombie")) {
				event.setCancelled(true);
				return;
			}

		}
		if (defender instanceof Player && Util.inGame((Player) defender)) {
			Player player = (Player) defender;

			if (Config.freeze || !Util.inGame(player)) {
				event.setCancelled(true);
				return;
			}

			if (damager instanceof Player && (Util.inTeam(player, "human") && Util.inTeam((Player) damager, "human")) ||
					damager instanceof Player && (Util.inTeam(player, "zombie") && Util.inTeam((Player) damager, "zombie"))) {
				Util.scm(damager, "&c" + player.getName() + " is on your team!");
				event.setCancelled(true);
			} else if (Config.isInstaKill && !hasTotem(player) && damager instanceof Player) {
				event.setCancelled(true);
				processDeath(player, damager, event);
			} else if (event.getFinalDamage() >= player.getHealth()) {
				if (hasTotem(player)) return;
				event.setCancelled(true);
				processDeath(player, damager, event);
			}
		}

	}

	@EventHandler(priority =  EventPriority.HIGHEST)
	private void onDeathByOther(EntityDamageEvent event) {

		if (event.getEntity() instanceof Player) {
			final Player player = ((Player) event.getEntity());
			if (!Util.inGame(player)) return;
			if (Util.inTeam(player,"mod") || Util.inTeam(player, "spectator") ||
					(Util.inGame(player) && ! Config.running)) {
				event.setCancelled(true);
				player.setFireTicks(0);
				return;
			}
			if (event instanceof EntityDamageByEntityEvent) return;

			if (event.getFinalDamage() >= player.getHealth()) {
				if (hasTotem(player)) return;
				event.setCancelled(true);
				processDeath(player, null, event);
			}
		}
	}

	@SuppressWarnings("ConstantConditions")
    private boolean hasTotem(Player player) {
		PlayerInventory inv = player.getInventory();
		if (inv.getItemInMainHand() != null && inv.getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING) return true;
        return inv.getItemInOffHand() != null && inv.getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING;
    }

	private void processDeath(Player player, Entity damager, EntityDamageEvent event) {
		EntityDamageEvent.DamageCause cause = event.getCause();
		if (! Util.inGame(player)) return;
		if (Util.inTeam(player,"human")) {dropInv(player);}
		if (Util.inTeam(player,"zombie")) {player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.EMERALD,2));}
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setSaturation(20);
		player.getInventory().clear();
		player.getInventory().setHelmet(head);
		player.getInventory().addItem(new ItemStack(Material.WHITE_WOOL,64));
		ItemStack shears = new ItemStack(org.bukkit.Material.SHEARS,1);
		shears.addEnchantment(Enchantment.DIG_SPEED,4);
		player.getInventory().addItem(shears);
		player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD,1));

		for (Player alive : Bukkit.getOnlinePlayers()) {
			if (alive != null && player != alive && ((Util.inTeam(alive, "human") && Util.inTeam(player,"zombie")) ||
					(Util.inTeam(player, "human") && Util.inTeam(alive,"zombie")))) {
				alive.playSound(alive.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 5, 1);
			}
		}
		for (Player p : Bukkit.getOnlinePlayers()) { // should combine this with the above :/
			if (! (p.equals(player) || Util.inTeam(p,"spectator") || Util.inTeam(p,"mod") || Util.inTeam(p,"zombie") || Util.inTeam(p,"stunned_zombie"))) continue;
			if (damager instanceof Player || cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
				Util.scm(p,(Util.getKillString(player.getName(),damager, plugin.getLang())));
			} else {
				Util.scm(p,Util.getDeathString(event.getCause(), player.getName(), plugin.getLang()));
			}
			if (Util.inTeam(player,"human")) {
				p.sendMessage(player.getName() + " is now a zombie!");
			}
		}
		player.sendMessage("You are now stunned!");
		Util.changeTeam(player, "stunned_zombie");
		Util.stunned(player,true,plugin);

		if (Config.respawn_points) {
			Location closest_loc = null;
			double distance = Integer.MAX_VALUE;
			World w = player.getWorld();
			try {
				for (String s : Config.respawn_locations.split(";")) {
					int x = (int) Integer.parseInt(s.split(",")[0]);
					int y = (int) Integer.parseInt(s.split(",")[1]);
					int z = (int) Integer.parseInt(s.split(",")[2]);
					Location loc = new Location(w, x, y, z);
					if (player.getLocation().distance(loc) < distance) {
						closest_loc = loc;
						distance = player.getLocation().distance(loc);
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				int x = (int) Integer.parseInt(Config.respawn_locations.split(",")[0]);
				int y = (int) Integer.parseInt(Config.respawn_locations.split(",")[1]);
				int z = (int) Integer.parseInt(Config.respawn_locations.split(",")[2]);
				closest_loc = new Location(w, x, y, z);
			}

			player.teleport(closest_loc);
		}
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			player.sendMessage("You are no longer stunned!");
			Util.changeTeam(player,"zombie");
			Util.stunned(player,false,plugin);}, Config.respawn_time*20);

		//game.runCommands(Game.CommandType.DEATH, player);


	}

	@EventHandler
	private void onSprint(FoodLevelChangeEvent event) {
		if (! Config.running) {
			return;
		}

		Player p = (Player) event.getEntity();
		p.setFoodLevel(20);
		p.setSaturation(20);
		event.setCancelled(true);
		// We're nice and don't change this when they're frozen
		if (Config.freeze) {
			if (Util.inGame(p) && ! Util.inTeam(p,"mod") && ! Util.inTeam(p, "spectator")) {
				event.setCancelled(true);
			}
		}
		// Prevent spectators and mods from losing food level
		if (Util.inTeam(p,"spectator") || Util.inTeam(p,"mod") ||
				Util.inTeam(p, "stunned_zombie")) {
            p.setFoodLevel(20);
            p.setSaturation(20);
		    event.setCancelled(true);
        }
	}

	private void useTrackStick(Player p) {
		ItemStack i = p.getInventory().getItemInMainHand();
		ItemMeta im = i.getItemMeta();
		assert im != null;
		im.getDisplayName();
		if (im.getDisplayName().startsWith(tsn)) {
			int uses = Integer.parseInt(im.getDisplayName().replace(tsn, ""));
			if (uses == 0) {
				Util.scm(p, lang.track_empty);
			} else {
				for (Entity e : p.getNearbyEntities(120, 50, 120)) {
					if (e instanceof Player) {
						if (! (Util.inTeam((Player) e,"human") && Util.inTeam(p, "zombie")||
								Util.inTeam((Player) e, "zombie") && Util.inTeam(p, "human"))) continue;
						im.setDisplayName(tsn + (uses - 1));
						Location l = e.getLocation();
						int range = (int) p.getLocation().distance(l);
						Util.scm(p, lang.track_nearest
								.replace("<player>", e.getName())
								.replace("<range>", String.valueOf(range))
								.replace("<location>", getDirection(p.getLocation().getBlock(), l.getBlock())));
						i.setItemMeta(im);
						p.updateInventory();
						return;
					}
				}
				Util.scm(p, lang.track_no_near);
			}
		}
	}

	private String getDirection(Block block, Block block1) {
		Vector bv = block.getLocation().toVector();
		Vector bv2 = block1.getLocation().toVector();
		float y = (float) angle(bv.getX(), bv.getZ(), bv2.getX(), bv2.getZ());
		float cal = (y * 10);
		int c = (int) cal;
		if (c <= 1 && c >= -1) {
			return "South";
		} else if (c > -14 && c < -1) {
			return "SouthWest";
		} else if (c >= -17 && c <= -14) {
			return "West";
		} else if (c > -29 && c < -17) {
			return "NorthWest";
		} else if (c > 17 && c < 29) {
			return "NorthEast";
		} else if (c <= 17 && c >= 14) {
			return "East";
		} else if (c > 1 && c < 14) {
			return "SouthEast";
		} else if (c <= 29 && c >= -29) {
			return "North";
		} else {
			return "UnKnown";
		}
	}

	private double angle(double d, double e, double f, double g) {
		//Vector differences
		int x = (int) (f - d);
		int z = (int) (g - e);

		return Math.atan2(x, z);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onTarget(EntityTargetEvent event) {
		Entity target = event.getTarget();
		if (target instanceof Player) {
			if (Util.inTeam((Player) target, "mod") || Util.inTeam((Player) target, "spectator") ||
					Util.inTeam((Player) target, "stunned_zombie")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void onChestUse(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && Config.running) {
			if (Util.inTeam(p,"stunned_zombie")) {
				event.setCancelled(true);
				return;
			}
				if ((Util.inTeam(p,"zombie") || Util.inTeam(p,"human"))) {
					Block block = event.getClickedBlock();
					assert block != null;
					Chest c = (Chest) block;
					if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST || block.getState() instanceof ShulkerBox ||
							(Util.isRunningMinecraft(1, 14) && block.getType() == Material.BARREL)) {
						if (Util.inTeam(p,"zombie")) {
							c.getBlockInventory().clear();
						}
						for (Player player2 : Bukkit.getOnlinePlayers()) {
							if (player2 != null && (Util.inTeam(player2, "mod") ||
									(Util.inTeam(player2, "spectator")) ||
									(Config.shulker_messages && Util.inGame(player2) && block.getState() instanceof ShulkerBox))) {
								Util.scm(player2, lang.chest_open_confirm.replace("<player>", p.getName()).
										replace("<location>", (int) p.getLocation().getX() + ", " + (int) p.getLocation().getY() + ", " + (int) p.getLocation().getZ()));
							}
						}
						if (c.getBlockInventory().isEmpty() && block.getType() == Material.CHEST) {
							HvZ.chests.add(block.getLocation());
							block.setType(Material.AIR);
						}

					}
				}
		}
	}

    private boolean isChest(Block block) {
        if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST || block.getState() instanceof Shulker) {
            return true;
        }
        return Util.isRunningMinecraft(1, 14) && block.getType() == Material.BARREL;
    }

	@EventHandler
	private void onVehicleUse(EntityMountEvent event) {
		if (! Config.running) {
			return;
		}
		Entity e = event.getMount(); // Should probably only affect vehicles in the arena and not mod rides
		if (! (event.getEntity() instanceof Player)) {
			return;
		}
		Player p = (Player) event.getEntity();
		if (! e.getCustomName().equals("immortal") && Util.inGame(p)) {
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				e.remove();}, Config.rideable_life*20);
		}
	}

	@EventHandler
	private void onEntityShoot(EntityShootBowEvent event) {
		if (! Config.running) {
			return;
		}
	    LivingEntity entity = event.getEntity();
		if (entity.hasMetadata("death-message")) {
			event.getProjectile().setMetadata("death-message",
					new FixedMetadataValue(plugin, entity.getMetadata("death-message").get(0).asString()));
		}
		if (entity instanceof Player) {
		    event.getProjectile().setMetadata("shooter", new FixedMetadataValue(plugin, entity.getName()));
        }
	}

	@EventHandler
	private void onChat(AsyncPlayerChatEvent event) {
		Player spectator = event.getPlayer();
		if (! Util.inGame(spectator)) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (! Util.inGame(player)) continue;
				event.getRecipients().remove(player);
			}
		}
		if (Util.inTeam(spectator, "stunned_zombie")) {
			event.setMessage("");
			event.setCancelled(true); // Stunned zombies can't talk
		}

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getLocation().distance(spectator.getLocation()) < Config.local_chat || // talk distance of 30m
					Util.inTeam(p,"mod") || Util.inTeam(p,"spectator")) continue;
			event.getRecipients().remove(p);
		}


	}

}
