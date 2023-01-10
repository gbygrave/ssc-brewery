package guru.sfg.brewery.domain.security;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String username;
    private String password;

    @Singular
    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.EAGER)
    // @formatter:off
    @JoinTable(name               = "user_role", 
               joinColumns        = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID") }, 
               inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID") })
    // @formatter:off
    private Set<Role> roles;

    @Transient
    private Set<Authority> authorities;
    
    public Set<Authority> getAuthorities() {
        return this.roles.stream()
                .map(Role::getAuthorities) // stream of authority sets.
                .flatMap(Set::stream) // Build stream from each set in outer strean and flatten into single stream.
                .collect(Collectors.toSet()); // Collect back into set.
    }
   
    @Builder.Default
    private Boolean        accountNonExpired = true;
    @Builder.Default
    private Boolean        accountNonLocked = true;
    @Builder.Default
    private Boolean        credentialsNonExpired = true;
    @Builder.Default
    private Boolean        enabled = true;
    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", password=" + password + ", accountNonExpired="
                + accountNonExpired + ", accountNonLocked=" + accountNonLocked + ", credentialsNonExpired="
                + credentialsNonExpired + ", enabled=" + enabled + 
                ", roles=" + roles.stream().map(Role::getName).collect(Collectors.toList()) + 
                "]";
    }

    
}
