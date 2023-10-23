package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity4 extends AppCompatActivity {

    TextView textViewBitsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_4);

        textViewBitsArray = (TextView) findViewById(R.id.textViewArray);

//        textViewBitsArray.setText("Bits Array: "+getIntent().getIntegerArrayListExtra("BITS ARRAY"));
//        ArrayList<Integer> aList = (ArrayList<Integer>) getIntent().getSerializableExtra("BITS ARRAY");
//        textViewBitsArray.setText(String.valueOf(aList));
    }
}

