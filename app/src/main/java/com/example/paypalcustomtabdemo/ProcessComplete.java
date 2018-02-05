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
import java.util.List;
import java.util.Properties;

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

        List<String> params = getIntent().getData().getPathSegments();
        TextView textView = (TextView) findViewById(R.id.status);
        final TextView textLoader = (TextView) findViewById(R.id.statusLoading);
        final HighlightJsView highlightJsView = (HighlightJsView) findViewById(R.id.highlight_view);
        if(params.get(0).equals("success")){
            textView.setText("Transaction Sucesss");
            try{

                AsyncHttpClient client = new AsyncHttpClient();
                client.setTimeout(60000);
                Properties properties = new Properties();
                AssetManager assetManager = getApplicationContext().getAssets();
                InputStream inputStream = assetManager.open("app.properties");
                properties.load(inputStream);
                String url = properties.get("getPaymentDetails").toString();
                url = url+"?token="+params.get(1)+"&payerID="+params.get(2);
                JSONObject obj = new JSONObject();
                StringEntity requestData = new StringEntity(obj.toString());

                client.post( url,  new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                        // pass the result to an sdk
                        highlightJsView.setTheme(Theme.ANDROID_STUDIO);
                        highlightJsView.setHighlightLanguage(Language.AUTO_DETECT);
                        try{
                            highlightJsView.setSource(String.valueOf(String.valueOf(response.toString(4))));
                            textLoader.setVisibility(View.INVISIBLE);
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                });

            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            textView.setText("Transaction Failed/Pending");

        }
    }
}
