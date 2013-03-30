package cstdr.ningningcat.util;

import java.io.File;

/**
 * 文件工具
 * @author cstdingran@gmail.com
 */
public class FileUtil {

    /**
     * 删除文件
     * @param path
     */
    public static void deleteFile(File file) {
        if(file.exists()) {
            if(file.isDirectory()) {
                for(File f: file.listFiles()) {
                    deleteFile(f);
                }
            } else {
                file.delete();
            }
        }
    }

    /**
     * 得到文件/文件夹的大小
     * @param file
     */
    public static long getFileLength(File file) {
        if(file.exists()) {
            if(file.isDirectory()) {
                long length=0;
                for(File f: file.listFiles()) {
                    length=length + getFileLength(f);
                }
                return length;
            } else {
                return file.length();
            }
        }
        return 0;
    }
}
