package com.breadwallet.presenter.activities.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.breadwallet.BreadApp;
import com.breadwallet.BuildConfig;
import com.breadwallet.tools.animation.BRAnimator;
import com.breadwallet.tools.security.BRKeyStore;
import com.breadwallet.tools.security.BitcoinUrlHandler;
import com.breadwallet.tools.security.PostAuth;
import com.breadwallet.tools.threads.BRExecutor;
import com.breadwallet.tools.util.BRConstants;
import com.breadwallet.wallet.BRWalletManager;
import com.platform.tools.BRBitId;

/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan on <mihail@breadwallet.com> 5/23/17.
 * Copyright (c) 2017 breadwallet LLC
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
public class BRActivity extends Activity {
    private final String TAG = this.getClass().getName();

    static {
        System.loadLibrary(BRConstants.NATIVE_LIB_NAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BreadApp.activityCounter.decrementAndGet();
        BreadApp.onStop(this);
        BreadApp.backgroundedTime = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BreadApp.activityCounter.incrementAndGet();
        BreadApp.setBreadContext(this);
        //lock wallet if 3 minutes passed
//        if (BuildConfig.FLAVOR.equals("loaf")) {
            if (BreadApp.backgroundedTime != 0 && (System.currentTimeMillis() - BreadApp.backgroundedTime >= 180 * 1000)) {
                if (!BRKeyStore.getPinCode(this).isEmpty()) {
                    BreadApp.backgroundedTime = System.currentTimeMillis();
                    BRAnimator.startBreadActivity(this, true);
                }
            }
//        } else
//        {
//            BreadApp.backgroundedTime = System.currentTimeMillis();
//            BRAnimator.startBreadActivity(this, true);
//        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        // 123 is the qrCode result
        switch (requestCode) {

            case BRConstants.PAY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if(BuildConfig.FLAVOR.equals("loaf"))
                        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                            @Override
                            public void run() {
                                PostAuth.getInstance().onPublishTxAuth(BRActivity.this, true);
                            }
                        });
                    else
                        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                            @Override
                            public void run() {
                                PostAuth.getInstance().onPublishTxAuth(BRActivity.this, false);
                            }
                        });
                }
                break;
            case BRConstants.REQUEST_PHRASE_BITID:
                if (resultCode == RESULT_OK) {
                    if(BuildConfig.FLAVOR.equals("loaf"))
                        PostAuth.getInstance().onBitIDAuth(BRActivity.this, true);
                    else
                        PostAuth.getInstance().onBitIDAuth(BRActivity.this, false);

                }
                break;

            case BRConstants.PAYMENT_PROTOCOL_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if(BuildConfig.FLAVOR.equals("loaf"))
                        PostAuth.getInstance().onPaymentProtocolRequest(this, true);
                    else
                        PostAuth.getInstance().onPaymentProtocolRequest(this, false);
                }
                break;

            case BRConstants.CANARY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if(BuildConfig.FLAVOR.equals("loaf"))
                        PostAuth.getInstance().onCanaryCheck(this, true);
                    else
                        PostAuth.getInstance().onCanaryCheck(this, false);
                } else {
                    finish();
                }
                break;

            case BRConstants.SHOW_PHRASE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if(BuildConfig.FLAVOR.equals("loaf"))
                        PostAuth.getInstance().onPhraseCheckAuth(this, true);
                    else
                        PostAuth.getInstance().onPhraseCheckAuth(this, false);
                }
                break;
            case BRConstants.PROVE_PHRASE_REQUEST:
                if (resultCode == RESULT_OK) {
                    if(BuildConfig.FLAVOR.equals("loaf"))
                        PostAuth.getInstance().onPhraseProveAuth(this, true);
                    else
                        PostAuth.getInstance().onPhraseProveAuth(this, false);
                }
                break;
            case BRConstants.PUT_PHRASE_RECOVERY_WALLET_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if(BuildConfig.FLAVOR.equals("loaf"))
                        PostAuth.getInstance().onRecoverWalletAuth(this, true);
                    else
                        PostAuth.getInstance().onRecoverWalletAuth(this, false);
                } else {
                    finish();
                }
                break;

            case BRConstants.SCANNER_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String result = data.getStringExtra("result");
                            if (BitcoinUrlHandler.isBitcoinUrl(result))
                                BitcoinUrlHandler.processRequest(BRActivity.this, result);
                            else if (BRBitId.isBitId(result))
                                BRBitId.signBitID(BRActivity.this, result, null);
                            else
                                Log.e(TAG, "onActivityResult: not litecoin address NOR bitID");
                        }
                    }, 500);

                }
                break;
            case BRConstants.SCANNER_BCH_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String result = data.getStringExtra("result");
                            PostAuth.getInstance().onSendBch(BRActivity.this, true, result);
                        }
                    }, 500);

                }
                break;

            case BRConstants.PUT_PHRASE_NEW_WALLET_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if(BuildConfig.FLAVOR.equals("loaf"))
                        PostAuth.getInstance().onCreateWalletAuth(this, true);
                    else
                        PostAuth.getInstance().onCreateWalletAuth(this, false);
                } else {
                    Log.e(TAG, "WARNING: resultCode != RESULT_OK");
                    BRWalletManager m = BRWalletManager.getInstance();
                    m.wipeWalletButKeystore(this);
                    finish();
                }
                break;
        }
    }
}
