package com.company.theshapefixer;

import java.util.ArrayList;
import java.util.List;

public class Shape2D {
    private List<Point2D> points;

    public Shape2D(List<Point2D> points) {
        this.points = new ArrayList<>();

        for (Point2D point : points) {
            this.points.add(new Point2D(point.x(), point.y()));
        }
    }

    public List<Point2D> getPoints() {
        return points;
    }
}
