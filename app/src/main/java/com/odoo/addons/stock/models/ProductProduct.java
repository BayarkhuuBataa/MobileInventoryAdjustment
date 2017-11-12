package com.odoo.addons.stock.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by ko on 10/5/17.
 */

public class ProductProduct extends OModel {

    public static final String TAG = ProductProduct.class.getSimpleName();

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn default_code = new OColumn("Default code", OVarchar.class).setSize(100);
    OColumn barcode = new OColumn("Barcode", OVarchar.class).setSize(100);

    public ProductProduct(Context context, OUser user) {
        super(context, "product.product", user);
        setDefaultNameColumn("name");
    }

}
