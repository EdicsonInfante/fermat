package com.bitdubai.fermat_cbp_plugin.layer.business_transaction.broker_ack_offline_payment.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantInsertRecordException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantUpdateRecordException;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ClauseType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ContractTransactionStatus;
import com.bitdubai.fermat_cbp_api.all_definition.enums.MoneyType;
import com.bitdubai.fermat_cbp_api.all_definition.exceptions.ObjectNotSetException;
import com.bitdubai.fermat_cbp_api.all_definition.exceptions.UnexpectedResultReturnedFromDatabaseException;
import com.bitdubai.fermat_cbp_api.all_definition.negotiation.Clause;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.broker_ack_offline_payment.interfaces.BrokerAckOfflinePaymentManager;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.common.exceptions.CantAckPaymentException;
import com.bitdubai.fermat_cbp_api.layer.business_transaction.common.interfaces.ObjectChecker;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_sale.exceptions.CantGetListCustomerBrokerContractSaleException;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_sale.interfaces.CustomerBrokerContractSale;
import com.bitdubai.fermat_cbp_api.layer.contract.customer_broker_sale.interfaces.CustomerBrokerContractSaleManager;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.exceptions.CantGetListSaleNegotiationsException;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.interfaces.CustomerBrokerSaleNegotiation;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.interfaces.CustomerBrokerSaleNegotiationManager;
import com.bitdubai.fermat_cbp_api.layer.negotiation.exceptions.CantGetListClauseException;
import com.bitdubai.fermat_cbp_plugin.layer.business_transaction.broker_ack_offline_payment.developer.bitdubai.version_1.database.BrokerAckOfflinePaymentBusinessTransactionDao;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;

import java.util.Collection;
import java.util.UUID;


/**
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 17/12/15.
 */
public class BrokerAckOfflinePaymentTransactionManager implements BrokerAckOfflinePaymentManager {

    /**
     * Represents the plugin database DAO
     */
    BrokerAckOfflinePaymentBusinessTransactionDao brokerAckOfflinePaymentBusinessTransactionDao;

    /**
     * Represents the CustomerBrokerContractSaleManager
     */
    private CustomerBrokerContractSaleManager customerBrokerContractSaleManager;

    /**
     * Represents the error manager
     */
    ErrorManager errorManager;

    /**
     * Represents the customerBrokerSaleNegotiationManager
     */
    CustomerBrokerSaleNegotiationManager customerBrokerSaleNegotiationManager;

    //TODO: I need to define if this manager needs others arguments in constructor
    public BrokerAckOfflinePaymentTransactionManager(
            BrokerAckOfflinePaymentBusinessTransactionDao brokerAckOfflinePaymentBusinessTransactionDao,
            CustomerBrokerContractSaleManager customerBrokerContractSaleManager,
            ErrorManager errorManager,
            CustomerBrokerSaleNegotiationManager customerBrokerSaleNegotiationManager){
        this.brokerAckOfflinePaymentBusinessTransactionDao = brokerAckOfflinePaymentBusinessTransactionDao;
        this.customerBrokerContractSaleManager=customerBrokerContractSaleManager;
        this.errorManager=errorManager;
        this.customerBrokerSaleNegotiationManager=customerBrokerSaleNegotiationManager;
    }

