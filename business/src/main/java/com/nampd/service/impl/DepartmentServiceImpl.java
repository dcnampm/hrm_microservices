package com.nampd.service.impl;

import com.nampd.mapper.DepartmentMapper;
import com.nampd.model.dto.DepartmentDto;
import com.nampd.model.entity.Department;
import com.nampd.client.RoleVO;
import com.nampd.repository.DepartmentRepository;
import com.nampd.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    @Override
    public Department createDepartment(Department department) {
        if (departmentRepository.existsByName(department.getName())) {
            throw new IllegalArgumentException("Department already exists");
        }
        return departmentRepository.save(department);
    }

    @Override
//    @Cacheable(value = "departments")
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(departmentMapper::toDepartmentDto)
                .collect(Collectors.toList());
    }

    @Override
//    @Cacheable(value = "departments", key = "#departmentId")
    public DepartmentDto getDepartmentById(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
        return departmentMapper.toDepartmentDto(department);
    }

    @Override
//    @Cacheable(value = "departments", key = "#alias")
    public DepartmentDto getDepartmentByAlias(String alias) {
        Department department = departmentRepository.findByAlias(alias)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
        return departmentMapper.toDepartmentDto(department);
    }

    @Override
//    @CachePut(value = "departments", key = "#departmentId")
    public DepartmentDto updateDepartment(Long departmentId, Department department) {
        Department existingDepartment = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
        existingDepartment.setName(department.getName());
        existingDepartment.setAlias(department.getAlias());

        Department updatedDepartment = departmentRepository.save(existingDepartment);
        return departmentMapper.toDepartmentDto(updatedDepartment);
    }

    @Override
//    @CacheEvict(value = "departments", allEntries = true)
    public void deleteDepartment(Long departmentId) {
        Optional<Department> existingDepartment = departmentRepository.findById(departmentId);
        if (existingDepartment.isPresent()) {
            departmentRepository.deleteById(departmentId);
        } else {
            throw new NoSuchElementException("Department not found with id: " + departmentId);
        }
    }

}
