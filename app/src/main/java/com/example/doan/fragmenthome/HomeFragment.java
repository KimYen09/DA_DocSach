//package com.example.doan.fragmenthome;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
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
//import com.example.doan.adapter.BannerAdapter;
//import com.example.doan.adapter.ChapterAdapter;
//import com.example.doan.adapter.StoryAdapter;
//import com.example.doan.homestory.AddStory;
//import com.example.doan.model.Story;
//import com.example.doan.ui.ChapterDetailActivity;
//import com.example.doan.ui.ChapterListActivity;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
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
//    private RecyclerView recyclerView, recyclerViewPre;
//    private StoryAdapter storyAdapter, storyAdapterPre;
//    private List<Story> storyList, storyListPre;
//    private DatabaseReference databaseReference;
//    private ViewPager2 bannerViewPager;
//    private BannerAdapter bannerAdapter;
//    private List<Integer> bannerImages;
//    private final Handler handler = new Handler();
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//
//        bannerViewPager = view.findViewById(R.id.bannerViewPager);
//        recyclerView = view.findViewById(R.id.recyclerView);
//        recyclerViewPre = view.findViewById(R.id.dspre);
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
//        loadStories();
//        databaseReference.keepSynced(true);
//
//
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
////    private void loadStories() {
////
////        databaseReference.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                storyList.clear();
////                for (DataSnapshot data : snapshot.getChildren()) {
////                    Story story = data.getValue(Story.class);
////                    if (story != null) {
////                        if (story.getId() == null || story.getId().isEmpty()) {
////                            story.setId(data.getKey());
////                        }
////                        Log.d("FirebaseData", "Story ID: " + story.getId() + ", Title: " + story.getTitle());
////                        storyList.add(story);
////                    }
////                    Log.d("FirebaseData", "Tổng số truyện tải về: " + storyList.size());
////
////
////                }
////
////                storyAdapter = new StoryAdapter(getContext(), storyList, null);
////                recyclerView.setAdapter(storyAdapter);
////                storyAdapter.notifyDataSetChanged();
////
////
////                storyAdapter.setOnItemClickListener(story -> {
////                    if (story.getId() == null || story.getId().isEmpty()) {
////                        Toast.makeText(getContext(), "Lỗi: ID truyện không hợp lệ!", Toast.LENGTH_SHORT).show();
////                        Log.d("FirebaseData", "Story ID sau khi cập nhật: " + story.getId());
////                        return;
////                    }
////                    Toast.makeText(getContext(), "Bạn đã chọn: " + story.getTitle(), Toast.LENGTH_SHORT).show();
////                    Intent intent = new Intent(getContext(), ChapterListActivity.class);
////                    intent.putExtra("storyId", story.getId());
////
////                    startActivity(intent);
////                });
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////                Toast.makeText(getContext(), "Lỗi tải truyện!", Toast.LENGTH_SHORT).show();
////                Log.e("FirebaseError", "Lỗi Firebase: " + error.getMessage());
////            }
////        });
////    }
//
//
//
//    private void loadStories() {
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
//                            story.setId(data.getKey());
//                        }
//                        Log.d("FirebaseData", "Story ID: " + story.getId() + ", Title: " + story.getTitle());
//
//                        // Lọc truyện theo type
//                        if ("Premium".equalsIgnoreCase(story.getType())) {
//                            storyListPre.add(story);
//                        } else {
//                            storyList.add(story);
//                        }
//                    }
//                }
//
//                Log.d("FirebaseData", "Tổng số truyện thường: " + storyList.size());
//                Log.d("FirebaseData", "Tổng số truyện Premium: " + storyListPre.size());
//
//                updateRecyclerViews();
//
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
//    // 🔥 Hàm chung để xử lý khi nhấn vào truyện
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
//    private void updateRecyclerViews() {
//        if (storyAdapter == null) {
//            storyAdapter = new StoryAdapter(getContext(), storyList, null);
//            recyclerView.setAdapter(storyAdapter);
//        } else {
//            storyAdapter.notifyDataSetChanged();
//        }
//
//        if (storyAdapterPre == null) {
//            storyAdapterPre = new StoryAdapter(getContext(), storyListPre, null);
//            recyclerViewPre.setAdapter(storyAdapterPre);
//        } else {
//            storyAdapterPre.notifyDataSetChanged();
//        }
//
//        // Kiểm tra số lượng truyện Premium và in ra Logcat
//        Log.d("RecyclerViewUpdate", "Cập nhật RecyclerView - Truyện thường: " + storyList.size());
//        Log.d("RecyclerViewUpdate", "Cập nhật RecyclerView - Truyện Premium: " + storyListPre.size());
//
//        // Đảm bảo sự kiện click được set lại
//        setupStoryClickListener(storyAdapter);
//        setupStoryClickListener(storyAdapterPre);
//    }
//
//
//}


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
import com.example.doan.ui.ChapterListActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ImageView imvPremium;
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



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerViewPre = view.findViewById(R.id.dspre);
        imvPremium = view.findViewById(R.id.imvPremium);

        // Setup Banner
        setupBanner();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPre.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        storyList = new ArrayList<>();
        storyListPre = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("stories");

        // Tắt cache Firebase
        databaseReference.keepSynced(false);
        databaseReference.getDatabase().purgeOutstandingWrites();

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
        Log.d("FirebaseData", "Bắt đầu tải dữ liệu từ Firebase...");

//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storyList.clear();
                storyListPre.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Story story = data.getValue(Story.class);
                    if (story != null) {
                        if (story.getId() == null || story.getId().isEmpty()) {
                            story.setId(data.getKey());
                        }
                        Log.d("FirebaseData", "Story ID: " + story.getId() + ", Title: " + story.getTitle() + ", Type: " + story.getType());

                        if (story.getType() != null && story.getType().trim().equalsIgnoreCase("Premium")) {
                            storyListPre.add(story);
                        } else {
                            storyList.add(story);
                        }

                    }
                }


                Log.d("FirebaseData", "Tổng số truyện thường: " + storyList.size());
                Log.d("FirebaseData", "Tổng số truyện Premium: " + storyListPre.size());

                // Cập nhật giao diện
                updateRecyclerViews();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải truyện!", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", "Lỗi Firebase: " + error.getMessage());
            }
        });
    }

    private void updateRecyclerViews() {
        recyclerView.setAdapter(null);
        storyAdapter = new StoryAdapter(getContext(), storyList, null);
        recyclerView.setAdapter(storyAdapter);
        storyAdapter.notifyDataSetChanged();

        recyclerViewPre.setAdapter(null);
        storyAdapterPre = new StoryAdapter(getContext(), storyListPre, null);
        recyclerViewPre.setAdapter(storyAdapterPre);
        storyAdapterPre.notifyDataSetChanged();
        recyclerViewPre.invalidate();

        Log.d("RecyclerViewUpdate", "Cập nhật RecyclerView - Truyện thường: " + storyList.size());
        Log.d("RecyclerViewUpdate", "Cập nhật RecyclerView - Truyện Premium: " + storyListPre.size());

        setupStoryClickListener(storyAdapter);
        setupStoryClickListener(storyAdapterPre);
    }

    private void setupStoryClickListener(StoryAdapter adapter) {
        adapter.setOnItemClickListener(story -> {
            if (story.getId() == null || story.getId().isEmpty()) {
                Toast.makeText(getContext(), "Lỗi: ID truyện không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getContext(), "Bạn đã chọn: " + story.getTitle(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), ChapterListActivity.class);
            intent.putExtra("storyId", story.getId());
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
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
        public void run() {
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
