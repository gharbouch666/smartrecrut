package com.smartrecrute.smartrecrute.rh;

import java.util.List;

public interface rhservice {
	List<rh> getAll();
	rh getById(Long id);
	rh create(rh rh);
	rh update(Long id, rh rh);
	void delete(Long id);
	rh findByEmail(String email);
}
