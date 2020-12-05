package ca.uwhvz.hvz.commands;

import ca.uwhvz.hvz.HvZ;
import ca.uwhvz.hvz.Util.PotionEffectUtils;
import ca.uwhvz.hvz.Util.Util;
import ca.uwhvz.hvz.data.Config;
import ca.uwhvz.hvz.data.ItemGenConfig;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class StartCmd extends BaseCmd {

	public StartCmd() {
		cmdName = "start";
		argLength = 1;
	}

	@Override
	public boolean run() {
		//Util.scm(sender, "here at start!");
		if (Config.running) {
			sender.sendMessage("The game is already running!");
			return true;
		}
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		if ((Config.ozs - Util.getzoms().size()) < 1) {
			Util.scm(sender, lang.oz_set_confirm.replace("<value>", "No"));
		} else {
			int z_to_assign = Config.ozs - Util.getzoms().size();
			int z_assigned = z_to_assign;
			Collections.shuffle(Arrays.asList(players.toArray()));
			for (Player p : players) {
				if (!Util.inTeam(p, "player")) continue;
				if (z_to_assign > 0) {
					Util.changeTeam(p, "zombie");
					p.getInventory().clear();
					p.getInventory().setHelmet(this.head);
					p.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD,1));
					p.getInventory().addItem(trackingStick);
					z_to_assign -= 1;
					Util.scm(p, lang.team_join.replace("<team>", "zombie"));
					Util.scm(sender, p.getName() + " -> " + "zombie");
				} else {
					Util.changeTeam(p, "human");
					p.getInventory().clear();
					Util.scm(p, lang.team_join.replace("<team>", "human"));
					Util.scm(sender, p.getName() + " -> " + "human");
				}
			}
			if (z_to_assign > 0) {
				sender.sendMessage(lang.oz_set_error.replace("<value>", Integer.toString(z_to_assign)));
				//Util.scm(sender, lang.oz_set_error.replace("<value>", Integer.toString(z_to_assign)));
			} else {
				sender.sendMessage(lang.oz_set_confirm.replace("<value>", Integer.toString(z_assigned - z_to_assign)));
				Util.scm(sender, lang.oz_set_confirm.replace("<value>", Integer.toString(z_assigned - z_to_assign)));
			}


		}
		for ( Player p : players) {
			if (! Util.inGame(p)) continue;
			if (Util.inTeam(p,"spectator")) {
				p.setGameMode(GameMode.SPECTATOR);
			}
			else if (Util.inTeam(p,"mod")) {
				p.setGameMode(GameMode.CREATIVE);
			}
			else {
				p.setGameMode(GameMode.SURVIVAL);
				HvZ.inv_map.put(p,p.getInventory().getStorageContents());
			}
			if (Config.wool && (Util.inTeam(p, "human") || Util.inTeam(p, "zombie"))) {
				if (p.getInventory().contains(new ItemStack(Material.SHEARS, 1)) &&
					p.getInventory().contains(new ItemStack(Material.WHITE_WOOL, 64))) continue;
				p.getInventory().addItem(new ItemStack(Material.WHITE_WOOL,64));
				ItemStack shears = new ItemStack(org.bukkit.Material.SHEARS,1);
				shears.addUnsafeEnchantment(Enchantment.DIG_SPEED,4);
				p.getInventory().addItem(shears);
			}
		}

