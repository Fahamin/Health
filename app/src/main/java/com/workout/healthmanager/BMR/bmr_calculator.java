package com.workout.healthmanager.BMR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.workout.healthmanager.R;
import com.workout.healthmanager.general.MyApplication;
import com.workout.healthmanager.utils.GlobalFunction;
import com.workout.healthmanager.utils.SharedPreferenceManager;
import com.workout.healthmanager.utils.TypefaceManager;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.interfaces.NetworkRequestCheckListener;
import java.io.PrintStream;
import java.util.ArrayList;


public class bmr_calculator extends AppCompatActivity {
    String TAG = getClass().getSimpleName();
    AdView adView;
    ArrayAdapter<String> adapter_gender;
    ArrayAdapter<String> adapter_height;
    ArrayAdapter<String> adapter_weight;
    double age;
    ArrayList<String> arraylist_gender = new ArrayList<>();
    ArrayList<String> arraylist_height = new ArrayList<>();
    ArrayList<String> arraylist_weigth = new ArrayList<>();
    double bmr;
    double bmr_height;
    double bmr_weight;
    EditText et_age;
    EditText et_height;
    EditText et_weight;
    Bundle extras;
    String gender;
    GlobalFunction globalFunction;
    String height_unit;
    float inserted_height;
    float inserted_weight;
    ImageView iv_back;
    ListView listViewGender;
    ListView listViewHeight;
    ListView listViewWeight;
    private PopupWindow popupWindowGender;
    private PopupWindow popupWindowHeight;
    private PopupWindow popupWindowWeight;
    RelativeLayout rl_main;
    SharedPreferenceManager sharedPreferenceManager;
    TextView tv_bmr;
    TextView tv_gender;
    TextView tv_genderunit;
    TextView tv_heightunit;
    TextView tv_search_bmr;
    TextView tv_weightunit;
    TypefaceManager typefaceManager;
    String weight_unit;


