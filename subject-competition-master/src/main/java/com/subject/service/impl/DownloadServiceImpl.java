package com.subject.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.subject.mapper.DownloadMapper;
import com.subject.domain.entity.Download;
import com.subject.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DownloadServiceImpl extends ServiceImpl<DownloadMapper, Download> implements DownloadService {

    @Autowired
    private DownloadMapper downloadMapper;

    @Override
    public boolean addDownload(Download download) {
        if (download == null || download.getTitle() == null || download.getFileUrl() == null) {
            return false;
        }
        int rows = downloadMapper.insertDownload(download);
        return rows > 0;
    }

    @Override
    public boolean updateDownload(Download download) {
        if (download == null || download.getDownloadId() == null) {
            return false;
        }
        int rows = downloadMapper.updateDownloadById(download);
        return rows > 0;
    }

    @Override
    public boolean removeDownload(Integer downloadId) {
        if (downloadId == null || downloadId <= 0) {
            return false;
        }
        int rows = downloadMapper.deleteDownloadById(downloadId);
        return rows > 0;
    }

    @Override
    public boolean incrementCount(Integer downloadId) {
        if (downloadId == null || downloadId <= 0) {
            return false;
        }
        int rows = downloadMapper.incrementDownloadCount(downloadId);
        return rows > 0;
    }
}
