package com.gbdynamicsgame.one;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.view.Window;
import android.widget.TextView;

import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.ServerManagedPolicy;

public class LicenseCheckActivity extends Activity
{
    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAidH2/GPJ0U8PF9cv3Fuh+/I1AiF7ips0qU200Mc99LtZ1KHFebw6qTWQZRVg/yNm7A1vz/YuV3txLCKalf+Fg4mffCGsdK4NKDqrGfrNzO5r2k6HgZiyKvelxTQBm6uIUiRo7YAstFpsnQd7hm0cg7Dp43tQ13Xqo+1vee+kYdpyX3gIf4ZFMcMFcPoH/B2pjZeN8GfHB1E443X74rRDbFvTgM4MJi5tR1wk3aZ5OdmwKiarNf3/o8ckt38fFjyLXNpV46FA7Gl0E9OWB4a8+pVgO10MsF7y66hGiLuhDSToUDhjGkWIgjrGkILQNs/iZnpNHXVTxuH5cPuOg9n1cQIDAQAB";

    private static final byte[] SALT = new byte[]{
            -26, 65, 50, -128, -103, -57, 74, -64, 53, 88, -95, -45, 79, -117, -36, -113, -11, 34, -64,
            89
    };

    private TextView mStatusText;
    private LicenseCheckerCallback mLicenseCheckerCallback;
    private LicenseChecker mChecker;
    // A handler on the UI thread.
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_license_check);

        mStatusText = (TextView) findViewById(R.id.status_text);
        mHandler = new Handler();

        // Try to use more data here. ANDROID_ID is a single point of attack.
        String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

        // Library calls this when it's done.
        mLicenseCheckerCallback = new MyLicenseCheckerCallback();
        // Construct the LicenseChecker with a policy.
        mChecker = new LicenseChecker(
                this, new ServerManagedPolicy(this,
                new AESObfuscator(SALT, getPackageName(), deviceId)),
                BASE64_PUBLIC_KEY);
        doCheck();
    }

    protected Dialog onCreateDialog(int id)
    {
        final boolean bRetry = id == 1;
        return new AlertDialog.Builder(this)
                .setTitle(R.string.unlicensed_dialog_title)
                .setMessage(bRetry ? R.string.unlicensed_dialog_retry_body : R.string.unlicensed_dialog_body)
                .setPositiveButton(bRetry ? R.string.retry_button : R.string.buy_button, new DialogInterface.OnClickListener()
                {
                    boolean mRetry = bRetry;

                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (mRetry)
                        {
                            doCheck();
                        } else
                        {
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                    "http://market.android.com/details?id=" + getPackageName()));
                            startActivity(marketIntent);
                        }
                    }
                })
                .setNegativeButton(R.string.quit_button, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
                    }
                }).create();
    }

    private void doCheck()
    {
        setProgressBarIndeterminateVisibility(true);
        mStatusText.setText(R.string.checking_license);
        mChecker.checkAccess(mLicenseCheckerCallback);
    }

    private void displayResult(final String result)
    {
        mHandler.post(new Runnable()
        {
            public void run()
            {
                mStatusText.setText(result);
                setProgressBarIndeterminateVisibility(false);
            }
        });
    }

    private void displayDialog(final boolean showRetry)
    {
        mHandler.post(new Runnable()
        {
            public void run()
            {
                setProgressBarIndeterminateVisibility(false);
                showDialog(showRetry ? 1 : 0);
            }
        });
    }

    private class MyLicenseCheckerCallback implements LicenseCheckerCallback
    {
        public void allow(int policyReason)
        {
            if (isFinishing())
            {
                return;
            }

            displayResult(getString(R.string.allow));
            launchApp();
        }

        public void dontAllow(int policyReason)
        {
            if (isFinishing())
            {
                return;
            }
            displayResult(getString(R.string.dont_allow));

            displayDialog(policyReason == Policy.RETRY);
        }

        public void applicationError(int errorCode)
        {
            if (isFinishing())
            {
                return;
            }

            String result = String.format(getString(R.string.application_error), errorCode);
            displayResult(result);
        }
    }

    private void launchApp()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mChecker.onDestroy();
    }

}
