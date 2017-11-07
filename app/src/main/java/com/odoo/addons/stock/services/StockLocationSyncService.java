package com.odoo.addons.stock.services;

import android.content.Context;
import android.os.Bundle;

import com.odoo.addons.stock.models.StockLocation;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by ko on 11/6/17.
 */

public class StockLocationSyncService extends OSyncService {
    public static final String TAG = StockLocationSyncService.class.getSimpleName();

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(context, StockLocation.class, this, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        ODomain domain = new ODomain();
        domain.add("active","=",true);
        adapter.syncDataLimit(80).setDomain(domain);
    }
}
