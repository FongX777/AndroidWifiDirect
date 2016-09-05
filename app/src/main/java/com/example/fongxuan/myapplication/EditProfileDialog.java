package com.example.fongxuan.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by fongxuan on 9/3/16.
 */
public class EditProfileDialog extends DialogFragment {
    private static User user;
    private static String userString;

    public static EditProfileDialog newInstance(String userName){
        EditProfileDialog fragment = new EditProfileDialog();
        Bundle bundle = new Bundle();
        bundle.putString("Name", userName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_efit_profile, null))
                // Add action buttons
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditProfileDialog.this.getDialog().cancel();
                    }
                });


        return builder.create();
    }

}
