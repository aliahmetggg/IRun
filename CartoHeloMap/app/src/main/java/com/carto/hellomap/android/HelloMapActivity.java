package com.carto.hellomap.android;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.carto.components.RenderProjectionMode;
import com.carto.core.MapPos;
import com.carto.core.MapPosVector;
import com.carto.datasources.LocalVectorDataSource;
import com.carto.layers.CartoBaseMapStyle;
import com.carto.layers.CartoOnlineVectorTileLayer;
import com.carto.layers.VectorLayer;
import com.carto.projections.EPSG3857;
import com.carto.projections.Projection;
import com.carto.styles.LineJoinType;
import com.carto.styles.LineStyleBuilder;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.ui.MapClickInfo;
import com.carto.ui.MapEventListener;
import com.carto.ui.MapView;
import com.carto.vectorelements.Line;
import com.carto.vectorelements.Marker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.security.ProviderInstaller;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

import javax.net.ssl.SSLContext;



public class HelloMapActivity extends AppCompatActivity implements LocationListener {

    ArrayList<MapPos> mpArr = new ArrayList<MapPos>();
    LocationManager locationManager;
    LocationListener locationListener;
    final static String[] Permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
    final static int Permissions_All = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallBack;

    static final String LICENSE = "XTUMwQ0ZDNGo4cFZKMklMZHlFQVdZditGYzduazV4QzZBaFVBbkJzRUExMmhqVnFxSEY3bkpTUFVyM0M2NzdRPQoKYXBwVG9rZW49YzQxYTM5ZjktN2I5MC00MThhLTkyZjUtN2I0ODljZDYxZmFhCnBhY2thZ2VOYW1lPWNvbS5jYXJ0by5oZWxsb21hcC5hbmRyb2lkCm9ubGluZUxpY2Vuc2U9MQpwcm9kdWN0cz1zZGstYW5kcm9pZC00LioKd2F0ZXJtYXJrPWNhcnRvZGIK";

    private MapView mapView;

    private void fixSSLConnectionOnOlderAndroidDevices() {
        // Older Android versions (4.x) have issues accepting TLSv1.2 secure connections that SDK requires.
        // This snippet installs a workaround for such devices.
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = location -> {
            Log.i("Location", location.toString());
            Log.i("Get Accuracy", String.valueOf(location.getAccuracy()));
        };


        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(Permissions, Permissions_All);
        }
        requestLocation();

        ////////////////////////////////////////////////////////////////////////////////////////////

        fixSSLConnectionOnOlderAndroidDevices();

        MapView.registerLicense(LICENSE, getApplicationContext());

        // Set view from layout resource
        setContentView(R.layout.activity_hello_map);
        setTitle("Hello Map");
        mapView = (MapView) this.findViewById(R.id.map_view);

        // Set map view options
        Projection proj = new EPSG3857();
        mapView.getOptions().setBaseProjection(proj);
        mapView.getOptions().setRenderProjectionMode(RenderProjectionMode.RENDER_PROJECTION_MODE_SPHERICAL);
        mapView.getOptions().setZoomGestures(true);
        mapView.getOptions().setClearColor(new com.carto.graphics.Color(0xff000000));

        // Add base map
        CartoOnlineVectorTileLayer baseLayer = new CartoOnlineVectorTileLayer(CartoBaseMapStyle.CARTO_BASEMAP_STYLE_VOYAGER);
        mapView.getLayers().add(baseLayer);

        // Set default location and zoom
        //MapPos berlin = mapView.getOptions().getBaseProjection().fromWgs84(new MapPos(13.38933, 52.51704));
        //mapView.setFocusPos(berlin, 0);
        mapView.setZoom(2, 0);
        mapView.setZoom(4, 2);

        // Create data source and layer for makrers
        LocalVectorDataSource dataSource = new LocalVectorDataSource(proj);
        VectorLayer layer = new VectorLayer(dataSource);
        mapView.getLayers().add(layer);

