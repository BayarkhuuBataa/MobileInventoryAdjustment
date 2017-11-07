package com.odoo.addons.inventory;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.odoo.R;
import com.odoo.addons.inventory.models.StockInventory;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.odoo.core.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OCursorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ko on 10/5/17.
 */

public class Adjustments extends BaseFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        ISyncStatusObserverListener,
        SwipeRefreshLayout.OnRefreshListener,
        OCursorListAdapter.OnViewBindListener,
        AdapterView.OnItemClickListener,
        IOnSearchViewChangeListener,
        View.OnClickListener
{

    public static final String TAG = Adjustments.class.getSimpleName();

    private View mView;
    private ListView listView;
    private OCursorListAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
//        setHasSyncStatusObserver(null, this, db());
        return inflater.inflate(R.layout.common_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        listView = (ListView) mView.findViewById(R.id.listview);
        listAdapter = new OCursorListAdapter(getActivity(), null, android.R.layout.simple_list_item_1);
        listView.setAdapter(listAdapter);

        listAdapter.setOnViewBindListener(this);
        listView.setOnItemClickListener(this);

        setHasFloatingButton(view, R.id.fabButton, listView, this);

        setHasSyncStatusObserver(TAG, this, db());
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        OControls.setText(view, android.R.id.text1, row.getString("name"));
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> menu = new ArrayList<>();
        menu.add(new ODrawerItem(TAG)
                .setTitle("Adjustments")
                .setIcon(R.drawable.ic_action_goods)
                .setInstance(new Adjustments()));
        return menu;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), db().uri(), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        listAdapter.changeCursor(data);
        if (data.getCount() > 0) {
            OControls.setGone(mView, R.id.loadingProgress);
            OControls.setVisible(mView, R.id.swipe_container);
            OControls.setGone(mView, R.id.data_list_no_item);
            setHasSwipeRefreshView(mView, R.id.swipe_container, this);
        } else {
            OControls.setGone(mView, R.id.loadingProgress);
            OControls.setGone(mView, R.id.swipe_container);
            OControls.setVisible(mView, R.id.data_list_no_item);
            setHasSwipeRefreshView(mView, R.id.data_list_no_item, this);
            OControls.setText(mView, R.id.title, "No Adjustments found");
            OControls.setText(mView, R.id.subTitle, "Swipe to check new adjustments");
        }
        if (db().isEmptyTable()) {
            // Request for sync
            onRefresh();
        }
    }

    @Override
    public void onStatusChange(Boolean changed) {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onRefresh() {
        if (inNetwork()) {
            parent().sync().requestSync(StockInventory.AUTHORITY);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        listAdapter.changeCursor(null);
    }

    @Override
    public Class<StockInventory> database() {
        return StockInventory.class;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadActivity(ODataRow row) {
        Bundle data = new Bundle();
        if (row != null) {
            data = row.getPrimaryBundleData();
        }
        IntentUtils.startActivity(getActivity(), AdjustmentDetails.class, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabButton:
                loadActivity(null);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ODataRow row = OCursorUtils.toDatarow((Cursor) listAdapter.getItem(position));
        loadActivity(row);
    }

    @Override
    public boolean onSearchViewTextChange(String newFilter) {
        return false;
    }

    @Override
    public void onSearchViewClose() {

    }
}
