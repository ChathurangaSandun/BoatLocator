package com.bankfinder.chathurangasandun.boatlocator;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Chathuranga Sandun on 6/3/2016.
 */
public class AddFisherManDialog extends AppCompatDialogFragment  {

    String nameString;

    private OnCompleteListener mListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {
        View view = inflater.inflate(R.layout.add_fisherman,container, false);

        final TextView name = (TextView) view.findViewById(R.id.editText);


        Button btcancel = (Button) view.findViewById(R.id.cancel);

        btcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("dialog box", "cancel ");
                dismiss();
            }
        });



        Button btok = (Button) view.findViewById(R.id.ok);

        btok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameString = name.getText().toString();
                mListener.onComplete(nameString);
                dismiss();
            }
        });


        return view;
    }


    //interface
    public static interface OnCompleteListener {
        public abstract void onComplete(String name);

    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }





}


