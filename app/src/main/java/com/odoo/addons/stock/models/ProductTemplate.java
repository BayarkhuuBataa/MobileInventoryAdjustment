package com.odoo.addons.stock.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by ko on 11/7/17.
 */

public class ProductTemplate extends OModel {
    public static final String TAG = ProductTemplate.class.getSimpleName();

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn uom_id = new OColumn("Product uom", ProductUom.class, OColumn.RelationType.ManyToOne);
    OColumn uom_po_id = new OColumn("Product uom po", ProductUom.class, OColumn.RelationType.ManyToOne);
    OColumn categ_id = new OColumn("Internal Category", ProductCategory.class, OColumn.RelationType.ManyToOne);
    OColumn type = new OColumn("Type", OVarchar.class);
    OColumn tracking = new OColumn("Type", OVarchar.class);
    OColumn location_id = new OColumn("Location", StockLocation.class, OColumn.RelationType.ManyToOne);

    public ProductTemplate(Context context, OUser user) {
        super(context, "product.template", user);
    }
}
