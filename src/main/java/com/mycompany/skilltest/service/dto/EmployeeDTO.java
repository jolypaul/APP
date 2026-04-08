package com.mycompany.skilltest.service.dto;

import com.mycompany.skilltest.domain.enumeration.Role;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.skilltest.domain.Employee} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EmployeeDTO implements Serializable {

    private Long id;

    @NotNull
    private String matricule;

    @NotNull
    private String nom;

    @NotNull
    private String prenom;

    @NotNull
    private String email;

    private String telephone;

    @NotNull
    private LocalDate dateEmbauche;

    @NotNull
    private Role role;

    private Boolean hasUserAccount;

    private Boolean canLogin;

    private String defaultPasswordHint;

    private PosteDTO poste;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getHasUserAccount() {
        return hasUserAccount;
    }

    public void setHasUserAccount(Boolean hasUserAccount) {
        this.hasUserAccount = hasUserAccount;
    }

    public Boolean getCanLogin() {
        return canLogin;
    }

    public void setCanLogin(Boolean canLogin) {
        this.canLogin = canLogin;
    }

    public String getDefaultPasswordHint() {
        return defaultPasswordHint;
    }

    public void setDefaultPasswordHint(String defaultPasswordHint) {
        this.defaultPasswordHint = defaultPasswordHint;
    }

    public PosteDTO getPoste() {
        return poste;
    }

    public void setPoste(PosteDTO poste) {
        this.poste = poste;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmployeeDTO)) {
            return false;
        }

        EmployeeDTO employeeDTO = (EmployeeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, employeeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmployeeDTO{" +
            "id=" + getId() +
            ", matricule='" + getMatricule() + "'" +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", email='" + getEmail() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", dateEmbauche='" + getDateEmbauche() + "'" +
            ", role='" + getRole() + "'" +
            ", hasUserAccount='" + getHasUserAccount() + "'" +
            ", canLogin='" + getCanLogin() + "'" +
            ", defaultPasswordHint='" + getDefaultPasswordHint() + "'" +
            ", poste=" + getPoste() +
            "}";
    }
}
