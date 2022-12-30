package com.workout.healthmanager.alcohol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
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


public class Alcohol_Calculator extends Activity {
    double BACinPer;
    String TAG = getClass().getSimpleName();
    AdView adView;
    ArrayAdapter<String> adapter_gender;
    ArrayAdapter<String> adapter_time;
    ArrayAdapter<String> adapter_weight;
    double alcohol_level;
    ArrayList<String> arraylist_gender = new ArrayList<>();
    ArrayList<String> arraylist_time = new ArrayList<>();
    ArrayList<String> arraylist_weight = new ArrayList<>();
    EditText et_alcohol_level;
    EditText et_drinkvolume;
    EditText et_timepassed;
    EditText et_weight;
    String gender;
    double gender_ratio;
    GlobalFunction globalFunction;
    ImageView iv_back;
    ListView listViewGender;
    ListView listViewHeight;
    ListView listViewWeight;
    private PopupWindow popupWindowGender;
    private PopupWindow popupWindowTime;
    private PopupWindow popupWindowWeight;
    SharedPreferenceManager sharedPreferenceManager;
    double timePassed;
    String timePassed_unit;
    double total_alcohol;
    TextView tv_alcohol;
    TextView tv_gender;
    TextView tv_genderunit;
    TextView tv_search_bloodalcohol_content;
    TextView tv_timeunit;
    TextView tv_weightunit;
    TypefaceManager typefaceManager;
    double volume;
    double weight;
    String weight_unit;


