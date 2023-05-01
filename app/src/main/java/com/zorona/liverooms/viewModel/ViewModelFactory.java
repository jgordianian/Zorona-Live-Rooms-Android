package com.zorona.liverooms.viewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private ViewModel model;

    public ViewModelFactory(ViewModel model) {

        this.model = model;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(model.getClass())) {
            return (T) model;
        }
        throw new IllegalArgumentException("unexpected model class " + modelClass);
    }

    public <T extends ViewModel> ViewModelProvider.Factory createFor() {
        return new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                if (modelClass.isAssignableFrom(model.getClass())) {
                    return (T) model;
                }
                throw new IllegalArgumentException("unexpected model class " + modelClass);
            }

        };

    }
}
