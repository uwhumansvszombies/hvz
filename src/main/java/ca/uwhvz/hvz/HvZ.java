package ca.uwhvz.hvz;

import ca.uwhvz.hvz.Util.Util;
import ca.uwhvz.hvz.commands.*;
import ca.uwhvz.hvz.data.*;
import ca.uwhvz.hvz.data.Events;
import ca.uwhvz.hvz.listeners.CommandListener;
import ca.uwhvz.hvz.listeners.GameListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HvZ extends JavaPlugin {

    private static HvZ plugin;
    private Config config;
    private ItemGenConfig itemgenconfig;
    private Events events;
    private Language lang;
    private Map<String, BaseCmd> cmds;
    public static List<Location> chests;
    public static Map<Location, Material> item_gen = new HashMap<>(); // map of which locations should be replaced by what after the game
    public static Map<Player,ItemStack[]> inv_map = new HashMap<>(); // player inventories to store during game

    @Override
    public void onEnable() {
        if (!Util.isRunningMinecraft(1, 13)) {
            Util.warning("HvZ does not support your version!");
            Util.warning("Only versions 1.13+ are supported");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        loadPlugin(true);

    }

    public void loadPlugin(boolean load) {
        plugin = this;

        if (load) {
            cmds = new HashMap<>();
        }

        config = new Config(this);
        itemgenconfig = new ItemGenConfig(this);
        events = new Events(this);

        lang = new Language(this);

        //PAPI check
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this).register();
            Util.log("&7PAPI found, Placeholders have been &aenabled");
        } else {
            Util.log("&7PAPI not found, Placeholders have been &cdisabled");
        }


        if (load) {
            //noinspection ConstantConditions
            getCommand("hvz").setExecutor(new CommandListener(this));
            loadCmds();
        }

        Util.register_teams();

        getServer().getPluginManager().registerEvents(new GameListener(this), this);

//       Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,
//                    new Runnable() {
//                        public void run() {
//                            if (Config.running && Config.wool && Config.wool_refill >= 0) {
//                                for (Player player : Bukkit.getOnlinePlayers()) {
//                                    int wool_amount = 0;
//                                    for (org.bukkit.inventory.ItemStack i : player.getInventory()) {
//                                        if (i.getType() == org.bukkit.Material.WHITE_WOOL) {
//                                            wool_amount += i.getAmount();
//                                        }
//                                    }
//                                    if (wool_amount < 64 && Config.wool) {
//                                        player.getInventory().addItem(new ItemStack(Material.WHITE_WOOL, 1));
//                                    }
//                                }
//                            }
//                        }
//                    }, 0, Config.wool_refill*20);


        Util.log("~~~~~~~~~~~~~~~~~~~~~~~");
        Util.log("HvZ has been &benabled!");
        Util.log("~~~~~~~~~~~~~~~~~~~~~~~");
    }

    public void reloadPlugin() {
        unloadPlugin(true);
    }

    private void unloadPlugin(boolean reload) {
        plugin = null;
        config = null;
        lang = null;
        HandlerList.unregisterAll(this);
        if (reload) {
            loadPlugin(false);
        } else {
            cmds = null;
        }
    }

    @Override
    public void onDisable() {
        unloadPlugin(false);
        Util.log("HvZ has been disabled!");

    }

    /** Get the instance of this plugin
     * @return This plugin
     */
    public static HvZ getPlugin() {
        return plugin;
    }

    /** Get a map of commands
     * @return Map of commands
     */
    public Map<String, BaseCmd> getCommands() {
        return this.cmds;
    }

    /** Get an instance of the language file
     * @return Language file
     */
    public Language getLang() {
        return this.lang;
    }

    /**
     * Get an instance of {@link Config}
     *
     * @return Config file
     */
    public Config getPluginConfig() {
        return config;
    }

    /**
     * Get an instance of the ItemGen config
     *
     * @return Config file
     */
    public ItemGenConfig getItemGenConfig() {
        return this.itemgenconfig;
    }

    /**
     * Get an instance of the Events config
     *
     * @return Config file
     */
    public Events getEvents() {
        return this.events;
    }

    private void loadCmds() {
        cmds.put("set", new SetCmd());
        cmds.put("msg", new GameMessageCmd());
        cmds.put("info", new InfoCmd());
        cmds.put("join", new JoinCmd());
        cmds.put("end", new EndCmd());
        cmds.put("mod", new ModCmd());
        cmds.put("start", new StartCmd());
        cmds.put("events", new EventsCmd());


        for (String bc : cmds.keySet()) {
            getServer().getPluginManager().addPermission(new Permission("hvz." + bc));

        }
    }


}