    public void attachBaseContext(Context context) {
        super.attachBaseContext(uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper.wrap(context));
    }


    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.alcohol_calculator);
        globalFunction = new GlobalFunction(this);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        typefaceManager = new TypefaceManager(getAssets(), this);
        globalFunction.set_locale_language();
        et_drinkvolume = (EditText) findViewById(R.id.et_drinkvolume);
        et_alcohol_level = (EditText) findViewById(R.id.et_alcohol_level);
        et_timepassed = (EditText) findViewById(R.id.et_timepassed);
        et_weight = (EditText) findViewById(R.id.et_weight);
        tv_search_bloodalcohol_content = (TextView) findViewById(R.id.tv_search_bloodalcohol_content);
        tv_timeunit = (TextView) findViewById(R.id.tv_timeunit);
        tv_weightunit = (TextView) findViewById(R.id.tv_weightunit);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_genderunit = (TextView) findViewById(R.id.tv_genderunit);
        tv_alcohol = (TextView) findViewById(R.id.tv_alcohol);
        adView = (AdView) findViewById(R.id.adView);
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_alcohol.setTypeface(typefaceManager.getBold());
        tv_search_bloodalcohol_content.setTypeface(typefaceManager.getBold());
        et_drinkvolume.setTypeface(typefaceManager.getLight());
        et_alcohol_level.setTypeface(typefaceManager.getLight());
        et_timepassed.setTypeface(typefaceManager.getLight());
        tv_timeunit.setTypeface(typefaceManager.getLight());
        et_weight.setTypeface(typefaceManager.getLight());
        tv_weightunit.setTypeface(typefaceManager.getLight());
        tv_genderunit.setTypeface(typefaceManager.getLight());
        tv_gender.setTypeface(typefaceManager.getLight());
        if (VERSION.SDK_INT >= 21) {
            getWindow().setFlags(67108864, 67108864);
        }
        iv_back.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });
        globalFunction.sendAnalyticsData(TAG, TAG);
        arraylist_time.clear();
        arraylist_time.add(getString(R.string.hour));
        arraylist_time.add(getString(R.string.Minute));
        arraylist_time.add(getString(R.string.Day));
        adapter_time = new ArrayAdapter<>(this, R.layout.spinner_item, R.id.text1, arraylist_time);
        arraylist_weight.clear();
        arraylist_weight.add(getString(R.string.lbs));
        arraylist_weight.add(getString(R.string.kg));
        adapter_weight = new ArrayAdapter<>(this, R.layout.spinner_item, R.id.text1, arraylist_weight);
        arraylist_gender.clear();
        arraylist_gender.add(getString(R.string.Male));
        arraylist_gender.add(getString(R.string.Female));
        adapter_gender = new ArrayAdapter<>(this, R.layout.spinner_item, R.id.text1, arraylist_gender);
        tv_weightunit.setOnClickListener(showPopupWindowWeight());
        tv_timeunit.setOnClickListener(showPopupWindowTime());
        tv_genderunit.setOnClickListener(showPopupWindowGender());
        tv_search_bloodalcohol_content.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (et_drinkvolume.getText().toString().trim().equals("") || et_drinkvolume.getText().toString().trim().equals(".")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.Enter_Drink_volume), Toast.LENGTH_SHORT).show();
                } else if (et_alcohol_level.getText().toString().trim().equals("") || et_alcohol_level.getText().toString().trim().equals(".")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.Enter_Alcohol_level), Toast.LENGTH_SHORT).show();
                } else if (et_timepassed.getText().toString().trim().equals("") || et_timepassed.getText().toString().trim().equals(".")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.Enter_time_passed_sence_drinking), Toast.LENGTH_SHORT).show();
                } else if (et_weight.getText().toString().trim().equals("") || et_weight.getText().toString().trim().equals(".")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.Enter_weight), Toast.LENGTH_SHORT).show();
                } else {
                    volume = Double.parseDouble(et_drinkvolume.getText().toString().trim());
                    volume *= 0.033814d;
                    alcohol_level = Double.parseDouble(et_alcohol_level.getText().toString().trim());
                    total_alcohol = (volume * alcohol_level) / 100.0d;
                    get_genderratio();
                    get_weight();
                    get_time_passed();
                    BACinPer = (((total_alcohol * 5.14d) / weight) * gender_ratio) - (timePassed * 0.015d);
                    int random = ((int) (Math.random() * 2.0d)) + 1;
                    PrintStream printStream = System.out;
                    StringBuilder sb = new StringBuilder();
                    sb.append("random_number==>");
                    sb.append(random);
                    printStream.println(sb.toString());
                    if (random == 2) {
                        showIntertitial();
                        return;
                    }
                    Intent intent = new Intent(Alcohol_Calculator.this, Alcohol_Result.class);
                    intent.putExtra("BACinPer", BACinPer);
                    startActivity(intent);
                }
            }
        });
    }

    private OnClickListener showPopupWindowTime() {
        return new OnClickListener() {
            public void onClick(View view) {
                popupWindowTime().showAsDropDown(view, 0, 0);
            }
        };
    }

    private OnClickListener showPopupWindowWeight() {
        return new OnClickListener() {
            public void onClick(View view) {
                popupWindowWeight().showAsDropDown(view, 0, 0);
            }
        };
    }

    private OnClickListener showPopupWindowGender() {
        return new OnClickListener() {
            public void onClick(View view) {
                popupWindowGender().showAsDropDown(view, 0, 0);
            }
        };
    }


    @SuppressLint("ResourceType")
    public PopupWindow popupWindowTime() {
        popupWindowTime = new PopupWindow(this);
        listViewHeight = new ListView(this);
        listViewHeight.setDividerHeight(0);
        listViewHeight.setAdapter(adapter_time);
        listViewHeight.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                tv_timeunit.setText((CharSequence) arraylist_time.get(i));
                dismissPopupTime();
            }
        });
        popupWindowTime.setFocusable(true);
        popupWindowTime.setWidth(tv_timeunit.getMeasuredWidth());
        popupWindowTime.setHeight(-2);
        popupWindowTime.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), 17170443));
        popupWindowTime.setContentView(listViewHeight);
        return popupWindowTime;
    }


    @SuppressLint("ResourceType")
    public PopupWindow popupWindowWeight() {
        popupWindowWeight = new PopupWindow(this);
        listViewWeight = new ListView(this);
        listViewWeight.setDividerHeight(0);
        listViewWeight.setAdapter(adapter_weight);
        listViewWeight.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                tv_weightunit.setText((CharSequence) arraylist_weight.get(i));
                dismissPopupWeight();
            }
        });
        popupWindowWeight.setFocusable(true);
        popupWindowWeight.setWidth(tv_weightunit.getMeasuredWidth());
        popupWindowWeight.setHeight(-2);
        popupWindowWeight.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), 17170443));
        popupWindowWeight.setContentView(listViewWeight);
        return popupWindowWeight;
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
        popupWindowGender.setWidth(tv_genderunit.getMeasuredWidth());
        popupWindowGender.setHeight(-2);
        popupWindowGender.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), 17170443));
        popupWindowGender.setContentView(listViewGender);
        return popupWindowGender;
    }


    public void dismissPopupTime() {
        if (popupWindowTime != null) {
            popupWindowTime.dismiss();
        }
    }


    public void dismissPopupWeight() {
        if (popupWindowWeight != null) {
            popupWindowWeight.dismiss();
        }
    }


    public void dismissPopupGender() {
        if (popupWindowGender != null) {
            popupWindowGender.dismiss();
        }
    }

    public void get_genderratio() {
        gender = tv_gender.getText().toString().trim();
        if (gender.equalsIgnoreCase(getString(R.string.Male))) {
            gender_ratio = 0.73d;
        } else {
            gender_ratio = 0.66d;
        }
    }

    public void get_weight() {
        if (!et_weight.getText().toString().trim().equals(".")) {
            weight = Double.parseDouble(et_weight.getText().toString().trim());
            weight_unit = tv_weightunit.getText().toString().trim();
            if (weight_unit.equalsIgnoreCase(getString(R.string.kg))) {
                weight *= 2.204622d;
                return;
            }
            return;
        }
        weight = 1.0d;
    }

    public void get_time_passed() {
        if (!et_timepassed.getText().toString().equals(".")) {
            timePassed = Double.parseDouble(et_timepassed.getText().toString().trim());
            timePassed_unit = tv_timeunit.getText().toString().trim();
            if (timePassed_unit.equalsIgnoreCase(getString(R.string.Minute))) {
                timePassed /= 60.0d;
            } else if (timePassed_unit.equalsIgnoreCase(getString(R.string.day))) {
                timePassed *= 24.0d;
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        onBackPressed();
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
        adView.setVisibility(View.GONE);
        ActivityCompat.finishAfterTransition(Alcohol_Calculator.this);
    }

    public void showIntertitial() {
        if (sharedPreferenceManager.get_Remove_Ad().booleanValue()) {
            Intent intent = new Intent(Alcohol_Calculator.this, Alcohol_Result.class);
            intent.putExtra("BACinPer", BACinPer);
            startActivity(intent);
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
            Intent intent2 = new Intent(Alcohol_Calculator.this, Alcohol_Result.class);
            intent2.putExtra("BACinPer", BACinPer);
            startActivity(intent2);
        } else {
            MyApplication.interstitial.show();
        }
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
                    Intent intent = new Intent(Alcohol_Calculator.this, Alcohol_Result.class);
                    intent.putExtra("BACinPer", BACinPer);
                    startActivity(intent);
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
