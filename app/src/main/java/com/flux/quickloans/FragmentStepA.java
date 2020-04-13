package com.flux.quickloans;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class FragmentStepA extends XFragment implements AdapterView.OnItemSelectedListener {

    @Override
    public View onFragmentCreate(LayoutInflater inflater, ViewGroup root) {
        View view = inflater.inflate(R.layout.fragment_step_a, root, false);

        if (data.getBoolean("step1", false)) fm.beginTransaction().replace(R.id.fragment, new FragmentStepB()).commit();

        Button go = view.findViewById(R.id.step1_continue);
        Spinner gender = view.findViewById(R.id.gender);

        ArrayAdapter<CharSequence> apartment_adapter = ArrayAdapter.createFromResource(cx, R.array.gender, R.layout.xml_spinner);
        apartment_adapter.setDropDownViewResource(R.layout.xml_spinner);
        gender.setAdapter(apartment_adapter);
        gender.setOnItemSelectedListener(this);


        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuickSDK.hideSoftKeys(fx);

                SharedPreferences.Editor e = data.edit();
                e.putBoolean("step1", true);
                e.apply();

                fm.beginTransaction().add(R.id.fragment, new FragmentStepB()).addToBackStack(null).commit();
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
