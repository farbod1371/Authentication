package com.example.elessar1992.authentication;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elessar1992.authentication.Check.Check;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
{
    private static int APP_REQUEST_CODE = 6117;

    @BindView(R.id.button_login)
    Button button_login;

    @BindView(R.id.textSkip)
    TextView textSkip;

    @OnClick(R.id.button_login)

    void loginUser()
    {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.CODE); // or .ResponseType.TOKEN
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @OnClick(R.id.textSkip)
    void skipLogin()
    {
        Intent intent = new Intent(this, FirstActivity.class);
        intent.putExtra(Check.IS_LOGIN,false);
        startActivity(intent);
        //finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == APP_REQUEST_CODE)
        {
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null)
            {
                toastMessage = loginResult.getError().getErrorType().getMessage();

            }
            else if (loginResult.wasCancelled())
            {
                toastMessage = "Login Cancelled";
            }
            else
            {
                Intent intent = new Intent(this, FirstActivity.class);
                intent.putExtra(Check.IS_LOGIN,true);
                startActivity(intent);
                finish();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        AccessToken accessToken = AccountKit.getCurrentAccessToken();

        if (accessToken != null)
        {
            Intent intent = new Intent(this, FirstActivity.class);
            intent.putExtra(Check.IS_LOGIN,true);
            startActivity(intent);
            finish();
            //Handle Returning User
        }
        else
        {
            setContentView(R.layout.activity_main);
            ButterKnife.bind(MainActivity.this);
            //Handle new or logged out user
        }

        printKeyHash();
    }

    private void printKeyHash()
    {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "your.package",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
