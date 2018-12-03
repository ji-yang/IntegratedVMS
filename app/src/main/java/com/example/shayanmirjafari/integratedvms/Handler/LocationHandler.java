package com.example.shayanmirjafari.integratedvms.Handler;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.shayanmirjafari.integratedvms.MainActivity;
import com.example.shayanmirjafari.integratedvms.database.Person;
import com.example.shayanmirjafari.integratedvms.nlp.InfoType;
import com.example.shayanmirjafari.integratedvms.nlp.LanguageProcessing;

import java.io.IOException;

import static android.widget.Toast.makeText;

/**
 * Created by shayan on 8/15/15.
 */
public class LocationHandler extends AsyncTask<String, Void, String> {
    private int result;
    private Context context;
    private LanguageProcessing proc;
    private MainActivity faceRecog;
    public LocationHandler(Context context, int result){
        this.context = context;
        this.result = result;
        proc = LanguageProcessing.getInstance();
        faceRecog = new MainActivity();
    }


    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(final String... strings) {
        String[] locations = null;
        try {

            locations = proc.findPersonName(strings[0], InfoType.Location);

        } catch (IOException e) {
            e.printStackTrace();

        }
//            String test = "";
//            if(names != null) {
//                for (String a : names)
//                    test += a + ",";
//            }

//            return test;
        if(locations != null && locations.length > 0)
            return locations[0];

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if(s != null)
            saveInfo(s);
        else
            makeText(context, "no location", Toast.LENGTH_LONG).show();

    }

    private void saveInfo(String location){
//        Person person = MainActivity.facialProc.retrievePerson(result);
        Person person = faceRecog.retrievePerson(result);
        if(person == null){
            person = new Person(result);
            person.setLocation(location);
            makeText(
                    context,
                    location
                            + " added successfully",
                    Toast.LENGTH_SHORT).show();

        }else{
            person.setLocation(location);
        }

        person.save();


    }

}
