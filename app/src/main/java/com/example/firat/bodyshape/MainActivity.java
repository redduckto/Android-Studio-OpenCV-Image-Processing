package com.example.firat.bodyshape;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firat.bodyshapeocv.R;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static java.lang.Math.round;
import static java.lang.System.*;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;
import static org.opencv.imgproc.Imgproc.COLOR_GRAY2RGBA;
import static org.opencv.imgproc.Imgproc.MORPH_OPEN;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.cvtColor;

import org.opencv.core.CvType;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView mTextMessage;
    private static final String TAG="MainActivity";
    static{
        if(OpenCVLoader.initDebug())
            Log.d(TAG,"Opencv Loaded");
        else
            Log.d(TAG, "Opencv NOT Loaded");
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    Button btn;
    Button btn2;
    ImageView iv;
    EditText edtTxt;
    EditText edtTxt2;
    Button btn3;
    TextView txtView;

    int a1, b1, c1, d1;
    int a2, b2, c2, d2;


    PinchZoomImageView mPinchZoomImageView;
    private static final int CALCULATE_REQUEST_CODE = 102;
    private static final int CAMERA_REQUEST_CODE = 100;
    // Request code for runtime permissions
    private final int REQUEST_CODE_PERMS = 321;
    private final int PICK_IMAGE = 101;

    Uri imageUri;
    Bitmap bitmapOCV;
    Bitmap bitmapGray;
    Mat gurk;

    int bust, waist, hHip, lHip;


    @Override
    public void onClick(View v)
    {
        if(v.getId()==R.id.button)
        {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                if (!hasPermissions()) {
                    // your app doesn't have permissions, ask for them.
                    requestNecessaryPermissions();
                } else {
                    // your app already have permissions allowed.
                    // do what you want.
                    dispatchTakePictureIntent();
                }


            } else {
                Toast.makeText(MainActivity.this, "Camera not supported", Toast.LENGTH_LONG).show();
            }
        }
        else if (v.getId()==R.id.button2)
        {
            	if (!hasPermissions()) {
                    // your app doesn't have permissions, ask for them.
                    requestNecessaryPermissions();
                } else {
                    // your app already have permissions allowed.
                    // do what you want.
                    openGallery();
                }
		
        }
        else if (v.getId()==R.id.button3 )
        {
            //TO DO here

            //OpenCV(bitmap;);
            //iv.setImageBitmap(OpenCV(bitmapOCV));

            //Calculate();
            bust = a1+a2;
            waist = b1+b2;
            hHip = c1+c2;
            lHip = d1+d2;
            txtView.setText(BodyShapeResult(bust, waist, hHip, lHip));
            //txtView.setText(gurk.get(300,300,));
            txtView.setVisibility(View.VISIBLE);




            //Toast.makeText(MainActivity.this, memed, Toast.LENGTH_SHORT).show();

            /*String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_OCV" + timeStamp + "_";
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmapOCV, imageFileName, "bodyShapeOCVPics");            burayı da çıkardım*/

        }

    }
	
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        edtTxt = findViewById(R.id.editText);
        edtTxt2 = findViewById(R.id.editText2);
        txtView = findViewById(R.id.textView4);

        iv = findViewById(R.id.imageView);
        mPinchZoomImageView = findViewById(R.id.pinchZoomImageView);

        btn.setOnClickListener(MainActivity.this);
        btn2.setOnClickListener(MainActivity.this);
        btn3.setOnClickListener(MainActivity.this);
        iv.setOnLongClickListener(new View.OnLongClickListener(){

            public boolean onLongClick(View v){
                Toast.makeText(getApplicationContext(), "Ready for new calculation. ", Toast.LENGTH_LONG).show();
                txtView.setVisibility(View.INVISIBLE);
                return true;
            }
        });


        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation =  findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.statistics:
                //TO DO here
                Toast.makeText(MainActivity.this, "Adjust the frame through neck to knee, focus on your belly button :)", Toast.LENGTH_LONG).show();
                return true;
            case R.id.about:
                Toast.makeText(MainActivity.this, "Version 1.00", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.contact:
                Toast.makeText(MainActivity.this, "Ege University Computer Engineering Assoc. Prof. Dr. Vecdi AYTAÇ", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean hasPermissions() {
        int res = 0;
        // list all permissions which you want to check are granted or not.
        String[] permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED) ){
                // it return false because your app doesn't have permissions.
                return false;
            }

        }

        // it return true, your app has permissions.
        return true;
    }

    private void requestNecessaryPermissions() {
        // make array of permissions which you want to ask from user.
        String[] permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // have arry for permissions to requestPermissions method.
            // and also send unique Request code.
            requestPermissions(permissions, REQUEST_CODE_PERMS);


        }
    }

    /* when user grant or deny permission then your app will check in
      onRequestPermissionsReqult about user's response. */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grandResults) {
        // this boolean will tell us that user granted permission or not.
        boolean allowed = true;
        switch (requestCode) {
            case REQUEST_CODE_PERMS:
                for (int res : grandResults) {
                    // if user granted all required permissions then 'allowed' will return true.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                // if user denied then 'allowed' return false.
                allowed = false;
                break;
        }
        if (allowed) {
            // if user granted permissions then do your work.
            dispatchTakePictureIntent();
        }
        else {
            // else give any custom waring message.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(MainActivity.this, "Camera Permissions denied", Toast.LENGTH_SHORT).show();
                }
                else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Toast.makeText(MainActivity.this, "Read Ex. Storage Permissions denied", Toast.LENGTH_SHORT).show();
                }
                else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(MainActivity.this, "Write Ex. Storage Permissions denied", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("lpl", ex.getMessage());
            }
            // Continue only if the File was successfully created
            String authorities = getApplicationContext().getPackageName() + ".fileprovider";

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        authorities,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }


    public void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        gallery.addCategory(Intent.CATEGORY_OPENABLE);
        gallery.setType("image/*");
        startActivityForResult(gallery, PICK_IMAGE);
    }


    public void Calculate(){
        Intent calc = new Intent(getBaseContext(), ShowCalculation.class);                       //getBaseContext yerinde this vardı
        calc.putExtra("key","OVAL");
        startActivity(calc);
        //startActivityForResult(calc,CALCULATE_REQUEST_CODE);
    }

    public String mCurrentPhotoPath;

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }



    private void setPic() {
        // Get the dimensions of the View
        int targetW = iv.getWidth();
        int targetH = iv.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        //bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        bitmapOCV=OpenCV(photoH, photoW, bitmap);
        //iv.setImageBitmap(bitmapOCV);
        //bitmapOCV=null;
        //bitmapOCV=bitmap;
        iv.setImageBitmap(bitmap);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, imageFileName , "bodyShapePics");
    }


    public File createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public Bitmap OpenCV(int height, int weight, Bitmap mBitmap) throws IllegalStateException {

        int ar, br, cr, dr;

         a1=0;
         b1=0;
         c1=0;
         d1=0;
        a2=0;
        b2=0;
        c2=0;
        d2=0;


        Bitmap result = null;
        try {

            Mat src, abc  ;
            abc = new Mat(mBitmap.getHeight(), mBitmap.getWidth(), CvType.CV_8UC4);
            src = new Mat(mBitmap.getHeight(), mBitmap.getWidth(), CvType.CV_8UC4);
            Utils.bitmapToMat(mBitmap, src);

            ar = abc.rows()*3/10;
            br = abc.rows()/2;
            cr = abc.rows()*7/12;
            dr = abc.rows()*3/4;

            result = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);

            Utils.matToBitmap(src, result);


            Mat dst ;
            dst = new Mat(mBitmap.getHeight(), mBitmap.getWidth(), CvType.CV_8UC4);


            Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2GRAY);

            Imgproc.blur(dst, abc, new Size(3, 3));
            Imgproc.threshold(abc, abc, 100, 255, Imgproc.THRESH_BINARY);



            //////?????????Imgproc.adaptiveThreshold(abc,abc,255,ADAPTIVE_THRESH_MEAN_C,THRESH_BINARY,15,5);
            ///Imgproc.adaptiveThreshold(abc, abc,255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY,15,5);
            //Mat kernel = new Mat(3,3, CvType.CV_8UC4);
            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(4,4));
            Mat kernel1 = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(2,2));
            Mat kernel2 = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(2,2));
            Point anchor = new Point(2,2);
            Point anchor1 = new Point(1,1);
            //int = abc.get(3, 3));
            //Mat kernel1 = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(15,15));
            //Imgproc.morphologyEx(abc, abc, Imgproc.MORPH_OPEN, kernel);
            //Imgproc.dilate(abc, abc, kernel);
            ////Imgproc.dilate(abc, abc, kernel, anchor, 1);
            ////Imgproc.morphologyEx(abc, abc, Imgproc.MORPH_OPEN, kernel, anchor, 15);
            //Imgproc.morphologyEx(abc, abc, Imgproc.MORPH_CLOSE, kernel);
            //Imgproc.morphologyEx(abc, abc, Imgproc.MORPH_OPEN, kernel);
            //////Imgproc.morphologyEx(abc, abc, Imgproc.MORPH_GRADIENT, kernel, anchor, 5);

       ///     Imgproc.morphologyEx(abc, abc, Imgproc.MORPH_DILATE, kernel, anchor, 5);
            Imgproc.morphologyEx(abc, abc, Imgproc.MORPH_ERODE, kernel1, anchor1, 1);
            Imgproc.morphologyEx(abc, abc, Imgproc.HOUGH_STANDARD, kernel, anchor, 8);
            Imgproc.morphologyEx(abc, abc, Imgproc.MORPH_ERODE, kernel1, anchor1, 1);
            Imgproc.adaptiveThreshold(abc, abc,255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY,15,5);

            double array[][] = new double[abc.height()][abc.width()];
            int array1[][] = new int[abc.height()][abc.width()];
            for (int i=0; i < abc.height(); i++)
            {
                for (int j=0; j < abc.width(); j++)
                {
                    array[i][j] = abc.get(i,j)[0];
                    array1[i][j] = (int)array[i][j] ;
                }
            }

            //txtView.setText(String.valueOf(abc.get(50,50)[0]));

            int ceval = Integer.valueOf(((int) abc.cols()));

            //Toast.makeText(MainActivity.this, Integer.toString(a1), Toast.LENGTH_LONG).show();
            gurk=abc;
            Imgproc.morphologyEx(abc, abc, Imgproc.MORPH_CLOSE, kernel2, anchor1, 1);
            ///Imgproc.erode(abc, abc, kernel, anchor, 1);
            //////Imgproc.morphologyEx(abc, abc, Imgproc.MORPH_OPEN, kernel, anchor, 5);
            //Imgproc.morphologyEx(abc, abc, Imgproc.MORPH_CLOSE, kernel, anchor, 10);
            //Imgproc.erode(abc, abc, kernel);
            //Imgproc.dilate(abc, abc, kernel);
            //Imgproc.adaptiveThreshold(abc,abc,255,ADAPTIVE_THRESH_GAUSSIAN_C,THRESH_BINARY,15,3);                     bu da fena değil
        /////    Imgproc.Canny(abc, abc, 50, 50 * 3, 3, false);
        ////    Imgproc.morphologyEx(abc, abc, Imgproc.MORPH_CLOSE, kernel, anchor, 5);
            //result = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888);
            result = Bitmap.createBitmap(abc.cols(), abc.rows(), Bitmap.Config.ARGB_8888);
            //Utils.matToBitmap(dst, result);
            for(int i=0;i<abc.rows();i++){

                    abc.put(i,abc.cols()/2,127);

            }


            int k;
            boolean finished = false;
            k=abc.cols()/2;

            while(!finished && k<abc.cols()){
                if(array1[ar][k]>130){
                    a1++;
                    k++;
                }
                else{
                    finished=true;
                    break;
                }
            }
            finished=false;
            k=abc.cols()/2;
            while(!finished && k<abc.cols()){
                if(array1[br][k]>130){
                    b1++;
                    k++;
                }
                else{
                    finished=true;
                    break;
                }
            }
            finished=false;
            k=abc.cols()/2;
            while(!finished && k<abc.cols()){
                if(array1[cr][k]>130){
                    c1++;
                    k++;
                }
                else{
                    finished=true;
                    break;
                }
            }
            finished=false;
            k=abc.cols()/2;
            while(!finished && k<abc.cols()){
                if(array1[dr][k]>130){
                    d1++;
                    k++;
                }
                else{
                    finished=true;
                    break;
                }
            }
            finished = false;
            k=abc.cols()/2;

            while(!finished && k>0){
                if(array1[ar][k]>130){
                    a2++;
                    k--;
                }
                else{
                    finished=true;
                    break;
                }
            }
            finished = false;
            k=abc.cols()/2;

            while(!finished && k>0){
                if(array1[br][k]>130){
                    b2++;
                    k--;
                }
                else{
                    finished=true;
                    break;
                }
            }
            finished = false;
            k=abc.cols()/2;

            while(!finished && k>0){
                if(array1[cr][k]>130){
                    c2++;
                    k--;
                }
                else{
                    finished=true;
                    break;
                }
            }
            finished = false;
            k=abc.cols()/2;

            while(!finished && k>0){
                if(array1[dr][k]>130){
                    d2++;
                    k--;
                }
                else{
                    finished=true;
                    break;
                }
            }
            finished = false;


            //Utils.matToBitmap(dst1, result);

            Utils.matToBitmap(abc, result);

            /*String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_OCV" + timeStamp + "_";
            MediaStore.Images.Media.insertImage(getContentResolver(), result, imageFileName, "bodyShapeOCVPics");*/
            //bitmapGray=result;
			
			//startActivityForResult(intent,onClick.button3);

            return result;
        } catch (CvException e) {
            e.printStackTrace();

        }

			return result;
    }

    public String BodyShapeResult (int a, int b, int c, int d){
        int boyTemp=Integer.parseInt(edtTxt.getText().toString());
        float boy = (float)boyTemp/100;
        int kilo=Integer.parseInt(edtTxt2.getText().toString());
        String bodyType="";



        if((float)(kilo/(boy*boy))>30){
            bodyType = Html.fromHtml("<html><body><font size=5 color=black>Result: </font> World </body><html>")+" OVAL";
        }
        else {
            if((float)(d-a)/b>=0.1) {
                if ((float) b / a <= 1) {
                    if ((float) c / b > 1.24)
                        bodyType = Html.fromHtml("<html><body><font size=5 color=black>Result: </font></body><html>")+" Spoon";
                    else
                        bodyType = Html.fromHtml("<html><body><font size=5 color=black>Result: </font></body><html>")+"Bottom Hourglass";
                } else
                    bodyType =  Html.fromHtml("<html><body><font size=5 color=black>Result: </font></body><html>")+ "Triangle";
            }
            else{
                if((float)(d/b)<1.21 && (float)a/b<1.22)
                    bodyType = Html.fromHtml("<html><body><font size= color=black>Result: </font></body><html>")+"Rectangle";
                else
                    bodyType = Html.fromHtml("<html><body><font size=5 color=black>Result: </font></body><html>")+"Hourglass";
            }
        }

        //Toast.makeText(MainActivity.this, "Read Ex. Storage Permissions denied", Toast.LENGTH_SHORT).show();
        return bodyType;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                setPic();
                /*createImageFile();
                galleryAddPic();*/

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            try {
                imageUri = data.getData();
                Bitmap oBitmap;
                Bitmap sonBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                oBitmap=OpenCV(sonBitmap.getHeight(), sonBitmap.getWidth(), sonBitmap);
                //Bitmap oBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                //bitmapGray=OpenCV(oBitmap);
                //iv.setImageBitmap(bitmapGray);
                bitmapOCV = oBitmap;
                iv.setImageURI(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }

}



