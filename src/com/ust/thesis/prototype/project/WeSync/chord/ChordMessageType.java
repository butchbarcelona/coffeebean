package com.ust.thesis.prototype.project.WeSync.chord;

public enum ChordMessageType {


	SENDING_NAME("sending_name"),
	CHANGING_ROOM("changing_room"),	
	
	MUSIC_PLAY("music_player"),
	VIDEO_PLAY("video_player"),
	SHOW_SURVEY("show_survey"),
	SHOW_PICTURE("show_picture"),
	SHOW_DOCUMENT("show_document"),
	WHITEBOARD("whiteboard"),


	CHORD_HELLO_TEST_CHANNEL("com.samsung.android.sdk.chord.example.HELLOTESTCHANNEL"),
	CHORD_SAMPLE_MESSAGE_TYPE("com.samsung.android.sdk.chord.example.MESSAGE_TYPE");


	String str;

	public String getString(){
		return this.str;
	}
	
	private ChordMessageType(String str){
		this.str = str; 
	}

	public static ChordMessageType getSyncType(String str){
		for(ChordMessageType type: ChordMessageType.values()){
			if(str.equals(type.getString()))
				return type;
		}
		
		return null;
	}
	
}
