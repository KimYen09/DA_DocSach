package com.example.doan.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.doan.model.Story;
import com.example.doan.premium.PremiumManager;
import com.example.doan.premium.ExamplePurchaseActivity;

/**
 * Helper class để kiểm tra quyền truy cập premium và hiển thị dialog yêu cầu nâng cấp
 */
public class PremiumAccessHelper {

    /**
     * Interface callback cho việc kiểm tra quyền truy cập
     */
    public interface AccessCheckCallback {
        void onAccessGranted();
        void onAccessDenied();
    }

    /**
     * Kiểm tra xem người dùng có thể truy cập truyện này không
     */
    public static void checkStoryAccess(Context context, Story story, AccessCheckCallback callback) {
        // Nếu truyện không phải premium, cho phép truy cập
        if (!story.isPremium()) {
            callback.onAccessGranted();
            return;
        }

        // Nếu là truyện premium, kiểm tra trạng thái premium của user
        PremiumManager premiumManager = new PremiumManager(context);
        premiumManager.refreshPremiumStatus(new PremiumManager.PremiumCheckCallback() {
            @Override
            public void onResult(boolean isPremium) {
                if (isPremium) {
                    callback.onAccessGranted();
                } else {
                    // Hiển thị dialog yêu cầu nâng cấp premium
                    showPremiumRequiredDialog(context, story.getTitle(), callback);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context, "Lỗi kiểm tra trạng thái premium: " + error, Toast.LENGTH_SHORT).show();
                callback.onAccessDenied();
            }
        });
    }

    /**
     * Hiển thị dialog yêu cầu nâng cấp premium
     */
    private static void showPremiumRequiredDialog(Context context, String storyTitle, AccessCheckCallback callback) {
        new AlertDialog.Builder(context)
                .setTitle("Nâng cấp Premium")
                .setMessage("Truyện \"" + storyTitle + "\" yêu cầu gói Premium.\n\n" +
                           "Nâng cấp ngay để đọc tất cả truyện premium không giới hạn!")
                .setPositiveButton("Nâng cấp ngay", (dialog, which) -> {
                    // Chuyển đến trang mua premium
                    Intent intent = new Intent(context, ExamplePurchaseActivity.class);
                    context.startActivity(intent);
                    dialog.dismiss();
                    callback.onAccessDenied();
                })
                .setNegativeButton("Để sau", (dialog, which) -> {
                    dialog.dismiss();
                    callback.onAccessDenied();
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Hiển thị thông báo yêu cầu premium đơn giản
     */
    public static void showPremiumRequiredToast(Context context) {
        Toast.makeText(context, "Truyện này yêu cầu gói Premium", Toast.LENGTH_LONG).show();
    }

    /**
     * Kiểm tra và hiển thị badge premium cho truyện
     */
    public static boolean shouldShowPremiumBadge(Story story) {
        return story.isPremium();
    }

    /**
     * Method để refresh adapter sau khi mua gói thành công
     */
    public static void notifyPremiumPurchased(Context context) {
        // Có thể thêm broadcast hoặc callback để notify adapter refresh
        Toast.makeText(context, "Gói premium đã được kích hoạt! Vui lòng thử lại.", Toast.LENGTH_LONG).show();
    }
}
