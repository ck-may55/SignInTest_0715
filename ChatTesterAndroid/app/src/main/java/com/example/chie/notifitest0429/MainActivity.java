package com.example.chie.notifitest0429;

import android.app.Activity;
import android.app.FragmentTransaction;
//

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
//

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends Activity implements SignInPage.OnFragmentInteractionListener,
        StartPage.OnFragmentInteractionListener,
        ChatPage.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";

    //ログイン成功時に取得したUIDを保存
    public static String uid;

    //Authentication機能を使うのに必要
    private FirebaseAuth mAuth;

    //ログイン状態を追うためのリスナー
    private FirebaseAuth.AuthStateListener mAuthListener;



    private TextView textFlag;
    private TextView textToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        boolean fromTray = false;

        // 起動直後にサインインfragmentに遷移
        openSignInPage();

        // 起動がアイコンからか、システムトレイ経由かどうかをチェック
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
                // システムトレイ経由だったらfromTrayをtrueにセットする
                fromTray = true;
            }
            Log.d(TAG, "getExtras() is not null !!");
        }
        // 初期画面をオープン
        //openStartPage(fromTray);
    }

    private void openSignInPage() {
        Log.d(TAG, "openSignInPage");
        setContentView(R.layout.activity_main);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        SignInPage signInPage = new SignInPage();
        transaction.add(R.id.container, signInPage);
        transaction.commit();
    }


    /**
     *　　初期画面のFragmentを開く
     */
    public void openStartPage(boolean fromTray, String userId) {

        Log.d("MainActivity", "openStart 1");
        // 初期画面のFragmentを開くトランザクション
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // 初期画面Fragmentの作成とセット
        StartPage startPage = new StartPage();
        transaction.replace(R.id.container, startPage);
        // トランザクションのコミット
        transaction.commit();

        // 起動がシステムトレイ（通知）経由かどうかを初期画面Fragmentに伝える
        Bundle bundle = new Bundle();
        bundle.putBoolean("FROM_TRAY", fromTray);
        // SignInPageでのユーザIDも伝える
        bundle.putString("USER_ID", userId);
        startPage.setArguments(bundle);

        Log.d("MainActivity", "openStart 2");
    }

    /**
     *　　チャット画面のFragmentを開く
     */
    public void openChat() {

        //setContentView(R.layout.activity_main);

        Log.d("MainActivity", "openChat 1");

        //　チャットページFragmentへの切り替え
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        ChatPage chatPage = new ChatPage();
        transaction.replace(R.id.container, chatPage);
        // 初期画面に戻るためのスタックをセットする
        transaction.addToBackStack(null);
        transaction.commit();

        Log.d("MainActivity", "openChat 2");


        //  画面トップのツールバーの表示を変更
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText("サポート・チャット");
        Button backButton = (Button) findViewById(R.id.left_back_button);
        backButton.setVisibility(View.VISIBLE);
        Button nextButton = (Button) findViewById(R.id.right_next_button);
        nextButton.setVisibility(View.GONE);

        Log.d("MainActivity", "openChat 3");
    }

    @Override
    public void onFragmentInteraction(Uri uri){

    }

    // チャット画面から初期画面に戻る処理
    public void onBack() {

        // スタックを一つ戻して（pop）初期画面に戻る
        getFragmentManager().popBackStack();

        // ツールバーの設定
        Button backButton = (Button) findViewById(R.id.left_back_button);
        backButton.setVisibility(View.GONE);
        Button nextButton = (Button) findViewById(R.id.right_next_button);
        nextButton.setVisibility(View.VISIBLE);
    }
}