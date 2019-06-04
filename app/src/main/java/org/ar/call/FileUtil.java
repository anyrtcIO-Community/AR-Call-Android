package org.ar.call;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author vijay maurya
 * @version 2016-04-14 11:45:00
 */
public class FileUtil {

    public static String SDCard = Environment.getExternalStorageDirectory().getAbsolutePath();


    /**
     * 返回File 如果没有就创建
     *
     * @return directory
     */
    public static File getDirectory(String path) {
        File appDir = new File(path);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        return appDir;
    }

    public static File getDirectory(String path, String child) {
        File appDir = new File(path, child);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        return appDir;
    }

    /**
     * 删除文件夹
     *
     * @param sPath 路径
     * @return
     */
    public static boolean deleteDirectory(String sPath) {
        boolean flag = false;
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } // 删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建文件夹
     *
     * @param filePath
     * @return 1 :创建成功 0 :已存在 -1 :创建失败
     */
    public static int createFloder(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return 0;
        }
        if (file.mkdirs()) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * 删除文件
     *
     * @param sPath 路径
     * @return
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 读取文件
     *
     * @return
     */
    public static String readerFile(String filePath) {
        StringBuffer buffer = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis, "utf-8");// 文件编码Unicode,UTF-8,ASCII,GB2312,Big5
            Reader in = new BufferedReader(isr);
            int ch;
            while ((ch = in.read()) > -1) {
                buffer.append((char) ch);
            }
            in.close();
        } catch (Exception e) {
            return null;
        }
        return buffer.toString();
    }

    /**
     * 写入文件
     *
     * @param path
     * @param content
     * @return 1: 写入成功 0: 写入失败
     */
    public static int writeFile(String path, String content) {
        try {
            File f = new File(path);
            if (f.exists()) {
                f.delete();
            }
            if (f.createNewFile()) {
                FileOutputStream utput = new FileOutputStream(f);
                utput.write(content.getBytes());
                utput.close();
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }

    /**
     * 写入文件
     *
     * @param path
     * @param in
     * @return 1: 写入成功 0: 写入失败
     */
    public static int writeFile(String path, InputStream in) {
        try {
            if (in == null)
                return 0;
            File f = new File(path);
            if (f.exists()) {
                f.delete();
            }
            if (f.createNewFile()) {
                FileOutputStream utput = new FileOutputStream(f);
                byte[] buffer = new byte[1024];
                int count = -1;
                while ((count = in.read(buffer)) != -1) {
                    utput.write(buffer, 0, count);
                    utput.flush();
                }
                utput.close();
                in.close();
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }

    /**
     * 复制文件
     *
     * @param is
     * @param os
     * @return 1: 写入成功 0: 写入失败
     * @throws IOException
     */
    public static int copyStream(InputStream is, OutputStream os) {
        try {
            final int buffer_size = 1024;
            byte[] bytes = new byte[buffer_size];
            while (true) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
            }
            return 1;
        } catch (IOException e) {
            return 0;
        } finally {
            try {
                is.close();
                os.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 将assets下的文件复制到SD卡
     *
     * @param context
     * @param assetsPath
     * @param newPath
     */
    public static void copyFileFromAssets(Context context, String assetsPath, String newPath) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getAssets().open(assetsPath);
            File file = new File(newPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            fos = new FileOutputStream(file + "/" + assetsPath);
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取 buffer字节
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            fos.flush();// 刷新缓冲区
            is.close();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            String message = e.getMessage();
            e.printStackTrace();
        }
    }

    /**
     * 读取序列化对象
     *
     * @param filePath
     * @return
     */
    public static Object readerObject(String filePath) {
        Object oo;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream objectIn = new ObjectInputStream(fis);
            oo = objectIn.readObject();
            objectIn.close();
            fis.close();
        } catch (Exception e) {
            return null;
        }
        return oo;
    }

    /**
     * 写入序列化对象
     *
     * @param path
     * @param object
     * @return
     */
    public static int writeObject(String path, Object object) {
        try {
            File f = new File(path);
            if (f.exists()) {
                f.delete();
            }
            if (f.createNewFile()) {
                FileOutputStream utput = new FileOutputStream(f);
                ObjectOutputStream objOut = new ObjectOutputStream(utput);
                objOut.writeObject(object);
                objOut.close();
                utput.close();
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }

    /**
     * 解压
     *
     * @param rootPath 解压的根目录
     * @param fileIn   要解压的ZIP、rar文件流
     * @throws Exception
     */
    public static void unzip(String rootPath, InputStream fileIn) {

        try {
            /* 创建根文件夹 */
            File rootFile = new File(rootPath);
            rootFile.mkdir();

            rootFile = new File(rootPath + "resource/");
            rootFile.mkdir();

            ZipInputStream in = new ZipInputStream(new BufferedInputStream(fileIn, 2048));

            ZipEntry entry = null;// 读取的压缩条目

			/* 解压缩开始 */
            while ((entry = in.getNextEntry()) != null) {
                decompression(entry, rootPath, in);// 解压
            }
            in.close();// 关闭输入流
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void decompression(ZipEntry entry, String rootPath, ZipInputStream in) throws Exception {
		/* 如果是文件夹 */
        if ((entry.isDirectory() || -1 == entry.getName().lastIndexOf("."))) {
            File file = new File(rootPath + entry.getName().substring(0, entry.getName().length() - 1));
            file.mkdir();
        } else {
            File file = new File(rootPath + entry.getName());
            if (!file.exists())
                file.createNewFile();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file), 2048);
            int b;
            while ((b = in.read()) != -1) {
                bos.write(b);
            }
            bos.close();
        }
    }

    /**
     * Bitmap 转换成 byte[]
     *
     * @param bm
     * @return
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 保存图片
     *
     * @param path
     * @param bitmap
     */
    public static void saveBitmap(String path, Bitmap bitmap) {
        try {
            File f = new File(path);
            if (f.exists())
                f.delete();
            f.createNewFile();
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] getFileContent(String fileName) {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(fileName);
            int length = fin.available();
            byte[] bytes = new byte[length];
            fin.read(bytes);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static final String FILES_PATH = "Compressor";
    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private FileUtil() {

    }

    public static File from(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = getFileName(context, uri);
        String[] splitName = splitFileName(fileName);
        File tempFile = File.createTempFile(splitName[0], splitName[1]);
        tempFile = rename(tempFile, fileName);
        tempFile.deleteOnExit();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            copy(inputStream, out);
            inputStream.close();
        }

        if (out != null) {
            out.close();
        }
        return tempFile;
    }

    static String[] splitFileName(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

    static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String realPath = cursor.getString(index);
            cursor.close();
            return realPath;
        }
    }

    static File rename(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        if (!newFile.equals(file)) {
            if (newFile.exists()) {
                if (newFile.delete()) {
                    Log.d("FileUtil", "Delete old " + newName + " file");
                }
            }
            if (file.renameTo(newFile)) {
                Log.d("FileUtil", "Rename file to " + newName);
            }
        }
        return newFile;
    }

    static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
    }

    static long copyLarge(InputStream input, OutputStream output, byte[] buffer)
            throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * 得到SD卡根目录.
     */
    public static File getRootPath(Context context) {
        if (FileUtil.sdCardIsAvailable()) {
            return Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
        } else {
            return context.getFilesDir();
        }
    }

    /**
     * SD卡是否可用.
     */
    public static boolean sdCardIsAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sd = new File(Environment.getExternalStorageDirectory().getPath());
            return sd.canWrite();
        } else
            return false;
    }

    public static String getPrintSize(double size) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        DecimalFormat df = new DecimalFormat("######0.00");
        if (size < 1024) {
            return String.valueOf(df.format(size)) + "B";
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(df.format(size)) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            return String.valueOf(df.format(size))+ "MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size  / 1024;
            return String.valueOf(df.format(size)) + "GB";
        }
    }


    public Bitmap getBitmap(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) iconUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bm = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

}
