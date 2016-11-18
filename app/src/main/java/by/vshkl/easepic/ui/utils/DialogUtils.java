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

import by.vshkl.easepic.R;
import by.vshkl.easepic.ui.listener.OnAlbumNameEditedListener;

public class DialogUtils {

    public static void showAlbumRenameDialog(final Context context, LayoutInflater inflater,
                                             final OnAlbumNameEditedListener onAlbumNameEditedListener, String albumName) {
        final FrameLayout container = (FrameLayout) inflater.inflate(R.layout.view_text_input, null);
        final TextInputLayout inputLayout = (TextInputLayout) container.findViewById(R.id.text_input_layout);
        final TextInputEditText inputEditText = (TextInputEditText) container.findViewById(R.id.text_input_edit_text);

        inputLayout.setHint(context.getString(R.string.dialog_album_edit_hint));

        inputEditText.setHint(context.getString(R.string.dialog_album_edit_hint));
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
                    inputLayout.setError(context.getString(R.string.dialog_album_edit_error));
                } else {
                    inputLayout.setError(null);
                    inputLayout.setErrorEnabled(false);
                }
            }
        });

        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_album_edit_title))
                .setView(container)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (onAlbumNameEditedListener != null) {
                            onAlbumNameEditedListener.onAlbumNameEdited(inputEditText.getText().toString());
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create()
                .show();
    }
}
