package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Uri imageUri;
    Bitmap grayBitmap,imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);

        OpenCVLoader.initDebug();

        //..initializes the buttons
        Button btnBack = findViewById(R.id.button3);
        //..set what happens when the user clicks
        btnBack.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //Toast.makeText(getApplicationContext(),"Starting...",Toast.LENGTH_LONG).show();
                Intent Ac3 = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(Ac3);
                finish();
            }
        });
    }

    public void openGallery(View v)
    {
        Intent myIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(myIntent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageView.setImageBitmap(imageBitmap);
        }
    }

    public void convertToGray(View v)
    {
        Mat Rgba = new Mat();
        Mat grayMat = new Mat();

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inDither=false;
        o.inSampleSize=1;

        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();
        Log.d("convertToGreyTag", "Width " + width);
        Log.d("convertToGreyTag", "Height " + height);

        grayBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);

        // bitmap to MAT

        Utils.bitmapToMat(imageBitmap,Rgba);

        Imgproc.cvtColor(Rgba,grayMat,Imgproc.COLOR_RGB2GRAY);

        Utils.matToBitmap(grayMat,grayBitmap);

        imageView.setImageBitmap(grayBitmap);

        getValues(grayMat);
    }

    public void getValues(Mat mat){
        Size s = mat.size();
        double  colAmount = s.width;
        double  rowAmount = s.height;

        int rowAmountInt = (int) rowAmount;
        int middleRow = rowAmountInt/2;

        int colAmountInt = (int) colAmount;

        double positionValueDouble;
        int[] positionValueArray = new int[mat.cols()];
        int thresholdValueCounter = 0;

        for (int col=0; col<colAmountInt; col++) {
            double[] positionValue = mat.get(middleRow,col);

//            Log.d("getValuesTag", "Column: " + col);
//            Log.d("getValuesTag", "Value: " + Arrays.toString(positionValue));

            positionValueDouble = positionValue[0];
            int positionValueInt = (int) positionValueDouble;
            positionValueArray[col] = positionValueInt;
            thresholdValueCounter += positionValueInt;

//                double thresholdValue, positionValueDouble;
//                thresholdValue = 170;
//                positionValueDouble = positionValue[0];
//
//                int sectionAmount = 40;
//                int amountPerSection = colAmount/sectionAmount;
//
//
//                int[] bitsArray = new int[mat.cols()];
//
//                if(positionValueDouble > thresholdValue){
//                    bitsArray[col] = 1;
//                }
//                else{
//                    bitsArray[col]=0;
//                }
//                Log.d("getValuesTagBit", "Column: " + col);
//                Log.d("getValuesTagBit", "Value: " + positionValueArray[col]);
        }

        int sectionAmount = 32; // how many bits are expected from one frame
        int amountPerSection = colAmountInt/sectionAmount;
        int halfOfAmountPerSection = amountPerSection/2;
        int[] bitsArray = new int[sectionAmount];
        int threshold = thresholdValueCounter/colAmountInt;
        //int threshold = 40;
        int bitsArrayOffset=0;

        for (int i = 0; i < colAmount-amountPerSection; i += amountPerSection) {
            if(positionValueArray[i+halfOfAmountPerSection] < threshold){
                bitsArray[bitsArrayOffset]=0;
                Log.d("getValues", "BIT 0: " + bitsArray[bitsArrayOffset]);
                //Log.d("getValues", "seção: " + bitsArrayOffset);
                bitsArrayOffset++;
            }else{
                bitsArray[bitsArrayOffset]=1;
                Log.d("getValues", "BIT 1: " + bitsArray[bitsArrayOffset]);
                //Log.d("getValues", "seção: " + bitsArrayOffset);
                bitsArrayOffset++;
            }
        }
    }
}
