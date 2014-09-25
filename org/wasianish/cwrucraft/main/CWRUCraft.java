package org.wasianish.cwrucraft.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class CWRUCraft extends JavaPlugin {
	
	public static HashMap<String,PlayerData> playerData = new HashMap<String,PlayerData>();
	public static List<String> commandListening = new ArrayList<String>();
	public static CCommands commandExecutor;
	public static CListener loginListener;
	private static String encryptionKey;
	private static String IV = "AAAAAAAAAAAAAAAA";
	
	private static BukkitScheduler scheduler;
	
	private static File configFile;
	private static File playerDataDir;
	private static FileConfiguration config;
	
	public void onEnable()	 {
		//Register commands
		commandExecutor = new CCommands();
		this.getCommand("register").setExecutor(commandExecutor);
		this.getCommand("login").setExecutor(commandExecutor);
		this.getCommand("confirm").setExecutor(commandExecutor);
		this.getCommand("c").setExecutor(commandExecutor);
		
		loginListener = new CListener();
		this.getServer().getPluginManager().registerEvents(loginListener, this);
		
		scheduler = Bukkit.getServer().getScheduler();
		
		configFile = new File(getDataFolder(), "config.yml");
		playerDataDir = new File(getDataFolder().getAbsolutePath() + File.separator + "PlayerData");
		try{
			firstRun();
		} catch(Exception e) {
			e.printStackTrace();
		}
		config = new YamlConfiguration();
		loadConfig();
		loadPlayerData();
		
		scheduler.scheduleSyncRepeatingTask(this, new LoginRegisterReminder(), 0L, 200L);
	}
	
	public void onDisable() {
		
	}
	
	public static boolean createNewPlayer(String name) {
		if(playerData.containsKey(name)) {
			return false;
		}
		playerData.put(name, new PlayerData(name));
		return true;
	}
	
	public static void loadConfig() {
		try {
			config.load(configFile);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		encryptionKey = config.getString("encryptionkey");
	}
	
	public static void loadPlayerData() {
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
		}
		
	}
	
	public static void storeConfig() {
		try {
			config.save(configFile);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void storePlayerData() {
		for(String key: playerData.keySet()) {
			File tempPlayerFile = new File(playerDataDir, key + ".yml"); //Get filename
			if(!tempPlayerFile.exists()) {
				tempPlayerFile.mkdirs(); //Create missing directories
			}
			FileConfiguration tempPlayerConfig = new YamlConfiguration(); //Initialize temp config
			tempPlayerConfig.set("Name", playerData.get(key).name); //Save name
			tempPlayerConfig.set("CaseID", playerData.get(key).caseID); //Save caseid
			tempPlayerConfig.set("Password",playerData.get(key).getPass()); //Save password
			try {
				tempPlayerConfig.save(tempPlayerFile); //Save file
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void firstRun() throws Exception
	{
		if(!configFile.exists())
		{
			configFile.getParentFile().mkdirs();
			copy(getResource("config.yml"), configFile);
		}
	}
	
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
	
	public static byte[] encrypt(String plainText) throws Exception {
	    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
	    SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
	    cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
	    return cipher.doFinal(plainText.getBytes("UTF-8"));
	}
	
	public static String decrypt(byte[] cipherText) throws Exception{
	    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
	    SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
	    cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
	    return new String(cipher.doFinal(cipherText),"UTF-8");
	}
	
	public static boolean hasPlayed(String name) {
		for(OfflinePlayer temp:Arrays.asList(Bukkit.getOfflinePlayers())) {
			if(temp.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
}
