package com.example.shayanmirjafari.integratedvms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shayanmirjafari.integratedvms.Handler.LocationHandler;
import com.example.shayanmirjafari.integratedvms.Handler.NameHandler;
import com.example.shayanmirjafari.integratedvms.Handler.OrganizationHandler;
import com.example.shayanmirjafari.integratedvms.database.Person;
import com.example.shayanmirjafari.integratedvms.listener.CustomizedVoiceListener;
import com.example.shayanmirjafari.integratedvms.listener.OnCallBackListener;
import com.example.shayanmirjafari.integratedvms.nlp.LanguageProcessing;
import com.example.shayanmirjafari.integratedvms.prefrences.PrefrencesManager;
import com.example.shayanmirjafari.integratedvms.view.CameraSurfacePreview;
import com.example.shayanmirjafari.integratedvms.view.DrawView;
import com.qualcomm.snapdragon.sdk.face.FaceData;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing;

import java.util.List;

import static android.widget.Toast.makeText;

public class MainActivity extends Activity implements Camera.PreviewCallback {


    private Camera cameraObj; // Accessing the Android native Camera.
    private FrameLayout preview; // Layout on which camera surface is displayed
    private CameraSurfacePreview mPreview;
    private PrefrencesManager manager;

    private final String TAG = "IntegratedVMS::MainActivity";

    private TextView frame_rate;

    private int frameWidth;
    private int frameHeight;

    private long pre_milisecond;
    private long cur_milisecond;

    private int addResult;
    private int fps = 0;

//    private int rotationAngle = 0;
    private static FacialProcessing.PREVIEW_ROTATION_ANGLE rotationAngle = FacialProcessing.PREVIEW_ROTATION_ANGLE.ROT_90;


    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    private Vibrator vibrate; // Vibrate on button click
    private OrientationEventListener orientationListener; // Accessing device

    private LanguageProcessing proc;

    public static FacialProcessing faceObj;

    private boolean toDetect = false, toRecognize = false;

