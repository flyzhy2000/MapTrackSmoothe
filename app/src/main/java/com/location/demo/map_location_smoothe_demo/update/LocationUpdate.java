
package com.location.demo.map_location_smoothe_demo.update;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.location.demo.map_location_smoothe_demo.equation.EquationUtils;
import com.location.demo.map_location_smoothe_demo.equation.PolynomialEquation;
import com.location.demo.map_location_smoothe_demo.listener.CommonLocationListener;
import com.location.demo.map_location_smoothe_demo.utils.FileUtils;
import com.location.demo.map_location_smoothe_demo.utils.LocationUtils;
import com.location.demo.map_location_smoothe_demo.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 位置信息的获取与上传.
 */
public class LocationUpdate {
    public static final int GPS_MODE = 0;
    public static final int NETWORK_MODE = 1;
    public static final int AUTO_MODE = 2;
    private static final String TAG = "LocationUpdate";
    private static final long UPLOAD_POI_INTERVAL = 10 * 1000l;
    private static final long REQUEST_LOCATION_INTERVAL = 10 * 1000l;
    private static final int POI_INTERVAL = 1000;
    private Context mContext;
    private int mLocationMode = GPS_MODE;
    private long mUploadPoiInterval = UPLOAD_POI_INTERVAL;
    private long mScanSpan = POI_INTERVAL;
    private long mLastUploadPoiTime;
    private LocationManager mLocationManager;
    private Location mLocation;
    private Location mLastLocation;
    private CommonLocationListener mListener;
    private Runnable mRun;
    private Handler mHandler;
    private List<Location> mSampleList = new ArrayList<Location>();

    /**
     * 构造函数,用于需要上传位置到指定服务器.
     *
     * @param context 上下文
     * @param mode    定位类型，Gps类型或网络类型或自动类型
     */
    private LocationUpdate(Context context, int mode) {
        mContext = context;
        mLocationMode = mode;
        initLocationManager();
    }

    /**
     * 初始化LocationManager.
     */
    private void initLocationManager() {
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        /*
         * Criteria criteria = new Criteria();
         * criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
         * criteria.setBearingRequired(true); criteria.setSpeedRequired(true);
         * criteria.setCostAllowed(false); criteria.setAltitudeRequired(false);
         * criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗 String
         * provider = mLocationManager.getBestProvider(criteria, true);
         * Log.d(TAG, "the best provider: " + provider); //mLocation =
         * mLocationManager.getLastKnownLocation(provider); if (mLocationMode ==
         * GPS_MODE) {
         * mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
         * POI_INTERVAL, 0, GpsLocationListener); } else if (mLocationMode ==
         * NETWORK_MODE) {
         * mLocationManager.requestLocationUpdates(LocationManager
         * .NETWORK_PROVIDER, POI_INTERVAL, 0, NetworkLocationListener); }
         */

    }


    /**
     * 启动获取定位信息.
     */
    public void start() {
        requestLocationUpdates();
    }

