package com.carto.hellomap.android.Controllers;


import static com.carto.hellomap.android.Controllers.WeatherMain.AppId;
import static com.carto.hellomap.android.Controllers.WeatherMain.BaseUrl;
import static com.carto.hellomap.android.Controllers.WeatherMain.lat;
import static com.carto.hellomap.android.Controllers.WeatherMain.lon;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
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
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.L;
import com.ajts.androidmads.fontutils.FontUtils;
import com.carto.components.RenderProjectionMode;
import com.carto.core.MapPos;
import com.carto.core.MapPosVector;
import com.carto.datasources.LocalVectorDataSource;
import com.carto.hellomap.android.Helpers.SQLiteHelper;
import com.carto.hellomap.android.R;
import com.carto.hellomap.android.Controllers.WeatherMain;
import com.carto.hellomap.android.Controllers.WeatherService;
import com.carto.layers.CartoBaseMapStyle;
import com.carto.layers.CartoOnlineVectorTileLayer;
import com.carto.layers.VectorLayer;
import com.carto.projections.EPSG3857;
import com.carto.projections.Projection;
import com.carto.styles.LineJoinType;
import com.carto.styles.LineStyleBuilder;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.styles.PointStyleBuilder;
import com.carto.ui.MapClickInfo;
import com.carto.ui.MapEventListener;
import com.carto.ui.MapView;
import com.carto.vectorelements.Label;
import com.carto.vectorelements.Line;
import com.carto.vectorelements.Marker;
import com.carto.vectorelements.Point;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.security.ProviderInstaller;
import com.google.type.LatLng;
import com.google.type.LatLngOrBuilder;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.net.ssl.SSLContext;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import static java.lang.Math.abs;

public class HelloMapActivity extends Activity implements LocationListener {

    Button startStop;
    TextView currentDistance, currentDuration, totDistance, totDuration;
    SimpleDateFormat simpleDateFormat;
    String date;
    boolean timerStart = false;
    Timer timer;
    TimerTask timerTask;
    double time = 0.0;
    SQLiteHelper db;
    SharedPreferences sharedPreferences;
    String userId;
    String finalDuration;
    String totalHours, totalMinutes, totalSeconds;
    String totalDuration = "";
    Date startDate;
    Date finishDate;
    long epochStart;
    long epochFinish;
    Calendar calenderStart;
    Calendar calenderFinish;
    int tempDistance = 0;


    ArrayList<MapPos> mpArr = new ArrayList<MapPos>();
    ArrayList<MapPos> mpArrTemp = new ArrayList<MapPos>();  // wgs84 EPSG 4326 location coordinate array
    LocationManager locationManager;
    LocationListener locationListener;
    SensorManager sensorManager;
    final static String[] Permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
    final static int Permissions_All = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallBack;

    static final String LICENSE = "XTUMwQ0ZDNGo4cFZKMklMZHlFQVdZditGYzduazV4QzZBaFVBbkJzRUExMmhqVnFxSEY3bkpTUFVyM0M2NzdRPQoKYXBwVG9rZW49YzQxYTM5ZjktN2I5MC00MThhLTkyZjUtN2I0ODljZDYxZmFhCnBhY2thZ2VOYW1lPWNvbS5jYXJ0by5oZWxsb21hcC5hbmRyb2lkCm9ubGluZUxpY2Vuc2U9MQpwcm9kdWN0cz1zZGstYW5kcm9pZC00LioKd2F0ZXJtYXJrPWNhcnRvZGIK";

    private MapView mapView;

