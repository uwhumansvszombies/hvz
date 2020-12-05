package ca.uwhvz.hvz.data;

import ca.uwhvz.hvz.HvZ;
import ca.uwhvz.hvz.Util.Util;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ItemGen config class <b>Internal Use Only</b>
 */
public class ItemGenConfig {

    //Basic settings
    public static HashMap<Material, List<String>> itemap = new HashMap<>();
    private final HvZ plugin;
    private static File configFile;
    private static FileConfiguration config;

    public ItemGenConfig(HvZ plugin) {
        this.plugin = plugin;
        loadConfigFile();
    }

    private void loadConfigFile() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "ItemGen.yml");
        }
        if (!configFile.exists()) {
            plugin.saveResource("ItemGen.yml", false);
            Util.log("&7New ItemGen.yml created");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        loadConfig();
        Util.log("&7ItemGen.yml loaded");
    }


    private void loadConfig() {
        for (String block : getConfig().getConfigurationSection("blocks").getKeys(false)) {
            Material m = null;
            try {m = Material.valueOf(block.toUpperCase());}
            catch (IllegalArgumentException e) {
                e.printStackTrace();
                continue;
            }
            Util.log(block + ": " + getConfig().getStringList("blocks." + block).toString());
            itemap.put(m,getConfig().getStringList("blocks." + block));
//            for (String string : getConfig().getStringList("blocks." + block)) {
//                if (string.isEmpty())
//                    continue;
//                String item = string.split(" ")[0];
//                Material i = null; // this variable actually does nothing other than check a material exists
//                try {
//                    i = Material.valueOf(item);
//                } catch (IllegalArgumentException e) {
//                    e.printStackTrace();
//                    continue;
//                }
//
//                if (itemap != null && itemap.containsKey(m)){
//                    itemap.get(m).add(string);
//                }
//                else {
//                    itemap.put(m, Arrays.asList(string));
//                }
//                Util.log(block + ", " + string + " was added to itemap");}
        }

//        String conf_string = config.getConfigurationSection("blocks").getValues(true).toString();
//        String[] keyValuePairs = conf_string.substring(0, conf_string.length() - 1).split(",");
//        for (String pair : keyValuePairs) {
//            String[] entry = pair.substring(2).split(" ");
//            Material m = null;
//            try {m = Material.valueOf(entry[0]);}
//            catch (IllegalArgumentException e) {
//                e.printStackTrace();
//                continue;
//            }
//            if (itemap.containsKey(m)){
//                itemap.get(m).add(entry[1]);
//            }
//            else {
//                itemap.put(m, Arrays.asList(entry[1]));
//            }
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
