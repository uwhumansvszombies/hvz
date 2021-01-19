package ca.uwhvz.hvz.data;

import ca.uwhvz.hvz.HvZ;
import ca.uwhvz.hvz.Util.Util;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

/**
 * Main config class <b>Internal Use Only</b>
 */
public class Config {

    //Basic settings
    public static boolean running;
    public static boolean freeze;
    public static int ozs;
    public static int respawn_time;
    public static boolean respawn_points;
    public static int rideable_life;
    public static boolean isInstaKill;
    public static boolean isInstaStun;
    public static boolean wool;
    public static boolean shulker_messages;
    public static int trackingstick_uses;
    public static String corner1;
    public static String corner2;
    public static String respawn_locations;
    public static int local_chat;
    public static int wool_replace_time;
    public static double regen_time_zombies;
    public static double regen_time_humans;
    public static double regen_amount;

    //Event Location Management
    public static HashMap<String, String> locationMap = new HashMap<>(); // Format of Name, Position

    private final HvZ plugin;
    private static File configFile;
    private static FileConfiguration config;

    public Config(HvZ plugin) {
        this.plugin = plugin;
        loadConfigFile();
    }

    private void loadConfigFile() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
            Util.log("&7New config.yml created");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        loadConfig();
        Util.log("&7config.yml loaded");
    }


    private void loadConfig() {
        running = config.getBoolean("settings.running");
        freeze = config.getBoolean("settings.freeze");
        respawn_time = config.getInt("settings.respawn-time");
        respawn_points = config.getBoolean("settings.respawn-points");
        rideable_life = config.getInt("settings.rideable-life");
        ozs = config.getInt("settings.ozs");
        isInstaKill = config.getBoolean("settings.insta-kill");
        isInstaStun = config.getBoolean("settings.insta-stun");
        wool = config.getBoolean("settings.wool");
        wool_replace_time = config.getInt("settings.wool-replace-time");
        shulker_messages = config.getBoolean("settings.shulker-messages");
        trackingstick_uses = config.getInt("settings.trackingstick-uses");
        corner1 = config.getString("settings.corner1");
        corner2 = config.getString("settings.corner2");
        respawn_locations = config.getString("settings.respawn-locations");
        local_chat = config.getInt("settings.local-chat");
        regen_time_zombies = config.getDouble("settings.regen-time-zombies");
        regen_time_humans = config.getDouble("settings.regen-time-humans");
        regen_amount = config.getDouble("settings.regen-amount");

        Util.log("&7Loading event locations...");
        for (String entry : config.getStringList("locations")) {
            if (entry == null || entry.isEmpty()) continue;
            String name = entry.split(" ")[1];
            String loc = entry.split(" ")[0];
            Util.log(name + " : " + loc);
            locationMap.put(name,loc);
        }

//        String conf_string = config.getConfigurationSection("settings").getValues(true).toString();
//        String[] keyValuePairs = conf_string.substring(1, conf_string.length() - 1).split(",");
//        for (String pair : keyValuePairs) {
//            String[] entry = pair.split("=");
//            HvZ.settings.put(entry[0],entry[1]);
//        }
    }

    public Configuration getConfig() {
        return config;
    }

//    public static void reloadConfig(){
//        try {
//            config.load(configFile);
//        }catch(Exception e){
//            e.printStackTrace();
//            System.out.println("HvZ could not reload config.yml!");
//        };
//    }

}
