package com.company.theshapefixer;

import java.util.*;

public class TheShapeFixer {

    /**
     * Checking if a shape is valid
     *
     * @param shape
     * @return
     */
    public boolean isValid(Shape2D shape) {
        List<Point2D> points = shape.getPoints();
        int n = points.size();

        // Check if the shape is closed (first point coincides with the last)
        if (!points.get(0).equals(points.get(n - 1))) {
            return false;
        }

        // Check for uniqueness of all points (except the first and last)
        Set<Point2D> seenPoints = new HashSet<>();
        for (int i = 0; i < n - 1; i++) { // We don't check the last point
            if (!seenPoints.add(points.get(i))) {
                // If the point is already in the set, the shape is invalid
                return false;
            }
        }

        // Check for self-intersection of edges
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 2; j < n - 1; j++) {
                if (linesIntersect(points.get(i), points.get(i + 1), points.get(j), points.get(j + 1))) {
                    return false;
                }
            }
        }

        // Check for internal edges or connection of two shapes
        return !hasInternalEdges(points);
    }

    /** Method for checking line intersection
     *
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return
     */
    private boolean linesIntersect(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
        double d1 = direction(p3, p4, p1);
        double d2 = direction(p3, p4, p2);
        double d3 = direction(p1, p2, p3);
        double d4 = direction(p1, p2, p4);

        return (((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0))
                && ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0)));
    }

    /** Method for calculating the direction of a vector (determining which side a point is on)
     *
     * @param pi
     * @param pj
     * @param pk
     * @return
     */
    private double direction(Point2D pi, Point2D pj, Point2D pk) {
        return (pj.x() - pi.x()) * (pk.y() - pi.y()) - (pk.x() - pi.x()) * (pj.y() - pi.y());
    }

    /**
     * Check for internal edges or connected shapes
     *
     * @param points
     * @return
     */
    private boolean hasInternalEdges(List<Point2D> points) {
        int n = points.size();

        // Using DFS (Depth-First Search)
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n - 1; j++) {
                if (linesIntersect(points.get(i), points.get(i + 1), points.get(j), points.get(j + 1))) {
                    // If an internal edge is found that intersects an external contour
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Fix the shape by removing internal contours and points used once
     *
     * @param shape
     * @return
     */
    public Shape2D repair(Shape2D shape) {
        List<Point2D> points = shape.getPoints();

        // Count the number of uses of each point
        Map<Point2D, Integer> pointUsage = new HashMap<>();
        for (Point2D point : points) {
            pointUsage.put(point, pointUsage.getOrDefault(point, 0) + 1);
        }

        // Storing the outer contour
        List<Point2D> fixedPoints = new ArrayList<>();
        fixedPoints.add(points.get(0)); // Add the first point

        // Saving unique points and their connections
        Set<Point2D> seenPoints = new HashSet<>();
        seenPoints.add(points.get(0));

        // Saving unique points and their connections
        int n = points.size();
        for (int i = 1; i < n; i++) {
            Point2D current = points.get(i);

            // Remove points that are used only once, except for the first and last
            if (i != 0 && i != n - 1 && pointUsage.get(current) == 1) {
                continue; // Skip points that are used only once
            }

            // Check if the current edge intersects with any of the previous edges
            boolean hasIntersection = false;
            for (int j = 0; j < fixedPoints.size() - 1; j++) {
                if (linesIntersect(
                        fixedPoints.get(j), fixedPoints.get(j + 1), fixedPoints.get(fixedPoints.size() - 1), current)) {
                    hasIntersection = true;
                    break;
                }
            }

            // If there are no intersections and the point has not been used before, add the point
            if (!hasIntersection && seenPoints.add(current)) {
                fixedPoints.add(current);
            }
        }

        // Check if the shape is closed
        if (!fixedPoints.get(0).equals(fixedPoints.get(fixedPoints.size() - 1))) {
            fixedPoints.add(fixedPoints.get(0)); // Close the figure
        }

        return new Shape2D(fixedPoints);
    }

}

