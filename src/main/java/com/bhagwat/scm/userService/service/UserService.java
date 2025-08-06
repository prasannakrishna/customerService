package com.bhagwat.scm.userService.service;

import com.bhagwat.scm.userService.dto.AppAccessLink;
import com.bhagwat.scm.userService.dto.UserCreationRequest;
import com.bhagwat.scm.userService.dto.UserCreationResponse;
import com.bhagwat.scm.userService.dto.UserLoginResponse;
import com.bhagwat.scm.userService.entity.ApplicationSubscription;
import com.bhagwat.scm.userService.entity.Org;
import com.bhagwat.scm.userService.entity.Role;
import com.bhagwat.scm.userService.entity.User;
import com.bhagwat.scm.userService.repository.ApplicationSubscriptionRepository;
import com.bhagwat.scm.userService.repository.OrgRepository;
import com.bhagwat.scm.userService.repository.RoleRepository;
import com.bhagwat.scm.userService.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final OrgRepository orgRepository;
    private final RoleRepository roleRepository;
    private final ApplicationSubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, OrgRepository orgRepository, RoleRepository roleRepository,
                       ApplicationSubscriptionRepository subscriptionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.orgRepository = orgRepository;
        this.roleRepository = roleRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserLoginResponse validateUserAndGetAppAccess(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return new UserLoginResponse("Invalid username or password.", false, null);
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return new UserLoginResponse("Invalid username or password.", false, null);
        }

        if (!user.getIsActive()) {
            return new UserLoginResponse("User account is inactive.", false, null);
        }

        Org org = user.getOrg();
        if (org == null || !org.getIsActive()) {
            return new UserLoginResponse("User's organization is inactive or not found.", false, null);
        }

        List<ApplicationSubscription> activeSubscriptions = subscriptionRepository
                .findByOrgOrgIdAndIsActiveTrueAndRenewalDateAfter(org.getOrgId(), LocalDate.now());

        if (activeSubscriptions.isEmpty()) {
            return new UserLoginResponse("No active subscriptions found for your organization.", false, null);
        }

        List<AppAccessLink> appAccessLinks = activeSubscriptions.stream()
                .map(sub -> new AppAccessLink(
                        sub.getAppId().name(),
                        sub.getAppId().getBaseUrl() + org.getOrgId(),
                        org.getOrgId()
                ))
                .collect(Collectors.toList());

        return new UserLoginResponse("Login successful. Access links provided.", true, appAccessLinks);
    }

    @Transactional
    public UserCreationResponse createUser(UserCreationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return new UserCreationResponse("Username already exists.", false, null);
        }

        Org org = orgRepository.findById(request.getOrgId())
                .orElse(null);

        if (org == null) {
            return new UserCreationResponse("Organization not found.", false, null);
        }
        if (!org.getIsActive()) {
            return new UserCreationResponse("Organization is inactive.", false, null);
        }

        Role role = roleRepository.findByRoleName(request.getRoleName())
                .orElse(null);

        if (role == null) {
            return new UserCreationResponse("Role not found.", false, null);
        }

        List<ApplicationSubscription> activeSubscriptions = subscriptionRepository
                .findByOrgOrgIdAndIsActiveTrueAndRenewalDateAfter(org.getOrgId(), LocalDate.now());

        if (activeSubscriptions.isEmpty()) {
            return new UserCreationResponse("Organization has no active subscriptions to create users.", false, null);
        }

        int maxAllowedUsers = activeSubscriptions.stream()
                .mapToInt(sub -> sub.getPlan().getMaxUsers())
                .max()
                .orElse(0);

        long currentUserCount = userRepository.countByOrgOrgIdAndIsActiveTrue(org.getOrgId());

        if (currentUserCount >= maxAllowedUsers) {
            return new UserCreationResponse(
                    String.format("User creation blocked: Organization has reached its user limit of %d. Current users: %d.",
                            maxAllowedUsers, currentUserCount),
                    false, null);
        }

        User newUser = new User();
        newUser.setUserId(UUID.randomUUID().toString());
        newUser.setUsername(request.getUsername());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setOrg(org);
        newUser.setRole(role);
        newUser.setIsActive(true);

        userRepository.save(newUser);

        return new UserCreationResponse("User created successfully.", true, newUser.getUserId());
    }
}