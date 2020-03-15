package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class BottomModelSheet extends BottomSheetDialogFragment {

    private BottomSheetListener mlistner;
    private Button btnCamera,btnGallery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_bottom_modal_sheet,container,false);
        btnCamera = (Button) v.findViewById(R.id.btn_camera);
        btnGallery = (Button) v.findViewById(R.id.btn_gallery);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlistner.onBottomClicked(1);
                dismiss();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlistner.onBottomClicked(2);
                dismiss();
            }
        });
        return v;
    }

    public interface BottomSheetListener{
        void onBottomClicked(int btnId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
        mlistner = (BottomSheetListener) context;
    }
    catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement BottomSheetListner");
    }
    }
}
