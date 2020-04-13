package com.flux.quickloans;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FragmentBVN extends XFragment implements AdapterView.OnItemSelectedListener {

    private Button check;
    private TextView error;
    private LottieAnimationView loader;

    @Override
    public View onFragmentCreate(LayoutInflater inflater, ViewGroup root) {
        View view = inflater.inflate(R.layout.fragment_bvn, root, false);

        Spinner bnk = view.findViewById(R.id.banknme);
        EditText bvn = view.findViewById(R.id.bvn_number);

        check = view.findViewById(R.id.bvn_button);
        error = view.findViewById(R.id.bvn_error);
        loader = view.findViewById(R.id.loader);

        ArrayAdapter<CharSequence> apartment_adapter = ArrayAdapter.createFromResource(cx, R.array.bank_names, R.layout.xml_spinner);
        apartment_adapter.setDropDownViewResource(R.layout.xml_spinner);
        bnk.setAdapter(apartment_adapter);
        bnk.setOnItemSelectedListener(this);

        bvn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (s.length() == 11){
                    check.setAlpha(1f);
                    check.setEnabled(true);
                    check.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            error.setText("");
                            check.setAlpha(.2f);
                            check.setText("Please Wait");
                            check.setEnabled(false);
                            checkUser(String.valueOf(s));
                        }
                    });
                } else {
                    check.setAlpha(.2f);
                    check.setEnabled(false);
                }
            }
        });
        return view;
    }


    private void checkUser(final String bvn){

        loader.setVisibility(View.VISIBLE);


        error.setText("Initializing");

        QuickSDK.hideSoftKeys(fx);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("bvn", bvn).build();
        Request rq = new Request.Builder().url(XClass.apiCheckUser).post(body).build();
        client.newCall(rq).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        check.setAlpha(1f);
                        check.setText("Get loan offers");
                        check.setEnabled(true);

                        loader.setVisibility(View.GONE);

                        error.setText("Internet connection lost");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String json = response.body().string();
                if (json.equals("00")){
                    //new user
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            checkBVN(bvn);
                        }
                    });
                } else {
                    //get all data
                    try {
                        JSONObject user = new JSONObject(json);

                        String mail = user.getString("email");
                        //String image = user.getString("base64Image");
                        String firstName = user.getString("firstname");
                        String middleName = user.getString("middlename");
                        String lastName = user.getString("lastname");
                        String numberA = user.getString("mobile");
                        String cardHolder = user.getString("cardname");

                        FragmentLoans loans = new FragmentLoans();

                        Bundle bundle = new Bundle();

                        bundle.putString("mail", mail);
                        bundle.putString("image", "");
                        bundle.putString("numberA", numberA);
                        bundle.putString("numberB", numberA);
                        bundle.putString("firstName", firstName);
                        bundle.putString("middleName", middleName);
                        bundle.putString("lastName", lastName);
                        bundle.putString("cardHolder", cardHolder);
                        bundle.putString("bvn", bvn);

                        loans.setArguments(bundle);

                        SharedPreferences.Editor e = data.edit();
                        e.putString(XClass.mail, mail);
                        e.putString(XClass.image, "");
                        e.putString(XClass.line, numberA);
                        e.putString(XClass.firstName, firstName);
                        e.putString(XClass.middleName, middleName);
                        e.putString(XClass.surname, lastName);
                        e.putString(XClass.cardHolder, cardHolder);
                        e.apply();

                        fm.beginTransaction().replace(R.id.fragment, loans).commit();

                    } catch (JSONException ignored){}


                }
            }
        });
    }

    private void checkBVN(final String bvn){


        error.setText("Processing request");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                error.setText("Please wait, we are currently analyzing your credit score");
            }
        }, 8000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                error.setText("Processing credit score");
            }
        }, 14000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                error.setText("Wrapping things up");
            }
        }, 20000);

        try {
            final String reference = String.valueOf(System.currentTimeMillis());
            final JSONObject object = new JSONObject();

            JSONObject auth = new JSONObject();
            auth.put("type", null);
            auth.put("secure", null);
            auth.put("auth_provider", "SunTrust");

            JSONObject customer = new JSONObject();
            customer.put("customer_ref", reference);
            customer.put("firstname", "");
            customer.put("surname", "");
            customer.put("email", "");
            customer.put("mobile_no", "");

            JSONObject details = new JSONObject();
            details.put("bvn", bvn);
            details.put("otp_validation", false);

            JSONObject transaction = new JSONObject();
            transaction.put("amount", null);
            transaction.put("transaction_ref", reference);
            transaction.put("transaction_desc", "BVN lookup");
            transaction.put("transaction_ref_parent", null);
            transaction.put("customer", customer);
            transaction.put("details", details);


            object.put("request_ref", reference);
            object.put("request_type", "bvn_lookup");
            object.put("auth", auth);
            object.put("transaction", transaction);



            StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, XClass.apiBVN, new Response.Listener<String>() {
                @Override
                public void onResponse(String str) {
                    loader.setVisibility(View.GONE);
                    try {

                        JSONObject response = new JSONObject(str);
                        String status = response.getString("status");
                        if (Objects.equals(status, "Successful")){
                            //save data on db

                            JSONObject user = response.getJSONObject("data").getJSONObject("provider_response");

                            String mail = user.getString("email");
                            String image = user.getString("base64Image");
                            String firstName = user.getString("firstName");
                            String middleName = user.getString("middleName");
                            String lastName = user.getString("lastName");
                            String numberA = user.getString("phoneNumber1");
                            String numberB = user.getString("phoneNumber2");
                            String cardHolder = user.getString("nameOnCard");

                            XClass.updateUser(
                                    firstName,
                                    middleName,
                                    lastName,
                                    user.getString("dateOfBirth"), //dob
                                    numberA,
                                    mail,
                                    user.getString("gender"), //gender
                                    user.getString("lgaOfOrigin"), //local gvt
                                    user.getString("lgaOfResidence"), //local res
                                    user.getString("maritalStatus"), //marital
                                    user.getString("nin"), //nin
                                    cardHolder,
                                    user.getString("nationality"), //nationality
                                    user.getString("residentialAddress"), //res addr
                                    user.getString("stateOfOrigin"), //state of ori
                                    user.getString("stateOfResidence"), //state of res
                                    user.getString("title"), //title
                                    user.getString("watchListed"), //watchlis
                                    bvn);

                            FragmentLoans loans = new FragmentLoans();

                            Bundle bundle = new Bundle();

                            bundle.putString("mail", mail);
                            bundle.putString("image", image);
                            bundle.putString("numberA", numberA);
                            bundle.putString("numberB", numberB);
                            bundle.putString("firstName", firstName);
                            bundle.putString("middleName", middleName);
                            bundle.putString("lastName", lastName);
                            bundle.putString("cardHolder", cardHolder);
                            bundle.putString("bvn", bvn);

                            loans.setArguments(bundle);

                            SharedPreferences.Editor e = data.edit();
                            e.putString(XClass.mail, mail);
                            e.putString(XClass.image, image);
                            e.putString(XClass.line, numberA);
                            e.putString(XClass.firstName, firstName);
                            e.putString(XClass.middleName, middleName);
                            e.putString(XClass.surname, lastName);
                            e.putString(XClass.cardHolder, cardHolder);
                            e.apply();

                            fm.beginTransaction().replace(R.id.fragment, loans).commit();
                        } else {
                            check.setAlpha(1f);
                            check.setText("Get loan offers");
                            check.setEnabled(true);

                            error.setText("Please check the BVN and try again");
                        }
                    } catch (JSONException ignored) {}
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError err) {
                    check.setAlpha(1f);
                    check.setText("Get loan offers");
                    check.setEnabled(true);

                    loader.setVisibility(View.GONE);

                    error.setText("Internet connection lost");
                }
            }) {

                @Override
                public byte[] getBody() {
                    return object.toString().getBytes(StandardCharsets.UTF_8);
                }

                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> header = new HashMap<>();
                    header.put("Authorization", XClass.onepipe);
                    header.put("Signature", QuickSDK.MD5(reference + ";" + XClass.signature));
                    header.put("Content-Type", "application/json");
                    return header;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(cx).add(request);
            Volley.newRequestQueue(cx).getCache().clear();

        } catch (JSONException ignored){
            Toast.makeText(fx, "errrrrrr", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}