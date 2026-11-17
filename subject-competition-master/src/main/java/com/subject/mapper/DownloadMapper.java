package com.subject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.subject.domain.entity.Download;
import org.apache.ibatis.annotations.Param;

public interface DownloadMapper extends BaseMapper<Download> {
    int insertDownload(Download download);

    int updateDownloadById(Download download);

    int deleteDownloadById(@Param("downloadId") Integer downloadId);

    int incrementDownloadCount(@Param("downloadId") Integer downloadId);
}
