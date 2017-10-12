package com.odoo.addons.inventory.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.base.addons.res.ResCompany;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by ko on 9/7/17.
 */

public class StockInventory extends OModel {

    public static final String TAG = StockInventory.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.inventory.models.stock_inventory";

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100).setRequired();
    OColumn location_id = new OColumn("Inventoried Location", StockLocation.class, OColumn.RelationType.ManyToOne).setRequired();
    OColumn filter = new OColumn("Filter", OSelection.class).addSelection("category", "One product category")
                                                            .addSelection("product", "One product only")
                                                            .addSelection("partial", "Select products manually");
    OColumn state = new OColumn("State", OSelection.class).addSelection("draft", "Draft")
                                                          .addSelection("confirm", "In Progress");
    OColumn exhausted = new OColumn("Include Exhausted Products", OBoolean.class);
    OColumn date = new OColumn("Date", ODateTime.class).setRequired();
    OColumn company_id = new OColumn("Company", ResCompany.class, OColumn.RelationType.ManyToOne).setRequired();
    OColumn product_id = new OColumn("Inventoried Product", ProductProduct.class, OColumn.RelationType.ManyToOne);
    OColumn category_id = new OColumn("Inventoried Category", ProductCategory.class, OColumn.RelationType.ManyToOne);
    OColumn line_ids = new OColumn("Inventory line", StockInventoryLine.class, OColumn.RelationType.OneToMany).setRelatedColumn("inventory_id");

    public StockInventory(Context context, OUser user) {
        super(context, "stock.inventory", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

}
