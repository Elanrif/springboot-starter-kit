package com.elanrif.springbootstarterkit.services.auth;

import com.elanrif.springbootstarterkit.dto.auth.CurrentUserDto;
import com.elanrif.springbootstarterkit.dto.UserDto;

public interface CurrentUserService {
    UserDto.Response updateMyProfile(CurrentUserDto.UpdateProfileRequest request);
    void changeMyPassword(CurrentUserDto.ChangePasswordRequest request);
    void deleteMyAccount(CurrentUserDto.DeleteAccountRequest request);
}