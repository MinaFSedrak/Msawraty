package egypt.sedrak.insta1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 3/21/2017.
 */

public class PageViewPagerAdapter extends ArrayAdapter<Package> {
    Context context;
    List<Package>myPackage;

    public PageViewPagerAdapter(Context context, List<Package>myPackage) {
        super(context,R.layout.package_viewpager_row,R.id.category_view ,myPackage);
        this.myPackage = myPackage;
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rootView = convertView;
        if (rootView== null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            rootView = inflater.inflate(R.layout.package_viewpager_row,parent,false);

        }

        ImageView logo = (ImageView) rootView.findViewById(R.id.category_view);
        TextView price = (TextView) rootView.findViewById(R.id.price_view);
        TextView no_photograpgers= (TextView) rootView.findViewById(R.id.no_Photographer_view);
        TextView duration = (TextView) rootView.findViewById(R.id.duration_view);
        TextView extra_description = (TextView) rootView.findViewById(R.id.extra_description_view);



        logo.setImageResource(context.getResources().getIdentifier("egypt.sedrak.insta1:mipmap/"+
                myPackage.get(position).getCategorieLogo(),null,null));

        price.setText(myPackage.get(position).getPrice());
        price.append(" LE");
        no_photograpgers.setText(myPackage.get(position).getNumOfPhotographers());
        duration.setText(myPackage.get(position).getDuration());
        extra_description.setText(myPackage.get(position).getExtraDescription());




        return rootView;
    }
}
