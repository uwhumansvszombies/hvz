package ca.uwhvz.hvz.commands;

import ca.uwhvz.hvz.HvZ;
import ca.uwhvz.hvz.Util.Util;
import ca.uwhvz.hvz.data.Config;
import ca.uwhvz.hvz.data.ItemGenConfig;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import sun.awt.HKSCS;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EndCmd extends BaseCmd {

	public EndCmd() {
		cmdName = "end";
		argLength = 1;
	}

	@Override
	public boolean run() {
		//Util.scm(sender, "here at end!");
//		for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
//			if (p == null) continue;
//			Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(p).removePlayer(p);
//			Util.scm(sender, "removed " + p.getName() + " from the game!");
//		}
		if (! config.running) {
			sender.sendMessage("The game isn't currently running!");
			return true;
		}
		config.running = false;
		Util.broadcast("HvZ has now ended! Thank you for playing!!");
		Util.broadcast("This game ended with the following players:");
		Util.broadcast("Name : Team");
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (Util.inGame(p)) {
				Util.broadcast(p.getName() + " : " + Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(p).getName());
			}
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (Util.inGame(p)) {
				Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(p).removePlayer(p);
				p.setGameMode(GameMode.SURVIVAL);
				p.getInventory().setHelmet(new ItemStack(Material.AIR,1));
				//Util.scm(sender, "removed " + p.getName() + " from the game!");
			}
		}

		for (Player p : HvZ.inv_map.keySet()) {
			p.getInventory().setArmorContents(null);
			p.getInventory().setStorageContents(HvZ.inv_map.get(p));
		}

		if (HvZ.chests != null && ! HvZ.chests.isEmpty()) {
			for (Location l : HvZ.chests) {
				l.getBlock().setType(Material.CHEST);
			}
		}

		for (Map.Entry<String, String> entry : Config.locationMap.entrySet()) { //TODO eventually remove any chests spawned in events
			Location l = new Location(player.getWorld(),Integer.parseInt(entry.getValue().split(",")[0]),
					Integer.parseInt(entry.getValue().split(",")[1]),
					Integer.parseInt(entry.getValue().split(",")[2]));
			l.getBlock().setType(Material.AIR);
		}

		for (Map.Entry<Location, Material> entry : HvZ.item_gen.entrySet()){
			for (Entity e : player.getWorld().getNearbyEntities(entry.getKey(),1,1,1)) {
				e.remove();
				//sender.sendMessage("Parsing location " + entry.getKey().toString());
			}
			player.getWorld().getBlockAt(entry.getKey()).setType(entry.getValue());
		}

		HvZ.item_gen.clear();

		Integer x_1 = Integer.parseInt(Config.corner1.split(",")[0]);
		Integer z_1 = Integer.parseInt(Config.corner1.split(",")[1]);
		Integer x_2 = Integer.parseInt(Config.corner2.split(",")[0]);
		Integer z_2 = Integer.parseInt(Config.corner2.split(",")[1]);
		World w = player.getWorld();

		int wool_cleared = 0;
		List<Material> blocks_to_check = Arrays.asList(Material.LIGHT_BLUE_WOOL, Material.GREEN_WOOL);

		for (int xPoint = Integer.min(x_1, x_2); xPoint <= Integer.max(x_1, x_2); xPoint++) {
			for (int zPoint = Integer.min(z_1, z_2); zPoint <= Integer.max(z_1, z_2); zPoint++) {
				for (int yPoint = 1; yPoint <= 256; yPoint++) {
					Block b = w.getBlockAt(xPoint, yPoint, zPoint);
					if (!blocks_to_check.contains(b.getType())) continue;
					b.setType(Material.AIR);
					for (Entity e : player.getWorld().getNearbyEntities(new Location(w,xPoint,yPoint,zPoint),1,1,1)) {
						if (e.getType() == EntityType.DROPPED_ITEM) e.remove(); // clear any items

					}}}}

		return true;
	}
}