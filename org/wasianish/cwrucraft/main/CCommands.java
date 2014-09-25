package org.wasianish.cwrucraft.main;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CCommands implements CommandExecutor {
	
	private HashMap<String,String> toConfirm = new HashMap<String,String>();
	private Random rand = new Random();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2,
			String[] args) {
		switch(cmd.getName().toLowerCase()) {
		case "login":
			// Command is /login <pass>
			if(!CWRUCraft.loginListener.toLogin.contains(sender.getName())) {
				if(CWRUCraft.loginListener.toRegister.contains(sender.getName())) {
					sender.sendMessage(ChatColor.RED + "You have not registered.  Please use /register");
				} else if(toConfirm.containsKey(sender.getName())) {
					sender.sendMessage(ChatColor.RED + "You have not confirmed your caseid.  Check your email for instructions.");
				} else {
					sender.sendMessage(ChatColor.RED + "You are already logged in.");
				}
				return true;
			}
			if(CWRUCraft.playerData.containsKey(sender.getName())) {
				if(CWRUCraft.playerData.get(sender.getName()).isPass(args[0])) {
					CWRUCraft.loginListener.toLogin.remove(sender.getName());
				}
			}
			break;
		case "register":
			// Command is /register <caseid> <pass> <pass>
			if(!CWRUCraft.loginListener.toRegister.contains(sender.getName())) {
				sender.sendMessage(ChatColor.RED + "You have already registered");
				if(CWRUCraft.loginListener.toLogin.contains(sender.getName())) {
					sender.sendMessage(ChatColor.RED + "Use /login to login");
				} else if(toConfirm.containsKey(sender.getName())) {
					sender.sendMessage(ChatColor.RED + "A confirmation email has been sent to you case email, use /confirm");
				} else {
					sender.sendMessage(ChatColor.RED + "You are already logged in as well");
				}
				return true;
			}
			if(!args[1].equals(args[2])) {
				sender.sendMessage("Passwords must match");
				return true;
			}
			String confirm = Integer.toString(rand.nextInt(Integer.MAX_VALUE));
			try {
				confirm = javax.xml.bind.DatatypeConverter.printHexBinary(CWRUCraft.encrypt(args[0]+sender.getName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			CWRUCraft.playerData.get(sender.getName()).caseID = args[0];
			CWRUCraft.playerData.get(sender.getName()).setPass(args[1]);
			toConfirm.put(sender.getName(),confirm);
			CWRUCraft.loginListener.toRegister.remove(sender.getName());
			CMailer.sendConfirm(args[0], confirm);
			sender.sendMessage(ChatColor.GREEN + "Thank you for registering, please cheeck your case email for confirmation before logging in");
			break;
		case "confirm":
			if(!toConfirm.containsKey(sender.getName())) {
				if(!CWRUCraft.loginListener.toRegister.contains(sender.getName())) {
					sender.sendMessage(ChatColor.RED + "You have already registered and confirmed");
				} else {
					sender.sendMessage(ChatColor.RED + "You have not registered yet");
				}
				return true;
			}
			if(!toConfirm.get(sender.getName()).equals(args[0])) {
				sender.sendMessage(ChatColor.RED + "That confirmation number does not match the one we sent you.");
				return true;
			}
			toConfirm.remove(sender.getName());
			CWRUCraft.loginListener.toLogin.add(sender.getName());
			sender.sendMessage(ChatColor.RED + "Thank you for confirming your case id, login with /login <caseid> <pass>");
			break;
		case "c":
			if(!sender.isOp()) {
				sender.sendMessage(ChatColor.RED + "You have to be OP to spy on my commands");
				return true;
			}
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
