package com.ust.thesis.prototype.project.WeSync;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class WeSyncVideoView extends VideoView{
	
	private IWeSyncVideoView listener;
	
	public WeSyncVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public WeSyncVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public WeSyncVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public void setListener(IWeSyncVideoView i){
		listener = i;
	}
	
	@Override
    public void pause() {
        super.pause();
        if (listener != null) {
        	listener.pause();
        }
    }

    @Override
    public void start() {
        super.start();
        if (listener != null) {
        	listener.play();
        }
    }
	
    @Override
    public void seekTo(int msec) {
    	// TODO Auto-generated method stub
    	super.seekTo(msec);

        if (listener != null) {
        	listener.seekTo(msec);
        }
    }
	
	
	
}
