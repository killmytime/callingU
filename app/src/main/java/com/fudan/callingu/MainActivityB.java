package com.fudan.callingu;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fudan.helper.ActivityCollector;
import com.fudan.helper.BaseActivity;
import com.fudan.helper.DataCheck;
import com.fudan.helper.HttpConnector;
import com.fudan.helper.HttpListener;
import com.fudan.helper.MobileInfoUtils;
import com.fudan.helper.NotifyService;
import com.fudan.helper.PermissionUtils;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by FanJin on 2017/1/19.
 */

public class MainActivityB extends BaseActivity implements View.OnClickListener,TencentLocationListener,HttpListener {

    private static final String TAG = "MainActivityB";

    private Button show_tell,aMeun;
    private Button switchBC;
    private TextView locationResult;
    private Intent intent,intent2;
    private DrawerLayout mDrawerLayout;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String myNumber;
    private String [] sos_str={"","突然晕倒","严重外伤","产科急救","儿科急救"};
    private String [] list_num=new String[100];
    private int [] list_sos=new int[100];
    /* lat,纬度，lng经度（两个都是缩写）*/
    private double[] list_lat = new double[100];
    private double[] list_lng = new double[100];
    private ArrayList<CharSequence> list_help;
    ListView listHelp;
    private SharedPreferences resource;
    private SpannableString str_sp1,str_sp2;
    StyleSpan styleSpan_A,styleSpan_B;

    TencentLocationManager locationManager;
    TencentLocationRequest request;
    int error;

