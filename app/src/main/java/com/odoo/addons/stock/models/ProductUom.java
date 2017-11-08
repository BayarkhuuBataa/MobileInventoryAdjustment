package com.odoo.addons.stock.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by ko on 10/24/17.
 */

public class ProductUom extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100);
    OColumn rounding = new OColumn("Rounding", OFloat.class).setSize(100);
    OColumn uom_type = new OColumn("Uom type", OVarchar.class).setSize(100);
    OColumn factor = new OColumn("Factor", OFloat.class).setSize(100);
    OColumn category_id = new OColumn("Internal Category", ProductCategory.class, OColumn.RelationType.ManyToOne);

    public ProductUom(Context context, OUser user) {
        super(context, "product.uom", user);
    }

}
