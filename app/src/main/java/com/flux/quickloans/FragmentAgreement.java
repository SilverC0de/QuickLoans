package com.flux.quickloans;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class FragmentAgreement extends XFragment {
    @Override
    public View onFragmentCreate(LayoutInflater inflater, ViewGroup root) {
        final View view = inflater.inflate(R.layout.fragment_terms, root, false);
        CheckBox check = view.findViewById(R.id.agree_checker);

        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Button agree = view.findViewById(R.id.agree);
                if (isChecked){
                    agree.setEnabled(true);
                    agree.setAlpha(1f);
                    agree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //support @flux

                            SharedPreferences.Editor e = data.edit();
                            e.putBoolean(XClass.agree, true);
                            e.apply();

                            fm.beginTransaction().replace(R.id.fragment, new FragmentStepA()).commit();
                        }
                    });
                } else {
                    agree.setEnabled(false);
                    agree.setAlpha(.4f);
                }
            }
        });
        return view;
    }
}
