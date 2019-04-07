package yolo.tbv.vancomycin;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import java.util.List;

public final class UIHelper {
    // helper method to smoothly scroll the view to a desired UI element
    // view is the desired UI element to scroll to
    // scrollView is the containing ScrollView that will be scrolled
    public static void focusOnView(final View view, final ScrollView scrollView){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                int[] coordinates = new int[2];
                int halfScreenHeight = Resources.getSystem().getDisplayMetrics().heightPixels / 2;

                view.getLocationOnScreen(coordinates);
                scrollView.smoothScrollTo(
                        0,
                        coordinates[1] + scrollView.getScrollY() - halfScreenHeight
                );
            }
        });
    }

    // takes in a list of EditText elements and validates them
    // valid means they have an input, and that input can be parsed to a double
    public static boolean validateEditTextList(List<EditText> textList, ScrollView scrollView) {
        boolean inputIsValid = true; // this variable is the one that gets returned
        boolean doNotScroll = false; // this flag is used to ensure only one scroll event is issued

        // text box validation
        for (EditText editTextItem : textList) {
            String inputString = editTextItem.getText().toString();

            if (inputString.length() == 0) {
                editTextItem.setHintTextColor(Color.RED);
                inputIsValid = false;
            } else {
                try {
                    Double.parseDouble(inputString);
                } catch (NumberFormatException e) {
                    editTextItem.setText("");
                    editTextItem.setHintTextColor(Color.RED);
                    inputIsValid = false;
                }
            }

            if (!inputIsValid && !doNotScroll) {
                UIHelper.focusOnView(editTextItem, scrollView);
                doNotScroll = true; // flip doNotScroll so this block isn't entered in subsequent loops
            }
        }

        return inputIsValid;
    }
}