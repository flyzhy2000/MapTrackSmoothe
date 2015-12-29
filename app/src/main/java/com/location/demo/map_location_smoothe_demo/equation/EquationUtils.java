package com.location.demo.map_location_smoothe_demo.equation;

import android.location.Location;

import java.util.List;

/**
 * Created by Huoyunren on 2015/12/27.
 */
public class EquationUtils {

    public static PolynomialEquation fittingLineToPoints(List<Location> mapPoints) {
        PolynomialEquation lineEquation = new PolynomialEquation();
        if (mapPoints == null || mapPoints.size() < 1) {
            return null;
        }
        double sumX = 0;
        double sumY = 0;
        double sumX2 = 0;
        double sumXY = 0;
        for (Location point : mapPoints) {
            sumX += point.getLongitude();
            sumY += point.getLatitude();
            sumX2 += point.getLongitude() * point.getLongitude();
            sumXY += point.getLongitude() * point.getLatitude();
        }
        double meanX = sumX / mapPoints.size();
        double meanY = sumY / mapPoints.size();
        lineEquation.a1 = (sumXY - sumX * meanY) / (sumX2 - sumX * meanX);
        lineEquation.a0 = meanY - lineEquation.a1 * meanX;

        return lineEquation;
    }

    public static double getMeanLongitude(List<Location> mapPoints) {
        if (mapPoints == null || mapPoints.size() < 1) {
            return Double.NaN;
        }
        double sumLongitude = 0;
        for (Location point : mapPoints) {
            sumLongitude += point.getLongitude();
        }

        return sumLongitude / mapPoints.size();
    }
}
