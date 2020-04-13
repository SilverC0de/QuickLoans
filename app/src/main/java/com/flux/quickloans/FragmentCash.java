package com.flux.quickloans;

import android.content.SharedPreferences;
import android.os.Bundle;
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

import androidx.core.widget.NestedScrollView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flux.quickloans.R;
import com.flux.quickloans.XFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class FragmentCash extends XFragment implements AdapterView.OnItemSelectedListener {

    private Button request_loan;
    private String bankC0de = "00";
    private LottieAnimationView loader;
    private NestedScrollView scroller;
    private TextView loanError;

    @Override
    public View onFragmentCreate(LayoutInflater inflater, ViewGroup root) {
        View view = inflater.inflate(R.layout.fragment_cash, root, false);

        final EditText editCardNumber = view.findViewById(R.id.cash_card_number);
        final EditText editCVV = view.findViewById(R.id.cash_card_cvv);
        final EditText editMM = view.findViewById(R.id.cash_card_mm);
        final EditText editYY = view.findViewById(R.id.cash_card_yy);
        final EditText editPin = view.findViewById(R.id.cash_card_pin);

        final EditText editBankNumber = view.findViewById(R.id.cash_account_number);
        final Spinner editBankCode = view.findViewById(R.id.cash_bank);


        final TextView cardError = view.findViewById(R.id.card_error);
        final TextView bankError = view.findViewById(R.id.bank_error);

        ArrayAdapter<CharSequence> apartment_adapter = ArrayAdapter.createFromResource(cx, R.array.bank_names, R.layout.xml_spinner);
        apartment_adapter.setDropDownViewResource(R.layout.xml_spinner);
        editBankCode.setAdapter(apartment_adapter);
        editBankCode.setOnItemSelectedListener(this);

        request_loan = view.findViewById(R.id.request);
        loader = view.findViewById(R.id.loader);
        scroller = view.findViewById(R.id.scroller);
        loanError = view.findViewById(R.id.loan_error);

        request_loan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardNumber = editCardNumber.getText().toString().trim();
                String cardCVV = editCVV.getText().toString().trim();
                String cardMM = editMM.getText().toString().trim();
                String cardYY = editYY.getText().toString().trim();
                String cardPin = editPin.getText().toString().trim();

                String accountNumber = editBankNumber.getText().toString().trim();


                if (cardNumber.length() < 12){
                    cardError.setText("Invalid card number");
                } else if (cardCVV.length() < 3){
                    cardError.setText("Invalid CVV code");
                } else if (cardMM.length() != 2 || cardYY.length() != 2) {
                    cardError.setText("Invalid expiry date");
                } else if (cardPin.length() != 4){
                    cardError.setText("Enter your debit card pin code");
                } else if (accountNumber.length() != 10){
                    bankError.setText("Invalid account number");
                } else if (bankC0de.equals("00")) {
                    bankError.setText("Choose a bank");
                } else {
                    String secure = QuickSDK.tripleDES(cardNumber + ";" + cardCVV + ";" + cardMM + cardYY + ";" + cardPin, XClass.signature);

                    Card card = new Card(cardNumber, Integer.parseInt(cardMM), Integer.parseInt(cardYY), cardCVV);
                    if (!card.isValid()){
                        cardError.setText("Invalid card details");
                    } else {
                        cardError.setText("");
                        bankError.setText("");
                        requestLoan(card, secure, accountNumber);
                    }
                }
            }
        });

        initialize(view);
        return view;
    }

    private void initialize(View x){
        TextView amount = x.findViewById(R.id.cash_amount);
        TextView interest = x.findViewById(R.id.cash_interest);
        TextView days = x.findViewById(R.id.cash_days);

        EditText card_holder = x.findViewById(R.id.cash_card_name);
        EditText bank_holder = x.findViewById(R.id.cash_account_name);


        Bundle bnd = getArguments();
        String __amount__ = bnd.getString("amount");
        String __interest__ = bnd.getString("interest");
        String __days__ = bnd.getString("days");

        String __name__ = bnd.getString("cardHolder");

        amount.setText(__amount__);
        interest.setText(__interest__);
        days.setText(__days__);

        card_holder.setText(__name__);
        bank_holder.setText(__name__);

        card_holder.setEnabled(false);
        bank_holder.setEnabled(false);
    }


    private void requestLoan(final Card card, String secure, String nuban) {
        QuickSDK.hideSoftKeys(fx);

        final String reference = String.valueOf(System.currentTimeMillis());
        final String signature = QuickSDK.MD5(reference + ";" + XClass.signature);


        loader.setVisibility(View.VISIBLE);
        scroller.setEnabled(false);
        scroller.setAlpha(.2f);

        request_loan.setAlpha(.4f);
        request_loan.setEnabled(false);
        request_loan.setText("Please wait");

        loanError.setText("");
        loanError.setVisibility(View.GONE);

        Bundle bnd = getArguments();
        String firstName = bnd.getString("firstName");
        String surname = bnd.getString("lastName");
        String mail = bnd.getString("mail");
        String line = bnd.getString("numberA");

        String offerID = bnd.getString("x");
        String tx = bnd.getString("tx");
        String providerCode = bnd.getString("code");

        try {
            final JSONObject object = new JSONObject();

            JSONObject customer = new JSONObject();
            customer.put("firstname", firstName);
            customer.put("surname", surname);
            customer.put("email", mail);
            customer.put("mobile_no", "234" + line.substring(1));
            customer.put("customer_ref", "234" + line.substring(1));

            JSONObject transaction = new JSONObject();
            transaction.put("transaction_ref", tx);
            transaction.put("transaction_desc", "A loan");
            transaction.put("offer_id", offerID);
            transaction.put("provider_code", providerCode);
            transaction.put("account_number", nuban);
            transaction.put("bank_code", bankC0de);
            transaction.put("customer", customer);

            JSONObject auth = new JSONObject();
            auth.put("type", "card");
            auth.put("secure", secure);


            object.put("request_ref", reference);
            object.put("auth", auth);
            object.put("transaction", transaction);


            Log.e("silvrrrr", object.toString());
            StringRequest request = new StringRequest(Request.Method.POST, XClass.apiAcceptLoan, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("silvrrrr222", response);

                    try {
                        JSONObject json = new JSONObject(response);
                        String status = json.getString("status");
                        if (status.equals("Successful")){
                            String mail = getArguments().getString("email");
                            requestCharges(mail, card);
                            request_loan.setText("Wrapping things up");
                        } else {
                            String error = json.getJSONObject("data").getJSONObject("error").getString("message");

                            loader.setVisibility(View.GONE);
                            scroller.setEnabled(true);
                            scroller.setAlpha(1f);

                            request_loan.setAlpha(1f);
                            request_loan.setEnabled(true);
                            request_loan.setText("Request for loan");

                            loanError.setVisibility(View.VISIBLE);
                            loanError.setText(error);
                        }
                    } catch (JSONException i) {
                        loanError.setVisibility(View.VISIBLE);
                        loanError.setText(i.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loader.setVisibility(View.VISIBLE);
                    scroller.setEnabled(true);
                    scroller.setAlpha(1f);

                    request_loan.setAlpha(1f);
                    request_loan.setEnabled(true);
                    request_loan.setText("Request for loan");

                    loanError.setVisibility(View.VISIBLE);
                    loanError.setText("Internet connection lost");
                    Log.e("silvr", "here ah" + error.getMessage());
                    Log.e("silvr", error.getMessage());
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
                    header.put("Signature", signature);
                    header.put("Content-Type", "application/json");
                    return header;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(cx).add(request);
            Volley.newRequestQueue(cx).getCache().clear();
        } catch (JSONException ignored) {}
    }

    private void requestCharges(String mail, Card card){
        PaystackSdk.setPublicKey(XClass.paystackKey);
        Charge transaction = new Charge();
        transaction.setAmount(10000);
        transaction.setEmail(mail);
        transaction.setCard(card);
        PaystackSdk.chargeCard(fx, transaction, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                SharedPreferences.Editor e = data.edit();
                e.putBoolean(XClass.newbie, false);
                e.apply();

                fm.beginTransaction().replace(R.id.fragment, new FragmentSuccess()).commit();
            }

            @Override
            public void beforeValidate(Transaction transaction) {

            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                loader.setVisibility(View.VISIBLE);
                scroller.setEnabled(true);
                scroller.setAlpha(1f);

                request_loan.setAlpha(1f);
                request_loan.setEnabled(true);
                request_loan.setText("Request for loan");

                loanError.setVisibility(View.VISIBLE);
                loanError.setText(error.getMessage());
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String[] banks = cx.getResources().getStringArray(R.array.bank_codes);
        bankC0de = banks[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
