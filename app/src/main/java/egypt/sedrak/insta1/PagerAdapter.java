package egypt.sedrak.insta1;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;


public class PagerAdapter extends FragmentPagerAdapter {

    Context context;

    public PagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                PackageFragment tab1 = new PackageFragment();
                return tab1;
            case 1:
                PhotoFragment tab2 = new PhotoFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position)
    {switch (position)
    {
        case 0:
            return context.getString(R.string.packages);
        case 1:
            return context.getString(R.string.photos);
        default:
            return null;

    }

    }

    @Override
    public int getCount() {
        return 2;
    }
}

