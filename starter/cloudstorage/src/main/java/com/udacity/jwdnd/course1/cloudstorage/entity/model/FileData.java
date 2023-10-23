package com.udacity.jwdnd.course1.cloudstorage.entity.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;

@AllArgsConstructor
@Setter
@Getter
public class FileData {
    private Blob fileData;

}
