package com.ust.thesis.prototype.project.WeSync.chord;

import android.graphics.Point;

public class ColorPoint {

	public Point pt;
	public String color;
	public String fromNode;


	public ColorPoint(Point point, String clr) {

		this.pt = point;
		this.color = clr;

	}

	public ColorPoint(String fromNode, Point point, String clr) {

		this.fromNode = fromNode;
		this.pt = point;
		this.color = clr;

	}
}
