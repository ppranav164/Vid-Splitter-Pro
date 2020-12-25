package com.card.splitter_pro;

import android.content.ClipData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ItemViewModel extends ViewModel {

    private final MutableLiveData<ClipData.Item> selectedItem = new MutableLiveData<>();

    public void selectItem(ClipData.Item item)
    {
        selectedItem.setValue(item);
    }

    public LiveData<ClipData.Item> getSelectedItem() {
        return selectedItem;
    }

}
