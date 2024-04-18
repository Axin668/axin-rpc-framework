package com.axinstar.rpc.api;

import lombok.*;

import java.io.Serializable;

/**
 * @author axin
 * @since 2024/03/30
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Hello implements Serializable {

    private String message;
    private String description;
}
