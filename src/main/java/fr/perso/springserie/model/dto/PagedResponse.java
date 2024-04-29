package fr.perso.springserie.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PagedResponse<D extends BaseDTO> {
    private List<D> content;
    private long size;

}
