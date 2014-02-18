package com.proptiger.data.thirdparty;

import java.io.Serializable;

/**
 * Point.java
 * 
 * The Point class represents a location in the two-dimensional Cartesian
 * corrdinate space.
 * 
 * Created: Aug 15 2006
 * 
 * Author: Xiumin Diao (xiumin@nmsu.edu)
 * 
 */

public class Point implements Serializable {
    // The x corrdinate of a point
    private double x;
    // The y corrdinate of a point
    private double y;

    // Construct a point at the origin
    public Point() {
        x = 0;
        y = 0;
    }

    // Construct a point at the specified location (xVal, yVal)
    public Point(double xVal, double yVal) {
        x = xVal;
        y = yVal;
    }

    // Construct a point with the same location as the specified point
    public Point(Point point) {
        x = point.x;
        y = point.y;
    }

    // Get the x corrdinate
    public double getX() {
        return x;
    }

    // Get the y corrdinate
    public double getY() {
        return y;
    }

    // Set the x corrdinate
    public void setX(double xVal) {
        x = xVal;
    }

    // Set the y corrdinate
    public void setY(double yVal) {
        y = yVal;
    }

    // Translate a point to the specified location
    public void translate(Point point) {
        translate(point.x, point.y);
    }

    // Translate a point to the specified location (newX, newY)
    public void translate(double newX, double newY) {
        x = newX;
        y = newY;
    }

    // Offset a point along the x and y axes by dx and dy, respectively
    public void offset(double dx, double dy) {
        x += dx;
        y += dy;
    }

    // Calcualte the distance between two points
    public double distance(Point point) {
        double dx = x - point.x;
        double dy = y - point.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Calcualte the square of the distance between two points
    public double distance2(Point point) {
        double dx = x - point.x;
        double dy = y - point.y;
        return (dx * dx + dy * dy);
    }

    // Calculate the middle point between two points
    public Point midPoint(Point point) {
        return new Point((x + point.x) / 2, (y + point.y) / 2);
    }

    // Determine whether two points are equal
    public boolean equals(Point point) {
        return (x == point.x) && (y == point.y);
    }

    // Return a representation of a point as a string
    public String toString() {
        return "point = (" + x + "," + y + ")";
    }
}