    private TextView weatherData;
    TextView pedometer;
    private double MagnitudePrevious = 0;
    private int stepCount = 0;




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

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);



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
        mapView = this.findViewById(R.id.map_view);

        // Set map view options
        Projection proj = new EPSG3857();
        mapView.getOptions().setBaseProjection(proj);
        mapView.getOptions().setRenderProjectionMode(RenderProjectionMode.RENDER_PROJECTION_MODE_SPHERICAL);
        mapView.getOptions().setZoomGestures(true);
        mapView.getOptions().setClearColor(new com.carto.graphics.Color(0xff000000));

        // Add base map
        CartoOnlineVectorTileLayer baseLayer = new CartoOnlineVectorTileLayer(CartoBaseMapStyle.CARTO_BASEMAP_STYLE_VOYAGER);
        mapView.getLayers().add(baseLayer);

        mapView.setZoom(2, 0);
        mapView.setZoom(4, 2);

        // Create data source and layer for makrers
        LocalVectorDataSource dataSource = new LocalVectorDataSource(proj);
        VectorLayer layer = new VectorLayer(dataSource);
        mapView.getLayers().add(layer);

        currentDuration = findViewById( R.id.total_duration2 );
        startStop = findViewById( R.id.start_newact2 );
        totDistance = findViewById( R.id.total_distance2 );
        totDuration = findViewById( R.id.duration_newact4 );
        currentDistance = findViewById(R.id.distance_newact2);

        pedometer = findViewById(R.id.textView4);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZZZZ");

        timer = new Timer();
        db = new SQLiteHelper( HelloMapActivity.this );

        sharedPreferences = getApplicationContext().getSharedPreferences( "UserData", Context.MODE_PRIVATE );
        userId = sharedPreferences.getString( "userId", "" );

        startStop.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {

                try {
                    startTimer();
                } catch ( ParseException e ) {
                    e.printStackTrace();
                }
            }
        } );

        weatherData = findViewById(R.id.weather);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
        FontUtils fontUtils = new FontUtils();
        fontUtils.applyFontToView(weatherData, typeface);

        getCurrentData();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
            }
        }


    }


    SensorEventListener stepDetector = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent!= null){
                float x_acceleration = sensorEvent.values[0];
                float y_acceleration = sensorEvent.values[1];
                float z_acceleration = sensorEvent.values[2];

                double Magnitude = Math.sqrt(x_acceleration*x_acceleration + y_acceleration*y_acceleration + z_acceleration*z_acceleration);
                double MagnitudeDelta = Magnitude - MagnitudePrevious;
                MagnitudePrevious = Magnitude;

                if (MagnitudeDelta > 6){
                    stepCount++;
                }
                pedometer.setText( String.valueOf(stepCount) );
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };



    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", stepCount);
        editor.apply();
    }

    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", stepCount);
        editor.apply();
    }

    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        stepCount = sharedPreferences.getInt("stepCount", 0);
    }



    void getCurrentData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory( GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call< WeatherResponse > call = service.getCurrentWeatherData(lat, lon, AppId);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;

                    int finalTemp = ( int ) ( weatherResponse.main.temp - 273.15 );

                    String stringBuilder = "Temperature: " + " \n" +  finalTemp ;

                    weatherData.setBackgroundColor( Color.WHITE );
                    weatherData.setText(stringBuilder + " °C");
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                weatherData.setText(t.getMessage());
            }
        });
    }


    public void startTimer() throws ParseException {
        if( !timerStart){
            timerStart = true;
            startStop.setText( "Stop" );
            calenderStart = Calendar.getInstance();
            startDate = simpleDateFormat.parse( simpleDateFormat.format( calenderStart.getTime() ) );
            epochStart = startDate.getTime();

            startCountUp();
        }
        else{
            timerStart = false;
            startStop.setText( "Start" );
            timerTask.cancel();
            finalDuration = currentDuration.getText().toString();
            calenderFinish = Calendar.getInstance();
            finishDate = simpleDateFormat.parse( simpleDateFormat.format( calenderFinish.getTime() ));
            epochFinish = finishDate.getTime();

            totalHours = finalDuration.substring( 0, 2 );
            totalMinutes = finalDuration.substring( 3, 5 );
            totalSeconds = finalDuration.substring( 6 );


            if( !totalHours.equals("00")){
                totalDuration = totalDuration + totalHours + " saat ";
            }

            if( ! totalMinutes.equals( "00" )){
                totalDuration = totalDuration + totalMinutes + " dakika ";
            }

            if( ! totalSeconds.equals( "00" )){
                totalDuration = totalDuration + totalSeconds + " saniye";
            }

            currentDuration.setText( totalDuration );
            totDuration.setText( "Toplam Süre:" );
            totDistance.setText( "Toplam mesafe:" );

            db.addNewActivity( userId, tempDistance, epochStart, epochFinish);

        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }

    public void startCountUp() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread( new Runnable(){
                    @Override
                    public void run() {
                        time++;
                        currentDuration.setText( getTimerText() );
                    }

                });
            }
        };
        timer.scheduleAtFixedRate( timerTask, 0, 1050 );
    }

    public String getTimerText(){
        int rounded = (int) Math.round( time );

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime( seconds, minutes, hours);

    }

    public String formatTime( int seconds, int minutes, int hours ) {

        return String.format( "%02d", hours ) + ":" + String.format( "%02d", minutes ) + ":" + String.format( "%02d", seconds );
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

    public double distance(List<MapPos> mapList) {
        float[] results = new float[1];
        Location.distanceBetween(mapList.get(mapList.size()-1).getX(),mapList.get(mapList.size()-1).getY(),
                mapList.get(mapList.size()-2).getX(),mapList.get(mapList.size()-2).getY(), results);
        return  results[0];
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        Log.d("mylog","Location" +location.getLongitude()  + "," +location.getLatitude()  );
        // Toast.makeText(this, "Got Location" +  location.getLongitude() + "," + location.getLatitude(), Toast.LENGTH_SHORT).show();
        locationManager.removeUpdates(this);

        Projection proj = new EPSG3857();

        MapPos mp = mapView.getOptions().getBaseProjection().fromWgs84(new MapPos(location.getLongitude() , location.getLatitude() ));
        mpArr.add(mp);
        mpArrTemp.add(new MapPos(location.getLongitude() , location.getLatitude() ));
        System.out.println(mpArr.get(mpArr.size() - 1));

        LocalVectorDataSource dataSource = new LocalVectorDataSource(proj);
        VectorLayer layer = new VectorLayer(dataSource);
        mapView.getLayers().add(layer);

        PointStyleBuilder pointStyleBuilder = new PointStyleBuilder();
        pointStyleBuilder.setColor(new com.carto.graphics.Color(Color.BLUE));
        pointStyleBuilder.setSize(16);

        // 3. Create Point, add to datasource with metadata
        Point point1 = new Point(mpArr.get(mpArr.size()-1), pointStyleBuilder.buildStyle());
        //point1.setMetaDataElement("ClickText", "Point nr 1");

        ///////////////////////////////////////////////////////////////////////////////////////////

        // 1. Create line style, and line poses
        LineStyleBuilder lineStyleBuilder = new LineStyleBuilder();
        lineStyleBuilder.setColor(new com.carto.graphics.Color(Color.GRAY) );
        lineStyleBuilder.setLineJoinType(LineJoinType.LINE_JOIN_TYPE_ROUND);
        lineStyleBuilder.setWidth(8);

        // 2. Special MapPosVector must be used for coordinates
        MapPosVector linePoses = new MapPosVector();

        if( mpArrTemp.size() > 2){

            tempDistance = (int) (tempDistance + distance( mpArrTemp ));

        }

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

        int kilometers = tempDistance / 1000;
        int meters = tempDistance % 1000;

        String finalDistance = "";

        if( kilometers != 0){
            finalDistance = kilometers + " kilometre ";
        }

        finalDistance = finalDistance + meters + " metre";

        currentDistance.setText( finalDistance );
        // 4. Add a line
        Line line1 = new Line(linePoses, lineStyleBuilder.buildStyle());

        // 2. Create style and position for the Point
        dataSource.add(line1);
        dataSource.add(point1);
        CartoOnlineVectorTileLayer baseLayer = new CartoOnlineVectorTileLayer(CartoBaseMapStyle.CARTO_BASEMAP_STYLE_POSITRON);
        mapView.getLayers().add(baseLayer);
        mapView.setFocusPos(mp,0);

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
    /*
     MAP CLICK LISTENER
     **/
    private static class MyMapEventListener extends MapEventListener {

        private final int[] colors = { Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN };

        private final LocalVectorDataSource dataSource;

        private final Random random;

        public MyMapEventListener(LocalVectorDataSource dataSource) {
            this.dataSource = dataSource;
            this.random = new Random();
        }
        @Override
        public void onMapClicked(MapClickInfo mapClickInfo) {
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