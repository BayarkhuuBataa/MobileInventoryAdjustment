package com.odoo.addons.inventory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.customers.utils.ShareUtil;
import com.odoo.addons.inventory.models.StockInventory;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OResource;
import com.odoo.core.utils.OStringColorUtil;

import java.util.ArrayList;
import java.util.List;

import odoo.controls.ExpandableListControl;
import odoo.controls.OField;
import odoo.controls.OForm;

import static com.odoo.core.utils.OAlert.showError;

/**
 * Created by ko on 10/5/17.
 */

public class AdjustmentDetails extends OdooCompatActivity
        implements View.OnClickListener,
        OField.IOnFieldValueChangeListener {

    public static final String TAG = Adjustments.class.getSimpleName();
    private static final int REQUEST_ADD_ITEMS = 0;

    private Bundle extras;
    private StockInventory stockInventory;
    private ODataRow record = null;
    private List<ODataRow> recordLine = null;
    private ImageView userImage = null;
    private OForm mForm;
    private App app;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private OFileManager fileManager;
    private String newImage = null;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private Context mContext;
    private OnStockInventoryChangeUpdate onStockInventoryChangeUpdate;
    private ExpandableListControl mList;
    private List<Object> objects = new ArrayList<Object>();
    private ExpandableListControl.ExpandableListAdapter mAdapter;
    private TabLayout tabLayout;
    private LinearLayout layoutAddItem;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_inventory_detail);

        mContext = getApplicationContext();

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Inventory Details"));
        layoutAddItem = (LinearLayout) findViewById(R.id.layoutAddItem);
        layoutAddItem.setOnClickListener(this);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.stock_inventory_collapsing_toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fileManager = new OFileManager(this);
        if (toolbar != null)
            collapsingToolbarLayout.setTitle("New");
        if (savedInstanceState != null) {
            mEditMode = true;
        }
        app = (App) getApplicationContext();
        stockInventory = new StockInventory(this, null);
        extras = getIntent().getExtras();
        if (!hasRecordInExtra())
            mEditMode = true;
        setupToolbar();
        ExtendableAdapter();
    }

    private boolean hasRecordInExtra() {
        return extras != null && extras.containsKey(OColumn.ROW_ID);
    }

    private void setMode(Boolean edit) {
        if (mMenu != null) {
            mMenu.findItem(R.id.menu_stock_inventory_detail_more).setVisible(!edit);
            mMenu.findItem(R.id.menu_stock_inventory_edit).setVisible(!edit);
            mMenu.findItem(R.id.menu_stock_inventory_save).setVisible(edit);
            mMenu.findItem(R.id.menu_stock_inventory_cancel).setVisible(edit);
        }
        int color = Color.DKGRAY;
        if (record != null) {
            color = OStringColorUtil.getStringColor(this, record.getString("name"));
        }
        mForm = (OForm) findViewById(R.id.stockInventoryForm);
        if (edit) {
            if (!hasRecordInExtra()) {
                collapsingToolbarLayout.setTitle("New");
            }
            mForm.setEditable(true);
        } else {
            mForm.setEditable(false);
        }
        setColor(color);
    }

    private void setupToolbar() {
        if (!hasRecordInExtra()) {
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(null);
        } else {
            int rowId = extras.getInt(OColumn.ROW_ID);
            record = stockInventory.browse(rowId);
            recordLine = record.getO2MRecord("line_ids").browseEach();
            checkControls();
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(record);
            collapsingToolbarLayout.setTitle(record.getString("name"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutAddItem:
                loadActivity();
                finish();
                break;
        }
    }

    private void loadActivity() {
        Intent intent = new Intent(this, AddProductLineWizard.class);
        Bundle extra = new Bundle();
//        for (String key : recordLine.keySet()) {
//            extra.putFloat(key, recordLine.get(key));
//        }
        intent.putExtras(extra);
        startActivityForResult(intent, REQUEST_ADD_ITEMS);

//        Intent intent = new Intent(this, AddProductLineWizard.class);
//        startActivity(intent);

    }

    private void checkControls() {
        findViewById(R.id.name).setOnClickListener(this);
//        findViewById(R.id.website).setOnClickListener(this);
//        findViewById(R.id.email).setOnClickListener(this);
//        findViewById(R.id.phone_number).setOnClickListener(this);
//        findViewById(R.id.mobile_number).setOnClickListener(this);
    }

    private void setColor(int color) {
        mForm.setIconTintColor(Color.RED);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onStockInventoryChangeUpdate = new OnStockInventoryChangeUpdate();
        ODomain domain = new ODomain();
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_stock_inventory_save:
                OValues values = mForm.getValues();
                if (values != null) {
                    if (record != null) {
                        stockInventory.update(record.getInt(OColumn.ROW_ID), values);
                        onStockInventoryChangeUpdate.execute(domain);
                        Toast.makeText(this, R.string.toast_information_saved, Toast.LENGTH_LONG).show();
                        mEditMode = !mEditMode;
                        setupToolbar();
                    } else {
                        final int row_id = stockInventory.insert(values);
                        if (row_id != OModel.INVALID_ROW_ID) {
                            onStockInventoryChangeUpdate.execute(domain);
                            Toast.makeText(this, R.string.stock_inventory_created, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
                break;
            case R.id.menu_stock_inventory_cancel:
            case R.id.menu_stock_inventory_edit:
                if (hasRecordInExtra()) {
                    mEditMode = !mEditMode;
                    setMode(mEditMode);
                    mForm.setEditable(mEditMode);
                    mForm.initForm(record);
//                    setCustomerImage();
                } else {
                    finish();
                }
                break;
            case R.id.menu_stock_inventory_share:
                ShareUtil.shareContact(this, record, true);
                break;
            case R.id.menu_stock_inventory_import:
                ShareUtil.shareContact(this, record, false);
                break;
            case R.id.menu_stock_inventory_delete:
                OAlert.showConfirm(this, OResource.string(this,
                        R.string.confirm_are_you_sure_want_to_delete),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    // Deleting record and finishing activity if success.
                                    if (stockInventory.delete(record.getInt(OColumn.ROW_ID))) {
                                        Toast.makeText(AdjustmentDetails.this, R.string.toast_record_deleted,
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            }
                        });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stock_inventory_detail, menu);
        mMenu = menu;
        setMode(mEditMode);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(String.valueOf(false), mEditMode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class OnStockInventoryChangeUpdate extends AsyncTask<ODomain, Void, Void> {

        @Override
        protected Void doInBackground(ODomain... params) {
            if (app.inNetwork()) {
                ODomain domain = params[0];
                domain.add("&");
                domain.add("|");
                domain.add("state","=","draft");
                domain.add("state","=","confirm");
                domain.add("filter","!=","none");
                List<ODataRow> rows = stockInventory.select(null, "id = ?", new String[]{"0"});
                for (ODataRow row : rows) {
                    stockInventory.quickCreateRecord(row);
                }
                stockInventory.quickSyncRecords(domain);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!app.inNetwork())
                Toast.makeText(mContext, OResource.string(mContext, R.string.toast_network_required), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFieldValueChange(OField field, Object value) {
//        if (record == null && field.getFieldName().equals("technic_id")) {
//            ODataRow techVal = (ODataRow) value;
//            technicSync(techVal.getString("id"));

        }

    private void ExtendableAdapter() {
        try {
            mList = (ExpandableListControl) findViewById(R.id.inventory_detail_lines);
            mList.setVisibility(View.VISIBLE);
//            mList.setOnClickListener();
            if (extras != null && record != null) {
                if (recordLine.size() > 0) {
                    objects.addAll(recordLine);
                }
            }
            mAdapter = mList.getAdapter(R.layout.inventory_details, objects,
                    new ExpandableListControl.ExpandableListAdapterGetViewListener() {
                        @Override
                        public View getView(int position, View mView, ViewGroup parent) {
                            ODataRow row = (ODataRow) mAdapter.getItem(position);
                            Log.d(TAG, "row : " + row);
                            ODataRow prod = row.getM2ORecord("product_id").browse();
                            ODataRow loc = row.getM2ORecord("location_id").browse();
                            OControls.setText(mView, R.id.edit_product, prod.getString("name"));
                            OControls.setText(mView, R.id.edit_location, loc.getString("name"));
                            OControls.setText(mView, R.id.edit_check_qty, String.format("%.2f", row.getFloat("theoretical_qty")));
                            OControls.setText(mView, R.id.edit_real_qty, String.format("%.2f", row.getFloat("product_qty")));
                            return mView;
                        }
                    });
            mAdapter.notifyDataSetChanged(objects);
        } catch (Exception ex) {
            showError(this, "Error" + ex.toString());
            Log.e(TAG, "ERROR", ex);
        }
    }
}