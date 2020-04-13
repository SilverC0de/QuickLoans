package com.flux.quickloans;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class XClass {
    static String sharedPreferences = "sp";
    static String apiCheckUser = "https://beta.quickloans.ng/getBvn.php";
    static String apiBVN = "https://api.onepipe.io/v1/generic/transact";
    static String apiRequestLoan = "https://api.onepipe.io/v1/loans/offers";
    static String apiAcceptLoan = "https://api.onepipe.io/v1/loans/accept";
    static String apiLoanStatus = "https://api.onepipe.io/v1/loans/status";
    static String onepipe = "Bearer 1a1aXwsCryTMZhPp0ckV_6f3230791a7542c7b6031d21aa033c37";
    static String signature = "uF4z5FVWBvvBBONE";

    static String paystackKei = "pk_test_219fe40f38e54f389a60160061bdcf153f2415d5";
    static String paystackKey = "pk_live_5f002e17e9fb61784f9db7395d4972c54b511a45";

    static String mail = "mail";
    static String image = "base64image";
    static String line = "line";
    static String firstName = "firstName";
    static String middleName = "middleName";
    static String surname = "surname";
    static String cardHolder = "cardhlder";
    static String bvn = "bvn";
    static String agree = "agree";

    static String newbie = "newbie";
    static String inducted = "indected";

    static String outcast = "";

    static void updateUser(String firstname, String middlename,
                           String lastname, String dob,
                           String mobile, String email, String gender,
                           String localgovt, String localresidence,
                           String marital, String nin,
                           String cardname, String nationality, String residentialadd,
                           String stateoforigin, String stateofresidence,
                           String title, String watchlisted, String bvn){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("firstname", firstname)
                .add("middlename", middlename)
                .add("lastname", lastname)
                .add("dob", dob)
                .add("mobile", mobile)
                .add("email", email)
                .add("gender", gender)
                .add("localgovt", localgovt)
                .add("localresidence", localresidence)
                .add("marital", marital)
                .add("nin", nin)
                .add("cardname", cardname)
                .add("nationality", nationality)
                .add("residentialadd", residentialadd)
                .add("stateoforigin", stateoforigin)
                .add("stateofresidence", stateofresidence)
                .add("title", title)
                .add("watchlisted", watchlisted)
                .add("bvn", bvn).build();

        Request r = new Request.Builder().url("https://beta.quickloans.ng/updateUser.php").post(body).build();
        client.newCall(r).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });
    }
}
