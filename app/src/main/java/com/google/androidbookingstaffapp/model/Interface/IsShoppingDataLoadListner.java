package com.google.androidbookingstaffapp.model.Interface;

import com.google.androidbookingstaffapp.model.entities.ShoppingItem;

import java.util.List;

public interface IsShoppingDataLoadListner {

    void ShoppingDataLoadSuccess(List<ShoppingItem> shoppingItemList);
    void ShoppingDataLoadFailed (String message);
}
