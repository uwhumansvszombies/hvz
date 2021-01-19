package ca.uwhvz.hvz.commands;

import ca.uwhvz.hvz.Util.Util;
import ca.uwhvz.hvz.data.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import java.util.*;

public class SetCmd extends BaseCmd {

	public SetCmd() {
		cmdName = "set";
		argLength = 2;
		usage = "<see /hvz info settings for all available settings>";
	}

	@Override
	public boolean run() {
		// Boolean Handler
		if (args[1].equalsIgnoreCase("running") ||
				args[1].equalsIgnoreCase("respawn-points") ||
				args[1].equalsIgnoreCase("insta-stun") ||
				args[1].equalsIgnoreCase("insta-kill") ||
				args[1].equalsIgnoreCase("shulker-messages") ||
				args[1].equalsIgnoreCase("wool")) {
			boolean to_set = false;
			if (args.length == 3) {
				if (args[2].equalsIgnoreCase("true")) {
					to_set = true;
				} else if (args[2].equalsIgnoreCase("false")) {
					to_set = false;
				}
				if (args[1].equalsIgnoreCase("running")) {
					Config.running = to_set;
				}
				if (args[1].equalsIgnoreCase("respawn-points")) {
					Config.respawn_points = to_set;
				}
				if (args[1].equalsIgnoreCase("insta-stun")) {
					Config.isInstaStun = to_set;
				}
				if (args[1].equalsIgnoreCase("insta-kill")) {
					Config.isInstaKill = to_set;
				}
				if (args[1].equalsIgnoreCase("shulker-messages")) {
					Config.shulker_messages = to_set;
				}
				if (args[1].equalsIgnoreCase("wool")) {
					Config.wool = to_set;
				}
			}
			plugin.getConfig().set("settings." + args[1],to_set);
			Util.scm(sender, lang.config_change_confirm.replace("<setting>", args[1]).replace("<value>", Boolean.toString(to_set)));
		}
		// Integer Handler
		if (args[1].equalsIgnoreCase("rideable-life") ||
				args[1].equalsIgnoreCase("respawn-time") ||
				args[1].equalsIgnoreCase("ozs") ||
				args[1].equalsIgnoreCase("wool-replace-time") ||
				args[1].equalsIgnoreCase("trackingstick-uses") ||
				args[1].equalsIgnoreCase("local-chat")) {
			if (args.length != 3) {
				Util.scm(sender, lang.cmd_base_wrongusage + " " + sendHelpLine());
				return true;
			}
			if (!Util.isInt(args[2])) {
				Util.scm(sender, lang.cmd_int_required.replace("<value>", args[2]));
			} else {
				int to_set = Integer.parseInt(args[2]);
				if (args[1].equalsIgnoreCase("rideable-life")) {
					Config.rideable_life = to_set;
				}
				if (args[1].equalsIgnoreCase("respawn-time")) {
					Config.respawn_time = to_set;
				}
				if (args[1].equalsIgnoreCase("ozs")) {
					Config.ozs = to_set;
				}
				if (args[1].equalsIgnoreCase("wool-replace-time")) {
					Config.wool_replace_time = to_set;
				}
				if (args[1].equalsIgnoreCase("trackingstick-uses")) {
					Config.trackingstick_uses = to_set;
				}
				if (args[1].equalsIgnoreCase("local-chat")) {
					Config.local_chat = to_set;
				}
				plugin.getConfig().set("settings." + args[1],to_set);
				Util.scm(sender, lang.config_change_confirm.replace("<setting>", args[1]).replace("<value>", args[2]));
			}

		}
		// Integer Handler
		if (args[1].equalsIgnoreCase("regen-amount") ||
				args[1].equalsIgnoreCase("regen-time")) {
			if (args.length != 3) {
				Util.scm(sender, lang.cmd_base_wrongusage + " " + sendHelpLine());
				return true;
			}
			if (!Util.isDouble(args[2])) {
				Util.scm(sender, lang.cmd_int_required.replace("<value>", args[2]).replace("an integer","a number"));
			} else {
				double to_set = Double.parseDouble(args[2]);
				if (args[1].equalsIgnoreCase("regen-amount")) {
					Config.regen_amount = to_set;
				}
				if (args[1].equalsIgnoreCase("regen-time-zombies")) {
					Config.regen_time_zombies = to_set;
				}
				if (args[1].equalsIgnoreCase("regen-time-humans")) {
					Config.regen_time_humans = to_set;
				}
				plugin.getConfig().set("settings." + args[1],to_set);
				Util.scm(sender, lang.config_change_confirm.replace("<setting>", args[1]).replace("<value>", args[2]));
			}}
		if (args[1].equalsIgnoreCase("corner1") ||
				args[1].equalsIgnoreCase("corner2") ||
				args[1].equalsIgnoreCase("respawn-locations") ||
				args[1].equalsIgnoreCase("reset-respawn")) {
			int x = (int) player.getLocation().getX();
			int z = (int) player.getLocation().getZ();
			int y = (int) player.getLocation().getY();
			String pos = x + "," + z;
			if (args[1].equalsIgnoreCase("corner1")) {
				Config.corner1 = pos;
				plugin.getConfig().set("settings.corner1",pos);
			}
			else if (args[1].equalsIgnoreCase("respawn-locations")) {
				pos = x + "," + y + "," + z;
				pos = Config.respawn_locations + ';' + pos;
				Config.respawn_locations = pos;
				plugin.getConfig().set("settings.respawn-locations",Config.respawn_locations + ';' + pos);
			}
			else if (args[1].equalsIgnoreCase("reset-respawn")) {
				pos = x + "," + y + "," + z;
				Config.respawn_locations = pos;
				plugin.getConfig().set("settings.respawn-locations",pos);
			}
			else {
				Config.corner2 = pos;
				plugin.getConfig().set("settings.corner2",pos);
			}
			Util.scm(sender, lang.config_change_confirm.replace("<setting>", args[1]).replace("<value>", pos));
		}
		plugin.saveConfig();
		return true;
	}
}