    public void attachBaseContext(Context context) {
        super.attachBaseContext(uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper.wrap(context));
    }


    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.bmr_calculator);
        globalFunction = new GlobalFunction(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        typefaceManager = new TypefaceManager(getAssets(), this);
        globalFunction.set_locale_language();
        globalFunction.sendAnalyticsData(TAG, TAG);
       /* adView = (AdView) findViewById(R.id.adView);
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
        et_height = (EditText) findViewById(R.id.et_height);
        et_weight = (EditText) findViewById(R.id.et_weight);
        et_age = (EditText) findViewById(R.id.et_age);
        rl_main = (RelativeLayout) findViewById(R.id.rl_main);
        tv_bmr = (TextView) findViewById(R.id.tv_bmr);
        tv_heightunit = (TextView) findViewById(R.id.tv_heightunit);
        tv_weightunit = (TextView) findViewById(R.id.tv_weightunit);
        tv_search_bmr = (TextView) findViewById(R.id.tv_search_bmr);
        tv_genderunit = (TextView) findViewById(R.id.tv_genderunit);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_bmr.setTypeface(typefaceManager.getBold());
        tv_search_bmr.setTypeface(typefaceManager.getBold());
        extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("from", "bmr").equals("bmr")) {
                tv_bmr.setText(getString(R.string.bmr_Calculator));
                tv_search_bmr.setText(getString(R.string.Calculate_BMR));
//                rl_main.setBackgroundResource(R.drawable.background_gradient1);
            } else {
                tv_bmr.setText(getString(R.string.rmr_Calculator));
                tv_search_bmr.setText(getString(R.string.Calculate_RMR));
//                rl_main.setBackgroundResource(R.drawable.background_gradient7);
            }
        }
        et_height.setTypeface(typefaceManager.getLight());
        et_weight.setTypeface(typefaceManager.getLight());
        et_age.setTypeface(typefaceManager.getLight());
        tv_heightunit.setTypeface(typefaceManager.getLight());
        tv_weightunit.setTypeface(typefaceManager.getLight());
        tv_genderunit.setTypeface(typefaceManager.getLight());
        tv_gender.setTypeface(typefaceManager.getLight());
        height_unit = getString(R.string.feet);
        weight_unit = getString(R.string.lbs);
        tv_heightunit.setOnClickListener(showPopupWindowHeight());
        tv_weightunit.setOnClickListener(showPopupWindow_Weight());
        tv_gender.setOnClickListener(showPopupWindowGender());
        arraylist_gender.clear();
        arraylist_gender.add(getString(R.string.Male));
        arraylist_gender.add(getString(R.string.Female));
        adapter_gender = new ArrayAdapter<>(this, R.layout.spinner_item, R.id.text1, arraylist_gender);
        arraylist_height.clear();
        arraylist_height.add(getString(R.string.feet));
        arraylist_height.add(getString(R.string.cm));
        arraylist_weigth.clear();
        arraylist_weigth.add(getString(R.string.kg));
        arraylist_weigth.add(getString(R.string.lbs));
        adapter_height = new ArrayAdapter<>(this, R.layout.spinner_item, R.id.text1, arraylist_height);
        adapter_weight = new ArrayAdapter<>(this, R.layout.spinner_item, R.id.text1, arraylist_weigth);
        if (VERSION.SDK_INT >= 21) {
            getWindow().addFlags(67108864);
        }
        iv_back.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });
        globalFunction.sendAnalyticsData(TAG, TAG);
        tv_search_bmr.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (et_age.getText().toString().trim().equals("")) {
                    et_age.setError(getString(R.string.Enter_Age));
                } else if (et_height.getText().toString().trim().equals("") || et_height.getText().toString().trim().equals(".")) {
                    et_height.setError(getString(R.string.Enter_Height));
                } else if (et_weight.getText().toString().trim().equals("") || et_weight.getText().toString().trim().equals(".")) {
                    et_weight.setError(getString(R.string.Enter_Weight));
                } else {
                    height_unit = tv_heightunit.getText().toString();
                    weight_unit = tv_weightunit.getText().toString();
                    inserted_weight = Float.parseFloat(et_weight.getText().toString());
                    inserted_height = Float.parseFloat(et_height.getText().toString());
                    age = (double) Integer.parseInt(et_age.getText().toString());
                    gender = tv_gender.getText().toString().trim();
                    int random = ((int) (Math.random() * 2.0d)) + 1;
                    PrintStream printStream = System.out;
                    StringBuilder sb = new StringBuilder();
                    sb.append("random_number==>");
                    sb.append(random);
                    printStream.println(sb.toString());
                    if (random == 2) {
                        showIntertitial();
                    } else {
                        calculate();
                    }
                }
            }
        });
        iv_back.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    public void calculate() {
        StringBuilder sb = new StringBuilder();
        sb.append("inserted_weight");
        sb.append(inserted_weight);
        Log.d("inserted_weight", sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("inserted_height");
        sb2.append(inserted_height);
        Log.d("inserted_height", sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("height_unit");
        sb3.append(height_unit);
        Log.d("height_unit", sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("weight_unit");
        sb4.append(weight_unit);
        Log.d("weight_unit", sb4.toString());
        if (height_unit.equalsIgnoreCase(getString(R.string.cm))) {
            bmr_height = (double) inserted_height;
        } else {
            bmr_height = (double) (inserted_height / 0.032808f);
        }
        if (weight_unit.equalsIgnoreCase(getString(R.string.kg))) {
            bmr_weight = (double) inserted_weight;
        } else {
            bmr_weight = (double) (inserted_weight / 2.2046f);
        }
        if (gender.equalsIgnoreCase(getString(R.string.Male))) {
            bmr = (((bmr_weight * 10.0d) + (bmr_height * 6.25d)) - (age * 5.0d)) + 5.0d;
            StringBuilder sb5 = new StringBuilder();
            sb5.append("bmr_male->");
            sb5.append(bmr);
            Log.d("bmr_male->", sb5.toString());
        } else {
            bmr = (((bmr_weight * 10.0d) + (bmr_height * 6.25d)) - (age * 5.0d)) - 161.0d;
            StringBuilder sb6 = new StringBuilder();
            sb6.append("bmr_female->");
            sb6.append(bmr);
            Log.d("bmr_female->", sb6.toString());
        }
        Intent intent = new Intent(this, BMR_Result.class);
        intent.putExtra("bmr", bmr);
        intent.putExtra("from", extras.getString("from", "bmr"));
        startActivity(intent);
    }

    private OnClickListener showPopupWindowHeight() {
        return new OnClickListener() {
            public void onClick(View view) {
                popupWindowHeight().showAsDropDown(view, 0, 0);
            }
        };
    }


    @SuppressLint("ResourceType")
    public PopupWindow popupWindowHeight() {
        popupWindowHeight = new PopupWindow(this);
        listViewHeight = new ListView(this);
        listViewHeight.setDividerHeight(0);
        listViewHeight.setAdapter(adapter_height);
        listViewHeight.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                tv_heightunit.setText((CharSequence) arraylist_height.get(i));
                dismissPopupHeight();
            }
        });
        popupWindowHeight.setFocusable(true);
        popupWindowHeight.setWidth(tv_heightunit.getMeasuredWidth());
        popupWindowHeight.setHeight(-2);
        popupWindowHeight.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), 17170443));
        popupWindowHeight.setContentView(listViewHeight);
        return popupWindowHeight;
    }


    public void dismissPopupHeight() {
        if (popupWindowHeight != null) {
            popupWindowHeight.dismiss();
        }
    }

    private OnClickListener showPopupWindow_Weight() {
        return new OnClickListener() {
            public void onClick(View view) {
                popupWindowWeight().showAsDropDown(view, 0, 0);
            }
        };
    }


    @SuppressLint("ResourceType")
    public PopupWindow popupWindowWeight() {
        popupWindowWeight = new PopupWindow(this);
        listViewWeight = new ListView(this);
        listViewWeight.setDividerHeight(0);
        listViewWeight.setAdapter(adapter_weight);
        listViewWeight.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                StringBuilder sb = new StringBuilder();
                sb.append("position->");
                sb.append(i);
                Log.d("position", sb.toString());
                StringBuilder sb2 = new StringBuilder();
                sb2.append("arraylist_weigth->");
                sb2.append((String) arraylist_weigth.get(i));
                Log.d("arraylist_weigth", sb2.toString());
                tv_weightunit.setText((CharSequence) arraylist_weigth.get(i));
                dismissPopupTopics();
            }
        });
        popupWindowWeight.setFocusable(true);
        popupWindowWeight.setWidth(tv_weightunit.getMeasuredWidth());
        popupWindowWeight.setHeight(-2);
        popupWindowWeight.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), 17170443));
        popupWindowWeight.setContentView(listViewWeight);
        return popupWindowWeight;
    }


    public void dismissPopupTopics() {
        if (popupWindowWeight != null) {
            popupWindowWeight.dismiss();
        }
    }


    @SuppressLint("ResourceType")
    public PopupWindow popupWindowGender() {
        popupWindowGender = new PopupWindow(this);
        listViewGender = new ListView(this);
        listViewGender.setDividerHeight(0);
        listViewGender.setAdapter(adapter_gender);
        listViewGender.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                tv_gender.setText((CharSequence) arraylist_gender.get(i));
                tv_genderunit.setText((CharSequence) arraylist_gender.get(i));
                dismissPopupGender();
            }
        });
        popupWindowGender.setFocusable(true);
        popupWindowGender.setWidth(tv_gender.getMeasuredWidth());
        popupWindowGender.setHeight(-2);
        popupWindowGender.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), 17170443));
        popupWindowGender.setContentView(listViewGender);
        return popupWindowGender;
    }


    public void dismissPopupGender() {
        if (popupWindowGender != null) {
            popupWindowGender.dismiss();
        }
    }

    private OnClickListener showPopupWindowGender() {
        return new OnClickListener() {
            public void onClick(View view) {
                popupWindowGender().showAsDropDown(view, 0, 0);
            }
        };
    }

    public void showIntertitial() {
        if (sharedPreferenceManager.get_Remove_Ad().booleanValue()) {
            calculate();
        } else if (MyApplication.interstitial == null || !MyApplication.interstitial.isLoaded()) {
            if (!MyApplication.interstitial.isLoading()) {
                ConnectionBuddy.getInstance().hasNetworkConnection(new NetworkRequestCheckListener() {
                    public void onNoResponse() {
                    }

                    public void onResponseObtained() {
                        MyApplication.interstitial.loadAd(new Builder().build());
                    }
                });
            }
            calculate();
        } else {
            MyApplication.interstitial.show();
        }
    }

    public void onBackPressed() {
        adView.setVisibility(View.GONE);
        ActivityCompat.finishAfterTransition(this);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        onBackPressed();
        return true;
    }


    public void onResume() {
        super.onResume();
        if (!sharedPreferenceManager.get_Remove_Ad().booleanValue() && MyApplication.interstitial != null && !MyApplication.interstitial.isLoaded() && !MyApplication.interstitial.isLoading()) {
            ConnectionBuddy.getInstance().hasNetworkConnection(new NetworkRequestCheckListener() {
                public void onNoResponse() {
                }

                public void onResponseObtained() {
                    MyApplication.interstitial.loadAd(new Builder().build());
                }
            });
        }
        if (!sharedPreferenceManager.get_Remove_Ad().booleanValue()) {
            MyApplication.interstitial.setAdListener(new AdListener() {
                public void onAdClosed() {
                    super.onAdClosed();
                    MyApplication.interstitial.loadAd(new Builder().build());
                    calculate();
                }

                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    if (MyApplication.interstitial != null && !MyApplication.interstitial.isLoading()) {
                        ConnectionBuddy.getInstance().hasNetworkConnection(new NetworkRequestCheckListener() {
                            public void onNoResponse() {
                            }

                            public void onResponseObtained() {
                                MyApplication.interstitial.loadAd(new Builder().build());
                            }
                        });
                    }
                }
            });
        }
    }
}
