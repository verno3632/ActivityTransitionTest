package jp.developer.retia.activitytransitiontest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.util.List;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {
    public static final String ARG_ITEM_IDS = "item_ids";
    public static final String ARG_POSITION = "position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        List<String> items = getIntent().getStringArrayListExtra(ARG_ITEM_IDS);
        int position = getIntent().getIntExtra(ARG_POSITION, 0);
        setResultPositioin(position);


        final ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);

        ItemAdapter adapter = new ItemAdapter(items);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setResultPositioin(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        supportPostponeEnterTransition();
        viewPager.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                viewPager.getViewTreeObserver().removeOnPreDrawListener(this);
                supportStartPostponedEnterTransition();
                return true;
            }
        });


        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                int position = viewPager.getCurrentItem();
                View v = viewPager.findViewWithTag("item_"+position);
                sharedElements.clear();
                sharedElements.put("item_"+position, v);
            }
        });
    }

    private void setResultPositioin(int position) {
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        data.putExtras(bundle);

        setResult(Activity.RESULT_OK, data);
    }

    public class ItemAdapter extends PagerAdapter {
        private List<String> items;

        public ItemAdapter(List<String> items){
            super();
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position){
            ImageView imageview = new ImageView(container.getContext());
            imageview.setImageResource(MasterActivity.image_drawables[position]);
            imageview.setTag("item_"+position);
            ViewCompat.setTransitionName(imageview, "item_"+position);
            container.addView(imageview);
            return imageview;
        }

        @Override
        public void destroyItem(ViewGroup collection, int pos, Object view) {
            collection.removeView((View)view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