    private DrawView drawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        manager = PrefrencesManager.createPref(this);
        vibrate = (Vibrator) MainActivity.this
                .getSystemService(Context.VIBRATOR_SERVICE);
        orientationListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {

            }
        };
        frame_rate = (TextView)findViewById(R.id.fps);
        init();

        if(manager.isFirstTime()){
            new Person().save();
            manager.setFirstTime();
        }

        proc = LanguageProcessing.getInstance();
    }

    private void init(){
        boolean isSupported= com.qualcomm.snapdragon.sdk.face.FacialProcessing.isFeatureSupported(com.qualcomm.snapdragon.sdk.face.FacialProcessing.FEATURE_LIST.FEATURE_FACIAL_RECOGNITION);
        if(isSupported)
        {
//            Log.d(LOG_TAG2,"Feature Facial Recognition is supported");
            faceObj = (com.qualcomm.snapdragon.sdk.face.FacialProcessing) com.qualcomm.snapdragon.sdk.face.FacialProcessing.getInstance();
            loadAlbum();
            if(faceObj!=null)
            {
                faceObj.setRecognitionConfidence(80);
                faceObj.setProcessingMode(FacialProcessing.FP_MODES.FP_MODE_VIDEO);
            }
            else
            {
//                Log.e(LOG_TAG2,"Facial Recognition object is NULL");
            }
            //resetAlbum();
            //loadAlbum();
        }else{
            makeText(MainActivity.this, getString(R.string.not_supporting), Toast.LENGTH_LONG).show();
        }
    }

    /*
     * Function to retrieve the byte array from the Shared Preferences.
     */
    public void loadAlbum() {
        byte[] albumArray = manager.loadAlbum();
        if(albumArray != null){
            faceObj.deserializeRecognitionAlbum(albumArray);
        }
    }

    /*
     * Method to save the recognition album to a permanent device memory
     */
    public void saveAlbum() {
        byte[] albumBuffer = faceObj.serializeRecogntionAlbum();
        manager.saveAlbum(albumBuffer);
    }


    private void startCamera() {
//        if (cameraFacingFront) {
//            cameraObj = Camera.open(FRONT_CAMERA_INDEX); // Open the Front
//            // camera
//        } else {
//        cameraObj = Camera.open(BACK_CAMERA_INDE); // Open the back camera
//        }
        cameraObj = Camera.open();
        pre_milisecond = System.currentTimeMillis();

        mPreview = new CameraSurfacePreview(MainActivity.this, cameraObj,
                orientationListener); // Create a new surface on which Camera
        // will be displayed.
        preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
        cameraObj.setPreviewCallback(this);
        //
        frameWidth = cameraObj.getParameters().getPreviewSize().width;
        frameHeight = cameraObj.getParameters().getPreviewSize().height;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

        /*
     * Function to retrieve information from DB
     *
     */

    public Person retrievePerson(int id){
        List<Person> persons = Person.listAll(Person.class);
        if(persons != null && persons.size() > 0){
            for(Person p: persons)
                if(p.getPersonID() == id)
                    return p;
        }

        return null;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        cur_milisecond = System.currentTimeMillis();
        fps++;
        if(cur_milisecond - pre_milisecond >= 1000){
            pre_milisecond = cur_milisecond;
            frame_rate.setText("frame rate: "+fps);
            fps = 0;
        }
//        Bitmap b = convertToBitmap(bytes, camera);
        if(toDetect){

            detect(data, camera);
        }else{
            recognize(data, camera);
        }
    }


    protected void onPause() {
        super.onPause();
        stopCamera();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (faceObj != null)
        {
            faceObj.release();
            faceObj = null;
            Log.d(TAG, "Face Recog Obj released");
        }
        if (orientationListener != null)
            orientationListener.disable();

        if(speech != null)
            speech.destroy();
    }

    protected void onResume() {
        super.onResume();
        if (cameraObj != null) {
            stopCamera();
        }
        startCamera();
    }

    /*
     * Stops the camera preview. Releases the camera. Make the objects null.
     */
    private void stopCamera() {

        if (cameraObj != null) {
            cameraObj.stopPreview();
            cameraObj.setPreviewCallback(null);
            preview.removeView(mPreview);
            cameraObj.release();
        }
        cameraObj = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(toDetect) {
            toDetect = false;

        }else
            toDetect = true;
        vibrate.vibrate(80);
        return super.onTouchEvent(event);
    }

    private void startRecording()
    {
        speech = SpeechRecognizer.createSpeechRecognizer(this);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        //Time Interval: EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS

        CustomizedVoiceListener listener = CustomizedVoiceListener.create(speech, recognizerIntent, new OnCallBackListener() {
            @Override
            public void onRecognitionResult(String result) {
                makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                new NameHandler(MainActivity.this, addResult).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{result});
                new LocationHandler(MainActivity.this, addResult).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{result});
                new OrganizationHandler(MainActivity.this, addResult).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{result});

            }
        });

        speech.setRecognitionListener(listener);

        speech.startListening(recognizerIntent);

    }

