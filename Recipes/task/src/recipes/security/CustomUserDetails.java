package recipes.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import recipes.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomUserDetails implements UserDetails {

    private List<Long> createdRecipeIds;

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
        this.createdRecipeIds = new ArrayList<>();
    }

    public void removeCreatedRecipeId(Long id){
        this.createdRecipeIds.remove(id);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return  mapRolesToAuthorities(user.getRoles());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private Set<GrantedAuthority> mapRolesToAuthorities(Set<String> roles){
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }
}
