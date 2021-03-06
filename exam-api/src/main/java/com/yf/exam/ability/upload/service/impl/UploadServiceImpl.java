package com.yf.exam.ability.upload.service.impl;

import com.yf.exam.modules.Constant;
import com.yf.exam.ability.upload.config.UploadConfig;
import com.yf.exam.ability.upload.dto.UploadReqDTO;
import com.yf.exam.ability.upload.dto.UploadRespDTO;
import com.yf.exam.ability.upload.service.UploadService;
import com.yf.exam.ability.upload.utils.FileUtils;
import com.yf.exam.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 文件上传业务类
 * @author bool 
 * @date 2019-07-30 21:02
 */
@Slf4j
@Service
public class UploadServiceImpl implements UploadService {

    @Autowired
    private UploadConfig conf;

    @Override
    public UploadRespDTO upload(UploadReqDTO reqDTO) {


        // 文件内容
        MultipartFile file = reqDTO.getFile();

        // 上传文件夹
        String fileDir = conf.getDir();

        // 真实物理地址
        String fullPath;

        try {
            // 新文件
            String filePath = FileUtils.processPath(file);
            // 文件保存地址
            fullPath = fileDir + filePath;
            // 创建文件夹
            FileUtils.checkDir(fullPath);
            // 上传文件
            FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(fullPath));

            return this.generateResult(filePath);

        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException("文件上传失败："+e.getMessage());
        }
    }



    @Override
    public void download(HttpServletRequest request, HttpServletResponse response) {

        // 获取真实的文件路径
        String filePath = this.getRealPath(request.getRequestURI());

        try {
            FileUtils.writeRange(request, response, filePath);
        } catch (IOException e) {
            response.setStatus(404);
            log.error("预览文件失败" + e.getMessage());
        }
    }

    @Override
    public void delete(String url) {
        String reqUrl = url.substring(url.indexOf(Constant.FILE_PREFIX));
        String filePath = this.getRealPath(reqUrl);
        System.out.println("++++需要删除的文件路径为：" + filePath);
        try {
            FileUtils.deleteFile(filePath);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    /**
     * 构造返回
     * @param fileName
     * @return
     */
    private UploadRespDTO generateResult(String fileName) {

        //获取加速域名
        String domain = conf.getUrl();

        // 返回结果
        return new UploadRespDTO(domain + fileName);
    }


    /**
     * 获取真实物理文件地址
     * @param uri
     * @return
     */
    @Override
    public String getRealPath(String uri){

        String regx = Constant.FILE_PREFIX+"(.*)";

        // 查找全部变量
        Pattern pattern = Pattern.compile(regx);
        Matcher m = pattern.matcher(uri);
        if (m.find()) {
            String str = m.group(1);
            return conf.getDir() + str;
        }

        return null;
    }

}
