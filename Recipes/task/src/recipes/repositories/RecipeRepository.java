package recipes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import recipes.model.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    public List<Recipe> findByCategoryIgnoreCaseOrderByDateDesc(String category);

    public List<Recipe> findByNameContainingIgnoreCaseOrderByDateDesc(String name);

    public List<Recipe> findByUserId(Long userId);


}
