package recipes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import recipes.model.Recipe;
import recipes.model.User;
import recipes.repositories.RecipeRepository;
import recipes.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RecipeRepository recipeRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public ResponseEntity<Long> saveUser(User user){

        Optional<User> existingUser = findUserByEmail(user.getEmail());

        if (existingUser.isPresent()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setEmail(user.getEmail());

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        newUser.setPassword(encodedPassword);

        newUser.setRoles(Set.of("USER"));

        List<Recipe> recipes = recipeRepository.findByUserId(user.getId());
        newUser.setRecipes(recipes);

        userRepository.save(newUser);

        return new ResponseEntity<>(newUser.getId(),HttpStatus.OK);

    }
}
