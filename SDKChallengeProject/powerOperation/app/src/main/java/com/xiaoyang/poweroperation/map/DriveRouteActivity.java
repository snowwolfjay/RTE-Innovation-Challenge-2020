package com.xiaoyang.poweroperation.map;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.xiaoyang.overlay.DrivingRouteOverlay;
import com.xiaoyang.poweroperation.R;
import com.xiaoyang.poweroperation.app.utils.AMapUtil;
import com.xiaoyang.poweroperation.app.utils.LocationUtils;
import com.xiaoyang.poweroperation.app.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 驾车出行路线规划 实现
 */
public class DriveRouteActivity extends Activity implements OnMapClickListener,
        OnMarkerClickListener, OnInfoWindowClickListener, InfoWindowAdapter, OnRouteSearchListener {
    @BindView(R.id.toolbar_back)
    RelativeLayout toolbarBack;
    @BindView(R.id.tv_toolbar_title_tv)
    TextView tvToolbarTitleTv;
    @BindView(R.id.img_right)
    ImageView imgRight;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.common_toolbar)
    Toolbar commonToolbar;
    @BindView(R.id.route_drive)
    ImageView routeDrive;
    @BindView(R.id.route_bus)
    ImageView routeBus;
    @BindView(R.id.route_walk)
    ImageView routeWalk;
    @BindView(R.id.route_CrosstownBus)
    TextView routeCrosstownBus;
    @BindView(R.id.routemap_choose)
    LinearLayout routemapChoose;
    @BindView(R.id.routemap_header)
    RelativeLayout mHeadLayout;
    @BindView(R.id.firstline)
    TextView mRotueTimeDes;
    @BindView(R.id.secondline)
    TextView mRouteDetailDes;
    @BindView(R.id.detail)
    LinearLayout detail;
    @BindView(R.id.tv_navigate)
    TextView tv_navigate;
    @BindView(R.id.bottom_layout)
    RelativeLayout mBottomLayout;
    @BindView(R.id.route_map)
    MapView mapView;
    @BindView(R.id.bus_result_list)
    ListView busResultList;
    @BindView(R.id.bus_result)
    LinearLayout busResult;
    private AMap aMap;
    private Context mContext;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private LatLonPoint mStartPoint;//起点，39.942295,116.335891
    private LatLonPoint mEndPoint;//终点，39.995576,116.481288
    private final int ROUTE_TYPE_DRIVE = 2;

    private ProgressDialog progDialog = null;// 搜索时进度条
    private String latitude;
    private String longitude;
    private static final int REQUEST_PERMISSION_LOCATION = 0;
    private String startLat;
    private String startLon;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_device_route);
        ButterKnife.bind(this);
        mContext = this.getApplicationContext();
        mapView = (MapView) findViewById(R.id.route_map);
        mapView.onCreate(bundle);// 此方法必须重写
        tvToolbarTitleTv.setText("路径规划");
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        mEndPoint = new LatLonPoint(Double.valueOf(latitude), Double.valueOf(longitude));
        //设置定位权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            }
        }
        init();
        getLocation();
        searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
    }

    /**
     * 获取定位坐标
     */
    private void getLocation() {
        Log.v("yxy", "定位操作");
        LocationUtils.getLocation(location -> {
            if (location.getErrorCode() == 0) {
                Log.v("yxy", "定位操作");
                double latitude = location.getLatitude();//获取纬度
                double longitude = location.getLongitude();//获取经度
                Log.v("yxy", "latitude" + latitude + "===" + location.getCity());
                startLat = String.valueOf(latitude);
                startLon = String.valueOf(longitude);
                mStartPoint = new LatLonPoint(latitude, longitude);
                setfromandtoMarker();
            } else {
                Log.v("yxy", "定位失败" + location.getErrorInfo());
                Log.e("AmapError", "location Error, ErrCode:"
                        + location.getErrorCode() + ", errInfo:"
                        + location.getErrorInfo());
            }

        });
    }

    private void setfromandtoMarker() {
        if (mStartPoint == null || mEndPoint == null) {
            return;
        }
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mStartPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mEndPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        registerListener();
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mHeadLayout = (RelativeLayout) findViewById(R.id.routemap_header);
        mRotueTimeDes = (TextView) findViewById(R.id.firstline);
        mRouteDetailDes = (TextView) findViewById(R.id.secondline);
        tv_navigate = findViewById(R.id.tv_navigate);
        mHeadLayout.setVisibility(View.GONE);
        tv_navigate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AMapUtil.route(DriveRouteActivity.this, startLat, startLon, latitude, longitude);
            }
        });
    }

    /**
     * 注册监听
     */
    private void registerListener() {
        aMap.setOnMapClickListener(DriveRouteActivity.this);
        aMap.setOnMarkerClickListener(DriveRouteActivity.this);
        aMap.setOnInfoWindowClickListener(DriveRouteActivity.this);
        aMap.setInfoWindowAdapter(DriveRouteActivity.this);

    }

    @Override
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onMapClick(LatLng arg0) {
        // TODO Auto-generated method stub

    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            ToastUtil.show(mContext, "定位中，稍后再试...");
            return;
        }
        if (mEndPoint == null) {
            ToastUtil.show(mContext, "终点未设置");
        }
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            DriveRouteQuery query = new DriveRouteQuery(fromAndTo, mode, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    if (drivePath == null) {
                        return;
                    }
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            mContext, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                    mRotueTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.VISIBLE);
                    int taxiCost = (int) mDriveRouteResult.getTaxiCost();
                    mRouteDetailDes.setText("打车约" + taxiCost + "元");
                    mBottomLayout.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Intent intent = new Intent(mContext,
//                                    DriveRouteDetailActivity.class);
//                            intent.putExtra("drive_path", drivePath);
//                            intent.putExtra("drive_result",
//                                    mDriveRouteResult);
//                            startActivity(intent);
                        }
                    });

                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }

            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }


    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {

    }


    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null) {
            progDialog = new ProgressDialog(this);
        }
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onRideRouteSearched(RideRouteResult arg0, int arg1) {
        // TODO Auto-generated method stub

    }

}

