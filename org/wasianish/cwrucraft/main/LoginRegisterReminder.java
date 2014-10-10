package org.wasianish.cwrucraft.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class LoginRegisterReminder extends BukkitRunnable {
	@Override
	public void run() {
		for(String name:CWRUCraft.toRegister) {
			// Remind to register
			try {
				Bukkit.getPlayer(name).sendMessage(ChatColor.RED + "Register with /register <caseid> <newpass> <confirmpass>");
			} catch(Exception e) {}
		}
		for(String name:CWRUCraft.toLogin) {
			// Remind to login
			try {
				Bukkit.getPlayer(name).sendMessage(ChatColor.RED + "Login with /login <caseid> <password>");
			} catch(Exception e) {}
		}
	}
}