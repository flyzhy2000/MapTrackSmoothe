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
import com.location.demo.map_location_smoothe_demo.utils.LocationParser;
import com.location.demo.map_location_smoothe_demo.utils.TimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements CommonLocationListener {

    private MapView mapView = null;
    private Polyline polyline;
    private Button mJiuPianBtn, mHistoryBtn;
    private BaiduMap mMap;
    private LocationUpdate mLocationUpdate;
    private List<LatLng> mTrackList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.bmapView);
        mMap = mapView.getMap();
        mJiuPianBtn = (Button) findViewById(R.id.btn_jiupian);
        mHistoryBtn = (Button) findViewById(R.id.btn_origin);

        mLocationUpdate = new LocationUpdate.Builder(this).setMode(LocationUpdate.GPS_MODE).create();
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

        mJiuPianBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocationUpdate.start();
            }
        });

        mHistoryBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTrackList.size() >= 2) {
                    mTrackList = LocationParser.transform2bdList(mTrackList);
                    LatLng center = mTrackList.get(0);
                    MapStatus mMapStatus = new MapStatus.Builder().target(center).zoom(15).build();
                    // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                    mMap.setMapStatus(mMapStatusUpdate);
                    OverlayOptions ooPolyline = new PolylineOptions().width(10)
                            .color(0xAAFF0000).points(mTrackList).visible(true);
                    if (mMap == null) {
                        mMap = mapView.getMap();
                    }
                    if (polyline != null) {
                        polyline.remove();
                    }
                    polyline = (Polyline) mMap.addOverlay(ooPolyline);
                }

            }
        });
    }

    @Override
    public void onReceiveCommonLocation(Location location) {
        LatLng tempLoc = new LatLng(location.getLatitude(), location.getLongitude());
        mTrackList.add(tempLoc);
        long time = System.currentTimeMillis();
        String timedate = TimeUtils.formatLocationTime(time);
        String data = "Gps---onReceiveCommonLocation -the location is: " + location.getLatitude() + ","
                + location.getLongitude();
        saveFile(timedate + "  " + data, "tracklocation");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationUpdate.stop();
    }

    public static void saveFile(String str, String fileName) {
        String filePath = null;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + fileName +".txt";
        } else
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + fileName +".txt";

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file, true);
            outStream.write(str.getBytes());
            String enter = "\r\n";
            outStream.write(enter.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
