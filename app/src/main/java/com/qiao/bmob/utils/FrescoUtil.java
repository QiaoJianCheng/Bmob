package com.qiao.bmob.utils;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.qiao.bmob.global.App;

/**
 * Created by Qiao on 2017/2/28.
 */

public class FrescoUtil {
    public static void display(SimpleDraweeView simpleDraweeView, String url) {
        if (simpleDraweeView == null) return;
        int width = simpleDraweeView.getMeasuredWidth();
        int height = simpleDraweeView.getMeasuredHeight();
        if (width == 0 || height == 0) {
            throw new RuntimeException("fresco display width or height is 0 !");
        }
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url == null ? "" : url))
                .setResizeOptions(new ResizeOptions(width, height))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder().setOldController(simpleDraweeView.getController())
                .setImageRequest(request)
                .setOldController(simpleDraweeView.getController())
                .build();
        simpleDraweeView.setController(controller);
    }

    public static void display(SimpleDraweeView simpleDraweeView, String url, int width, int height) {
        Uri uri = Uri.parse(url == null ? "" : url);
        display(simpleDraweeView, uri, width, height, 0);
    }

    public static void display(SimpleDraweeView simpleDraweeView, String url, int width, int height, int placeHolderId) {
        Uri uri = Uri.parse(url == null ? "" : url);
        display(simpleDraweeView, uri, width, height, placeHolderId);
    }

    public static void displayFromFile(SimpleDraweeView simpleDraweeView, String filePath, int width, int height) {
        display(simpleDraweeView, "file://" + filePath, width, height);
    }

    public static void displayFromResource(SimpleDraweeView simpleDraweeView, int resId, int width, int height) {
        display(simpleDraweeView, "res://" + App.getContext().getPackageName() + "/" + resId, width, height);
    }

    private static void display(SimpleDraweeView simpleDraweeView, Uri uri, int width, int height, int placeHolderId) {
        if (simpleDraweeView == null) return;
        if (width == 0 || height == 0) {
            throw new RuntimeException("fresco display width or height is 0 !");
        }
        if (uri == null) {
            throw new RuntimeException("fresco display null uri !");
        }
        if (placeHolderId != 0) {
            GenericDraweeHierarchy hierarchy = simpleDraweeView.getHierarchy();
            hierarchy.setPlaceholderImage(placeHolderId);
            simpleDraweeView.setHierarchy(hierarchy);
        }
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder().setOldController(simpleDraweeView.getController())
                .setImageRequest(request)
                .setOldController(simpleDraweeView.getController())
                .build();
        simpleDraweeView.setController(controller);
    }
}
