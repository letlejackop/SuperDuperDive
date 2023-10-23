package com.udacity.jwdnd.course1.cloudstorage.entity.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class FileInfo {
    private Integer fileId;
    private String fileName;
    private String contentType;
    private long fileSize;
    private Integer userId;
}
