package org.wasianish.cwrucraft.main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;

public class CListener implements Listener {
	
	
	/*
	 * When a player first queries the server
	 */
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		// Check IP
		for(Player temp:Bukkit.getOnlinePlayers()) {
			if(event.getAddress().getHostAddress().equals(temp.getAddress().getAddress().getHostAddress())) {
				event.disallow(Result.KICK_OTHER, "Same IP already on server");
			}
		}
		// If first time
		if(!Bukkit.getOfflinePlayer(event.getPlayer().getName()).hasPlayedBefore()) {
			// Add to register
			CWRUCraft.toRegister.add(event.getPlayer().getName());
			// Create player data
			CWRUCraft.createNewPlayer(event.getPlayer().getName());
		}
	}
	
	/*
	 * When a player actually joins the server
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// If doesnt need to register
		if(!CWRUCraft.toRegister.contains(event.getPlayer().getName())) {
			CWRUCraft.toLogin.add(event.getPlayer().getName());
		}
	}
	
	/*
	 * When a player moves
	 */
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if(CWRUCraft.toRegister.contains(event.getPlayer().getName()) || CWRUCraft.toLogin.contains(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		for(String pl:CWRUCraft.commandListening) {
			Bukkit.getPlayer(pl).sendMessage(event.getPlayer().getName() + " : " + event.getMessage());
		}
	}
}
