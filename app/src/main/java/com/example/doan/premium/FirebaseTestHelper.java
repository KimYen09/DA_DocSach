package com.example.doan.premium;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Class để test kết nối Firebase và debug lỗi
 */
public class FirebaseTestHelper {

    private static final String TAG = "FirebaseTest";

    public static void testFirebaseConnection(Context context) {
        Log.d(TAG, "=== FIREBASE CONNECTION TEST START ===");
        Toast.makeText(context, "🔍 Bắt đầu test Firebase...", Toast.LENGTH_SHORT).show();

        // Test Firebase Auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            Log.d(TAG, "✓ User logged in: " + user.getUid());
            Log.d(TAG, "✓ User email: " + user.getEmail());
            Toast.makeText(context, "✓ User đã đăng nhập: " + user.getEmail(), Toast.LENGTH_SHORT).show();

            // Test Firebase Database permissions
            testDatabasePermissions(context, user.getUid());
        } else {
            Log.e(TAG, "✗ User NOT logged in");
            Toast.makeText(context, "❌ Cần đăng nhập để test Firebase", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "=== FIREBASE CONNECTION TEST END ===");
    }

    private static void testDatabasePermissions(Context context, String userId) {
        Log.d(TAG, "Testing database permissions...");
        Toast.makeText(context, "🔍 Testing database permissions...", Toast.LENGTH_SHORT).show();

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://docsachdb-default-rtdb.asia-southeast1.firebasedatabase.app");
            Log.d(TAG, "✓ Database instance created");

            // Test 1: Write permission to test path
            Log.d(TAG, "Testing WRITE permission to test node...");
            DatabaseReference testRef = database.getReference("test").child("connection_test");

            testRef.setValue("test_" + System.currentTimeMillis())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✓ WRITE to test node: SUCCESS");
                    Toast.makeText(context, "✓ WRITE test: SUCCESS", Toast.LENGTH_SHORT).show();

                    // Test 2: Read permission to giaoDich node (not PurchaseHistory)
                    testReadPermission(context, database, userId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "✗ WRITE permission: FAILED");
                    Log.e(TAG, "Write error: " + e.getMessage());
                    Toast.makeText(context, "❌ WRITE test FAILED: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

        } catch (Exception e) {
            Log.e(TAG, "✗ Firebase Database error: " + e.getMessage());
            Toast.makeText(context, "❌ Database error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private static void testReadPermission(Context context, FirebaseDatabase database, String userId) {
        Log.d(TAG, "Testing READ permission to giaoDich node...");
        Toast.makeText(context, "🔍 Testing READ permission...", Toast.LENGTH_SHORT).show();

        // Sử dụng node giaoDich thay vì PurchaseHistory
        DatabaseReference purchaseRef = database.getReference("giaoDich").child(userId);

        purchaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "✓ READ permission: SUCCESS");
                Log.d(TAG, "Data exists: " + dataSnapshot.exists());
                Log.d(TAG, "Children count: " + dataSnapshot.getChildrenCount());
                Toast.makeText(context, "✅ Firebase hoạt động OK!\nRead/Write thành công!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "✗ READ permission: FAILED");
                Log.e(TAG, "Read error: " + error.getMessage());
                Log.e(TAG, "Error code: " + error.getCode());
                Log.e(TAG, "Error details: " + error.getDetails());

                if (error.getCode() == DatabaseError.PERMISSION_DENIED) {
                    Toast.makeText(context,
                        "❌ Permission denied!\nCần cập nhật Firebase Rules",
                        Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "❌ READ failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Test lưu một PurchaseRecord mẫu
     */
    public static void testSavePurchaseRecord(Context context) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(context, "Cần đăng nhập để test", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Testing PurchaseManager...");
        PurchaseManager manager = new PurchaseManager(context);
        manager.savePurchaseRecord("Test Package", "0 VNĐ", new PurchaseManager.PurchaseCallback() {
            @Override
            public void onSuccess(PurchaseRecord purchaseRecord) {
                Log.d(TAG, "✓ Test purchase saved: " + purchaseRecord.getPurchaseId());
                Toast.makeText(context, "Test thành công: " + purchaseRecord.getPurchaseId(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "✗ Test purchase failed: " + error);
                Toast.makeText(context, "Test thất bại: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Debug thông tin chi tiết về Firebase config
     */
    public static void debugFirebaseConfig(Context context) {
        Log.d(TAG, "=== FIREBASE CONFIG DEBUG ===");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            Log.d(TAG, "User ID: " + user.getUid());
            Log.d(TAG, "User Email: " + user.getEmail());
            Log.d(TAG, "User Display Name: " + user.getDisplayName());
            Log.d(TAG, "User Phone: " + user.getPhoneNumber());
            Log.d(TAG, "User Provider: " + user.getProviderId());
            Log.d(TAG, "Is Anonymous: " + user.isAnonymous());
        }

        // Database URL check
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://docsachdb-default-rtdb.asia-southeast1.firebasedatabase.app");
            Log.d(TAG, "Database URL: " + "https://docsachdb-default-rtdb.asia-southeast1.firebasedatabase.app");
            Log.d(TAG, "Database reference created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Database connection error: " + e.getMessage());
        }

        Log.d(TAG, "=== END CONFIG DEBUG ===");
    }

    /**
     * Kiểm tra Google Play Services có sẵn không
     */
    public static void checkGooglePlayServices(Context context) {
        Log.d(TAG, "=== GOOGLE PLAY SERVICES CHECK ===");

        try {
            int resultCode = com.google.android.gms.common.GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(context);

            if (resultCode == com.google.android.gms.common.ConnectionResult.SUCCESS) {
                Log.d(TAG, "✓ Google Play Services: AVAILABLE");
                Toast.makeText(context, "Google Play Services hoạt động bình thường", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "✗ Google Play Services: NOT AVAILABLE");
                Log.e(TAG, "Error code: " + resultCode);

                String errorMessage = getPlayServicesErrorMessage(resultCode);
                Toast.makeText(context, "Lỗi Google Play Services: " + errorMessage, Toast.LENGTH_LONG).show();

                // Thử sửa tự động
                com.google.android.gms.common.GoogleApiAvailability.getInstance()
                    .makeGooglePlayServicesAvailable((android.app.Activity) context);
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Google Play Services check failed: " + e.getMessage());
            Toast.makeText(context, "Không thể kiểm tra Google Play Services: " + e.getMessage(),
                Toast.LENGTH_LONG).show();
        }

        Log.d(TAG, "=== END PLAY SERVICES CHECK ===");
    }

    private static String getPlayServicesErrorMessage(int errorCode) {
        switch (errorCode) {
            case com.google.android.gms.common.ConnectionResult.SERVICE_MISSING:
                return "Google Play Services bị thiếu";
            case com.google.android.gms.common.ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                return "Cần cập nhật Google Play Services";
            case com.google.android.gms.common.ConnectionResult.SERVICE_DISABLED:
                return "Google Play Services bị vô hiệu hóa";
            case com.google.android.gms.common.ConnectionResult.SERVICE_INVALID:
                return "Google Play Services không hợp lệ";
            default:
                return "Lỗi không xác định: " + errorCode;
        }
    }

    /**
     * Debug chi tiết về package và certificate
     */
    public static void debugPackageInfo(Context context) {
        Log.d(TAG, "=== PACKAGE & CERTIFICATE DEBUG ===");

        try {
            String packageName = context.getPackageName();
            Log.d(TAG, "Package Name: " + packageName);

            // Lấy thông tin certificate
            android.content.pm.PackageManager pm = context.getPackageManager();
            android.content.pm.PackageInfo packageInfo = pm.getPackageInfo(packageName,
                android.content.pm.PackageManager.GET_SIGNATURES);

            for (android.content.pm.Signature signature : packageInfo.signatures) {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA1");
                md.update(signature.toByteArray());

                String sha1 = bytesToHex(md.digest());
                Log.d(TAG, "SHA-1 Certificate: " + sha1);

                // Hiển thị cho user
                Toast.makeText(context,
                    "Package: " + packageName + "\nSHA-1: " + sha1,
                    Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "✗ Package debug failed: " + e.getMessage());
            Toast.makeText(context, "Lỗi debug package: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        Log.d(TAG, "=== END PACKAGE DEBUG ===");
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X:", b));
        }
        return result.length() > 0 ? result.substring(0, result.length() - 1) : "";
    }

    /**
     * Test Firebase Auth cụ thể để tìm lỗi SecurityException
     */
    public static void testFirebaseAuth(Context context) {
        Log.d(TAG, "=== FIREBASE AUTH SPECIFIC TEST ===");

        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            Log.d(TAG, "✓ FirebaseAuth instance created");

            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                Log.d(TAG, "✓ Current user exists: " + currentUser.getUid());
                Log.d(TAG, "✓ User email: " + currentUser.getEmail());
                Log.d(TAG, "✓ User provider: " + currentUser.getProviderId());

                // Test token refresh để xem có lỗi SecurityException không
                currentUser.getIdToken(true)
                    .addOnSuccessListener(result -> {
                        Log.d(TAG, "✓ Token refresh: SUCCESS");
                        Toast.makeText(context, "Firebase Auth hoạt động bình thường!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "✗ Token refresh: FAILED");
                        Log.e(TAG, "Token error: " + e.getMessage());
                        if (e.getMessage().contains("SecurityException")) {
                            Toast.makeText(context, "❌ Tìm thấy SecurityException trong Firebase Auth!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Lỗi token: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

            } else {
                Log.w(TAG, "⚠️ No current user logged in");
                Toast.makeText(context, "Chưa đăng nhập - không thể test Auth", Toast.LENGTH_SHORT).show();
            }

        } catch (SecurityException e) {
            Log.e(TAG, "✗ SecurityException in Firebase Auth: " + e.getMessage());
            Toast.makeText(context, "❌ SecurityException: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "✗ Firebase Auth test failed: " + e.getMessage());
            Toast.makeText(context, "Lỗi Firebase Auth: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        Log.d(TAG, "=== END AUTH TEST ===");
    }
}
