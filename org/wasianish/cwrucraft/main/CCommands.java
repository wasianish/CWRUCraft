package org.wasianish.cwrucraft.main;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CCommands implements CommandExecutor {
	
	// Random numer var
	private Random rand = new Random();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		switch(cmd.getName().toLowerCase()) {
		// Command is /login <pass>
		case "login":
			// Check if allowed to login
			if(!CWRUCraft.toLogin.contains(sender.getName())) {
				if(CWRUCraft.toRegister.contains(sender.getName())) {
					sender.sendMessage(ChatColor.RED + "You have not registered.  Please use /register");
				} else if(CWRUCraft.toConfirm.containsKey(sender.getName())) {
					sender.sendMessage(ChatColor.RED + "You have not confirmed your caseid.  Check your email for instructions.");
				} else {
					sender.sendMessage(ChatColor.RED + "You are already logged in.");
				}
				return true;
			}
			// Check pass
			if(CWRUCraft.playerData.get(sender.getName()).isPass(args[0])) {
				CWRUCraft.toLogin.remove(sender.getName());
			} else {
				sender.sendMessage(ChatColor.RED + "Wrong password");
			}
			break;
			
		// Command is /register <caseid> <pass> <pass>
		case "register":
			// Check if allowed to register
			if(!CWRUCraft.toRegister.contains(sender.getName())) {
				sender.sendMessage(ChatColor.RED + "You have already registered");
				if(CWRUCraft.toLogin.contains(sender.getName())) {
					sender.sendMessage(ChatColor.RED + "Use /login to login");
				} else if(CWRUCraft.toConfirm.containsKey(sender.getName())) {
					sender.sendMessage(ChatColor.RED + "A confirmation email has been sent to you case email, use /confirm");
				} else {
					sender.sendMessage(ChatColor.RED + "You are already logged in as well");
				}
				return true;
			}
			// Do passwords match
			if(!args[1].equals(args[2])) {
				sender.sendMessage("Passwords must match");
				return true;
			}
			// Default confirmation
			String confirm = Integer.toString(rand.nextInt(Integer.MAX_VALUE));
			try {
				// Confirmation number is encrypted case id
				confirm = javax.xml.bind.DatatypeConverter.printHexBinary(CWRUCraft.encrypt(args[0]));
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Set playerdata
			CWRUCraft.playerData.get(sender.getName()).caseID = args[0];
			CWRUCraft.playerData.get(sender.getName()).setPass(args[1]);
			// Set waiting for confirm
			CWRUCraft.toConfirm.put(sender.getName(),confirm);
			// No longer needs to register
			CWRUCraft.toRegister.remove(sender.getName());
			// Send confirmation email
			CMailer.sendConfirm(args[0], confirm);
			// Send player message
			sender.sendMessage(ChatColor.GREEN + "Thank you for registering, please cheeck your case email for confirmation before logging in");
			break;
		// Command is /confirm <confirmstring>
		case "confirm":
			// Check if allowed to confirm
			if(!CWRUCraft.toConfirm.containsKey(sender.getName())) {
				if(!CWRUCraft.toRegister.contains(sender.getName())) {
					sender.sendMessage(ChatColor.RED + "You have already registered and confirmed");
				} else {
					sender.sendMessage(ChatColor.RED + "You have not registered yet");
				}
				return true;
			}
			// Check confirm number
			if(!CWRUCraft.toConfirm.get(sender.getName()).equals(args[0])) {
				sender.sendMessage(ChatColor.RED + "That confirmation number does not match the one we sent you.");
				return true;
			}
			// No longer needs to confirm
			CWRUCraft.toConfirm.remove(sender.getName());
			// Needs to login
			CWRUCraft.toLogin.add(sender.getName());
			// Send player message
			sender.sendMessage(ChatColor.RED + "Thank you for confirming your case id, login with /login <caseid> <pass>");
			break;
		// Command is /c <on|off>
		case "c":
			// Check op
			if(!sender.isOp()) {
				sender.sendMessage(ChatColor.RED + "You have to be OP to spy on my commands");
				return true;
			}
			// Turn on or off
			if(args[0].equals("on")) {
				if(!CWRUCraft.commandListening.contains(sender.getName())) {
					CWRUCraft.commandListening.add(sender.getName());
				}
			} else {
				if(CWRUCraft.commandListening.contains(sender.getName())) {
					CWRUCraft.commandListening.remove(sender.getName());
				}
			}
			break;
		}
		return false;
	}

}
