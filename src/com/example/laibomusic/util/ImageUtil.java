package com.example.laibomusic.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.example.fragments.PICFragment;
import com.example.laibomusic.R;
import com.example.widget.CircleImageView;

public class ImageUtil {
	
	
	public static Map<String,Set<ImageView>> pendingImagesMap = new HashMap<String,Set<ImageView>>();
	//二级缓存
	public static Map<String, SoftReference<Bitmap>> sImageCache = new HashMap<String, SoftReference<Bitmap>>();
	/**
	 * 加载资源图片
	 * 
	 * @param context
	 * @param imageview
	 * @param resourceID
	 * @param defResourceID
	 */
	public static void loadResourceImage(final Context context,
			final ImageView imageview, final int resourceID,
			final int defResourceID) {
		imageview.setBackgroundResource(defResourceID);
		new AsyncTask<Void,Integer,Object>(){

			@Override
			protected Object doInBackground(Void... params) {
				return ImageUtil.readBitmap(context, resourceID);
			}

			@Override
			protected void onPostExecute(Object result) {
				Bitmap bm = (Bitmap) result;
				if (bm == null) {
					imageview.setBackgroundResource(defResourceID);
				} else {
					imageview.setBackgroundDrawable(new BitmapDrawable(bm));
				}
				super.onPostExecute(result);
			}
		}.execute( );
		
	}

	/**
	 * 
	 * 
	 * @param context
	 * @param id
	 *           
	 * @return
	 */
	public static Bitmap readBitmap(Context context, int id) {
		Bitmap bm = null;
		if (sImageCache.containsKey(id + "")) {
			bm = sImageCache.get(id + "").get();
			if (bm == null) {
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inPreferredConfig = Bitmap.Config.RGB_565;// 表示16位位�?
																// 565代表对应三原色占的位�?
				opt.inInputShareable = true;
				opt.inPurgeable = true;// 设置图片可以被回�?
				InputStream is = context.getResources().openRawResource(id);
				bm = BitmapFactory.decodeStream(is, null, opt);
				sImageCache.put(id + "", new SoftReference<Bitmap>(bm));
			}
		} else {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;// 表示16位位�?
															// 565代表对应三原色占的位�?
			opt.inInputShareable = true;
			opt.inPurgeable = true;// 设置图片可以被回�?
			InputStream is = context.getResources().openRawResource(id);
			bm = BitmapFactory.decodeStream(is, null, opt);
			sImageCache.put(id + "", new SoftReference<Bitmap>(bm));
		}
		return bm;
	}

