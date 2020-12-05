package ca.uwhvz.hvz.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftMetaEntityTag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import ca.uwhvz.hvz.HvZ;
import ca.uwhvz.hvz.data.*;
import ca.uwhvz.hvz.Util.Util;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class BaseCmd {

    HvZ plugin;
    Language lang;
    Config config;
	public ItemStack head;

	public BaseCmd() {
        this.plugin = HvZ.getPlugin();
    }

	public CommandSender sender;
	public String[] args;
	public String cmdName;
	public int argLength = 0;
	public String usage = "";
	public Player player;

	private String tsn = ChatColor.GOLD + "TrackingStick " + ChatColor.GREEN + "Uses: ";
	public ItemStack trackingStick;

	public boolean processCmd(HvZ plugin, CommandSender sender, String[] args) {
		ItemStack it = new ItemStack(Material.STICK, 1);
		ItemMeta im = it.getItemMeta();
		assert im != null;
		im.setDisplayName(tsn + Config.trackingstick_uses);
		it.setItemMeta(im);
		ItemStack trackingStick = it;

		ItemStack head = new ItemStack(Material.ZOMBIE_HEAD,1);
		ItemMeta hmeta = head.getItemMeta();
		hmeta.addEnchant(Enchantment.BINDING_CURSE,1,true);
		head.setItemMeta(hmeta);

		this.trackingStick = trackingStick;
		this.head = head;
		this.sender = sender;
		this.args = args; // disregards the initial command! /hvz info -> has args.length of 1
		this.lang = plugin.getLang();
		this.config = plugin.getPluginConfig();
		try {
			this.player = (Player) sender;
		}
		catch (Exception e) {
			this.player = null;
		}
		if (!sender.hasPermission("hvz." + cmdName))
			Util.scm(this.sender, lang.cmd_base_noperm.replace("<command>", cmdName));
		else if (argLength > args.length)
			Util.scm(sender, lang.cmd_base_wrongusage + " " + sendHelpLine());
		else return run();
		return true;
	}

	public abstract boolean run();

	public String sendHelpLine() {
		return "&3&l/hvz &b" + cmdName + " &6" + usage.replaceAll("<", "&7&l<&f").replaceAll(">", "&7&l>");
	}

}
