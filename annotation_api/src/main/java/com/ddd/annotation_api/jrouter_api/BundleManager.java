package com.ddd.annotation_api.jrouter_api;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class BundleManager {
    private Bundle mBundle = new Bundle();

    public Bundle getBundle() {
        return mBundle;
    }

    public BundleManager putString(@Nullable String key, @Nullable String strParams) {
        mBundle.putString(key, strParams);
        return this;
    }

    public BundleManager putInt(@Nullable String key, @Nullable int intParams) {
        mBundle.putInt(key, intParams);
        return this;
    }

    public BundleManager putBoolean(@Nullable String key, @Nullable boolean boolParams) {
        mBundle.putBoolean(key, boolParams);
        return this;
    }

    public BundleManager putBundle(Bundle bundle) {
        mBundle = bundle;
        return this;
    }


    public void navigation(Context context) {
        RouterManager.getInstance().navigation(context, this);
    }
}