        // Build Marker style
        //MarkerStyleBuilder styleBuilder = new MarkerStyleBuilder();
        //styleBuilder.setSize(20);
        //styleBuilder.setColor(new com.carto.graphics.Color(Color.WHITE));
        //MarkerStyle style = styleBuilder.buildStyle();

        // Create the actual Marker and add it to the data source
        //Marker marker = new Marker(berlin, style);
        //dataSource.add(marker);

        // Set map event listener to receive click events
        //mapView.setMapEventListener(new MyMapEventListener(dataSource));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Disconnect map event listener to avoid leaks
        mapView.setMapEventListener(null);
    }

    private void buildLocationRequest(){
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(100);

        locationRequest.setFastestInterval(100);
        locationRequest.setSmallestDisplacement(1);
    }

    private void buildLocationCallBack(){
        locationCallBack = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                for (Location location: locationResult.getLocations()){
                    String latitude = String.valueOf(location.getLatitude());
                    String longtitude = String.valueOf(location.getLongitude());

                }
            }
        };
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("mylog","Location" +location.getLongitude()  + "," +location.getLatitude()  );
        Toast.makeText(this, "Got Location" +  location.getLongitude() + "," + location.getLatitude(), Toast.LENGTH_SHORT).show();
        locationManager.removeUpdates(this);

        Projection proj = new EPSG3857();
        MapPos mp = mapView.getOptions().getBaseProjection().fromWgs84(new MapPos(location.getLongitude() , location.getLatitude() ));
        mpArr.add(mp);

        //mapView.setFocusPos(mp, 0);
        //mapView.setZoom(2, 0);
        //mapView.setZoom(4, 2);

        // Create data source and layer for makrers
        //mapView.getLayers().clear();
        //mapView.getMapRenderer().setMapRendererListener(null);
        //mapView.getMapRenderer().delete();

        LocalVectorDataSource dataSource = new LocalVectorDataSource(proj);
        VectorLayer layer = new VectorLayer(dataSource);
        mapView.getLayers().add(layer);

        // Build Marker style
        MarkerStyleBuilder styleBuilder = new MarkerStyleBuilder();
        styleBuilder.setSize(20);
        styleBuilder.setColor(new com.carto.graphics.Color(Color.BLUE));
        MarkerStyle style = styleBuilder.buildStyle();

        // Create the actual Marker and add it to the data source
        Marker marker = new Marker(mp, style);
        dataSource.add(marker);

        ///////////////////////////////////////////////////////////////////////////////////////////

        // 1. Create line style, and line poses
        LineStyleBuilder lineStyleBuilder = new LineStyleBuilder();
        lineStyleBuilder.setColor(new com.carto.graphics.Color(Color.BLUE) );
        lineStyleBuilder.setLineJoinType(LineJoinType.LINE_JOIN_TYPE_ROUND);
        lineStyleBuilder.setWidth(8);

        // 2. Special MapPosVector must be used for coordinates
        MapPosVector linePoses = new MapPosVector();
        //MapPos initial = proj.fromWgs84(new MapPos(11.38933, 52.51704));
        //linePoses.add(mp);

        if(mpArr.size()==0){
            mpArr.add(mp);
            //linePoses.add(mp);
        }
        else{
            // 3. Add positions
            for (int i = 0; i< mpArr.size(); i++){
                linePoses.add(mpArr.get(i));
            }
        }

        //linePoses.add(initial);
        //linePoses.add(mp);
        //linePoses.add(mapView.getOptions().getBaseProjection().fromWgs84(new MapPos(location.getLongitude() , location.getLatitude() )));

        //linePoses.add(proj.fromWgs84(new MapPos(24.645351, 59.419149)));
        //linePoses.add(proj.fromWgs84(new MapPos(24.648956, 59.420393)));
        //linePoses.add(proj.fromWgs84(new MapPos(24.650887, 59.422707)));

        // 4. Add a line
        Line line1 = new Line(linePoses, lineStyleBuilder.buildStyle());
        //line1.setMetaDataElement("ClickText", new Variant("Line nr 1"));
        //line1.getPoses().add(mp);

        //line1.getPoses().add(mapView.getOptions().getBaseProjection().fromWgs84(new MapPos(location.getLongitude() , location.getLatitude() )));

        dataSource.add(line1);

        // Add base map
        CartoOnlineVectorTileLayer baseLayer = new CartoOnlineVectorTileLayer(CartoBaseMapStyle.CARTO_BASEMAP_STYLE_VOYAGER);
        mapView.getLayers().add(baseLayer);
        mapView.setFocusPos(mp,0);
        //mapView.setMapEventListener(new MyMapEventListener(dataSource));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //Request Location Now
            requestLocation();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestLocation();
                    handler.postDelayed(this, 1000);
                }
            },1000);
        }
    }
    public void requestLocation(){
        if (locationManager == null){
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1000, this );
            }
        }
    }
    /*********************
     MAP CLICK LISTENER
     **********************/
    private static class MyMapEventListener extends MapEventListener {

        private int[] colors = { Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN };

        private LocalVectorDataSource dataSource;

        private Random random;

        public MyMapEventListener(LocalVectorDataSource dataSource) {
            this.dataSource = dataSource;
            this.random = new Random();
        }

        @Override
        public void onMapClicked(MapClickInfo mapClickInfo) {

            /*
            super.onMapClicked(mapClickInfo);

            // Build new marker style with random size and color
            MarkerStyleBuilder styleBuilder = new MarkerStyleBuilder();
            styleBuilder.setSize(random.nextInt(30) + 15);
            styleBuilder.setColor(new com.carto.graphics.Color(colors[random.nextInt(colors.length)]));
            MarkerStyle style = styleBuilder.buildStyle();

            // Create a new marker with the defined style at the clicked position
            Marker marker = new Marker(mapClickInfo.getClickPos(), style);
            dataSource.add(marker);

            */
            super.onMapClicked(mapClickInfo);
            Projection proj = new EPSG3857();

            // Build new marker style with random size and color
            MarkerStyleBuilder styleBuilder = new MarkerStyleBuilder();
            styleBuilder.setSize(random.nextInt(30) + 15);
            styleBuilder.setColor(new com.carto.graphics.Color(colors[random.nextInt(colors.length)]));
            MarkerStyle style = styleBuilder.buildStyle();

            // Create a new marker with the defined style at the clicked position
            Marker marker = new Marker(mapClickInfo.getClickPos(), style);
            dataSource.add(marker);

            // 1. Create line style, and line poses
            LineStyleBuilder lineStyleBuilder = new LineStyleBuilder();
            lineStyleBuilder.setColor(new com.carto.graphics.Color(colors[random.nextInt(colors.length)]));
            //lineStyleBuilder.setLineJointType(LineJointType.LINE_JOINT_TYPE_ROUND);
            lineStyleBuilder.setWidth(8);

            // 2. Special MapPosVector must be used for coordinates
            MapPosVector linePoses = new MapPosVector();
            MapPos initial = proj.fromWgs84(mapClickInfo.getClickPos());

            // 3. Add positions
            linePoses.add(initial);
            linePoses.add(proj.fromWgs84(new MapPos(13.38933, 52.51704)));
            //linePoses.add(proj.fromWgs84(new MapPos(24.645351, 59.419149)));
            //linePoses.add(proj.fromWgs84(new MapPos(24.648956, 59.420393)));
            //linePoses.add(proj.fromWgs84(new MapPos(24.650887, 59.422707)));

            // 4. Add a line
            Line line1 = new Line(linePoses, lineStyleBuilder.buildStyle());
            //line1.setMetaDataElement("ClickText", new Variant("Line nr 1"));

            dataSource.add(line1);
        }
    }
}
