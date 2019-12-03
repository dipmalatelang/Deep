package com.travel.cotravel.fragment.trip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.travel.cotravel.BaseActivity;
import com.travel.cotravel.MainActivity;
import com.travel.cotravel.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterTripActivity extends BaseActivity implements View.OnClickListener {

    ArrayList<String> array_lang, array_look, array_sort, array_from, array_to, array_eyes, array_hairs, array_height, array_bodytype;
    ArrayAdapter<String> adapter_lang, adapter_look, adapter_sort, adapter_from, adapter_to, adapter_eyes, adapter_hairs, adapter_height, adapter_bodytype;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_city)
    EditText etCity;
    @BindView(R.id.spinner_lang)
    Spinner spinnerLang;
    @BindView(R.id.spinner_eyes)
    Spinner spinnerEyes;
    @BindView(R.id.spinner_hairs)
    Spinner spinnerHairs;
    @BindView(R.id.spinner_height)
    Spinner spinnerHeight;
    @BindView(R.id.spinner_bodytype)
    Spinner spinnerBodytype;
    @BindView(R.id.spinner_look)
    Spinner spinnerLook;
    @BindView(R.id.spinner_sort)
    Spinner spinnerSort;
    @BindView(R.id.spinner_from)
    Spinner spinnerFrom;
    @BindView(R.id.spinner_to)
    Spinner spinnerTo;

    @BindView(R.id.btn_add_trip)
    Button btnAddTrip;
    @BindView(R.id.linearLayoutradiobutton)
    LinearLayout linearLayoutradiobutton;
    @BindView(R.id.checkbox)
    AppCompatCheckBox checkbox;
    @BindView(R.id.linearLayoutfilter)
    LinearLayout linearLayoutfilter;
    @BindView(R.id.activity_filter_trip_coodinatelayout)
    CoordinatorLayout activityFilterTripCoodinatelayout;
    @BindView(R.id.rg_trip)
    RadioGroup rgTrip;
    RadioButton rb_trip;
    @BindView(R.id.rb_from)
    RadioButton rbFrom;
    @BindView(R.id.rb_visit)
    RadioButton rbVisit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_trip);
        ButterKnife.bind(this);

        initComponent();

        prefs = getSharedPreferences("Filter_TripList", MODE_PRIVATE);

        if (prefs.contains("FilterFlag")) {
            int flag = prefs.getInt("FilterFlag", 0);
            if (flag > 0) {
                if (prefs.contains("str_name")) {
                    String str_name = prefs.getString("str_name", "");
                    etName.setText(str_name);
                }

                if (prefs.contains("str_city")) {
                    String str_city = prefs.getString("str_city", "");
                    etCity.setText(str_city);
                }

                if (prefs.contains("str_trip")) {
                    String str_trip = prefs.getString("str_trip", "");
                    if (str_trip.equalsIgnoreCase("from")) {
                        rbFrom.setChecked(true);
                    } else {
                        rbVisit.setChecked(true);
                    }
                }

                if (prefs.contains("str_eyes")) {
                    String str_eyes = prefs.getString("str_eyes", "");
                    spinnerLang.setSelection(adapter_eyes.getPosition(str_eyes));
                }

                if (prefs.contains("str_hairs")) {
                    String str_hairs = prefs.getString("str_hairs", "");
                    spinnerHairs.setSelection(adapter_hairs.getPosition(str_hairs));
                }

                if (prefs.contains("str_lang")) {
                    String str_lang = prefs.getString("str_lang", "");
                    spinnerLang.setSelection(adapter_lang.getPosition(str_lang));
                }

                if (prefs.contains("str_looking_for")) {
                    String str_looking_for = prefs.getString("str_looking_for", "");
                    spinnerLook.setSelection(adapter_look.getPosition(str_looking_for));
                }

                if (prefs.contains("str_bodytype")) {
                    String str_bodytype = prefs.getString("str_bodytype", "");
                    spinnerBodytype.setSelection(adapter_bodytype.getPosition(str_bodytype));
                }

                if (prefs.contains("str_height")) {
                    String str_height = prefs.getString("str_height", "");
                    spinnerHeight.setSelection(adapter_height.getPosition(str_height));
                }

                if (prefs.contains("str_from")) {
                    int str_from = prefs.getInt("str_from", 18);
                    spinnerFrom.setSelection(adapter_from.getPosition(String.valueOf(str_from)));
                }

                if (prefs.contains("str_to")) {
                    int str_to = prefs.getInt("str_to", 99);
                    spinnerTo.setSelection(adapter_to.getPosition(String.valueOf(str_to)));
                }
            }
        }

        assert getSupportActionBar() != null;

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void initComponent() {

        array_lang = new ArrayList<>(
                Arrays.asList("All", "Arabic", "Danish", "German", "Belorussian", "Dutch", "Greek", "Japanese", "Portuguese", "Italian", "Polish", "Spanish", "Swedish", "Bulgarian", "English", "Hebrew", "Korean", "Romanian", "Thai", "Catalan",
                        "Estonian", "Hindi", "Latvian", "Russian", "Turkish", "Chinese", "Filipino", "Hungarian", "Lithuanian", "Serbian", "Ukrainian", "Croatian", "Finnish", "Icelandic", "Norwegian", "Slovak", "Urdu", "Czech", "French",
                        "Indonesian", "Persian", "Slovenian", "Vietnamese", "Nepali", "Armenian", "Kurdish"));

        array_look = new ArrayList<>(Arrays.asList("All", "Friends", "Adventure", "Soulmate", "Job"));
        array_sort = new ArrayList<>(Arrays.asList("Default", "Last registered ", "Last online"));

        array_from = new ArrayList<>(Arrays.asList("18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
                "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
                "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70",
                "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90",
                "91", "92", "93", "94", "95", "96", "97", "98", "99"));


        array_to = new ArrayList<>(Arrays.asList("18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
                "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
                "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70",
                "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90",
                "91", "92", "93", "94", "95", "96", "97", "98", "99"));

        array_eyes = new ArrayList<>(Arrays.asList("All", "Brown", "Blue", "Green", "Hazel", "Gray", "Amber", "Other"));

        array_hairs = new ArrayList<>(Arrays.asList("All", "Blond", "Brown", "Black", "Red", "Auburn", "Grey", "Other"));

        array_height = new ArrayList<>(Arrays.asList("All", "150 cm (4'11\")", "151 cm (4'11\")", "152 cm (5'00\")",
                "153 cm (5'00\")", "154 cm (5'00\")", "155 cm (5'01\")", "156 cm (5'01\")", "157 cm (5'02\")",
                "158 cm (5'02\")", "159 cm (5'02\")", "160 cm (5'03\")", "161 cm (5'03\")", "162 cm (5'03\")",
                "163 cm (5'04\")", "164 cm (5'04\")", "165 cm (5'05\")", "166 cm (5'05\")", "167 cm (5'05\")",
                "168 cm (5'06\")", "169 cm (5'06\")", "170 cm (5'07\")", "171 cm (5'07\")", "172 cm (5'07\")",
                "173 cm (5'08\")", "174 cm (5'08\")", "175 cm (5'09\")", "176 cm (5'09\")", "177 cm (5'09\")",
                "178 cm (5'10\")", "179 cm (5'10\")", "180 cm (5'11\")", "181 cm (5'11\")", "182 cm (5'11\")",
                "183 cm (6'00\")", "184 cm (6'00\")", "185 cm (6'01\")", "186 cm (6'01\")", "187 cm (6'01\")",
                "188 cm (6'02\")", "189 cm (6'02\")", "190 cm (6'02\")", "191 cm (6'03\")", "192 cm (6'03\")",
                "193 cm (6'04\")", "194 cm (6'04\")", "195 cm (6'04\")", "196 cm (6'05\")", "197 cm (6'05\")",
                "198 cm (6'06\")", "199 cm (6'06\")", "200 cm (6'06\")", "201 cm (6'07\")", "202 cm (6'07\")",
                "203 cm (6'08\")", "204 cm (6'08\")", "205 cm (6'08\")", "206 cm (6'09\")", "207 cm (6'09\")",
                "208 cm (6'10\")", "209 cm (6'10\")", "210 cm (6'10\")", "211 cm (6'11\")", "212 cm (6'11\")",
                "213 cm (7'00\")", "214 cm (7'00\")", "215 cm (7'00\")", "216 cm (7'01\")", "217 cm (7'01\")",
                "218 cm (7'02\")", "219 cm (7'02\")", "220 cm (7'02\")"));

        array_bodytype = new ArrayList<>(Arrays.asList("All", "Slim", "Athletic", "Average", "Curvy", "Heavy"));

    /*    tvGirl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutfilter.setVisibility(VISIBLE);
                linearLayoutradiobutton.setVisibility(VISIBLE);
                checkbox.setVisibility(VISIBLE);

            }
        });
        tvMen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutradiobutton.setVisibility(GONE);
                linearLayoutfilter.setVisibility(VISIBLE);
                checkbox.setVisibility(GONE);

            }
        });*/

        adapter_sort = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array_sort);
        adapter_sort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapter_sort);


        adapter_lang = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array_lang);
        adapter_lang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLang.setAdapter(adapter_lang);

        adapter_look = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array_look);
        adapter_look.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLook.setAdapter(adapter_look);

        adapter_eyes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array_eyes);
        adapter_eyes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEyes.setAdapter(adapter_eyes);

        adapter_hairs = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array_hairs);
        adapter_hairs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHairs.setAdapter(adapter_hairs);

        adapter_height = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array_height);
        adapter_height.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHeight.setAdapter(adapter_height);

        adapter_bodytype = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array_bodytype);
        adapter_bodytype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBodytype.setAdapter(adapter_bodytype);

        adapter_from = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array_from);
        adapter_from.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter_from);

        adapter_to = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array_to);
        adapter_to.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTo.setAdapter(adapter_to);

        spinnerTo.setSelection(adapter_to.getPosition("99"));

        btnAddTrip.setOnClickListener(this);

        setPopup();
    }

    private void setPopup() {
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);


            ListPopupWindow TopopupWindow = (ListPopupWindow) popup.get(spinnerTo);
            ListPopupWindow FrompopupWindow = (ListPopupWindow) popup.get(spinnerFrom);
            ListPopupWindow LanguagepopupWindow = (ListPopupWindow) popup.get(spinnerLang);
            ListPopupWindow HeightpopupWindow = (ListPopupWindow) popup.get(spinnerHeight);
            ListPopupWindow BodypopupWindow = (ListPopupWindow) popup.get(spinnerBodytype);
            ListPopupWindow HairpopupWindow = (ListPopupWindow) popup.get(spinnerHairs);
            ListPopupWindow EyespopupWindow = (ListPopupWindow) popup.get(spinnerEyes);


            Objects.requireNonNull(TopopupWindow).setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            Objects.requireNonNull(FrompopupWindow).setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            Objects.requireNonNull(LanguagepopupWindow).setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            Objects.requireNonNull(HeightpopupWindow).setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            Objects.requireNonNull(BodypopupWindow).setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            Objects.requireNonNull(HairpopupWindow).setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            Objects.requireNonNull(EyespopupWindow).setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add_trip) {
            editor = prefs.edit();
            editor.putInt("FilterFlag", 1);
            String str_name = etName.getText().toString();
            String str_city = etCity.getText().toString();
            String str_eyes = spinnerEyes.getSelectedItem().toString();
            String str_hairs = spinnerHairs.getSelectedItem().toString();
            String str_height = spinnerHeight.getSelectedItem().toString();
            String str_bodytype = spinnerBodytype.getSelectedItem().toString();
            int str_from = Integer.parseInt(spinnerFrom.getSelectedItem().toString());
            int str_to = Integer.parseInt(spinnerTo.getSelectedItem().toString());
            String str_lang = spinnerLang.getSelectedItem().toString();
            String str_looking_for = spinnerLook.getSelectedItem().toString();

            int selectedGender = rgTrip.getCheckedRadioButtonId();
            if (selectedGender < 0)
                snackBar(activityFilterTripCoodinatelayout,"Select Gender");

            else
                rb_trip = findViewById(selectedGender);

            String str_trip = rb_trip.getText().toString();



            editor.putString("str_trip", str_trip);
            editor.putInt("str_from", str_from);
            editor.putInt("str_to", str_to);


            if (!TextUtils.isEmpty(str_name)) {
                editor.putString("str_name", str_name);
            }

            if (!TextUtils.isEmpty(str_city)) {
                editor.putString("str_city", str_city);
            }

            if (!TextUtils.isEmpty(str_eyes)) {
                editor.putString("str_eyes", str_eyes);
            }

            if (!TextUtils.isEmpty(str_hairs)) {
                editor.putString("str_hairs", str_hairs);
            }

            if (!TextUtils.isEmpty(str_height)) {
                editor.putString("str_height", str_height);
            }

            if (!TextUtils.isEmpty(str_bodytype)) {
                editor.putString("str_bodytype", str_bodytype);
            }

            if (!TextUtils.isEmpty(str_lang)) {
                editor.putString("str_lang", str_lang);
            }

            if (!TextUtils.isEmpty(str_looking_for)) {
                editor.putString("str_looking_for", str_looking_for);
            }


            startActivity(new Intent(FilterTripActivity.this, MainActivity.class));
            editor.apply();

        }
    }
}
