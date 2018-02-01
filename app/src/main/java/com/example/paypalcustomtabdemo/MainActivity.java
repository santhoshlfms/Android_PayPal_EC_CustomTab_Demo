package com.example.paypalcustomtabdemo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Properties;

import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.Header;

public class MainActivity extends AppCompatActivity {
    private final int CHROME_CUSTOM_TAB_REQUEST_CODE = 100;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CHROME_CUSTOM_TAB_REQUEST_CODE) {
            Toast.makeText(getApplicationContext(), "Custom tab closed by tapping X button !", Toast.LENGTH_SHORT).show();
        }
    }

    public void showLoader(Boolean setVisibilty) {
        ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);
        int colorCodeDark = Color.parseColor("#253B80");
        progress.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));
        if(setVisibilty) {
            progress.setVisibility(View.VISIBLE);
        }else {
            progress.setVisibility(View.INVISIBLE);
        }
    }

    public boolean isPackageInstalled(String packageName) {
        try{
            PackageManager packageManager = getApplicationContext().getPackageManager();
            packageManager.getPackageInfo(packageName, 0);
            return true;
        }catch(PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button btnCart = (Button) findViewById(R.id.cartbutton);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    JSONObject productJson = new JSONObject();
                    productJson.put("description","This is camera");
                    productJson.put("shipping","5");
                    productJson.put("tax","2");
                    productJson.put("shipping_discount","-3");
                    productJson.put("total","17");
                    productJson.put("currency","USD");
                    productJson.put("intent","sale");
                    productJson.put("subtotal","10.00");
                    productJson.put("name","Camera");
                    productJson.put("price","10.00");
                    productJson.put("quantity","1");
                    productJson.put("handling_fee","1");
                    productJson.put("insurance","2");
                    productJson.put("customFlag","false");
                    showLoader(true); // set loader visible

                    // make api call here ....

                    AsyncHttpClient client = new AsyncHttpClient();
                    StringEntity requestData = new StringEntity(productJson.toString());
                    Properties properties = new Properties();
                    AssetManager assetManager = getApplicationContext().getAssets();
                    InputStream inputStream = assetManager.open("app.properties");
                    properties.load(inputStream);
                    String url = properties.get("createPayments").toString();
                    client.post(getApplicationContext(), url, requestData, "application/json", new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Got response from server" , Toast.LENGTH_SHORT).show();

                            try{
                                String url = Utility.getUrlFromJSONArray(response);
                                if(!url.equals(null)) {
                                    showLoader(false);

                                    String packageName = "com.android.chrome";
                                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                                    CustomTabsIntent customTabsIntent = builder.build();
                                    // check if chrome is installed if installed always open in chrome
                                    // so we can have OneTouch Feature !
                                    if(isPackageInstalled(packageName)) {
                                        customTabsIntent.intent.setPackage(packageName);
                                    }

                                    customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    customTabsIntent.intent.setData(Uri.parse(url));
                                    startActivityForResult(customTabsIntent.intent, CHROME_CUSTOM_TAB_REQUEST_CODE);

                                }
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
