package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.entity.File;
import com.udacity.jwdnd.course1.cloudstorage.entity.model.FileInfo;
import org.apache.ibatis.annotations.*;

import java.sql.Blob;
import java.util.List;

@Mapper
public interface FileMapper {
    @Select("SELECT * FROM FILES WHERE userid = #{userId}")
    List<FileInfo> getAllFilesUserId(Integer userId);

    @Select("SELECT * FROM FILES WHERE fileid = #{fileId}")
    File getOneFileInfo(Integer fileId);

    @Select("SELECT fileid FROM FILES WHERE ( filename, contenttype, filesize, userid) = ( #{fileName}, #{contentType}, #{fileSize}, #{userId})")
    Integer getFileId(FileInfo fileInfoEntity);

    @Insert("INSERT INTO FILES ( filename, contenttype, filesize, userid) VALUES( #{fileName}, #{contentType}, #{fileSize}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    void insert(FileInfo file);

    @Update("UPDATE FILES SET filedata= #{fileData} WHERE fileId = #{fileId}")
    void updateFileData(Blob fileData, int fileId);

    @Delete("DELETE FROM FILES WHERE fileId = #{fileId}")
    void deleteFile(int fileId);

}
