package ca.uwhvz.hvz.commands;

import ca.uwhvz.hvz.HvZ;
import ca.uwhvz.hvz.Util.Util;
import ca.uwhvz.hvz.data.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class InfoCmd extends BaseCmd {

	public InfoCmd() {
		cmdName = "info";
		argLength = 2;
		usage = "<settings/players>";
	}

	@Override
	public boolean run() {
		//Util.scm(sender, "here at info!");
		//Util.scm(sender, config.getConfig().getConfigurationSection("settings").getKeys(true).toString());
		if (args[1].equalsIgnoreCase("players")) {
			player.sendMessage("Name : Team");
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (Util.inGame(p)) {
					player.sendMessage(p.getName() + " : " + Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(p).getName());
				}
			}
		}
		if (args[1].equalsIgnoreCase("settings")) {
			Util.scm(sender, "Setting : Value");
			Util.scm(sender, "running : " + Config.running);
			Util.scm(sender, "respawn-time : " + Config.respawn_time);
			Util.scm(sender, "respawn-points : " + Config.respawn_points);
			Util.scm(sender, "respawn-locations : " + Config.respawn_locations);
			Util.scm(sender, "rideable-life : " + Config.rideable_life);
			Util.scm(sender, "ozs : " + Config.ozs);
			Util.scm(sender, "insta-kill : " + Config.isInstaKill);
			Util.scm(sender, "insta-stun : " + Config.isInstaStun);
			Util.scm(sender, "wool : " + Config.wool);
			Util.scm(sender, "wool-replace-time : " + Config.wool_replace_time);
			Util.scm(sender, "shulker-messages : " + Config.shulker_messages);
			Util.scm(sender, "trackingstick-uses : " + Config.trackingstick_uses);
			Util.scm(sender, "corner1 : " + Config.corner1);
			Util.scm(sender, "corner2 : " + Config.corner2);
			Util.scm(sender, "local-chat : " + Config.local_chat);
			Util.scm(sender, "regen-time-humans : " + Config.regen_time_humans);
			Util.scm(sender, "regen-time-zombies : " + Config.regen_time_zombies);
			Util.scm(sender, "regen-amount : " + Config.regen_amount);
		}
		return true;
	}
}