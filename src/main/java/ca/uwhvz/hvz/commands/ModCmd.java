package ca.uwhvz.hvz.commands;

import ca.uwhvz.hvz.Util.Util;
import ca.uwhvz.hvz.data.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ModCmd extends BaseCmd {

	public ModCmd() {
		cmdName = "mod";
		argLength = 2;
		usage = "<contact/tracking-stick/reset-teams/set-stunned/beacons>";
	}

	@Override
	public boolean run() {
		// This command will handle mod related stuff
		// Such as: "/hvz mod tracking-stick @p" will give a player a tracking stick
		// "/hvz mod compass @p" will give a player a compass
		// "hvz mod contact" will send mods a message from the current position, and, if applicable, include player name
		if (args[1].equalsIgnoreCase("contact")) {
			String msg = "To mods: ";
			if (sender instanceof Player) {
				msg += ("sent by " + sender.getName() + ": ");
			}
			if (args.length > 2) {
				msg += Arrays.stream(Arrays.copyOfRange(args,2,args.length)).collect(Collectors.joining(" "));
			}
			else {
				msg += lang.mod_assist_req.replace("<pos>",sender.getName());
			}
			for (Player player2 : Bukkit.getOnlinePlayers())
			{
				if (player2 != null && Util.inTeam(player2, "mod")) {
					Util.scm(player2, msg);
				}}
			if (sender instanceof Player) {
				player.sendMessage(lang.msg_send_confirm.replace("<team>","mods"));
			}

		}

		if (args[1].equalsIgnoreCase("reset-teams")) {
			Util.unregister_teams();
			Util.register_teams();
			sender.sendMessage("Teams have been completely reset!");
		}

		if (args[1].equalsIgnoreCase("beacons")) {
			if (args.length == 3 && Util.isInt(args[2])) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (Util.inTeam(p,"zombie")) {
						Util.tempBeacon(p,Integer.parseInt(args[2]),plugin);
					}
					if (Util.inTeam(p,"human")) {
						Util.tempBeacon(p,Integer.parseInt(args[2]),plugin);
					}
				}
			}
			else {
				sender.sendMessage("beacons expects an integer for the number of seconds to last");
			}
		}

		if (args[1].equalsIgnoreCase("freeze") && args.length == 3) {
			int affected = 0;
			for (Player player2 : Bukkit.getOnlinePlayers())
			{
				if (player2 != null && Util.inGame(player2) && ! Util.inTeam(player2,"mod") &&
						! Util.inTeam(player2, "spectator")) {
					if (args[2].equalsIgnoreCase("true")) {
						Util.freeze(player2, true);
						Util.scm(player2, "freeze!");
						Config.freeze = true;
						affected += 1;
					}
					else if (args[2].equalsIgnoreCase("false")) {
						Util.freeze(player2, false);
						Util.scm(player2, "unfreeze!");
						Config.freeze = false;
						affected += 1;
					}
				}}
			sender.sendMessage(affected + " players affected!");
		}

		if (args[1].equalsIgnoreCase("set-stunned") && args.length == 4) {
			try {
				for (Player p : Bukkit.getOnlinePlayers()){
					if (p.getName().equals(args[3])) {
						Util.stunned(p, Boolean.parseBoolean(args[2]), plugin);
						sender.sendMessage(p.getName() + " had their stunned set to " + args[2]);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage("No one was affected");
			}
		}

		if (args[1].equalsIgnoreCase("tracking-stick") && args.length == 3)  {
			try {
				for (Player p : Bukkit.getOnlinePlayers()){
					if (p.getName().equals(args[2])) {
						p.getInventory().addItem(trackingStick);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}