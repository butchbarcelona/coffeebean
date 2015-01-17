package com.ust.thesis.prototype.project.WeSync;

import java.util.ArrayList;
import java.util.HashMap;

import com.ust.thesis.prototype.project.WeSync.chord.ColorPoint;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class DrawPanel_butch extends View { 
	Context otherc;
	private Paint paint;
	private Paint paint2;
	String penColor;
	private boolean erase=false;
	private HashMap<String, ArrayList<ColorPoint>> pts2;
	private HashMap<String, ArrayList<ArrayList<ColorPoint>>> strks2;
	private ArrayList<ColorPoint> pts;
	private ArrayList<ArrayList<ColorPoint>> strks;
	
	
	
	
	public DrawPanel_butch(Context context) {
		super(context);
		pts2 = new HashMap<String, ArrayList<ColorPoint>>();
		strks2 = new HashMap<String, ArrayList<ArrayList<ColorPoint>>>();
		pts = new ArrayList<ColorPoint>();
		strks = new ArrayList<ArrayList<ColorPoint>>();
		
		paint = createPaint(Color.BLACK, 8);
		paint2 = createPaint(Color.BLACK, 8);
		System.out.println("Draw Panel");
		
	}

	@Override
	public void onDraw(Canvas c){
		super.onDraw(c);
		this.setBackgroundColor(Color.WHITE);

		System.out.println("on Draw");
		
		for(ArrayList<ColorPoint> strokes: strks){
			drawStroke2( strokes, c);
		}
		drawStroke2(pts, c); //draws current 

		
		
		for(String key: strks2.keySet()){
			
			for(ArrayList<ColorPoint> strokes: strks2.get(key)){
				drawStroke2(strokes, c);
			}
		}
		
		for(String key: pts2.keySet()){ 
			drawStroke2(pts2.get(key), c);
		}
		
		 
	}


	public void DrawFromOther(String fromNode, String pen,double x, double y){
		System.out.println("Pen Color : "+pen);
		penColor = pen;
		int xint = (int) x;
		int yint = (int) y; 
		
		ArrayList<ColorPoint> tempColorPts = pts2.get(fromNode) ;
		if(tempColorPts == null)
			tempColorPts = new ArrayList<ColorPoint>(); 
		
		tempColorPts.add(new ColorPoint(new Point(xint, yint),pen));
		pts2.put(fromNode, tempColorPts);
		
		invalidate();  
	}
	
	public void EndDrawFromOther(String fromNode){
		String penColor = Global.getColor()+"";
		ArrayList<ColorPoint> tempColorPts = pts2.get(fromNode);
		ArrayList<ArrayList<ColorPoint>> tempStrokes = strks2.get(fromNode);
		
		if(tempStrokes == null)
			tempStrokes = new ArrayList<ArrayList<ColorPoint>>() ;
		
		tempStrokes.add((ArrayList<ColorPoint>) tempColorPts.clone());
		strks2.put(fromNode,tempStrokes);
		pts2.get(fromNode).clear();
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(event.getActionMasked() == MotionEvent.ACTION_MOVE){			
			pts.add(new ColorPoint(new Point((int)event.getX(), (int)event.getY()), Global.getColor()));			
			invalidate();
		}

		if(event.getActionMasked() == MotionEvent.ACTION_UP){
			strks.add((ArrayList<ColorPoint>)pts.clone());
			pts.clear();
		}

		return true;
	}


	private void drawStroke2(ArrayList<ColorPoint> nodeStrokes, Canvas c){
		System.out.println("Pen Color Strokes: "+penColor);
		
		String pastPenColor = "Black";
		Paint nodePaint = null;
		
		if (nodeStrokes.size() > 0) {			
			Point p0 = (Point)nodeStrokes.get(0).pt;
			for (int i = 1; i < nodeStrokes.size(); i++) {
				penColor = nodeStrokes.get(i).color;
				
				if(penColor != pastPenColor || nodePaint == null){
					nodePaint = createPaint(penColor);
					pastPenColor = penColor;
				}
				
				Point p1 =  (Point)nodeStrokes.get(i).pt;
				c.drawLine(p0.x, p0.y, p1.x, p1.y, nodePaint);
				p0 = p1;
			}
		} 
	}

	
	
	
	public void setErase(boolean isErase){
		erase=isErase;
/*		if(erase) paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		//paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));}
		else paint.setXfermode(null);
		//paint2.setXfermode(null);}
*/	}

	
	private Paint createPaint(String penColor){
		if(penColor.equals("Red")) return createPaint(Color.RED , 8);
		else if(penColor.equals("Magenta")) return createPaint(Color.MAGENTA , 8);
		else if(penColor.equals("Yellow")) return createPaint(Color.YELLOW , 8);
		else if(penColor.equals("Green")) return createPaint(Color.GREEN, 8);
		else if(penColor.equals("Blue")) return createPaint(Color.BLUE , 8);
		else if(penColor.equals("Cyan")) return createPaint(Color.CYAN , 8);
		else if(penColor.equals("White")) return createPaint(Color.WHITE , 8);
		else return createPaint(Color.BLACK , 8);
	}
	
	private Paint createPaint(int color, float width){
		
		Paint temp = new Paint();
		temp.setStyle(Paint.Style.STROKE);
		temp.setAntiAlias(true);
		temp.setColor(color);
		temp.setStrokeWidth(width);
		temp.setStrokeCap(Cap.ROUND);

		return temp;
	}


}
