package com.smartrecrute.smartrecrute.admin;

import java.util.List;

public interface adminservice {
	List<admin> getAll();
	admin getById(Long id);
	admin create(admin admin);
	admin update(Long id, admin admin);
	void delete(Long id);
	admin findByUsername(String username);
}
