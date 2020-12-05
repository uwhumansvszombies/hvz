package ca.uwhvz.hvz.commands;

import ca.uwhvz.hvz.Util.Util;
import ca.uwhvz.hvz.data.Config;
import org.bukkit.entity.Player;

public class JoinCmd extends BaseCmd {

	public JoinCmd() {
		cmdName = "join";
		argLength = 1;
	}

	@Override
	public boolean run() {
		if (Config.running) {
			Util.scm(sender, lang.game_running);
			return true;
		}
		Util.changeTeam((Player) sender,"player");
		Util.scm(sender, lang.team_join.replace("<team>","player"));
		return true;
	}
}