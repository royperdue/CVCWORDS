package com.game.one;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;

import com.game.one.model.UserData;
import com.game.one.persistence.DBAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewDataActivity extends Activity
{
    private UserSessionManager session;
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private Dialog confirm;
    private Dialog confirmReset;
    private Dialog logout;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private Button btnLogout, btnResetPw, clear;
    private int count = -1;
    private ArrayList<String> contents;

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);
        session = new UserSessionManager(getApplicationContext());
        TextView lblName = (TextView) findViewById(R.id.lblName);
        TextView lblWarning = (TextView) findViewById(R.id.lbl_warning);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnResetPw = (Button) findViewById(R.id.btnResetPw);
        clear = (Button) findViewById(R.id.clear_all_saved);

        // Check user login
        // If User is not logged in , This will redirect user to LoginActivity.
        if (session.checkLogin())
            finish();

        lblName.setText(Html.fromHtml("CVC Word"));
        lblWarning.setText(Html
                .fromHtml("Clearing saved data can not be undone."));

        this.setupConfirmDialog();
        this.setupConfirmResetDialog();
        this.setuplogoutDialog();

        btnLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                session.logoutUser();
            }
        });

        btnResetPw.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                confirmReset.show();
            }
        });

        clear.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                confirm.show();
            }
        });
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        try
        {
            prepareListData();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        listAdapter = new ExpandableListAdapter(this, listDataHeader,
                listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener()
        {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id)
            {
                return false;
            }
        });

        expListView.setOnGroupExpandListener(new OnGroupExpandListener()
        {
            @Override
            public void onGroupExpand(int groupPosition)
            {
            }
        });

        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener()
        {
            @Override
            public void onGroupCollapse(int groupPosition)
            {
            }
        });

        expListView.setOnChildClickListener(new OnChildClickListener()
        {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id)
            {
                return false;
            }
        });
    }

    private void resetPassword()
    {
        SharedPreferences userDetails = getSharedPreferences("USER_INFO",
                MODE_PRIVATE);
        Editor edit = userDetails.edit();
        edit.clear().commit();
    }

    private void setupConfirmResetDialog()
    {
        confirmReset = new Dialog(this);
        confirmReset.setContentView(R.layout.reset_confirm_dialog);
        confirmReset.setCancelable(true);

        ((Button) confirmReset.findViewById(R.id.confirm_Yes)).setTextSize(Util
                .getTextSize());
        ((Button) confirmReset.findViewById(R.id.confirm_No)).setTextSize(Util
                .getTextSize());
        ((TextView) confirmReset.findViewById(R.id.confirm_text))
                .setTextSize(Util.getTextSize());

        confirmReset.setOnDismissListener(new OnDismissListener()
        {
            public void onDismiss(DialogInterface dialog)
            {
                onResume();
            }
        });
        ((Button) confirmReset.findViewById(R.id.confirm_Yes))
                .setOnClickListener(new OnClickListener()
                {
                    public void onClick(View v)
                    {
                        resetPassword();
                        session.logoutUser();
                        confirmReset.dismiss();
                        finish();
                    }
                });
        ((Button) confirmReset.findViewById(R.id.confirm_No))
                .setOnClickListener(new OnClickListener()
                {
                    public void onClick(View v)
                    {
                        confirmReset.dismiss();
                    }
                });
    }

    private void setupConfirmDialog()
    {
        confirm = new Dialog(this);
        confirm.setContentView(R.layout.confirm_dialog);
        confirm.setCancelable(true);

        ((Button) confirm.findViewById(R.id.confirm_Yes)).setTextSize(Util
                .getTextSize());
        ((Button) confirm.findViewById(R.id.confirm_No)).setTextSize(Util
                .getTextSize());
        ((TextView) confirm.findViewById(R.id.confirm_text)).setTextSize(Util
                .getTextSize());

        confirm.setOnDismissListener(new OnDismissListener()
        {
            public void onDismiss(DialogInterface dialog)
            {
                onResume();
            }
        });
        ((Button) confirm.findViewById(R.id.confirm_Yes))
                .setOnClickListener(new OnClickListener()
                {
                    public void onClick(View v)
                    {
                        try
                        {
                            clearData();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        confirm.dismiss();
                        finish();
                    }
                });
        ((Button) confirm.findViewById(R.id.confirm_No))
                .setOnClickListener(new OnClickListener()
                {
                    public void onClick(View v)
                    {
                        confirm.dismiss();
                    }
                });
    }

    private void setuplogoutDialog()
    {
        logout = new Dialog(this);
        logout.setContentView(R.layout.logout_dialog);
        logout.setCancelable(true);

        ((Button) logout.findViewById(R.id.logout_Yes)).setTextSize(Util
                .getTextSize());
        ((Button) logout.findViewById(R.id.logout_No)).setTextSize(Util
                .getTextSize());
        ((TextView) logout.findViewById(R.id.logout_text)).setTextSize(Util
                .getTextSize());

        logout.setOnDismissListener(new OnDismissListener()
        {
            public void onDismiss(DialogInterface dialog)
            {
                onResume();
            }
        });
        ((Button) logout.findViewById(R.id.logout_Yes))
                .setOnClickListener(new OnClickListener()
                {
                    public void onClick(View v)
                    {
                        session.logoutUser();
                        logout.dismiss();
                        finish();
                    }
                });
        ((Button) logout.findViewById(R.id.logout_No))
                .setOnClickListener(new OnClickListener()
                {
                    public void onClick(View v)
                    {
                        logout.dismiss();
                        Intent i = new Intent(getApplicationContext(),
                                MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                        finish();
                    }
                });
    }

    private void clearData() throws IOException
    {
        SharedPreferences userDetails = getSharedPreferences("USER_INFO",
                MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();

        if(userDetails.getBoolean("DATA_EXISTS", false) == true)
        {
            List<UserData> data = DBAdapter.getAllUserData();

            for (UserData dt : data)
            {
                DBAdapter.deleteUserData(dt);
            }
            edit.putBoolean("DATA_EXISTS", false);
        }

        Intent intent = getIntent();
        finish();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    /*
     * Preparing the list data
     */
    @SuppressWarnings("unchecked")
    private void prepareListData() throws IOException
    {
        SharedPreferences userDetails = getSharedPreferences("USER_INFO",
                MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        List<String> dayOne = new ArrayList<String>();
        List<String> dayTwo = new ArrayList<String>();
        List<String> dayThree = new ArrayList<String>();
        List<String> dayFour = new ArrayList<String>();
        List<String> dayFive = new ArrayList<String>();
        List<String> daySix = new ArrayList<String>();
        List<String> daySeven = new ArrayList<String>();
        List<String> dayEight = new ArrayList<String>();
        List<String> dayNine = new ArrayList<String>();
        List<String> dayTen = new ArrayList<String>();

        List<String> dayEleven = new ArrayList<String>();
        List<String> dayTwelve = new ArrayList<String>();
        List<String> dayThirteen = new ArrayList<String>();
        List<String> dayFourteen = new ArrayList<String>();
        List<String> dayFifteen = new ArrayList<String>();
        List<String> daySixteen = new ArrayList<String>();
        List<String> daySeventeen = new ArrayList<String>();
        List<String> dayEightteen = new ArrayList<String>();
        List<String> dayNineteen = new ArrayList<String>();
        List<String> dayTwenty = new ArrayList<String>();
        contents = new ArrayList<String>();
        String date = "";
        String s = "";

        if(userDetails.getBoolean("DATA_EXISTS", false) == true)
        {
            List<UserData> data = DBAdapter.getAllUserData();

            for (UserData dt : data)
            {
                String userData = dt.getData();
                contents.add(userData);
            }
        }
        for (int i = 0; i < contents.size(); i++)
        {
            s = contents.get(i);
            String[] strs = s.split("&");


            if (date.equals(strs[0]) == false)
            {
                count++;
                listDataHeader.add(strs[0]);
            }

            if (strs[1].contains(" 1 ") == true
                    && strs[1].contains("11") == false && i != 0)
            {
                if (count == 0)
                    dayOne.add("Start of a new game!");
                if (count == 1)
                    dayTwo.add("Start of a new game!");
                if (count == 2)
                    dayThree.add("Start of a new game!");
                if (count == 3)
                    dayFour.add("Start of a new game!");
                if (count == 4)
                    dayFive.add("Start of a new game!");
                if (count == 5)
                    daySix.add("Start of a new game!");
                if (count == 6)
                    daySeven.add("Start of a new game!");
                if (count == 7)
                    dayEight.add("Start of a new game!");
                if (count == 8)
                    dayNine.add("Start of a new game!");
                if (count == 9)
                    dayTen.add("Start of a new game!");

                if (count == 10)
                    dayEleven.add("Start of a new game!");
                if (count == 11)
                    dayTwelve.add("Start of a new game!");
                if (count == 12)
                    dayThirteen.add("Start of a new game!");
                if (count == 13)
                    dayFourteen.add("Start of a new game!");
                if (count == 14)
                    dayFifteen.add("Start of a new game!");
                if (count == 15)
                    daySixteen.add("Start of a new game!");
                if (count == 16)
                    daySeventeen.add("Start of a new game!");
                if (count == 17)
                    dayEightteen.add("Start of a new game!");
                if (count == 18)
                    dayNineteen.add("Start of a new game!");
                if (count == 19)
                    dayTwenty.add("Start of a new game!");
            }

            if (count == 0)
                listDataChild.put(listDataHeader.get(0), dayOne);
            if (count == 1)
                listDataChild.put(listDataHeader.get(1), dayTwo);
            if (count == 2)
                listDataChild.put(listDataHeader.get(2), dayThree);
            if (count == 3)
                listDataChild.put(listDataHeader.get(3), dayFour);
            if (count == 4)
                listDataChild.put(listDataHeader.get(4), dayFive);
            if (count == 5)
                listDataChild.put(listDataHeader.get(5), daySix);
            if (count == 6)
                listDataChild.put(listDataHeader.get(6), daySeven);
            if (count == 7)
                listDataChild.put(listDataHeader.get(7), dayEight);
            if (count == 8)
                listDataChild.put(listDataHeader.get(8), dayNine);
            if (count == 9)
                listDataChild.put(listDataHeader.get(9), dayTen);

            if (count == 10)
                listDataChild.put(listDataHeader.get(10), dayEleven);
            if (count == 11)
                listDataChild.put(listDataHeader.get(11), dayTwelve);
            if (count == 12)
                listDataChild.put(listDataHeader.get(12), dayThirteen);
            if (count == 13)
                listDataChild.put(listDataHeader.get(13), dayFourteen);
            if (count == 14)
                listDataChild.put(listDataHeader.get(14), dayFifteen);
            if (count == 15)
                listDataChild.put(listDataHeader.get(15), daySixteen);
            if (count == 16)
                listDataChild.put(listDataHeader.get(16), daySeventeen);
            if (count == 17)
                listDataChild.put(listDataHeader.get(17), dayEightteen);
            if (count == 18)
                listDataChild.put(listDataHeader.get(18), dayNineteen);
            if (count == 19)
                listDataChild.put(listDataHeader.get(19), dayTwenty);

            date = strs[0];

            if (count == 0)
                dayOne.add(strs[1]);
            if (count == 1)
                dayTwo.add(strs[1]);
            if (count == 2)
                dayThree.add(strs[1]);
            if (count == 3)
                dayFour.add(strs[1]);
            if (count == 4)
                dayFive.add(strs[1]);
            if (count == 5)
                daySix.add(strs[1]);
            if (count == 6)
                daySeven.add(strs[1]);
            if (count == 7)
                dayEight.add(strs[1]);
            if (count == 8)
                dayNine.add(strs[1]);
            if (count == 9)
                dayTen.add(strs[1]);

            if (count == 10)
                dayEleven.add(strs[1]);
            if (count == 11)
                dayTwelve.add(strs[1]);
            if (count == 12)
                dayThirteen.add(strs[1]);
            if (count == 13)
                dayFourteen.add(strs[1]);
            if (count == 14)
                dayFifteen.add(strs[1]);
            if (count == 15)
                daySixteen.add(strs[1]);
            if (count == 16)
                daySeventeen.add(strs[1]);
            if (count == 17)
                dayEightteen.add(strs[1]);
            if (count == 18)
                dayNineteen.add(strs[1]);
            if (count == 19)
                dayTwenty.add(strs[1]);
        }

        count = -1;
    }

    @Override
    public void onBackPressed()
    {
        this.onPause();

        logout.show();
    }

}
