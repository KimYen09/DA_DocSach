//
//
//package com.example.doan.fragmenthome;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.viewpager2.widget.ViewPager2;
//
//import com.example.doan.R;
//import com.example.doan.account.EditProfile;
//import com.example.doan.adapter.BannerAdapter;
//import com.example.doan.adapter.StoryAdapter;
//import com.example.doan.model.Story;
//import com.example.doan.premium.Premium;
//import com.example.doan.ui.ChapterListActivity;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class HomeFragment extends Fragment {
//    private ImageView imvPremium;
//    private RecyclerView recyclerView, recyclerViewPre;
//    private StoryAdapter storyAdapter, storyAdapterPre;
//    private List<Story> storyList, storyListPre;
//    private DatabaseReference databaseReference;
//    private ViewPager2 bannerViewPager;
//    private BannerAdapter bannerAdapter;
//    private List<Integer> bannerImages;
//    private final Handler handler = new Handler();
//
//    public static final String PREMIUM_PREFS_NAME = "MyAppPremiumPrefs";
//    public static final String KEY_IS_USER_PREMIUM = "isUserPremium";
//
//
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//
//        bannerViewPager = view.findViewById(R.id.bannerViewPager);
//        recyclerView = view.findViewById(R.id.recyclerView);
//        recyclerViewPre = view.findViewById(R.id.dspre);
//        imvPremium = view.findViewById(R.id.imvPremium);
//
//        // Setup Banner
//        setupBanner();
//
//        // Setup RecyclerView
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        recyclerViewPre.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//
//        storyList = new ArrayList<>();
//        storyListPre = new ArrayList<>();
//        databaseReference = FirebaseDatabase.getInstance().getReference("stories");
//
//        // Tắt cache Firebase
//        databaseReference.keepSynced(false);
//        databaseReference.getDatabase().purgeOutstandingWrites();
//
//        // Load dữ liệu từ Firebase
//        loadStories();
//        // Set OnClickListener for imvPremium
//        imvPremium.setOnClickListener(v -> startActivity(new Intent(getActivity(), Premium.class)));
//        return view;
//    }
//
//    private void setupBanner() {
//        bannerImages = new ArrayList<>();
//        bannerImages.add(R.drawable.banner1);
//        bannerImages.add(R.drawable.banner2);
//        bannerImages.add(R.drawable.banner3);
//        bannerAdapter = new BannerAdapter(bannerImages);
//        bannerViewPager.setAdapter(bannerAdapter);
//        bannerViewPager.setCurrentItem(bannerImages.size() * 500, false);
//    }
//
//    private void loadStories() {
//        Log.d("FirebaseData", "Bắt đầu tải dữ liệu từ Firebase...");
//
////        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//        databaseReference.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                storyList.clear();
//                storyListPre.clear();
//
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    Story story = data.getValue(Story.class);
//                    if (story != null) {
//                        if (story.getId() == null || story.getId().isEmpty()) {
//                            story.setId(data.getKey());
//                        }
//                        Log.d("FirebaseData", "Story ID: " + story.getId() + ", Title: " + story.getTitle() + ", Type: " + story.getType());
//
//                        if (story.getType() != null && story.getType().trim().equalsIgnoreCase("Premium")) {
//                            storyListPre.add(story);
//                        } else {
//                            storyList.add(story);
//                        }
//
//                    }
//                }
//
//
//                Log.d("FirebaseData", "Tổng số truyện thường: " + storyList.size());
//                Log.d("FirebaseData", "Tổng số truyện Premium: " + storyListPre.size());
//
//                // Cập nhật giao diện
//                updateRecyclerViews();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Lỗi tải truyện!", Toast.LENGTH_SHORT).show();
//                Log.e("FirebaseError", "Lỗi Firebase: " + error.getMessage());
//            }
//        });
//    }
//
//    private void updateRecyclerViews() {
//        recyclerView.setAdapter(null);
//        storyAdapter = new StoryAdapter(getContext(), storyList, null);
//        recyclerView.setAdapter(storyAdapter);
//        storyAdapter.notifyDataSetChanged();
//
//        recyclerViewPre.setAdapter(null);
//        storyAdapterPre = new StoryAdapter(getContext(), storyListPre, null);
//        recyclerViewPre.setAdapter(storyAdapterPre);
//        storyAdapterPre.notifyDataSetChanged();
//        recyclerViewPre.invalidate();
//
//        Log.d("RecyclerViewUpdate", "Cập nhật RecyclerView - Truyện thường: " + storyList.size());
//        Log.d("RecyclerViewUpdate", "Cập nhật RecyclerView - Truyện Premium: " + storyListPre.size());
//
//        setupStoryClickListener(storyAdapter);
//        setupStoryClickListener(storyAdapterPre);
//    }
//
//    private void setupStoryClickListener(StoryAdapter adapter) {
//        adapter.setOnItemClickListener(story -> {
//            if (story.getId() == null || story.getId().isEmpty()) {
//                Toast.makeText(getContext(), "Lỗi: ID truyện không hợp lệ!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Toast.makeText(getContext(), "Bạn đã chọn: " + story.getTitle(), Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(getContext(), ChapterListActivity.class);
//            intent.putExtra("storyId", story.getId());
//            startActivity(intent);
//        });
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (bannerImages != null && !bannerImages.isEmpty()) {
//            handler.postDelayed(runnable, 3000);
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        handler.removeCallbacks(runnable);
//    }
//
//    private final Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            if (bannerViewPager != null && bannerImages != null && !bannerImages.isEmpty()) {
//                int currentItem = bannerViewPager.getCurrentItem();
//                bannerViewPager.setCurrentItem(currentItem + 1, true);
//                handler.postDelayed(this, 3000);
//            }
//        }
//    };
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        handler.removeCallbacks(runnable);
//    }
//}
//
//package com.example.doan.fragmenthome;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.viewpager2.widget.ViewPager2;
//
//import com.example.doan.R;
//import com.example.doan.account.EditProfile;
//import com.example.doan.adapter.BannerAdapter;
//import com.example.doan.adapter.StoryAdapter;
//import com.example.doan.model.Story;
//import com.example.doan.premium.Premium;
//import com.example.doan.ui.ChapterListActivity;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.HashMap; // Thêm import cho HashMap
//import java.util.List;
//import java.util.Map; // Thêm import cho Map
//
//public class HomeFragment extends Fragment {
//    private ImageView imvPremium;
//    private RecyclerView recyclerView, recyclerViewPre;
//    private StoryAdapter storyAdapter, storyAdapterPre;
//    private List<Story> storyList, storyListPre;
//    private DatabaseReference databaseReference;
//    private ViewPager2 bannerViewPager;
//    private BannerAdapter bannerAdapter;
//    private List<Integer> bannerImages;
//    private final Handler handler = new Handler();
//
//    public static final String PREMIUM_PREFS_NAME = "MyAppPremiumPrefs";
//    public static final String KEY_IS_USER_PREMIUM = "isUserPremium";
//
//    private static final String TAG = "HomeFragment"; // Thêm TAG cho Log
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//
//        bannerViewPager = view.findViewById(R.id.bannerViewPager);
//        recyclerView = view.findViewById(R.id.recyclerView);
//        recyclerViewPre = view.findViewById(R.id.dspre);
//        imvPremium = view.findViewById(R.id.imvPremium);
//
//        // Setup Banner
//        setupBanner();
//
//        // Setup RecyclerView
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        recyclerViewPre.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//
//        storyList = new ArrayList<>();
//        storyListPre = new ArrayList<>();
//        databaseReference = FirebaseDatabase.getInstance().getReference("stories");
//
//        // Tắt cache Firebase (có thể gây ra hành vi không mong muốn nếu cần dữ liệu offline)
//        // databaseReference.keepSynced(false);
//        // databaseReference.getDatabase().purgeOutstandingWrites();
//
//        // Load dữ liệu từ Firebase
//        loadStories();
//        // Set OnClickListener for imvPremium
//        imvPremium.setOnClickListener(v -> startActivity(new Intent(getActivity(), Premium.class)));
//        return view;
//    }
//
//    private void setupBanner() {
//        bannerImages = new ArrayList<>();
//        bannerImages.add(R.drawable.banner1);
//        bannerImages.add(R.drawable.banner2);
//        bannerImages.add(R.drawable.banner3);
//        bannerAdapter = new BannerAdapter(bannerImages);
//        bannerViewPager.setAdapter(bannerAdapter);
//        bannerViewPager.setCurrentItem(bannerImages.size() * 500, false);
//    }
//
//    private void loadStories() {
//        Log.d(TAG, "Bắt đầu tải dữ liệu từ Firebase...");
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                storyList.clear();
//                storyListPre.clear();
//
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    Story story = data.getValue(Story.class);
//                    if (story != null) {
//                        if (story.getId() == null || story.getId().isEmpty()) {
//                            story.setId(data.getKey()); // Đảm bảo ID được set
//                        }
//                        Log.d(TAG, "Story ID: " + story.getId() + ", Title: " + story.getTitle() + ", Type: " + story.getType());
//
//                        if (story.getType() != null && story.getType().trim().equalsIgnoreCase("Premium")) {
//                            storyListPre.add(story);
//                        } else {
//                            storyList.add(story);
//                        }
//                    }
//                }
//
//                Log.d(TAG, "Tổng số truyện thường: " + storyList.size());
//                Log.d(TAG, "Tổng số truyện Premium: " + storyListPre.size());
//
//                // Cập nhật giao diện
//                updateRecyclerViews();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Lỗi tải truyện!", Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "Lỗi Firebase: " + error.getMessage());
//            }
//        });
//    }
//
//    private void updateRecyclerViews() {
//        // Khởi tạo lại adapter mỗi khi dữ liệu thay đổi
//        // Đảm bảo truyền actionListener hoặc null nếu không cần các chức năng edit/delete/onStoryClick
//        // Trong HomeFragment, bạn chỉ cần click vào truyện để xem chi tiết, nên truyền null là hợp lý
//        storyAdapter = new StoryAdapter(getContext(), storyList, null);
//        recyclerView.setAdapter(storyAdapter);
//        storyAdapter.notifyDataSetChanged();
//
//        storyAdapterPre = new StoryAdapter(getContext(), storyListPre, null);
//        recyclerViewPre.setAdapter(storyAdapterPre);
//        storyAdapterPre.notifyDataSetChanged();
//        // recyclerViewPre.invalidate(); // Dòng này thường không cần thiết
//
//        Log.d(TAG, "Cập nhật RecyclerView - Truyện thường: " + storyList.size());
//        Log.d(TAG, "Cập nhật RecyclerView - Truyện Premium: " + storyListPre.size());
//
//        // Setup click listener cho cả hai adapter
//        setupStoryClickListener(storyAdapter);
//        setupStoryClickListener(storyAdapterPre);
//    }
//
//    private void setupStoryClickListener(StoryAdapter adapter) {
//        adapter.setOnItemClickListener(story -> {
//            if (story.getId() == null || story.getId().isEmpty()) {
//                Toast.makeText(getContext(), "Lỗi: ID truyện không hợp lệ!", Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "ID truyện null hoặc rỗng khi click: " + story.getTitle());
//                return;
//            }
//            Toast.makeText(getContext(), "Bạn đã chọn: " + story.getTitle(), Toast.LENGTH_SHORT).show();
//
//            // --- LOGIC KHÓA TRUYỆN PREMIUM ---
//            boolean isUserPremium = sharedPreferences.getBoolean(KEY_IS_USER_PREMIUM, false); // Đọc trạng thái Premium của người dùng
//
//            if (story.getType() != null && story.getType().trim().equalsIgnoreCase("Premium") && !isUserPremium) {
//                // Nếu truyện là Premium VÀ người dùng KHÔNG phải Premium
//                Toast.makeText(getContext(), "Truyện này dành cho thành viên Premium. Vui lòng nâng cấp!", Toast.LENGTH_LONG).show();
//                // Chuyển hướng đến Activity Premium
//                Intent intent = new Intent(getContext(), Premium.class);
//                startActivity(intent);
//                Log.d(TAG, "Người dùng không Premium, chuyển hướng đến trang Premium.");
//            } else {
//                // Nếu truyện không phải Premium HOẶC người dùng là Premium
//                // Tăng lượt đọc và chuyển sang ChapterListActivity
//                incrementViewCount(story.getId());
//
//                Intent intent = new Intent(getContext(), ChapterListActivity.class);
//                intent.putExtra("storyId", story.getId());
//                startActivity(intent);
//                Log.d(TAG, "Truyện không Premium hoặc người dùng là Premium, mở ChapterListActivity.");
//            }
//        });
//    }
//
//    /**
//     * Tăng số lượt đọc (viewCount) của một truyện trong Firebase.
//     * @param storyId ID của truyện cần tăng lượt đọc.
//     */
//    private void incrementViewCount(String storyId) {
//        if (storyId == null || storyId.isEmpty()) {
//            Log.e(TAG, "Không thể tăng lượt đọc: storyId là null hoặc rỗng.");
//            return;
//        }
//
//        DatabaseReference storyRef = databaseReference.child(storyId);
//
//        storyRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    // Lấy giá trị viewCount hiện tại
//                    Long currentViewCount = snapshot.child("viewCount").getValue(Long.class);
//                    if (currentViewCount == null) {
//                        currentViewCount = 0L; // Khởi tạo nếu chưa có
//                    }
//
//                    // Tăng lượt đọc
//                    long newViewCount = currentViewCount + 1;
//
//                    // Cập nhật lại Firebase
//                    Map<String, Object> updates = new HashMap<>();
//                    updates.put("viewCount", newViewCount);
//
//                    storyRef.updateChildren(updates)
//                            .addOnSuccessListener(aVoid -> {
//                                Log.d(TAG, "Đã tăng lượt đọc cho truyện " + storyId + " lên: " + newViewCount);
//                            })
//                            .addOnFailureListener(e -> {
//                                Log.e(TAG, "Lỗi khi tăng lượt đọc cho truyện " + storyId + ": " + e.getMessage());
//                            });
//                } else {
//                    Log.w(TAG, "Không tìm thấy truyện với ID: " + storyId + " để tăng lượt đọc.");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e(TAG, "Lỗi Firebase khi đọc dữ liệu để tăng lượt đọc: " + error.getMessage());
//            }
//        });
//    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        // Cập nhật trạng thái Premium của người dùng mỗi khi Fragment được hiển thị lại
//        // (ví dụ: sau khi người dùng quay lại từ màn hình Premium)
//        // Bạn có thể cần thêm logic để đọc trạng thái Premium từ Firebase/Auth ở đây
//        // nếu trạng thái này không chỉ được lưu trong SharedPreferences.
//        // Ví dụ: checkUserPremiumStatusFromFirebase();
//        if (bannerImages != null && !bannerImages.isEmpty()) {
//            handler.postDelayed(runnable, 3000);
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        handler.removeCallbacks(runnable);
//    }
//
//    private final Runnable runnable = new Runnable() {
//        @Override
//        public void run() { // Đã sửa 'void run()' thành 'run()'
//            if (bannerViewPager != null && bannerImages != null && !bannerImages.isEmpty()) {
//                int currentItem = bannerViewPager.getCurrentItem();
//                bannerViewPager.setCurrentItem(currentItem + 1, true);
//                handler.postDelayed(this, 3000);
//            }
//        }
//    };
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        handler.removeCallbacks(runnable);
//    }
//


package com.example.doan.fragmenthome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.SharedPreferences; // Thêm import này
import static android.content.Context.MODE_PRIVATE; // Thêm import này

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.doan.R;
import com.example.doan.account.EditProfile;
import com.example.doan.adapter.BannerAdapter;
import com.example.doan.adapter.StoryAdapter;
import com.example.doan.model.Story;
import com.example.doan.premium.Premium;
import com.example.doan.premium.PremiumManager; // Thêm import này
import com.example.doan.ui.ChapterListActivity;
import com.example.doan.ui.RegisterAuthorActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private ImageView imvPremium, imvRegisterAuthor;
    private RecyclerView recyclerView, recyclerViewPre;
    private StoryAdapter storyAdapter, storyAdapterPre;
    private List<Story> storyList, storyListPre;
    private DatabaseReference databaseReference;
    private ViewPager2 bannerViewPager;
    private BannerAdapter bannerAdapter;
    private List<Integer> bannerImages;
    private final Handler handler = new Handler();

    public static final String PREMIUM_PREFS_NAME = "MyAppPremiumPrefs";
    public static final String KEY_IS_USER_PREMIUM = "isUserPremium";

    private static final String TAG = "HomeFragment";
    private SharedPreferences sharedPreferences; // Khai báo SharedPreferences

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerViewPre = view.findViewById(R.id.dspre);
        imvPremium = view.findViewById(R.id.imvPremium);
        imvRegisterAuthor = view.findViewById(R.id.imvRegisterAuthor);

        // Khởi tạo SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREMIUM_PREFS_NAME, MODE_PRIVATE);

        // Setup Banner
        setupBanner();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPre.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        storyList = new ArrayList<>();
        storyListPre = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("stories");

        imvRegisterAuthor.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), RegisterAuthorActivity.class);
            startActivity(intent);

        });


        // Load dữ liệu từ Firebase
        loadStories();
        // Set OnClickListener for imvPremium
        imvPremium.setOnClickListener(v -> startActivity(new Intent(getActivity(), Premium.class)));
        return view;

    }

    private void setupBanner() {
        bannerImages = new ArrayList<>();
        bannerImages.add(R.drawable.banner1);
        bannerImages.add(R.drawable.banner2);
        bannerImages.add(R.drawable.banner3);
        bannerAdapter = new BannerAdapter(bannerImages);
        bannerViewPager.setAdapter(bannerAdapter);
        bannerViewPager.setCurrentItem(bannerImages.size() * 500, false);
    }

    private void loadStories() {
        Log.d(TAG, "Bắt đầu tải dữ liệu từ Firebase...");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storyList.clear();
                storyListPre.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Story story = data.getValue(Story.class);
                    if (story != null) {
                        if (story.getId() == null || story.getId().isEmpty()) {
                            story.setId(data.getKey()); // Đảm bảo ID được set
                        }
                        Log.d(TAG, "Story ID: " + story.getId() + ", Title: " + story.getTitle() + ", Type: " + story.getType());

                        if (story.getType() != null && story.getType().trim().equalsIgnoreCase("Premium")) {
                            storyListPre.add(story);
                        } else {
                            storyList.add(story);
                        }
                    }
                }

                Log.d(TAG, "Tổng số truyện thường: " + storyList.size());
                Log.d(TAG, "Tổng số truyện Premium: " + storyListPre.size());

                // Cập nhật giao diện
                updateRecyclerViews();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải truyện!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Lỗi Firebase: " + error.getMessage());
            }
        });
    }

    private void updateRecyclerViews() {
        storyAdapter = new StoryAdapter(getContext(), storyList, null);
        recyclerView.setAdapter(storyAdapter);
        storyAdapter.notifyDataSetChanged();

        storyAdapterPre = new StoryAdapter(getContext(), storyListPre, null);
        recyclerViewPre.setAdapter(storyAdapterPre);
        storyAdapterPre.notifyDataSetChanged();

        Log.d(TAG, "Cập nhật RecyclerView - Truyện thường: " + storyList.size());
        Log.d(TAG, "Cập nhật RecyclerView - Truyện Premium: " + storyListPre.size());

        // Setup click listener cho cả hai adapter
        setupStoryClickListener(storyAdapter);
        setupStoryClickListener(storyAdapterPre);
    }

    private void setupStoryClickListener(StoryAdapter adapter) {
        adapter.setOnItemClickListener(story -> {
            if (story.getId() == null || story.getId().isEmpty()) {
                Toast.makeText(getContext(), "Lỗi: ID truyện không hợp lệ!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "ID truyện null hoặc rỗng khi click: " + story.getTitle());
                return;
            }
            Toast.makeText(getContext(), "Bạn đã chọn: " + story.getTitle(), Toast.LENGTH_SHORT).show();

            // --- LOGIC KHÓA TRUYỆN PREMIUM (CẬP NHẬT) ---
            // Kiểm tra xem truyện có phải premium không
            if (story.getType() != null && story.getType().trim().equalsIgnoreCase("Premium")) {
                // Nếu là truyện premium, kiểm tra trạng thái premium từ Firebase
                PremiumManager premiumManager = new PremiumManager(getContext());
                premiumManager.refreshPremiumStatus(new PremiumManager.PremiumCheckCallback() {
                    @Override
                    public void onResult(boolean isPremium) {
                        if (isPremium) {
                            // Người dùng có premium, cho phép truy cập
                            Log.d(TAG, "Người dùng có Premium, mở truyện premium: " + story.getTitle());
                            incrementViewCount(story.getId());

                            Intent intent = new Intent(getContext(), ChapterListActivity.class);
                            intent.putExtra("storyId", story.getId());
                            startActivity(intent);
                        } else {
                            // Người dùng chưa có premium, yêu cầu nâng cấp
                            Log.d(TAG, "Người dùng không Premium, chuyển hướng đến trang Premium.");
                            Toast.makeText(getContext(), "Truyện này dành cho thành viên Premium. Vui lòng nâng cấp!", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getContext(), Premium.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Lỗi kiểm tra trạng thái premium: " + error);
                        Toast.makeText(getContext(), "Lỗi kiểm tra trạng thái premium. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Nếu truyện không phải Premium, cho phép truy cập ngay
                Log.d(TAG, "Truyện miễn phí, mở ngay: " + story.getTitle());
                incrementViewCount(story.getId());

                Intent intent = new Intent(getContext(), ChapterListActivity.class);
                intent.putExtra("storyId", story.getId());
                startActivity(intent);
            }
        });
    }

    /**
     * Tăng số lượt đọc (viewCount) của một truyện trong Firebase.
     * @param storyId ID của truyện cần tăng lượt đọc.
     */
    private void incrementViewCount(String storyId) {
        if (storyId == null || storyId.isEmpty()) {
            Log.e(TAG, "Không thể tăng lượt đọc: storyId là null hoặc rỗng.");
            return;
        }

        DatabaseReference storyRef = databaseReference.child(storyId);

        storyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy giá trị viewCount hiện tại
                    Long currentViewCount = snapshot.child("viewCount").getValue(Long.class);
                    if (currentViewCount == null) {
                        currentViewCount = 0L; // Khởi tạo nếu chưa có
                    }

                    // Tăng lượt đọc
                    long newViewCount = currentViewCount + 1;

                    // Cập nhật lại Firebase
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("viewCount", newViewCount);

                    storyRef.updateChildren(updates)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Đã tăng lượt đọc cho truyện " + storyId + " lên: " + newViewCount);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Lỗi khi tăng lượt đọc cho truyện " + storyId + ": " + e.getMessage());
                            });
                } else {
                    Log.w(TAG, "Không tìm thấy truyện với ID: " + storyId + " để tăng lượt đọc.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi Firebase khi đọc dữ liệu để tăng lượt đọc: " + error.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật trạng thái Premium của người dùng mỗi khi Fragment được hiển thị lại
        // (ví dụ: sau khi người dùng quay lại từ màn hình Premium)
        // Bạn có thể cần thêm logic để đọc trạng thái Premium từ Firebase/Auth ở đây
        // nếu trạng thái này không chỉ được lưu trong SharedPreferences.
        // Ví dụ: checkUserPremiumStatusFromFirebase();
        if (bannerImages != null && !bannerImages.isEmpty()) {
            handler.postDelayed(runnable, 3000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() { // Đã sửa 'void run()' thành 'run()'
            if (bannerViewPager != null && bannerImages != null && !bannerImages.isEmpty()) {
                int currentItem = bannerViewPager.getCurrentItem();
                bannerViewPager.setCurrentItem(currentItem + 1, true);
                handler.postDelayed(this, 3000);
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
    }
}
