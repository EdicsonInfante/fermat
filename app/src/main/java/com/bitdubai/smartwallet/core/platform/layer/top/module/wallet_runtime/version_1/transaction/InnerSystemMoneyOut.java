package com.bitdubai.smartwallet.core.platform.layer.top.module.wallet_runtime.version_1.transaction;

import com.bitdubai.smartwallet.core.platform.global.definitions.money.Discount;
import com.bitdubai.smartwallet.core.platform.global.definitions.money.MoneySource;

/**
 * Created by ciencias on 21.12.14.
 */
public  class InnerSystemMoneyOut extends FiatCryptoWithSystemUser {

    private MoneySource[] mMoneySource;
    private Discount mDiscount;
}
