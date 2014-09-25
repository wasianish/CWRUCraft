package org.wasianish.cwrucraft.main;


public class PlayerData {
	
	public String caseID;
	private String password;
	public String name;
	public boolean isFinal = false;
	
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
			// TODO Auto-generated catch block
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
	
	public String getPass() {
		return password;
	}
	
	public boolean hasRegistered() {
		return (caseID != null && password != null);
	}
	
}
