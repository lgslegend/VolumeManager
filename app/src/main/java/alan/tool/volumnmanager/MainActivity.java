package alan.tool.volumnmanager;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
