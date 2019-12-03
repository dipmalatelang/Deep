package com.travel.cotravel.fragment.account.profile.verify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.ListPopupWindow;

import java.lang.reflect.Field;
import java.util.Objects;


import com.travel.cotravel.BaseActivity;
import com.travel.cotravel.R;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditPhoneActivity extends BaseActivity {


    @BindView(R.id.ed_number)
    EditText edNumber;
    @BindView(R.id.btn_sub)
    Button btnSub;
    @BindView(R.id.spinner)
    Spinner spinner;
    private String  code;

    String mobile,mobileCode;
    ArrayAdapter<State> StateAdapter;
    State[] StateSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone);
        ButterKnife.bind(this);

        StateSpinner = new State[]{
                new State("+0", "None"),
                new State("+93", "Afghanistan"),
                new State("+355", "Albania"),
                new State("+213", "Algeria"),
                new State("+376", "Andorra"),
                new State("+244", "Angola"),
                new State("+1268", "Antigua and Barbuda"),
                new State("+54", "Argentina"),
                new State("+374", "Armenia"),
                new State("+297", "Aruba"),
                new State("+61", "Australia"),
                new State("+43", "Austria"),
                new State("+994", "Azerbaijan"),
                new State("+1242", "Bahamas"),
                new State("+973", "Bahrain"),
                new State("+880", "Bangladesh"),
                new State("+1246", "Barbados"),
                new State("+375", "Belarus"),
                new State("+32", "Belgium"),
                new State("+501", "Belize"),
                new State("+229", "Benin"),
                new State("+1441", "Bermuda"),
                new State("+975", "Bhutan"),
                new State("+591", "Bolivia"),
                new State("+387", "Bosnia and Herzegovina"),
                new State("+267", "Botswana"),
                new State("+55", "Brazil"),
                new State("+673", "Brunei"),
                new State("+359", "Bulgaria"),
                new State("+226", "Burkina Faso"),
                new State("+257", "Burundi"),
                new State("+855", "Cambodia"),
                new State("+237", "Cameroon"),
                new State("+1", "Canada"),
                new State("+238", "Cape Verde"),
                new State("+1345", "Cayman Islands"),
                new State("+236", "Central African Republic"),
                new State("+235", "Chad"),
                new State("+56", "Chile"),
                new State("+86", "China"),
                new State("+57", "Colombia"),
                new State("+269", "Comoros"),
                new State("+242", "Congo Brazzaville"),
                new State("+243", "Congo"),
                new State("+682", "Cook Islands"),
                new State("+506", "Costa Rica"),
                new State("+225", "Cote D Ivoire"),
                new State("+385", "Croatia"),
                new State("+53", "Cuba"),
                new State("+357", "Cyprus"),
                new State("+420", "Czech Republic"),
                new State("+45", "Denmark"),
                new State("+253", "Djibouti"),
                new State("+1767", "Dominica"),
                new State("+18", "Dominican Republic"),
                new State("+593", "Ecuador"),
                new State("+20", "Egypt"),
                new State("+503", "El Salvador"),
                new State("+240", "Equatorial Guinea"),
                new State("+291", "Eritrea"),
                new State("+372", "Estonia"),
                new State("+251", "Ethiopia"),
                new State("+298", "Faroe Islands"),
                new State("+679", "Fiji"),
                new State("+358", "Finland"),
                new State("+33", "France"),
                new State("+241", "Gabon"),
                new State("+220", "Gambia"),
                new State("+995", "Georgia"),
                new State("+49", "Germany"),
                new State("+233", "Ghana"),
                new State("+350", "Gibraltar"),
                new State("+30", "Greece"),
                new State("+299", "Greenland"),
                new State("+1473", "Grenada"),
                new State("+590", "Guadeloupe"),
                new State("+1671", "Guam"),
                new State("+502", "Guatemala"),
                new State("+224", "Guinea"),
                new State("+245", "Guinea Bissau"),
                new State("+592", "Guyana"),
                new State("+509", "Haiti"),
                new State("+504", "Honduras"),
                new State("+852", "Hong Kong"),
                new State("+36", "Hungary"),
                new State("+354", "Iceland"),
                new State("+91", "India"),
                new State("+62", "Indonesia"),
                new State("+98", "Iran"),
                new State("+964", "Iraq"),
                new State("+353", "Ireland"),
                new State("+972", "Israel"),
                new State("+39", "Italy"),
                new State("+1876", "Jamaica"),
                new State("+81", "Japan"),
                new State("+962", "Jordan"),
                new State("+7", "Kazakhstan"),
                new State("+254", "Kenya"),
                new State("+686", "Kiribati"),
                new State("+850", "Korea, North"),
                new State("+82", "Korea, South"),
                new State("+965", "Kuwait"),
                new State("+996", "Kyrgyzstan"),
                new State("+856", "Laos"),
                new State("+371", "Latvia"),
                new State("+961", "Lebanon"),
                new State("+266", "Lesotho"),
                new State("+231", "Liberia"),
                new State("+218", "Libya"),
                new State("+423", "Liechtenstein"),
                new State("+370", "Lithuania"),
                new State("+352", "Luxembourg"),
                new State("+853", "Macao"),
                new State("+389", "Macedonia"),
                new State("+261", "Madagascar"),
                new State("+265", "Malawi"),
                new State("+60", "Malaysia"),
                new State("+960", "Maldives"),
                new State("+223", "Mali"),
                new State("+356", "Malta"),
                new State("+692", "Marshall Islands"),
                new State("+596", "Martinique"),
                new State("+222", "Mauritania"),
                new State("+230", "Mauritius"),
                new State("+269", "Mayotte"),
                new State("+52", "Mexico"),
                new State("+691", "Micronesia, Federated States Of"),
                new State("+373", "Moldova"),
                new State("+377", "Monaco"),
                new State("+976", "Mongolia"),
                new State("+382", "Montenegro"),
                new State("+1664", "Montserrat"),
                new State("+212", "Morocco"),
                new State("+258", "Mozambique"),
                new State("+95", "Myanmar"),
                new State("+264", "Namibia"),
                new State("+674", "Nauru"),
                new State("+977", "Nepal"),
                new State("+31", "Netherlands"),
                new State("+599", "Netherlands Antilles"),
                new State("+687", "New Caledonia"),
                new State("+64", "New Zealand"),
                new State("+505", "Nicaragua"),
                new State("+227", "Niger"),
                new State("+234", "Nigeria"),
                new State("+683", "Niue"),
                new State("+1670", "Northern Mariana Islands"),
                new State("+47", "Norway"),
                new State("+968", "Oman"),
                new State("+92", "Pakistan"),
                new State("+680", "Palau"),
                new State("+507", "Panama"),
                new State("+675", "Papua New Guinea"),
                new State("+595", "Paraguay"),
                new State("+51", "Peru"),
                new State("+63", "Philippines"),
                new State("+48", "Poland"),
                new State("+351", "Portugal"),
                new State("+1787", "Puerto Rico"),
                new State("+974", "Qatar"),
                new State("+262", "Reunion"),
                new State("+40", "Romania"),
                new State("+7", "Russia"),
                new State("+250", "Rwanda"),
                new State("+290", "Saint Helena"),
                new State("+1869", "Saint Kitts and Nevis"),
                new State("+1758", "Saint Lucia"),
                new State("+508", "Saint Pierre and Miquelon"),
                new State("+1784", "Saint Vincent and the Grenadines"),
                new State("+685", "Samoa"),
                new State("+378", "San Marino"),
                new State("+239", "Sao Tome and Principe"),
                new State("+966", "Saudi Arabia"),
                new State("+221", "Senegal"),
                new State("+381", "Serbia"),
                new State("+248", "Seychelles"),
                new State("+232", "Sierra Leone"),
                new State("+65", "Singapore"),
                new State("+421", "Slovakia"),
                new State("+386", "Slovenia"),
                new State("+677", "Solomon Islands"),
                new State("+252", "Somalia"),
                new State("+27", "South Africa"),
                new State("+34", "Spain"),
                new State("+94", "Sri Lanka"),
                new State("+249", "Sudan"),
                new State("+597", "Suriname"),
                new State("+268", "Swaziland"),
                new State("+46", "Sweden"),
                new State("+41", "Switzerland"),
                new State("+963", "Syria"),
                new State("+886", "Taiwan"),
                new State("+992", "Tajikistan"),
                new State("+255", "Tanzania"),
                new State("+66", "Thailand"),
                new State("+670", "Timor-Leste"),
                new State("+228", "Togo"),
                new State("+690", "Tokelau"),
                new State("+676", "Tonga"),
                new State("+1868", "Trinidad and Tobago"),
                new State("+216", "Tunisia"),
                new State("+90", "Turkey"),
                new State("+993", "Turkmenistan"),
                new State("+1649", "Turks and Caicos Islands"),
                new State("+688", "Tuvalu"),
                new State("+256", "Uganda"),
                new State("+380", "Ukraine"),
                new State("+971", "United Arab Emirates"),
                new State("+44", "United Kingdom"),
                new State("+1", "United States"),
                new State("+598", "Uruguay"),
                new State("+998", "Uzbekistan"),
                new State("+678", "Vanuatu"),
                new State("+3", "Vatican City State (Holy See)"),
                new State("+58", "Venezuela"),
                new State("+84", "Vietnam"),
                new State("+1284", "Virgin Islands, British"),
                new State("+1340", "Virgin Islands, US"),
                new State("+681", "Wallis and Futuna"),
                new State("+967", "Yemen"),
                new State("+260", "Zambia"),
                new State("+263", "Zimbabwe")
        };

        setPopup1();

        StateAdapter = new ArrayAdapter<>(this, R.layout.spinner_text, StateSpinner);
        StateAdapter.setDropDownViewResource(R.layout.spinner_text);
        spinner.setAdapter(StateAdapter);