    @Override
    public void onHttpFinish(int state, String responseData){
        //刷新求救者列表
        list_help = new ArrayList<CharSequence> ();
        if (((! responseData.equals("200")) && (! responseData.equals("401"))) ){
            parseJSON(responseData.toString());
            listHelp.setVisibility(View.VISIBLE);
            locationResult.setVisibility(View.INVISIBLE);
        }
        else {
            listHelp.setVisibility(View.INVISIBLE);
            locationResult.setVisibility(View.VISIBLE);
            if (state == -1){
                Toast.makeText(MainActivityB.this,"无法连接到服务器，请检查网络状态",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        pref= getSharedPreferences("loginStatus",MODE_PRIVATE);
        myNumber=pref.getString("number","0");
        //ToDo 发布前记得把这里注释去掉
//
////        if (!DataCheck.isBuser(myNumber)){
////            Intent intent=new Intent(MainActivityB.this,MainActivityC.class);
////            startActivity(intent);
////            finish();
////        }

        setContentView(R.layout.have_menu_b);

        mDrawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView=(NavigationView) findViewById(R.id.nav_view);
        View headView = navView.getHeaderView(0);
        TextView user = headView.findViewById(R.id.user_center_name);
        user.setText(myNumber);

        // 菜单栏事件监听
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.user_center_preference:
                        Intent intentMyPreference=new Intent(MainActivityB.this,MyPreference.class);
                        startActivity(intentMyPreference);
                        break;
                    case R.id.user_center_wallet:
                        Log.e("click","------------credit");
                        intent2=new Intent(MainActivityB.this,MyCredit.class);
                        startActivity(intent2);
                        break;
                    case R.id.user_center_permission:
                        Intent intentMyPermission=new Intent(MainActivityB.this,MyPermission.class);
                        startActivity(intentMyPermission);
                        break;
                    case R.id.user_center_help:
                        Log.e("click","------------help");
                        intent2=new Intent(MainActivityB.this,MyHelp.class);
                        startActivity(intent2);
                        break;
                    case R.id.user_center_about:
                        Log.e("click","------------about");
                        intent2=new Intent(MainActivityB.this,MyAbout.class);
                        startActivity(intent2);
                        break;
                    case R.id.user_center_logout:
                        new AlertDialog.Builder(MainActivityB.this)
                                .setTitle("退出登录")
                                .setMessage("确定要退出吗？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        pref= getSharedPreferences("loginStatus",MODE_PRIVATE);
                                        editor=pref.edit();
                                        editor.clear();
                                        editor.putBoolean("isOnline",false);
                                        editor.apply();
                                        ActivityCollector.finishAll();
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .setCancelable(false)
                                .create()
                                .show();
                        break;
                    default:
                }
                return true;
            }
        });
        locationResult = (TextView) findViewById(R.id.location_result);
        show_tell=(Button)findViewById(R.id.a_help);
        aMeun=(Button) findViewById(R.id.a_menu);
        switchBC=(Button)findViewById(R.id.switch_b);
        listHelp = (ListView)findViewById(R.id.list_help);

        intent=new Intent(MainActivityB.this,ShowMapB.class);
        show_tell.setOnClickListener(this);
        aMeun.setOnClickListener(this);
        if (DataCheck.isBuser(myNumber)){
            switchBC.setClickable(true);
            switchBC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(MainActivityB.this,MainActivityC.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        requestPermission();
        /**
         * self-start dialog would show if it is the first time to use this APP.
         */
        SharedPreferences historyFile = getSharedPreferences("historyFile",MODE_PRIVATE);
        boolean isFirst = historyFile.getBoolean("isFirst",true);
        if (isFirst){
            jumpStartInterface();
            SharedPreferences.Editor editor = historyFile.edit();
            editor.putBoolean("isFirst",false);
            editor.apply();
        }
        Intent service = new Intent(MainActivityB.this,NotifyService.class);
        startService(service);


        /**
         * set the effect for drawLayout
         */
        ImageView blurImageView = headView.findViewById(R.id.iv_blur);
        ImageView avatarImageView =headView.findViewById(R.id.iv_avatar);
        Glide.with(this).load(R.drawable.dddd)
                .apply(bitmapTransform(new BlurTransformation(25)))
                .into(blurImageView);

        Glide.with(this).load(R.drawable.ic_person)
                .apply(bitmapTransform(new CropCircleTransformation()))
                .into(avatarImageView);

        styleSpan_A  = new StyleSpan(Typeface.BOLD);
        styleSpan_B  = new StyleSpan(Typeface.BOLD);

        /**
         * check new version everyday
         */
        resource = getSharedPreferences("resource",MODE_PRIVATE);
        final long nowdate = System.currentTimeMillis();
        final long lastdata = resource.getLong("lastdate",0);
        Log.d(TAG, "onCreate: ----"+lastdata);
        Log.d(TAG, "onCreate: ----"+nowdate);
        if (nowdate-lastdata >24*60*60*1000){
            HttpConnector.checkNew(1,new HttpListener() {
                @Override
                public void onHttpFinish(int state, String responseData) {
                    if (state==-1){
                        Toast.makeText(MainActivityB.this,"无法连接到服务器，请检查网络状态",Toast.LENGTH_SHORT).show();
                    }else {
                        SharedPreferences.Editor editor = resource.edit();
                        editor.putLong("lastdate",nowdate);
                        editor.apply();
                        if (responseData.equals("new")){
                            new AlertDialog.Builder(MainActivityB.this)
                                    .setTitle("发现新版本")
                                    .setMessage("请升级APP！")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            HttpConnector.downloadNew(new HttpListener() {
                                                @Override
                                                public void onHttpFinish(int state, String responseData) {
                                                    if (state == -1){
                                                        Toast.makeText(MainActivityB.this,"无法连接到服务器，请检查网络状态",Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        //
                                                    }
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("以后再说", null)
                                    .create()
                                    .show();
                        }
                    }
                }
            });
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.a_help:
                //to server
                Intent intent=new Intent(MainActivityB.this,MyHelp.class);
                startActivity(intent);
                break;

            case R.id.a_menu:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
    }

    /**
     * Jump Start Interface
     */
    private void jumpStartInterface() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.app_user_auto_start);
            builder.setPositiveButton("立即设置",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MobileInfoUtils.jumpStartInterface(MainActivityB.this);
                        }
                    });
            builder.setNegativeButton("暂时不设置",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.setCancelable(false);
            builder.create().show();
        } catch (Exception e) {
        }
    }

//check the permission
    private void requestPermission(){
        List<String> permissionList=new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivityB.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivityB.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivityB.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivityB.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.CALL_PHONE);
        }
        if (!permissionList.isEmpty()){
            String [] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivityB.this,permissions,1);
        } else {
            requestLocation();

        }
    }
    /**
     * start to request the location data
     */
    private void requestLocation(){
        //mLocationClient.start();
        request = TencentLocationRequest.create()
                .setInterval(3*1000)
                .setAllowCache(true)
                .setRequestLevel(4);
        locationManager = TencentLocationManager.getInstance(this);
        error = locationManager.requestLocationUpdates(request, this);
    }

