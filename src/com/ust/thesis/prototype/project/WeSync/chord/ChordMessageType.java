package com.ust.thesis.prototype.project.WeSync.chord;

public enum ChordMessageType {


	SENDING_NAME("sending_name"),
	CHANGING_ROOM("changing_room"),	
	
	MUSIC_PLAY("music_player"),
	
	MUSIC_PLAY_DATA_COMPLETE("music_player_complete"),
	MUSIC_PLAY_PLAY("music_player_play"),
	MUSIC_PLAY_SKIP_TO("music_player_skip_to"),
	
	//TODO:
	MUSIC_PLAY_PAUSE("music_player_pause"),
	MUSIC_PLAY_CONTINUE("music_player_continue"),
	
	VIDEO_PLAY("video_player"),

	VIDEO_PLAY_DATA_COMPLETE("video_player_complete"),
	VIDEO_PLAY_PLAY("video_player_play"),
	VIDEO_PLAY_SKIP_TO("video_player_skip_to"),
	
	//TODO:
	VIDEO_PLAY_PAUSE("video_player_pause"),
	VIDEO_PLAY_CONTINUE("video_player_continue"),
	
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
