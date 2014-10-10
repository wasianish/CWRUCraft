package org.wasianish.cwrucraft.main;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class CListener implements Listener {
	
	
	/*
	 * When a player first queries the server
	 */
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		// If first time
		if(!Bukkit.getOfflinePlayer(event.getPlayer().getName()).hasPlayedBefore()) {
			// Add to register
			CWRUCraft.toRegister.add(event.getPlayer().getName());
			Bukkit.getServer().getLogger().info(event.getPlayer().getName() + " to register");
			// Create player data
			CWRUCraft.createNewPlayer(event.getPlayer().getName());
			CWRUCraft.cantDoShit.add(event.getPlayer().getName());
		}
	}
	
	/*
	 * When a player actually joins the server
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// If doesnt need to register
		if(!CWRUCraft.toRegister.contains(event.getPlayer().getName())) {
			Bukkit.getServer().getLogger().info(event.getPlayer().getName() + " to login");
			CWRUCraft.clearedInventory.put(event.getPlayer().getName(), CWRUCraft.copyInventory(event.getPlayer().getInventory()));
			CWRUCraft.loginLocs.put(event.getPlayer().getName(), event.getPlayer().getLocation());
			event.getPlayer().getInventory().clear();
			CWRUCraft.toLogin.add(event.getPlayer().getName());
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		event.setFormat(CWRUCraft.updatedPlayerName(event.getPlayer().getName()) + " : " + event.getMessage());
	}
	
	/*
	 * When a player moves
	 */
/*	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if(CWRUCraft.toRegister.contains(event.getPlayer().getName()) || CWRUCraft.toLogin.contains(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}*/
	
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		for(String pl:CWRUCraft.commandListening) {
			Bukkit.getPlayer(pl).sendMessage(event.getPlayer().getName() + " : " + event.getMessage());
		}
	}
}
