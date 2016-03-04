package com.bitdubai.android_core.app.common.version_1.recents;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bitdubai.fermat.R;
import com.wirelesspienetwork.overview.model.OverviewAdapter;

import java.util.List;

/**
 * Created by mati on 2016.03.03..
 */
public class RecentsAdapter extends OverviewAdapter<RecentHolder,RecentApp> {


    private final Context context;

    public RecentsAdapter(Context context,List<RecentApp> recentApps) {
        super(recentApps);
        this.context = context;
    }

    @Override
    public RecentHolder onCreateViewHolder(Context context, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.recents_dummy, null);
        return new RecentHolder(v);
    }

    @Override
    public void onBindViewHolder(RecentHolder viewHolder) {
        viewHolder.itemView.setBackgroundColor(viewHolder.model.getBackgroundColor());
        //viewHolder.getRoot().addView(View.inflate(context, R.layout.widgetlayout, null));
    }


}