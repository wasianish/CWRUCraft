package org.wasianish.cwrucraft.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class LoginRegisterReminder extends BukkitRunnable {
	@Override
	public void run() {
		for(String name:CWRUCraft.toRegister) {
			// Remind to register
			Bukkit.getPlayer(name).sendMessage(ChatColor.RED + "Register with /register <caseid> <newpass> <confirmpass>");
		}
		for(String name:CWRUCraft.toLogin) {
			// Remind to login
			Bukkit.getPlayer(name).sendMessage(ChatColor.RED + "Login with /login <caseid> <password>");
		}
	}
}