package pl.aaronrose.bj.sleeptick;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

//Aktywność sprawdziająca czy włączona jest usługa lokalizacji
//Jeśli nie jest to prosi użytkownika o włączenie
public class AskToTurnOnLocationServices extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acrivity_ask_to_turn_on_location_services);
        ImageView button = (ImageView) findViewById(R.id.turn_on_location_services);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent turnOnLocationServices = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(turnOnLocationServices);
                finish();
            }
        });
    }
}
