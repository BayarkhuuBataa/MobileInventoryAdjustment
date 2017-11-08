package com.odoo.addons.stock;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.stock.models.StockLocation;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.OdooCompatActivity;

import odoo.controls.OForm;

/**
 * Created by ko on 11/7/17.
 */

public class LocationDetails extends OdooCompatActivity {
    public static final String TAG = LocationDetails.class.getSimpleName();
    private StockLocation stockLocation;
    private ODataRow record;
    private Boolean mEditMode = false;
    private OForm form;
    private Menu menu;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_view);

        collapsingToolbarLayout = (CollapsingToolbarLayout)
                findViewById(R.id.collapsing_toolbar_location);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        form = (OForm) findViewById(R.id.locationForm);

        extras = getIntent().getExtras();
        init();
    }
    private void init() {
        int rowId = extras.getInt(OColumn.ROW_ID);
        stockLocation = new StockLocation(this, null);
        record = stockLocation.browse(rowId);
        form.initForm(record);
    }

    private void setMode(Boolean edit) {
        if (menu != null) {
            menu.findItem(R.id.menu_stock_location_edit).setVisible(!edit);
            menu.findItem(R.id.menu_stock_location_save).setVisible(edit);
            menu.findItem(R.id.menu_stock_location_cancel).setVisible(edit);
        }
//        form.setEditable(edit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu item) {
        getMenuInflater().inflate(R.menu.menu_stock_location_detail, item);
        menu = item;
        setMode(mEditMode);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_stock_location_save:
                OValues values = form.getValues();
                if (values != null) {
                    OValues oVal = new OValues();
                    oVal.put("count", false);
                    stockLocation.update(null, null, oVal);
                    oVal.put("count", true);
                    stockLocation.update(extras.getInt(OColumn.ROW_ID), oVal);
                    Toast.makeText(this, R.string.toast_information_saved, Toast.LENGTH_LONG).show();
                    mEditMode = !mEditMode;
                    setMode(false);
                }
                break;
            case R.id.menu_stock_location_edit:
                mEditMode = !mEditMode;
                setMode(mEditMode);
                break;
            case R.id.menu_stock_location_cancel:
                setMode(false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
