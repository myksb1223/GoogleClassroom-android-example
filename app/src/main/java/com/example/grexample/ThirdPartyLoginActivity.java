package com.example.grexample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;

import java.util.ArrayList;

/**
 * Created by seungbeomkim on 2019. 4. 16..
 */

public abstract class ThirdPartyLoginActivity extends AppCompatActivity {
    private static final String TAG = "TPLActivity";
    private static final int REQUEST_CODE_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    protected ClassroomServiceHelper mClassroomServiceHelper;

    //This is for cancel task.
    protected CancellationTokenSource cts;
    protected String account;
    protected ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        makeSignInClient(new Scope(ClassroomScopes.CLASSROOM_COURSES), new Scope(ClassroomScopes.CLASSROOM_COURSEWORK_STUDENTS));
        checkAlreadyLogin();
    }

    public void checkAlreadyLogin() {
        Task<GoogleSignInAccount> task = mGoogleSignInClient.silentSignIn();
        if (task.isSuccessful()) {
//            GoogleSignInAccount signInAccount = task.getResult();
            makeClassroomHelper();
            updateUI(true);
        } else {
            task.addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                @Override
                public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                    updateUI(true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    updateUI(false);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                handleSignInResult(data);
            }
            else {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }
    }

    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        // Login Success!
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        account = googleSignInAccount.getEmail();
                        Log.d(TAG, "acount : " + account);
                        updateUI(true);
                        makeClassroomHelper();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Login Fail!
                        updateUI(false);
                    }
                });
    }

    protected void thirdPartLogin() {
        dialog.setMessage("Login....");
        dialog.show();
        makeSignInClient(new Scope(ClassroomScopes.CLASSROOM_COURSES), new Scope(ClassroomScopes.CLASSROOM_COURSEWORK_STUDENTS));
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    protected void thirdPartLogout() {
        dialog.setMessage("Logout....");
        dialog.show();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                updateUI(false);
                mGoogleSignInClient = null;
                mClassroomServiceHelper = null;
                afterLogout();
            }
        });

    }

    protected abstract void afterLogout();
    protected abstract void updateUI(boolean login);

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    protected void makeSignInClient(Scope scope) {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(scope)
                        .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions);
    }

    protected void makeSignInClient(Scope scope1, Scope scope2) {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(scope1)
                        .requestScopes(scope2)
                        .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions);
    }

    protected void makeClassroomHelper() {
        ArrayList<String> scopes = new ArrayList<>();
        scopes.add(ClassroomScopes.CLASSROOM_COURSES);
        scopes.add(ClassroomScopes.CLASSROOM_COURSEWORK_STUDENTS);
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        this,  scopes);
        credential.setSelectedAccount(googleSignInAccount.getAccount());
        Classroom service = new Classroom.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
                .setApplicationName("GRExample")
                .build();

        mClassroomServiceHelper = new ClassroomServiceHelper(service);
    }

}
