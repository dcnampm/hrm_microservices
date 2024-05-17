package com.nampd.service.impl;

import com.nampd.mapper.UserMapper;
import com.nampd.model.dto.UserCredentialsDto;
import com.nampd.model.dto.UserDto;
import com.nampd.model.entity.Department;
import com.nampd.client.RoleVO;
import com.nampd.model.entity.User;
import com.nampd.repository.DepartmentRepository;
import com.nampd.repository.UserRepository;
import com.nampd.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RestTemplate restTemplate;

    public UserServiceImpl(UserRepository userRepository, DepartmentRepository departmentRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public User addUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Username already exists");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return
                userRepository.findAll()
                        .stream()
                        .map(userMapper::toUserDto)
                        .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserCredentialsDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userMapper.toUserCredentialsDto(user);
    }

    @Override
    public List<UserDto> getUserByFirstName(String firstName) {
        List<User> foundUsers = userRepository.findByFirstName(firstName);

        if (foundUsers != null && !foundUsers.isEmpty()) {
            return foundUsers.stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList()
            );
        } else {
            throw new NoSuchElementException("Not found users with first name: " + firstName);
        }
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setGender(updatedUser.getGender());
        existingUser.setBirthday(updatedUser.getBirthday());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());

        String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
        existingUser.setPassword(encodedPassword);

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("User not found");
        }
    }


    @Override
    public void assignRoleAndDepartmentToUser(String username, String role, Long departmentId) {
        RoleVO roleVO = restTemplate.getForObject("/roles/{roleName}", RoleVO.class, role);

        Optional<User> foundUser = userRepository.findByEmail(username);
        Department department = departmentRepository.findById(departmentId).orElse(null);

        if (foundUser.isPresent() && roleVO != null && department != null) {
            if (roleVO.getDepartment().equals(department.getName())) {
                User user = foundUser.get();
                user.setRole(roleVO.getName());
                user.setDepartment(department);
                userRepository.save(user);
            } else {
                throw new IllegalArgumentException("Role does not belong to the specified department");
            }
        } else {
            throw new NoSuchElementException("User, role, or department not found");
        }
    }

    //remove user from department
    @Override
    public void removeUserFromDepartmentAndUnassignRole(String username) {
        Optional<User> foundUser = userRepository.findByEmail(username);

        if (foundUser.isPresent()) {
            User user = foundUser.get();
            user.setDepartment(null);
            user.setRole(null);
            userRepository.save(user);
        } else {
            throw new NoSuchElementException("User not found");
        }
    }

}
