package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {
    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ReportingStructure getReportingStructure (String employeeId) {
        LOG.debug("Creating reporting structure for the employee with id [{}]", employeeId);

        // Find the employee for a reporting check
        Employee employee = employeeRepository.findByEmployeeId(employeeId);
        // Check if the employee exists
        if (employee == null) {
            throw new RuntimeException("Employee not found with id: " + employeeId);
        }
        // Count the number of reports recursively and create a reporting structure with the present data
        int numberOfReports = countReporters(employee);
        return new ReportingStructure(employee, numberOfReports);
    }

    // Recursive method to count directly or indirectly reporting employees
    private int countReporters(Employee employee) {
        int count = 0;
        // Check if the employee exists and has any reporters
        if (employee != null && employee.getDirectReports() != null) {
            // For each reporter
            for (Employee directReport : employee.getDirectReports()) {
                // Retrieve the Employee data in case of other possible reporters
                Employee reporter = employeeRepository.findByEmployeeId(directReport.getEmployeeId());
                if (reporter != null) {
                    // Recursively try counting other possible reporters while increasing the count of reporters
                    count += countReporters(reporter) + 1;
                }
            }
        }
        // Return the final reporter count
        return count;
    }
}
