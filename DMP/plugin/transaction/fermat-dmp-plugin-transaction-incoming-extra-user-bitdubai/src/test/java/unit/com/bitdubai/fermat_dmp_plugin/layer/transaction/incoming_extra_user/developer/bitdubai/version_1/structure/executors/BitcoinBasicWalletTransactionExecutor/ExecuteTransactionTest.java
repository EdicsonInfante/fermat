package unit.com.bitdubai.fermat_dmp_plugin.layer.transaction.incoming_extra_user.developer.bitdubai.version_1.structure.executors.BitcoinBasicWalletTransactionExecutor;

import com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.AsymmectricCryptography;
import com.bitdubai.fermat_api.layer.all_definition.crypto.util.CryptoHasher;
import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.money.CryptoAddress;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.Action;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.Transaction;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.crypto_transactions.CryptoStatus;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.crypto_transactions.CryptoTransaction;
import com.bitdubai.fermat_api.layer.dmp_basic_wallet.bitcoin_wallet.interfaces.BitcoinWalletBalance;
import com.bitdubai.fermat_api.layer.dmp_basic_wallet.bitcoin_wallet.interfaces.BitcoinWalletWallet;
import com.bitdubai.fermat_dmp_plugin.layer.transaction.incoming_extra_user.developer.bitdubai.version_1.structure.executors.BitcoinBasicWalletTransactionExecutor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.fest.assertions.api.Assertions.*;
import static com.googlecode.catchexception.CatchException.*;
/**
 * Created by jorgegonzalez on 2015.07.08..
 */
@RunWith(MockitoJUnitRunner.class)
public class ExecuteTransactionTest {

    @Mock
    private BitcoinWalletWallet mockBitcoinWallet;
    @Mock
    private BitcoinWalletBalance mockBalance;

    private Transaction<CryptoTransaction> testTransaction;

    private BitcoinBasicWalletTransactionExecutor testExecutor;

    @Before
    public void setUpBalanceMockitoRules(){
        when(mockBitcoinWallet.getAvailableBalance()).thenReturn(mockBalance);
        when(mockBitcoinWallet.getBookBalance()).thenReturn(mockBalance);
    }

    @Test
    public void ExecuteTransaction_OnCryptoNetwork_InvocationSuccessfull() throws Exception {
        testExecutor = new BitcoinBasicWalletTransactionExecutor(mockBitcoinWallet);
        testTransaction = setUpTransaction(CryptoStatus.ON_CRYPTO_NETWORK);
        catchException(testExecutor).executeTransaction(testTransaction);
        assertThat(caughtException()).isNull();
    }

    @Test
    public void ExecuteTransaction_OnBlockChain_InvocationSuccessfull() throws Exception {
        testExecutor = new BitcoinBasicWalletTransactionExecutor(mockBitcoinWallet);
        testTransaction = setUpTransaction(CryptoStatus.ON_BLOCKCHAIN);
        catchException(testExecutor).executeTransaction(testTransaction);
        assertThat(caughtException()).isNull();
    }

    @Test
    public void ExecuteTransaction_ReverseOnCryptoNetwork_InvocationSuccessfull() throws Exception {
        testExecutor = new BitcoinBasicWalletTransactionExecutor(mockBitcoinWallet);
        testTransaction = setUpTransaction(CryptoStatus.REVERSED_ON_CRYPTO_NETWORK);
        catchException(testExecutor).executeTransaction(testTransaction);
        assertThat(caughtException()).isNull();
    }

    @Test
    public void ExecuteTransaction_ReverseOnBlockChain_InvocationSuccessfull() throws Exception {
        testExecutor = new BitcoinBasicWalletTransactionExecutor(mockBitcoinWallet);
        testTransaction = setUpTransaction(CryptoStatus.REVERSED_ON_BLOCKCHAIN);
        catchException(testExecutor).executeTransaction(testTransaction);
        assertThat(caughtException()).isNull();
    }

    private Transaction<CryptoTransaction> setUpTransaction(final CryptoStatus testStatus){
        return new Transaction<>(UUID.randomUUID(), setUpCryptoTransaction(testStatus), Action.APPLY, System.currentTimeMillis());
    }

    private CryptoTransaction setUpCryptoTransaction(final CryptoStatus testStatus){
        String addressFromString = AsymmectricCryptography.generateTestAddress(AsymmectricCryptography.derivePublicKey(AsymmectricCryptography.createPrivateKey()));
        String addressToString = AsymmectricCryptography.generateTestAddress(AsymmectricCryptography.derivePublicKey(AsymmectricCryptography.createPrivateKey()));
        CryptoAddress addressFrom = new CryptoAddress(addressFromString, CryptoCurrency.BITCOIN);
        CryptoAddress addressTo = new CryptoAddress(addressToString, CryptoCurrency.BITCOIN);
        String transactionHash = CryptoHasher.performSha256(AsymmectricCryptography.createPrivateKey());
        return new CryptoTransaction(transactionHash, addressFrom, addressTo, CryptoCurrency.BITCOIN, 1L, testStatus);
    }
}
