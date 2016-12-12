package com.example.snowiot.snowiotsimple;

/**
 * Created by Felipe on 12/11/2016.
 */

public static class ChatHolder extends RecyclerView.ViewHolder {
    private final TextView mNameField;
    private final TextView mTextField;

    public ChatHolder(View itemView) {
        super(itemView);
        mNameField = (TextView) itemView.findViewById(android.R.id.text1);
        mTextField = (TextView) itemView.findViewById(android.R.id.text2);
    }

    public void setName(String name) {
        mNameField.setText(name);
    }

    public void setText(String text) {
        mTextField.setText(text);
    }
}