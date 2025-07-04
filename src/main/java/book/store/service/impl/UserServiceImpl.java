package book.store.service.impl;

import book.store.dto.user.UserRegistrationRequestDto;
import book.store.dto.user.UserResponseDto;
import book.store.exception.RegistrationException;
import book.store.mapper.UserMapper;
import book.store.model.Role;
import book.store.model.RoleName;
import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.repository.role.RoleRepository;
import book.store.repository.shoppingcart.ShoppingCartRepository;
import book.store.repository.user.UserRepository;
import book.store.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder getPasswordEncoder;
    private final RoleRepository roleRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("Email already in use: " + requestDto.getEmail());
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(getPasswordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: "
                        + RoleName.ROLE_USER));
        user.setRoles(Set.of(userRole));
        User savedUser = userRepository.save(user);
        ShoppingCart cart = new ShoppingCart();
        cart.setUser(savedUser);
        shoppingCartRepository.save(cart);
        return userMapper.toDto(savedUser);
    }

    @Override
    public Long getCurrentUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }
}
