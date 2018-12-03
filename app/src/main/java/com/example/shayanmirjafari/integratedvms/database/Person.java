package com.example.shayanmirjafari.integratedvms.database;

import com.orm.SugarRecord;

/**
 * Created by shayan on 8/14/15.
 */
public class Person extends SugarRecord<Person>{

    private int personID;
    private String name = "no info";
    private String location = "no info";
    private String organization = "no info";

    public Person(){

    }

    public Person(int personID){
//        this.personID = Integer.toString(personID);
        this.personID = personID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name != null)
            this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if(location != null)
            this.location = location;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        if(organization != null)
            this.organization = organization;

    }

//    public String getPersonID() {
//        return personID;
//    }
//
//    public void setPersonID(String personID) {
//        this.personID = personID;
//    }

    public int getPersonID(){
        return personID;
    }

}
