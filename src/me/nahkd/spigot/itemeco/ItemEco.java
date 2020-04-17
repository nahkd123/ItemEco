package me.nahkd.spigot.itemeco;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class ItemEco extends JavaPlugin {

	private String prefix;
	private String suffix;
	private DecimalFormat formatter;
	public Economy economy;
	
	@Override
	public void onEnable() {
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			getServer().getConsoleSender().sendMessage(new String[] {
					"§cUnable to enable ItemEco: Missing economy service provider",
					"§c",
					"§cAt least 1 economy that's hooked with Vault is required",
					"§cto use this plugin."
			});
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		economy = rsp.getProvider();
		if (economy == null) {
			getServer().getConsoleSender().sendMessage(new String[] {
					"§cUnable to enable ItemEco: Economy provider is null",
					"§c",
					"§cAt least 1 economy that's hooked with Vault is required",
					"§cto use this plugin."
			});
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		if (!new File(getDataFolder(), "config.yml").exists()) saveResource("config.yml", false);
		reloadConfig();

		prefix = getConfig().getString("Display.Lore Prefix", "§7Price: §e");
		suffix = getConfig().getString("Display.Lore Suffix", " §5¥");
		formatter = new DecimalFormat("###,##0.0");
		
		getCommand("isell").setExecutor(new MainCommand(this));
		getServer().getPluginManager().registerEvents(new InventoryEventsHandler(this), this);
		getServer().getConsoleSender().sendMessage("§aItemEco is enabled!");
	}
	
	@Override
	public void onDisable() {
		// Prevent "memory leak"
		prefix = suffix = null;
		formatter = null;
		economy = null;
		getServer().getConsoleSender().sendMessage("§cItemEco is disabled!");
	}
	
	public double getPriceFor(ItemMeta meta) {
		if (meta == null || !meta.hasLore()) return -1;
		List<String> lore = meta.getLore();
		for (String line : lore) if (line.startsWith(prefix) && line.endsWith(suffix))
			try {return formatter.parse(line.substring(prefix.length(), line.length() - suffix.length())).doubleValue();}
			catch (ParseException e) {return Double.parseDouble(line.substring(prefix.length(), line.length() - suffix.length()));}
		return -1;
	}
	public void applyPriceFor(ItemMeta meta, double price) {
		List<String> lore;
		if (meta.hasLore()) lore = meta.getLore();
		else lore = new ArrayList<String>();
		
		if (lore.size() <= 0) {
			lore.add(prefix + formatter.format(price) + suffix);
			meta.setLore(lore);
			return;
		} else for (int i = 0; i < lore.size(); i++) {
			String line = lore.get(i);
			if (line.startsWith(prefix) && line.endsWith(suffix)) {
				lore.set(i, prefix + price + suffix);
				meta.setLore(lore);
				return;
			}
		}
		
		lore.add(prefix + formatter.format(price) + suffix);
		meta.setLore(lore);
	}
	
}
