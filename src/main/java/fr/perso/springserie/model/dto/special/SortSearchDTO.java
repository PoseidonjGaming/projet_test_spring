package fr.perso.springserie.model.dto.special;

import fr.perso.springserie.model.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SortSearchDTO<D extends BaseDTO> {
    private SortDTO sortDTO;
    private SearchDTO<D> searchDTO;
}
