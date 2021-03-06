package org.wasianish.cwrucraft.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class CWRUCraft extends JavaPlugin {
	
	// Stores the player data
	public static HashMap<String,PlayerData> playerData = new HashMap<String,PlayerData>();
	
	// Stores the names of players to push the command logger to
	public static List<String> commandListening = new ArrayList<String>();
	
	// Can't do shit
	public static List<String> cantDoShit = new ArrayList<String>();
	
	// Player Locations
	public static HashMap<String,Location> loginLocs = new HashMap<String,Location>();
	
	// Cleared player inventories
	public static HashMap<String,ItemStack[]> clearedInventory = new HashMap<String,ItemStack[]>();
	
	// Stores the special majors
	public static String[] goodMajors = {"EE", "CE", "CS"};
	
	// Instance of CCommands
	public static CCommands commandExecutor;
	
	// Instance of CListener
	public static CListener loginListener;
	
	// Encryption key (read from file)
	private static String encryptionKey;
	
	// EmailPass
	public static String emailPass;
	
	// Initialization vector
	private static String IV = "AAAAAAAAAAAAAAAA";
	
	// Stores a list of all announcements to be broadcasted
	public static List<String> announcements = new ArrayList<String>();
	
	// Stores all possible majors at case
	public static HashMap<String,String> majors = new HashMap<String,String>();
	
	// Scheduler for scheduling
	private static BukkitScheduler scheduler;
	
	// Config file location in plugins folder
	private static File configFile;
	
	// List of case majors file in plugins folder
	private static File majorsFile;
	
	// Player data directory in plugins folder
	private static File playerDataDir;
	
	// Stores YAML config data
	public static FileConfiguration config;
	
	// Waiting on confirm
	public static HashMap<String,String> toConfirm = new HashMap<String,String>();
	
	public static List<String> toRegister = new ArrayList<String>();
	public static List<String> toLogin = new ArrayList<String>();
	
	public void onEnable()	 {
		// Register commands
		commandExecutor = new CCommands();
		this.getCommand("register").setExecutor(commandExecutor);
		this.getCommand("login").setExecutor(commandExecutor);
		this.getCommand("confirm").setExecutor(commandExecutor);
		this.getCommand("c").setExecutor(commandExecutor);
		this.getCommand("major").setExecutor(commandExecutor);
		
		// Register event listener
		loginListener = new CListener();
		this.getServer().getPluginManager().registerEvents(loginListener, this);
		
		// Get scheduler
		scheduler = Bukkit.getServer().getScheduler();
		
		// Load list of majors
		majorsFile = new File(getDataFolder(), "Majors.txt");
		loadMajorList();
		
		// Set config file
		configFile = new File(getDataFolder(), "config.yml");
		
		// Set player data dir
		playerDataDir = new File(getDataFolder().getAbsolutePath() + File.separator + "PlayerData");
		
		// First Run
		firstRun();
		
		// Load config
		config = new YamlConfiguration();
		loadConfig();
		
		// Load player data
		loadPlayerData();
		
		// Schedule tasks
		scheduler.scheduleSyncRepeatingTask(this, new LoginRegisterReminder(), 0L, 200L);
		scheduler.scheduleSyncRepeatingTask(this, new AnnouncementRunnable(), 100L, 6000L);
		scheduler.scheduleSyncRepeatingTask(this, new ResetPlayerRunnable(), 0L, 20L);
	}
	
	public void onDisable() {
		// Save data
		saveConfig();
		storePlayerData();
	}
	
	// Creates space for new player data.  Returns true if successfully added
	public static boolean createNewPlayer(String name) {
		if(playerData.containsKey(name)) {
			return false;
		}
		playerData.put(name, new PlayerData(name));
		return true;
	}
	
	// Add a prefix to peoples names
	public static String updatedPlayerName(String name) {
		PlayerData temp = playerData.get(name);
		if(temp.major == "") {
			return name;
		} else {
			for(int i = 0; i < goodMajors.length; i++) {
				if(temp.major.equals(goodMajors[i])) {
					return "[" + ChatColor.GOLD + temp.major + ChatColor.RESET + "] " + name;
				}
			}
			return "[" + ChatColor.DARK_AQUA + temp.major + ChatColor.RESET + "] " + name;
		}
	}
	
	// Load the config and store values
	public static void loadConfig() {
		try {
			config.load(configFile);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		emailPass = config.getString("emailpass");
		encryptionKey = config.getString("encryptionkey");
		announcements = config.getStringList("announcements");
	}
	
	// Load player data from file
	public static void loadPlayerData() {
		if(!playerDataDir.exists()) {
			playerDataDir.mkdirs();
		}
		File[] playerFiles = playerDataDir.listFiles(); //Get all player files
		for(File temp:playerFiles) {
			String tempName = temp.getName().replace(".yml", ""); //Convert file name to name
			FileConfiguration tempPlayerData = new YamlConfiguration(); //Initialize temp YAMLConfig
			try {
				tempPlayerData.load(temp); //Load data
			} catch (Exception e) {
				e.printStackTrace();
			}
			playerData.put(tempName, new PlayerData(tempName)); //Create new player data in hashmap
			playerData.get(tempName).caseID = tempPlayerData.getString("CaseID"); //Get caseID
			playerData.get(tempName).setEncrPass(tempPlayerData.getString("Password")); //Get password
			playerData.get(tempName).major = tempPlayerData.getString("Major");
		}
		
	}
	
	// Save config data
	public static void storeConfig() {
		config.set("announcements", announcements);
		try {
			config.save(configFile);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Saves player data
	public static void storePlayerData() {
		for(String key: playerData.keySet()) {
			File tempPlayerFile = new File(playerDataDir, key + ".yml"); //Get filename
			if(!tempPlayerFile.exists()) {
				tempPlayerFile.getParentFile().mkdirs(); //Create missing directories
			}
			FileConfiguration tempPlayerConfig = new YamlConfiguration(); //Initialize temp config
			try {
				tempPlayerConfig.set("Name", playerData.get(key).name); //Save name
				tempPlayerConfig.set("CaseID", playerData.get(key).caseID); //Save caseid
				tempPlayerConfig.set("Password",playerData.get(key).getEncPass()); //Save password
				tempPlayerConfig.set("Major", playerData.get(key).major);
				tempPlayerConfig.save(tempPlayerFile); //Save file
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Creates dirs and copies files
	public void firstRun() {
		try {
			if(!configFile.exists()) {
				configFile.getParentFile().mkdirs();
				copy(getResource("config.yml"), configFile);
			}
			if(!majorsFile.exists()) {
				majorsFile.getParentFile().mkdirs();
				copy(getResource("Majors.txt"), majorsFile);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Copy a file
	private void copy(InputStream in, File file) {
	    try {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0){
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	// AES encryption
	public static byte[] encrypt(String plainText) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
		    SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
		    cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
		    return cipher.doFinal(plainText.getBytes("UTF-8"));
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// AES decryption
	/*private static String decrypt(byte[] cipherText) {
		try {
		    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
		    SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
		    cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
		    return new String(cipher.doFinal(cipherText),"UTF-8");
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}*/
	
	// Load the list of majors
	private static void loadMajorList() {
	    try {
			BufferedReader br = new BufferedReader(new FileReader(majorsFile.getAbsolutePath())); // Reader
	        String line = br.readLine(); 

	        while (line != null) { // Loop to read each line
	        	String temp = line.split("\t")[0];
	        	if(line.split("\t").length == 2) {
	        		temp = line.split("\t")[1];
	        	}
	        	majors.put(line.split("\t")[0], temp);
	            line = br.readLine();
	        }
	        br.close();
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	// Finds exact match, many matches, or no matches
	public static List<String> findMajor(String in) {
		List<String> matches = new ArrayList<String>();
		for(String temp:majors.keySet()) {
			temp = temp.split("\t")[0];
			if(temp.equalsIgnoreCase(in)) {
				matches.removeAll(matches);
				matches.add(temp);
				break;
			}
			if(temp.toLowerCase().contains(in.toLowerCase())) {
				matches.add(temp);
			}
		}
		return matches;
	}
	
	public static ItemStack[] copyInventory(PlayerInventory p) {
		ItemStack[] out = new ItemStack[36];
		for(int i = 0; i < 36; i++) {
			out[i] = p.getItem(i);
		}
		return out;
	}
	
	public static void copyToInventory(ItemStack[] is, PlayerInventory p) {
		for(int i = 0; i < 36; i++) {
			p.setItem(i, is[i]);
		}
	}
	
	public void info(String s) {
		getLogger().info(s);
	}
	
}
