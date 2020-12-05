package ca.uwhvz.hvz.commands;

import ca.uwhvz.hvz.HvZ;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

/**
 * Internal placeholder class
 */
public class Placeholders extends PlaceholderExpansion {

    private HvZ plugin;

    public Placeholders(HvZ plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "hvz";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
}