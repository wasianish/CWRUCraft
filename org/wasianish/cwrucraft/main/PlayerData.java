package org.wasianish.cwrucraft.main;


public class PlayerData {
	
	public String caseID;
	private String password;
	public String name;
	public boolean isFinal = false;
	public String major;
	public int year;
	
	public PlayerData (String nam) {
		name = nam;
	}
	
	public void setEncrPass(String encPass) {
		password = encPass;
	}
	
	public void setPass(String pass) {
		try {
			password = javax.xml.bind.DatatypeConverter.printHexBinary(CWRUCraft.encrypt(password));
		} catch (Exception e) {
			password = "";
			e.printStackTrace();
		}
	}
	
	public boolean isPass(String pass) {
			try {
				return (javax.xml.bind.DatatypeConverter.printHexBinary(CWRUCraft.encrypt(pass)).equals(password));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
	}
	
	public String getEncPass() {
		return password;
	}
	
	public boolean hasRegistered() {
		return (caseID != null && password != null);
	}
	
}