	/**
	 * 加载圆形专辑图片
	 * 
	 * @param context
	 * @param imageview
	 * @param defResourceID
	 * @param filePath
	 * @param fileSid
	 * @param url
	 */
	public static void loadCircleAlbum(final Context context,
			final CircleImageView imageview, final int defResourceID,
			final String filePath, final String fileSid, final String url) {
		final String fileName = Consts.PATH_ALBUM + File.separator + fileSid
				+ ".jpg";
		imageview.setImageResource(defResourceID);
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected void onPostExecute(Object result) {
				final Bitmap bm = (Bitmap) result;
				if (bm == null) {
					imageview.setImageResource(defResourceID);
				} else {
					new Thread() {

						@Override
						public void run() {
							saveImage(bm, fileName);
						}
					}.start();

					imageview.setImageDrawable(new BitmapDrawable(bm));
				}
			}
			@Override
			protected Object doInBackground(Object... params) {
				return getAlbum(context, filePath, fileSid, url, fileName);
			}
		}.execute();
	}

	/**
	 * 加载专辑图片
	 * 
	 * @param imageview
	 * @param defResourceID
	 * @param filePath
	 * @param fileSid
	 * @param url
	 */
	public static void loadAlbum(final Context context,
			final ImageView imageview, final int defResourceID,
			final String filePath, final String fileSid, final String url) {
		final String fileName = Consts.PATH_ALBUM + File.separator + fileSid
				+ ".jpg";
		imageview.setBackgroundResource(defResourceID);
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected void onPostExecute(Object result) {
				final Bitmap bm = (Bitmap) result;
				if (bm == null) {
					imageview.setBackgroundResource(defResourceID);
				} else {
					new Thread() {

						@Override
						public void run() {
							saveImage(bm, fileName);
						}
					}.start();

					imageview.setBackgroundDrawable(new BitmapDrawable(bm));
				}
			}
			@Override
			protected Object doInBackground(Object... params) {
				return getAlbum(context, filePath, fileSid, url, fileName);
			}
		}.execute();
	}

	/**
	 * 获取图片数据
	 * 
	 * @param filePath
	 * @param fileSid
	 * @param url
	 * @return
	 */
	public static Bitmap getAlbum(Context context, String filePath,
			String fileSid, String url, String fileName) {
		if (fileName == null || fileName.equals("")) {
			fileName = Consts.PATH_ALBUM + File.separator + fileSid + ".jpg";
		}
		Bitmap bm = getFirstArtwork(context, filePath, fileSid, fileName);
		if (bm != null) {
			return bm;
		}
		if (url != null && !url.equals("")) {
			bm = getBitmap(url);
			if (bm != null) {
				sImageCache.put(filePath, new SoftReference<Bitmap>(bm));
			}
		}
		return bm;
	}

	/**
	 * 根据歌曲文件获取相关的专辑图�?
	 * 
	 * @param filePath
	 *            文件路径
	 * @return
	 */
	private static Bitmap getFirstArtwork(Context context, String filePath,
			String fileSid, String fileName) {

		Bitmap bm = null;
		if (sImageCache.containsKey(filePath)) {
			bm = sImageCache.get(filePath).get();
			if (bm == null) {
				bm = loadFirstArtwork(filePath, fileName, context);
			} else {
				return bm;
			}
		} else {
			bm = loadFirstArtwork(filePath, fileName, context);
		}
		return bm;
	}

	/**
	 * 加载图片
	 * 
	 * @param filePath
	 *            文件路径
	 * @return
	 */
	private static Bitmap loadFirstArtwork(String filePath, String fileName,
			Context context) {
		File artworkFile = new File(fileName);
		Bitmap bm = null;
		if (artworkFile.exists()) {
			bm = getImageFormFile(filePath, context);
			if (bm != null) {
				sImageCache.put(filePath, new SoftReference<Bitmap>(bm));
				return bm;
			}
			bm = getArtworkFormFile(filePath, fileName);
		} else {
			bm = getArtworkFormFile(filePath, fileName);
		}
		return bm;
	}

	/**
	 * 保存图片到本�?
	 * 
	 * @param bm
	 * @param fileName
	 */
	private static void saveImage(Bitmap bm, String fileName) {
		if (bm == null) {
			return;
		}
		try {
			//你要存放的文件
			File file = new File(fileName);
			// file文件的上�?��文件�?
			File parentFile = new File(file.getParent());
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}

			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	/**
	 * 从mp3文件获取专辑图片
	 * 
	 * @param filePath
	 * @return
	 */
	private static Bitmap getArtworkFormFile(String filePath, String fileName) {
//		File sourceFile = new File(filePath);
//		if (!sourceFile.exists())
//			return null;
//		Bitmap bm = null;
//		try {
//			AudioFileIO.logger.setLevel(Level.SEVERE);
//			ID3v23Frame.logger.setLevel(Level.SEVERE);
//			ID3v23Tag.logger.setLevel(Level.SEVERE);
//			MP3File mp3file = new MP3File(sourceFile);
//			if (mp3file.hasID3v2Tag()) {
//				AbstractID3v2Tag tag = mp3file.getID3v2Tag();
//				AbstractID3v2Frame frame = (AbstractID3v2Frame) tag
//						.getFrame("APIC");
//				if (frame != null) {
//					FrameBodyAPIC body = (FrameBodyAPIC) frame.getBody();
//					if (body != null) {
//						byte[] imageData = body.getImageData();
//						// 通过BitmapFactory转成Bitmap
//						bm = BitmapFactory.decodeByteArray(imageData, 0,
//								imageData.length);
//						sImageCache
//								.put(filePath, new SoftReference<Bitmap>(bm));
//						return bm;
//					} else {
//						return null;
//					}
//
//				} else {
//					return null;
//				}
//			} else {
//				return null;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
 		return null;
	}

	/**
	 * 从文件中获取图片
	 */
	private static Bitmap getImageFormFile(String filePath, Context context) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, opts);
        
		/** 这里是获取手机屏幕的分辨率用来处�?图片 溢出问题的�?begin */
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		int displaypixels = dm.widthPixels * dm.heightPixels;

		opts.inSampleSize = computeSampleSize(opts, -1, displaypixels);
		opts.inJustDecodeBounds = false;
		try {
			return BitmapFactory.decodeFile(filePath, opts);
		} catch (OutOfMemoryError err) {
			err.printStackTrace();
			return null;
		}
	}

	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 根据�?��网络连接(URL)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 */
	public static Bitmap getBitmap(URL imageUri) {
		// 显示网络上的图片
		URL myFileUrl = imageUri;
		Bitmap bitmap = null;
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 根据�?��网络连接(String)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	public static Bitmap getBitmap(String imageUri) {
		// 显示网络上的图片
		if(!imageUri.startsWith("http"))
			return null;
		
		System.setProperty ("http.keepAlive", "false");
		Bitmap bitmap = null;
		if (imageUri == null || ("").equals(imageUri))
			return null;
		try {
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect(); 
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

 
	
	
	/**
	 * 加载图片
	 * 
	 * @param context
	 * @param imageview
	 * @param defResourceID
	 *            默认的图片id
	 * @param fileParentPath
	 *            图片保存的文件夹
	 * @param url
	 */
	
	public static void loadImage(Context context, ImageView imageview,
			String url, String artistName) {
		new ImageDownLoadTask(context, imageview, url).execute(artistName);
	}
	
	
	
//	public static void loadImage(final Context context,   ImageView imageview,
//			final int defResourceID, final String fileParentPath,
//			final String url,final String artistname) {
//        //artistname 相当于     url
//		final String filePath = fileParentPath + File.separator
//		+ artistname + ".jpg";
//		//imageview.setImageResource(defResourceID);
//		new AsyncTask<Object, Object, Bitmap>() {
//			@SuppressWarnings("deprecation")
//			@Override
//			protected void onPostExecute(Bitmap result) {
//				Bitmap bm = result;
//			    if(bm != null){    //如果result不为空
//					ImageSize imagesize = null;
//					if(imageview instanceof ImageView)
//						 imagesize = getImageViewSize((ImageView)imageview);
//				     int a =  imagesize.width;
//				     int b = imagesize.height;
//				     int c = bm.getWidth();
//				     int d = bm.getHeight();
//				     String tagname = imageview.getTag().toString();
//				     Log.d("TAG","@异步加载后   imageview的tag：" + imageview.getTag().toString());
//				     Log.d("TAG	","imagesize.width :"+ a + " imagesize.height:" + b + "bm.getwidth()" + c +"bm.getheight" + d);
//				     float scale1 =    (a * 1.0f / c) ;
//				     float scale2 =    (c * 1.0f/ d);
//				     float scale = Math.max(scale1, scale2);
////				     	if(pendingImagesMap.containsKey(tagname)){
////				     		Set<ImageView> sets =  pendingImagesMap.get(tagname);
////				     	 
//// 				     		for(ImageView imageview : sets){
//// 				     		 
//// 				     			imageview.setImageBitmap(scaleBitmap(bm,scale));
//// 				     		    Log.d("TAG","异步加载后打印imageview的tag：" + imageview.getTag().toString());
//// 				     		}
////				     	    
////				     		pendingImagesMap.remove(tagname);
////				     	}		
//				     if(imageview.getTag()!=null && imageview.getTag().equals(artistname))
//				       ((ImageView)imageview).setImageBitmap(scaleBitmap(bm,scale));
//				     PICFragment.mNeedImgUpdate = false;
//				}
//				
//			}
//			@Override
//			protected Bitmap doInBackground(Object... params) {
//				return ImageUtil.loadImage(context, filePath, url);
//			}
//		}.execute();
//	}

	
	public static class ImageDownLoadTask extends AsyncTask<String,Integer,Bitmap>{

		private ImageView imageview;
		private String artistUri;
		private Context context;
		private String url;
		private String artisname;
		
		public ImageDownLoadTask(Context context,ImageView imageview,String url){
			this.context  = context;
			this.imageview = imageview;
			this.url = url;
		}
		
		@Override
		protected Bitmap doInBackground(String... artisname) {
			File sd = Environment.getExternalStorageDirectory(); 
			String fileParentPath = sd.getPath()+"/notes"; 
			String filePath = fileParentPath + File.separator
					+ artisname[0] + ".jpg";
			 this.artisname = artisname[0];
			if (url == null || url.equals(""))  //如果传进来的url不为空的话，表示从网络加载  ，如果为空的话，就是表示不从网络加载
				return loadImage(context, filePath, artisname[0]);// 这里小心
			else
				return loadImage(context, filePath, url);// 这里小心 是param0 不是url
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if(result != null){
				ImageSize imagesize = null;
				if (imageview instanceof ImageView)
					imagesize = getImageViewSize((ImageView) imageview);
				int a = imagesize.width;
				int b = imagesize.height;
				int c = result.getWidth();
				int d = result.getHeight();
				float scale1 = (a * 1.0f / c);
				float scale2 = (c * 1.0f / d);
				float scale = Math.max(scale1, scale2);
				if (imageview.getTag() != null
						&& imageview.getTag().equals(artisname)){
					((ImageView) imageview).setImageBitmap(scaleBitmap(result, scale));
					Log.d("TAG","图像生成  iamgeview.getTag:"+imageview.getTag().toString()+"artistname: " + artisname);
				}
				
				PICFragment.mNeedImgUpdate = false;
			}else{
			   imageview.setImageResource(R.drawable.no_art_normal); 
			}
			
		}
		
		
	}
	
	
	
	/**
	 * 加载本地图片
	 * 
	 * @param context
	 * @param imageview
	 * @param defResourceID
	 * @param filePath
	 */
	public static void loadLocalImage(final Context context,
			final View imageview, final int defResourceID, final String filePath) {
		imageview.setBackgroundResource(defResourceID);
		new AsyncTask<Void,Integer,Object>() {

			@Override
			protected void onPostExecute(Object result) {
				Bitmap bm = (Bitmap) result;
				if (bm == null) {
					imageview.setBackgroundResource(defResourceID);
				} else {
					imageview.setBackgroundDrawable(new BitmapDrawable(bm));
				}
			}

			@Override
			protected Object doInBackground(Void... params) {
				return ImageUtil.loadLocalImage(context, filePath);
			}

			 
		}.execute();
	}

	/**
	 * 
	 * @param context
	 * @param filePath
	 * @return
	 */
	private static Bitmap loadLocalImage(Context context, String filePath) {
		// 判断内存中是否存在图片
		Bitmap bitmap = null;
		if (sImageCache.containsKey(filePath)) {
			bitmap = sImageCache.get(filePath).get();
		}
		if (bitmap == null) {
			// 判断内存卡里面是否存在图片
			bitmap = getImageFormFile(filePath, context);
		}

		if (bitmap != null) {
			sImageCache.put(filePath, new SoftReference<Bitmap>(bitmap));
		}
		return bitmap;
	}

	/**
	 * 加载图片
	 * 
	 * @param context
	 * @param filePath
	 *            图片路径
	 * @param url
	 *            图片下载路径
	 * @return
	 */
	private static Bitmap loadImage(Context context, String filePath, String url) {
		
		Bitmap bitmap = null;
		//首先从内存当中去加载。。。   
		if (sImageCache.containsKey(url)  ) {
			bitmap = sImageCache.get(url).get();
			if(bitmap != null ) Log.d("TAG","sImageCache get bitmap success");
		}
		//从本地文件当中去加载。。。     
		
		if (bitmap == null) {
			bitmap = getImageFormFile(filePath, context);
			if(bitmap!= null) Log.d("TAG","getImageFormFile success!");
		}
		//从网络当中去加载。。。最常见的
		if (bitmap == null) {
			Log.d("TAG","从网络当中去加载: " + url);
			bitmap = getBitmap(url);
			if (bitmap != null) {
				saveImage(bitmap, filePath);
			}
		}
		 
		//加载完之后也往内存中保存一份   但是仅当url不为空的时候
		if (bitmap != null && url != null && !("").equals(url) ) {
			sImageCache.put(url, new SoftReference<Bitmap>(bitmap));
			Log.d("TAG","保存到内存中url" + url);
		}
		if(bitmap != null)Log.i("TAG","从网络中正确加载到了图片！！");
		return bitmap;
	}
	
	private static Bitmap scaleBitmap(Bitmap bitmap, float scale) {
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizeBitmap;
	}
	
	
	
	/**
	 * 根据需求的宽和高以及图片实际的宽和高计算SampleSize
	 * 
	 * @param options
	 * @param width
	 * @param height
	 * @return
	 */
	public static int caculateInSampleSize(Options options, int reqWidth,
			int reqHeight){
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;
		if (width > reqWidth || height > reqHeight) {
			int widthRadio = Math.round(width * 1.0f / reqWidth);
			int heightRadio = Math.round(height * 1.0f / reqHeight);
			inSampleSize = Math.max(widthRadio, heightRadio);
		}
		return inSampleSize;
	}
	
	
	
	/**
	 * 根据ImageView获适当的压缩的宽和高
	 * 
	 * @param imageView
	 * @return
	 */
	public static ImageSize getImageViewSize(ImageView imageView)
	{
		ImageSize imageSize = new ImageSize();
		DisplayMetrics displayMetrics = imageView.getContext().getResources()
				.getDisplayMetrics();
		

		LayoutParams lp = imageView.getLayoutParams();

		int width = imageView.getWidth();// 获取imageview的实际宽度
		if (width <= 0)
		{
			width = lp.width;// 获取imageview在layout中声明的宽度
		}
		if (width <= 0)
		{
			 //width = imageView.getMaxWidth();// 检查最大值
			width = getImageViewFieldValue(imageView, "mMaxWidth");
		}
		if (width <= 0)
		{
			width = displayMetrics.widthPixels;
		}

		int height = imageView.getHeight();// 获取imageview的实际高度
		if (height <= 0)
		{
			height = lp.height;// 获取imageview在layout中声明的宽度
		}
		if (height <= 0)
		{
			height = getImageViewFieldValue(imageView, "mMaxHeight");// 检查最大值
		}
		if (height <= 0)
		{
			height = displayMetrics.heightPixels;
		}
		imageSize.width = width;
		imageSize.height = height;

		return imageSize;
	}
	
	
	/**
	 * 通过反射获取imageview的某个属性值
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName)
	{
		int value = 0;
		try
		{
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = field.getInt(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE)
			{
				value = fieldValue;
			}
		} catch (Exception e)
		{
		}
		return value;

	}
	
	 
	
	
	public static class ImageSize
	{
		int width;
		int height;
	}




	
}
