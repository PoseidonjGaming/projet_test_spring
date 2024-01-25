package fr.perso.springserie.service.mapper;

import com.google.gson.Gson;
import fr.perso.springserie.model.dto.UserDTO;
import fr.perso.springserie.task.MapService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import static fr.perso.springserie.service.utility.ServiceUtility.getField;

@Service
@Transactional
public class UserMapper extends Mapper implements IMapper {
    public UserMapper(MapService mapService) {
        super(mapService);
    }


    @Override
    public <S, T> T convert(S source, Class<T> targetClass) {

        if (source instanceof UserDTO userDTO) {
            T target = super.convert(source, targetClass);
            if (userDTO.getRoles() != null) {
                Gson json = new Gson();
                set(json.toJson(userDTO.getRoles()), target, getField("roles", targetClass));
            }

            return target;
        } else {
            return super.convert(source, targetClass);
        }

    }
}
