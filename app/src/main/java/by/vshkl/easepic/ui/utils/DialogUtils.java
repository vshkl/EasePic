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
import android.widget.GridLayout;
import android.widget.TextView;

import by.vshkl.easepic.R;
import by.vshkl.easepic.mvp.model.PictureInfo;
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
                .setTitle(context.getString(R.string.dialog_picture_details_title))
                .setView(container)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (onAlbumNameEditedListener != null) {
                            onAlbumNameEditedListener.onAlbumNameEdited(inputEditText.getText().toString());
                        }
                    }
                })
                .create()
                .show();
    }

    public static void showPictureDetailsDialog(Context context, PictureInfo pictureInfo) {
        final GridLayout container = (GridLayout) FrameLayout.inflate(context, R.layout.dialog_picture_details, null);

        final TextView tvPath = (TextView) container.findViewById(R.id.tv_path);
        final TextView tvMimetype = (TextView) container.findViewById(R.id.tv_mimetype);
        final TextView tvSize = (TextView) container.findViewById(R.id.tv_size);
        final TextView tvResolution = (TextView) container.findViewById(R.id.tv_resolution);
        final TextView tvDate = (TextView) container.findViewById(R.id.tv_date);

        tvPath.setText(pictureInfo.getPath());
        tvMimetype.setText(pictureInfo.getMimeType());
        tvSize.setText(FileSizeUtils.humanReadableByteCount(Long.valueOf(pictureInfo.getSize()), false));
        tvResolution.setText(pictureInfo.getWidth() + "x" + pictureInfo.getHeight());
        tvDate.setText(DateUtils.humanReadableDate(Long.valueOf(pictureInfo.getDate())));
        System.out.println(pictureInfo.getDate());

        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_picture_details_title))
                .setView(container)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }
}