//    private void detect(byte[] data, Camera camera){
//        boolean result1 = faceObj.setFrame(data, frameWidth, frameHeight, false, rotationAngle);
//        if(result1){
//            FaceData[] faceDataArray = faceObj.getFaceData();
//            if (faceDataArray != null) {
////                convertToBitmap(data, camera);
//                for (int i = 0; i < faceDataArray.length; i++) {
//                        int personId = faceDataArray[i].getPersonId();
//                        if (personId < 0) {
//
////                            arrayPosition = i;
//                            addResult = faceObj.addPerson(i);
//                            savePerson(addResult, null, null, null);
////                            makeText(this, "person added", Toast.LENGTH_SHORT).show();
//
//                            saveAlbum();
////                            createAlert();
//                            startRecording();
//
//                        } else {
//                            if(faceDataArray[i].getRecognitionConfidence() >= 70){
//                                int result = faceObj.updatePerson(personId,
//                                        i);
//                                String userName = "Unknown person";
////                            Iterator<HashMap.Entry<String, String>> iter = retrieveHash(this).entrySet()
////                                    .iterator();
////                            while (iter.hasNext()) {
////                                HashMap.Entry<String, String> entry = iter.next();
////                                if (entry.getValue().equals(Integer.toString(personId))) {
////                                    userName = entry.getKey();
////                                }
////                            }
//
//                                Person person = retrievePerson(personId);
//                                if(person != null)
//                                    userName = person.getName();
//
//                                if (result == 0) {
//                                    Toast.makeText(
//                                            getApplicationContext(),
//                                            "'"
//                                                    + userName
//                                                    + "' updated successfully ",
//                                            Toast.LENGTH_SHORT).show();
//
//                                } else {
//                                    Toast.makeText(
//                                            getApplicationContext(),
//                                            "Maximum face limit for " + "'"
//                                                    + userName
//                                                    + "' reached.",
//                                            Toast.LENGTH_SHORT).show();
//                                }
//                                saveAlbum();
//                            }else{
//                                makeText(
//                                    getApplicationContext(),
//                                    "Similar face already exists. Try updating that person. Confidence= +"
//                                            + Integer
//                                            .toString(faceDataArray[i]
//                                                    .getRecognitionConfidence()),
//                                    Toast.LENGTH_SHORT).show();
//
//
//                            }
//
//                        }
//
//
//                }
//            }
//
//        }
//        toDetect = false;
//    }

    private void detect(byte[] data, Camera camera){
        boolean result = faceObj.setFrame(data, frameWidth, frameHeight, false, rotationAngle);
        if(result) {
            int number = faceObj.getNumFaces();
            if (number != 0) {
                FaceData[] faceDataArray = faceObj.getFaceData();
                if (faceDataArray != null) {
                    for (int i = 0; i < faceDataArray.length; i++) {
                        int personID = faceDataArray[i].getPersonId();
                        if (personID < 0) {
                            addResult = faceObj.addPerson(i);
//                            }else{
//                                addResult = faceObj.updatePerson(personID, i);
//                            }

//                            new NameHandler(context, addResult).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{info});
//                            new LocationHandler(context, addResult).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{info});
//                            new OrganizationHandler(context, addResult).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{info});
                            startRecording();

                            saveAlbum();
                        }
                    }
                }
            }
        }
        toDetect = false;

    }


    private void recognize(byte[] data, Camera camera){
        boolean result = faceObj.setFrame(data, frameWidth, frameHeight, false, rotationAngle);
        if (result) {
            int numFaces = faceObj.getNumFaces();
            if (numFaces == 0) {
                Log.d("TAG", "No Face Detected");
                if (drawView != null) {
                    preview.removeView(drawView);
                    drawView = new DrawView(this, null, false);
                    preview.addView(drawView);
                }
            } else {
                FaceData[] faceArray = faceObj.getFaceData();
                if (faceArray == null) {
                    Log.e("TAG", "Face array is null");
                } else {

                    int surfaceWidth = mPreview.getWidth();
                    int surfaceHeight = mPreview.getHeight();
                    faceObj.normalizeCoordinates(surfaceWidth, surfaceHeight);
                    preview.removeView(drawView); // Remove the previously created view to avoid unnecessary stacking of
                    // Views.
                    drawView = new DrawView(this, faceArray, true);
                    preview.addView(drawView);
                }
            }
        }

    }

    /*
     * Function to store a person to DB.
     *
     */

    protected void savePerson(int id, String name, String location, String organization){
        Person person = new Person(id);
        person.setName(name);
        person.setLocation(location);
        person.setOrganization(organization);

        person.save();
        toRecognize = true;

    }

}

