package alan.tool.volumnmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    protected static final int PROGRESS_CHANGED = 0;
    private static final String RING_VOLUME = "ring_volume";
    private static final String MEDIA_VOLUME = "media_volume";
    private static final String CALL_VOLUME = "call_volume";
    private static final String ALARM_VOLUME = "alarm_volume";
    protected Handler handler;
    private int selectedPreference = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSeekBars();
        initButtons();

        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case PROGRESS_CHANGED:
                        initSeekBars();
                        break;
                }
            }
        };

        ((Button) findViewById(R.id.button_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile(String.valueOf(selectedPreference));
            }
        });

        ((Button) findViewById(R.id.button_load)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadProfile(String.valueOf(selectedPreference));
            }
        });

        initUiUpdateThread();
    }

    protected void initButtons() {

        final Button btnLoad = (Button) findViewById(R.id.button_load);
        btnLoad.setEnabled(false);

        final Button btnSave = (Button) findViewById(R.id.button_save);
        btnSave.setEnabled(false);

        RadioGroup preferenceGroup = (RadioGroup) findViewById(R.id.radiogroup_preferences);
        preferenceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                btnLoad.setEnabled(true);
                btnSave.setEnabled(true);

                selectedPreference = checkedId;
            }
        });
    }

    protected void saveProfile(String profile) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int ringVol = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        int mediaVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int callVol = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        int alarmVol = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);

        SharedPreferences pref = getSharedPreferences(profile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(RING_VOLUME, ringVol);
        editor.putInt(MEDIA_VOLUME, mediaVol);
        editor.putInt(CALL_VOLUME, callVol);
        editor.putInt(ALARM_VOLUME, alarmVol);

        editor.commit();
    }

    protected void loadProfile(String profile) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        SharedPreferences pref = getSharedPreferences(profile, Context.MODE_PRIVATE);
        int ringVol = pref.getInt(RING_VOLUME, 0);
        int mediaVol = pref.getInt(MEDIA_VOLUME, 0);
        int callVol = pref.getInt(CALL_VOLUME, 0);
        int alarmVol = pref.getInt(ALARM_VOLUME, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, ringVol, AudioManager.FLAG_VIBRATE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mediaVol, AudioManager.FLAG_VIBRATE);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, callVol, AudioManager.FLAG_VIBRATE);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, alarmVol, AudioManager.FLAG_VIBRATE);
    }

    protected void initSeekBars() {
        SeekBar seekbarRingVol = (SeekBar) findViewById(R.id.seekbar_ring_vol);
        SeekBar seekbarMediaVol = (SeekBar) findViewById(R.id.seekbar_media_vol);
        SeekBar seekbarCallVol = (SeekBar) findViewById(R.id.seekbar_call_vol);
        SeekBar seekbarAlarmVol = (SeekBar) findViewById(R.id.seekbar_alarm_vol);

        initSeekBar(seekbarRingVol, AudioManager.STREAM_RING, R.id.textview_current_ring_vol);
        initSeekBar(seekbarMediaVol, AudioManager.STREAM_MUSIC, R.id.textview_current_media_vol);
        initSeekBar(seekbarCallVol, AudioManager.STREAM_VOICE_CALL, R.id.textview_current_call_vol);
        initSeekBar(seekbarAlarmVol, AudioManager.STREAM_ALARM, R.id.textview_current_alarm_vol);
    }

    protected void initSeekBar(SeekBar seekBar, final int streamType, int labelId) {
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        final int maxVol = audioManager.getStreamMaxVolume(streamType);
        seekBar.setMax(maxVol);

        final int currentVol = audioManager.getStreamVolume(streamType);
        seekBar.setProgress(currentVol);

        final TextView volLabel = (TextView) findViewById(labelId);
        volLabel.setText(currentVol + " / " + maxVol);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(streamType, progress, AudioManager.FLAG_PLAY_SOUND);
                volLabel.setText(progress + " / " + maxVol);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });
    }

    private void initUiUpdateThread() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted()) {
                            Message msg = new Message();
                            msg.what = PROGRESS_CHANGED;

                            MainActivity.this.handler.sendMessage(msg);

                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }).start();
    }
}
