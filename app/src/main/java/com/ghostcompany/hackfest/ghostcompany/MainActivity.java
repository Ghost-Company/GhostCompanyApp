package com.ghostcompany.hackfest.ghostcompany;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.ghostcompany.hackfest.ghostcompany.Async.AsyncGetEmpresasProximas;
import com.ghostcompany.hackfest.ghostcompany.Async.AsyncSendInform;
import com.ghostcompany.hackfest.ghostcompany.models.AdapterEntity;
import com.ghostcompany.hackfest.ghostcompany.models.Attributes;
import com.ghostcompany.hackfest.ghostcompany.models.Empresa;
import com.ghostcompany.hackfest.ghostcompany.models.Entity;
import com.ghostcompany.hackfest.ghostcompany.models.OnGetEmpresaProximaCompletedCallback;
import com.ghostcompany.hackfest.ghostcompany.models.OnPostEmpresaInfoCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, OnGetEmpresaProximaCompletedCallback,
        LocationListener, BaseSliderView.OnSliderClickListener, OnPostEmpresaInfoCallback, GoogleApiClient.OnConnectionFailedListener, ViewPagerEx.OnPageChangeListener {
    private SliderLayout sliderImages;
    private Button btFoto, btnYes, btnNo;
    private TextView tvEndereco, tvCnpj, tvEmpresaTitulo;
    private static final int TIRAR_FOTO = 1001;
    private ImageView ivSlider;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private static String ID;


    private ActionBarDrawerToggle mDrawerToggle;
    public boolean threadsAlive = false;
    private DrawerLayout mDrawerLayout;

    private String strLat = "";
    private String strLng = "";

   // private GoogleMap googleMap;
    private static final long INTERVAL = 1000 * 4;
    private static final long FASTEST_INTERVAL = 1000 * 2;
    private static final long SMALLEST_DISPLACEMENT = 10;
    private long alertaAtual;
    private long alertaAnterior;
    private boolean startup = true;
    private boolean infoSended;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyConection();
        tvEmpresaTitulo = (TextView) findViewById(R.id.tvMainEmpresaTitulo);
        tvEmpresaTitulo.setText("Investigando...");
        //tvEmpresaTitulo.setVisibility(View.INVISIBLE);
        tvCnpj = (TextView) findViewById(R.id.tvMainEmpresaCnpj);
        tvCnpj.setText("Empresas próximas.");
        //       tvCnpj.setVisibility(View.INVISIBLE);

        tvEndereco = (TextView) findViewById(R.id.tvMainEmpresaEnderecoj);
        tvEndereco.setText("");
        tvEndereco.setVisibility(View.INVISIBLE);
        callConnection();
//        sliderImages = (SliderLayout) findViewById(R.id.slider);
        ivSlider = (ImageView) findViewById(R.id.ivSlider);


        btnYes = (Button) findViewById(R.id.button);
        btnYes.setOnClickListener(btnYesListener);
        btnYes.setVisibility(View.INVISIBLE);
        btnNo = (Button) findViewById(R.id.button2);
        btnNo.setOnClickListener(btnNoListener);
        btnNo.setVisibility(View.INVISIBLE);
        ID = getDeviceUniqueID(this);
        startup = true;
    }

    public void verifyConection(){
        if(!Util.isNetworkAvaiable(this)){
            Toast.makeText(getApplicationContext(), getText(R.string.no_network_avaible), Toast.LENGTH_LONG).show();
        }
    }

    public View.OnClickListener btnYesListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String[] params = {ID,String.valueOf(tvCnpj.getText()),"1"};
            // Toast.makeText(getApplicationContext(), "Lat "+strLat+" Lng "+strLng, Toast.LENGTH_SHORT).show();
            try {
                AsyncSendInform asyncSendInform = new AsyncSendInform(MainActivity.this);
                asyncSendInform.execute(params);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    public static String getDeviceUniqueID(Activity activity){
        String device_unique_id = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return device_unique_id;
    }
    public View.OnClickListener btnNoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String[] params = {ID,String.valueOf(tvCnpj.getText()),"0"};
            // Toast.makeText(getApplicationContext(), "Lat "+strLat+" Lng "+strLng, Toast.LENGTH_SHORT).show();
            try {
                AsyncSendInform asyncSendInform = new AsyncSendInform(MainActivity.this);
                asyncSendInform.execute(params);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }
    private synchronized void callConnection(){
        Log.i("LOG", "UpdateLocationActivity.callConnection()");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onResume() {
        super.onResume();
        verifyConection();
        mGoogleApiClient.connect();
        //setMainScreen();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startLocationUpdate();
        }

    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        // sliderImages.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this, slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void setLayouts(Empresa e) {
        //tvTitulo.setText("Empresaa");
        tvEmpresaTitulo.setText("Empresaa");
        tvCnpj.setText("12234567");
    }

    @Override
    public void onGetEmpresaProximaCompleted(String result) throws Exception {
        // get string with company close 30m
        if (!result.contains("No context element found") && !result.equals("") && startup) {
            String distance = getDistance(result);

            alertaAtual = System.nanoTime();
            if ((Double.parseDouble(distance) <= 30.0)) {
              /*  if (startup) {
                    alertaAnterior = alertaAtual;*/
                    notify(result);
              //      startup = false;
             /*   } else if (alertaAtual - alertaAnterior > 30000000000.0f) {
                    notify(result);
                    alertaAnterior = alertaAtual;
                }*/
            }


            Log.v("DISTANCIA", distance);
        } else {
            setMainScreen();

        }

    }

    public void notify(String s) throws Exception {
        List<Entity> listEntity = AdapterEntity.parseListEntity(s);

        for (Entity entity : listEntity) {
            for (Attributes att : entity.getAttributes()) {
                if (att.getName().equalsIgnoreCase("Title")) {
                    tvEmpresaTitulo.setText(att.getValue());
                }
                if (att.getName().equalsIgnoreCase("empresaCode")) {
                    tvCnpj.setText("CNPJ: "+att.getValue());
                }
                if (att.getName().equalsIgnoreCase("endereco")) {
                    tvEndereco.setText("Endereco: "+att.getValue());
                }
            }
        }
        Vibrar();
        ivSlider.setImageResource(R.mipmap.companyalert);
        btnYes.setVisibility(View.VISIBLE);
        btnNo.setVisibility(View.VISIBLE);
        tvEndereco.setVisibility(View.VISIBLE);
    }

    public void setMainScreen(){
        tvEmpresaTitulo.setText("Investigando...");
        tvEndereco.setVisibility(View.INVISIBLE);
        tvCnpj.setText("Empresas próximas.");
        ivSlider.setImageResource(R.mipmap.detective);
        btnYes.setVisibility(View.INVISIBLE);
        btnNo.setVisibility(View.INVISIBLE);
        tvEndereco.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("LOG", "UpdateLocationActivity.onConnected(" + bundle + ")");

        mLastLocation = getLastLocation(); // PARA JÁ TER UMA COORDENADA PARA O UPDATE FEATURE UTILIZAR

        if (mLastLocation != null) {
            LatLng currentLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
          //  this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16));
        }

        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "UpdateLocationActivity.onConnectionSuspended(" + i + ")");

    }

    private Location getLastLocation(){
        if(PermissionRequest.checkLocationPermission(this)){
          //  googleMap.setMyLocationEnabled(true);
        }else{
            PermissionRequest.requestLocationPermission(this);
        }
        if(mGoogleApiClient!=null && mGoogleApiClient.isConnected())
            return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        return null;
    }

    public String getDistance(String result) throws Exception {
        List<Entity> listEntity = AdapterEntity.parseListEntity(result);
        double minDistance = 0;
        double distance = 0;
        boolean isFirst = true;
        for (Entity entity : listEntity) {
            for (Attributes att : entity.getAttributes()) {
                if (att.getName().equalsIgnoreCase("GPSCoord")) {
                    String[] tokensVal = att.getValue().split(",");
                    distance = Util.distance(tokensVal[0].trim(), tokensVal[1].trim(), strLat, strLng, 'M');
                    if (isFirst) {
                        minDistance = distance;
                        isFirst = false;
                    } else {
                        if (minDistance < distance) {
                            minDistance = distance;
                        }
                    }
                }
            }
        }
        return String.valueOf(minDistance);
    }

    private void startLocationUpdate(){
        if(PermissionRequest.checkLocationPermission(this)){
            startLocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else{
            PermissionRequest.requestLocationPermission(this);
        }
    }

    private void startLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT); // deslocamento mínimo em metros
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    private void stopLocationUpdate(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        setStrLat(String.valueOf(location.getLatitude()));
        setStrLng(String.valueOf(location.getLongitude()));
        searchCloseCompany(strLat, strLng);
    }

    public void searchCloseCompany(String strLat, String strLng){
        String[] myLatLngTaskParams = {strLat,strLng};
        // Toast.makeText(getApplicationContext(), "Lat "+strLat+" Lng "+strLng, Toast.LENGTH_SHORT).show();
        try {
            AsyncGetEmpresasProximas asyncGetEmpresasProximas = new AsyncGetEmpresasProximas(MainActivity.this);
            asyncGetEmpresasProximas.execute(myLatLngTaskParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("LOG", "UpdateLocationActivity.onConnectionFailed(" + connectionResult + ")");
    }

    @Override
    public void onPostEmpresaInfoCompleted(String result) {
        if(result.equals("200")){
             infoSended = true;
             Toast.makeText(getApplicationContext(), " Reporte Enviado com Sucesso!", Toast.LENGTH_SHORT).show();
            setMainScreen();
        }
    }

    private void Vibrar()
    {
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        long milliseconds = 1000;//'500' é o tempo em milissegundos, é basicamente o tempo de duração da vibração. portanto, quanto maior este numero, mais tempo de vibração você irá ter
        v.vibrate(milliseconds);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_action_settings:
                getMapActivity();
                return true;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getMapActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void setStrLat(String latitudeString) {
        this.strLat = latitudeString;
    }

    public void setStrLng(String longitudeString) {
        this.strLng = longitudeString;
    }


}