    /**
     * TencentLocationListener callback
     */
    @Override
    public void onLocationChanged(TencentLocation location, int error, String reason) {
        if (TencentLocation.ERROR_OK == error) {
            // 定位成功
            HttpConnector.downInformation(myNumber, location.getLatitude(),location.getLongitude(),"0",0,MainActivityB.this);
        } else {
            // 定位失败
        }
    }

    /**
     * TencentLocationListener callback
     */
    @Override
    public void onStatusUpdate(String name, int status, String desc) {
        // do your work
    }

    @Override
    protected void onResume() {
        requestLocation();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        requestLocation();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        locationManager.removeUpdates(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length >0){
                    for (int result :grantResults){
                        if (result !=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有权限才能使用本程序！",
                                    Toast.LENGTH_SHORT).show();

                            /**
                             * permission dialog would show if it is the first time.
                             */
                            SharedPreferences historyFile = getSharedPreferences("historyFile",MODE_PRIVATE);
                            boolean isFirst = historyFile.getBoolean("isFirstPermission",true);
                            if (isFirst){
                                PermissionUtils.permissionWarning(this,getResources().getString(R.string.permission_message));
                                SharedPreferences.Editor editor = historyFile.edit();
                                editor.putBoolean("isFirstPermission",false);
                                editor.apply();
                            }

                            return;
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    /**
     * parse the data which come from the server
     */
    public void parseJSON(String jsonData){
        String num;
        int sos;
        double latitude,longitude;
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            Log.e(TAG, "parseJSON: "+jsonData);
            for (int i=0; i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                num=jsonObject.getString("number");
                sos=jsonObject.getInt("sos");
                latitude=jsonObject.getDouble("latitude");
                longitude=jsonObject.getDouble("longitude");
                str_sp1 = new SpannableString(num+"         \n");
                str_sp2 = new SpannableString("求救原因  "+sos_str[sos]+"                | 详情 >");

                str_sp1.setSpan(styleSpan_A, 0,17, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                str_sp2.setSpan(styleSpan_B, 0, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                list_help.add(TextUtils.concat(str_sp1,str_sp2));
                list_num[i]=num;
                list_sos[i]=sos;
                list_lat[i] = latitude;
                list_lng[i] =longitude;

                Log.e(TAG, "parseJSON: "+"-------------"+i+"-----------"+latitude+"-----------"+longitude);
                Log.e("parse","-------------"+i+"-----------over");
            }
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(MainActivityB.this, R.layout.array_adapter, list_help);

            ListView listView = (ListView) findViewById(R.id.list_help);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    Intent intent=new Intent(MainActivityB.this,ShowMapB.class);
                    intent.putExtra("target",list_num[arg2]);
                    intent.putExtra("sos",list_sos[arg2]);
                    intent.putExtra("latitudeSos",list_lat[arg2]);
                    intent.putExtra("longitudeSos",list_lng[arg2]);
                    startActivity(intent);
                }
            });

        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
