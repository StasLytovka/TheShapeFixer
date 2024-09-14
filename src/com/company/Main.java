package com.company;

import com.company.theshapefixer.Point2D;
import com.company.theshapefixer.Shape2D;
import com.company.theshapefixer.TheShapeFixer;
import com.company.theshapefixer.TheShapeFixerUtils;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        TheShapeFixer fixer = new TheShapeFixer();

        System.out.println("простого квадрата без внутренних пересечений.");
        int[][] pointsArray = new int[][]{{0, 0}, {10, 0}, {10, 10}, {0, 10}, {0, 0}}; // Замыкающая точка
        texting(fixer, pointsArray);

        System.out.println("Пример Невалидной фигуры (квадрат внутри квадрата):");
        pointsArray = new int[][]{
                {0, 0},  // Внешний квадрат
                {10, 0},
                {10, 10},
                {0, 10},
                {0, 0},  // Замыкание внешнего квадрата
                {3, 3},  // Внутренний квадрат
                {7, 3},
                {7, 7},
                {3, 7},
                {3, 3}   // Замыкание внутреннего квадрата
        };
        texting(fixer, pointsArray);

        System.out.println("Пример невалидной фигуры (повторяющиеся точки):");
        pointsArray = new int[][]{
                {0, 0},
                {10, 0},
                {0, 0},  // Повторяющаяся точка
                {10, 0},
                {10, 10},
                {0, 10},
                {0, 0}  // Замыкающая точка
        };
        texting(fixer, pointsArray);

        System.out.println("Пример невалидной фигуры (пересекающиеся рёбра):");
        pointsArray = new int[][]{
                {0, 0},
                {10, 0},
                {0, 10},
                {10, 10},
                {0, 0} // Замыкающая точка
        };
        texting(fixer, pointsArray);

        System.out.println("Пример валидной фигуры (выпуклый многоугольник):");
        pointsArray = new int[][]{
                {0, 0},
                {5, 0},
                {10, 5},
                {5, 10},
                {0, 5},
                {0, 0} // Замыкающая точка
        };
        texting(fixer, pointsArray);

         /*
         pointsArray = new int[][]{{0, 0}, {10, 0},
                {0, 0}, {10, 0}, {10, 10}, {0, 10}, {0, 0},
                {10, 10}, {0, 10}, {0, 0}};
        texting(fixer, pointsArray);
        pointsArray = new int[][]{{0, 0}, {10, 0},
                {10, 10}, {0, 10}, {0, 0}};
        texting(fixer, pointsArray);
        */

       /* if (!isValid) {
            Shape2D repairedShape = fixer.repair(shape);
            isValid = fixer.isValid(repairedShape);
            System.out.println("Is Shape Valid: " + isValid);
            System.out.println("Shape has been repair.");
        }*/

    }

    private static void texting(TheShapeFixer fixer, int[][] pointsArray) {
        boolean isValid;
        List<Point2D> points;
        Shape2D shape;

        points = TheShapeFixerUtils.convertToPoints(pointsArray);
        shape = new Shape2D(points);
        isValid = fixer.isValid(shape);
        System.out.println("Is Shape Valid: " + isValid);
        if (!isValid) {
            //
        }
    }
}
