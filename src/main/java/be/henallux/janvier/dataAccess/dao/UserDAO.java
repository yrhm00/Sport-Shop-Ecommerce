package be.henallux.janvier.dataAccess.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import be.henallux.janvier.dataAccess.entity.AuthorityEntity;
import be.henallux.janvier.dataAccess.entity.UserEntity;
import be.henallux.janvier.dataAccess.repository.AuthorityRepository;
import be.henallux.janvier.dataAccess.repository.UserRepository;
import be.henallux.janvier.dataAccess.util.ProviderConverter;
import be.henallux.janvier.model.User;

@Service
@Transactional
public class UserDAO implements UserDataAccess {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final ProviderConverter converter;

    @Autowired
    public UserDAO(UserRepository userRepository, AuthorityRepository authorityRepository, ProviderConverter converter) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.converter = converter;
    }

    @Override
    public User save(User user) {
        UserEntity entity = converter.userModelToEntity(user);
        entity = userRepository.save(entity);
        
        // Sauvegarder les autorités si présentes (uniquement si elles n'existent pas déjà)
        if (user.getAuthorities() != null && !user.getAuthorities().isEmpty()) {
            for (org.springframework.security.core.GrantedAuthority grantedAuthority : user.getAuthorities()) {
                // Vérifier si l'autorité existe déjà
                if (!authorityRepository.existsByUsernameAndAuthority(entity.getUsername(), grantedAuthority.getAuthority())) {
                    AuthorityEntity authEntity = new AuthorityEntity(entity.getUsername(), grantedAuthority.getAuthority());
                    authorityRepository.save(authEntity);
                }
            }
        }
        
        return converter.userEntityToModel(entity);
    }

    @Override
    public User findByUsername(String username) {
        UserEntity entity = userRepository.findByUsername(username);
        return converter.userEntityToModel(entity);
    }

    @Override
    public User findByEmail(String email) {
        return converter.userEntityToModel(userRepository.findByEmail(email));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}

