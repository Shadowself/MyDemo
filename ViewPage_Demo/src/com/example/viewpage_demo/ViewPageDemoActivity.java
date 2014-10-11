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
	private int mScreenWidth; // ��ȡ��Ļ���
	private int item_width; // �»��߿��

	private int endPosition;
	private int beginPosition;
	private int currentFragmentIndex;
	private boolean isEnd;
	private boolean isClickTop = false; //�Ƿ��ǵ���¼�

	private TextView liaotian, faxian, tongxunlu;

	private ArrayList<Fragment> fragments;

	PopupWindow rightTopWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_page_demo);

		// ��ʼ��view
		initView();

		// ��ʼ��viewPager
		initViewPager();

	}

	public void openMoreMenu(View v) {

		if (rightTopWindow == null) {
			View contentView = getLayoutInflater().inflate(R.layout.moremenu,
					null);
			PopupWindow rightTopWindow = new PopupWindow(contentView,
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			/**
			 * ��������ⲿ �� �رմ���
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
					Toast.makeText(getApplicationContext(), "��������", Toast.LENGTH_LONG).show();
				}
			});
			
		}

		rightTopWindow.showAsDropDown(v, -320, 10);
	}

	public void initView() {
		/**
		 * �������������󶨼�����
		 */

		liaotian = (TextView) findViewById(R.id.tv_liaotian);
		faxian = (TextView) findViewById(R.id.tv_faxian);
		tongxunlu = (TextView) findViewById(R.id.tv_tongxunlu);

		liaotian.setOnClickListener(this);
		faxian.setOnClickListener(this);
		tongxunlu.setOnClickListener(this);

		/**
		 * �޸Ķ�����ɫImageView�Ŀ��
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

	// ��ʼ��Viewpager
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

	// ��ӦTextView����¼�
	@Override
	public void onClick(View v) {
		isClickTop = true;
		switch (v.getId()) {
		case R.id.tv_liaotian:
			// �ᴥ��OnPageChangeListener��onPageSelected(index)
			pager.setCurrentItem(0);
			break;

		case R.id.tv_faxian:
			// �ᴥ��OnPageChangeListener��onPageSelected(index)
			pager.setCurrentItem(1);
			break;

		case R.id.tv_tongxunlu:
			// �ᴥ��OnPageChangeListener��onPageSelected(index)
			pager.setCurrentItem(2);
			break;

		default:
			break;
		}
	}

	// ����FragmentPagerAdapter
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

	// ���û����¼�
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
				animation.setFillAfter(true); // ����Ϊtrue�������ͻ�ͣ�������ĵط�
				animation.setDuration(100); // ���ó���ʱ��
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

		// ��ʾ��ǰһ��ҳ�滬������һ��ҳ���ʱ����ǰһ��ҳ�滬��ǰ���õķ���
		// position :��ǰҳ�棬������������ҳ��
		// positionOffset:��ǰҳ��ƫ�Ƶİٷֱ�
		// positionOffsetPixels:��ǰҳ��ƫ�Ƶ�����λ��
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

		// state ==1��ʱ���ʾ���ڻ�����state==2��ʱ���ʾ��������ˣ�state==0��ʱ���ʾʲô��û��������ͣ����
		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_DRAGGING) {
				isEnd = false;
			} else if (state == ViewPager.SCROLL_STATE_SETTLING) {
				isEnd = true;
			    beginPosition = currentFragmentIndex * item_width;
				if (pager.getCurrentItem() == currentFragmentIndex) {
					// δ������һ��ҳ��
					mImageView.clearAnimation();
					Animation animation = null;
					// �ָ�λ��
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
