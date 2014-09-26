package org.wasianish.cwrucraft.main;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class AnnouncementRunnable extends BukkitRunnable {
	private int counter = 0;
	
	@Override
	public void run() {
		// Broadcast announcements
		Bukkit.getServer().broadcastMessage(CWRUCraft.announcements.get(counter % CWRUCraft.announcements.size()));
		counter++;
	}

}
