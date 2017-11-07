package com.odoo.addons.stock.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by ko on 10/10/17.
 */

public class ProductCategory extends OModel {

    public static final String TAG = ProductCategory.class.getSimpleName();

    OColumn name = new OColumn("Name", OVarchar.class).setRequired();
    OColumn parent_id = new OColumn("Parent Category", ProductCategory.class, OColumn.RelationType.ManyToOne);

    public ProductCategory(Context context, OUser user) {
        super(context, "product.category", user);
    }

}
