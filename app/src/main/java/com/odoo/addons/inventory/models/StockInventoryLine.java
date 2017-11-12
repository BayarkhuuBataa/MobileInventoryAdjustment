package com.odoo.addons.inventory.models;

import android.content.Context;

import com.odoo.addons.stock.models.ProductProduct;
import com.odoo.addons.stock.models.StockLocation;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.support.OUser;

/**
 * Created by ko on 10/5/17.
 */

public class StockInventoryLine extends OModel {

    OColumn inventory_id = new OColumn("inventory", StockInventory.class, OColumn.RelationType.ManyToOne);
    OColumn product_id = new OColumn("Product", ProductProduct.class, OColumn.RelationType.ManyToOne);
    OColumn location_id = new OColumn("Location", StockLocation.class, OColumn.RelationType.ManyToOne);
    OColumn product_qty = new OColumn("Product qty", OFloat.class).setSize(100);
    OColumn theoretical_qty = new OColumn("Theoretical qty", OFloat.class);

    public StockInventoryLine(Context context, OUser user) {
        super(context, "stock.inventory.line", user);
    }

}