//		int refill_time = 1;
//		if (Config.wool_refill > 0) {refill_time = Config.wool_refill*20;}
//
//		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,
//				new Runnable() {
//					public void run() {
//						if (Config.running && Config.wool && Config.wool_refill >= 0) {
//							for (Player player : Bukkit.getOnlinePlayers()) {
//								if (! Util.inGame(player)) continue;
//								int wool_amount = 0;
//								for (org.bukkit.inventory.ItemStack i : player.getInventory()) {
//									if (i.getType() == org.bukkit.Material.WHITE_WOOL) {
//										wool_amount += i.getAmount();
//									}
//								}
//								if (wool_amount < 64 && Config.wool) {
//									player.getInventory().addItem(new ItemStack(Material.WHITE_WOOL, 1));
//								}
//							}
//						}
//					}
//				}, 0, refill_time);

		List<Material> blocks_to_check = new ArrayList<>();
		if (! ItemGenConfig.itemap.isEmpty()) {
			for (Material block : ItemGenConfig.itemap.keySet()) {
				if (block == null) continue;
				blocks_to_check.add(block);
			}

			Integer x_1 = Integer.parseInt(Config.corner1.split(",")[0]);
			Integer z_1 = Integer.parseInt(Config.corner1.split(",")[1]);
			Integer x_2 = Integer.parseInt(Config.corner2.split(",")[0]);
			Integer z_2 = Integer.parseInt(Config.corner2.split(",")[1]);
			World w = player.getWorld();

			int items_spawned = 0;

			for (int xPoint = Integer.min(x_1, x_2); xPoint <= Integer.max(x_1, x_2); xPoint++) {
				for (int zPoint = Integer.min(z_1, z_2); zPoint <= Integer.max(x_1, x_2); zPoint++) {
					for (int yPoint = 0; yPoint <= 256; yPoint++) {
						Block b = w.getBlockAt(xPoint, yPoint, zPoint);
						if (!blocks_to_check.contains(b.getType()))
							continue; // 90% of blocks (we hope) are not in the file
						Location loc = new Location(w, xPoint, yPoint, zPoint);
						HvZ.item_gen.put(loc, b.getType());
						// Now to make the items spawn!
						//sender.sendMessage("Parsing itemgen " + ItemGenConfig.itemap.toString());
						List<String> item_choices = ItemGenConfig.itemap.get(b.getType());
						Integer total_probability = 0;
						for (String item : item_choices) {
							if (item == null) continue;
							if (item.contains(" P:")) {
								Integer prob = Integer.parseInt(item.split(" P:")[1].split(" ")[0]);
								total_probability += prob;
							} else {
								total_probability += 10;
							}
						}
						double p = Math.random() * total_probability;
						double cumulative_p = 0;
						String item_choice = "";
						for (String item : item_choices) {
							if (item.contains(" P:")) {
								Integer prob = Integer.parseInt(item.split(" P:")[1].split(" ")[0]);
								cumulative_p += prob;
							} else {
								cumulative_p += 10;
							}
							if (p <= cumulative_p) {
								item_choice = item;
								break;
							}
						}
						// Now we've chosen the item we want. Let's make it appear
						String item_type = item_choice.split(" ")[0];
						Integer amount = 1;
						if (item_choice.split(" ").length > 1 && Util.isInt(item_choice.split(" ")[1])) {
							amount = Integer.parseInt(item_choice.split(" ")[1]);
						}
						ItemStack item = new ItemStack(Material.valueOf(item_type), amount);
						ItemMeta itemMeta = item.getItemMeta();
						// other stuff for the item goes here
						if (item_choice.contains(" N:")) {
							itemMeta.setDisplayName(item_choice.split(" N:")[1].split(" ")[0]);
						}
						itemMeta.setLore(Arrays.asList("NoDespawn"));
						item.setItemMeta(itemMeta);
						if (item_choice.contains(" Potion:")) {
							String Potion = item_choice.split(" Potion:")[1].split(" ")[0];
							PotionMeta potionMeta = (PotionMeta) itemMeta;
							for (String pdata : Potion.substring(1, Potion.length() - 1).split(",")) {
								try {
									String pname = pdata.split(":")[0];
									Integer plvl = Integer.valueOf(pdata.split(":")[1]);
									Integer ptime = Integer.valueOf(pdata.split(":")[2]);
									//sender.sendMessage("Trying potion " + pname);
									potionMeta.addCustomEffect(new PotionEffect(PotionEffectUtils.get(pname),ptime*20,plvl),true);
									//sender.sendMessage("Success!");
								} catch (Exception exception) {
									exception.printStackTrace();
									continue;
								}
							}
						}
						item.setItemMeta(itemMeta);
						if (item_choice.contains(" Enchantments:")) {
							String Enchants = item_choice.split(" Enchantments:")[1].split(" ")[0];
							//sender.sendMessage(Enchants.substring(1, Enchants.length() - 1).split(",").toString());
							for (String e : Enchants.substring(1, Enchants.length() - 1).split(",")) {
								String enchantname = e.split(":")[0].toLowerCase();
								Integer enchantlvl = Integer.valueOf(e.split(":")[1]);
								sender.sendMessage("Trying enchantment " + enchantname);
								try {
									item.addEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(enchantname)), enchantlvl);
									sender.sendMessage("Success!");
								} catch (Exception exception) {
									exception.printStackTrace();
									continue;
								}
							}
						}
						b.setType(Material.AIR);
						//sender.sendMessage("Generated " + item.getType().toString());
						Item i = w.dropItem(loc, item);
						//i.setGravity(false);
						items_spawned += 1;
					}
				}
			}
			sender.sendMessage("Generated " + items_spawned + " items");
		}

		config.running = true;
		Util.broadcast("HvZ has now begun!");
		return true;
	}
}

