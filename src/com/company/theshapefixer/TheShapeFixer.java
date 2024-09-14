package com.company.theshapefixer;

import java.util.*;

public class TheShapeFixer {

    // Проверка допустимости фигуры
    public boolean isValid(Shape2D shape) {
        List<Point2D> points = shape.getPoints();
        int n = points.size();

        // Проверка, что фигура замкнута (первая точка совпадает с последней)
        if (!points.get(0).equals(points.get(n - 1))) {
            return false;
        }

        // Проверка на уникальность всех точек (кроме первой и последней)
        Set<Point2D> seenPoints = new HashSet<>();
        for (int i = 0; i < n - 1; i++) { // Последнюю точку не проверяем
            if (!seenPoints.add(points.get(i))) {
                // Если точка уже есть в множестве, фигура недопустима
                return false;
            }
        }

        // Проверка на самопересечения рёбер
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 2; j < n - 1; j++) {
                if (linesIntersect(points.get(i), points.get(i + 1), points.get(j), points.get(j + 1))) {
                    return false;
                }
            }
        }

        // Проверка на наличие внутренних рёбер или соединение двух фигур
        return !hasInternalEdges(points);
    }

    // Метод для проверки пересечения линий
    private boolean linesIntersect(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
        double d1 = direction(p3, p4, p1);
        double d2 = direction(p3, p4, p2);
        double d3 = direction(p1, p2, p3);
        double d4 = direction(p1, p2, p4);

        return (((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0))
                && ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0)));
    }

    // Метод для расчета направления вектора (определение с какой стороны находится точка)
    private double direction(Point2D pi, Point2D pj, Point2D pk) {
        return (pj.x() - pi.x()) * (pk.y() - pi.y()) - (pk.x() - pi.x()) * (pj.y() - pi.y());
    }

    // Проверка на наличие внутренних рёбер или соединённых фигур
    private boolean hasInternalEdges(List<Point2D> points) {
        int n = points.size();

        // Используем DFS или другой способ для проверки связности
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n - 1; j++) {
                if (linesIntersect(points.get(i), points.get(i + 1), points.get(j), points.get(j + 1))) {
                    // Если обнаружено внутреннее ребро, которое пересекает внешний контур
                    return true;
                }
            }
        }

        return false;
    }

    // Исправление фигуры, удаляя внутренние контуры и точки, используемые один раз
    public Shape2D repair(Shape2D shape) {
        List<Point2D> points = shape.getPoints();
        int n = points.size();

        // Шаг 1: Собираем количество использований каждой точки
        Map<Point2D, Integer> pointUsage = new HashMap<>();
        for (Point2D point : points) {
            pointUsage.put(point, pointUsage.getOrDefault(point, 0) + 1);
        }

        // Хранение внешнего контура
        List<Point2D> fixedPoints = new ArrayList<>();
        fixedPoints.add(points.get(0)); // Добавляем первую точку

        // Сохранение уникальных точек и их связей
        Set<Point2D> seenPoints = new HashSet<>();
        seenPoints.add(points.get(0));

        // Проверяем пересечения рёбер и удаляем внутренние контуры
        for (int i = 1; i < n; i++) {
            Point2D current = points.get(i);

            // Удаляем точки, которые используются только один раз, кроме первой и последней
            if (i != 0 && i != n - 1 && pointUsage.get(current) == 1) {
                continue; // Пропускаем точки, которые используются только один раз
            }

            // Проверяем, пересекается ли текущее ребро с каким-либо из предыдущих рёбер
            boolean hasIntersection = false;
            for (int j = 0; j < fixedPoints.size() - 1; j++) {
                if (linesIntersect(fixedPoints.get(j), fixedPoints.get(j + 1), fixedPoints.get(fixedPoints.size() - 1), current)) {
                    hasIntersection = true;
                    break;
                }
            }

            // Если пересечений нет и точка не использовалась ранее, добавляем точку
            if (!hasIntersection && seenPoints.add(current)) {
                fixedPoints.add(current);
            }
        }

        // Проверка, замкнута ли фигура
        if (!fixedPoints.get(0).equals(fixedPoints.get(fixedPoints.size() - 1))) {
            fixedPoints.add(fixedPoints.get(0)); // Замыкаем фигуру
        }

        return new Shape2D(fixedPoints); // Возвращаем исправленную фигуру
    }

}

