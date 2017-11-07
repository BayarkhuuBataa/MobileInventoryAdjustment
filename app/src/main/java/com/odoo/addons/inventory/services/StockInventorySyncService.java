package com.odoo.addons.inventory.services;

import android.content.Context;
import android.os.Bundle;

import com.odoo.addons.inventory.models.StockInventory;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by ko on 9/8/17.
 */

public class StockInventorySyncService extends OSyncService {

    public static final String TAG = StockInventorySyncService.class.getSimpleName();
    private Context mContext;

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        mContext = context;
        return new OSyncAdapter(context, StockInventory.class, service, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        if(adapter.getModel().getModelName().equals("res.partner")) {
            ODomain domain = new ODomain();
//            domain.add("|");
//            domain.add("filter", "=", "partial");
//            domain.add("state", "=", "draft");
//            domain.add("state", "=", "confirm");
            adapter.syncDataLimit(100).setDomain(domain);
//            adapter.onSyncFinish(this);
        }
    }
}
