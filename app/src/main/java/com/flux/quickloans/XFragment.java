package com.flux.quickloans;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public abstract class XFragment extends Fragment {

    Activity fx;
    Context cx;
    FragmentManager fm;
    SharedPreferences data;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        fx = getActivity();
        cx = context;
        fm = getFragmentManager();
        data = cx.getSharedPreferences(XClass.sharedPreferences, Context.MODE_PRIVATE);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return onFragmentCreate(inflater, container);
    }

    public abstract View onFragmentCreate(LayoutInflater inflater, ViewGroup root);
}
