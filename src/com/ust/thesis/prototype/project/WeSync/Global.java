package com.ust.thesis.prototype.project.WeSync;

public class Global{
	
	private static String color;
	private static String playerName;
	private static String playerType;
	
	public Global(String color,String playerName, String playerType) {
    	this.color = color;
    	this.playerName = playerName;
    	this.playerType = playerType;
	}
	
	public static String getColor() {
		return color;
	}
	
	public static void setColor(String aColor) {
	   color = aColor;
	}
	public static String getPlayerName() {
		return playerName;
	}
	
	public static void setPlayerName(String aplayerName) {
	   playerName = aplayerName;	
	}
	public static String getPlayerType() {
		return playerType;
	}
	
	public static void setPlayerType(String aplayerType) {
	   playerType = aplayerType;
	}
   
	
}
 