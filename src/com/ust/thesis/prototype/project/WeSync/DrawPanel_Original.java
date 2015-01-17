package com.ust.thesis.prototype.project.WeSync;

import java.util.ArrayList;

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

public class DrawPanel_Original extends View { 
	Context otherc;
	private Paint paint;
	private Paint paint2;
	String penColor;
	private boolean erase=false;
	private ArrayList points;
	private ArrayList strokes;
	private ArrayList<String> colors;
	private ArrayList<ArrayList<String>> colorsStrokes;
	
	private ArrayList points2;
	private ArrayList strokes2;
	private ArrayList<String> colors2;
	private ArrayList<ArrayList<String>> colorsStrokes2;
	
	public DrawPanel_Original(Context context) {
		super(context);
		points = new ArrayList();
		strokes = new ArrayList();
		colors = new ArrayList<String>();
		colorsStrokes = new ArrayList<ArrayList<String>>();
		
		
		
		points2 = new ArrayList();
		strokes2 = new ArrayList();
		colors2 = new ArrayList<String>();
		colorsStrokes2 = new ArrayList<ArrayList<String>>();
		
		paint = createPaint(Color.BLACK, 8);
		paint2 = createPaint(Color.BLACK, 8);
		System.out.println("Draw Panel");
	}

	@Override
	public void onDraw(Canvas c){
		super.onDraw(c);
		this.setBackgroundColor(Color.WHITE);

		System.out.println("on Draw");
		int count = 0;
		for(Object obj: strokes){
			drawStroke((ArrayList)obj,colorsStrokes.get(count), c);
			count++;
		}
		drawStroke(points, colors, c); //draws current 

		count = 0;
		for(Object obj: strokes2){
			drawStroke2((ArrayList)obj, colorsStrokes2.get(count), c);
			count++;
		}
		drawStroke2(points2,colors2, c);
	}


	public void DrawFromOther(String pen,double x, double y){
		System.out.println("Pen Color : "+pen);
		penColor = pen;
		int xint = (int) x;
		int yint = (int) y;
		colors2.add(pen);
		points2.add(new Point(xint, yint));
		invalidate();  
	}
	public void EndDrawFromOther(){
		String penColor = Global.getColor()+"";
		this.strokes2.add(points2);
		points2 = new ArrayList();  
		this.colorsStrokes2.add(colors2); 
		colors2 = new ArrayList<String>();
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(event.getActionMasked() == MotionEvent.ACTION_MOVE){
			colors.add(Global.getColor());
			points.add(new Point((int)event.getX(), (int)event.getY()));	
			invalidate();
		}

		if(event.getActionMasked() == MotionEvent.ACTION_UP){
			this.strokes.add(points);
			
			this.colorsStrokes.add(colors);
			points = new ArrayList();
			colors = new ArrayList<String>();
		}

		return true;
	}



	private void drawStroke(ArrayList strokes, ArrayList colors, Canvas c){

		System.out.println("draw Stroke ");
		
		String globalPen = "Black";
		
		if (strokes.size() > 0) {
			Point p0 = (Point)strokes.get(0);
			for (int i = 1; i < strokes.size(); i++) {
				//"Red","Magenta","Yellow", "Green", "Blue","Cyan"};
				//String globalPen = Global.getColor()+"";
				
				if(colors.size() > i)
					globalPen = (String) colors.get(i);
				
				if(globalPen.equals("Red")) paint = createPaint(Color.RED , 8);
				else if(globalPen.equals("Magenta")) paint = createPaint(Color.MAGENTA , 8);
				else if(globalPen.equals("Yellow")) paint = createPaint(Color.YELLOW , 8);
				else if(globalPen.equals("Green")) paint = createPaint(Color.GREEN, 8);
				else if(globalPen.equals("Blue")) paint = createPaint(Color.BLUE , 8);
				else if(globalPen.equals("Cyan")) paint = createPaint(Color.CYAN , 8);
				else if(globalPen.equals("White")) paint = createPaint(Color.WHITE , 8);
				else if(globalPen.equals("Black")) paint = createPaint(Color.BLACK , 8);
				
				Point p1 = (Point)strokes.get(i); 
				
				c.drawLine(p0.x, p0.y, p1.x, p1.y, paint);
				p0 = p1;
			}
		}
		colors = new ArrayList();
		strokes = new ArrayList();
	}


	private void drawStroke2(ArrayList strokes2, ArrayList colors2, Canvas c){
		System.out.println("Pen Color Strokes: "+penColor);
		if (strokes2.size() > 0) {
			Point p0 = (Point)strokes2.get(0);
			for (int i = 1; i < strokes2.size(); i++) {


				if(colors2.size() > i)
					penColor = (String) colors2.get(i);
				
				if(penColor.equals("Red")) paint2 = createPaint(Color.RED , 8);
				else if(penColor.equals("Magenta")) paint2 = createPaint(Color.MAGENTA , 8);
				else if(penColor.equals("Yellow")) paint2 = createPaint(Color.YELLOW , 8);
				else if(penColor.equals("Green")) paint2 = createPaint(Color.GREEN, 8);
				else if(penColor.equals("Blue")) paint2 = createPaint(Color.BLUE , 8);
				else if(penColor.equals("Cyan")) paint2 = createPaint(Color.CYAN , 8);
				else if(penColor.equals("White")) paint2 = createPaint(Color.WHITE , 8);
				else paint2 = createPaint(Color.BLACK , 8);

				Point p1 = (Point)strokes2.get(i); 
				c.drawLine(p0.x, p0.y, p1.x, p1.y, paint2);
				p0 = p1;
			}
		}

		colors2 = new ArrayList();
		strokes2 = new ArrayList();
	}

	public void setErase(boolean isErase){
		erase=isErase;
		if(erase) paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		//paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));}
		else paint.setXfermode(null);
		//paint2.setXfermode(null);}
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
