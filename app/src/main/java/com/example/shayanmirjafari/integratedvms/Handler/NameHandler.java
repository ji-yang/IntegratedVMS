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
public class NameHandler extends AsyncTask<String, Void, String> {

    private int result;
    private Context context;
    private LanguageProcessing proc;
//    private FacialProcessing faceRecog;
    private MainActivity faceRecog;
    public NameHandler(Context context, int result){
        this.context = context;
        this.result = result;
        proc = LanguageProcessing.getInstance();
//        faceRecog = new FacialProcessing();
        faceRecog = new MainActivity();
    }


    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(final String... strings) {
        String[] names = null;
        String[] locations = null;
        String[] organizations = null;
        try {

            names = proc.findPersonName(strings[0], InfoType.PersonalName);

        } catch (IOException e) {
            e.printStackTrace();

        }
//            String test = "";
//            if(names != null) {
//                for (String a : names)
//                    test += a + ",";
//            }

//            return test;
        if(names != null && names.length > 0)
            return names[0];

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if(s != null)
            saveInfo(s);
        else
            makeText(context, "no name", Toast.LENGTH_LONG).show();

    }

    private void saveInfo(String name){
        Person person = faceRecog.retrievePerson(result);
        if(person == null){
            person = new Person(result);
            person.setName(name);
            makeText(
                    context,
                    name
                            + " added successfully",
                    Toast.LENGTH_SHORT).show();

        }else{
            person.setName(name);
        }

        person.save();


    }
}
