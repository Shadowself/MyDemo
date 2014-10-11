package com.example.viewpage_demo;

import java.util.ArrayList;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class ViewPageDemoActivity extends FragmentActivity implements
		OnClickListener {

	private ViewPager pager;
	private ImageView mImageView;
	private int mScreenWidth; // 获取屏幕宽度
	private int item_width; // 下划线宽度

	private int endPosition;
	private int beginPosition;
	private int currentFragmentIndex;
	private boolean isEnd;
	private boolean isClickTop = false; //是否是点击事件

	private TextView liaotian, faxian, tongxunlu;

	private ArrayList<Fragment> fragments;

	PopupWindow rightTopWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_page_demo);

		// 初始化view
		initView();

		// 初始化viewPager
		initViewPager();

	}

	public void openMoreMenu(View v) {

		if (rightTopWindow == null) {
			View contentView = getLayoutInflater().inflate(R.layout.moremenu,
					null);
			PopupWindow rightTopWindow = new PopupWindow(contentView,
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			/**
			 * 点击窗口外部 就 关闭窗口
			 */
			rightTopWindow.setFocusable(true);
			rightTopWindow.setOutsideTouchable(true);

			this.rightTopWindow = rightTopWindow;
			rightTopWindow.setBackgroundDrawable(new BitmapDrawable());
			
			RelativeLayout personalinfor = (RelativeLayout)contentView.findViewById(R.id.personalinfor);
			
			personalinfor.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "个人中心", Toast.LENGTH_LONG).show();
				}
			});
			
		}

		rightTopWindow.showAsDropDown(v, -320, 10);
	}

	public void initView() {
		/**
		 * 给顶部工具栏绑定监听器
		 */

		liaotian = (TextView) findViewById(R.id.tv_liaotian);
		faxian = (TextView) findViewById(R.id.tv_faxian);
		tongxunlu = (TextView) findViewById(R.id.tv_tongxunlu);

		liaotian.setOnClickListener(this);
		faxian.setOnClickListener(this);
		tongxunlu.setOnClickListener(this);

		/**
		 * 修改顶部蓝色ImageView的宽度
		 */
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mImageView = (ImageView) findViewById(R.id.top_image);
		item_width = (int) ((mScreenWidth / 3.0 + 0.5f));

		LayoutParams params = (LayoutParams) mImageView.getLayoutParams();
		params.width = item_width;
		mImageView.setLayoutParams(params);

		pager = (ViewPager) findViewById(R.id.pager);
	}

	// 初始化Viewpager
	private void initViewPager() {
		fragments = new ArrayList<Fragment>();

		BaseFragment fragment = new BaseFragment();
		fragments.add(fragment);

		BaseFragment1 fm = new BaseFragment1();
		fragments.add(fm);

		BaseFragment2 fragment2 = new BaseFragment2();
		fragments.add(fragment2);

		MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(
				getSupportFragmentManager(), fragments);
		pager.setAdapter(fragmentPagerAdapter);
		fragmentPagerAdapter.setFragments(fragments);
		pager.setOnPageChangeListener(new MyOnPageChangeListener());
		pager.setCurrentItem(0);
		liaotian.setEnabled(false);
	}

	// 响应TextView点击事件
	@Override
	public void onClick(View v) {
		isClickTop = true;
		switch (v.getId()) {
		case R.id.tv_liaotian:
			// 会触发OnPageChangeListener的onPageSelected(index)
			pager.setCurrentItem(0);
			break;

		case R.id.tv_faxian:
			// 会触发OnPageChangeListener的onPageSelected(index)
			pager.setCurrentItem(1);
			break;

		case R.id.tv_tongxunlu:
			// 会触发OnPageChangeListener的onPageSelected(index)
			pager.setCurrentItem(2);
			break;

		default:
			break;
		}
	}

	// 配置FragmentPagerAdapter
	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		private ArrayList<Fragment> fragments;
		private FragmentManager fm;

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
			this.fm = fm;
		}

		public MyFragmentPagerAdapter(FragmentManager fm,
				ArrayList<Fragment> fragments) {
			super(fm);
			this.fm = fm;
			this.fragments = fragments;
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void setFragments(ArrayList<Fragment> fragments) {
			if (this.fragments != null) {
				FragmentTransaction ft = fm.beginTransaction();
				for (Fragment f : this.fragments) {
					ft.remove(f);
				}
				ft.commit();
				ft = null;
				fm.executePendingTransactions();
			}
			this.fragments = fragments;
			notifyDataSetChanged();
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			Object obj = super.instantiateItem(container, position);
			return obj;
		}

	}

	// 配置滑动事件
	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(final int position) {
			Animation animation =null;
			if(!isClickTop){
				animation = new TranslateAnimation(endPosition, position
						* item_width, 0, 0);

			}else{
				animation = new TranslateAnimation(beginPosition, position
						* item_width, 0, 0);
				isClickTop = false;
			}
			beginPosition = position * item_width;
			currentFragmentIndex = position;
			if (animation != null) {
				animation.setFillAfter(true); // 设置为true，动画就会停留在最后的地方
				animation.setDuration(100); // 设置持续时间
				mImageView.startAnimation(animation);
			}

			Toast.makeText(getApplicationContext(), "selected" + position,
					Toast.LENGTH_LONG).show();
			switch (position) {
			case 0:
				liaotian.setEnabled(false);
				faxian.setEnabled(true);
				tongxunlu.setEnabled(true);
				break;
			case 1:
				liaotian.setEnabled(true);
				faxian.setEnabled(false);
				tongxunlu.setEnabled(true);
				break;
			case 2:
				liaotian.setEnabled(true);
				faxian.setEnabled(true);
				tongxunlu.setEnabled(false);
				break;

			default:
				break;
			}

		}

		// 表示在前一个页面滑动到后一个页面的时候，在前一个页面滑动前调用的方法
		// position :当前页面，及你点击滑动的页面
		// positionOffset:当前页面偏移的百分比
		// positionOffsetPixels:当前页面偏移的像素位置
		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			if (!isEnd) {
				if (currentFragmentIndex == position) {
					endPosition = item_width * currentFragmentIndex
							+ (int) (item_width * positionOffset);
				}
				if (currentFragmentIndex == position + 1) {
					endPosition = item_width * currentFragmentIndex
							- (int) (item_width * (1 - positionOffset));
				}

				Animation mAnimation = new TranslateAnimation(beginPosition,
						endPosition, 0, 0);
				mAnimation.setFillAfter(true);
				mAnimation.setDuration(100);
				if(!isClickTop)
					mImageView.startAnimation(mAnimation);
			    beginPosition = endPosition;

			}
		}

		// state ==1的时候表示正在滑动，state==2的时候表示滑动完毕了，state==0的时候表示什么都没做，就是停在那
		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_DRAGGING) {
				isEnd = false;
			} else if (state == ViewPager.SCROLL_STATE_SETTLING) {
				isEnd = true;
			    beginPosition = currentFragmentIndex * item_width;
				if (pager.getCurrentItem() == currentFragmentIndex) {
					// 未跳入下一个页面
					mImageView.clearAnimation();
					Animation animation = null;
					// 恢复位置
					animation = new TranslateAnimation(endPosition,
							currentFragmentIndex * item_width, 0, 0);
					animation.setFillAfter(true);
					animation.setDuration(100);
					if(!isClickTop)
					   mImageView.startAnimation(animation);
					endPosition = currentFragmentIndex * item_width;
				}

			}
		}

	}

}
