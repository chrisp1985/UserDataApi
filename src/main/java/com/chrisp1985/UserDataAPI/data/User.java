package com.chrisp1985.UserDataAPI.data;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @NotNull
    Integer id;

    @NotBlank
    String name;

    @NotNull
    Integer value;

}
