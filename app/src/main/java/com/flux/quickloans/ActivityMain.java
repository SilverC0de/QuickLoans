package com.flux.quickloans;

import android.os.Bundle;

public class ActivityMain extends XActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isNewbie = data.getBoolean(XClass.newbie, true);
        boolean hasAgreed = data.getBoolean(XClass.agree, false);

        if (isNewbie) {
            if (hasAgreed){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new FragmentStepA()).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new FragmentAgreement()).commit();
            }
        } else {
            FragmentLoans loans = new FragmentLoans();

            Bundle bundle = new Bundle();
            String mail = data.getString(XClass.mail, XClass.outcast);
            String line = data.getString(XClass.line, XClass.outcast);
            String firstName = data.getString(XClass.firstName, XClass.outcast);
            String middleName = data.getString(XClass.firstName, XClass.outcast);
            String lastName = data.getString(XClass.surname, XClass.outcast);
            String cardHolder = data.getString(XClass.cardHolder, XClass.outcast);

            bundle.putString("mail", mail);
            //bundle.putString("image", image);
            bundle.putString("numberA", line);
            //bundle.putString("numberB", numberB);
            bundle.putString("firstName", firstName);
            bundle.putString("middleName", middleName);
            bundle.putString("lastName", lastName);
            bundle.putString("cardHolder", cardHolder);
            //bundle.putString("bvn", bvn);

            loans.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, loans).commit();
        }
    }
}