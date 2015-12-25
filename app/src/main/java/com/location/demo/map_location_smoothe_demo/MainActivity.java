package com.location.demo.map_location_smoothe_demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private MapView mapView = null;
    Polyline polyline;
    Button mJiuPianBtn, mHistoryBtn;
    LatLng point1, point2, point3, point4, point5, point6, point7, point8;
    BaiduMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.bmapView);
        mMap = mapView.getMap();
        mJiuPianBtn = (Button) findViewById(R.id.btn_jiupian);
        mHistoryBtn = (Button) findViewById(R.id.btn_origin);


// 设定地图状态（设定初始中心点和缩放级数）
        LatLng szjm = new LatLng(30.594723, 104.074576);
        MapStatus mMapStatus = new MapStatus.Builder().target(szjm).zoom(15).build();

        // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化

        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

// 设置地图状态

        mMap.setMapStatus(mMapStatusUpdate);
        //mMap.addOverlay(new MarkerOptions().position(szjm).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
        //mMap.addOverlay(new TextOptions().position(szjm).bgColor(0XAAFFFF00).fontColor(0xFFFF00FF).fontSize(24).text("这里是天府大道！"));


        point1 = new LatLng(30.594723, 104.074576);
        point2 = new LatLng(30.593572, 104.074512);
        point3 = new LatLng(30.591987, 104.074666);
        point4 = new LatLng(30.589811, 104.076083);
        point5 = new LatLng(30.587215, 104.074809);
        point6 = new LatLng(30.584992, 104.074745);
        point7 = new LatLng(30.583562, 104.074945);
        point8 = new LatLng(30.581774, 104.074935);


        final List<LatLng> points = new ArrayList<LatLng>();
        points.add(point1);
        points.add(point2);
        points.add(point3);
        points.add(point4);
        points.add(point5);
        points.add(point6);
        points.add(point7);
        points.add(point8);
        OverlayOptions ooPolyline = new PolylineOptions().width(10)
                .color(0xAAFF0000).points(points).visible(true);
        polyline = (Polyline) mMap.addOverlay(ooPolyline);

        PolynomialEquation equation = fittingLineToPoints(points);
        double fittingLongitude = getMeanLongitude(points);
        double fittingLatitude = equation.a1 * fittingLongitude + equation.a0;
        LatLng correctPoint = new LatLng(fittingLatitude, fittingLongitude);
        mMap.addOverlay(new MarkerOptions().position(correctPoint).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));


        mJiuPianBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng point_modify = new LatLng(30.589811, 104.075083);
                List<LatLng> newPoints = new ArrayList<LatLng>();
                newPoints = polyline.getPoints();
                newPoints.set(3, point_modify);
                polyline.remove();
                OverlayOptions ooPolyline = new PolylineOptions().width(10)
                        .color(0xAAFF0000).points(newPoints).visible(true);
                polyline = (Polyline) mMap.addOverlay(ooPolyline);

            }
        });

        mHistoryBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng point_modify = new LatLng(30.589811, 104.076083);
                List<LatLng> newPoints = new ArrayList<LatLng>();
                newPoints = polyline.getPoints();
                newPoints.set(3, point_modify);
                polyline.remove();
                OverlayOptions ooPolyline = new PolylineOptions().width(10)
                        .color(0xAAFF0000).points(newPoints).visible(true);
                polyline = (Polyline) mMap.addOverlay(ooPolyline);

            }
        });
    }


    class PolynomialEquation {
        double a0;
        double a1;
    }

    private PolynomialEquation fittingLineToPoints (List<LatLng> mapPoints) {
        PolynomialEquation lineEquation = new PolynomialEquation();

        double sumX = 0;
        double sumY = 0;
        double sumX2 = 0;
        double sumXY = 0;
        for (LatLng point : mapPoints) {
            sumX += point.longitude;
            sumY += point.latitude;
            sumX2 += point.longitude*point.longitude;
            sumXY += point.longitude*point.latitude;
        }
        double meanX = sumX/mapPoints.size();
        double meanY = sumY/mapPoints.size();
        lineEquation.a1 = (sumXY - sumX * meanY) / (sumX2 - sumX * meanX);
        lineEquation.a0 = meanY - lineEquation.a1 * meanX;

        return lineEquation;
    }

    private double getMeanLongitude (List<LatLng> mapPoints) {
        double sumLongitude = 0;
        for (LatLng point : mapPoints) {
            sumLongitude += point.longitude;
        }

        return sumLongitude/mapPoints.size();
    }
}
