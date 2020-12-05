package ca.uwhvz.hvz.commands;

import ca.uwhvz.hvz.HvZ;
import ca.uwhvz.hvz.Util.Util;
import ca.uwhvz.hvz.data.Config;
import net.minecraft.server.v1_16_R2.CommandTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GameMessageCmd extends BaseCmd {

	public GameMessageCmd() {
		cmdName = "msg";
		argLength = 3;
		usage = "<h/z/m/s> <message>";
	}

	@Override
	public boolean run() {
		String msg = Arrays.stream(Arrays.copyOfRange(args,2,args.length)).collect(Collectors.joining(""));
		if (args[1].equalsIgnoreCase("h"))
		{
			if (Util.inTeam(player,"human"))
			{
				player.sendMessage(lang.human_msg_error);
				return true;
			}
			if (Util.inTeam(player,"spectator"))
			{
				player.sendMessage(lang.spectator_msg_error);
				return true;
			}
			for (Player player2 : Bukkit.getOnlinePlayers())
			{
				if (player2 != null && Util.inTeam(player2, "human") || Util.inTeam(player2, "mod") ||
						Util.inTeam(player2, "spectator")) {
					Util.scm(player2, player.getName() + " to humans: " + msg);
			}}
			player.sendMessage(lang.msg_send_confirm.replace("<team>","humans"));

		}
		else if (args[1].equalsIgnoreCase("z"))
		{
			if (Util.inTeam(player,"spectator"))
			{
				player.sendMessage(lang.spectator_msg_error);
				return true;
			}
			for (Player player2 : Bukkit.getOnlinePlayers())
			{
				if (player2 != null && (Util.inTeam(player2, "zombie") || Util.inTeam(player2, "mod")) ||
						Util.inTeam(player2, "spectator")) {
					Util.scm(player2, player.getName() + " to zombies: " + msg);
				}}
			player.sendMessage(lang.msg_send_confirm.replace("<team>","zombies"));

		}
		else if (args[1].equalsIgnoreCase("m"))
		{
			for (Player player2 : Bukkit.getOnlinePlayers())
			{
				if (player2 != null && Util.inTeam(player2, "mod")) {
					Util.scm(player2, player.getName() + " to mods: " + msg);
				}}
			player.sendMessage(lang.msg_send_confirm.replace("<team>","mods"));

		}
		else if (args[1].equalsIgnoreCase("s"))
		{
			for (Player player2 : Bukkit.getOnlinePlayers())
			{
				if (player2 != null && Util.inTeam(player2, "spectator") || Util.inTeam(player2, "mod"))
				{
					Util.scm(player2, player.getName() + " to spectators: " + msg);
				}}
			player.sendMessage(lang.msg_send_confirm.replace("<team>","spectators"));

		}
		else {
			player.sendMessage(lang.cmd_base_wrongusage + " " + sendHelpLine());
		}
		return true;
	}
}