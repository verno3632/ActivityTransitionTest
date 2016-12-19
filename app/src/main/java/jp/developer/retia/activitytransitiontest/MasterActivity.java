package jp.developer.retia.activitytransitiontest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MasterActivity extends AppCompatActivity {
    private View sharedElement;
    public static final Integer[] image_drawables = {
            R.drawable.dog1,
            R.drawable.dog2,
            R.drawable.dog3,
            R.drawable.dog4,
            R.drawable.dog5,
            R.drawable.dog1,
            R.drawable.dog2,
            R.drawable.dog3,
            R.drawable.dog4,
            R.drawable.dog5,
            R.drawable.dog1,
            R.drawable.dog2,
            R.drawable.dog3,
            R.drawable.dog4,
            R.drawable.dog5
    };

    public static final int REQUEST_CODE_POSITION = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View recyclerView = findViewById(R.id.item_list);
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Arrays.asList(image_drawables)));
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {

        final int position = data.getIntExtra(DetailActivity.ARG_POSITION, -1);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.item_list);
        assert recyclerView != null;

        final GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();

        if (position < manager.findFirstVisibleItemPosition()
                || position > manager.findLastVisibleItemPosition()) {
            supportPostponeEnterTransition();

            recyclerView.scrollToPosition(position);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (position >= manager.findFirstVisibleItemPosition()
                            && position <= manager.findLastVisibleItemPosition()) {
                        sharedElement = recyclerView.getChildAt(
                                position - manager.findFirstVisibleItemPosition()).findViewById(
                                R.id.content);
                        recyclerView.removeOnScrollListener(this);
                        supportStartPostponedEnterTransition();

                    }
                }
            });
        } else {
            sharedElement = recyclerView.getChildAt(
                    position - manager.findFirstVisibleItemPosition()).findViewById(R.id.content);
        }

        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                sharedElements.clear();
                sharedElements.put("item_" + position, sharedElement);
                setExitSharedElementCallback(new SharedElementCallback() {
                });
            }
        });
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Integer> mValues;

        public SimpleItemRecyclerViewAdapter(List<Integer> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mContentView.setImageResource(mValues.get(position));

            holder.mContentView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    ActivityOptionsCompat options1 =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    MasterActivity.this, v,
                                    "item_" + holder.getAdapterPosition());
                    Intent intent = new Intent(context, DetailActivity.class);

                    intent.putIntegerArrayListExtra(DetailActivity.ARG_ITEM_IDS,
                            new ArrayList<>(mValues));
                    intent.putExtra(DetailActivity.ARG_POSITION, holder.getAdapterPosition());

                    ActivityCompat.startActivityForResult(MasterActivity.this, intent,
                            REQUEST_CODE_POSITION, options1.toBundle());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mContentView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (ImageView) view.findViewById(R.id.content);
            }
        }
    }
}
