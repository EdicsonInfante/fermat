package com.bitdubai.reference_niche_wallet.bitcoin_wallet.fragments.wallet_final_version;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bitdubai.android_fermat_ccp_wallet_bitcoin.R;
import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatButton;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView;
import com.bitdubai.fermat_android_api.ui.transformation.CircleTransform;
import com.bitdubai.fermat_api.AndroidCoreManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.enums.NetworkStatus;
import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrencyVault;
import com.bitdubai.fermat_api.layer.all_definition.enums.Engine;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.ReferenceWallet;
import com.bitdubai.fermat_api.layer.all_definition.enums.UISource;
import com.bitdubai.fermat_api.layer.all_definition.enums.VaultType;
import com.bitdubai.fermat_api.layer.all_definition.money.CryptoAddress;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Activities;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Wallets;
import com.bitdubai.fermat_api.layer.pip_engine.interfaces.ResourceProviderManager;
import com.bitdubai.fermat_ccp_api.all_definition.util.BitcoinConverter;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.crypto_wallet.exceptions.CantCreateWalletContactException;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.crypto_wallet.exceptions.CantFindWalletContactException;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.crypto_wallet.exceptions.CantGetAllWalletContactsException;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.crypto_wallet.exceptions.CantGetCryptoWalletException;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.crypto_wallet.exceptions.CantListCryptoWalletIntraUserIdentityException;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.crypto_wallet.exceptions.CantRequestCryptoAddressException;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.crypto_wallet.exceptions.CantSendCryptoException;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.crypto_wallet.exceptions.ContactNameAlreadyExistsException;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.crypto_wallet.exceptions.InsufficientFundsException;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.crypto_wallet.exceptions.WalletContactNotFoundException;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.crypto_wallet.interfaces.CryptoWallet;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.crypto_wallet.interfaces.CryptoWalletWalletContact;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedUIExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedWalletExceptionSeverity;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.bar_code_scanner.IntentIntegrator;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.contacts_list_adapter.WalletContact;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.contacts_list_adapter.WalletContactListAdapter;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.popup.ConnectionWithCommunityDialog;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.popup.ErrorConnectingFermatNetworkDialog;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.utils.BitmapWorkerTask;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.utils.WalletUtils;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.session.ReferenceWalletSession;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;
import static com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.utils.WalletUtils.showMessage;

/**
 * Created by Matias Furszyfer on 2015.11.05..
 */
public class SendFormFragment extends AbstractFermatFragment<ReferenceWalletSession,ResourceProviderManager> implements View.OnClickListener{

    private AndroidCoreManager androidCoreManager;
    private NetworkStatus networkStatus;
    /**
     * Plaform reference
     */
    private CryptoWallet cryptoWallet;
    /**
     * UI
     */
    private View rootView;
    private AutoCompleteTextView contactName;
    private EditText editTextAmount;
    private ImageView imageView_contact;
    private FermatButton send_button;
    private TextView txt_notes;
    private BitcoinConverter bitcoinConverter;

    /**
     * Adapters
     */
    private WalletContactListAdapter contactsAdapter;

    /**
     * User selected
     */
    private CryptoWalletWalletContact cryptoWalletWalletContact;

    private WalletContact walletContact;
    private boolean connectionDialogIsShow;
    private boolean onFocus;
    private Spinner spinner;
    private FermatTextView txt_type;
    private ImageView spinnerArrow;


