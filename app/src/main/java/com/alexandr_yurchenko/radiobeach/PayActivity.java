package com.alexandr_yurchenko.radiobeach;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PayActivity extends AppCompatActivity {

    private final String threeUrl = "https://yoomoney.ru/quickpay/button-widget?targets=%D0%9F%D0%BE%D0%B4%D0%BF%D0%B8%D1%81%D0%BA%D0%B0%203%20%D0%BC%D0%B5%D1%81%D1%8F%D1%86%D0%B0&default-sum=199&button-text=11&any-card-payment-type=on&button-size=m&button-color=orange&successURL=&quickpay=small&account=410014590614828&";
    private final String sixUrl = "https://yoomoney.ru/quickpay/button-widget?targets=%D0%9F%D0%BE%D0%B4%D0%BF%D0%B8%D1%81%D0%BA%D0%B0%206%20%D0%BC%D0%B5%D1%81%D1%8F%D1%86%D0%B5%D0%B2&default-sum=499&button-text=11&any-card-payment-type=on&button-size=m&button-color=orange&successURL=&quickpay=small&account=410014590614828&";
    private final String twelveUrl = "https://yoomoney.ru/quickpay/button-widget?targets=%D0%9F%D0%BE%D0%B4%D0%BF%D0%B8%D1%81%D0%BA%D0%B0%2012%20%20%D0%BC%D0%B5%D1%81%D1%8F%D1%86%D0%B5%D0%B2&default-sum=799&button-text=11&any-card-payment-type=on&button-size=m&button-color=orange&successURL=&quickpay=small&account=410014590614828&";

    private WebView webView;
    private String dataUrl;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor myEdit;

    private Boolean isThree = false, isSix = false, isTwelve = false;

    private ConstraintLayout checkContainer;
    private CheckBox checkBox;
    private Button threeBtn, sixBtn, twelveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        checkContainer = findViewById(R.id.checkContainer);
        checkBox = findViewById(R.id.payCheckBox);
        threeBtn = findViewById(R.id.threeMonth);
        sixBtn = findViewById(R.id.sixMonth);
        twelveBtn = findViewById(R.id.twelveMonth);

        threeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()){
                    checkContainer.setVisibility(View.INVISIBLE);
                    webView.setVisibility(View.VISIBLE);
                    isThree = true;
                    webView.loadUrl(threeUrl);
                } else {
                    Toast.makeText(PayActivity.this, "Вам нужно подтвердить условия пользованием подпиской!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sixBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()){
                    checkContainer.setVisibility(View.INVISIBLE);
                    webView.setVisibility(View.VISIBLE);
                    isSix = true;
                    webView.loadUrl(sixUrl);
                } else {
                    Toast.makeText(PayActivity.this, "Вам нужно подтвердить условия пользованием подпиской!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        twelveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()){
                    checkContainer.setVisibility(View.INVISIBLE);
                    webView.setVisibility(View.VISIBLE);
                    isTwelve = true;
                    webView.loadUrl(twelveUrl);
                } else {
                    Toast.makeText(PayActivity.this, "Вам нужно подтвердить условия пользованием подпиской!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class MyWebViewClient extends WebViewClient {
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            System.out.println(request.getUrl().toString());
            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            System.out.println(url);
            dataUrl = url;
            if (dataUrl.contains("transfer/process/success")){
                System.out.println("\n\n Succes \n\n");
                myEdit.putString("Subscription", "Success");
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss zz yyyy");
                if (isThree){
                    Date currentDate = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(currentDate);
                    cal.add(Calendar.MONTH, 3);
                    myEdit.putString("offTime", sdf.format(cal.getTime()));
                } else if (isSix){
                    Date currentDate = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(currentDate);
                    cal.add(Calendar.MONTH, 6);
                    myEdit.putString("offTime", sdf.format(cal.getTime()));
                } else if (isTwelve){
                    Date currentDate = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(currentDate);
                    cal.add(Calendar.MONTH, 12);
                    myEdit.putString("offTime", sdf.format(cal.getTime()));
                }
                myEdit.commit();
                Intent intent = new Intent(getApplicationContext(), SubscriptionActivity.class);
                startActivity(intent);
                finish();
            }
        }

        // Для старых устройств
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}