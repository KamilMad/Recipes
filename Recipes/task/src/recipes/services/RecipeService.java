package recipes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import recipes.errors.RecipeNotFoundException;
import recipes.model.Recipe;
import recipes.model.User;
import recipes.payload.RecipeDto;
import recipes.repositories.RecipeRepository;
import recipes.repositories.UserRepository;
import recipes.security.CustomUserDetails;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

        Optional<User> user = userRepository.findByEmail(auth.getName());
        newRecipe.setUser(user.get());

        recipeRepository.save(newRecipe);

        user.get().getRecipes().add(newRecipe);


        return newRecipe.getId();
    }

   /* public Long addRecipe(Recipe recipe) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();

        Recipe newRecipe = new Recipe(recipe.getName(), recipe.getCategory(),
                recipe.getDescription(), recipe.getIngredients(), recipe.getDirections(), user);

        recipeRepository.save(newRecipe);
        user.getRecipes().add(newRecipe);

        addCreatedRecipeIds(newRecipe.getId());

        return newRecipe.getId();
    }*/

    public RecipeDto getRecipeById(Long id){
        Recipe recipe =  recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException("No such a recipe"));

        return mapEntityToDto(recipe);


    }

    public void deleteById(Long id) {

        Recipe recipe= recipeRepository.findById(id).orElseThrow(() -> new RecipeNotFoundException("No such a recipe"));

        if (!isAuthor(id, recipe)) {
            throw new AccessDeniedException("Access denied");
        }

        recipeRepository.deleteById(id);
    }

    public void updateById(Long id, Recipe recipe){

        Recipe tempRecipe = recipeRepository.findById(id).orElseThrow(() -> new RecipeNotFoundException("No such a recipe"));

        if (!isAuthor(id, recipe)) {
            throw new AccessDeniedException("Access denied");
        }

        tempRecipe.setName(recipe.getName());
        tempRecipe.setIngredients(recipe.getIngredients());
        tempRecipe.setDescription(recipe.getDescription());
        tempRecipe.setCategory(recipe.getCategory());
        tempRecipe.setDirections(recipe.getDirections());

        recipeRepository.save(tempRecipe);

    }

    public List<RecipeDto> findAll(String category, String name){

        if ((category == null && name == null)
                || (category != null && name != null)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        }
        List<Recipe> recipes;

        if (category != null){
            recipes = recipeRepository.findByCategoryIgnoreCaseOrderByDateDesc(category);
        }
        else{
            recipes = recipeRepository.findByNameContainingIgnoreCaseOrderByDateDesc(name);
        }

        return recipes.stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }

    public List<Recipe> findRecipesByUserId(Long userId){

        return recipeRepository.findByUserId(userId);
    }

    public boolean isAuthor(Long recipeId, Recipe recipe) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return  (recipe.getUser().getId() == userDetails.getUser().getId());
    }


    private RecipeDto mapEntityToDto(Recipe recipe){

        return new RecipeDto(recipe.getName(),recipe.getCategory(),recipe.getDate(),recipe.getDescription(),
                recipe.getIngredients(), recipe.getDirections());
    }


    private void addCreatedRecipeIds(Long recipeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        userDetails.getCreatedRecipeIds().add(recipeId);
    }

    private void deleteCreatedRecipeIds(Long recipeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        userDetails.getCreatedRecipeIds().remove(recipeId);
    }


}
