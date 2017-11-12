package com.odoo.addons.stock.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.support.OUser;

/**
 * Created by ko on 11/12/17.
 */

public class StockQuant extends OModel {

    OColumn product_id = new OColumn("Product", ProductProduct.class, OColumn.RelationType.ManyToOne);
    OColumn location_id = new OColumn("Location", StockLocation.class, OColumn.RelationType.ManyToOne);
    OColumn qty = new OColumn("Qty", OFloat.class);

    public StockQuant(Context context, OUser user) {
        super(context, "stock.quant", user);
    }


}
