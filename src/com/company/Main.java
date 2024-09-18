package com.company;

import com.company.theshapefixer.Point2D;
import com.company.theshapefixer.Shape2D;
import com.company.theshapefixer.TheShapeFixer;
import com.company.theshapefixer.TheShapeFixerUtils;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        TheShapeFixer fixer = new TheShapeFixer();
        int[][] pointsArray = new int[][]{{0, 0}};

        System.out.println("1. Example of a simple square without internal intersections.");
        pointsArray = new int[][]{
                {0, 6}, {0, 0},
                {1, 1}, {7, 1},
                {0, 2}, {4, 2}, {4, 10}, {0, 10}, {0, 2},
                {4, 2}, {8, 2}, {8, 6}, {4, 6}, {4, 2} // Closing point
        };
        testing(fixer, pointsArray);

        System.out.println("2. Example of a simple square without internal intersections.");
        pointsArray = new int[][]{
                {0, 0},
                {10, 0},
                {10, 10},
                {0, 10},
                {0, 0} // Closing point
        };
        testing(fixer, pointsArray);

        System.out.println("3. Example of an Invalid figure (a square inside a square)");
        pointsArray = new int[][]{
                {0, 0},  // Outer square
                {10, 0},
                {10, 10},
                {0, 10},
                {0, 0},  // Closing the outer square
                {3, 3},  // Inner square
                {7, 3},
                {7, 7},
                {3, 7},
                {3, 3}   // Closing the inner square
        };
        testing(fixer, pointsArray);

        System.out.println("4. Example of an invalid figure (repeated dots)");
        pointsArray = new int[][]{
                {0, 0},
                {10, 0},
                {0, 0},  // Repeating point
                {10, 0},
                {10, 10},
                {0, 10},
                {0, 0}  // Closing point
        };
        testing(fixer, pointsArray);

        System.out.println("5. Example of an invalid figure (intersecting edges)");
        pointsArray = new int[][]{
                {0, 0},
                {10, 0},
                {0, 10},
                {10, 10},
                {0, 0} // Closing point
        };
        testing(fixer, pointsArray);

        System.out.println("6. Example of a valid figure (convex polygon)");
        pointsArray = new int[][]{
                {0, 0},
                {5, 0},
                {10, 5},
                {5, 10},
                {0, 5},
                {0, 0} // Closing point
        };
        testing(fixer, pointsArray);

    }

    private static void testing(TheShapeFixer fixer, int[][] pointsArray) {
        boolean isValid;
        List<Point2D> points;
        Shape2D shape;

        points = TheShapeFixerUtils.convertToPoints(pointsArray);
        shape = new Shape2D(points);
        isValid = fixer.isValid(shape);
        System.out.println("Is Shape Valid: " + isValid);
        if (!isValid) {
            //Shape2D newShape = fixer.repair(shape);
            Shape2D newShape = fixer.repair(shape);
            isValid = fixer.isValid(newShape);
            System.out.println("Is Shape Valid after repair: " + isValid);
        }
    }
}
