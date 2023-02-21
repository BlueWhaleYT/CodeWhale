package com.bluewhaleyt.codewhale.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class AssetsFileLoader {
    public static String getAssetsFileContent(Context ctx, String file) {
        try {
            InputStream is = ctx.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
