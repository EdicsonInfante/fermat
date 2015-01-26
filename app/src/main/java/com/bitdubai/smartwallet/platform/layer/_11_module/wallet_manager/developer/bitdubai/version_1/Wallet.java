package com.bitdubai.smartwallet.platform.layer._11_module.wallet_manager.developer.bitdubai.version_1;


import com.bitdubai.smartwallet.platform.layer._11_module.wallet_manager.*;
import com.bitdubai.smartwallet.platform.layer._1_definition.enums.DeviceDirectory;
import com.bitdubai.smartwallet.platform.layer._1_definition.event.PlatformEvent;
import com.bitdubai.smartwallet.platform.layer._2_event.EventManager;
import com.bitdubai.smartwallet.platform.layer._2_event.manager.DealsWithEvents;
import com.bitdubai.smartwallet.platform.layer._2_event.manager.EventType;
import com.bitdubai.smartwallet.platform.layer._2_event.manager.WalletCreatedEvent;
import com.bitdubai.smartwallet.platform.layer._2_event.manager.WalletWentOnlineEvent;
import com.bitdubai.smartwallet.platform.layer._3_os.*;



import java.util.UUID;

/**
 * Created by ciencias on 25.01.15.
 */
public class Wallet implements WalletManagerWallet, DealsWithEvents, DealsWithFileSystem {

    UUID walletId;
    String walletName = "";
    WalletType walletType;
    WalletStatus status;

    /**
     * UsesFileSystem Interface member variables.
     */
    FileSystem fileSystem;

    /**
     * DealWithEvents Interface member variables.
     */
    EventManager eventManager;


    /**
     * Wallet Interface implementation.
     */

    /**
     * This method is to be used for creating a new wallet.
     */

    public void createWallet(WalletType walletType) throws CantCreateWalletException {
        this.walletId = UUID.randomUUID();
        this.status = WalletStatus.CLOSED;
        this.walletType = walletType;

        try {
            persist();
        }
        catch (CantPersistWalletException cantPersistWalletException)
        {
            /**
             * This is bad, but lets handle it...
             */
            System.err.println("CantPersistWalletException: " + cantPersistWalletException.getMessage());
            cantPersistWalletException.printStackTrace();
            throw new CantCreateWalletException();
        }

        /**
         * Now I fire the Wallet Created  event.
         */

        PlatformEvent platformEvent = eventManager.getNewEvent(EventType.WALLET_CREATED);
        ((WalletCreatedEvent) platformEvent).setWalletId(this.walletId);
        eventManager.raiseEvent(platformEvent);

    }


    /**
     * This method is to be used to load a saved wallet and to put it online.
     */

    public void loadWallet (UUID walletId) throws CantLoadWalletException {

        this.walletId = walletId;

        try {
            load();
            this.changeToOnlineStatus();
        }
        catch (CantLoadWalletException cantLoadWalletException)
        {
            /**
             * This is bad, but lets handle it...
             */
            System.err.println("CantLoadWalletException: " + cantLoadWalletException.getMessage());
            cantLoadWalletException.printStackTrace();
            throw new CantLoadWalletException();
        }

    }

    @Override
    public UUID getId() {
        return this.walletId ;
    }

    @Override
    public String getWalletName() {
        return this.walletName;
    }

    @Override
    public WalletType getWalletType() {
        return this.walletType;
    }

    @Override
    public WalletStatus getStatus() {
        return this.status;
    }

    @Override
    public void open() throws OpenFailedException {


        // TODO: Raise event signaling this wallet was opened.

        //if (this.password != password) {
        //    throw new OpenFailedException();
        //}
        //else
        {
            this.status = WalletStatus.OPEN;
        }
    }

    /**
     * UsesFileSystem Interface implementation.
     */

    @Override
    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    /**
     * DealWithEvents Interface implementation.
     */

    @Override
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    /**
     * Private methods implementation.
     */

    private void changeToOnlineStatus(){

        this.status = WalletStatus.ONLINE;

        /**
         * Now I fire the ONLINE event.
         */

        PlatformEvent platformEvent = eventManager.getNewEvent(EventType.WALLET_WENT_ONLINE);
        ((WalletWentOnlineEvent) platformEvent).setWalletId(this.walletId);
        eventManager.raiseEvent(platformEvent);

    }


    private void persist() throws CantPersistWalletException {

        PlatformFile file = this.fileSystem.createFile(
                DeviceDirectory.LOCAL_WALLETS.getName(),
                this.walletId.toString(),
                FilePrivacy.PRIVATE,
                FileLifeSpan.PERMANENT
        );

        file.setContent(this.walletName + ";" + this.walletType.getTypeName());

        try {
            file.persistToMedia();
        }
        catch (CantPersistFileException cantPersistFileException) {
            /**
             * This is bad, but lets handle it...
             */
            System.err.println("CantPersistFileException: " + cantPersistFileException.getMessage());
            cantPersistFileException.printStackTrace();
            throw new CantPersistWalletException();
        }
    }

    private void load() throws CantLoadWalletException {

        try {
            PlatformFile file = this.fileSystem.getFile(
                    DeviceDirectory.LOCAL_WALLETS.getName(),
                    this.walletId.toString(),
                    FilePrivacy.PRIVATE,
                    FileLifeSpan.PERMANENT
            );

            file.loadToMemory();
            String[] values = file.getContent().split(";", -1);

            this.walletName = values[0];
            this.walletType = WalletType.getTypeByName(values[1]);

        }
        catch (FileNotFoundException|CantLoadFileException ex)
        {
            /**
             * This is bad, but lets handle it...
             */
            System.err.println("FileNotFoundException or CantLoadFileException: " + ex.getMessage());
            ex.printStackTrace();
            throw new CantLoadWalletException();
        }
    }


}
