package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.entity.File;
import com.udacity.jwdnd.course1.cloudstorage.entity.model.FileData;
import com.udacity.jwdnd.course1.cloudstorage.entity.model.FileInfo;
import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileService {
    private final FileMapper fileMapper;
    private List<FileInfo> fileList;


    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public File getFileById(int fileId) {
        return fileMapper.getOneFileInfo(fileId);
    }

    public List<FileInfo> getFileList(int userId) {

        if (fileList == null || fileList.isEmpty()) {
            setFileList(userId);
            return fileList;
        }

        AtomicInteger userid = new AtomicInteger();
        Stream<FileInfo> peek = this.fileList.stream().peek(fileInfo -> userid.set(fileInfo.getUserId()));
        if (userid.get() == userId) {
            return fileList;
        } else {
            setFileList(userId);
        }
        return fileList;
    }

    public void setFileList(int userId) {
        fileList = fileMapper.getAllFilesUserId(userId);
    }

    public void uploadFile(MultipartFile file, int userId) throws SQLException, IOException, FileUploadException {

        if (file.isEmpty()) {
            throw new FileUploadException("6");
        }
        getFileList(userId);

        List<String> fileNamesForUser = new ArrayList<>();
        for (FileInfo fileInfo : fileList) {
            fileNamesForUser.add(fileInfo.getFileName());
        }

        if (fileNamesForUser.contains(file.getOriginalFilename())) {
            throw new FileAlreadyExistsException("2");
        }

        FileInfo fileInfoHolder = getFileInfo(file, userId);
        FileData fileDataHolder = getFileData(file);

        fileMapper.insert(fileInfoHolder);

        int fileId = fileMapper.getFileId(fileInfoHolder);


        fileMapper.updateFileData(fileDataHolder.getFileData(), fileId);
    }

    public FileInfo getFileInfo(MultipartFile file, int userId) throws IOException, SQLException {
        return new FileInfo(null, file.getOriginalFilename(), file.getContentType(), file.getSize(), userId);
    }

    public FileData getFileData(MultipartFile file) throws IOException, SQLException {
        return new FileData(new SerialBlob(file.getBytes()));
    }

    public void deleteFile(int fileId) {
        fileMapper.deleteFile(fileId);
    }
}
