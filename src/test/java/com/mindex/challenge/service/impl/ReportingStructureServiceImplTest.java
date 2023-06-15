package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {
    private String reportingStructureUrl;
    private String employeeUrl;

    @Autowired
    private ReportingStructureService reportingStructureService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        reportingStructureUrl = "http://localhost:" + port + "/report/{id}";
    }

    @Test
    public void testGetReportingStructureWithReporters() {
        // Prepare test data for the employer with reporters
        String testEmployee = "03aa1462-ffa9-4978-901b-7c001562cf6f";

        // Get a report for the employee to see if reporter count works
        ReportingStructure report = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, testEmployee).getBody();
        assertNotNull(report);
        assertEquals(2, report.getNumberOfReports());
    }

    @Test
    public void testGetReportingStructureWithNestedReporters() {
        // Prepare test data for the employer with reporters, which may also have reporters
        String testEmployee = "16a596ae-edd3-4847-99fe-c4518e82c86f";

        // Get a report for the employee to see if recursive call works
        ReportingStructure report = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, testEmployee).getBody();
        assertNotNull(report);
        assertEquals(4, report.getNumberOfReports());
    }

    @Test
    public void testGetReportingStructureWithNewEmployees() {
        // Create new data for a new employee with no reporters
        // https://en.wikipedia.org/wiki/Fifth_Beatle#Stuart_Sutcliffe
        Employee firstTestEmployee = new Employee();
        firstTestEmployee.setFirstName("Stuart");
        firstTestEmployee.setLastName("Sutcliffe");
        firstTestEmployee.setDepartment("Engineering");
        firstTestEmployee.setPosition("Developer I");

        Employee firstCreatedEmployee = restTemplate.postForEntity(employeeUrl, firstTestEmployee, Employee.class).getBody();

        assertNotNull(firstCreatedEmployee.getEmployeeId());

        // Get a report for the first employee to see if the number of reports is 0
        ReportingStructure firstTestReport = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, firstCreatedEmployee.getEmployeeId()).getBody();

        assertNotNull(firstTestReport);
        assertEquals(0, firstTestReport.getNumberOfReports());

        // Create new data for another employee which will receive reports from the previous employee
        // https://en.wikipedia.org/wiki/Fifth_Beatle#Chas_Newby
        Employee secondTestEmployee = new Employee();
        secondTestEmployee.setFirstName("Chas");
        secondTestEmployee.setLastName("Newby");
        secondTestEmployee.setDepartment("Engineering");
        secondTestEmployee.setPosition("Developer II");

        // Before the request, add the first employee as a reporter to the second employee
        secondTestEmployee.setDirectReports(Collections.singletonList(firstCreatedEmployee));

        Employee secondTestResult = restTemplate.postForEntity(employeeUrl, secondTestEmployee, Employee.class).getBody();

        assertNotNull(secondTestResult.getEmployeeId());

        // Get a second report for the second employee to see if the number is 1
        ReportingStructure secondTestReport = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, secondTestResult.getEmployeeId()).getBody();

        assertNotNull(secondTestReport);
        assertEquals(1, secondTestReport.getNumberOfReports());
    }
}
