package fr.perso.springserie.service;

import fr.perso.springserie.model.dto.ActeurDTO;
import fr.perso.springserie.model.entity.Acteur;
import fr.perso.springserie.repository.IActeurRepo;
import fr.perso.springserie.service.interfaces.IActeurService;
import org.springframework.stereotype.Service;

@Service
public class ActeurService extends BaseService<Acteur, ActeurDTO> implements IActeurService {
    protected ActeurService(IActeurRepo repository) {
        super(repository, ActeurDTO.class, Acteur.class);
    }
}
