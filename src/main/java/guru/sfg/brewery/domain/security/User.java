package guru.sfg.brewery.domain.security;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import guru.sfg.brewery.domain.Customer;
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
public class User implements UserDetails, CredentialsContainer {

    private static final long serialVersionUID = -2260149435746983421L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String username;
    private String password;

    @Builder.Default
    private Boolean accountNonExpired     = true;
    @Builder.Default
    private Boolean accountNonLocked      = true;
    @Builder.Default
    private Boolean credentialsNonExpired = true;
    @Builder.Default
    private Boolean enabled               = true;
    @Builder.Default
    private Boolean userGoogle2Fa         = false;
    private String  google2FaSecret;
    @Transient
    private Boolean google2FaRequired     = true;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    private Customer customer;

    @Singular
    @ManyToMany(cascade = { CascadeType.MERGE }, fetch = FetchType.EAGER)
    // @formatter:off
    @JoinTable(name               = "user_role", 
               joinColumns        = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID") }, 
               inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID") })
    // @formatter:off
    private Set<Role> roles;

    @Transient
    public Set<GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(Role::getAuthorities) // stream of authority sets.
                .flatMap(Set::stream) // Build stream from each set in outer strean and flatten into single stream.
                .map(authority -> new SimpleGrantedAuthority(authority.getPermission()))
                .collect(Collectors.toSet()); // Collect back into set.
    }
   
    @Override
    public void eraseCredentials() {
        password = null;
    }
    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }
    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
    
    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", password=" + password + ", accountNonExpired="
                + accountNonExpired + ", accountNonLocked=" + accountNonLocked + ", credentialsNonExpired="
                + credentialsNonExpired + ", enabled=" + enabled + 
                ", roles=" + roles.stream().map(Role::getName).collect(Collectors.toList()) + 
                "]";
    }
}
