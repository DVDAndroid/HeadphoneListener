package com.dvd.android.headphonelistener.preference;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.dvd.android.headphonelistener.R;

public class AppPickerPreference extends Preference implements
		AdapterView.OnItemClickListener {

	private Context mContext;
	private List<App> mApplicationsList = new ArrayList<>();
	private ListView mListView;
	private LayoutInflater mLayoutInflater;
	private Dialog mDialog;
	private Boolean showPackageName;
	private ImageButton mBtnAppIcon;

	public AppPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.mContext = context;

		TypedArray typedArray = mContext.obtainStyledAttributes(attrs,
				R.styleable.AppPickerPreference);
		showPackageName = typedArray.getBoolean(
				R.styleable.AppPickerPreference_showPackageName, false);

		typedArray.recycle();

		mLayoutInflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	@SuppressWarnings("deprecation")
	protected void onBindView(View view) {
		// from GravityBox by C3C0
		LinearLayout widgetFrameView = ((LinearLayout) view
				.findViewById(android.R.id.widget_frame));
		mBtnAppIcon = new ImageButton(mContext);
		int mAppIconPreviewSizePx = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 60, mContext.getResources()
						.getDisplayMetrics());

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				mAppIconPreviewSizePx, mAppIconPreviewSizePx);
		lp.gravity = Gravity.CENTER;
		mBtnAppIcon.setLayoutParams(lp);
		mBtnAppIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
		mBtnAppIcon.setFocusable(false);
		mBtnAppIcon.setClickable(false);
		try {
			Drawable icon = mContext.getPackageManager().getApplicationIcon(
					getPackageAppName());
			mBtnAppIcon.setImageDrawable(icon);
		} catch (PackageManager.NameNotFoundException e) {
			mBtnAppIcon.setImageDrawable(mContext.getResources().getDrawable(
					R.mipmap.ic_launcher));
		}
		widgetFrameView.addView(mBtnAppIcon);
		widgetFrameView.setVisibility(View.VISIBLE);

		super.onBindView(view);
	}

	@Override
	protected void onAttachedToActivity() {
		super.onAttachedToActivity();

		setSummary(getAppName());
	}

	@Override
	@SuppressLint("InflateParams")
	protected void onClick() {
		super.onClick();

		View mDialogView = mLayoutInflater.inflate(R.layout.alert_layout, null);

		mDialog = new Dialog(mContext);
		mDialog.setTitle(getTitle());
		mDialog.setContentView(mDialogView);
		mDialog.show();

		mListView = (ListView) mDialogView.findViewById(R.id.listViewApps);
		mListView.setOnItemClickListener(this);

		new LoadApplications(mDialogView).execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		saveString(mApplicationsList.get(position).getName().toString(),
				mApplicationsList.get(position).getPackageName().toString());

		mDialog.dismiss();
	}

	public String getAppName() {
		return getSharedPreferences().getString(getKey(),
				mContext.getString(R.string.none));
	}

	public String getPackageAppName() {
		return getSharedPreferences().getString(getKey() + "_packageName",
				mContext.getString(R.string.none));
	}

	@SuppressWarnings("deprecation")
	public void saveString(String name, String packageName) {
		// String sName = getSharedPreferences().getString(getKey(), null);

		getSharedPreferences().edit().putString(getKey(), name).apply();
		getSharedPreferences().edit()
				.putString(getKey() + "_packageName", packageName).apply();

		try {
			Drawable icon = mContext.getPackageManager().getApplicationIcon(
					getPackageAppName());
			mBtnAppIcon.setImageDrawable(icon);
		} catch (PackageManager.NameNotFoundException e) {
			mBtnAppIcon.setImageDrawable(mContext.getResources().getDrawable(
					R.mipmap.ic_launcher));
		}
		setSummary(name);
	}

	public class App {

		private CharSequence name;
		private CharSequence packageName;
		private Drawable icon;

		public App() {
		}

		public CharSequence getName() {
			return name;
		}

		public void setName(CharSequence name) {
			this.name = name;
		}

		public CharSequence getPackageName() {
			return packageName;
		}

		public void setPackageName(CharSequence packageName) {
			this.packageName = packageName;
		}

		public Drawable getIcon() {
			return icon;
		}

		public void setIcon(Drawable icon) {
			this.icon = icon;
		}

	}

	public class LoadApplications extends AsyncTask<Void, Void, Void> {

		int id_app;
		View dialogView;

		private LoadApplications(View view) {
			this.dialogView = view;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			id_app = 0;
			mApplicationsList.clear();
		}

		@Override
		@SuppressLint("NewApi")
		protected Void doInBackground(Void... params) {
			final PackageManager pm = mContext.getPackageManager();

			App fake_app = new App();
			fake_app.setName(mContext.getString(R.string.none));
			fake_app.setPackageName(mContext.getString(R.string.none));
			fake_app.setIcon(mContext.getResources().getDrawable(
					R.mipmap.ic_launcher));

			mApplicationsList.add(fake_app);
			List<PackageInfo> packageList = pm.getInstalledPackages(0);
			Collections.sort(packageList, new Comparator<PackageInfo>() {
				@Override
				public final int compare(PackageInfo aa, PackageInfo ab) {
					Collator sCollator = Collator.getInstance();

					CharSequence sa = aa.applicationInfo.loadLabel(pm);
					CharSequence sb = ab.applicationInfo.loadLabel(pm);

					if (sa == null) {
						sa = aa.packageName;
					}
					if (sb == null) {
						sb = ab.packageName;
					}

					return sCollator.compare(sa.toString(), sb.toString());
				}
			});

			for (PackageInfo p : packageList) {
				App app = new App();
				app.setName(p.applicationInfo.loadLabel(pm));
				app.setPackageName(p.applicationInfo.packageName);
				app.setIcon(p.applicationInfo.loadIcon(pm));

				if ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					mApplicationsList.add(app);
				}

			}

			String current_packageName = getSharedPreferences().getString(
					getKey() + "_packageName",
					mContext.getString(R.string.none));
			assert current_packageName != null;
			if (!current_packageName.equals(mContext.getString(R.string.none))) {
				for (int i = 0; i < mApplicationsList.size(); i++) {
					if (current_packageName.equals(mApplicationsList.get(i)
							.getPackageName())) {
						id_app = i;
					}
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			AppListAdapter adapter = new AppListAdapter(mContext,
					R.layout.listitem_row, mApplicationsList);
			ProgressBar progressBar = (ProgressBar) dialogView
					.findViewById(R.id.loadingProgressbar);

			mListView.setAdapter(adapter);
			mListView.setSelection(id_app);

			progressBar.setVisibility(View.GONE);

			super.onPostExecute(result);
		}
	}

	public class AppListAdapter extends ArrayAdapter<CharSequence> {

		public AppListAdapter(Context context, int resource, List list) {
			super(context, resource, list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View itemView = convertView;
			if (itemView == null) {
				itemView = mLayoutInflater.inflate(R.layout.listitem_row,
						parent, false);
			}

			TextView app_name = (TextView) itemView.findViewById(R.id.app_name);
			TextView app_package = (TextView) itemView
					.findViewById(R.id.app_pkg_name);
			ImageView icon = (ImageView) itemView.findViewById(R.id.app_icon);

			App currentApp = mApplicationsList.get(position);

			app_name.setSelected(true);
			app_package.setSelected(true);

			app_name.setText(currentApp.getName());
			app_package.setText(currentApp.getPackageName());
			icon.setImageDrawable(currentApp.getIcon());

			if (showPackageName) {
				app_package.setVisibility(View.VISIBLE);
			}

			return itemView;
		}
	}
}