//        spinner.setSelection(StateAdapter.getPosition(new State("+91","India")));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                State user = StateAdapter.getItem(position);
                // Here you can do the action you want to...
                code=user.getCode();
                ((TextView) view).setText(user.getCode());
                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
//                Toast.makeText(EditPhoneActivity.this, ""+user.getCode(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);

        if (sharedPreferences.contains("Phone")) {
            mobile = (sharedPreferences.getString("Phone", ""));
            mobileCode=sharedPreferences.getString("mobileCode","");
            if (!mobile.equalsIgnoreCase("")) {
                edNumber.setEnabled(false);
                spinner.setEnabled(false);
                btnSub.setClickable(false);
                edNumber.setText(mobile);
                spinner.setSelection(getPos(mobileCode));

            } else {
                edNumber.setEnabled(true);
                spinner.setEnabled(true);
                btnSub.setClickable(true);
            }

        }


    }

    int pos=0;
    private int getPos(String s) {
        for(int i=0;i<StateSpinner.length;i++)
        {
            if(StateSpinner[i].getCode().equalsIgnoreCase(s))
            {
                pos=i;
            }
        }
        return pos;
    }

    private void setPopup1() {
        Field popup1;
        try {
            popup1 = Spinner.class.getDeclaredField("mPopup");
            popup1.setAccessible(true);

            ListPopupWindow StatepopupWindow = (ListPopupWindow) popup1.get(spinner);

            Objects.requireNonNull(StatepopupWindow).setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {

        }
    }

    @OnClick(R.id.btn_sub)
    public void onViewClicked() {
        String mobile = edNumber.getText().toString().trim();

        if (mobile.isEmpty() || mobile.length() < 10) {
            edNumber.setError("Enter a valid mobile");
            edNumber.requestFocus();
            return;
        }

        Intent intent = new Intent(EditPhoneActivity.this, VerifyPhoneActivity.class);
        intent.putExtra("mobileCode",code);
        intent.putExtra("mobile", mobile);
        startActivity(intent);
        finish();
    }

    private class State {
        private String code;
        private String statename;

        public State(String code, String statename) {
            this.code = code;
            this.statename = statename;
        }

        public String getCode() {
            return code;
        }

        public String getStatename() {
            return statename;
        }

        @Override
        public String toString() {
            return this.statename;

        }
    }
}