package com.example.locationalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class enter_details extends AppCompatActivity {

    EditText name,desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_details);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar_top);
        toolbar.setTitle("ENTER DETAILS");
        //toolbar.setNavigationIcon(R.drawable.quantum_ic_arrow_back_grey600_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AddTask.class));
            }
        });

        name=findViewById(R.id.griddata);
        desc=findViewById(R.id.extratips);


        Intent intent=getIntent();
        //name.setHint(intent.getStringExtra("name"));
        name.setText(intent.getStringExtra("name"));
    }

    public void geolocation(View view) {
        Intent intent=new Intent(enter_details.this,mapactivity.class);
        intent.putExtra("Col_2",name.getText().toString());
        intent.putExtra("Col_5",desc.getText().toString());
        startActivityForResult(intent,1);
        startActivity(intent);

    }


}