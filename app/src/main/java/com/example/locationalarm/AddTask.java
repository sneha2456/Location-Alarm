package com.example.locationalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class AddTask extends AppCompatActivity {
    GridView gridView;
    String[] taskname={"Meet","Buy Groceries","Pickup","Bank","Laundry","Food","Buy Clothes","Shopping","Others"};
    int[] images={R.drawable.conversation,R.drawable.groceries,R.drawable.car,R.drawable.cost,R.drawable.laundry,R.drawable.pasta,R.drawable.shoes,R.drawable.shoppingbag,R.drawable.other};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_top);
        toolbar.setTitle("ADD TASK");
        //toolbar.setNavigationIcon(R.drawable.quantum_ic_arrow_back_grey600_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),HomePage.class));
            }
        });

        gridView =findViewById(R.id.gridView);

        CustomAdapter customerAdapter=new CustomAdapter();
        gridView.setAdapter(customerAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?>  adapterView, View view, int i, long l) {
                Intent intent=new Intent(getApplicationContext(),enter_details.class);
                intent.putExtra("name",taskname[i]);
                intent.putExtra( "image",images[i]);
                startActivity(intent);
            }
        });


    }

    public void addclick(View view) {
        Intent intent = new Intent(getApplicationContext(), enter_details.class);
        startActivity(intent);
    }


    private class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1=getLayoutInflater().inflate(R.layout.activity_row_data,null);
            TextView name = view1.findViewById(R.id.taskname);
            ImageView image = view1.findViewById(R.id.images);
            name.setText(taskname[i]);
            image.setImageResource(images[i]);
            return view1;
        }

    }
}


