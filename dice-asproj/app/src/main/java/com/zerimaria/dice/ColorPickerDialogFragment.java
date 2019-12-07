package com.zerimaria.dice;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class ColorPickerDialogFragment extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Hello AlertDialog!");



        return builder.create();
    }
}
