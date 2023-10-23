package com.udacity.jwdnd.course1.cloudstorage.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;

@AllArgsConstructor
@Getter
@Setter
public class File {
    private Integer fileId;
    private String fileName;
    private String contentType;
    private String fileSize;
    private Blob fileData;
    private Integer userId;
}
