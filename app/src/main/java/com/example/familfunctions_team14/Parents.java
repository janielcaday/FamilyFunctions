package com.example.familfunctions_team14;

public class Parents {

    private String email;
    private String firstname;
    private String id;
    private String lastname;
    private String password;



    public Parents() {


    }

    public Parents(String email, String firstname, String id, String lastname, String password) {
        this.email = email;
        this.firstname = firstname;
        this.id = id;
        this.lastname = lastname;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
