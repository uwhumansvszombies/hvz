package ca.uwhvz.hvz.listeners;

import ca.uwhvz.hvz.HvZ;
import ca.uwhvz.hvz.Util.*;
import ca.uwhvz.hvz.data.*;
import ca.uwhvz.hvz.commands.BaseCmd;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Internal command listener
 * To add a command with tab complete, register it here
 * To add a command in the general sense, register it in HvZ
 */
@SuppressWarnings("NullableProblems")
public class CommandListener implements CommandExecutor, TabCompleter {

	private final HvZ plugin;

	public CommandListener(HvZ plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
		if (args.length == 0 || !plugin.getCommands().containsKey(args[0])) {
			Util.scm(s, "&4*&c&m                         &7*( &3&lHvZ &7)*&c&m                          &4*");
			for (BaseCmd cmd : plugin.getCommands().values().toArray(new BaseCmd[0])) {
				if (s.hasPermission("hvz." + cmd.cmdName)) Util.scm(s, "  &7&l- " + cmd.sendHelpLine());
			}
			Util.scm(s, "&4*&c&m                                                                             &4*");
		} else plugin.getCommands().get(args[0]).processCmd(plugin, s, args);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
		if (args.length == 1) {
			ArrayList<String> matches = new ArrayList<>();
			for (String name : plugin.getCommands().keySet()) {
				if (StringUtil.startsWithIgnoreCase(name, args[0])) {
					if (sender.hasPermission("hvz." + name))
						matches.add(name);
				}
			}
			return matches;
		} else if (args.length >= 2) {
			if (args[0].equalsIgnoreCase("set") && sender instanceof Player && args.length == 2) {
				List<String> listSet = new ArrayList<>();
				listSet.add("running");
				listSet.add("respawn-time");
				listSet.add("respawn-points");
				listSet.add("rideable-life");
				listSet.add("ozs");
				listSet.add("insta-kill");
				listSet.add("insta-stun");
				listSet.add("shulker-messages");
				listSet.add("trackingstick-uses");
				listSet.add("respawn-time");
				listSet.add("corner1");
				listSet.add("corner2");
				listSet.add("respawn-locations");
				listSet.add("reset-respawn");
				listSet.add("wool");
				listSet.add("wool-replace-time");
				listSet.add("local-chat");
				ArrayList<String> matchesSet = new ArrayList<>();
				for (String name : listSet) {
					if (StringUtil.startsWithIgnoreCase(name, args[1])) {
						matchesSet.add(name);
					}
				}
				return matchesSet;
			} else if (args[0].equalsIgnoreCase("msg") && sender instanceof Player && args.length == 2) {
				List<String> listInfo = new ArrayList<>();
				listInfo.add("h");
				listInfo.add("z");
				listInfo.add("m");
				listInfo.add("s");
				ArrayList<String> matchesInfo = new ArrayList<>();
				for (String name : listInfo) {
					if (StringUtil.startsWithIgnoreCase(name, args[1])) {
						matchesInfo.add(name);
					}
				}
				return matchesInfo;
			}
			else if (args[0].equalsIgnoreCase("info") && sender instanceof Player && args.length == 2) {
				List<String> listInfo = new ArrayList<>();
				listInfo.add("settings");
				listInfo.add("players");
				ArrayList<String> matchesInfo = new ArrayList<>();
				for (String name : listInfo) {
					if (StringUtil.startsWithIgnoreCase(name, args[1])) {
						matchesInfo.add(name);
					}
				}
				return matchesInfo;

			} else if (args[0].equalsIgnoreCase("mod") && sender instanceof Player && args.length == 2) {
				List<String> listMod = new ArrayList<>();
				listMod.add("contact");
				listMod.add("reset-teams");
				listMod.add("beacons");
				listMod.add("freeze");
				listMod.add("tracking-stick");
				listMod.add("set-stunned");
				ArrayList<String> matchesMod = new ArrayList<>();
				for (String name : listMod) {
					if (StringUtil.startsWithIgnoreCase(name, args[1])) {
						matchesMod.add(name);
					}
				}
				return matchesMod;
			} else if (args[0].equalsIgnoreCase("events")) {
				List<String> listEvents = new ArrayList<>();
				if (args.length == 2) {
					listEvents.add("location");
					listEvents.add("run");
				}
				if (args.length == 3) {
					if (args[1].equalsIgnoreCase("location")) {
						listEvents.add("add");
						listEvents.add("remove");
						listEvents.add("list");
					}
					else if (args[1].equalsIgnoreCase("run")) {
						listEvents.add("escort");
						listEvents.add("point-hold");
						listEvents.add("supply-drop");
						listEvents.add("random");
					}
				}
				if (args.length >= 4) {
					if (args[2].equalsIgnoreCase("remove") ||
							args[2].equalsIgnoreCase("run")) {
						for (String loc : Config.locationMap.keySet()) {
							listEvents.add(loc);
						}
					}
					else if (args[2].equalsIgnoreCase("run")) {
						listEvents.add("escort");
						listEvents.add("point-hold");
						listEvents.add("supply-drop");
						listEvents.add("random");
					}
				}
				ArrayList<String> matchesEvents = new ArrayList<>();
				for (String name : listEvents) {
					if (StringUtil.startsWithIgnoreCase(name, args[args.length - 1])) {
						matchesEvents.add(name);
					}
				}
				return matchesEvents;
			}
			else if (args.length == 3 && (args[1].equalsIgnoreCase("running") ||
					args[1].equalsIgnoreCase("freeze") ||
					args[1].equalsIgnoreCase("respawn-points") ||
					args[1].equalsIgnoreCase("insta-kill") ||
					args[1].equalsIgnoreCase("insta-stun") ||
					args[1].equalsIgnoreCase("shulker-messages") ||
					args[1].equalsIgnoreCase("set-stunned"))) {
				List<String> listBool = new ArrayList<>();
				listBool.add("true");
				listBool.add("false");
				ArrayList<String> matchesBool = new ArrayList<>();
				for (String name : listBool) {
					if (StringUtil.startsWithIgnoreCase(name, args[2])) {
						matchesBool.add(name);
					}
				}
				return matchesBool;
			}
			// Player list!
			else if ((args.length == 4 && args[1].equalsIgnoreCase("set-stunned")) ||
					(args.length == 3 && args[1].equalsIgnoreCase("tracking-stick"))) {
				ArrayList<String> matchesPlayers = new ArrayList<>();
				for (Player p : Bukkit.getOnlinePlayers()) {
					matchesPlayers.add(p.getName());
					}
				return matchesPlayers;
			}
		}
		return Collections.emptyList();
	}
}