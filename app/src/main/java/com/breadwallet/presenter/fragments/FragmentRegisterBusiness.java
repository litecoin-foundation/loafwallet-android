package com.breadwallet.presenter.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.breadwallet.BuildConfig;
import com.breadwallet.R;
import com.breadwallet.presenter.activities.settings.MapsActivity;
import com.breadwallet.presenter.customviews.BRButton;
import com.breadwallet.presenter.customviews.BRKeyboard;
import com.breadwallet.presenter.customviews.BRLinearLayoutWithCaret;
import com.breadwallet.presenter.customviews.BRText;
import com.breadwallet.presenter.entities.PaymentItem;
import com.breadwallet.presenter.entities.RequestObject;
import com.breadwallet.tools.animation.BRAnimator;
import com.breadwallet.tools.animation.SlideDetector;
import com.breadwallet.tools.animation.SpringAnimator;
import com.breadwallet.tools.manager.BRSharedPrefs;
import com.breadwallet.tools.security.BRSender;
import com.breadwallet.tools.security.BitcoinUrlHandler;
import com.breadwallet.tools.util.BRConstants;
import com.breadwallet.tools.util.BRCurrency;
import com.breadwallet.tools.util.BRExchange;
import com.breadwallet.tools.util.Utils;
import com.breadwallet.wallet.BRWalletManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import static com.platform.HTTPServer.URL_SUPPORT;

//import static com.platform.HTTPServer.URL_SUPPORT;


