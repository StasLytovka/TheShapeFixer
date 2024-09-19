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
        if (n == 0 || !points.get(0).equals(points.get(n - 1))) {
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
        if (hasInternalEdges(points)) {
            return false;
        }

        if (SegmentPassingThroughPoint.hasSegmentPassingThroughPoint(points)) {
            return false;
        }

        return true;
    }


    /**
     * Method for checking line intersection
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

    /**
     * Method for calculating the direction of a vector (determining which side a point is on)
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
     * // Method for finding and storing all closed contours
     *
     * @param shape
     * @return
     */
    public Shape2D repair(Shape2D shape) {
        List<Shape2D> closedContours = findOpenContours(shape);

        for (int i = 0; i < closedContours.size(); i++) {
            Shape2D contour = closedContours.get(i);
            List<Point2D> points = contour.getPoints();
            if (SegmentPassingThroughPoint.hasSegmentPassingThroughPoint(points)) {
                List<Point2D> repairPoints = SegmentPassingThroughPoint.repairShape(points);
                closedContours.set(i, new Shape2D(repairPoints));
            }
        }

        Shape2D mergeCommonLines;
        if (closedContours.size() > 1) {
            mergeCommonLines = mergeShapesWithCommonLine(closedContours.get(1), closedContours.get(0));
            return mergeCommonLines;
        }

        // Join all closed contours into one list
        List<Point2D> result = new ArrayList<>();
        for (Shape2D contour : closedContours) {
            result.addAll(contour.getPoints());
        }

        return new Shape2D(result);

    }

    /**
     * Method to find contours with nested contour handling using recursion
     *
     * @param shape
     * @return
     */
    public List<Shape2D> findOpenContours(Shape2D shape) {
        List<Point2D> points = shape.getPoints();  // Get the points from the shape
        List<Point2D> startAndEndPoints = findStartAndEndPoints(shape);  // Find duplicate points
        Set<Point2D> startAndEndPointSet = new HashSet<>(startAndEndPoints);  // Use a set for quick lookups
        List<Shape2D> contours = new ArrayList<>();
        boolean[] visited = new boolean[points.size()];  // Track visited points to avoid reprocessing

        // Start processing the main outer contour
        processContour(points, contours, startAndEndPointSet, visited, 0, null);

        return contours;  // Return all found contours
    }

    /**
     * Recursive method to process contours
     *
     * @param points
     * @param contours
     * @param startAndEndPointSet
     * @param visited
     * @param startIndex
     * @param openingPoint
     * @return
     */
    private int processContour(List<Point2D> points, List<Shape2D> contours, Set<Point2D> startAndEndPointSet,
                               boolean[] visited, int startIndex, Point2D openingPoint) {
        List<Point2D> currentContour = new ArrayList<>();

        for (int i = startIndex; i < points.size(); i++) {
            Point2D point = points.get(i);

            // Skip if already visited
            if (visited[i]) {
                continue;
            }

            // Check if the point is a duplicate and opens/closes a contour
            if (startAndEndPointSet.contains(point)) {
                if (openingPoint == null) {
                    // This is the opening point of a new contour, so we add it and continue
                    openingPoint = point;
                    currentContour.add(point);  // Add the opening point
                    visited[i] = true;
                } else if (point.equals(openingPoint)) {
                    // We've closed the current contour
                    currentContour.add(point);  // Add the closing point
                    contours.add(new Shape2D(currentContour));
                    return i;  // Return the index where the contour closed
                } else {
                    // This is a nested contour, first add the point to the current contour
                    currentContour.add(point);
                    visited[i] = true;

                    // Find the starting index of the nested contour
                    int nestedContourStartIndex = findPointIndex(points, point, i + 1);

                    // Process the nested contour recursively
                    int endIndex = processContour(
                            points, contours, startAndEndPointSet, visited, nestedContourStartIndex, null);
                    //i = endIndex;  // Skip the points already processed in the nested contour
                }
            } else if (openingPoint != null) {
                // Only add points to the contour once the contour is opened
                currentContour.add(point);
                visited[i] = true;
            }
        }

        // If we reach the end of the points without closing the contour
        if (openingPoint != null) {
            currentContour.add(openingPoint);  // Close the contour manually
            contours.add(new Shape2D(currentContour));
        }

        return points.size();  // Return the final index if we processed all points
    }

    /**
     * Method to find the index of the next occurrence of a point
     *
     * @param points
     * @param targetPoint
     * @param startIndex
     * @return
     */
    private int findPointIndex(List<Point2D> points, Point2D targetPoint, int startIndex) {
        for (int i = startIndex; i < points.size(); i++) {
            if (points.get(i).equals(targetPoint)) {
                return i;  // Return the index of the next occurrence of the target point
            }
        }
        return points.size();  // If not found, return the end of the list
    }

    /**
     * Existing method to find duplicate points
     *
     * @param shape
     * @return
     */
    public List<Point2D> findStartAndEndPoints(Shape2D shape) {
        List<Point2D> points = shape.getPoints();
        Map<Point2D, Integer> pointCount = new HashMap<>();
        List<Point2D> duplicates = new ArrayList<>();

        // Count occurrences of each point
        for (Point2D point : points) {
            pointCount.put(point, pointCount.getOrDefault(point, 0) + 1);
        }

        // Find points that occur more than once
        for (Map.Entry<Point2D, Integer> entry : pointCount.entrySet()) {
            if (entry.getValue() > 1) {
                duplicates.add(entry.getKey());
            }
        }

        return duplicates;
    }


    private List<Shape2D> getShape2DS(Shape2D shape) {
        List<Point2D> points = shape.getPoints();
        List<Shape2D> closedContours = new ArrayList<>(); // To store all closed contours
        List<Point2D> currentContour = new ArrayList<>(); // To store the current contour

        for (Point2D current : points) {

            // Add the current point to the current contour
            currentContour.add(current);

            if (currentContour.size() > 2) {
                Point2D before = currentContour.get(currentContour.size() - 2);

                if (before.x() == current.x() || before.y() == current.y()) {
                    Point2D first = currentContour.get(0);

                    if (first.x() == current.x() && first.y() == current.y()) {
                        // Add a closed contours
                        closedContours.add(new Shape2D(currentContour));
                        currentContour.clear(); // Clear the current contour to look for new contours
                    }

                } else {
                    currentContour.clear();
                    currentContour.add(current);
                }
            }
        }

        // If there is an unclosed contour, we do not save it
        if (!currentContour.isEmpty()) {
            currentContour.clear();
        }
        return closedContours;
    }

    /**
     * Method to detect matching lines and merge shapes
     *
     * @param shape1
     * @param shape2
     * @return
     */
    public Shape2D mergeShapesWithCommonLine(Shape2D shape1, Shape2D shape2) {
        List<Point2D> points1 = shape1.getPoints();
        List<Point2D> points2 = shape2.getPoints();

        // Finding a matching line
        List<Point2D> commonLine = findCommonLine(points1, points2);

        // Find a matching vertical line
        if (commonLine != null) {
            List<Point2D> newPoints = new ArrayList<>();

            //Go through the first figure to the point before the matching line
            int i = 0;
            while (i < points1.size() && !points1.get(i).equals(commonLine.get(0))) {
                newPoints.add(points1.get(i));
                i++;
            }

            // go through the points of the second figure until we find a point that falls on the line
            int j = 0;
            while (j < points2.size()) {
                Point2D currentPoint = points2.get(j);
                if (!isPointOnLine(currentPoint, commonLine.get(0), commonLine.get(1))) {
                    newPoints.add(currentPoint);
                } else {
                    newPoints.add(currentPoint);
                    // If the point falls on the line and is not the beginning of the line, we complete the cycle
                    if (!currentPoint.equals(commonLine.get(0))) {
                        break;
                    }
                }
                j++;
            }

            // As soon as the point of the second figure hits the line, add the combined line
            newPoints.add(commonLine.get(1));

            // Continue adding the remaining points of the first figure after the intersection
            i += 2;
            while (i < points1.size()) {
                newPoints.add(points1.get(i));
                i++;
            }

            return new Shape2D(newPoints);
        }

        // If the common line is not found, return the first figure
        return shape1;
    }

    /**
     * Method to find a matching vertical line between two shapes
     *
     * @param points1
     * @param points2
     * @return
     */
    private List<Point2D> findCommonLine(List<Point2D> points1, List<Point2D> points2) {
        for (int i = 0; i < points1.size() - 1; i++) {
            Point2D p1 = points1.get(i);
            Point2D p2 = points1.get(i + 1);

            if (p1.x() == p2.x()) { // Vertical line
                for (int j = 0; j < points2.size() - 1; j++) {
                    Point2D q1 = points2.get(j);
                    Point2D q2 = points2.get(j + 1);

                    if (q1.x() == q2.x() && p1.x() == q1.x()) { // Vertical line on the same axis
                        if (linesOverlap(p1, p2, q1, q2)) {
                            return List.of(
                                    new Point2D(p1.x(), Math.min(p1.y(), q1.y())),
                                    new Point2D(p1.x(), Math.max(p2.y(), q2.y()))
                            );
                        }
                    }
                }
            }
        }
        return null; // Common line not found
    }


    /**
     * Check if a point is on a vertical line between two other points
     *
     * @param point
     * @param lineStart
     * @param lineEnd
     * @return
     */
    private boolean isPointOnLine(Point2D point, Point2D lineStart, Point2D lineEnd) {
        return point.x() == lineStart.x() &&
                point.y() >= Math.min(lineStart.y(), lineEnd.y()) &&
                point.y() <= Math.max(lineStart.y(), lineEnd.y());
    }

    /**
     * Check if two vertical segments intersect
     *
     * @param p1
     * @param p2
     * @param q1
     * @param q2
     * @return
     */
    private boolean linesOverlap(Point2D p1, Point2D p2, Point2D q1, Point2D q2) {
        return Math.max(p1.y(), p2.y()) >= Math.min(q1.y(), q2.y()) &&
                Math.max(q1.y(), q2.y()) >= Math.min(p1.y(), p2.y());
    }

}

