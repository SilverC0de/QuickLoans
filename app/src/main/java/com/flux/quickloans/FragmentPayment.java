package com.flux.quickloans;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentPayment extends XFragment {
    @Override
    public View onFragmentCreate(LayoutInflater inflater, ViewGroup root) {
        View view = inflater.inflate(R.layout.fragment_payment, root, false);

        return view;
    }
}
