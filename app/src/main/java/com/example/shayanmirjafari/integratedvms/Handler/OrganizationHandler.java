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
public class OrganizationHandler extends AsyncTask<String, Void, String> {
    private int result;
    private Context context;
    private LanguageProcessing proc;
    private MainActivity faceRecog;
    public OrganizationHandler(Context context, int result){
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

        String[] organizations = null;
        try {

            organizations = proc.findPersonName(strings[0], InfoType.Organization);

        } catch (IOException e) {
            e.printStackTrace();

        }
//            String test = "";
//            if(names != null) {
//                for (String a : names)
//                    test += a + ",";
//            }

//            return test;
        if(organizations != null && organizations.length > 0)
            return organizations[0];

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if(s != null)
            saveInfo(s);
        else
            makeText(context, "no organization", Toast.LENGTH_LONG).show();

    }

    private void saveInfo(String org){
//            HashMap hash = retrieveHash(TestActivity.this);
//            if (!hash.containsKey(inputName)) {
//
//                hash.put(inputName, Integer.toString(result));
//                saveHash(hash, getApplicationContext());
//                saveAlbum();
//                Toast.makeText(
//                        getApplicationContext(),
//                        inputName
//                                + " added successfully",
//                        Toast.LENGTH_SHORT).show();
//
//            } else {
//                Toast.makeText(getApplicationContext(),
//                        "Username '" + inputName + "' already exist",
//                        Toast.LENGTH_SHORT).show();
//            }
        Person person = faceRecog.retrievePerson(result);
//        Person person = MainActivity.facialProc.retrievePerson(result);
        if(person == null){
            person = new Person(result);
            person.setOrganization(org);
            makeText(
                    context,
                    org
                            + " added successfully",
                    Toast.LENGTH_SHORT).show();

        }else{
            person.setOrganization(org);
        }

        person.save();


    }
}
