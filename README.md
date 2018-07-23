### 截图

<img width="300" height="500" src="https://github.com/linecyc/RecyclerBanner/blob/master/screenshots/p4.png"/> <img width="300" height="500" src="https://github.com/linecyc/RecyclerBanner/blob/master/screenshots/p1.png"/><br />
<img width="300" height="500" src="https://github.com/linecyc/RecyclerBanner/blob/master/screenshots/p2.png"/> <img width="300" height="500" src="https://github.com/linecyc/RecyclerBanner/blob/master/screenshots/p3.png"/>


### 使用

* 只需要实现BannerCreator,自定义布局，在bannerView.setupWithBannerCreator(new ScaleCreator()),
最后onRefreshData()就行。

    	class ScaleCreator implements BannerCreator<T> {
    
      		@Override public int getLayoutResId() {
				//布局
    			return R.layout.item_scale;
      		}
    
      		@Override public void onBindData(View view, T data, int position) {
				//绑定数据
    			ImageView imageView = view.findViewById(R.id.iv);
    		imageView.setBackgroundColor(data);
      		}
    	}

* 属性表格

属性 | 方法 | 含义
---|---|---
 null | setupWithBannerCreator() | 初始化布局
 null | onRefreshData() | 刷新数据 
 null | onStart() | 开始
 null | onStop() | 停止
 null | setOnBannerClickListener() | 点击监听
 null | setOnBannerScrollChangeListener() | 滚动监听
orientation | setOrientation | 方向
durationTime | setDurationTime() | 自动播放间隔时间
isShowIndicator | setShowIndicator() | 是否展示指示器
 null | setIndicatorDrawableRes() | 设置指示器样式（select）
 null | setIndicatorColorRes() | 设置指示器样式（select）
 null | setIndicatorStyle() | 设置指示器位置样式
isAutoPlay | setAutoPlay() | 是否自动播放
isLoop | setLoop() | 是否无限循环
isScaleCover | setScaleCover() | 是否启用放大中间模式
scaleSize | setScaleSize() | 缩小比例（0到1）
isShowBothEnds | setShowBothEnds() | 是否展示上下两个
BothEndsSpace | setSpaceBetween() | 两端预留空间

* 备注
 * 1、非无限模式下，即isLoop = false；初始和结束位置不会居中；
 * 2、isScaleCover = true时，默认isShowBothEnds = true;
 * 3、scaleSize是重正常大小缩小到设置的比例；

