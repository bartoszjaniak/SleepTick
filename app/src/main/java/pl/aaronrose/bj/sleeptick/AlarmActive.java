package pl.aaronrose.bj.sleeptick;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
//TODO zmienić parametry lokalizacji - oszczędność baterii
//TODO ogarnij ten kod kurwa!!

public class AlarmActive extends AppCompatActivity implements LocationListener {
    int width;
    ImageView progress, ecoIcon,wakeUp, accuracyIcon;
    LocationManager locationManager;
    Criteria criteria;
    Location location;
    String bestProvider;
    Destination destination = new Destination();
    double distance;
    int startDistance;
    int ringDistance;
    TextView destinationName, distanceToDestination, status;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_alarm_active);
        Bundle b = getIntent().getExtras();
        destination.Name = b.getString("name");
        destination.Longitude= b.getDouble("longitude");
        destination.Latitude = b.getDouble("latitude");
        ringDistance = b.getInt("distanceToRing");
        startDistance = -1;

        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


        Toast.makeText(getApplicationContext(), "Alarm uruchomiony", Toast.LENGTH_SHORT).show();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        //TODO: Rozdzielczość dla starszych androidków
        display.getSize(size);
        width = size.x;
        progress = (ImageView) findViewById(R.id.progress);
        ecoIcon = (ImageView) findViewById(R.id.eco);
        ecoIcon.setVisibility(View.GONE);
        accuracyIcon = (ImageView) findViewById(R.id.celownik);
        accuracyIcon.setVisibility(View.GONE);
        destinationName = (TextView) findViewById(R.id.city_name);
        distanceToDestination = (TextView) findViewById(R.id.distance);
        status = (TextView) findViewById(R.id.status);
        wakeUp = (ImageView) findViewById(R.id.wakeUp);

        ImageView miasteczko = (ImageView) findViewById(R.id.miasteczko);
        miasteczko.getLayoutParams().width = (int)width/2;
        miasteczko.getLayoutParams().height = (int)width/2;

        criteria = new Criteria();

        locationManager =(LocationManager) getSystemService(LOCATION_SERVICE);
        bestProvider = locationManager.getBestProvider(criteria, true);
        refreshPossition();
        try {
            locationManager.requestLocationUpdates(bestProvider, 5000, 100, this);
        }
        catch(SecurityException e) {
        }
        destinationName.setText(destination.Name);
        refreshPossition();
    }

    public void refreshPossition(){

        bestProvider = locationManager.getBestProvider(criteria,true);
        try {
            location = locationManager.getLastKnownLocation(bestProvider);
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), "Problem z odswierzeniem pozycji", Toast.LENGTH_SHORT).show();
        }


        if(location !=null) {
            distance = calculateTheDistance(location.getLongitude(), location.getLatitude(), destination.Longitude, destination.Latitude);
            //ustawianie paska budzenia
            if(startDistance == -1) {
                startDistance = (int) distance;
                wakeUp.getLayoutParams().width = (int)(((double) ringDistance /(double)startDistance)*width);
            }

            distanceToDestination.setText("do celu pozostało " + Math.round(distance) + " km");
            status.setText("dokładność: " + Math.round(location.getAccuracy()) + "m");

            //bsługa paska progressu
            if(startDistance == 0)
                setPro(100);
            else {
                int a = (int) (100.0-(distance/(double)startDistance)*100);
                setPro(a);
            }

            //uruchamianie buzika
            if(distance <= ringDistance){
                startAlarm(false);
            }
        }
        else {
            distanceToDestination.setText("Czekanie na informację o pozycji");
        }
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            if(level<5) {
                Toast.makeText(getApplicationContext(), "Niski stan baterii", Toast.LENGTH_LONG).show();
                startAlarm(true);
            }
        }
    };

    private double calculateTheDistance(double longitude1, double latitude1, double longitude2, double latitude2)
    {
        return Math.sqrt(Math.pow((Math.cos((Math.PI * longitude1) / 180) * (latitude2 - latitude1)), 2) + Math.pow((longitude2 - longitude1), 2)) * Math.PI * (12756.274 / 360);
    }

    public void setPro(int pro){
        int progressBarLenght = (int)(width*((double)pro/100));
        progress.getLayoutParams().width = progressBarLenght;
        progress.requestLayout();
    }

    @Override
    public void onLocationChanged(Location location) {
        refreshPossition();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //
        locationManager.removeUpdates(this);
        Toast.makeText(getApplicationContext(), "Nawigacja zatrzymana", Toast.LENGTH_SHORT).show();
    }

    //TODO notyfikacje z zatrzymaniem alarmu
    private void createNotification() {

        Intent intent = new Intent(this, Ring.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.alarm_icon);
        Notification noti = new NotificationCompat.Builder(this)
                .setContentTitle("SleepTick")
                .setContentText("Dojeżdżasz")
                .setTicker("Do celu pozostało: " + Math.round(distance) + " km")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setLargeIcon(icon)
                .setAutoCancel(true)
                .setContentIntent(pIntent)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, noti);
    }

    public void startAlarm(boolean lowEnergy){

        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);

        Alarm.startRing(getApplicationContext());
        Intent i = new Intent(this, Ring.class);
        if(lowEnergy){
            i.putExtra("nameOfDestination", "Niski poziom baterii");
            i.putExtra("distance", "Alarm włączony dla bezpieczeństwa");
        }
        else {
            i.putExtra("nameOfDestination", destination.Name);
            i.putExtra("distance", distance);
        }
        onDestroy();
        startActivity(i);
        this.finish();
        createNotification();
    }

    // Przyciski
    public void stopAlarm(View v) {
        Alarm.stopRing(getApplicationContext());
        this.finish();
        //TODO pytanie o zamknięcie jeśli nie dzowni
    }

    public void ecoInfo(View v){
        Toast.makeText(getApplicationContext(), "Tryb oszczędności baterii jest włączony", Toast.LENGTH_SHORT).show();
    }
    public void dokladnoscInfo(View v){
        Toast.makeText(getApplicationContext(), "Tryb wysokiej dokładności jest włączony", Toast.LENGTH_SHORT).show();
    }

}
