package com.location.demo.map_location_smoothe_demo;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.location.demo.map_location_smoothe_demo.listener.CommonLocationListener;
import com.location.demo.map_location_smoothe_demo.update.LocationUpdate;
import com.location.demo.map_location_smoothe_demo.utils.FileUtils;
import com.location.demo.map_location_smoothe_demo.utils.LocationParser;
import com.location.demo.map_location_smoothe_demo.utils.TimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements CommonLocationListener {

    private MapView mapView = null;
    private Polyline mTrackPolyline, mNoLinePolyline;
    private Button mJiuPianBtn, mHistoryBtn;
    private BaiduMap mMap;
    private LocationUpdate mLocationUpdate;
    private List<LatLng> mTrackList = new ArrayList<>();
    private List<LatLng> mOriginTrackList = new ArrayList<>();
    private List<LatLng> mNoNiLineList = new ArrayList<>();
    private List<LatLng> mNoNiLineTrackList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.bmapView);
        mMap = mapView.getMap();
        mJiuPianBtn = (Button) findViewById(R.id.btn_jiupian);
        mHistoryBtn = (Button) findViewById(R.id.btn_origin);

        mLocationUpdate = new LocationUpdate.Builder(this).setMode(LocationUpdate.AUTO_MODE).create();
        mLocationUpdate.registerCommonLocationListener(this);


        // 设定地图状态（设定初始中心点和缩放级数）
        LatLng szjm = new LatLng(30.498112, 104.07966);
        MapStatus mMapStatus = new MapStatus.Builder().target(szjm).zoom(15).build();

        // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

        // 设置地图状态
        mMap.setMapStatus(mMapStatusUpdate);
        //mMap.addOverlay(new MarkerOptions().position(szjm).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
        //mMap.addOverlay(new TextOptions().position(szjm).bgColor(0XAAFFFF00).fontColor(0xFFFF00FF).fontSize(24).text("这里是天府大道！"));
        //createData();
        mJiuPianBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocationUpdate.start();
                /*LatLng L10 = new LatLng(30.493525925999997,104.07449416299998);
                LatLng L11 = new LatLng(30.493520190999998,104.074644867);
                LatLng L12 = new LatLng(30.493570219000002,104.074781229);
                mOriginTrackList.add(L10);mOriginTrackList.add(L11);mOriginTrackList.add(L12);*/
            }
        });

        mHistoryBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                if (mTrackPolyline != null) {
                    mTrackPolyline.remove();
                    mTrackPolyline = null;
                }
                if (mNoLinePolyline != null) {
                    mNoLinePolyline.remove();
                    mNoLinePolyline = null;
                }

                mTrackList.clear();
                mTrackList.addAll(mOriginTrackList);
                if (mTrackList.size() >= 2) {
                    mTrackList = LocationParser.transform2bdList(mTrackList);
                    /*LatLng center = mTrackList.get(0);
                    MapStatus mMapStatus = new MapStatus.Builder().target(center).zoom(15).build();
                    // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                    mMap.setMapStatus(mMapStatusUpdate);*/
                    OverlayOptions ooPolyline = new PolylineOptions().width(10)
                            .color(0xAAFF0000).points(mTrackList).visible(true);
                    mTrackPolyline = (Polyline) mMap.addOverlay(ooPolyline);
                }
                mNoNiLineTrackList.clear();
                mNoNiLineTrackList.addAll(mNoNiLineList);
                if (mNoNiLineTrackList.size() >= 2) {
                    mNoNiLineTrackList = LocationParser.transform2bdList(mNoNiLineTrackList);
                    /*LatLng center = mTrackList.get(0);
                    MapStatus mMapStatus = new MapStatus.Builder().target(center).zoom(15).build();
                    // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                    mMap.setMapStatus(mMapStatusUpdate);*/
                    OverlayOptions ooPolyline = new PolylineOptions().width(10)
                            .color(0xAAFF78FF).points(mNoNiLineTrackList).visible(true);
                    mNoLinePolyline = (Polyline) mMap.addOverlay(ooPolyline);
                }

            }
        });
    }

    private void createData() {
        LatLng L1 = new LatLng(30.49352167495377,104.07360493181025);
        LatLng L2 = new LatLng(30.49346112,104.07370635800001);
        LatLng L3 = new LatLng(30.493456818000006,104.073786082);
        LatLng L4 = new LatLng(30.493484645999995,104.07386442800001);
        LatLng L5 = new LatLng(30.493521377,104.073944108);
        LatLng L6 = new LatLng(30.493547019000005,104.074069058);
        LatLng L7 = new LatLng(30.493550698000007,104.074188287);
        LatLng L8 = new LatLng(30.493485104999998,104.074309038);
        LatLng L9 = new LatLng(30.49349949900001,104.074388731);
        LatLng L10 = new LatLng(30.493525925999997,104.07449416299998);
        LatLng L11 = new LatLng(30.493520190999998,104.074644867);
        LatLng L12 = new LatLng(30.493570219000002,104.074781229);
        mOriginTrackList.add(L1);mOriginTrackList.add(L2);mOriginTrackList.add(L3);mOriginTrackList.add(L4);
        mOriginTrackList.add(L5);mOriginTrackList.add(L6);mOriginTrackList.add(L7);mOriginTrackList.add(L8);
        mOriginTrackList.add(L9);//mOriginList.add(L10);mOriginList.add(L11);mOriginList.add(L12);

    }


    @Override
    public void onReceiveCommonLocation(Location sample_location, Location origin_location) {
        if (sample_location == null || origin_location == null) {
            return;
        }
        LatLng tempSampleLoc = new LatLng(sample_location.getLatitude(), sample_location.getLongitude());
        mOriginTrackList.add(tempSampleLoc);
        LatLng tempNoNiLineLoc = new LatLng(origin_location.getLatitude(), origin_location.getLongitude());
        mNoNiLineList.add(tempNoNiLineLoc);
        long time = System.currentTimeMillis();
        String timedate = TimeUtils.formatLocationTime(time);
        String data = "Gps---onReceiveCommonLocation -the location is: " + sample_location.getLatitude() + ","
                + sample_location.getLongitude();
        FileUtils.saveFile(timedate + "  " + data, "tracklocation_demo");
        String noLineData = "Gps---onReceiveCommonLocation -the location is: " + origin_location.getLatitude() + ","
                + origin_location.getLongitude();
        FileUtils.saveFile(timedate + "  " + noLineData, "nolinetracklocation_demo");
        mHistoryBtn.performClick();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationUpdate.stop();
    }

}
