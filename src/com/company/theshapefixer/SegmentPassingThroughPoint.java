package com.company.theshapefixer;

import java.util.ArrayList;
import java.util.List;

public class SegmentPassingThroughPoint {

    private SegmentPassingThroughPoint() {
    }

    /**
     * Method to check if any segment passes through a point it shouldn't
     *
     * @param points
     * @return
     */
    public static boolean hasSegmentPassingThroughPoint(List<Point2D> points) {
        int n = points.size();

        // Loop through all segments formed by consecutive points
        for (int i = 0; i < n - 1; i++) {
            Point2D p1 = points.get(i);
            Point2D p2 = points.get(i + 1);

            // Check if the segment intersects any other point
            for (int j = 0; j < n; j++) {
                Point2D pointToCheck = points.get(j);

                // Skip the check if the point is one of the endpoints of the segment
                if (pointToCheck.equals(p1) || pointToCheck.equals(p2)) {
                    continue;
                }

                // Check if pointToCheck lies on the segment p1-p2
                if (isPointOnLineSegment(p1, p2, pointToCheck)) {
                    return true;  // Invalid intersection found
                }
            }
        }

        return false;  // No invalid intersections found
    }

    /**
     * Method to check if a point lies on a line segment between two other points
     *
     * @param p1
     * @param p2
     * @param point
     * @return
     */
    private static boolean isPointOnLineSegment(Point2D p1, Point2D p2, Point2D point) {
        // Check if the point is collinear with the line formed by p1 and p2 using the cross product
        double crossProduct = (point.y() - p1.y()) * (p2.x() - p1.x()) - (point.x() - p1.x()) * (p2.y() - p1.y());

        if (Math.abs(crossProduct) > 1e-7) {
            return false;  // Point is not on the line
        }

        // Check that the point lies between p1 and p2
        double dotProduct = (point.x() - p1.x()) * (p2.x() - p1.x()) + (point.y() - p1.y()) * (p2.y() - p1.y());
        if (dotProduct < 0) {
            return false;  // Point outside the segment
        }

        double squaredLength = (p2.x() - p1.x()) * (p2.x() - p1.x()) + (p2.y() - p1.y()) * (p2.y() - p1.y());
        if (dotProduct > squaredLength) {
            return false;  // Point outside the segment
        }

        return true;  // The point lies on the segment
    }

    /**
     * Method to repair the shape by removing invalid points that pass through a segment
     *
     * @param points
     * @return
     */
    public static List<Point2D> repairShape(List<Point2D> points) {
        List<Point2D> repairedPoints = new ArrayList<>(points);

        // Loop through all segments formed by consecutive points
        for (int i = 0; i < points.size() - 1; i++) {
            Point2D p1 = points.get(i);
            Point2D p2 = points.get(i + 1);

            // Loop through all points to check for invalid intersections
            for (int j = 0; j < points.size(); j++) {
                Point2D pointToCheck = points.get(j);

                // Skip if it's one of the endpoints of the segment
                if (pointToCheck.equals(p1) || pointToCheck.equals(p2)) {
                    continue;
                }

                // If the point is strictly between p1 and p2 on the segment, remove it
                if (isStrictlyBetween(p1, p2, pointToCheck)) {
                    repairedPoints.remove(pointToCheck);  // Remove invalid point
                    return repairedPoints;  // Return repaired shape immediately after fixing
                }
            }
        }

        return repairedPoints;  // Return the repaired shape if no invalid point was found
    }

    /**
     * Helper method to check if a point lies strictly between two other points on a line segment
     *
     * @param p1
     * @param p2
     * @param point
     * @return
     */
    private static boolean isStrictlyBetween(Point2D p1, Point2D p2, Point2D point) {
        // Check if the point is collinear with the line formed by p1 and p2 using the cross-product
        double crossProduct = (point.y() - p1.y()) * (p2.x() - p1.x()) - (point.x() - p1.x()) * (p2.y() - p1.y());

        if (Math.abs(crossProduct) > 1e-7) {
            return false;  // The point is not on the line
        }

        // Check if the point is strictly between p1 and p2 (not equal to either endpoint)
        if (point.equals(p1) || point.equals(p2)) {
            return false;  // The point is an endpoint, not between
        }

        // Ensure that the point lies within the bounds of the segment
        double dotProduct = (point.x() - p1.x()) * (p2.x() - p1.x()) + (point.y() - p1.y()) * (p2.y() - p1.y());
        if (dotProduct < 0 || dotProduct > (p2.x() - p1.x()) * (p2.x() - p1.x()) + (p2.y() - p1.y()) * (p2.y() - p1.y())) {
            return false;  // The point is outside the segment
        }

        return true;  // The point lies strictly between p1 and p2
    }

}
