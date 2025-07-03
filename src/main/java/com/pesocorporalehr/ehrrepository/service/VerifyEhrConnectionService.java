package com.pesocorporalehr.ehrrepository.service;

import com.pesocorporalehr.ehrrepository.service.exception.EhrbaseConnectionException;

public interface VerifyEhrConnectionService {
    boolean verifyEhrconn() throws EhrbaseConnectionException;
}