/**
 * BreadWallet
 * <p>
 * Created by Mihail Gutan <mihail@breadwallet.com> on 6/29/15.
 * Copyright (c) 2016 breadwallet LLC
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

public class FragmentRegisterBusiness extends Fragment {
    private static final String TAG = FragmentRegisterBusiness.class.getName();
    public ScrollView backgroundLayout;
    public LinearLayout signalLayout;
    private BRKeyboard keyboard;
    private EditText addressEdit;
    private Button send;
    private EditText businessEdit;
    private EditText businessProductsEdit;
    private EditText latEdit;
    private EditText lngEdit;
    private TextView businessDistLabel;
    private EditText businessDistEdit;
    private StringBuilder amountBuilder;
    private TextView isoText;
    private EditText amountEdit;
    private TextView balanceText;
    private TextView feeText;
    private long curBalance;
    private String selectedIso;
    private Button isoButton;
    private ImageButton close;
    private ConstraintLayout amountLayout;
    private BRButton regular;
    private BRButton economy;
    private BRLinearLayoutWithCaret feeLayout;
    private boolean feeButtonsShown = false;
    private BRText feeDescription;
    private BRText warningText;
    public static boolean isEconomyFee;
    private boolean amountLabelOn = true;
    private String feeForRegistration;
    private int lengthOfRegistration;

    private static String savedBusiness;
    private static String savedBusinessProducts;
    private static String savedLat;
    private static String savedLng;
    private static String savedBusinessDist;

    private static String savedIso;
    private static String savedAmount;

    private boolean ignoreCleanup;
    private Spinner spinnerEdit;
    private String formattedDate;
    private final String USER_AGENT = "Mozilla/5.0";
    private String urlParameters;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_register_business, container, false);
        backgroundLayout = (ScrollView) rootView.findViewById(R.id.background_layout);
        signalLayout = (LinearLayout) rootView.findViewById(R.id.signal_layout);
        isoText = (TextView) rootView.findViewById(R.id.iso_text);
        addressEdit = (EditText) rootView.findViewById(R.id.address_edit);
        send = (Button) rootView.findViewById(R.id.send_button);
        businessEdit = (EditText) rootView.findViewById(R.id.business_edit);
        businessProductsEdit = (EditText) rootView.findViewById(R.id.businessProducts_edit);
        latEdit = (EditText) rootView.findViewById(R.id.lat_edit);
        lngEdit = (EditText) rootView.findViewById(R.id.lng_edit);
        businessDistLabel = (TextView) rootView.findViewById(R.id.businessDist_label);
        businessDistEdit = (EditText) rootView.findViewById(R.id.businessDist_edit);
        amountEdit = (EditText) rootView.findViewById(R.id.amount_edit);
        balanceText = (TextView) rootView.findViewById(R.id.balance_text);
        feeText = (TextView) rootView.findViewById(R.id.fee_text);
        isoButton = (Button) rootView.findViewById(R.id.iso_button);
        amountLayout = (ConstraintLayout) rootView.findViewById(R.id.amount_layout);
        feeLayout = (BRLinearLayoutWithCaret) rootView.findViewById(R.id.fee_buttons_layout);
        feeDescription = (BRText) rootView.findViewById(R.id.fee_description);
        warningText = (BRText) rootView.findViewById(R.id.warning_text);

//        regular = (BRButton) rootView.findViewById(R.id.left_button);
//        economy = (BRButton) rootView.findViewById(R.id.right_button);
        close = (ImageButton) rootView.findViewById(R.id.close_button);
        selectedIso = "NAH"; //BRSharedPrefs.getPreferredBTC(getContext()) ? "NAH" : BRSharedPrefs.getIso(getContext());

        spinnerEdit = (Spinner) rootView.findViewById(R.id.spinner1);

        amountBuilder = new StringBuilder(0);

        if (BuildConfig.FLAVOR.equals("loaf")) {
            businessDistLabel.setVisibility(View.GONE);
            businessDistEdit.setVisibility(View.GONE);
        }

        setListeners();
        isoText.setText(getString(R.string.Send_amountLabel));
        isoText.setTextSize(18);
        isoText.setTextColor(getContext().getColor(R.color.light_gray));
        isoText.requestLayout();
        signalLayout.setOnTouchListener(new SlideDetector(getContext(), signalLayout));

        signalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        ImageButton faq = (ImageButton) rootView.findViewById(R.id.faq_button);

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                Activity app = getActivity();
                if (app == null) {
                    Log.e(TAG, "onClick: app is null, can't start the webview with url: " + URL_SUPPORT);
                    return;
                }
                BRAnimator.showSupportFragment(app, BRConstants.send);
            }
        });

        setButton(true);

        signalLayout.setLayoutTransition(BRAnimator.getDefaultTransition());

        return rootView;
    }

    private void setListeners() {
/*        amountEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
*/
//                showKeyboard(true);
        if (amountLabelOn) { //only first time
            amountLabelOn = false;
            amountEdit.setHint("0");
            amountEdit.setTextSize(24);
            balanceText.setVisibility(View.VISIBLE);
            feeText.setVisibility(View.VISIBLE);
            isoText.setTextColor(getContext().getColor(R.color.almost_black));
            isoText.setText(BRCurrency.getSymbolByIso(getActivity(), selectedIso));
            isoText.setTextSize(28);
/*            final float scaleX = amountEdit.getScaleX();
            amountEdit.setScaleX(0);

            AutoTransition tr = new AutoTransition();
            tr.setInterpolator(new OvershootInterpolator());
            tr.addListener(new android.support.transition.Transition.TransitionListener() {
                @Override
                public void onTransitionStart(@NonNull android.support.transition.Transition transition) {

                }

                @Override
                public void onTransitionEnd(@NonNull android.support.transition.Transition transition) {
                    amountEdit.requestLayout();
                    amountEdit.animate().setDuration(100).scaleX(scaleX);
                }

                @Override
                public void onTransitionCancel(@NonNull android.support.transition.Transition transition) {

                }

                @Override
                public void onTransitionPause(@NonNull android.support.transition.Transition transition) {

                }

                @Override
                public void onTransitionResume(@NonNull android.support.transition.Transition transition) {

                }
            });

            ConstraintSet set = new ConstraintSet();
            set.clone(amountLayout);
            TransitionManager.beginDelayedTransition(amountLayout, tr);

            int px4 = Utils.getPixelsFromDps(getContext(), 4);
//                    int px8 = Utils.getPixelsFromDps(getContext(), 8);
            set.connect(balanceText.getId(), ConstraintSet.TOP, isoText.getId(), ConstraintSet.BOTTOM, px4);
            set.connect(feeText.getId(), ConstraintSet.TOP, balanceText.getId(), ConstraintSet.BOTTOM, px4);
            set.connect(feeText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, px4);
            set.connect(isoText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, px4);
            set.connect(isoText.getId(), ConstraintSet.BOTTOM, -1, ConstraintSet.TOP, -1);
            set.applyTo(amountLayout);
*/
        }

        spinnerEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position) {
                    case 0:  //one day
                    {
                        feeForRegistration = "20";
                        lengthOfRegistration = 1;
                        break;
                    }
                    case 1:  //one week
                    {
                        feeForRegistration = "30";
                        lengthOfRegistration = 7;  //allow for longest month
                        break;
                    }
                    case 2:  //one month
                    {
                        feeForRegistration = "50";
                        lengthOfRegistration = 31;  //allow for longest month
                        break;
                    }
                    case 3:  //6 months
                    {
                        feeForRegistration = "120";
                        lengthOfRegistration = 183;  //allow for leap year
                        break;
                    }
                    case 4:  //one year
                    {
                        feeForRegistration = "200";
                        lengthOfRegistration = 366;  //alow for leap yeat
                        break;
                    }
                }
                amountBuilder = new StringBuilder(0);
                handleClick(feeForRegistration);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        //needed to fix the overlap bug
        businessEdit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    amountLayout.requestLayout();
                    return true;
                }
                return false;
            }
        });

        businessProductsEdit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    amountLayout.requestLayout();
                    return true;
                }
                return false;
            }
        });

        businessDistEdit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    amountLayout.requestLayout();
                    return true;
                }
                return false;
            }
        });

