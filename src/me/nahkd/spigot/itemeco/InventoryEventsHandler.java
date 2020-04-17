package me.nahkd.spigot.itemeco;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import net.milkbowl.vault.economy.EconomyResponse;

public class InventoryEventsHandler implements Listener {
	
	protected static final String INVENTORY_PREFIX = "§i§c§o§s§r";
	
	ItemEco plugin;
	
	public InventoryEventsHandler(ItemEco plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void inventoryClose(InventoryCloseEvent event) {
		if (event.getView().getTitle().startsWith(INVENTORY_PREFIX + plugin.getConfig().getString("Display.GUI Title", "Insert title here"))) {
			double moneh = 0.0;
			double p;
			int accepted = 0;
			List<ItemStack> rejected = new ArrayList<ItemStack>();
			for (ItemStack i : event.getInventory().getContents()) {
				if (i == null || i.getType() == Material.AIR) continue;
				if (!i.hasItemMeta() || !i.getItemMeta().hasLore() || (p = plugin.getPriceFor(i.getItemMeta())) <= 0) {
					rejected.add(i);
					continue;
				}
				moneh += p;
				accepted++;
			}
			
			EconomyResponse response = plugin.economy.depositPlayer(Bukkit.getPlayer(event.getPlayer().getName()), moneh);
			if (response.transactionSuccess()) {
				// Give player rejected items
				if (rejected.size() > 0) for (ItemStack i : rejected) event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), i);
				
				String msg = plugin.getConfig().getString("Display.Messages.Result", "%i %i %d");
				msg = msg.replaceFirst("%i", "" + accepted).replaceFirst("%i", "" + rejected.size()).replaceFirst("%d", "" + moneh);
				event.getPlayer().sendMessage(msg);
			} else {
				// Give player all items
				for (ItemStack i : event.getInventory().getContents()) if (i != null && i.getType() != Material.AIR) event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), i);
				
				event.getPlayer().sendMessage(plugin.getConfig().getString("Display.Messages.Failed", "failed"));
			}
		}
	}
	
}
