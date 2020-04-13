package com.flux.quickloans;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FragmentLoans extends XFragment {

    private LottieAnimationView loader;
    private ImageView illustration;
    private TextView message;
    private ListView loans;

    @Override
    public View onFragmentCreate(LayoutInflater inflater, ViewGroup root) {
        View view = inflater.inflate(R.layout.fragment_loans, root, false);
        loader = view.findViewById(R.id.loader);
        illustration = view.findViewById(R.id.loans_illustration);
        message = view.findViewById(R.id.loans_message);
        loans = view.findViewById(R.id.list);

        Bundle bnd = getArguments();
        if (bnd != null) {
            String mail = bnd.getString("mail");
            String firstName = bnd.getString("firstName");
            String middleName = bnd.getString("middleName");
            String lastName = bnd.getString("lastName");
            String image = bnd.getString("image");
            String numberA = bnd.getString("numberA");
            String numberB = bnd.getString("numberB");

            //initializeLoans(firstName, lastName, mail,"2348161284937");
            initializeLoans(firstName, lastName, mail,"234" + numberA.substring(1));
        }
        return view;
    }


    private void initializeLoans(String firstName, String lastName, String mail, String number){

        try {
            final String reference = String.valueOf(System.currentTimeMillis());
            final JSONObject object = new JSONObject();

            JSONObject customer = new JSONObject();
            customer.put("customer_ref", number);
            customer.put("firstname", firstName);
            customer.put("surname", lastName);
            customer.put("email", mail);
            customer.put("mobile_no", number);

            JSONObject transaction = new JSONObject();
            transaction.put("amount", "20000");
            transaction.put("transaction_ref", reference);
            transaction.put("transaction_desc", "Instant loan");
            transaction.put("service_type", "MONEY");
            transaction.put("customer", customer);

            object.put("request_ref", reference);
            object.put("transaction", transaction);

            StringRequest request = new StringRequest(Request.Method.POST, XClass.apiRequestLoan, new Response.Listener<String>() {
                @Override
                public void onResponse(String json) {
                    Log.e("silvr", "no json" + json);

                    try {
                        JSONObject response = new JSONObject(json);
                        JSONArray offers = response.getJSONObject("data").getJSONArray("offers");
                        String status = response.getString("status");


                        if (Objects.equals(status, "OffersDelivered") && offers.length() != 0) {
                            final List<HashMap<String, String>> list = new ArrayList<>();

                            loader.setVisibility(View.GONE);
                            illustration.setVisibility(View.GONE);

                            for (int i = 0; i < offers.length(); i++){
                                HashMap<String, String> map = new HashMap<>();

                                JSONObject obj = offers.getJSONObject(i);
                                String x = obj.getString("offerId");
                                String code = obj.getString("providerCode");
                                String interest = obj.getString("interest");
                                String amount = obj.getString("amountOffered");
                                String days = obj.getString("tenure");

                                //2348161284937
                                map.put("x", x);
                                map.put("code", code);
                                map.put("interest", interest + "%");
                                map.put("amount", NumberFormat.getIntegerInstance().format(Integer.parseInt(amount) / 100)); //remove kobo
                                map.put("days", days + " Days");

                                list.add(map);
                            }

                            String[] from = {"interest", "amount", "days"};
                            int[] to = {R.id.interest, R.id.amount, R.id.days};
                            SimpleAdapter adapter = new SimpleAdapter(cx, list, R.layout.loan, from, to);
                            loans.setVisibility(View.VISIBLE);
                            loans.setAdapter(adapter);
                            loans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    FragmentCash cash = new FragmentCash();

                                    Bundle bnd = getArguments();
                                    bnd.putString("x", list.get(position).get("x")); //offer id
                                    bnd.putString("code", list.get(position).get("code")); //provider code
                                    bnd.putString("amount", list.get(position).get("amount"));
                                    bnd.putString("days", list.get(position).get("days"));
                                    bnd.putString("interest", list.get(position).get("interest"));
                                    bnd.putString("tx", reference);

                                    cash.setArguments(bnd);
                                    fm.beginTransaction().add(R.id.fragment, cash).addToBackStack(null).commit();
                                }
                            });
                        } else {
                            Log.e("silvr", "no load");
                            loader.setVisibility(View.GONE);
                            message.setVisibility(View.VISIBLE);
                            message.setText("We are sorry to inform you that we cannot offer you a loan now, please kindly check back within the next 3 hours or send a mail to support@fluxtechafrica.com");
                        }
                    } catch (JSONException ignored){
                        Log.e("silvr", "no loand" + ignored.getMessage());

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loader.setVisibility(View.GONE);
                    message.setVisibility(View.VISIBLE);
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
        } catch (JSONException ignored){ }
    }
}