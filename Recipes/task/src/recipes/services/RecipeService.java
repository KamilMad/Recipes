package recipes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import recipes.errors.RecipeNotFoundException;
import recipes.model.Recipe;
import recipes.model.User;
import recipes.payload.RecipeDto;
import recipes.repositories.RecipeRepository;
import recipes.repositories.UserRepository;
import recipes.security.CustomUserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

   public Long addRecipe(Recipe recipe) {

        Recipe newRecipe = new Recipe();

        newRecipe.setId(recipe.getId());
        newRecipe.setName(recipe.getName());
        newRecipe.setCategory(recipe.getCategory());
        newRecipe.setDescription(recipe.getDescription());
        newRecipe.setIngredients(recipe.getIngredients());
        newRecipe.setDirections(recipe.getDirections());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Suche a user not found"));

        newRecipe.setUser(user);

        recipeRepository.save(newRecipe);

        user.getRecipes().add(newRecipe);

        return newRecipe.getId();
    }

    public RecipeDto getRecipeById(Long id){
        Recipe recipe =  recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException("No such a recipe"));

        return fromDto(recipe);


    }

    public void deleteById(Long id) {

        Recipe recipe= recipeRepository.findById(id).orElseThrow(() -> new RecipeNotFoundException("No such a recipe"));

        if (!isAuthor(recipe)) {
            throw new AccessDeniedException("Access denied");
        }

        recipeRepository.deleteById(id);
    }

    public void updateById(Long id, Recipe recipe){

        Recipe existingRecipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException("No such a recipe"));


        if (!isAuthor(existingRecipe)) {
            throw new AccessDeniedException("Access denied");
        }

        existingRecipe.setName(recipe.getName());
        existingRecipe.setDate(LocalDateTime.now());
        existingRecipe.setIngredients(recipe.getIngredients());
        existingRecipe.setDescription(recipe.getDescription());
        existingRecipe.setCategory(recipe.getCategory());
        existingRecipe.setDirections(recipe.getDirections());

        recipeRepository.save(existingRecipe);
    }

    public List<RecipeDto> findAll(String category, String name){

        if (category == null && name == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        List<Recipe> recipes;
        if (category != null){
            recipes = recipeRepository.findByCategoryIgnoreCaseOrderByDateDesc(category);
        }
        else{
            recipes = recipeRepository.findByNameContainingIgnoreCaseOrderByDateDesc(name);
        }

        return recipes.stream()
                .map(this::fromDto)
                .collect(Collectors.toList());
    }

    public boolean isAuthor(Recipe recipe) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();
        return recipe.getUser().equals(user);
    }


    private RecipeDto fromDto(Recipe recipe){

        return new RecipeDto(recipe.getName(),recipe.getCategory(),recipe.getDate(),recipe.getDescription(),
                recipe.getIngredients(), recipe.getDirections());
    }
}
