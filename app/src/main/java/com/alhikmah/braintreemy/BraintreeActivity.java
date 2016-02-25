package com.alhikmah.braintreemy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.braintreepayments.api.BraintreePaymentActivity;
import com.braintreepayments.api.PaymentRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import cz.msebera.android.httpclient.Header;

public class BraintreeActivity extends AppCompatActivity {

    private int REQUEST_CODE = 30;
    private String HALAL_TOKEN_URI = "http://45.55.196.7:1337/rest/paymentgateway/token";
    private String HALAL_TRANSACTION = "http://45.55.196.7:1337/rest/paymentgateway/transaction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_braintree);

        Button button=(Button) findViewById(R.id.button_pay);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                halalResponse();

            }
        });

    }
    public void onBraintreeSubmit(String token) {

        PaymentRequest paymentRequest = new PaymentRequest()
                .clientToken(token);
        startActivityForResult(paymentRequest.getIntent(this), REQUEST_CODE);

    }
    private void halalResponse() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(HALAL_TOKEN_URI, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {

                    String token = response.getString("token");
                    onBraintreeSubmit(token);

                    Log.e("onSuccees Halal", token);
                } catch (JSONException e) {

                }


            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                PaymentMethodNonce paymentMethodNonce = data.getParcelableExtra(
                        BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE
                );
                String nonce = paymentMethodNonce.getNonce();
                Log.e("NONCE", nonce);
                // Send the nonce to your server.
                postNonceToServer(nonce);
            }
        }
    }

    void postNonceToServer(String nonce) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("payment_method_nonce", nonce);
        params.put("amount", new BigDecimal("100.00"));
        client.post(HALAL_TRANSACTION, params,
                new JsonHttpResponseHandler() {
                    // Your implementation here


                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("postNonceToServer", response.toString());
                    }
                }
        );
    }
}
