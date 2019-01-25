package com.example.paypalcustomtabdemo;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pddstudio.highlightjs.HighlightJsView;
import com.pddstudio.highlightjs.models.Language;
import com.pddstudio.highlightjs.models.Theme;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.Header;

/**
 * Created by sannelson on 11/17/2017.
 */

public class ProcessComplete extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_complete);
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.d("LOG_TAG", String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }
        //System.out.println("INT DATA : " + i.getStringExtra("PayerID"));

        System.out.println("URI : " + i.getData());
        Uri dataFromPayPal = i.getData();
        if(dataFromPayPal  != null) {
            final  String  paymentId = dataFromPayPal.getQueryParameter("paymentId");
            final  String  payerId = dataFromPayPal.getQueryParameter("PayerID");

            System.out.println("paymentId : " + paymentId);
            System.out.println("PayerID : " + payerId);


            try{
                AsyncHttpClient client = new AsyncHttpClient();
                client.setTimeout(60000);
                final Properties properties = new Properties();
                AssetManager assetManager = getApplicationContext().getAssets();
                InputStream inputStream = assetManager.open("app.properties");
                properties.load(inputStream);
                String url = properties.get("getPaymentDetails").toString();
                url = url + "?token=" + paymentId + "&payerID=" + "31343242";
                JSONObject obj = new JSONObject();
                StringEntity requestData = new StringEntity(obj.toString());

                client.post(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                        // pass the result to an sdk
                        try {
                            JSONObject payer = response.getJSONObject("payer");
                            JSONObject payer_info = payer.getJSONObject("payer_info");
                            String payerId = payer_info.getString("payer_id");
                            System.out.println("payerId : " + payerId);

                            String executeUrl = properties.get("executePayment").toString();
                            executeUrl = executeUrl + "?paymentId=" + paymentId + "&PayerID=" + payerId;
                            System.out.println("executeUrl : " + executeUrl);
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.get(executeUrl, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                                    // pass the result to an sdk
                                    final TextView textLoader = (TextView) findViewById(R.id.statusLoading);
                                    final HighlightJsView highlightJsView = (HighlightJsView) findViewById(R.id.highlight_view);
                                    highlightJsView.setTheme(Theme.ANDROID_STUDIO);
                                    highlightJsView.setHighlightLanguage(Language.AUTO_DETECT);
                                    try {
                                        highlightJsView.setSource(String.valueOf(String.valueOf(response.toString(4))));
                                        textLoader.setVisibility(View.INVISIBLE);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }




        /*Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();*/
        Log.d("URI","*******************************************************");
       /* System.out.println("URI : " + appLinkIntent.getData());
        System.out.println("id : " + appLinkData.getQueryParameter("id"));
        if(appLinkData != null) {
            String token = appLinkData.getQueryParameter("id");

            setContentView(R.layout.process_complete);
*/
            // Log.d("status", status);Log.d("payerId", payerId);Log.d("token", token);
           /* TextView textView = (TextView) findViewById(R.id.status);
            final TextView textLoader = (TextView) findViewById(R.id.statusLoading);
            final HighlightJsView highlightJsView = (HighlightJsView) findViewById(R.id.highlight_view);*/
         //   if (token.length() > 0) {
               // textView.setText("Transaction Sucesss :" + token);
            /*    try {

                    AsyncHttpClient client = new AsyncHttpClient();
                    client.setTimeout(60000);
                    Properties properties = new Properties();
                    AssetManager assetManager = getApplicationContext().getAssets();
                    InputStream inputStream = assetManager.open("app.properties");
                    properties.load(inputStream);
                    String url = properties.get("getPaymentDetails").toString();
                    url = url + "?token=" + token + "&payerID=" + payerId;
                    JSONObject obj = new JSONObject();
                    StringEntity requestData = new StringEntity(obj.toString());

                    client.post(url, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                            // pass the result to an sdk
                            highlightJsView.setTheme(Theme.ANDROID_STUDIO);
                            highlightJsView.setHighlightLanguage(Language.AUTO_DETECT);
                            try {
                                highlightJsView.setSource(String.valueOf(String.valueOf(response.toString(4))));
                                textLoader.setVisibility(View.INVISIBLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }*/

          /*  } else {
                textView.setText("Transaction Failed/Pending");

            }*/
        }
        // ATTENTION: This was auto-generated to handle app links.

  //  }
}
