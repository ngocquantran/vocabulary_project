package com.example.myvocab.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class FileUploadUtil {



    //    Lấy extension file
//    image.png,avatar.jpg
    public static String getExtensionFile(String fileName){
        int lastIndexOf=fileName.lastIndexOf(".");
        if (lastIndexOf==-1){
            return "";
        }
        return fileName.substring(lastIndexOf+1);
    }


    //        Kiểm tra extension có nằm trong danh sách được upload không
    public static boolean checkFileExtension(String fileExtension){
        List<String> extensions= Arrays.asList("png","jpg","jpeg","mp3");
        return extensions.contains(fileExtension);
    }
}
