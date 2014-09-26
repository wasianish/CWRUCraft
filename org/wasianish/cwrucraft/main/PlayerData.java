package org.wasianish.cwrucraft.main;


public class PlayerData {
	
	// Fields
	public String caseID;
	private String password;
	public String name;
	public boolean isFinal = false;
	public String major;
	public int year;
	
	public PlayerData (String nam) {
		name = nam;
	}
	
	// Set an already encrypted password
	public void setEncrPass(String encPass) {
		password = encPass;
	}
	
	// Set a password to be encrypted
	public void setPass(String pass) {
		try {
			password = javax.xml.bind.DatatypeConverter.printHexBinary(CWRUCraft.encrypt(password));
		} catch (Exception e) {
			password = "";
			e.printStackTrace();
		}
	}
	
	// Check pass
	public boolean isPass(String pass) {
			try {
				return (javax.xml.bind.DatatypeConverter.printHexBinary(CWRUCraft.encrypt(pass)).equals(password));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
	}
	
	// Get the encrypted pass
	public String getEncPass() {
		return password;
	}
	
}
