package com.subject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.subject.domain.entity.Download;

public interface DownloadService extends IService<Download> {
    boolean addDownload(Download download);

    boolean updateDownload(Download download);

    boolean removeDownload(Integer downloadId);

    boolean incrementCount(Integer downloadId);
}
