package com.odoo.addons.stock.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by ko on 11/7/17.
 */

public class StockLocation extends OModel {
    public static final String TAG = StockLocation.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.stock.models.stock_location";

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100);
    OColumn location_id = new OColumn("Parent Location", StockLocation.class, OColumn.RelationType.ManyToOne);
    OColumn count = new OColumn("Count", OBoolean.class).setLocalColumn();

    public StockLocation(Context context, OUser user) {
        super(context, "stock.location", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

}
