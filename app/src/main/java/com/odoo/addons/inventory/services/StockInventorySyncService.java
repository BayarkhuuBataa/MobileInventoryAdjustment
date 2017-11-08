package com.odoo.addons.inventory.services;

import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.odoo.addons.inventory.models.StockInventory;
import com.odoo.addons.inventory.models.StockInventoryLine;
import com.odoo.addons.stock.models.ProductProduct;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.service.ISyncFinishListener;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ko on 9/8/17.
 */

public class StockInventorySyncService extends OSyncService implements ISyncFinishListener {

    public static final String TAG = StockInventorySyncService.class.getSimpleName();
    private Context mContext;

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        mContext = context;
        return new OSyncAdapter(context, StockInventory.class, service, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        adapter.syncDataLimit(100);
        adapter.onSyncFinish(this);
//            domain.add("|");
//            domain.add("filter", "=", "partial");
//            domain.add("state", "=", "draft");
//            domain.add("state", "=", "confirm");
    }

    @Override
    public OSyncAdapter performNextSync(OUser user, SyncResult syncResult) {
        StockInventoryLine st = new StockInventoryLine(getApplicationContext(), null);
        List<ODataRow> stRows = st.select();
        ProductProduct productProduct = new ProductProduct(getApplicationContext(), user);
        List<Integer> ids=new ArrayList<>();

        for(ODataRow row:stRows){
            int serverId = productProduct.selectServerId(row.getInt("product_id"));
            ids.add(serverId);
        }

        ODomain domain = new ODomain();
        domain.add("id","in",ids);
        productProduct.quickSyncRecords(domain);
        return null;
    }
}
