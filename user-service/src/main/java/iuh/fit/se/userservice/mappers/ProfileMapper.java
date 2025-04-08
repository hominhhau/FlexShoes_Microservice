package iuh.fit.se.userservice.mappers;

import iuh.fit.se.userservice.dtos.ProfileCreationRequest;
import iuh.fit.se.userservice.dtos.SignUpRequest;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ProfileMapper {

    public ProfileCreationRequest mapToProfile(SignUpRequest customer);
}
