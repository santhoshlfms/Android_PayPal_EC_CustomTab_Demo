package com.example.paypalcustomtabdemo;

import android.content.ActivityNotFoundException;
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
import com.paypal.android.lib.riskcomponent.RiskComponent;
import com.paypal.android.lib.riskcomponent.SourceApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.Header;

public class MainActivity extends AppCompatActivity {
    private final int CHROME_CUSTOM_TAB_REQUEST_CODE = 100;
    Map<String, Object> additionalParams = new HashMap<String, Object>();

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
            Log.d("firing ","Fire in the hole");
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button btnCart = (Button) findViewById(R.id.cartbutton);
        // valid for paypal only
        final String  paringId = RiskComponent.getInstance().init(this.getApplicationContext(), SourceApp.UNKNOWN, "",additionalParams);
        String againParingId = RiskComponent.getInstance().init(this.getApplicationContext(), paringId, SourceApp.UNKNOWN, "",additionalParams);
        Log.d("riskparingId :",paringId);
        Log.d("againParingId :",againParingId);
        // we might use this for other payments
        String riskParingId = RiskComponent.getInstance().getPairingID();
        //after getting risk paring id
        Log.d("body :", riskParingId);
        String riskPayload = RiskComponent.getInstance().getRiskPayload().toString();
        //debug
        Log.d("dsadsadsa",RiskComponent.getInstance().getRiskPayload().names().toString());
        Log.d("riskPayload" , riskPayload);

        System.setProperty("dyson.debug.mode", Boolean.TRUE.toString());

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.hannam18014850.qa.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=EC-142650723S898581C";
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
                Log.d("URL" , url);
                customTabsIntent.intent.setData(Uri.parse(url));
                startActivityForResult(customTabsIntent.intent, CHROME_CUSTOM_TAB_REQUEST_CODE);

                /*try{
                    JSONObject productJson = new JSONObject();
                    productJson.put("description","This is camera");
                    productJson.put("shipping","5");
                    productJson.put("tax","2");
                    productJson.put("shipping_discount","-3");
                    productJson.put("total","17");
                    productJson.put("currency","INR");
                    productJson.put("intent","sale");
                    productJson.put("subtotal","10.00");
                    productJson.put("name","Camera");
                    productJson.put("price","10.00");
                    productJson.put("quantity","1");
                    productJson.put("handling_fee","1");
                    productJson.put("insurance","2");
                    productJson.put("customFlag","false");
                    productJson.put("return_url","com.example.paypalcustomtabdemo");
                    productJson.put("cancel_url","https://node-paypal-express-sever.herokuapp.com/error.html");

                    showLoader(true); // set loader visible

                    // send the risk id
                    productJson.put("riskParingId",paringId);
                    // make api call here ....

                    AsyncHttpClient client = new AsyncHttpClient();
                    StringEntity requestData = new StringEntity(productJson.toString());
                    Properties properties = new Properties();
                    AssetManager assetManager = getApplicationContext().getAssets();
                    InputStream inputStream = assetManager.open("app.properties");
                    properties.load(inputStream);
                    String url = properties.get("createPayments").toString();
                    Log.d("TAG", "onSuccess: ********************* onCancel");
                    client.post(getApplicationContext(), url, requestData, "application/json", new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                            // code block to open up URL in Custom Tab
                            try{
                                String url = Utility.getUrlFromJSONArray(response);
                                Log.d("TAG", "onSuccess: ");
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
                                    Log.d("URL" , url);
                                    customTabsIntent.intent.setData(Uri.parse(url));
                                    startActivityForResult(customTabsIntent.intent, CHROME_CUSTOM_TAB_REQUEST_CODE);
                                }
                            }
                            catch(JSONException e) {
                                e.printStackTrace();
                            }
                            catch(ActivityNotFoundException a) {
                                Toast.makeText(getApplicationContext(),"No Browser Found",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        });
    }
}
