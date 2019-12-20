package com.vinyl.service;

import com.vinyl.model.Status;
import com.vinyl.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusServiceImpl implements StatusService{
    @Autowired
    private StatusRepository statusRepository;

    @Override
    public Status findById(Long id){
        return statusRepository.getOne(id);
    }
}
