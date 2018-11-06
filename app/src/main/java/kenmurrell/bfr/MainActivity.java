package kenmurrell.bfr;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String BF_NUM = "6138858659";

    public static Boolean sendMessage(String msg, String number){
        if(PhoneNumberUtils.isGlobalPhoneNumber(number)){
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(number, null, msg, null, null );
            return true;
        }else{
            return false;
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //----PERSISTENCE----
        final List<String> attentionMsgList = Arrays.asList(getResources().getStringArray(R.array.attentionMsgList));
        final List<String> foodMsgList = Arrays.asList(getResources().getStringArray(R.array.foodMsgList));

        SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
        SharedPreferences.Editor editor = settings.edit();
        if(!settings.contains("PHONE_NUMBER")){
            editor.putString("PHONE_NUMBER", "0");
            Log.d("---EVENT---","Created new phone_number field");
        }
        if(!settings.contains("RANDOM_MSG")){
            editor.putBoolean("RANDOM_MSG", false);
            Log.d("---EVENT---","Created new random_msg field");
        }
        if(!settings.contains("SEND_DATA")){
            editor.putBoolean("SEND_DATA", true);
            Log.d("---EVENT---","Created new send_data field");
        }
        editor.apply();


        //----PERMISSIONS-----
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.SEND_SMS)) {
                //pass
            } else {
                // Request the permission
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        } else {
            // Permission has already been granted
        }


        //-----BUTTONS-----
        FloatingActionButton attentionButton = (FloatingActionButton) findViewById(R.id.attentionButton);
        attentionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idx = new Random().nextInt(attentionMsgList.size());
                //sendMessage(attentionMsgList.get(idx));
                String number = getSharedPreferences("SETTINGS", 0).getString("PHONE_NUMBER", "0");
                String confirmSent = sendMessage("Give attention pls", number) ? "Sent" : "Not Sent";
                Snackbar.make(view, confirmSent, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        FloatingActionButton foodButton = (FloatingActionButton) findViewById(R.id.foodButton) ;
        foodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idx = new Random().nextInt(foodMsgList.size());
                //sendMessage(foodMsgList.get(idx));
                String number = getSharedPreferences("SETTINGS", 0).getString("PHONE_NUMBER", "0");
                String confirmSent = sendMessage("FEED ME", number) ? "Sent" : "Not Sent";
                Snackbar.make(view, confirmSent, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //------------------------
        //Error is here! onCheckedChange is not being called
        //Look at this: https://stackoverflow.com/questions/36868099/make-switch-button-which-is-in-an-alertdialog-do-something-when-its-state-cha/36868526#36868526
        //------------------------
        LayoutInflater factory = getLayoutInflater();
        View optionsView = factory.inflate(R.layout.options_menu, null);
        SwitchCompat randomSwitch = optionsView.findViewById(R.id.switchRandom);
        randomSwitch.setChecked(getSharedPreferences("SETTINGS", 0).getBoolean("RANDOM_MSG", false));
        randomSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("SETTINGS",0).edit();
                editor.putBoolean("RANDOM_MSG", isChecked);
                Log.i("--EVENT--","Changed RANDOM_MSG");
                editor.apply();
            }
        });
        SwitchCompat dataSwitch = optionsView.findViewById(R.id.switchData);
        dataSwitch.setChecked(getSharedPreferences("SETTINGS", 0).getBoolean("SEND_DATA", true));
        dataSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("SETTINGS",0).edit();
                editor.putBoolean("USER_DATA", isChecked);
                Log.i("--EVENT--","Changed USER_DATA");
                editor.apply();
            }
        });


        //-----NAVIGATION-----
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.nav_number:
                onNavNumber();
                break;
            case R.id.nav_settings:
                onNavSettings();
                break;
            case R.id.nav_about:
                onNavAbout();
                break;
            case R.id.nav_share:
                onNavShare();
                break;
            default:
                assert true;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean onNavNumber(){
        final SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please add a phone number");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setText(settings.getString("PHONE_NUMBER", "None"));
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("PHONE_NUMBER", input.getText().toString());
                editor.apply();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
        return true;
    }

    public boolean onNavSettings(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCustomTitle(getLayoutInflater().inflate(R.layout.options_menu,null));
        builder.setNeutralButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
        return true;
    }

    public boolean onNavAbout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About the Developer");
        builder.setMessage("add some stuff here about me later idk");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
        return true;
    }

    public boolean onNavShare(){
        return true;
    }

}
