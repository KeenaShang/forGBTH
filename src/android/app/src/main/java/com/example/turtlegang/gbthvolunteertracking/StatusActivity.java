package com.example.turtlegang.gbthvolunteertracking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;

public class StatusActivity extends AppCompatActivity{
    private RadioGroup radioStatusGroup;
    final String KEY_SAVED_RADIO_BUTTON_INDEX = "SAVED_RADIO_BUTTON_INDEX";
    private RadioButton radioStatusButton;
    private Button setStatus;
    private int savedRadioIndex;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.status);
      
        addListenerOnButton();

        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        savedRadioIndex = sharedPreferences.getInt(KEY_SAVED_RADIO_BUTTON_INDEX, 0);
        checkButton(savedRadioIndex);
    }
    
    public void checkButton(int savedRadioIndex) {
        if (savedRadioIndex == 1) {radioStatusGroup.check(R.id.availableRadioButton);}
        else if (savedRadioIndex == 2) {radioStatusGroup.check(R.id.respondingRadioButton);}
        else if (savedRadioIndex == 3){radioStatusGroup.check(R.id.onSceneRadioButton);}
        else if (savedRadioIndex == 4){radioStatusGroup.check(R.id.blockedRadioButton);}
        else if (savedRadioIndex == 5){radioStatusGroup.check(R.id.unavailableRadioButton);}
        else {radioStatusGroup.check(R.id.doNotDisturbRadioButton);}
    }

    public void addListenerOnButton() {
        radioStatusGroup = findViewById(R.id.statusRadioGroup);
        setStatus = findViewById(R.id.setStatusButton);
        setStatus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioStatusGroup.getCheckedRadioButtonId();
                radioStatusButton = findViewById(selectedId);
                String toastText = "Updated status to " + radioStatusButton.getText();
                Toast.makeText(StatusActivity.this,
                        toastText, Toast.LENGTH_SHORT).show();
                int selectedIndex = radioStatusGroup.indexOfChild(radioStatusButton);
                savePreferences(KEY_SAVED_RADIO_BUTTON_INDEX, selectedIndex);
                setStatus(radioStatusButton);
            }
        });
    }
  
    public void setStatus(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        int statusTagNum = Integer.parseInt(view.getTag().toString());
        intent.putExtra("status", statusTagNum);
        startActivity(intent);
    }

    public void savePreferences(String key, int value) {
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

}
