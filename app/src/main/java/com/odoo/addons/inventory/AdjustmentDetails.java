package com.odoo.addons.inventory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.customers.utils.ShareUtil;
import com.odoo.addons.inventory.models.ProductProduct;
import com.odoo.addons.inventory.models.ProductUom;
import com.odoo.addons.inventory.models.StockInventory;
import com.odoo.addons.inventory.models.StockInventoryLine;
import com.odoo.addons.inventory.models.StockLocation;
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
import java.util.HashMap;
import java.util.List;

import odoo.controls.ExpandableListControl;
import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by ko on 10/5/17.
 */

public class AdjustmentDetails extends OdooCompatActivity
        implements View.OnClickListener,
        OField.IOnFieldValueChangeListener {

    public static final String TAG = Adjustments.class.getSimpleName();
    public static final int REQUEST_ADD_ITEMS = 323;

    private Bundle extras;
    private ODataRow record = null;
    private List<ODataRow> recordLine = new ArrayList<>();
    private OForm mForm;
    private App app;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private StockInventory stockInventory;
    private StockInventoryLine stockInventoryLine;
    private ProductProduct productProduct;
    private ProductUom productUom;
    private ExpandableListControl mList;
    private Context mContext;
    private OnStockInventoryChangeUpdate onSIChangeUpdate;
    private LinearLayout layoutAddItem;
    private HashMap<String, Float> lineValues = new HashMap<String, Float>();
    private HashMap<String, Integer> lineIds = new HashMap<String, Integer>();
    private ExpandableListControl.ExpandableListAdapter mAdapter;
    private List<Object> objects = new ArrayList<>();
    private StockLocation stockLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_inventory_detail);

        collapsingToolbarLayout = (CollapsingToolbarLayout)
                findViewById(R.id.stock_inventory_collapsing_toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (toolbar != null)
            collapsingToolbarLayout.setTitle("");
        if (savedInstanceState != null) {
            mEditMode = true;
        }
        app = (App) getApplicationContext();

        mList = (ExpandableListControl) findViewById(R.id.inventory_detail_lines);
        mList.setVisibility(View.VISIBLE);

        stockInventory = new StockInventory(this, null);
        stockInventoryLine = new StockInventoryLine(this, null);
        productProduct = new ProductProduct(this, null);
        stockLocation = new StockLocation(this, null);
        productUom = new ProductUom(this, null);

        extras = getIntent().getExtras();
        if (!hasRecordInExtra())
            mEditMode = true;
        setupToolbar();
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
        if (edit) {
            if (!hasRecordInExtra()) {
                collapsingToolbarLayout.setTitle("New");
            }
            mForm.setEditable(true);
            layoutAddItem.setEnabled(true);
            findViewById(R.id.layoutAddItem).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.layoutAddItem).setVisibility(View.GONE);
            layoutAddItem.setEnabled(false);
            mForm.setEditable(false);
        }
        setColor(color);
    }

    private void setupToolbar() {
        layoutAddItem = (LinearLayout) findViewById(R.id.layoutAddItem);
        layoutAddItem.setOnClickListener(this);
        mForm = (OForm) findViewById(R.id.stockInventoryForm);
        if (!hasRecordInExtra()) {
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(null);
        } else {
            init();
            drawLines(recordLine);
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(record);
            collapsingToolbarLayout.setTitle(record.getString("name"));

        }
    }

    private void init() {
        int rowId = extras.getInt(OColumn.ROW_ID);
        record = stockInventory.browse(rowId);
        recordLine = record.getO2MRecord("line_ids").browseEach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutAddItem:
                if (mForm.getValues() != null) {
                    getLineProducts();

                    Intent intent = new Intent(this, AddProductLineWizard.class);
                    Bundle extra = new Bundle();
                    for (String key : lineValues.keySet()) {
                        extra.putFloat(key, lineValues.get(key));
                    }
                    intent.putExtras(extra);
                    startActivityForResult(intent, REQUEST_ADD_ITEMS);
                }
                break;
        }
    }

    private void getLineProducts() {
        if (recordLine.size() > 0) {
            for (ODataRow line : recordLine) {
                int product_id = productProduct.selectServerId(line.getInt("product_id"));
                if (product_id != 0) {
                    lineValues.put(product_id + "", line.getFloat("product_qty"));
                }
            }
        }
    }

    private void setColor(int color) {
        mForm.setIconTintColor(color);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ODomain domain = new ODomain();
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_stock_inventory_save:
                OValues values = mForm.getValues();
                if (values != null) {
                    int stockInventoryId;
                    if (record != null) {
                        stockInventory.update(record.getInt(OColumn.ROW_ID), values);
                        stockInventoryId = record.getInt(OColumn.ROW_ID);
                        Toast.makeText(this, R.string.toast_information_saved, Toast.LENGTH_LONG).show();
                        mEditMode = !mEditMode;
                        setMode(false);
                    } else {
                        final int row_id = stockInventory.insert(values);
                        stockInventoryId = row_id;
                        if (row_id != OModel.INVALID_ROW_ID) {
                            Toast.makeText(this, R.string.stock_inventory_created, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                    createLocal(stockInventoryId);
                }
                break;
            case R.id.menu_stock_inventory_cancel:
            case R.id.menu_stock_inventory_edit:
                if (hasRecordInExtra()) {
                    mEditMode = !mEditMode;
                    setMode(mEditMode);
                    mForm.setEditable(mEditMode);
                    mForm.initForm(record);
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

    private class OnStockInventoryChangeUpdate extends AsyncTask<ODomain, Void, Void> {

        @Override
        protected Void doInBackground(ODomain... params) {
            if (app.inNetwork()) {
                ODomain domain = params[0];
                for (ODataRow r: stockInventory.select(null, "id = ?", new String[]{"0"})) {
                    stockInventory.quickCreateRecord(r);
                }
                stockInventory.quickSyncRecords(domain);
                if (!recordLine.equals("null")) {
                    for (ODataRow row : recordLine) {
                        stockInventoryLine.quickCreateRecord(row);
                    }
                    stockInventoryLine.quickSyncRecords(domain);
                }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stock_inventory_detail, menu);
        mMenu = menu;
        setMode(mEditMode);
        return true;
    }

    @Override
    public void onFieldValueChange(OField field, Object value) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(String.valueOf(false), mEditMode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_ITEMS && resultCode == Activity.RESULT_OK) {
            lineValues.clear();
            for (String key : data.getExtras().keySet()) {
//                if (data.getExtras().getFloat(key) > 0)
                lineValues.put(key, data.getExtras().getFloat(key));
            }
            propareLineData(lineValues);

        }
    }

    private void createLocal(Integer stockInventoryId) {
        onSIChangeUpdate = new OnStockInventoryChangeUpdate();
        List<ODataRow> existData = stockInventoryLine.select(null, "inventory_id = ?", new String[]{String.valueOf(stockInventoryId)});
        ODomain domain = new ODomain();

        if (!recordLine.equals(null)) {
            String updateRowId = new String();
            if (!existData.equals(null)) {
                for (ODataRow rec: recordLine) {
                    for (ODataRow local: existData) {
                        OValues oValues = new OValues();
                        oValues.put("inventory_id", stockInventoryId);
                        rec.addAll(oValues.toDataRow());
                        if (local.getInt("product_id") == rec.getInt("product_id")) {
                            updateRowId = String.valueOf(local.getString("_id"));
                        }
                    }
                    if (updateRowId.equals("")) {
                        stockInventoryLine.insert(rec.toValues());
                    } else {
                        stockInventoryLine.update(Integer.parseInt(updateRowId), rec.toValues());
                    }

                }
            } else {
                stockInventoryLine.insert((OValues) recordLine);
            }
        }

        List<ODataRow> updateLine = stockInventoryLine.select(null, "inventory_id = ?", new String[]{String.valueOf(stockInventoryId)});
        Log.d(" ___ NOW __", String.valueOf(updateLine));

        List ids = new ArrayList();
        for (ODataRow row : updateLine) {
            ids.add(row.getInt("_id"));
        }
        OValues values = new OValues();
        values.put("line_ids", ids);
        stockInventory.update(stockInventoryId, values);
//        onSIChangeUpdate.execute(domain);
        record = stockInventory.browse(stockInventoryId);
        List<ODataRow> fin = record.getO2MRecord("line_ids").browseEach();
        Log.d(" ___ FinaL __", String.valueOf(fin));
    }


    private void propareLineData(HashMap<String, Float>... params) {
        recordLine.clear();
        for (String key : params[0].keySet()) {
            Float qty = params[0].get(key);
            int product_row_id = productProduct.selectRowId(Float.valueOf(key).intValue());
            OValues values = new OValues();
            values.put("product_id", product_row_id);
            values.put("location_id", 1);
            values.put("theoretical_qty", 0.0);
            values.put("product_qty", qty);
            values.put("product_uom_id", 1);
            recordLine.add(values.toDataRow());
        }
        drawLines(recordLine);
    }

    private void drawLines(List<ODataRow> rows) {
        objects.clear();
        objects.addAll(rows);
        mAdapter = mList.getAdapter(R.layout.inventory_details, objects,
                new ExpandableListControl.ExpandableListAdapterGetViewListener() {
                    @Override
                    public View getView(int position, View mView, ViewGroup parent) {
                        ODataRow row = (ODataRow) mAdapter.getItem(position);
                        Log.d(TAG, "row : " + row);

                        List<ODataRow> prod = productProduct.select(null, "_id = ?", new String[]{row.getString("product_id")});
                        List<ODataRow> loc = stockLocation.select(null, "_id = ?", new String[]{row.getString("location_id")});
                        Log.d(TAG, "row : " + prod +" ||| "+ loc);

                        OControls.setText(mView, R.id.edit_product, prod.get(0).getString("name"));
                        OControls.setText(mView, R.id.edit_location, loc.get(0).getString("name"));
                        OControls.setText(mView, R.id.edit_check_qty, String.format("%.2f", row.getFloat("theoretical_qty")));
                        OControls.setText(mView, R.id.edit_real_qty, String.format("%.2f", row.getFloat("product_qty")));

                        return mView;
                    }
                });
        mAdapter.notifyDataSetChanged(objects);
    }

}