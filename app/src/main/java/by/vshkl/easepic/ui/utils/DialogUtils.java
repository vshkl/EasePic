package by.vshkl.easepic.ui.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.Toast;

import by.vshkl.easepic.R;

public class DialogUtils {

    public static void showAlbumRenameDialog(final Context context, LayoutInflater inflater, String albumName) {
        final FrameLayout container = (FrameLayout) inflater.inflate(R.layout.view_text_input, null);
        final TextInputLayout inputLayout = (TextInputLayout) container.findViewById(R.id.text_input_layout);
        final TextInputEditText inputEditText = (TextInputEditText) container.findViewById(R.id.text_input_edit_text);

        inputLayout.setHint("Album name");

        inputEditText.setHint("Album name");
        inputEditText.setText(albumName);
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    inputLayout.setErrorEnabled(true);
                    inputLayout.setError("Album name can't be empty");
                } else {
                    inputLayout.setError(null);
                    inputLayout.setErrorEnabled(false);
                }
            }
        });

        new AlertDialog.Builder(context)
                .setTitle("Rename album")
                .setView(container)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "Rename 'em all!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create()
                .show();
    }
}
