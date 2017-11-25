package ru.shuttlecar.shuttlecar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

import ru.shuttlecar.shuttlecar.models.Constants;
import ru.shuttlecar.shuttlecar.models.MarkerItem;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        ClusterManager.OnClusterClickListener<MarkerItem>, ClusterManager.OnClusterInfoWindowClickListener<MarkerItem>,
        ClusterManager.OnClusterItemClickListener<MarkerItem>, ClusterManager.OnClusterItemInfoWindowClickListener<MarkerItem>, View.OnClickListener, Animation.AnimationListener {

    private Animation anim;
    private GoogleMap mMap;
    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<Marker> temp_markers = new ArrayList<>();
    private int count = 0;
    private ClusterManager<MarkerItem> mClusterManager;
    private ImageButton btn_done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setTitle(R.string.title_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        btn_done = (ImageButton) findViewById(R.id.map_btn_done);
        btn_done.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.setRenderer(new Renderer());
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);


        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.map_style));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.344331, 86.064463), 10));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            mMap.setMyLocationEnabled(true);
        }
        List<MarkerItem> items = HelpMethods.getStations();
        mClusterManager.addItems(items);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);


        mMap.setOnInfoWindowClickListener(this);
        mClusterManager.cluster();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (count == 0) {
            markers.add(marker);
            Toast.makeText(this, "Пункт A выбран", Toast.LENGTH_SHORT).show();
            count++;
            temp_markers.add(mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title("Пункт A")));
        } else if (count == 1) {
            markers.add(marker);
            Toast.makeText(this, "Пункт B выбран", Toast.LENGTH_SHORT).show();
            temp_markers.add(mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title("Пункт B")));
            count++;
            anim = AnimationUtils.loadAnimation(this, R.anim.layout_order_slide);
            anim.setAnimationListener(this);

            btn_done.startAnimation(anim);
            btn_done.setVisibility(View.VISIBLE);

        } else {
            for (Marker mark : temp_markers) {
                mark.remove();
            }
            markers.clear();
            count = 0;
            btn_done.setVisibility(View.GONE);

            markers.add(marker);
            Toast.makeText(this, "Пункт A выбран", Toast.LENGTH_SHORT).show();
            count++;
            temp_markers.add(mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title("Пункт A")));
        }
    }

    @Override
    public boolean onClusterClick(Cluster<MarkerItem> cluster) {
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<MarkerItem> cluster) {

    }

    @Override
    public boolean onClusterItemClick(MarkerItem markerItem) {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(MarkerItem markerItem) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.map_btn_done) {
            if (markers != null && !markers.isEmpty() && markers.size() == 2) {
                String pdis, pdel;
                pdis = markers.get(0).getTitle();
                pdel = markers.get(1).getTitle();

                Intent intent = new Intent();
                intent.putExtra(Constants.PDIS, pdis);
                intent.putExtra(Constants.PDEL, pdel);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == anim) {
        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    private class Renderer extends DefaultClusterRenderer<MarkerItem> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;

        Renderer() {
            super(getApplicationContext(), mMap, mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.map_info_window, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterIconGenerator.setBackground(ContextCompat.getDrawable(getApplicationContext(), android.R.color.transparent));

            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.map_icon);
            mImageView = new ImageView(getApplicationContext());
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerItem markerItem, MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_station)).title(markerItem.getTitle());
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<MarkerItem> cluster, MarkerOptions markerOptions) {
            mClusterImageView.setImageResource(R.mipmap.ic_station);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() > 1;
        }
    }
}