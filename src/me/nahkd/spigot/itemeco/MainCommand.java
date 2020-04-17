package me.nahkd.spigot.itemeco;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MainCommand implements CommandExecutor {
	
	ItemEco plugin;
	
	public MainCommand(ItemEco plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return sendMessage(sender, "§cYou must be player to use this.");
		
		Player player = (Player) sender;
		if (args.length == 0) return sendMessage(sender, plugin.getConfig().getStringList("Display.Help Message").toArray(new String[0]));
		if (args[0].equalsIgnoreCase("lore")) {
			if (!sender.hasPermission("itemeco.general.addlore")) return sendMessage(sender, plugin.getConfig().getString("Display.Messages.No Permission", "no permission"));
			if (args.length < 2) return sendMessage(sender, "§7>> §cMissing parameter: §3<Value>");
			double price = Double.parseDouble(args[1]);
			
			// Get item from hand
			EquipmentSlot hand = player.getInventory().getItemInMainHand() != null? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
			ItemStack item = player.getInventory().getItemInMainHand() != null? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
			if (item == null || item.getType() == Material.AIR) return sendMessage(sender, "§7>> §cNo item in hand :(");
			
			ItemMeta meta = item.getItemMeta();
			plugin.applyPriceFor(meta, price);
			ItemStack clone = new ItemStack(item);
			clone.setItemMeta(meta);
			
			if (hand == EquipmentSlot.HAND) player.getInventory().setItemInMainHand(clone);
			else player.getInventory().setItemInOffHand(clone);
			return sendMessage(sender, plugin.getConfig().getString("Display.Messages.Added Lore", "§7>> §aAdded lore"));
		}
		if (args[0].equalsIgnoreCase("open") || args[0].equalsIgnoreCase("sell")) {
			if (!sender.hasPermission("itemeco.general.sell")) return sendMessage(sender, plugin.getConfig().getString("Display.Messages.No Permission", "no permission"));
			
			Inventory inv = plugin.getServer().createInventory(null, plugin.getConfig().getInt("GUI.Size", 4) * 9, InventoryEventsHandler.INVENTORY_PREFIX + plugin.getConfig().getString("Display.GUI Title", "Insert title here"));
			player.openInventory(inv);
		}
		return true;
	}

	private static boolean sendMessage(CommandSender sender, String... msg) {
		sender.sendMessage(msg);
		return true;
	}

}
