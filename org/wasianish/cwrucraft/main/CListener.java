package org.wasianish.cwrucraft.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class CListener implements Listener {

	public List<String> toRegister = new ArrayList<String>();
	public List<String> toLogin = new ArrayList<String>();
	
	
	
	public void hasRegistered(String name) {
		toRegister.remove(name);
	}
	
	public void hasLoggedIn(String name) {
		toLogin.remove(name);
	}
	
	
	/*
	 * When a player first queries the server
	 */
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		if(!CWRUCraft.hasPlayed(event.getPlayer().getName())) {
			toRegister.add(event.getPlayer().getName());
			CWRUCraft.playerData.put(event.getPlayer().getName(), new PlayerData(event.getPlayer().getName()));
		}
	}
	
	/*
	 * When a player actually joins the server
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(!toRegister.contains(event.getPlayer().getName())) {
			toLogin.add(event.getPlayer().getName());
		}
	}
	
	/*
	 * When a player moves
	 */
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if(toRegister.contains(event.getPlayer().getName()) || toLogin.contains(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}
}