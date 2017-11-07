package com.odoo.addons.stock.providers;

import com.odoo.addons.stock.models.StockLocation;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by ko on 11/6/17.
 */

public class StockLocationProvider extends BaseModelProvider {
    public static final String TAG = StockLocationProvider.class.getSimpleName();

    @Override
    public String authority() {
        return StockLocation.AUTHORITY;
    }
}
