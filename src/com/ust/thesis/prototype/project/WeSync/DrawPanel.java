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

public class DrawPanel extends View { 
  Context otherc;
  private Paint paint;
  private Paint paint2;
  String penColor;
  private boolean erase=false;
  private ArrayList points;
  private ArrayList strokes;
  private ArrayList points2;
  private ArrayList strokes2;
  public DrawPanel(Context context) {
  super(context);
  points = new ArrayList();
  strokes = new ArrayList();
  points2 = new ArrayList();
  strokes2 = new ArrayList();
  paint = createPaint(Color.BLACK, 8);
  paint2 = createPaint(Color.BLACK, 8);
  System.out.println("Draw Panel");
 }

 @Override
 public void onDraw(Canvas c){
  super.onDraw(c);
  this.setBackgroundColor(Color.WHITE);
  

  System.out.println("on Draw");
  for(Object obj: strokes){
   drawStroke((ArrayList)obj, c);
  }
  drawStroke(points, c);
  
  for(Object obj: strokes2){
	   drawStroke2((ArrayList)obj, c);
  }
	  drawStroke2(points2, c);
 }
 
 
 public void DrawFromOther(String pen,double x, double y){
	 System.out.println("Pen Color : "+pen);
	 penColor = pen;
	 int xint = (int) x;
	 int yint = (int) y;
	 points2.add(new Point(xint, yint));
     invalidate();  
 }
 public void EndDrawFromOther(){
	 String penColor = Global.getColor()+"";
	 this.strokes2.add(points2);
	 points2 = new ArrayList();  
 }
 
 @Override
 public boolean onTouchEvent(MotionEvent event){
  if(event.getActionMasked() == MotionEvent.ACTION_MOVE){
   points.add(new Point((int)event.getX(), (int)event.getY()));	
   invalidate();
  }
  
  if(event.getActionMasked() == MotionEvent.ACTION_UP){
   this.strokes.add(points);
   points = new ArrayList();
  }
  
  return true;
 }
 
 
 
 private void drawStroke(ArrayList strokes, Canvas c){
	
  System.out.println("draw Stroke ");
  if (strokes.size() > 0) {
   Point p0 = (Point)strokes.get(0);
   for (int i = 1; i < strokes.size(); i++) {
	   //"Red","Magenta","Yellow", "Green", "Blue","Cyan"};
		String globalPen = Global.getColor()+"";
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
  
  strokes = new ArrayList();
 }
 
 
 private void drawStroke2(ArrayList strokes2, Canvas c){
	 System.out.println("Pen Color Strokes: "+penColor);
	  if (strokes2.size() > 0) {
	   Point p0 = (Point)strokes2.get(0);
	   for (int i = 1; i < strokes2.size(); i++) {
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
