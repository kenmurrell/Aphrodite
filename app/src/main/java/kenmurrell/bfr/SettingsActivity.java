package kenmurrell.bfr;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.CompoundButton;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //------------------------
        //Error is here! onCheckedChange is not being called
        //Look at this: https://stackoverflow.com/questions/36868099/make-switch-button-which-is-in-an-alertdialog-do-something-when-its-state-cha/36868526#36868526
        //------------------------

        SwitchCompat randomSwitch = findViewById(R.id.switchRandom);
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
        SwitchCompat dataSwitch = findViewById(R.id.switchData);
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
    }
}
