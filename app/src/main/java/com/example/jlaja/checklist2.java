package com.example.jlaja;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class checklist2 extends AppCompatActivity {
    Spinner spin;
    EditText value;
    Button add, remove;
    ArrayList datachecklist;

    private LinearLayout checkboxcontainer;
    private int checkboxcount = 0;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "checkboxprefs";
    private static final String KEY_CHECKBOXES = "checkboxlist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checklist2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //PAKE SPINNER
//        spin=findViewById(R.id.spinnerchecklist);
//
        value = findViewById(R.id.valuesss);
//        add = findViewById(R.id.addchecklist);
//        remove = findViewById(R.id.deletechecklist);
//        datachecklist = new ArrayList();
//        ArrayAdapter adapter= new ArrayAdapter(checklist2.this, android.R.layout.simple_spinner_item, datachecklist);
//        spin.setAdapter(adapter);
//
//        add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                datachecklist.add(value.getText().toString());
//                Toast.makeText(checklist2.this, "Added !!", Toast.LENGTH_SHORT).show();
//            }
//        });
//        remove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                datachecklist.remove(value.getText().toString());
//                Toast.makeText(checklist2.this, "Added !!", Toast.LENGTH_SHORT).show();
//            }
//        });

        //PAKE SCROLL VIEW
        Button addchecklist = findViewById(R.id.addchecklist);
        Button deletechecklist = findViewById(R.id.deletechecklist);
        checkboxcontainer = findViewById(R.id.checkboxcontainer);
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        //load previous Checkbox
        try {
            loadCheckBox();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        addchecklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addcheckbox();
            }
        });
//        deletechecklist.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deletecheckbox(value);
//            }
//        });

    }

    private void addcheckbox() {
        CheckBox checkbox = new CheckBox(this);

//        if (text != null) {
//            checkbox.setText(text);
//            checkboxcontainer.addView(checkbox);
//        }

        if (TextUtils.isEmpty(value.getText())) {
            Toast.makeText(this, "Checkbox can't be BLANK!", Toast.LENGTH_SHORT).show();
            return;
        }


        checkbox.setText(value.getText().toString());
        checkbox.setId(View.generateViewId());
        checkboxcontainer.addView(checkbox);
        value.setText("");



        savecheckbox();

    }

    private void addcheckbox2(String intext) {
        CheckBox checkbox = new CheckBox(this);

//        if (text != null) {
//            checkbox.setText(text);
//            checkboxcontainer.addView(checkbox);
//        }

//        if (TextUtils.isEmpty(intext.getText())) {
//            Toast.makeText(this, "Checkbox can't be BLANK!", Toast.LENGTH_SHORT).show();
//            return;
//        }


        checkbox.setText(intext);
        checkbox.setId(View.generateViewId());
        checkboxcontainer.addView(checkbox);
//        value.setText("");


        savecheckbox();

    }

//    private void deletecheckbox(View checkbox) {
//        checkboxcontainer.removeView(checkbox);
//
//        Toast.makeText(this, "DONE!", Toast.LENGTH_SHORT).show();
//    }

    private void savecheckbox() {
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < checkboxcontainer.getChildCount(); i++) {
            View child = checkboxcontainer.getChildAt(i);
            if (child instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) child;
                // Save each CheckBox's text and checked state
                JSONArray checkBoxData = new JSONArray();
                checkBoxData.put(checkBox.getText().toString());
                checkBoxData.put(checkBox.isChecked());
                jsonArray.put(checkBoxData);

            }
        }

        sharedPreferences.edit().putString(KEY_CHECKBOXES, jsonArray.toString()).apply();
    }

    private void loadCheckBox() throws JSONException {
        String savedData = sharedPreferences.getString(KEY_CHECKBOXES, null);

//        if (savedData != null) {
//            try {
//                JSONArray jsonArray = new JSONArray(savedData);
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONArray checkBoxData = jsonArray.getJSONArray(i);
//                    String text = checkBoxData.getString(0);
//                    boolean isChecked = checkBoxData.getBoolean(1);
//
//                    addcheckbox();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

        if (savedData == null) {
            Log.d("SavedData", "No data saved in SharedPreferences");
            return; // Exit if there's no data
        } else {
            Log.d("SavedData", "Retrieved JSON: " + savedData);
        }

        JSONArray jsonArray = new JSONArray(savedData);

        for (int i = 0; i < jsonArray.length(); i++) {
            // Extract each CheckBox data
            JSONArray checkBoxData = jsonArray.optJSONArray(i); // Safe access to array
            if (checkBoxData != null && checkBoxData.length() == 2) {
                String text = checkBoxData.optString(0, ""); // Default empty text if missing
                boolean isChecked = checkBoxData.optBoolean(1, false); // Default unchecked

                // Add the CheckBox to the layout
                addcheckbox2(text);
            } else {
                Log.e("LoadCheckBoxes", "Invalid CheckBox data at index " + i);
            }
        }
    }

    public void BackButton3(View view) {
        onBackPressed();
    }

}