package com.zzqs.app_kc.z_kc.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.net.Connectivities;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.DialogView;
import com.zzqs.app_kc.z_kc.entitiy.Tender;
import com.zzqs.app_kc.z_kc.listener.MyOnClickListener;
import com.zzqs.app_kc.z_kc.tool.LocationTool;
import com.zzqs.app_kc.z_kc.util.overlayutil.DrivingRouteOverlay;
import com.zzqs.app_kc.z_kc.util.overlayutil.OverlayManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ray on 2017/1/14.
 * Class name : MapViewActivity
 * Description :
 */
public class MapViewActivity extends BaseActivity implements OnGetRoutePlanResultListener {
    private TextView tvLeft, tvTitle, tvDistance;
    private MapView mapView;
    private BaiduMap baiduMap;
    private OverlayManager overlayManager;
    private Tender tender;
    private RoutePlanSearch routePlanSearch;
    private LocationTool locationTool;

    List<OverlayOptions> overlayOptions;
    List<LatLng> overlayPoints;
    LatLng pickUpLatLng, deliveryLatLng, currentLatLng;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LocationTool.LOCATION_CHANGED:
                    currentLatLng = locationTool.getCurrentLatLng();
                    deliveryLatLng = new LatLng(tender.getDelivery_region_location().get(1), tender.getDelivery_region_location().get(0));
                    startSearch(currentLatLng, deliveryLatLng);
                    locationTool.locationStop();
                    break;
            }
        }
    };

    @Override
    public void initVariables() {
        overlayOptions = new ArrayList<>();
        overlayPoints = new ArrayList<>();
        tender = getIntent().getParcelableExtra("tender");
        routePlanSearch = RoutePlanSearch.newInstance();
        routePlanSearch.setOnGetRoutePlanResultListener(this);
        locationTool = new LocationTool(this);
        locationTool.enableLocationListener(handler);
        locationTool.locationStart();
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.z_kc_act_map_view);
        tvLeft = (TextView) findViewById(R.id.head_back);
        tvLeft.setOnClickListener(new MyOnClickListener() {
            @Override
            public void OnceOnClick(View view) {
                finish();
            }
        });
        tvTitle = (TextView) findViewById(R.id.head_title);
        tvTitle.setText("车辆地图");
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        if (!Connectivities.isConnected(getApplicationContext())) {
            DialogView.showConfirmDialog(this, "提醒", "当前无网络，请检查您的网络连接状态", new Handler());
            return;
        }
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.showZoomControls(false);
        mapView.removeViewAt(1);
        baiduMap = mapView.getMap();
        baiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                overlayManager.zoomToSpan();
            }
        });
        overlayManager = new OverlayManager(baiduMap) {
            @Override
            public List<OverlayOptions> getOverlayOptions() {
                return overlayOptions;
            }

            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }

            @Override
            public boolean onPolylineClick(Polyline polyline) {
                return false;
            }
        };

        pickUpLatLng = new LatLng(tender.getPickup_region_location().get(1), tender.getPickup_region_location().get(0));//定义Maker坐标点
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_pickup);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions startOption = new MarkerOptions().position(pickUpLatLng).icon(bitmap);
        baiduMap.addOverlay(startOption);

//
//        overlayOptions.add(startOption);
//        overlayPoints.add(pickUpLatLng);
//


//        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_delivery);
//        MarkerOptions endOption = new MarkerOptions().position(deliveryLatLng).icon(bitmap);
//        overlayOptions.add(endOption);
//        overlayPoints.add(deliveryLatLng);
//
////        OverlayOptions ooPolyline = new PolylineOptions().width(10).color(Color.BLUE).points(overlayPoints);
////        Polyline mPolyline = (Polyline) baiduMap.addOverlay(ooPolyline);
//
//        overlayManager.addToMap();


//        startSearch(pickUpLatLng, deliveryLatLng);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTool.locationStop();
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            drivingRouteResult.getSuggestAddrInfo();
            Log.d("baiduMap", "起终点或途经点地址有岐义");
            return;
        }

        if (drivingRouteResult.error == SearchResult.ERRORNO.PERMISSION_UNFINISHED) {
            //权限鉴定未完成则再次尝试
            Log.d("baiduMap", "权限鉴定未完成,再次尝试");
            startSearch(pickUpLatLng, deliveryLatLng);
            return;
        }

        if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            return;
        }

        if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            int distance = drivingRouteResult.getRouteLines().get(0).getDistance();
            tvDistance.setText("距离：" + StringTools.getDistance(distance));
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(baiduMap);
            baiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(drivingRouteResult.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
            overlayManager.addToMap();
        }
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    private void startSearch(LatLng start, LatLng end) {
        PlanNode stNode = PlanNode.withLocation(start);
        PlanNode enNode = PlanNode.withLocation(end);

        routePlanSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));

    }

    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        /**
         * 构造函数
         *
         * @param baiduMap 该DrivingRouteOvelray引用的 BaiduMap
         */
        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public int getLineColor() {
            //红色的路径
            return Color.RED;
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            //自定义的起点图标
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_map_sign);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            //自定义的终点图标
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_map_delivery);
        }
    }
}
