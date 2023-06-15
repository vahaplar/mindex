package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompensationServiceImpl implements CompensationService {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Compensation create (Compensation compensation) {
        LOG.debug("Creating compensation for the employee with id [{}]", compensation.getEmployee().getEmployeeId());

        compensationRepository.insert(compensation);

       return compensation;
    }

    @Override
    public Compensation read (String employeeId) {
        LOG.debug("Reading compensation of the employee with id [{}]", employeeId);

        Compensation compensation = compensationRepository.findByEmployeeEmployeeId(employeeId);

        if (compensation == null) {
            throw new RuntimeException("Employee not found with id: " + employeeId);
        }

        return compensation;
    }
}