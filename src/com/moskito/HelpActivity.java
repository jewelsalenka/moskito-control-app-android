package com.moskito;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import com.example.moskito_control_app_android.R;

/**
 * User: Olenka Shemshey
 * Date: 12.10.13
 */
public class HelpActivity extends Activity {
    @Override
    public void onCreate(Bundle saveBundle){
        super.onCreate(saveBundle);
        setContentView(R.layout.help);

        WebView webView = (WebView) findViewById(R.id.web_help);
        webView.loadUrl("file:///android_asset/Help/index.html");

        View backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.static_in_app, R.anim.push_down_out_app);
            }
        });
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.static_in_app, R.anim.push_down_out_app);
    }
}
