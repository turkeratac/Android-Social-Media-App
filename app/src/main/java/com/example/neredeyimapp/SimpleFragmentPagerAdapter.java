    package com.example.neredeyimapp;

    import androidx.annotation.NonNull;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentActivity;
    import androidx.fragment.app.FragmentManager;
    import androidx.lifecycle.Lifecycle;
    import androidx.viewpager2.adapter.FragmentStateAdapter;

    public class SimpleFragmentPagerAdapter extends FragmentStateAdapter {

        public SimpleFragmentPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if(position == 0)
            {
                return new BlankFragmentNull();
            }
            else
            {
                return new BlankFragment1();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }

    }
