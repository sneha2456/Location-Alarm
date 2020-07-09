package com.example.locationalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class Notification extends AppCompatActivity {

    Ringtone ringtone;
    Uri uriAlarm;
    TextView title,Desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Intent intent = getIntent();
        Button offbtn = (Button)findViewById(R.id.offbtn);
        title = (TextView) findViewById(R.id.textViewAlarmTitle);
        Desc = (TextView) findViewById(R.id.textViewAlarmDesc);
        title.setText(intent.getStringExtra("Title"));
        Desc.setText(intent.getStringExtra("Description"));

        uriAlarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(),uriAlarm);
        ringtone.play();
        Toast.makeText(this,"uri :" + uriAlarm.toString(),Toast.LENGTH_SHORT).show();
        offbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ringtone != null){
                    ringtone.stop();
                    ringtone=null;
                }
                setResult(12);
                finish();
            }
        });
    }
}
