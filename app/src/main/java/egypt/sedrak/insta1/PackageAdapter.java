package egypt.sedrak.insta1;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovov on 09-Mar-17.
 */

public class PackageAdapter extends ArrayAdapter<Package> {

    Context context;
    List<Package> myPackages;
/*

    public PackageAdapter(Context context, int resource, int textViewResourceId, ArrayList<Package> objects) {
        super(context, resource, textViewResourceId, objects);
    }
*/

    public PackageAdapter(Context context , List<Package> myPackages){
        super(context, R.layout.package_row ,R.id.categorieView, myPackages);

        this.context = context;
        this.myPackages = myPackages;
    }



    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = convertView;


        if(rootView == null){
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rootView = inflater.inflate(R.layout.package_row,parent,false);
        }

        ImageView mCategorieView = (ImageView) rootView.findViewById(R.id.categorieView);
        TextView mPriceView = (TextView) rootView.findViewById(R.id.priceView);

        mCategorieView.setImageResource(context.getResources().getIdentifier("egypt.sedrak.insta1:mipmap/"+
                myPackages.get(position).getCategorieLogo(),null,null));
        mPriceView.setText(myPackages.get(position).getPrice());
        mPriceView.append(" LE");



        return rootView;
    }
}
