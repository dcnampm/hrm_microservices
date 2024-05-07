package com.nampd.service.impl;

import com.nampd.mapper.UserMapper;
import com.nampd.model.dto.UserCredentialsDto;
import com.nampd.model.dto.UserDto;
import com.nampd.model.entity.Department;
import com.nampd.model.entity.Role;
import com.nampd.model.entity.User;
import com.nampd.repository.DepartmentRepository;
import com.nampd.repository.RoleRepository;
import com.nampd.repository.UserRepository;
import com.nampd.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, DepartmentRepository departmentRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
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
    public void assignRoleAndDepartmentToUser(String username, String roleName, Long departmentId) {
        Optional<User> foundUser = userRepository.findByEmail(username);
        Role role = roleRepository.findByName(roleName);
        Department department = departmentRepository.findById(departmentId).orElse(null);

        if (foundUser.isPresent() && role != null && department != null) {
            User user = foundUser.get();
            user.setRole(role);
            user.setDepartment(department);
            userRepository.save(user);
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
