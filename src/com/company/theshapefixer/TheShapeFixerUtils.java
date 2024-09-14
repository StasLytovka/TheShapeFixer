package com.company.theshapefixer;

import java.util.ArrayList;
import java.util.List;

public class TheShapeFixerUtils {

    private TheShapeFixerUtils() {
    }

    public static List<Point2D> convertToPoints(int[][] pointsArray) {
        ArrayList<Point2D> points = new ArrayList<>();
        for (int[] point : pointsArray) {
            points.add(new Point2D(point[0], point[1]));
        }
        return points;
    }
}
