package org.wasianish.cwrucraft.main;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ResetPlayerRunnable extends BukkitRunnable {

	@Override
	public void run() {
		for(String temp:CWRUCraft.cantDoShit) {
			try{
				Bukkit.getPlayer(temp).teleport(CWRUCraft.loginLocs.get(temp));
			} catch(Exception e) {}
		}
	}

}
