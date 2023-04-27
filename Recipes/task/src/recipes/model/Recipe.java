package recipes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    @NotBlank
    private String name;

    @Column
    @NotBlank
    private String category;
    @Column
    @NotBlank
    private String description;

    @Column
    @UpdateTimestamp
    private LocalDateTime date;

    @ElementCollection
    @CollectionTable(name = "ingredients", joinColumns = @JoinColumn(name = "recipe"))
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @NotEmpty
    @Size(min = 1)
    private List<String> ingredients;

    @ElementCollection
    @CollectionTable(name = "directions", joinColumns = @JoinColumn(name = "recipe"))
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @NotEmpty
    @Size(min = 1)
    private List<String> directions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Recipe(String name, String category, String description, List<String> ingredients, List<String> directions) {
        this.name = name;
        this.category = category;
        this.date = LocalDateTime.now();
        this.description = description;
        this.ingredients = ingredients;
        this.directions = directions;
    }

    public Recipe(String name, String category, String description, List<String> ingredients, List<String> directions, User user) {
        this.name = name;
        this.category = category;
        this.date = LocalDateTime.now();
        this.description = description;
        this.ingredients = ingredients;
        this.directions = directions;
        this.setUser(user);
    }


}