/*        isoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedIso.equalsIgnoreCase(BRSharedPrefs.getIso(getContext()))) {
                    selectedIso = "NAH";
                } else {
                    selectedIso = BRSharedPrefs.getIso(getContext());
                }
                updateText();
            }
        });
*/
        latEdit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    amountLayout.requestLayout();
                    return true;
                }
                return false;
            }
        });

        lngEdit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    amountLayout.requestLayout();
                    return true;
                }
                return false;
            }
        });

//        commentEdit.addTextChangedListener(new BRTextWatcher());
//        addressEdit.addTextChangedListener(new BRTextWatcher());

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //not allowed now
                if (!BRAnimator.isClickAllowed()) {
                    return;
                }

                boolean allFilled = true;
                String address = addressEdit.getText().toString();
                String distAddress = "";
                distAddress = businessDistEdit.getText().toString();
                String amountStr = amountBuilder.toString();
                String iso = selectedIso;

                if (businessEdit.getText().toString() == null) {
                    Toast.makeText(getActivity(), "Registered Business must have a name !", Toast.LENGTH_LONG).show();
                    return;
                }

                if (businessProductsEdit.getText().toString() == null) {
                    Toast.makeText(getActivity(), "Registered Business must have some products or services !", Toast.LENGTH_LONG).show();
                    return;
                }

                double lat = Double.parseDouble(latEdit.getText().toString());
                double lng = Double.parseDouble(lngEdit.getText().toString());
                if (lat < -90 || lat > 90) {
                    Toast.makeText(getActivity(), "Latitude is out of range (-90 to +90)", Toast.LENGTH_LONG).show();
                    return;
                }

                if (lng < -180 || lng > 180) {
                    Toast.makeText(getActivity(), "Longitude is out of range (-180 to +180)", Toast.LENGTH_LONG).show();
                    return;
                }

                Date cDate = new Date();
                formattedDate = new SimpleDateFormat("dd/MM/yy").format(cDate);

                String comment = "Business=" + businessEdit.getText().toString() +
                        ",Products=" + businessProductsEdit.getText().toString() +
                        "\nLat=" + latEdit.getText().toString() +
                        ",Lng=" + lngEdit.getText().toString() +
                        "\nDate=" + formattedDate +
                        "\nDays Registered=" + lengthOfRegistration;

                //get amount in satoshis from any isos
                BigDecimal bigAmount = new BigDecimal(Utils.isNullOrEmpty(amountStr) ? "0" : amountStr);
                BigDecimal satoshiAmount = BRExchange.getSatoshisFromAmount(getActivity(), iso, bigAmount);

                if (address.isEmpty() || !BRWalletManager.validateAddress(address)) {
                    allFilled = false;
                    SpringAnimator.failShakeAnimation(getActivity(), addressEdit);
                }
                if (amountStr.isEmpty()) {
                    allFilled = false;
                    SpringAnimator.failShakeAnimation(getActivity(), amountEdit);
                }

                if (BuildConfig.FLAVOR.equals("POS"))
                {
                    if (!BRWalletManager.validateAddress(distAddress)) {
                        allFilled = false;
                        SpringAnimator.failShakeAnimation(getActivity(), businessDistEdit);
                    }

                }

                if (satoshiAmount.longValue() > BRWalletManager.getInstance().getBalance(getActivity())) {
                    SpringAnimator.failShakeAnimation(getActivity(), balanceText);
                    SpringAnimator.failShakeAnimation(getActivity(), feeText);
                }

                if (allFilled) {

                    //register business first, running on the main thread as an exception
                    StrictMode.ThreadPolicy policyDefault;
                    policyDefault=StrictMode.getThreadPolicy();
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    Boolean registered=false;

                        //    insert the business name in this format
                        // https://api.strayawallet.com/businessdirectory/register.php?businessname=test3&businessproducts=Developer&latitude=0.00&longitude=0.00&registrationdate=04/03/18&registerlength=365

                        urlParameters = "bn=" + businessEdit.getText().toString() +
                                "&bp=" + businessProductsEdit.getText().toString() +
                                "&lat=" + String.valueOf(lat) +
                                "&lng=" + String.valueOf(lng) +
                                "&ds=" + formattedDate +
                                "&rl=" + lengthOfRegistration +
                                "&pd=" + distAddress;

                        try {
                            sendPost();
                            registered=true;
                            // store away the payment address of the distributor
                            BRSharedPrefs.putDistAddress(getContext(), distAddress);
                        } catch (Exception e) {
                            registered=false;
//                        e.printStackTrace();
                        }

                    StrictMode.setThreadPolicy(policyDefault);

                    //pay second
                    if (registered)
                    BRSender.getInstance().sendTransaction(getContext(), new PaymentItem(new String[]{address}, null, satoshiAmount.longValue(), null, false, comment));
                }

            }
        });

        backgroundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                getActivity().onBackPressed();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity app = getActivity();
                if (app != null)
                    app.getFragmentManager().popBackStack();
            }
        });

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ViewTreeObserver observer = signalLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                observer.removeGlobalOnLayoutListener(this);
                BRAnimator.animateBackgroundDim(backgroundLayout, false);
                BRAnimator.animateSignalSlide(signalLayout, false, new BRAnimator.OnSlideAnimationEnd() {
                    @Override
                    public void onAnimationEnd() {
                        Bundle bundle = getArguments();
                        if (bundle != null && bundle.getString("url") != null) {
                            setUrl(bundle.getString("url"));
                            setLat(bundle.getDouble("lat"));
                            setLng(bundle.getDouble("lng"));
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        BRAnimator.animateBackgroundDim(backgroundLayout, true);
        BRAnimator.animateSignalSlide(signalLayout, true, new BRAnimator.OnSlideAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                if (getActivity() != null) {
                    try {
                        getActivity().getFragmentManager().popBackStack();
                    } catch (Exception ignored) {

                    }
                }
            }
        });
        isEconomyFee = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMetaData();

    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.hideKeyboard(getActivity());
        if (!ignoreCleanup) {
            savedIso = null;
            savedAmount = null;
            savedBusiness = null;
            savedBusinessProducts = null;
            savedLat = null;
            savedLng = null;
            savedBusinessDist=null;
        }
    }

    private void handleClick(String key) {
        if (key == null) {
            Log.e(TAG, "handleClick: key is null! ");
            return;
        }
        String keyChar;
        for (int i = 0; i < key.length(); i++) {
            if (Character.isDigit(key.charAt(i))) {
                handleDigitClick(Integer.parseInt(key.substring(i, i + 1)));
            } else if (key.charAt(i) == '.') {
                handleSeparatorClick();
            }
        }
    }

    private void handleDigitClick(Integer dig) {
        String currAmount = amountBuilder.toString();
        String iso = selectedIso;
        if (new BigDecimal(currAmount.concat(String.valueOf(dig))).doubleValue()
                <= BRExchange.getMaxAmount(getActivity(), iso).doubleValue()) {
            //do not insert 0 if the balance is 0 now
            if (currAmount.equalsIgnoreCase("0")) amountBuilder = new StringBuilder("");
            if ((currAmount.contains(".") && (currAmount.length() - currAmount.indexOf(".") > BRCurrency.getMaxDecimalPlaces(iso))))
                return;
            amountBuilder.append(dig);
            updateText();
        }
    }

    private void handleSeparatorClick() {
        String currAmount = amountBuilder.toString();
        if (currAmount.contains(".") || BRCurrency.getMaxDecimalPlaces(selectedIso) == 0)
            return;
        amountBuilder.append(".");
        updateText();
    }

    private void handleDeleteClick() {
        String currAmount = amountBuilder.toString();
        if (currAmount.length() > 0) {
            amountBuilder.deleteCharAt(currAmount.length() - 1);
            updateText();
        }

    }

    private void updateText() {
        if (getActivity() == null) return;
        String tmpAmount = amountBuilder.toString();
        setAmount();
        String balanceString;
        String iso = selectedIso;
        curBalance = BRWalletManager.getInstance().getBalance(getActivity());
        if (!amountLabelOn)
            isoText.setText(BRCurrency.getSymbolByIso(getActivity(), selectedIso));
        isoButton.setText(String.format("%s(%s)", BRCurrency.getCurrencyName(getActivity(), selectedIso), BRCurrency.getSymbolByIso(getActivity(), selectedIso)));
        //Balance depending on ISO
        long satoshis = (Utils.isNullOrEmpty(tmpAmount) || tmpAmount.equalsIgnoreCase(".")) ? 0 :
                (selectedIso.equalsIgnoreCase("NAH") ? BRExchange.getSatoshisForBitcoin(getActivity(), new BigDecimal(tmpAmount)).longValue() : BRExchange.getSatoshisFromAmount(getActivity(), selectedIso, new BigDecimal(tmpAmount)).longValue());
        BigDecimal balanceForISO = BRExchange.getAmountFromSatoshis(getActivity(), iso, new BigDecimal(curBalance));
        //formattedBalance
        String formattedBalance = BRCurrency.getFormattedCurrencyString(getActivity(), iso, balanceForISO);
        //Balance depending on ISO
        long fee = satoshis == 0 ? 0 : BRWalletManager.getInstance().feeForTransactionAmount(satoshis);
        BigDecimal feeForISO = BRExchange.getAmountFromSatoshis(getActivity(), iso, new BigDecimal(curBalance == 0 ? 0 : fee));
        //formattedBalance
        String aproxFee = BRCurrency.getFormattedCurrencyString(getActivity(), iso, feeForISO);
        if (new BigDecimal((tmpAmount.isEmpty() || tmpAmount.equalsIgnoreCase(".")) ? "0" : tmpAmount).doubleValue() > balanceForISO.doubleValue()) {
            balanceText.setTextColor(getContext().getColor(R.color.warning_color));
            feeText.setTextColor(getContext().getColor(R.color.warning_color));
            amountEdit.setTextColor(getContext().getColor(R.color.warning_color));
            if (!amountLabelOn)
                isoText.setTextColor(getContext().getColor(R.color.warning_color));
        } else {
            balanceText.setTextColor(getContext().getColor(R.color.light_gray));
            feeText.setTextColor(getContext().getColor(R.color.light_gray));
            amountEdit.setTextColor(getContext().getColor(R.color.almost_black));
            if (!amountLabelOn)
                isoText.setTextColor(getContext().getColor(R.color.almost_black));
        }
        balanceString = String.format(getString(R.string.Send_balance), formattedBalance);
        balanceText.setText(String.format("%s", balanceString));
        feeText.setText(String.format(getString(R.string.Send_fee), aproxFee));
        amountLayout.requestLayout();
    }

    public void setUrl(String url) {
        RequestObject obj = BitcoinUrlHandler.getRequestFromString(url);
        if (obj == null) return;
        if (obj.address != null && addressEdit != null) {
            addressEdit.setText(obj.address.trim());
        }
        if (obj.message != null && businessEdit != null) {
            businessEdit.setText(obj.message);
        }
        if (obj.amount != null) {
            String iso = selectedIso;
            BigDecimal satoshiAmount = new BigDecimal(obj.amount).multiply(new BigDecimal(100000000));
            amountBuilder = new StringBuilder(BRExchange.getAmountFromSatoshis(getActivity(), iso, satoshiAmount).toPlainString());
            updateText();
        }
    }

    public void setLat(double lat) {
        latEdit.setText(Double.toString(lat));
    }

    public void setLng(double lng) {
        lngEdit.setText(Double.toString(lng));
    }

    private void setAmount() {
        String tmpAmount = amountBuilder.toString();
        int divider = tmpAmount.length();
        if (tmpAmount.contains(".")) {
            divider = tmpAmount.indexOf(".");
        }
        StringBuilder newAmount = new StringBuilder();
        for (int i = 0; i < tmpAmount.length(); i++) {
            newAmount.append(tmpAmount.charAt(i));
            if (divider > 3 && divider - 1 != i && divider > i && ((divider - i - 1) % 3 == 0)) {
                newAmount.append(",");
            }
        }
        amountEdit.setText(newAmount.toString());
    }

    private void setButton(boolean isRegular) {
        if (isRegular) {
            isEconomyFee = false;
            BRWalletManager.getInstance().setFeePerKb(BRSharedPrefs.getFeePerKb(getContext()), false);
//            regular.setTextColor(getContext().getColor(R.color.white));
//            regular.setBackground(getContext().getDrawable(R.drawable.b_half_left_blue));
//            economy.setTextColor(getContext().getColor(R.color.dark_blue));
//            economy.setBackground(getContext().getDrawable(R.drawable.b_half_right_blue_stroke));
//            feeDescription.setText(String.format(getString(R.string.FeeSelector_estimatedDeliver), getString(R.string.FeeSelector_regularTime)));
//            warningText.getLayoutParams().height = 0;
        } else {
            isEconomyFee = true;
            BRWalletManager.getInstance().setFeePerKb(BRSharedPrefs.getEconomyFeePerKb(getContext()), false);
//            regular.setTextColor(getContext().getColor(R.color.dark_blue));
//            regular.setBackground(getContext().getDrawable(R.drawable.b_half_left_blue_stroke));
//            economy.setTextColor(getContext().getColor(R.color.white));
//            economy.setBackground(getContext().getDrawable(R.drawable.b_half_right_blue));
//            feeDescription.setText(String.format(getString(R.string.FeeSelector_estimatedDeliver), getString(R.string.FeeSelector_economyTime)));
//            warningText.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
//        warningText.requestLayout();
        updateText();
    }

    //only used for paste addresses
    private boolean isInputValid(String input) {
        return input.matches("[a-zA-Z0-9]*");
    }

    // from the link above
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    private void saveMetaData() {
        if (!businessEdit.getText().toString().isEmpty())
            savedBusiness = businessEdit.getText().toString();
        if (!businessProductsEdit.getText().toString().isEmpty())
            savedBusinessProducts = businessProductsEdit.getText().toString();
        if (!latEdit.getText().toString().isEmpty())
            savedLat = latEdit.getText().toString();
        if (!lngEdit.getText().toString().isEmpty())
            savedLng = lngEdit.getText().toString();
        if (!businessDistEdit.getText().toString().isEmpty())
            savedBusinessDist = businessDistEdit.getText().toString();
        if (!amountBuilder.toString().isEmpty())
            savedAmount = amountBuilder.toString();
        savedIso = selectedIso;
        ignoreCleanup = true;
    }

    private void loadMetaData() {
        ignoreCleanup = false;
        if (!Utils.isNullOrEmpty(savedBusiness))
            businessEdit.setText(savedBusiness);
        if (!Utils.isNullOrEmpty(savedBusinessProducts))
            businessEdit.setText(savedBusinessProducts);
        if (!Utils.isNullOrEmpty(savedLat))
            latEdit.setText(savedLat);
        if (!Utils.isNullOrEmpty(savedLng))
            lngEdit.setText(savedLng);
        if (!Utils.isNullOrEmpty(savedBusinessDist))
            businessDistEdit.setText(savedBusinessDist);
        if (!Utils.isNullOrEmpty(savedIso))
            selectedIso = savedIso;
        if (!Utils.isNullOrEmpty(savedAmount)) {
            amountBuilder = new StringBuilder(savedAmount);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    amountEdit.performClick();
                    updateText();
                }
            }, 500);

        }
    }

    // HTTP POST request
    private StringBuffer sendPost() throws Exception {

        urlParameters = urlParameters.replaceAll(" ", "%20");
        String url = "https://api.strayawallet.com/businessdirectory/register.php?"+ urlParameters;
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        urlParameters="";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        return response;
//        System.out.println(response.toString());

    }

}