package com.ust.thesis.prototype.project.WeSync.chord;

public enum RoomType {

	HOST("host"), 
	WHITEBOARD("whiteboard"), 
	MUSIC("music"), 
	VIDEO("video"), 
	PICTURE("picture"),
	DOCUMENT("document"), 
	SURVEY("survey");
	
	String str;

	public String getString(){
		return this.str;
	}
	
	private RoomType(String str){
		this.str = str; 
	}

	public static RoomType getRoomType(String str){
		for(RoomType type: RoomType.values()){
			if(str.equals(type.getString()))
				return type;
		}
		
		return null;
	}
}
