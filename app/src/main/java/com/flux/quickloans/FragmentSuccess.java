package com.flux.quickloans;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentSuccess extends XFragment {

    @Override
    public View onFragmentCreate(LayoutInflater inflater, ViewGroup root) {
        View view = inflater.inflate(R.layout.fragment_success, root, false);

        Button end = view.findViewById(R.id.end);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.beginTransaction().replace(R.id.fragment, new FragmentHistory()).commit();
            }
        });
        return view;
    }
}
