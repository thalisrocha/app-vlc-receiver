package com.example.myapplication;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class MainActivity3 extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    ArrayList<String> list;
    private static final String FILE_NAME = "example.txt";
    private static final ArrayList<Integer> samplesVector = new ArrayList<>();
    CameraBridgeViewBase cameraBridgeViewBase;

    Mat mat1, mat2, mat3, matNT, mat;
    BaseLoaderCallback baseLoaderCallback;

    TextView BitsValuesTextView;
    public Context context;

    // -------------------------------------------------------------
    private static class MyHandler extends Handler {}
    private final MyHandler mHandler = new MyHandler();

    public static class MyRunnable implements Runnable {
        private final WeakReference<Activity> mActivity;

        public MyRunnable(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            Activity activity = mActivity.get();
            if (activity != null) {
                Button btn = (Button) activity.findViewById(R.id.button);
                //btn.setBackgroundResource(R.drawable.defaultcard);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

//    boolean avisado = false, avisado2 = false;
//    int realSize, FPScounter=0;
//    int[] DataArray;
//    String DataString, stringAll = "", stringResp = "", stringPres = "";
//    char nextChar;

//    int frames_counter=0;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_3);
        cameraBridgeViewBase=(JavaCameraView)findViewById(R.id.myCameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);

        //..initializes the button
        Button btnBack2 = findViewById(R.id.btnBack);
        //..set what happens when the user clicks
        btnBack2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(),"Stopping...",Toast.LENGTH_LONG).show();
                Intent Ac3 = new Intent(MainActivity3.this, MainActivity2.class);
                startActivity(Ac3);
                finish();
            }
        });

        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {

                switch (status){
                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
    }

    int addFile=0;
    int nPacks = 0;
    int nBits = 0;
    int nFrames = 0;
    final ArrayList<ArrayList<List<Integer>>> finalSamples = new ArrayList<ArrayList<List<Integer>>>();
    long avrgTime = 0;
    long avrgOut = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        long startTime = System.nanoTime();
        // OnCameraFrame is called to receive a frame, processing
        // and returning it to the application.
        nFrames++;
//        frames_counter = frames_counter+1; // for tests

        final ArrayList<String> aList = new ArrayList<>();

        matNT = inputFrame.gray();
        //mat1 = mat.clone();
        mat = matNT.t();
        Core.flip(matNT.t(), mat, 1);
        Imgproc.resize(mat, mat, matNT.size());

        // Creating a grayscale bitmap
        final Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);

        Size s = mat.size();
        double  colAmount = s.width;
        double  rowAmount = s.height;

        int rowAmountInt = (int) rowAmount;
        int colAmountInt = (int) colAmount;

        int window = 120;
        Vector<Integer> pixels = new Vector<>();
        int pixel = 0;
        int [][] img = new int [2*window+rowAmountInt][2*window+colAmountInt];
        Arrays.stream(img).forEach(a -> Arrays.fill(a, 0));
        int [][] imgAux = new int [2*window+rowAmountInt][2*window+colAmountInt];
        Arrays.stream(imgAux).forEach(a -> Arrays.fill(a, 0));
        int data[] = new int [colAmountInt*rowAmountInt];
        int dataIndex = 0;

        for (int row=0; row< rowAmountInt; row++) {
          for (int col = 0; col < colAmountInt; col++) {
                pixel = bitmap.getPixel(col, row);
                pixels.add(pixel);
                //int flagEnd = 0;
                int p = pixels.get(dataIndex);
                int r = (p & 0xff0000) >> 16;
                data[dataIndex] = r;
                dataIndex++;
                img[row+window][col+window] = r;
                imgAux[row+window][col+window] = r;
          }
        }

        int avrg = 0;
        for (int row=405; row<410; row++) {
            for (int col = 0; col < colAmountInt+window; col++) {
                int sumElements = 0;
                int yKernel = 0;
                int xKernel = 0;
                for (int i = row - window/2; i < row + window/2; i++) {
                    for (int j = col; j < col+window; j++) {
                        int x = i - (row - window/2);
                        int y = j - (col);
                        sumElements = sumElements + img[i][j];
                        if (x == window/2 && y == window/2) {
                            xKernel = i;
                            yKernel = j;
                        }
                    }
                }
                avrg = sumElements / (window*window);
                pixel = img[xKernel][yKernel];
                if (pixel > avrg) {
                    img[xKernel][yKernel] = 255;
                } else {
                    img[xKernel][yKernel] = 0;
                }
            }
        }

        int aux = 0;
        ArrayList<Integer> indexZero = new ArrayList<Integer>();
        ArrayList<Integer> indexOne = new ArrayList<Integer>();
        ArrayList<Integer> index = new ArrayList<Integer>();
        for(int j=window; j<colAmountInt+window; j++){
            if(aux>=10){
                aux++;
                if(img[407][j]==0){
                    indexZero.add(j); //beginning
                    indexOne.add(j);  // end
                    index.add(j);
                    aux=0;
                }
            }else if(img[407][j]==255 && img[407][j+1]==255){
                if(j>0 && aux==0){
                    indexZero.add(j-1); //end
                }else if(aux==9){
                    indexOne.add(j-9); // beginning
                    index.add(j-9);
                }
                aux++;
            }else{
                aux=0;
            }
        }
        final ArrayList<Integer> rangesZero = new ArrayList<>();
        final ArrayList<Integer> rangesOne = new ArrayList<>();
        if ((indexOne.size()^1) != indexOne.size()+1){
            indexOne.remove(indexOne.size()-1);
        }
        if ((indexZero.size()^1) != indexZero.size()+1){
            indexZero.remove(indexZero.size()-1);
        }

        if ((index.size()^1) == index.size()+1){
            index.add(index.size()-1);
        }
        for(int i=0; i<index.size(); i+=2){
            if(i+1 <index.size()){
                rangesOne.add(index.get(i + 1) - index.get(i));
                rangesZero.add(index.get(i + 2) - index.get(i + 1));
            }
        }

        //Log.d("indices", "indexOne: " + indexOne);
        //Log.d("indices", "indexZero: " + indexZero);
        Log.d("indices", "index: " + index);
        Log.d("ranges", "rangesOne: " + rangesOne);
        Log.d("ranges", "rangesZero: " + rangesZero);

        int lim=0;

        if(rangesOne.size()<rangesZero.size()){

            lim = rangesOne.size();
        }else{
            lim = rangesZero.size();
        }
        final ArrayList<Integer> output = new ArrayList<>();


        for(int i=0; i<lim; i++){
            if(rangesOne.get(i) <=34){
                output.add(1);
            }else if(rangesOne.get(i)>=37 && rangesOne.get(i)<=47){
                output.add(1);
                output.add(1);
            }else if(rangesOne.get(i)>=55 && rangesOne.get(i)<=69){
                output.add(1);
                output.add(1);
                output.add(1);
            }else if(rangesOne.get(i)>=77 && rangesOne.get(i)<=89){
                output.add(1);
                output.add(1);
                output.add(1);
                output.add(1);
            }else if(rangesOne.get(i)>=99 && rangesOne.get(i)<=108){
                output.add(1);
                output.add(1);
                output.add(1);
                output.add(1);
                output.add(1);
            }else{
                //output.add("rangesOne("+ i +")");
            }
            if(rangesZero.get(i) <=34){
                output.add(0);
            }else if(rangesZero.get(i)>=35 && rangesZero.get(i)<=48){
                output.add(0);
                output.add(0);
            }else if(rangesZero.get(i)>=49 && rangesZero.get(i)<=63){
                output.add(0);
                output.add(0);
                output.add(0);
            }else if(rangesZero.get(i)>=80 && rangesZero.get(i)<=89){
                output.add(0);
                output.add(0);
                output.add(0);
                output.add(0);
            }else if(rangesZero.get(i)>=99 && rangesZero.get(i)<=104){
                output.add(0);
                output.add(0);
                output.add(0);
                output.add(0);
                output.add(0);
            }else{
                //output.add("rangesZero("+ i +")");
            }
        }



        //T = 166,6667 ms - Transmitter
        /*
        for(int i=0; i<lim; i++) {
            if (rangesOne.get(i) <= 18) {
                output.add(1);
            } else if (rangesOne.get(i) >= 100 && rangesOne.get(i) <= 150) {
                output.add(1);
                output.add(1);
            } else if (rangesOne.get(i) >= 100 && rangesOne.get(i) <= 150) {
                output.add(1);
                output.add(1);
                output.add(1);
            } else if (rangesOne.get(i) >= 35 && rangesOne.get(i) <= 38) {
                output.add(1);
                output.add(1);
                output.add(1);
                output.add(1);
            } else if (rangesOne.get(i) >= 99 && rangesOne.get(i) <= 108) {
                output.add(1);
                output.add(1);
                output.add(1);
                output.add(1);
                output.add(1);
            } else {
                //output.add("rangesOne("+ i +")");
            }
            if (rangesZero.get(i) <= 9) {
                output.add(0);
            } else if (rangesZero.get(i) >= 12 && rangesZero.get(i) <= 16) {
                output.add(0);
                output.add(0);
            } else if (rangesZero.get(i) >= 17 && rangesZero.get(i) <= 20) {
                output.add(0);
                output.add(0);
                output.add(0);
            } else if (rangesZero.get(i) >= 100 && rangesZero.get(i) <= 150) {
                output.add(0);
                output.add(0);
                output.add(0);
                output.add(0);
            } else if (rangesZero.get(i) >= 100 && rangesZero.get(i) <= 150) {
                output.add(0);
                output.add(0);
                output.add(0);
                output.add(0);
                output.add(0);
            } else {
                //output.add("rangesZero("+ i +")");
            }
        }

         */

        //T = 500 ms - Transmitter

        /*
        for(int i=0; i<lim; i++) {
            if (rangesOne.get(i) <= 17) {
                output.add(1);
            } else if (rangesOne.get(i) >= 100 && rangesOne.get(i) <= 150) {
                output.add(1);
                output.add(1);
            } else if (rangesOne.get(i) >= 100 && rangesOne.get(i) <= 150) {
                output.add(1);
                output.add(1);
                output.add(1);
            } else if (rangesOne.get(i) >= 47 && rangesOne.get(i) <= 49) {
                output.add(1);
                output.add(1);
                output.add(1);
                output.add(1);
            } else if (rangesOne.get(i) >= 99 && rangesOne.get(i) <= 108) {
                output.add(1);
                output.add(1);
                output.add(1);
                output.add(1);
                output.add(1);
            } else {
                //output.add("rangesOne("+ i +")");
            }
            if (rangesZero.get(i) <= 13) {
                output.add(0);
            } else if (rangesZero.get(i) >= 21 && rangesZero.get(i) <= 24) {
                output.add(0);
                output.add(0);
            } else if (rangesZero.get(i) >= 31 && rangesZero.get(i) <= 35) {
                output.add(0);
                output.add(0);
                output.add(0);
            } else if (rangesZero.get(i) >= 100 && rangesZero.get(i) <= 150) {
                output.add(0);
                output.add(0);
                output.add(0);
                output.add(0);
            } else if (rangesZero.get(i) >= 100 && rangesZero.get(i) <= 150) {
                output.add(0);
                output.add(0);
                output.add(0);
                output.add(0);
                output.add(0);
            } else {
                //output.add("rangesZero("+ i +")");
            }
        }

         */


        Collections.reverse(output);

        Log.d("output", "Output: " + output);

        List<Integer> header = Arrays.asList(0,1,0,0,0,1,0,0,1,1,1,1,0,1,0,1);

        final ArrayList<List<Integer>> finalData = new ArrayList<>();
        for(int i=0; i<output.size(); i++){
            if((output.size()-i > 16)
                    && output.get(i)==header.get(0) && output.get(i+1)==header.get(1)
                    && output.get(i+2)==header.get(2) && output.get(i+3)==header.get(3)
                    && output.get(i+4)==header.get(4) && output.get(i+5)==header.get(5)
                    && output.get(i+6)==header.get(6) && output.get(i+7)==header.get(7)
                    && output.get(i+8)==header.get(8) && output.get(i+9)==header.get(9)
                    && output.get(i+10)==header.get(10) && output.get(i+11)==header.get(11)
                    && output.get(i+12)==header.get(12) && output.get(i+13)==header.get(13)
                    && output.get(i+14)==header.get(14) && output.get(i+15)==header.get(15)){
                //if(output.size()-(i+7)>=8){
                        finalData.add(output.subList(i, i+16));
                        i+=15;
                        nPacks++;
                        nBits = nPacks*16;
                //}

            }
        }

        Log.d("output", "Output: " + finalData);
        Log.d("N Packs", "nPacks: " + nPacks);

        finalSamples.add(finalData);
        //saveSamples(Arrays.deepToString(imgAux));
        //createSamplesTxt(Arrays.deepToString(imgAux));
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000;  //divide by 1000000 to get milliseconds.
        avrgTime+=duration;

        Log.d("Time", "frameTime: " + duration);
        Log.d("Frame", "frameQtd: " + nFrames);
        return mat;
    }

    @Override
    public void onCameraViewStopped(){
        mat1.release();
        mat2.release();
        mat3.release();
        mat.release();
        matNT.release();
    }

    @Override
    public void onCameraViewStarted(int width, int height){
        mat1 = new Mat(width,height, CvType.CV_8UC4);
        mat2 = new Mat(width,height,CvType.CV_8UC4);
        mat3 = new Mat(width,height,CvType.CV_8UC4);
        matNT = new Mat(height,width,CvType.CV_8UC4);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(), "There is a problem in openCV", Toast.LENGTH_SHORT).show();
        }
        else{
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(cameraBridgeViewBase!=null)
        {
            cameraBridgeViewBase.disableView();
        }
    }

    String filename = "myfile";
    String filepath = "MyFileDir";
    public void saveSamples(String samples){
        addFile++;
        File root = new File(getExternalFilesDir(filepath), (filename + addFile+".txt"));
        //File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS );
        //File root = getApplicationContext().getFilesDir();
        //File root = new File("/SD card/Android/data/samples.txt");
        //File root = context.getFilesDir();
        //if you want to create a sub-dir
        //root = new File(root, "SubDirVLC");
        //root.mkdir();

        // select the name for your file
        //root = new File(root , "mycsv.txt");
        FileOutputStream fileout= null;

        try {
            fileout = new FileOutputStream(root);
            //OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            //outputWriter.write(samples);
            //outputWriter.close();
            fileout.write(samples.getBytes());
            //display file saved message
            //Toast.makeText(getBaseContext(), "File saved successfully!",
            //        Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createSamplesTxt(String sample)
    {
        FileReader fr = null;
        File myExternalFile = new File(getExternalFilesDir(filepath), (filename+addFile+".txt"));
        StringBuilder stringBuilder = new StringBuilder();
        try
        {
            fr = new FileReader(myExternalFile);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while(line != null){
                // Append the line read to StringBuilder object. Also, append a new-line
                stringBuilder.append(line).append('\n');
                // Again read the next line and store in variable line
                line = br.readLine();
            }
            /*FileOutputStream fos = openFileOutput("samples.txt", Context.MODE_PRIVATE);
            fos.close();
            File f = new File("samples.txt");
            f.delete();

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("samples.txt",
                    Context.MODE_PRIVATE));
            outputStreamWriter.write(sample);
            outputStreamWriter.close();
            Toast.makeText(MainActivity3.this, "Saved your text", Toast.LENGTH_LONG).show();
        */
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } finally {
            // Convert the StringBuilder content into String and add text "File contents\n"
            // at the beginning.
            String fileContents = "File contents\n" + stringBuilder.toString();
            // Set the TextView with fileContents
            //tvLoad.setText(fileContents);
        }
    }

    /*
    public void save(String p){
        FileOutputStream fos = null;

        try {
            fos= openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(p.getBytes());

            //Toast.makeText(this, "Saved to"+ getFilesDir() +"/"+ FILE_NAME, Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
     */

    /*private void WriteFile(String p){
        File file = new File(MainActivity3.this.getFilesDir(), "text");
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            //final TextView output = findViewById(R.id.output);
            File gpxfile = new File(file, "sample");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(p);
            writer.flush();
            writer.close();
            setText(readFile());
            Toast.makeText(MainActivity3.this, "Saved your text", Toast.LENGTH_LONG).show();
        } catch (Exception e) { }
    }

    private String readFile() {
        File fileEvents = new File(MainActivity3.this.getFilesDir() + "/text/sample");
        fileEvents = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        fileEvents = new File(fileEvents, "SubDirVLC");
        fileEvents.mkdir();

        // select the name for your file
        fileEvents = new File(fileEvents , "my_csv.txt");

        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) { }
        String result = Arrays.toString(new ArrayList[]{list});
        return result;
    }*/

}