    public static SendFormFragment newInstance() {
        return new SendFormFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bitcoinConverter = new BitcoinConverter();
        setHasOptionsMenu(true);
        try {
            cryptoWallet = appSession.getModuleManager().getCryptoWallet();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } catch (CantGetCryptoWalletException e) {
            appSession.getErrorManager().reportUnexpectedWalletException(Wallets.CWP_WALLET_RUNTIME_WALLET_BITCOIN_WALLET_ALL_BITDUBAI, UnexpectedWalletExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
            showMessage(getActivity(), "CantGetCryptoWalletException- " + e.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {
            rootView = inflater.inflate(R.layout.send_form_base, container, false);
           /* if (getFermatState().getFermatNetworkStatus() != null) {
                switch (getFermatState().getFermatNetworkStatus()) {
                    case CONNECTED:
                        setUpUI();
                        contactName.setText("");
                        setUpActions();
                        setUpUIData();
                        setUpContactAddapter();
                        break;
                    case DISCONNECTED:
                        showErrorConnectionDialog();
                        break;
                }
            } else {*/
            setUpUI();
            contactName.setText("");
            setUpActions();
            setUpUIData();
            setUpContactAddapter();

            return rootView;
        } catch (Exception e) {
            makeText(getActivity(), "Oooops! recovering from system error", Toast.LENGTH_SHORT).show();
            appSession.getErrorManager().reportUnexpectedUIException(UISource.VIEW, UnexpectedUIExceptionSeverity.CRASH, e);
        }

        return null;
    }

    private void showErrorConnectionDialog() {
        final ErrorConnectingFermatNetworkDialog errorConnectingFermatNetworkDialog = new ErrorConnectingFermatNetworkDialog(getActivity(), appSession, null);
        errorConnectingFermatNetworkDialog.setLeftButton("CANCEL", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorConnectingFermatNetworkDialog.dismiss();
            }
        });
        errorConnectingFermatNetworkDialog.setRightButton("CONNECT", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorConnectingFermatNetworkDialog.dismiss();
                changeActivity(Activities.CCP_BITCOIN_WALLET_SETTINGS_ACTIVITY, appSession.getAppPublicKey());
            }
        });
        errorConnectingFermatNetworkDialog.show();
    }

    private void setUpUI() {
        contactName = (AutoCompleteTextView) rootView.findViewById(R.id.contact_name);
        spinnerArrow = (ImageView) rootView.findViewById(R.id.spinner_open);
        txt_notes = (TextView) rootView.findViewById(R.id.notes);
        editTextAmount = (EditText) rootView.findViewById(R.id.amount);
        imageView_contact = (ImageView) rootView.findViewById(R.id.profile_Image);
        send_button = (FermatButton) rootView.findViewById(R.id.send_button);
        txt_type = (FermatTextView) rootView.findViewById(R.id.txt_type);
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();
        list.add("Bits");
        list.add("BTC");
        list.add("Satoshis");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_spinner, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = "";
                String txtType = txt_type.getText().toString();
                String amount = editTextAmount.getText().toString();
                String newAmount = "";
                switch (position) {
                    case 0:
                        text = "[bits]";
                        if (txtType.equals("[btc]")) {
                            newAmount = bitcoinConverter.getBitsFromBTC(amount);
                        } else if (txtType.equals("[satoshis]")) {
                            newAmount = bitcoinConverter.getBits(amount);
                        } else {
                            newAmount = amount;
                        }

                        break;
                    case 1:
                        text = "[btc]";
                        if (txtType.equals("[bits]")) {
                            newAmount = bitcoinConverter.getBitcoinsFromBits(amount);
                        } else if (txtType.equals("[satoshis]")) {
                            newAmount = bitcoinConverter.getBTC(amount);
                        } else {
                            newAmount = amount;
                        }
                        break;
                    case 2:
                        text = "[satoshis]";
                        if (txtType.equals("[bits]")) {
                            newAmount = bitcoinConverter.getSathoshisFromBits(amount);
                        } else if (txtType.equals("[btc]")) {
                            newAmount = bitcoinConverter.getSathoshisFromBTC(amount);
                        } else {
                            newAmount = amount;
                        }
                        break;
                }
                AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.4, 1);
                alphaAnimation.setDuration(300);
                final String finalText = text;
                if (newAmount.equals("0"))
                    newAmount = "";

                final String finalAmount = newAmount;
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        txt_type.setText(finalText);
                        editTextAmount.setText(finalAmount);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                txt_type.startAnimation(alphaAnimation);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.performClick();
            }
        });

    }

    private void setUpActions() {

        /**
         * Listeners
         */
        imageView_contact.setOnClickListener(this);
        send_button.setOnClickListener(this);
        rootView.findViewById(R.id.scan_qr).setOnClickListener(this);

        contactName.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    // in.hideSoftInputFromWindow(autoEditText.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //Commented line is for hide keyboard. Just make above code as comment and test your requirement
                    //It will work for your need. I just putted that line for your understanding only
                    //You can use own requirement here also.

                    if (!connectionDialogIsShow) {
                        ConnectionWithCommunityDialog connectionWithCommunityDialog = new ConnectionWithCommunityDialog(getActivity(), appSession, appSession.getResourceProviderManager());
                        connectionWithCommunityDialog.show();
                        connectionWithCommunityDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                contactName.setText("");
                                connectionDialogIsShow = false;
                            }
                        });
                        connectionDialogIsShow = true;
                    }
                    return true;
                }
                return false;
            }
        });
        contactName.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(contactName, 0);
            }
        }, 50);
        contactName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onFocus = hasFocus;
                if (!onFocus) {
                    if (walletContact == null) {
                        contactName.setText("");
                    }
                }
            }
        });
        /**
         *  Amount observer
         */
        editTextAmount.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                try {
                    //Long amount = Long.parseLong(editTextAmount.getText().toString());
                    //if (amount > 0) {
                    //long actualBalance = cryptoWallet.getBalance(BalanceType.AVAILABLE,referenceWalletSession.getWalletSessionType().getWalletPublicKey());
                    //editTextAmount.setHint("Available amount: " + actualBalance + " bits");
                    //}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        /**
         * Selector
         */
        //send_button.selector(R.drawable.bg_home_accept_normal,R.drawable.bg_home_accept_active, R.drawable.bg_home_accept_normal );
    }

    private void setUpUIData() {
        if(cryptoWalletWalletContact==null) {
            cryptoWalletWalletContact = appSession.getLastContactSelected();
        }
        if (cryptoWalletWalletContact != null) {
            try {
                BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(imageView_contact, getResources(), false);
                bitmapWorkerTask.execute(cryptoWalletWalletContact.getProfilePicture());
            } catch (Exception e) {
                Picasso.with(getActivity()).load(R.drawable.ic_profile_male).transform(new CircleTransform()).into(imageView_contact);
            }
            contactName.setText(cryptoWalletWalletContact.getActorName());

        } else {
            Picasso.with(getActivity()).load(R.drawable.ic_profile_male).transform(new CircleTransform()).into(imageView_contact);
        }

    }

    private void setUpContactAddapter() {
        contactsAdapter = new WalletContactListAdapter(getActivity(), R.layout.wallets_bitcoin_fragment_contacts_list_item, getWalletContactList());

        contactName.setAdapter(contactsAdapter);
        //autocompleteContacts.setTypeface(tf);
        contactName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                walletContact = (WalletContact) arg0.getItemAtPosition(position);

                //add connection like a wallet contact
                try {
                    if (walletContact.isConnection) {
                        cryptoWalletWalletContact = appSession.getModuleManager().getCryptoWallet().convertConnectionToContact(
                                walletContact.name,
                                Actors.INTRA_USER,
                                walletContact.actorPublicKey,
                                walletContact.profileImage,
                                Actors.INTRA_USER,
                                appSession.getIntraUserModuleManager().getPublicKey(),
                                appSession.getAppPublicKey(),
                                CryptoCurrency.BITCOIN,
                                BlockchainNetworkType.TEST);
                    }else {
                        try {
                            cryptoWalletWalletContact = appSession.getModuleManager().getCryptoWallet().findWalletContactById(walletContact.contactId,appSession.getIntraUserModuleManager().getPublicKey());
                        } catch (CantFindWalletContactException e) {
                            e.printStackTrace();
                        } catch (WalletContactNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    walletContact.name = cryptoWalletWalletContact.getActorName();
                    walletContact.actorPublicKey = cryptoWalletWalletContact.getActorPublicKey();
                    if (cryptoWalletWalletContact.getReceivedCryptoAddress().isEmpty()) {
                        appSession.getModuleManager().getCryptoWallet().requestAddressToKnownUser(
                                appSession.getIntraUserModuleManager().getPublicKey(),
                                Actors.INTRA_USER,
                                cryptoWalletWalletContact.getActorPublicKey(),
                                cryptoWalletWalletContact.getActorType(),
                                Platforms.CRYPTO_CURRENCY_PLATFORM,
                                VaultType.CRYPTO_CURRENCY_VAULT,
                                CryptoCurrencyVault.BITCOIN_VAULT.getCode(),
                                appSession.getAppPublicKey(),
                                ReferenceWallet.BASIC_WALLET_BITCOIN_WALLET
                        );
                    } else {
                        walletContact.address = cryptoWalletWalletContact.getReceivedCryptoAddress().get(0).getAddress();
                        if (cryptoWalletWalletContact != null) {
                            walletContact.name = cryptoWalletWalletContact.getActorName();
                            walletContact.actorPublicKey = cryptoWalletWalletContact.getActorPublicKey();
                            if (cryptoWalletWalletContact.getReceivedCryptoAddress().isEmpty()) {
                                appSession.getModuleManager().getCryptoWallet().requestAddressToKnownUser(
                                        appSession.getIntraUserModuleManager().getPublicKey(),
                                        Actors.INTRA_USER,
                                        cryptoWalletWalletContact.getActorPublicKey(),
                                        cryptoWalletWalletContact.getActorType(),
                                        Platforms.CRYPTO_CURRENCY_PLATFORM,
                                        VaultType.CRYPTO_CURRENCY_VAULT,
                                        CryptoCurrencyVault.BITCOIN_VAULT.getCode(),
                                        appSession.getAppPublicKey(),
                                        ReferenceWallet.BASIC_WALLET_BITCOIN_WALLET
                                );
                            } else {
                                walletContact.address = cryptoWalletWalletContact.getReceivedCryptoAddress().get(0).getAddress();
                            }
                            walletContact.contactId = cryptoWalletWalletContact.getContactId();
                            walletContact.profileImage = cryptoWalletWalletContact.getProfilePicture();
                            walletContact.isConnection = cryptoWalletWalletContact.isConnection();
                        }

                        setUpUIData();

                    }
                } catch (CantCreateWalletContactException e) {
                    appSession.getErrorManager().reportUnexpectedWalletException(Wallets.CWP_WALLET_RUNTIME_WALLET_BITCOIN_WALLET_ALL_BITDUBAI, UnexpectedWalletExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
                    showMessage(getActivity(), "CantCreateWalletContactException- " + e.getMessage());

                } catch (ContactNameAlreadyExistsException e) {
                    appSession.getErrorManager().reportUnexpectedWalletException(Wallets.CWP_WALLET_RUNTIME_WALLET_BITCOIN_WALLET_ALL_BITDUBAI, UnexpectedWalletExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
                    showMessage(getActivity(), "ContactNameAlreadyExistsException- " + e.getMessage());

                } catch (CantGetCryptoWalletException e) {
                    e.printStackTrace();
                } catch (CantListCryptoWalletIntraUserIdentityException e) {
                    e.printStackTrace();
                } catch (CantRequestCryptoAddressException e) {
                    e.printStackTrace();
                }
            }
        });


        contactName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
//                    linear_address.setVisibility(activeAddress ? View.VISIBLE : View.GONE);
//                    // if (!editTextAddress.getText().equals("")) linear_address.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.scan_qr) {
            IntentIntegrator integrator = new IntentIntegrator(getActivity(), (EditText) rootView.findViewById(R.id.address));
            integrator.initiateScan();
        } else if (id == R.id.send_button) {
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (getActivity().getCurrentFocus() != null && im.isActive(getActivity().getCurrentFocus())) {
                im.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
            if (cryptoWalletWalletContact != null) {
                sendCrypto();
            } else
                Toast.makeText(getActivity(), "Contact not found, please add it.", Toast.LENGTH_LONG).show();
        } else if (id == R.id.imageView_contact) {
            // if user press the profile image
        } else if (id == R.id.btn_expand_send_form) {
            Object[] objects = new Object[1];
            objects[0] = walletContact;
            changeApp(Engine.BITCOIN_WALLET_CALL_INTRA_USER_COMMUNITY,
                    appSession.getCommunityConnection(), objects);
        }


    }


    //TODO: VER QUE PASA  SI EL CONTACTO NO TIENE UNA WALLET ADDRESS
    private void sendCrypto() {
        try {
            if (cryptoWalletWalletContact.getReceivedCryptoAddress().size() > 0) {
                CryptoAddress validAddress = WalletUtils.validateAddress(cryptoWalletWalletContact.getReceivedCryptoAddress().get(0).getAddress(), cryptoWallet);
                if (validAddress != null) {
                    EditText txtAmount = (EditText) rootView.findViewById(R.id.amount);
                    String amount = txtAmount.getText().toString();

                    BigDecimal money = new BigDecimal("0");

                    if (!amount.equals(""))
                        money = new BigDecimal("0");

                    if(!amount.equals("") && !money.equals(new BigDecimal(0))) {
                        try {
                            String notes = null;
                            if (txt_notes.getText().toString().length() != 0) {
                                notes = txt_notes.getText().toString();
                            }

                            String txtType = txt_type.getText().toString();
                            String newAmount = "";


                            if (txtType.equals("[btc]")) {
                                newAmount = bitcoinConverter.getSathoshisFromBTC(amount);
                            } else if (txtType.equals("[satoshis]")) {
                                newAmount = amount;
                            } else if (txtType.equals("[bits]")) {
                                newAmount = bitcoinConverter.getSathoshisFromBits(amount);
                            }

                            BigDecimal operator = new BigDecimal(newAmount);
                            cryptoWallet.send(
                                    operator.longValueExact(),
                                    validAddress,
                                    notes,
                                    appSession.getAppPublicKey(),
                                    cryptoWallet.getActiveIdentities().get(0).getPublicKey(),
                                    Actors.INTRA_USER,
                                    cryptoWalletWalletContact.getActorPublicKey(),
                                    cryptoWalletWalletContact.getActorType(),
                                    ReferenceWallet.BASIC_WALLET_BITCOIN_WALLET
                            );
                            Toast.makeText(getActivity(), "Sending...", Toast.LENGTH_SHORT).show();
                            onBack(null);
                        } catch (InsufficientFundsException e) {
                            Toast.makeText(getActivity(), "Insufficient funds", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        } catch (CantSendCryptoException e) {
                            appSession.getErrorManager().reportUnexpectedWalletException(Wallets.CWP_WALLET_RUNTIME_WALLET_BITCOIN_WALLET_ALL_BITDUBAI, UnexpectedWalletExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
                            Toast.makeText(getActivity(), "Insufficient funds", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            appSession.getErrorManager().reportUnexpectedUIException(UISource.VIEW, UnexpectedUIExceptionSeverity.UNSTABLE, e);
                            Toast.makeText(getActivity(), "oooopps, we have a problem here", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Invalid Amount", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Contact don't have an Address\n" +
                            "please wait 2 minutes", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "Contact don't have an Address\nplease wait 2 minutes", Toast.LENGTH_LONG).show();
            }


        } catch (Exception e) {
            Toast.makeText(getActivity(), "oooopps, we have a problem here", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    /**
     * Obtain the wallet contacts from the cryptoWallet
     *
     * @return
     */
    private List<WalletContact> getWalletContactList() {
        List<WalletContact> contacts = new ArrayList<>();
        try {
            List<CryptoWalletWalletContact> walletContactRecords = appSession.getModuleManager().getCryptoWallet().listAllActorContactsAndConnections(appSession.getAppPublicKey(), appSession.getIntraUserModuleManager().getPublicKey());
            for (CryptoWalletWalletContact wcr : walletContactRecords) {

                String contactAddress = "";
                if (wcr.getReceivedCryptoAddress().size() > 0)
                    contactAddress = wcr.getReceivedCryptoAddress().get(0).getAddress();
                contacts.add(new WalletContact(wcr.getContactId(), wcr.getActorPublicKey(), wcr.getActorName(), contactAddress, wcr.isConnection(), wcr.getProfilePicture()));
            }
        } catch (CantGetAllWalletContactsException e) {
            appSession.getErrorManager().reportUnexpectedWalletException(Wallets.CWP_WALLET_RUNTIME_WALLET_BITCOIN_WALLET_ALL_BITDUBAI, UnexpectedWalletExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
            showMessage(getActivity(), "CantGetAllWalletContactsException- " + e.getMessage());
        } catch (CantGetCryptoWalletException e) {
            e.printStackTrace();
        } catch (CantListCryptoWalletIntraUserIdentityException e) {
            e.printStackTrace();
        }
        return contacts;
    }


    @Override
    public void onDestroy() {
        contactsAdapter = null;
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }
}
