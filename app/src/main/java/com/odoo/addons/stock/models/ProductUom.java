package com.odoo.addons.stock.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by ko on 10/24/17.
 */

public class ProductUom extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100).setRequired();

    public ProductUom(Context context, OUser user) {
        super(context, "product.uom", user);
    }

}
