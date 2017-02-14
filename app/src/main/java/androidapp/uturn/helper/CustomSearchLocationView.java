package androidapp.uturn.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import java.util.HashMap;

/**
 * Created by Joakim on 2016-02-29
 */
public class CustomSearchLocationView extends AutoCompleteTextView {

    public CustomSearchLocationView(Context context, AttributeSet attributeSet) {

        super(context, attributeSet);
    }

    @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {

        HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
        return hm.get("description");
    }
}
