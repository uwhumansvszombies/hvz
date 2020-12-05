package ca.uwhvz.hvz.data;

import ca.uwhvz.hvz.HvZ;
import ca.uwhvz.hvz.Util.Util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Language handler for plugin messages
 */
public class Language {

    private FileConfiguration lang = null;
    private File customLangFile = null;
    private final HvZ plugin;

    public String prefix;
    public String game_start;
    public String game_countdown;
    public String game_running;
    public String oz_set_confirm;
    public String oz_set_error;
    public String team_join;
    public String contact_mod;
    public String mod_assist_req;
    public String human_msg_error;
    public String spectator_msg_error;
    public String msg_send_confirm;
    public String cmd_base_noperm;
    public String cmd_base_wrongusage;
    public String cmd_int_required;
    public String config_change_confirm;
    public String chest_open_confirm;

    public String compass_nearest_player;
    public String track_nearest;
    public String track_no_near;
    public String track_empty;
    public String track_bar;
    public String track_new1;
    public String track_new2;

    public String death_explosion;
    public String death_custom;
    public String death_fall;
    public String death_falling_block;
    public String death_fire;
    public String death_projectile;
    public String death_lava;
    public String death_magic;
    public String death_suicide;
    public String death_other_cause;
    public String death_player;
    public String death_zombie;
    public String death_skeleton;
    public String death_spider;
    public String death_drowned;
    public String death_trident;
    public String death_stray;
    public String death_other_entity;


    public Language(HvZ plugin) {
        this.plugin = plugin;
        loadLangFile();
    }

    private void loadLangFile() {
        if (customLangFile == null) {
            customLangFile = new File(plugin.getDataFolder(), "language.yml");
        }
        if (!customLangFile.exists()) {
            plugin.saveResource("language.yml", false);
            lang = YamlConfiguration.loadConfiguration(customLangFile);
            Util.log("&7New language.yml created");
        } else {
            lang = YamlConfiguration.loadConfiguration(customLangFile);
        }
        loadLang();
        Util.log("&7language.yml loaded");
    }

    private void loadLang() {
        prefix = lang.getString("prefix");
        game_start = lang.getString("game-start");
        game_countdown = lang.getString("game-countdown");
        game_running = lang.getString("game-running");
        oz_set_confirm = lang.getString("oz-set-confirm");
        oz_set_error = lang.getString("oz-set-error");
        team_join = lang.getString("team-join");
        contact_mod = lang.getString("contact-mod");
        mod_assist_req = lang.getString("mod-assist-req");
        human_msg_error = lang.getString("human-msg-error");
        spectator_msg_error = lang.getString("spectator-msg-error");
        cmd_base_noperm = lang.getString("cmd-base-noperm");
        cmd_base_wrongusage = lang.getString("cmd-base-wrongusage");
        cmd_int_required = lang.getString("cmd-int-required");
        config_change_confirm = lang.getString("config-change-confirm");
        chest_open_confirm = lang.getString("chest-open-confirm");

        compass_nearest_player = lang.getString("compass-nearest-player");
        track_nearest = lang.getString("track-nearest");
        track_no_near = lang.getString("track-no-near");
        track_empty = lang.getString("track-empty");
        track_bar = lang.getString("track-bar");
        track_new1 = lang.getString("track-new1");
        track_new2 = lang.getString("track-new2");
        msg_send_confirm = lang.getString("msg-send-confirm");

        death_explosion = lang.getString("death-explosion");
        death_custom = lang.getString("death-custom");
        death_fall = lang.getString("death-fall");
        death_falling_block = lang.getString("death-falling-block");
        death_fire = lang.getString("death-fire");
        death_projectile = lang.getString("death-projectile");
        death_lava = lang.getString("death-lava");
        death_magic = lang.getString("death-magic");
        death_suicide = lang.getString("death-suicide");
        death_other_cause = lang.getString("death-other-cause");
        death_player = lang.getString("death-player");
        death_zombie = lang.getString("death-zombie");
        death_skeleton = lang.getString("death-skeleton");
        death_spider = lang.getString("death-spider");
        death_stray = lang.getString("death-stray");
        death_drowned = lang.getString("death-drowned");
        death_trident = lang.getString("death-trident");
        death_other_entity = lang.getString("death-other-entity");
    }

}
