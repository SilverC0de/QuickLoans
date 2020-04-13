package com.flux.quickloans;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class FragmentStepB extends XFragment implements AdapterView.OnItemSelectedListener {

    @Override
    public View onFragmentCreate(LayoutInflater inflater, ViewGroup root) {
        View view = inflater.inflate(R.layout.fragment_step_b, root, false);

        if (data.getBoolean("step2", false)) fm.beginTransaction().replace(R.id.fragment, new FragmentStepC()).commit();

        Button go = view.findViewById(R.id.step2_continue);
        Spinner qualification = view.findViewById(R.id.qualification);
        Spinner employment = view.findViewById(R.id.employment);

        ArrayAdapter<CharSequence> apartment_adapter = ArrayAdapter.createFromResource(cx, R.array.qualification, R.layout.xml_spinner);
        apartment_adapter.setDropDownViewResource(R.layout.xml_spinner);
        qualification.setAdapter(apartment_adapter);
        qualification.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> employment_adapter = ArrayAdapter.createFromResource(cx, R.array.employment, R.layout.xml_spinner);
        employment_adapter.setDropDownViewResource(R.layout.xml_spinner);
        employment.setAdapter(employment_adapter);
        employment.setOnItemSelectedListener(this);


        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuickSDK.hideSoftKeys(fx);
                SharedPreferences.Editor e = data.edit();
                e.putBoolean("step2", true);
                e.apply();

                fm.beginTransaction().add(R.id.fragment, new FragmentStepC()).addToBackStack(null).commit();
            }
        });
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
