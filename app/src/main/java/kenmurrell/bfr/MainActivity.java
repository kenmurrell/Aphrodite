package kenmurrell.bfr;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String BF_NUM = "6138858659";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //----PERSISTENCE----
        final List<String> attentionMsgList = Arrays.asList(getResources().getStringArray(R.array.attentionMsgList));
        final List<String> foodMsgList = Arrays.asList(getResources().getStringArray(R.array.foodMsgList));
        SharedPreferences settings = getSharedPreferences("PHONE_NUMBER", 0);
        if(!settings.contains("NUM")){
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("NUM");
            editor.putString("NUM", BF_NUM);
            editor.apply();
        }


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
        FloatingActionButton foodButton = (FloatingActionButton) findViewById(R.id.foodButton) ;
        attentionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idx = new Random().nextInt(attentionMsgList.size());
                sendMessage(attentionMsgList.get(idx));
                Snackbar.make(view, "Sent!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        foodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idx = new Random().nextInt(foodMsgList.size());
                sendMessage(foodMsgList.get(idx));
                Snackbar.make(view, "Sent!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
// ---OPTIONS MENU--- probably getting rid of this...this too ^^^
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_number) {
            onNavNumber();
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void sendMessage(String msg){
        SharedPreferences settings = getSharedPreferences("PHONE_NUMBER", 0);
        String number = settings.getString("NUM", "0");
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(number, null, msg, null, null );
    }


    public boolean onNavNumber(){
        final SharedPreferences settings = getSharedPreferences("PHONE_NUMBER", 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please add a phone number");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        builder.setView(input);
        builder.setMessage("Current: " + settings.getString("NUM", "None"));

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = settings.edit();
                editor.remove("NUM");
                editor.putString("NUM", input.getText().toString());
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

}
