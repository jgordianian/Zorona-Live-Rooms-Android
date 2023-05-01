package com.zorona.liverooms;

import androidx.databinding.ObservableBoolean;


/**
 * myloder class is a replacement of multiple viewmodels that use only for loading progressbar work
 * you can set myloader  in xml layouts for progressbars
 */
public class MyLoader {
   // public ObservableBoolean isLoadMoreLoading = new ObservableBoolean(true);
    public ObservableBoolean isFristTimeLoading = new ObservableBoolean(true);
    public ObservableBoolean noData = new ObservableBoolean(false);


}
