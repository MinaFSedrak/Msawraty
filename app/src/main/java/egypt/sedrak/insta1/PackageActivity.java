package egypt.sedrak.insta1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PackageActivity extends AppCompatActivity implements View.OnClickListener{

    private Spinner mCategorieSpinner;

    private List<SpinnerModel> mSpinnerList;

    private EditText mPriceField;
    private EditText mDurationField;
    private EditText mNoOfPhotographersField;
    private EditText mExtraDescriptionField;

    private String selectedCategorie;

    private Button mAddBtn;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_package);
        mCategorieSpinner = (Spinner) findViewById(R.id.package_categorie_spinner);
        mSpinnerList = new ArrayList<SpinnerModel>();
        setSpinnerList();

        SpinnerAdap spinnerAdap = new SpinnerAdap(getBaseContext(), mSpinnerList);
        mCategorieSpinner.setAdapter(spinnerAdap);

        mCategorieSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedCategorie = ((TextView)view.findViewById(R.id.spinnerCategorieName)).getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        mPriceField = (EditText) findViewById(R.id.package_price);
        mDurationField = (EditText) findViewById(R.id.package_duration);
        mNoOfPhotographersField = (EditText) findViewById(R.id.package_no_photographer);
        mExtraDescriptionField = (EditText) findViewById(R.id.package_extraDescription);


        mAddBtn = (Button) findViewById(R.id.package_add);
        mAddBtn.setOnClickListener(this);
    }

    private void setSpinnerList(){
        String[] categories = {"Wedding_and_Engagement", "Graduation", "PhotoBooth", "Birthday"};

        for(int i=0; i<categories.length; i++){

            SpinnerModel newSpinner = new SpinnerModel();

            newSpinner.setLogo("categorie"+(i+1));
            newSpinner.setCategorie(categories[i]);

            mSpinnerList.add(newSpinner);


        }

    }

    private void addPackage() {

        mIntent = new Intent(PackageActivity.this, PhotographerFormActivity.class);

        if(selectedCategorie.equals("Wedding_and_Engagement")){
            mIntent.putExtra("categorieLogo","categorie1");}
        else if(selectedCategorie.equals("Graduation")){
            mIntent.putExtra("categorieLogo","categorie2");}
        else if(selectedCategorie.equals("PhotoBooth")){
            mIntent.putExtra("categorieLogo","categorie3");}
        else if(selectedCategorie.equals("Birthday")){
            mIntent.putExtra("categorieLogo","categorie4");}


        mIntent.putExtra("categorie", selectedCategorie);
        mIntent.putExtra("price", mPriceField.getText().toString().trim());
        mIntent.putExtra("duration", mDurationField.getText().toString().trim());
        mIntent.putExtra("noOfPh", mNoOfPhotographersField.getText().toString().trim());

        if(mExtraDescriptionField.getText().toString().length() != 0){
            mIntent.putExtra("extraDescription", mExtraDescriptionField.getText().toString().trim());
        }else{
            mIntent.putExtra("extraDescription","nothing");}

        setResult(Activity.RESULT_OK,mIntent);
        finish();
        //Toast.makeText(this, "add package Runs correctly", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.package_add){

            boolean error = false;

            if(TextUtils.isEmpty(selectedCategorie)){
                LinearLayout linearLayout = (LinearLayout)mCategorieSpinner.getSelectedView();
                for(int i=0; i<linearLayout.getChildCount(); i++){
                    View view =  (View) linearLayout.getChildAt(i);

                    if(view instanceof TextView){
                        TextView SpinnerErrorText = (TextView) view;
                        SpinnerErrorText.setError("");
                        SpinnerErrorText.setTextColor(Color.RED);//just to highlight that this is an error
                        SpinnerErrorText.setText("Categorie is required ");//changes the selected item text to this
                        break;

                    }

                }
                error = true;}

            if(mPriceField.getText().toString().length() == 0 || Integer.parseInt(mPriceField.getText().toString().trim()) < 300){
                mPriceField.setError("A minimum Price 300 LE  is Required");
                error = true;}


            if(mNoOfPhotographersField.getText().toString().length() == 0){
                mNoOfPhotographersField.setError("No of photographers is required");
                error = true;}

            if(mDurationField.getText().toString().length() == 0){
                mDurationField.setError("Duration is required");
                error = true;}



            if(error == false){
                addPackage();
            }

        }


    }


}