    /**
     * 停止获取定位信息.
     */
    public void stop() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRun);
        }
        if (mLocationMode == GPS_MODE) {
            mLocationManager.removeUpdates(GpsLocationListener);
        } else if (mLocationMode == NETWORK_MODE) {
            mLocationManager.removeUpdates(NetworkLocationListener);
        } else if (mLocationMode == AUTO_MODE) {
            mLocationManager.removeUpdates(GpsLocationListener);
            mLocationManager.removeUpdates(NetworkLocationListener);
        }
        unRegisterCommonLocationListener();

    }


    public void registerCommonLocationListener(CommonLocationListener listener) {
        mListener = listener;
    }

    public void unRegisterCommonLocationListener() {
        mListener = null;
    }


    /**
     * 设置上传位置信息的间隔时间.
     *
     * @param interval 间隔时间
     */
    public void setUploadPoiInterval(long interval) {
        mUploadPoiInterval = interval;
    }

    /**
     * 设置定位时间间隔.
     *
     * @param interval 时间间隔
     */
    public void setScanSpan(long interval) {
        mScanSpan = interval;
    }

    /**
     * 请求位置信息更新.
     */
    private void requestLocationUpdates() {
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (mLocationMode == GPS_MODE) {
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                mScanSpan, 0, GpsLocationListener);
                    } else if (mLocationMode == NETWORK_MODE) {
                        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                mScanSpan, 0, NetworkLocationListener);
                    } else if (mLocationMode == AUTO_MODE) {
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                mScanSpan, 0, GpsLocationListener);
                        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                mScanSpan, 0, NetworkLocationListener);
                    }
                }
            };
        }

        if (mRun == null) {
            mRun = new Runnable() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(0);
                    mHandler.postDelayed(mRun, mScanSpan);
                }
            };
        }
        mHandler.post(mRun);
    }

    private boolean checkFittingInterval() {
        long now = System.currentTimeMillis();
        long div = now - mLastUploadPoiTime;
        if (div > mUploadPoiInterval) {
            mLastUploadPoiTime = now;
            return true;
        } else {
            return false;
        }
    }

    // 定位类型为Gps时的LocationListener.
    private LocationListener GpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mSampleList.add(location);

            if (checkFittingInterval()) {
                PolynomialEquation equation = EquationUtils.fittingLineToPoints(mSampleList);
                if (equation != null) {
                    double fittingLongitude = EquationUtils.getMeanLongitude(mSampleList);
                    double fittingLatitude = equation.a1 * fittingLongitude + equation.a0;

                    if (!Double.isNaN(fittingLatitude) && !Double.isNaN(fittingLongitude)) {
                        Location updateLocation = new Location(LocationManager.GPS_PROVIDER);
                        updateLocation.setLatitude(fittingLatitude);
                        updateLocation.setLongitude(fittingLongitude);
                        updateLocation.setTime(System.currentTimeMillis());
                        mSampleList.clear();   // 清空待拟合的位置

                        double distance = LocationUtils.distanceBetween(mLastLocation, updateLocation);
                        if (distance > 10 && distance < 500) {  // 排除不合理的点
                            mLocation = updateLocation;
                            mLastLocation = mLocation;
                            if (mListener != null) {
                                mListener.onReceiveCommonLocation(mLocation, location);
                            }
                        }

                        long time = System.currentTimeMillis();
                        String timedate = TimeUtils.formatLocationTime(time);
                        String data = "Gps---get origin location is: " + location.getLatitude() + ","
                                + location.getLongitude();
                        FileUtils.saveFile(timedate + "  " + data, "originlocation_demo");
                    }
                }

            }
            /*if (mSampleList.size() == 10) {
                PolynomialEquation equation =  EquationUtils.fittingLineToPoints(mSampleList);
                double fittingLongitude = EquationUtils.getMeanLongitude(mSampleList);
                double fittingLatitude = equation.a1 * fittingLongitude + equation.a0;
                if (!Double.isNaN(fittingLatitude) && !Double.isNaN(fittingLongitude)) {
                    Location updateLocation = new Location(LocationManager.GPS_PROVIDER);
                    updateLocation.setLatitude(fittingLatitude);
                    updateLocation.setLongitude(fittingLongitude);
                    updateLocation.setTime(System.currentTimeMillis());
                    mLocation = updateLocation;
                    mSampleList.clear();
                    long time = System.currentTimeMillis();
                    String timedate = TimeUtils.formatLocationTime(time);
                    String data = "Gps---get origin location is: " + location.getLatitude() + ","
                            + location.getLongitude();
                    FileUtils.saveFile(timedate + "  " + data, "originlocation");
                }
                if (mListener != null) {
                    mListener.onReceiveCommonLocation(mLocation, location);
                }
            } else if (mSampleList.size() < 10) {
                mSampleList.add(location);
            }*/

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            // 当适配器禁用时调用
        }

        @Override
        public void onProviderEnabled(String provider) {
            // 当适配器有效时调用
        }

    };

    // 定位类型为网络时的LocationListener.
    private LocationListener NetworkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mSampleList.add(location);
            if (checkFittingInterval()) {
                PolynomialEquation equation = EquationUtils.fittingLineToPoints(mSampleList);
                if (equation != null) {
                    double fittingLongitude = EquationUtils.getMeanLongitude(mSampleList);
                    double fittingLatitude = equation.a1 * fittingLongitude + equation.a0;

                    if (!Double.isNaN(fittingLatitude) && !Double.isNaN(fittingLongitude)) {
                        Location updateLocation = new Location(LocationManager.NETWORK_PROVIDER);
                        updateLocation.setLatitude(fittingLatitude);
                        updateLocation.setLongitude(fittingLongitude);
                        updateLocation.setTime(System.currentTimeMillis());
                        mSampleList.clear();   // 清空待拟合的位置

                        double distance = LocationUtils.distanceBetween(mLastLocation, updateLocation);
                        if (distance > 10 && distance < 500) {  // 排除不合理的点
                            mLocation = updateLocation;
                            mLastLocation = mLocation;
                            if (mListener != null) {
                                mListener.onReceiveCommonLocation(mLocation, location);
                            }
                        }

                        long time = System.currentTimeMillis();
                        String timedate = TimeUtils.formatLocationTime(time);
                        String data = "Network---get origin location is: " + location.getLatitude() + ","
                                + location.getLongitude();
                        FileUtils.saveFile(timedate + "  " + data, "originlocation_demo");
                    }
                }
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            // 当适配器禁用时调用
        }

        @Override
        public void onProviderEnabled(String provider) {
            // 当适配器有效时调用
        }

    };

    public static class Builder {
        private Context mContext;
        private int mMode = AUTO_MODE;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setMode(int mode) {
            mMode = mode;
            return this;
        }

        public LocationUpdate create() {
            final LocationUpdate locationUpdate = new LocationUpdate(mContext, mMode);

            return locationUpdate;

        }

    }

}
