package egypt.sedrak.insta1;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;


public class RequestFormActivity extends AppCompatActivity {
    // CalendarView calendar;
    private EditText mNameField;
    private EditText mTelephoneField;
    private EditText mPlaceField;

    private static TextView mDateTextField;
    private static TextView mTimeTextField;

    private Button mSubmitBtn;
    private Button mDateBtn;
    private Button mTimeBtn;

    private ProgressDialog mProgress;

    private Intent mIntent;
    private DatabaseReference mDatabaseRequests;
    private DatabaseReference mDatabaseCategories;

    private DatabaseReference mDatabasePackages;
    private com.google.firebase.database.Query mPackagesQuery;

    private DatabaseReference mDatabaseWeddingCategorie;
    private DatabaseReference mDatabaseBirthdayCategorie;
    private DatabaseReference mDatabaseGraduationCategorie;
    private DatabaseReference mDatabasePhotoBoothCategorie;

    private FirebaseAuth mAuth;

    private Spinner mCategorieSpinner;
    private List<Package> mPackageList;
    private List<String> mPackagesKeys;
    private Integer key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request_form);
        setTitle("Event Order");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_18dp);
        setSupportActionBar(toolbar);

        mProgress = new ProgressDialog(this);

        mDateTextField = (TextView) findViewById(R.id.eventDateField);
        mTimeTextField = (TextView) findViewById(R.id.eventTimeField);

        mSubmitBtn = (Button) findViewById(R.id.eventSubmit);
        mDateBtn = (Button) findViewById(R.id.eventDateBtn);
        mTimeBtn = (Button) findViewById(R.id.eventTimeBtn);

        mNameField = (EditText) findViewById(R.id.eventCustomerName);
        mTelephoneField = (EditText) findViewById(R.id.eventCustomerPhone);
        mPlaceField = (EditText) findViewById(R.id.eventPlace);

        mDatabaseCategories = FirebaseDatabase.getInstance().getReference().child("Categories");

        mDatabaseWeddingCategorie = mDatabaseCategories.child("Wedding");
        mDatabaseBirthdayCategorie = mDatabaseCategories.child("Birthday");
        mDatabaseGraduationCategorie = mDatabaseCategories.child("Graduation");
        mDatabasePhotoBoothCategorie = mDatabaseCategories.child("PhotoBooth");


        mDatabaseRequests = FirebaseDatabase.getInstance().getReference().child("Requests");
        mAuth = FirebaseAuth.getInstance();


        mIntent = getIntent();
        final String userUID = mIntent.getStringExtra("userUID");

        mDatabasePackages = FirebaseDatabase.getInstance().getReference().child("Packages");
        mPackagesQuery = mDatabasePackages.orderByChild("owner").equalTo(userUID);
        mPackageList = new ArrayList<>();
        mPackagesKeys = new ArrayList<>();

        mCategorieSpinner = (Spinner) findViewById(R.id.request_form_spinner);
        MyApplication.PackageListener(mPackagesQuery,mPackageList, mPackagesKeys, mCategorieSpinner, getBaseContext());

        mCategorieSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                key =  parent.getSelectedItemPosition();
                Toast.makeText(RequestFormActivity.this,mPackagesKeys.get(key), Toast.LENGTH_SHORT).show();
                Toast.makeText(RequestFormActivity.this,String.valueOf(key), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);}
        });

        mTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);}
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error = false;

                if(mNameField.getText().toString().length() == 0){
                    mNameField.setError("Please enter a name");
                    error = true;}

                if(mTelephoneField.getText().toString().length() == 0){
                    mTelephoneField.setError("Enter mobile number");
                    error = true;}

                if(mPlaceField.getText().toString().length() == 0){
                    mPlaceField.setError("Enter event address");
                    error = true;}

                if(mDateTextField.getText().toString().length() == 0 || mTimeTextField.getText().toString().length() == 0){
                    Toast.makeText(RequestFormActivity.this, "Enter date and time",Toast.LENGTH_LONG).show();
                    error = true;}

                if(key == null){
                    Toast.makeText(RequestFormActivity.this, "Package Key Error",Toast.LENGTH_LONG).show();
                    error = true;}

                if(error == false){
                    submitRequest();}

            }
        });



    }


    private void submitRequest() {

        final String name = mNameField.getText().toString().trim();
        final String mobileNumber = mTelephoneField.getText().toString().trim();
        final String address = mPlaceField.getText().toString().trim();
        final String date = mDateTextField.getText().toString().trim();
        final String time =  mTimeTextField.getText().toString().trim();
        final String event_date =  date + " " + time;
        final String expire_date = creatingExpireDate();


        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(mobileNumber) && !TextUtils.isEmpty(address) &&
                !TextUtils.isEmpty(date) && !TextUtils.isEmpty(time) && !TextUtils.isEmpty(expire_date)){

            mProgress.setMessage("Pushing request...");
            mProgress.show();
            mProgress.setCancelable(false);
            mProgress.setCanceledOnTouchOutside(false);


            DatabaseReference request = mDatabaseRequests.push();
            request.child("from").setValue(mAuth.getCurrentUser().getUid());
            request.child("to").setValue(mIntent.getStringExtra("userUID"));
            request.child("expire_date").setValue(expire_date);
            request.child("name").setValue(name);
            request.child("telephoneNumber").setValue(mobileNumber);
            request.child("address").setValue(address);
            request.child("event_date").setValue(event_date);
            request.child("status").setValue("Waiting");
            request.child("packageId").setValue(mPackagesKeys.get(key));

            Intent mainIntent = new Intent(RequestFormActivity.this, VerifiedRequest.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            mProgress.dismiss();

        }else {
            Toast.makeText(this, "Empty Fields", Toast.LENGTH_LONG).show();
            mProgress.dismiss();}


    }

    public String creatingExpireDate(){

        // Increase submit time by 2 days
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, +2);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY)-5;
        int minute = calendar.get(Calendar.MINUTE);

        return day + "/" + month + "/" + year + " " + hour + ":" + minute;

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);

        }


        // When dialog box is closed , this method is called
        public void onDateSet(DatePicker view, int year, int month, int day) {

            boolean oldDate = false;

            if(year < mYear){
                oldDate = true;}

            if(month < mMonth && year == mYear){
                oldDate = true;}

            if(day < mDay && month == mMonth && year == mYear){

                oldDate = true;}

            if(oldDate == false){
                mDateTextField.setVisibility(View.VISIBLE);
                mDateTextField.setText(day + "/" + (month + 1) + "/" + year);
            }
            else {
                Toast.makeText(getContext(), "Event request can't be set with old date", Toast.LENGTH_LONG).show();
            }

        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY)-5;
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            mTimeTextField.setVisibility(View.VISIBLE);
            mTimeTextField.setText(hourOfDay + ":" + minute);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

   /*  calendar = (CalendarView) findViewById(R.id.calendarView);
        calendar.setHorizontalScrollBarEnabled(true);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Toast.makeText(getApplicationContext(), dayOfMonth + "/" + month + "/" + year, Toast.LENGTH_LONG);

            }
        });*/