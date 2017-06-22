package egypt.sedrak.insta1;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lenovov on 20-Mar-17.
 */

public class RequestSpinnerAdapter extends ArrayAdapter<Package> {

    Context context;
    List<Package> spinnerModelList;

    public RequestSpinnerAdapter(Context context, List<Package> spinnerModelList) {
        super(context, R.layout.spinner_row,spinnerModelList);

        this.context = context;
        this.spinnerModelList = spinnerModelList;


    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position,convertView,parent);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = convertView;


        if(rootView == null){
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rootView = inflater.inflate(R.layout.spinner_row,parent,false);
        }



            TextView mCategorie = (TextView) rootView.findViewById(R.id.spinnerCategorieName);
            ImageView mLogo = (ImageView) rootView.findViewById(R.id.logoCategorie);

            Log.i("test",spinnerModelList.get(position).getPrice());
            Log.i("test",spinnerModelList.get(position).getCategorieLogo());



            mCategorie.setText(spinnerModelList.get(position).getPrice() + "  LE");
            mLogo.setImageResource(context.getResources().getIdentifier("egypt.sedrak.insta1:mipmap/"
                    +spinnerModelList.get(position).getCategorieLogo(),null,null));





        return rootView;


    }
}