    /**
     * This method creates an ack offline payment by a contract hash given
     * @param walletPublicKey
     * @param contractHash
     * @throws CantAckPaymentException
     */
    @Override
    public void ackPayment(String walletPublicKey,
                           String contractHash) throws
            CantAckPaymentException {
        try{
            //Checking the arguments
            Object[] arguments={walletPublicKey, contractHash};
            ObjectChecker.checkArguments(arguments);
            //First we check if the contract exits in this plugin database
            boolean contractExists=
                    this.brokerAckOfflinePaymentBusinessTransactionDao.isContractHashInDatabase(
                            contractHash);
            CustomerBrokerContractSale customerBrokerContractSale;
            if(!contractExists){
                /**
                 * If the contract is not in database, we are going to check if exists in contract Layer,
                 * in theory this won't happen, when a contract is open is created in contract layer
                 * and is raised an event that build a record in this plugin database. In this case we
                 * will suppose that the agent in this plugin has not created the contract, but exists in
                 * contract layer.
                 */
                customerBrokerContractSale=
                        this.customerBrokerContractSaleManager.getCustomerBrokerContractSaleForContractId(
                                contractHash);
                if(customerBrokerContractSale==null){
                    throw new CantAckPaymentException("The CustomerBrokerContractSale with the hash \n" +
                            contractHash+"\n" +
                            "is null");
                }
                MoneyType paymentType=getMoneyTypeFromContract(customerBrokerContractSale);
                this.brokerAckOfflinePaymentBusinessTransactionDao.persistContractInDatabase(
                        customerBrokerContractSale,
                        paymentType);

            } else{
                /**
                 * The contract exists in database, we are going to check the contract status.
                 * We are going to get the record from this contract and
                 * update the status to indicate the agent to send a ack notification to a Crypto Customer.
                 */
                ContractTransactionStatus contractTransactionStatus=getContractTransactionStatus(
                    contractHash);
                //If the status is different to PENDING_OFFLINE_PAYMENT_CONFIRMATION the ack process was started.
                if(!contractTransactionStatus.getCode()
                        .equals(ContractTransactionStatus.PENDING_ACK_OFFLINE_PAYMENT.getCode())){
                    this.brokerAckOfflinePaymentBusinessTransactionDao.updateContractTransactionStatus(
                            contractHash,
                            ContractTransactionStatus.PENDING_OFFLINE_PAYMENT_CONFIRMATION);

                } else{
                    try{
                        throw new CantAckPaymentException(
                                "The Ack offline payment with the contract ID "+
                                        contractHash +
                                        " process has begun");
                    }catch (CantAckPaymentException e){
                        errorManager.reportUnexpectedPluginException(
                                Plugins.BROKER_ACK_OFFLINE_PAYMENT,
                                UnexpectedPluginExceptionSeverity.NOT_IMPORTANT,
                                e);
                    }
                }

            }
        } catch (UnexpectedResultReturnedFromDatabaseException e) {
            throw new CantAckPaymentException(e,
                    "Creating Broker Ack Offline Payment Business Transaction",
                    "Unexpected result from database");
        } catch (CantGetListCustomerBrokerContractSaleException e) {
            throw new CantAckPaymentException(e,
                    "Creating Broker Ack Offline Payment Business Transaction",
                    "Cannot get the contract from customerBrokerContractSaleManager");
        } catch (CantInsertRecordException e) {
            throw new CantAckPaymentException(e,
                    "Creating Broker Ack Offline Payment Business Transaction",
                    "Cannot insert the contract record in database");
        } catch (CantUpdateRecordException e) {
            throw new CantAckPaymentException(e,
                    "Creating Broker Ack Offline Payment Business Transaction",
                    "Cannot update the contract status in database");
        } catch (ObjectNotSetException e) {
            throw new CantAckPaymentException(e,
                    "Creating Broker Ack Offline Payment Business Transaction",
                    "Invalid input to this manager");
        } catch (CantGetListSaleNegotiationsException e) {
            throw new CantAckPaymentException(e,
                    "Creating Broker Ack Offline Payment Business Transaction",
                    "Cannot get the payment type");
        }

    }

    /**
     * This method returns the actual ContractTransactionStatus by the contract Id/hash
     * @param contractHash
     * @return
     * @throws UnexpectedResultReturnedFromDatabaseException
     */
    @Override
    public ContractTransactionStatus getContractTransactionStatus(
            String contractHash) throws
            UnexpectedResultReturnedFromDatabaseException {
        try{
            ObjectChecker.checkArgument(contractHash, "The contractHash argument is null");
            return this.brokerAckOfflinePaymentBusinessTransactionDao.getContractTransactionStatus(
                    contractHash);
        } catch (ObjectNotSetException e) {
            errorManager.reportUnexpectedPluginException(
                    Plugins.BROKER_ACK_OFFLINE_PAYMENT,
                    UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN,
                    e);
            throw new UnexpectedResultReturnedFromDatabaseException(
                    "Cannot check a null contractHash/Id");
        }
    }
    /**
     * This method returns the currency type from a contract
     *
     * @param customerBrokerContractSale
     * @return
     * @throws CantGetListSaleNegotiationsException
     */
    public MoneyType getMoneyTypeFromContract(
            CustomerBrokerContractSale customerBrokerContractSale) throws
            CantGetListSaleNegotiationsException {
        try {
            String negotiationId = customerBrokerContractSale.getNegotiatiotId();
            CustomerBrokerSaleNegotiation customerBrokerSaleNegotiation =
                    customerBrokerSaleNegotiationManager.getNegotiationsByNegotiationId(
                            UUID.fromString(negotiationId));
            ObjectChecker.checkArgument(customerBrokerSaleNegotiation,"The customerBrokerSaleNegotiation is null");
            Collection<Clause> clauses = customerBrokerSaleNegotiation.getClauses();
            ClauseType clauseType;
            for (Clause clause : clauses) {
                clauseType = clause.getType();
                if (clauseType.equals(ClauseType.BROKER_PAYMENT_METHOD)) {
                    return MoneyType.getByCode(clause.getValue());
                }
            }
            throw new CantGetListSaleNegotiationsException(
                    "Cannot find the proper clause");
        } catch (InvalidParameterException e) {
            throw new CantGetListSaleNegotiationsException(
                    "Cannot get the negotiation list",
                    e);
        } catch (CantGetListClauseException e) {
            throw new CantGetListSaleNegotiationsException(
                    "Cannot find clauses list");
        } catch (ObjectNotSetException e) {
            throw new CantGetListSaleNegotiationsException(
                    "The customerBrokerSaleNegotiation is null",
                    e);
        }

    }
}
