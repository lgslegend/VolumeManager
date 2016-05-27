package alan.tool.volumnmanager;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSetMaxVol = (Button) findViewById(R.id.button_set_max_vol);
        btnSetMaxVol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioManager audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audioMgr.setStreamVolume(AudioManager.STREAM_RING, audioMgr.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_SHOW_UI);
            }
        });

        SeekBar seekbarRingVol = (SeekBar) findViewById(R.id.seekbar_ring_vol);
        SeekBar seekbarMediaVol = (SeekBar) findViewById(R.id.seekbar_media_vol);
        SeekBar seekbarCallVol = (SeekBar) findViewById(R.id.seekbar_call_vol);
        SeekBar seekbarAlarmVol = (SeekBar) findViewById(R.id.seekbar_alarm_vol);
    }

    protected void setVolumn() {

    }
}
