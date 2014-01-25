package de.htw.saarland.gamedev.nap.data.skills;

import de.htw.saarland.gamedev.nap.data.entities.Entity;

public interface ISkillEvent {
	void skillStarted(Skill s, Entity e);
	void skillEnd(Skill s, Entity e);
}
