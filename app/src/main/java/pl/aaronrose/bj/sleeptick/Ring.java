package pl.aaronrose.bj.sleeptick;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class Ring extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_ring);



        Bundle b = getIntent().getExtras();
        TextView miasto = (TextView) findViewById(R.id.cityNameRing);
        miasto.setText(b.getString("nameOfDestination"));

        TextView dystans = (TextView) findViewById(R.id.dystansRing);
        dystans.setText(Math.round(b.getDouble("distance"))+" km");
    }
    public void stopAlarm(View v){
        Alarm.stopRing(getApplicationContext());
        this.finish();
    }

    @Override
    public void onBackPressed() {
        //
    }
}
