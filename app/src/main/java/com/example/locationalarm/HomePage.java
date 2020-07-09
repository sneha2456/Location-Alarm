package com.example.locationalarm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.locationalarm.AddTask;
import com.example.locationalarm.R;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity {

    ListView listViewRemindersList;
    ArrayList arrayListName = new ArrayList();
    ArrayList arrayListDesc = new ArrayList();
    ArrayList<Integer> arrayListReminderId=new ArrayList();
    ArrayList arrayListStatus=new ArrayList();
    Databasehelper databaseHelper;
    Cursor cursor;
    View rootview;
    //Button btnviewAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
       // Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar_top);
//        toolbar.setTitle("HOME PAGE");
//        toolbar.setNavigationIcon(R.drawable.quantum_ic_arrow_back_grey600_24);
//         toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 startActivity(new Intent(getApplicationContext(),AddTask.class));
//             }
//         });
//        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_top);
//        toolbar.setTitle("HOME PAGE");
//        toolbar.setNavigationIcon(R.drawable.quantum_ic_arrow_back_grey600_24);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(),AddTask.class));
//            }
//        });

        databaseHelper = new Databasehelper(this);
        listViewRemindersList = (ListView)findViewById(R.id.listViewReminders);

        setupList();



        listViewRemindersList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int rem = arrayListReminderId.get(position);
                Intent intent = new Intent(HomePage.this,ViewRemainder.class);
                intent.putExtra("Remid",rem);
                startActivity(intent);
            }
        });


        //toast for trial afterwards remove this
//        btnviewAll = findViewById(R.id.button);
//       viewAll();

    }

//    private void viewAll() {
//        btnviewAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Cursor cursor = databaseHelper.getRemainderData();
//                if(cursor.getCount()==0){
//                    //show message
//                    showMessage("Error","Nothing found");
//                    return;
//                }
//                StringBuffer buffer = new StringBuffer();
//                while(cursor.moveToNext())
//                {
//                    buffer.append("id"+cursor.getString(0)+"\n");
//                    buffer.append("title"+cursor.getString(1)+"\n");
//                    buffer.append("latitude"+cursor.getString(2)+"\n");
//                    buffer.append("longitude"+cursor.getString(3)+"\n");
//                    buffer.append("Description"+cursor.getString(4)+"\n");
//
//                }
//                //show all data
//                showMessage("Data",buffer.toString());
//            }
//        });
//    }
//
//    public void showMessage(String title,String Message)
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(true);
//        builder.setTitle(title);
//        builder.setMessage(Message);
//        builder.show();
//    }

    //upto this try part


    public  void onResume()
    {
        setupList();
        super.onResume();
    }

    private void setupList() {
        cursor = databaseHelper.getRemainderData();
        if(cursor.getCount()==0)
            findViewById(R.id.textViewNoTodo).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.textViewNoTodo).setVisibility(View.GONE);

        if(!arrayListName.isEmpty()|| !arrayListDesc.isEmpty())
        {
            arrayListReminderId .clear();
            arrayListName.clear();
            arrayListDesc.clear();
            arrayListStatus.clear();
        }

        while(cursor.moveToNext()){
            arrayListReminderId.add(Integer.valueOf(cursor.getInt(0)));
            arrayListName.add(cursor.getString(1));
            arrayListDesc.add(cursor.getString(4));
            arrayListStatus.add(cursor.getString(5));
        }


        CustomeAdapter customeAdapter = new CustomeAdapter();
        Log.d("AdaptorCheck","adaptor count"+customeAdapter.getCount());
        listViewRemindersList.setAdapter(customeAdapter);
    }

    class CustomeAdapter extends BaseAdapter{

        @Override
        public int getCount() {

            return cursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.remainder_list_item,null);
            TextView textViewRemainderTitle = (TextView)convertView.findViewById(R.id.textViewReminderTitle);
            TextView textViewRemainderDesc = (TextView)convertView.findViewById(R.id.textViewReminderDesc);
            SwitchCompat toggleRemainder = (SwitchCompat)convertView.findViewById(R.id.toogleReminder);
            textViewRemainderTitle.setText("Task: "+ arrayListName.get(position).toString());

            Log.d("taskcheck","task value"+arrayListName.get(position).toString());

            textViewRemainderDesc.setText(arrayListDesc.get(position).toString());

            if(arrayListStatus.get(position).equals("true"))
            {
                toggleRemainder.setChecked(true);
                textViewRemainderTitle.setTextColor(Color.WHITE);
            }
            else if(arrayListStatus.get(position).equals("false"))
            {
                toggleRemainder.setChecked(false);
                textViewRemainderTitle.setTextColor(Color.rgb(183,187,215));
            }

            toggleRemainder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                    {
                        textViewRemainderTitle.setTextColor(Color.WHITE);
                        databaseHelper.updateStatus(arrayListReminderId.get(position),"true");
                        arrayListStatus.add(position,true);
                        Log.e("arrayStatus at:" +position," "+arrayListStatus.get(position));
                    }

                    else if(!isChecked)
                    {
                        textViewRemainderTitle.setTextColor(Color.rgb(183,187,215));
                        databaseHelper.updateStatus(arrayListReminderId.get(position),"false");
                        arrayListStatus.add(position,"false");
                        Log.e("arrayStatus at:" +position," "+arrayListStatus.get(position));
                    }
                }
            });
            return convertView;
        }
    }


    public void btnclick(View view) {

        Intent intent = new Intent(getApplicationContext(), AddTask.class);
        startActivity(intent);
    }

    public void mapclick(View view) {
        Intent intent = new Intent(getApplicationContext(), AddTask.class);
        startActivity(intent);
    }
}
