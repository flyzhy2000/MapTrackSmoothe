package com.location.demo.map_location_smoothe_demo;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.Point;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private MapView mapView = null;
    Polyline polyline;
    LatLng point1, point2, point3, point4,point5,point6,point7,point8,point9;
    BaiduMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.bmapView);
        mMap = mapView.getMap();

        LatLng szjm = new LatLng(30.594723, 104.074576);

// 设定地图状态（设定初始中心点和缩放级数）

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


        List<LatLng> points = new ArrayList<LatLng>();
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
        mMap.addOverlay(ooPolyline);
    }


}
