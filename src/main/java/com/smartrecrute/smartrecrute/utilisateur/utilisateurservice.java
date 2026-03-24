package com.smartrecrute.smartrecrute.utilisateur;

import java.util.List;

public interface utilisateurservice {
	List<utilisateur> getAll();
	utilisateur getById(Long id);
	utilisateur create(utilisateur user);
	utilisateur update(Long id, utilisateur user);
	void delete(Long id);
	utilisateur findByUsername(String username);
}
