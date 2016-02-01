package com.bitdubai.fermat_dap_plugin.layer.wallet.asset.user.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.all_definition.money.CryptoAddress;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_user_wallet.interfaces.AssetUserWalletTransaction;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.enums.BalanceType;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.enums.TransactionType;

/**
 * Created by franklin on 08/10/15.
 */
public class AssetUserWalletTransactionWrapper implements AssetUserWalletTransaction {
    private final String transactionId;
    private final String transactionHash;
    private final String assetPublicKey;
    private final TransactionType transactionType;
    private final CryptoAddress addressFrom;
    private final CryptoAddress addressTo;
    private final String actorFromPublicKey;
    private final String actorToPublicKey;
    private final Actors actorFromType;
    private final Actors actorToType;
    private final BalanceType balanceType;
    private final long amount;
    private final long runningBookBalance;
    private final long runningAvailableBalance;
    private final long timeStamp;
    private final String memo;

    public AssetUserWalletTransactionWrapper(final String transactionId,
                                               final String transactionHash,
                                               final String assetPublicKey,
                                               final TransactionType transactionType,
                                               final CryptoAddress addressFrom,
                                               final CryptoAddress addressTo,
                                               final String actorFromPublicKey,
                                               final String actorToPublicKey,
                                               final Actors actorFromType,
                                               final Actors actorToType,
                                               final BalanceType balanceType,
                                               final long amount,
                                               final long runningBookBalance,
                                               final long runningAvailableBalance,
                                               final long timeStamp,
                                               final String memo) {
        this.transactionId = transactionId;
        this.assetPublicKey = assetPublicKey;
        this.transactionHash = transactionHash;
        this.transactionType = transactionType;
        this.addressFrom = addressFrom;
        this.addressTo = addressTo;
        this.actorFromPublicKey = actorFromPublicKey;
        this.actorToPublicKey = actorToPublicKey;
        this.actorFromType = actorFromType;
        this.actorToType = actorToType;
        this.balanceType = balanceType;
        this.amount = amount;
        this.runningBookBalance = runningBookBalance;
        this.runningAvailableBalance = runningAvailableBalance;
        this.timeStamp = timeStamp;
        this.memo = memo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssetUserWalletTransactionWrapper that = (AssetUserWalletTransactionWrapper) o;

        if (getActualTransactionHash() != null ? !getActualTransactionHash().equals(that.getActualTransactionHash()) : that.getActualTransactionHash() != null)
            return false;
        if (getGenesisTransaction() != null ? !getGenesisTransaction().equals(that.getGenesisTransaction()) : that.getGenesisTransaction() != null)
            return false;
        return !(getAssetPublicKey() != null ? !getAssetPublicKey().equals(that.getAssetPublicKey()) : that.getAssetPublicKey() != null);

    }

    @Override
    public int hashCode() {
        int result = getActualTransactionHash() != null ? getActualTransactionHash().hashCode() : 0;
        result = 31 * result + (getGenesisTransaction() != null ? getGenesisTransaction().hashCode() : 0);
        result = 31 * result + (getAssetPublicKey() != null ? getAssetPublicKey().hashCode() : 0);
        return result;
    }

    @Override
    public String getAssetPublicKey() {
        return assetPublicKey;
    }

    @Override
    public String getActualTransactionHash() {
        return transactionId;
    }

    @Override
    public String getGenesisTransaction() {
        return transactionHash;
    }

    @Override
    public CryptoAddress getAddressFrom() {
        return addressFrom;
    }

    @Override
    public Actors getActorFromType() {
        return actorFromType;
    }

    @Override
    public CryptoAddress getAddressTo() {
        return addressTo;
    }

    @Override
    public Actors getActorToType() {
        return actorToType;
    }

    @Override
    public String getActorToPublicKey() {
        return actorToPublicKey;
    }

    @Override
    public String getActorFromPublicKey() {
        return actorFromPublicKey;
    }

    @Override
    public BalanceType getBalanceType() {
        return balanceType;
    }

    @Override
    public TransactionType getTransactionType() {
        return transactionType;
    }

    @Override
    public long getTimestamp() {
        return timeStamp;
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @Override
    public long getRunningBookBalance() {
        return runningBookBalance;
    }

    @Override
    public long getRunningAvailableBalance() {
        return runningAvailableBalance;
    }

    @Override
    public String getMemo() {
        return memo;
    }

}
