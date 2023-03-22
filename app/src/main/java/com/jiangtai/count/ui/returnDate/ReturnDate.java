package com.jiangtai.count.ui.returnDate;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.jiangtai.count.R;
import com.jiangtai.count.bean.LocationReceiver;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ReturnDate extends AppCompatActivity {

    private MapView ismapView_fragment;
    private TextView returnname;
    private AMap map;
    private Polyline polyline;
    private List<LocationReceiver> all;
    private ArrayList<LatLng> objects;
    private ArrayList<LatLng> zuobiao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_date);
        returnname = findViewById(R.id.returnname);
        ismapView_fragment = findViewById(R.id.ismapView_fragment);
        ismapView_fragment.onCreate(savedInstanceState);
        all = LitePal.findAll(LocationReceiver.class);
        getWindow().setStatusBarColor((Color.TRANSPARENT));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
         Intent intent = getIntent();
        String projectId = intent.getStringExtra("projectId");
        String personId = intent.getStringExtra("personId");

        String name = intent.getStringExtra("name");
        returnname.setText(name);

        if (map==null){
            map = ismapView_fragment.getMap();
        }
        map.moveCamera(CameraUpdateFactory.zoomTo(18));
        map.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(40.821296, 114.899288)));


        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.setDottedLine(true);
        polylineOptions.geodesic(true);
        polylineOptions.visible(true);
        polylineOptions.useGradient(true);


        polyline = map.addPolyline(polylineOptions);
        LatLngBounds.Builder builder = LatLngBounds.builder();
        objects = new ArrayList<>();
        zuobiao = new ArrayList<>();
        for (int i=0;i<all.size();i++){
            String projectId1 = all.get(i).getProjectId();
            String userId = all.get(i).getUserId();
            if (projectId1.equals(projectId)){
                if (personId.equals(userId)){
                    if (!all.get(i).getLat().equals("0.0")){
                        zuobiao.add(new LatLng(Double.parseDouble(all.get(i).getLat()), Double.parseDouble(all.get(i).getLng())));
                        LatLng latLng = new LatLng(Double.parseDouble(all.get(i).getLat()), Double.parseDouble(all.get(i).getLng()));
                        this.objects.add(latLng);
                        //  builder.include(new LatLng(Double.parseDouble(all.get(i).getLat()),Double.parseDouble(all.get(i).getLng())));
                    }
                }
            }
        }
        map.addPolyline((new PolylineOptions()).addAll(objects).width(20).setDottedLine(true).geodesic(true)
                .color(Color.RED));
        int size = zuobiao.size();


if (size>0){
    MarkerOptions markerOptions1 = new MarkerOptions().position(zuobiao.get(size-1));
    markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.mipmap.zd));
    Marker marker1 = map.addMarker(markerOptions1);
    marker1.showInfoWindow();

for (int i = 0;i<zuobiao.size();i++){
    if (zuobiao.get(i).latitude!=0.0){
        MarkerOptions markerOptions = new MarkerOptions().position(zuobiao.get(i));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.qd));
        Marker marker = map.addMarker(markerOptions);
        marker.showInfoWindow();
        return;
    }


}

}





    }
}