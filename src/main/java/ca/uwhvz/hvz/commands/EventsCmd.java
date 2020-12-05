package ca.uwhvz.hvz.commands;

import ca.uwhvz.hvz.HvZ;
import ca.uwhvz.hvz.data.Config;
import ca.uwhvz.hvz.data.Events;

import java.util.*;

public class EventsCmd extends BaseCmd {

	public EventsCmd() {
		cmdName = "events";
		argLength = 3;
		usage = "<location list/add/remove <name>, run <type> <name>>";
	}

	@Override
	public boolean run() {
		// Boolean Handler
		if (args.length > 2) {
			if (args[1].equalsIgnoreCase("location")) {
				if (args[2].equalsIgnoreCase("add")){
					if (args.length < 4) {
						sender.sendMessage("Please specify a name!");
						return true;
					}
					int x = (int) player.getLocation().getX();
					int z = (int) player.getLocation().getZ();
					int y = (int) player.getLocation().getY();
					String pos = x + "," + y + "," + z;
					String name = String.join(" ", Arrays.asList(args).subList(3, args.length));
					Config.locationMap.put(name,pos);
					List<String> locs = plugin.getConfig().getStringList("locations");
					locs.add(pos + " " + name);
					plugin.getConfig().set("locations",locs);
					sender.sendMessage("Added location <name> at <pos>".replace("<name>",name).replace("<pos>",Config.locationMap.get(name)));
				}
				else if (args[2].equalsIgnoreCase("remove")) {
					String name = String.join(" ", Arrays.asList(args).subList(args.length - 3, args.length));
					sender.sendMessage(name);
					int name_pos = -1;
					List<String> loc = plugin.getConfig().getStringList("locations");
					for (int pos = 0; pos <= loc.size()-1; pos++) {
						List<String> list_split = Arrays.asList(loc.get(pos).split(" "));
						sender.sendMessage(list_split.toString());
						if (String.join(" ",list_split.subList(3,list_split.size())).equalsIgnoreCase(name)) {
							name_pos = pos;
						}
					}
					if (name_pos == -1) {
						sender.sendMessage("That event location does not exist!");
						return true;
					}
					sender.sendMessage("Removed location <name> at <pos>".replace("<name>",name).replace("<pos>",Config.locationMap.get(name)));
					Config.locationMap.remove(name);
					List<String> locs = plugin.getConfig().getStringList("locations");
					locs.remove(name_pos);
					plugin.getConfig().set("locations",locs);
			}
				else if (args[2].equalsIgnoreCase("list")) {
					sender.sendMessage("Name : Location");
					for (Map.Entry<String,String> entry : Config.locationMap.entrySet()) {
						sender.sendMessage(entry.getKey() + " : " + entry.getValue());
					}
				}
			}
			if (args[1].equalsIgnoreCase("run") && args.length > 2) {
				String event_type = "";
				if (args[2].equals("random")) {
					List<String> eventlist = Arrays.asList("escort","supply-drop","point-hold");
					Collections.shuffle(eventlist);
					event_type = eventlist.get(0);
				}
				else if (! Arrays.asList("escort","supply-drop","point-hold").contains(args[2].toLowerCase())) {
					sender.sendMessage("run expects one of escort, supply-drop, or point-hold!");
					return true;
				}
				else {
					event_type = args[2].toLowerCase();
				}
				//sender.sendMessage(EventLocations.locationMap.toString());

				if (event_type.equalsIgnoreCase("escort")) {
					Events.escort(plugin,Config.locationMap);
				}
				else if (event_type.equalsIgnoreCase("point-hold")) {
					Events.capture(plugin,Config.locationMap);
				}
				else if (event_type.equalsIgnoreCase("supply-drop")) {
					Events.supply_drop(plugin,Config.locationMap);
				}
			}
		}
		plugin.saveConfig();
		return true;
	}
}