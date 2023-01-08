package guru.sfg.brewery.domain.security;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;

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
    @ManyToMany(cascade = CascadeType.MERGE)
    // @formatter:off
    @JoinTable(name               = "user_authority", 
               joinColumns        = {@JoinColumn(name = "USER_ID",      referencedColumnName = "ID") }, 
               inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID") })
    // @formatter:off
    private Set<Authority> authorities;
    
    @Builder.Default
    private Boolean        accountNonExpired = true;
    @Builder.Default
    private Boolean        accountNonLocked = true;
    @Builder.Default
    private Boolean        credentialsNonExpired = true;
    @Builder.Default
    private Boolean        enabled = true;

}
