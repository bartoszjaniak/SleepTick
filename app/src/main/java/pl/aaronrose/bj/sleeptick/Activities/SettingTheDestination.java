package pl.aaronrose.bj.sleeptick.Activities;

import android.app.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import pl.aaronrose.bj.sleeptick.Other.SelectionOption;
import pl.aaronrose.bj.sleeptick.R;
import pl.aaronrose.bj.sleeptick.models.Destination;

public class SettingTheDestination extends AppCompatActivity {

    public int distanceToStartRinging = 20;
    EditText nameToSearch;
    SelectionOption option1, option2, option3, option4;
    List<Destination> listOfDestinations = new ArrayList();
    List<Destination> listOfChoice = new ArrayList<Destination>();
    int choice = 0;
    boolean nothingFound = false;

    @Override
    protected void onResume() {
        super.onResume();
        //sprawdzanie czy lokalizacja jest włączona
        int off = 0;
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if(off==0){
            Toast.makeText(getApplicationContext(), "Aby aplikacja dziłała poprawnie musisz włączyć lokalizację", Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, AskToTurnOnLocationServices.class);
            startActivity(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        setContentView(R.layout.activity_setting_the_destination);

        loadComponents();
        loadDestinationsFile();
        search("A");
    }

    private void textAreaEvent() {
        //obsługa pola tekstowego

        if (nameToSearch != null) {
            nameToSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if(s.length() != 0)
                        search(String.valueOf(nameToSearch.getText()));
                }
            });
            nameToSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (v == nameToSearch) {
                        if (!hasFocus) {
                            // Close keyboard
                            ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                                    .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                        }
                    }
                }
            });
        }
    }

    private void sliderEvent() {
        //obsługa suwaka
        SeekBar seekBar = (SeekBar)findViewById(R.id.dystansSeekBar);
        final TextView seekBarValue = (TextView)findViewById(R.id.dystansTekst);
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    seekBarValue.setText("Obudź mnie " + String.valueOf(progress) + " km przed celem");
                    distanceToStartRinging = seekBar.getProgress();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

    private void loadDestinationsFile() {
        //wczytywanie pliku z danymi miast
        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.danepkp);

            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            String dane = new String(b);

            Scanner scanner = new Scanner(dane);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.length()>4){
                    try {
                    String[] separated = line.split(";");
                    Destination destination = new Destination();
                        destination.Name = separated[0];
                        destination.Longitude = new Double(separated[1].trim());
                        destination.Latitude = new Double(separated[2].trim());
                        listOfDestinations.add(destination);
                    }catch (Exception e) {

                    }
                }
            }
            scanner.close();

        } catch (Exception e) {
            makeToast(e.toString());
        }
    }



    private void loadComponents(){

        option1 = new SelectionOption(this, (TextView) findViewById(R.id.t1), (LinearLayout) findViewById(R.id.l1));
        option2 = new SelectionOption(this,(TextView) findViewById(R.id.t2),(LinearLayout) findViewById(R.id.l2));
        option3 = new SelectionOption(this,(TextView) findViewById(R.id.t3),(LinearLayout) findViewById(R.id.l3));
        option4 = new SelectionOption(this,(TextView) findViewById(R.id.t4),(LinearLayout) findViewById(R.id.l4));
        nameToSearch = (EditText)findViewById(R.id.poleMiasto);
        sliderEvent();
        textAreaEvent();

    }

    public void toAboutClick(View v){
            Intent i = new Intent(this, About.class);
            startActivity(i);
    }

    public void toNavigateClick(View v){
        if(choice != 0) {
            Intent i = new Intent(this, AlarmActive.class);
            i.putExtra("name", listOfChoice.get(choice - 1).Name);
            i.putExtra("longitude", listOfChoice.get(choice -1).Longitude);
            i.putExtra("latitude", listOfChoice.get(choice -1).Latitude);
            i.putExtra("distanceToRing", distanceToStartRinging);
            startActivity(i);
        }
    }

    public void onSearchClick(View v){
        nameToSearch.clearFocus();
        search(String.valueOf(nameToSearch.getText()));
    }

    public void makeToast(String wiadomosc){
        Toast.makeText(getApplicationContext(), wiadomosc, Toast.LENGTH_SHORT).show();
    }

    public void search(String s){
        listOfChoice.clear();
        //TODO odległość przy wyświetlaniu w wyszukiwarce
        for(int i = 0; i < listOfDestinations.size(); i++) {
            if (myNormalizer(listOfDestinations.get(i).Name).toLowerCase().startsWith(myNormalizer(s.toLowerCase())))
                listOfChoice.add(listOfDestinations.get(i));
            if(listOfChoice.size()==4) break;
        }
        listMaker(listOfChoice);

    }

    private String myNormalizer(String string){
        return string.replace('ą','a').replace('ć', 'c').replace('ę','e').replace('ł','l').replace('ń','n').
                replace('ó', 'o').replace('ś', 's').replace('ź','z').replace('ż','z');
    }

    public void listMaker(List<Destination> miasta){
        odznaczWszystkie();
        pokazWszystkie();

        if(miasta.size() < 1) {
            option1.setValue("Nie znaleziono :(");
            nothingFound = true;

        }
        else {
            nothingFound = false;
            option1.setValue(miasta.get(0).Name);
        }

        if (miasta.size() < 2) option2.invisible();
        else
        option2.setValue(miasta.get(1).Name);

        if (miasta.size() < 3) option3.invisible();
        else
        option3.setValue(miasta.get(2).Name);

        if (miasta.size() < 4) option4.invisible();
        else
        option4.setValue(miasta.get(3).Name);


    }
    public void odznaczWszystkie(){
        choice = 0;
        option1.uncheck();
        option2.uncheck();
        option3.uncheck();
        option4.uncheck();
    }
    public void pokazWszystkie(){
        option1.visible();
        option2.visible();
        option3.visible();
        option4.visible();
    }

    public void zaznaczenie(View v){
        if(!nothingFound) {
            nameToSearch.clearFocus();
            odznaczWszystkie();
            int id = v.getId();
            switch (id) {
                case R.id.l1:
                    option1.check();
                    choice = 1;
                    break;
                case R.id.l2:
                    option2.check();
                    choice = 2;
                    break;
                case R.id.l3:
                    option3.check();
                    choice = 3;
                    break;
                case R.id.l4:
                    option4.check();
                    choice = 4;
                    break;
            }
        }
    }


}
