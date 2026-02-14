package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.response.EmployeeShorterResponseDto;
import com.capestone.hrms_backend.dto.response.OrgChartRespnseDto;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.service.IHierarchyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HierarchyServiceImpl implements IHierarchyService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<EmployeeShorterResponseDto> getMyDirectTeam(Long empId) {
        return employeeRepository.findByManagerId(empId).stream().map(m->modelMapper.map(m,EmployeeShorterResponseDto.class)).toList();
    }

    @Override
    public List<EmployeeShorterResponseDto> getMyFullTeam(Long empId) {
        Set<Employee> visited = new HashSet<>();
        Queue<Long> queue = new LinkedList<>();

        while(!queue.isEmpty()){
            Long manager = queue.poll();

            List<Employee> subs = employeeRepository.findByManagerId(manager);
            for(Employee e : subs){
                if(visited.add(e)){
                    queue.add(e.getId());
                }
            }
        }
        return visited.stream().map(m->modelMapper.map(m,EmployeeShorterResponseDto.class)).toList();
    }

    @Override
    public OrgChartRespnseDto getOrgChart(Long empId) {
        Employee root = employeeRepository.findById(empId).orElseThrow(()->new ResourceNotFoundException("Employee not found."));
        while(root.getManager()!=null){
            root=root.getManager();
        }

        return generateTree(root);
    }

    @Override
    public void allocateManager(Long empId, Long managerId) {
        Employee emp = employeeRepository.findById(empId).orElseThrow(()->new ResourceNotFoundException("Employee doesn't exist"));
        Employee mgr = employeeRepository.findById(managerId).orElseThrow(()->new ResourceNotFoundException("Manager doesn't exist"));

        //If both found, allocate manager
        emp.setManager(mgr);
        employeeRepository.save(emp);
    }

    public OrgChartRespnseDto generateTree(Employee emp){
        OrgChartRespnseDto node = modelMapper.map(emp,OrgChartRespnseDto.class);
        node.setName(emp.getFirstName()+" "+emp.getLastName());

        List<Employee> subordinates = employeeRepository.findByManagerId(emp.getId());
        for(Employee s : subordinates){
            node.getSubordinates().add(generateTree(s));
        }
        return node;
    }
}
