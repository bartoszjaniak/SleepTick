package pl.aaronrose.bj.sleeptick;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;

//Klasa odpowiedzialna za obsługę alarmu
public class Alarm {
    static public boolean isRinging;
    static private MediaPlayer mediaPlayer;
    static private Vibrator vibrator;

    //TODO odblokowanie ekranu i wznowienie aplikacji
    static public void startRing(Context context){
        if(!isRinging) {
            isRinging = true;
            vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

            mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
            mediaPlayer.start();

            new Thread(new Runnable() {
                public void run(){
                    while (isRinging){
                        vibrator.vibrate(1000);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();


        }
    }

    static public void stopRing(Context context){
        if(isRinging) {
            isRinging = false;
            vibrator.cancel();
            mediaPlayer.stop();
        }
    }
}
