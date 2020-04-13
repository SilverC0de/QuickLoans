package com.flux.quickloans;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class FragmentStepC extends XFragment implements AdapterView.OnItemSelectedListener {

    @Override
    public View onFragmentCreate(LayoutInflater inflater, ViewGroup root) {
        View view = inflater.inflate(R.layout.fragment_step_c, root, false);

        if (data.getBoolean("step3", false)) fm.beginTransaction().replace(R.id.fragment, new FragmentBVN()).commit();

        Button go = view.findViewById(R.id.step3_continue);
        Spinner state = view.findViewById(R.id.state);
        Spinner residential = view.findViewById(R.id.residencial);

        ArrayAdapter<CharSequence> state_adapter = ArrayAdapter.createFromResource(cx, R.array.state, R.layout.xml_spinner);
        state_adapter.setDropDownViewResource(R.layout.xml_spinner);
        state.setAdapter(state_adapter);
        state.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> residential_adapter = ArrayAdapter.createFromResource(cx, R.array.residential, R.layout.xml_spinner);
        residential_adapter.setDropDownViewResource(R.layout.xml_spinner);
        residential.setAdapter(residential_adapter);
        residential.setOnItemSelectedListener(this);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuickSDK.hideSoftKeys(fx);

                SharedPreferences.Editor e = data.edit();
                e.putBoolean("step3", true);
                e.apply();

                fm.beginTransaction().add(R.id.fragment, new FragmentBVN()).addToBackStack(null).commit();
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
