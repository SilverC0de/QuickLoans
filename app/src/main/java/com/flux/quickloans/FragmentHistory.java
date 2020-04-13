package com.flux.quickloans;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentHistory extends XFragment {

    private List<HashMap<String, String>> list;

    @Override
    public View onFragmentCreate(LayoutInflater inflater, ViewGroup root) {
        final View view = inflater.inflate(R.layout.fragment_history, root, false);

        final Button start = view.findViewById(R.id.start);
        final TextView message = view.findViewById(R.id.history_message);
        final LottieAnimationView loader = view.findViewById(R.id.loader);
        final ImageView history_illustration = view.findViewById(R.id.history_illustration);

        list = new ArrayList<>();

        final String reference = String.valueOf(System.currentTimeMillis());
        try {
            String line = data.getString(XClass.line, XClass.outcast);
            String firstName = data.getString(XClass.firstName, XClass.outcast);
            String surname = data.getString(XClass.surname, XClass.outcast);
            final String mail = data.getString(XClass.mail, XClass.outcast);


            JSONObject customer = new JSONObject();
            customer.put("customer_ref", "234" + line.substring(1));
            customer.put("firstname", firstName);
            customer.put("surname", surname);
            customer.put("email", mail);
            customer.put("mobile_no", "234" + line.substring(1));

            JSONObject transaction = new JSONObject();
            transaction.put("transaction_ref", reference);
            transaction.put("transaction_desc", "Am I owing?");
            transaction.put("customer", customer);

            final JSONObject object = new JSONObject();
            object.put("request_ref", reference);
            object.put("transaction", transaction);

            StringRequest request = new StringRequest(Request.Method.POST, XClass.apiLoanStatus, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    loader.setVisibility(View.GONE);

                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray loans = json.getJSONObject("data").getJSONArray("loans");
                        String status = json.getString("status");
                        if (status.equals("Successful") && loans.length() != 0){
                            history_illustration.setVisibility(View.GONE);
                            for (int i = 0; i < loans.length(); i++){
                                HashMap<String, String> map = new HashMap<>();
                                JSONObject obj = loans.getJSONObject(i);

                                String amount = obj.getString("amount");
                                String interest = obj.getString("interest");
                                String days = obj.getString("tenure");
                                String open = obj.getString("dateCreated");
                                String close = obj.getString("dueDate");
                                String stat = obj.getString("status");

                                int cash = Integer.parseInt(amount) / 100;
                                map.put("amount", String.valueOf(cash));
                                map.put("interest", interest + "%");
                                map.put("open", QuickSDK.relativeTime(open, true));
                                map.put("close", QuickSDK.relativeTime(close, true));
                                map.put("stat", stat);

                                list.add(map);
                            }
                            String[] from = {"amount", "interest", "stat", "open", "close"};
                            int[] to = {R.id.amount, R.id.interest, R.id.stat, R.id.start, R.id.end};

                            ListView loan_list = view.findViewById(R.id.list);
                            SimpleAdapter adapter = new SimpleAdapter(cx, list, R.layout.xml_history, from, to);
                            loan_list.setAdapter(adapter);
                        } else {
                            message.setText("You do not have any active loans");
                        }
                    }catch (JSONException ignored){}
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loader.setVisibility(View.GONE);
                    message.setText(error.getMessage());
                }
            }){
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
            request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(cx).add(request);
            Volley.newRequestQueue(cx).getCache().clear();
        } catch (JSONException ignored){}

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                fm.beginTransaction().add(R.id.fragment, loans).addToBackStack(null);
            }
        });
        return view;
    }
}