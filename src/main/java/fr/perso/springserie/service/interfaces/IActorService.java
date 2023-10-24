package fr.perso.springserie.service.interfaces;

import fr.perso.springserie.controller.ActorController;
import fr.perso.springserie.model.dto.ActorDTO;

import java.util.List;


public interface IActorService extends IBaseService<ActorDTO> {
    List<ActorDTO> test(ActorDTO dto);
}
