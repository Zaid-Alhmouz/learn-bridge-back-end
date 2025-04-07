package com.learnbridge.learn_bridge_back_end.dao;

import com.learnbridge.learn_bridge_back_end.entity.Admin;

import java.util.List;

public interface AdminDAO {

    void saveAdmin(Admin admin);
    void updateAdmin(Admin admin);
    void deleteAdmin(Long adminId);
    Admin getAdminById(Long adminId);
    Admin getAdminByFullName(String firstName, String lastName);
    List<Admin> findAllAdmins();
}
