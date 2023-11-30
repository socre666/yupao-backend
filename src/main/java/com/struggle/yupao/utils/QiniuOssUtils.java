package com.struggle.yupao.utils;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
 
/**
 * 七牛OSS管理工具
 */
public class QiniuOssUtils {
 
    /**
     * 存储空间名
     */
    @Value("${qiniu.client.BUCKET}")
    private String BUCKET;
    /**
     * accessKey和secretKey
     */
    @Value("${qiniu.client.ACCESS_KEY}")
    private String ACCESS_KEY;
    @Value("${qiniu.client.SECRET_KEY}")
    private String SECRET_KEY;
    /**
     * 外网访问地址(内置域名有效期只有30天)
     */
    @Value("${qiniu.client.BASE_URL}")
    private String BASE_URL;
 
    /**
     * 上传管理器
     */
    private UploadManager upload;
    /**
     * 桶管理器（存储空间管理器）
     */
    private BucketManager bucket;
 
    public QiniuOssUtils() {
        //创建配置对象
        Configuration cfg = new Configuration(Zone.zone2());
        //创建上传管理器
        upload = new UploadManager(cfg);
        //创建存储空间管理器
        bucket = new BucketManager(getAuth(), cfg);
    }
 
    /**
     * 返回认证器（包含的访问密钥）
     *
     * @return
     */
    private Auth getAuth() {
        return Auth.create(ACCESS_KEY, SECRET_KEY);
    }
 
    /**
     * 获取令牌对象（服务器返回的授权信息）
     *
     * @return
     */
    private String getToken() {
        return getAuth().uploadToken(BUCKET);
    }
 
    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    public String upload(File file, String key) {
        try {
            return upload(new FileInputStream(file), key);
        } catch (FileNotFoundException | QiniuException e) {
            e.printStackTrace();
        }
        return null;
    }
 
    /**
     * 上传文件
     *
     * @param is
     * @param key
     * @return
     * @throws QiniuException
     */
    public String upload(InputStream is, String key) throws QiniuException {
        //上传流
        Response response = upload.put(is, key, getToken(), null, null);
        //解析返回结果
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        //将文件的访问地址返回
        return BASE_URL + putRet.key;
    }
 
    /**
     * 删除文件
     *
     * @param key
     */
    public void delete(String key) {
        try {
            bucket.delete(BUCKET, key);
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }
}