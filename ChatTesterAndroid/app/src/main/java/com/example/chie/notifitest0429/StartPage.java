package com.example.chie.notifitest0429;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StartPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartPage extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "flagment_StartPage";

    //
    private String userId;
    private String token;

    //ログイン成功時に取得したUIDを保存
    public static String uid;

    //Authentication機能を使うのに必要
    private FirebaseAuth mAuth;

    //ログイン状態を追うためのリスナー
    private FirebaseAuth.AuthStateListener mAuthListener;


    private TextView textFlag;
    private TextView textToken;
    private boolean fromTray;
    private String tokentest;
    private OnFragmentInteractionListener mListener;

    public StartPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartPage.
     */
    // TODO: Rename and change types and number of parameters
    public static StartPage newInstance(String param1, String param2) {
        StartPage fragment = new StartPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            // システムトレイ経由かどうかを受け取る
            this.fromTray = getArguments().getBoolean("FROM_TRAY");
            // SignInPageからのuserIdを受け取る
            this.userId = getArguments().getString("USER_ID").toUpperCase();
            Log.d(TAG, "USER_ID: " + userId);
        }

        mAuth = FirebaseAuth.getInstance();

        // トークンの取得
        this.token = FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start_page, container, false);
        Log.d(TAG, "layout_startpage");
        //view関連
        TextView userIdView = (TextView) view.findViewById(R.id.user_id);
        userIdView.setText("User ID: " + userId);
        textToken = (TextView) view.findViewById(R.id.token_view);
        if (token != null) {
            textToken.setText(token);
        }


        // チャット画面を開くボタンの設定
        Button button = (Button) view.findViewById(R.id.button_chat_open);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //
                if (mListener != null) {
                    mListener.openChat();
                }
            }
        });

        // ツールバーにチャット画面を開くリンクをセット
        android.widget.Button right_button = (Button) getActivity().findViewById(R.id.right_next_button);
        right_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("StartPage", "onClick next button");

                if (mListener != null) {
                    /**
                     *
                     */
                    mListener.openChat();
                }
            }
        });

        // システムトレイ経由での起動時は、チャット画面を開くボタンを表示する
        if (this.fromTray) {
            Button openChatButton = (Button)view.findViewById(R.id.button_chat_open);
            openChatButton.setVisibility(View.VISIBLE);
        }


        return view;
    }



    @Override
    public void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);
        Log.d(TAG,"onStart()");
        // トークンを受信したMyFirebaseMessagingServiceからのLocalBroadcasterを受け取るための登録
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((mMessageReceiver),
                new IntentFilter("ReceivedMessage")
        );
        submit();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        // LocalBroadcasterのリリース
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    // トークンを受け取ったMyFirebaeMessagingServiceからの通知の受信クロージャのセット
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        // LocalBroadcasterを受信した時に実行される関数
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceived");
            // チャット画面を開くボタンを表示する
            Button openChatButton = (Button)getView().findViewById(R.id.button_chat_open);
            openChatButton.setVisibility(View.VISIBLE);
        }
    };



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void submit(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Log.d(TAG, "getInstance");

        //ListとMapを用いたDBへの書き込み
        final DatabaseReference refUser = database.getReference("usersTokens");

        // 新しいレコードの作成
        final User userToken = new User();
        userToken.createdAt = new Date().getTime() /1000L;
        userToken.endedAt = 0;
        // 新しいトークンのレコードmapを作成（トークンとUIDの組み合わせ）
        // Firebaseコンソールの操作でトークンをキーにした方がコピーペーストし易いために変更しました
        HashMap<String, String> mapToken = new HashMap<String,String>();
        mapToken.put(token, Constants.UID);
        userToken.token = mapToken;

        // /usersTokens/userId以下を受け取るクロージャを設定
        refUser.child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {

                    // データを受信した時に実行される関数
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        /**
                         Log.d(TAG, "onDataChange userId : " + dataSnapshot.child(userId));

                         if (dataSnapshot.child(userId) == null) {
                         Log.d(TAG, "onDataChange user data is null");
                         return;
                         }
                         */

                        // DBにセットするMap
                        Map<String, Object> map = new HashMap<String, Object>();

                        /**
                         Log.d(TAG, "getChildren: " + dataSnapshot.getChildren());
                         Log.d(TAG, "getKey : " + dataSnapshot.getKey());
                         Log.d(TAG, "getRef : " + dataSnapshot.getRef());
                         */
                        // UserId以下に設定されている全レコードチェックする
                        int i = 0;
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            Log.d(TAG, "in for");
                            // endedAtが0だった場合は、現在の時間をセット
                            User user = child.getValue(User.class);
                            if (user.endedAt == 0) {
                                user.endedAt = new Date().getTime() /1000L;
                            }
                            // i（添字）とUserクラスのオブジェクトの組み合わせmapを追加
                            map.put(i + "", user);
                            i++;
                        }
                        // 最後に新しいトークンのレコードを追加する
                        map.put(i + "", userToken);

                        Log.d(TAG, "onDataChange " + userToken.token);

                        // 作成したmapをDBにセットする
                        refUser.child(userId).setValue(map);
                        Log.d(TAG, "onDataChange setValue " + map);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    // Activityとやりとりするためのインターフェース
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void openChat();
    }
}
