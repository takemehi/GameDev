package de.htw.saarland.gamedev.nap.client.render;

import com.badlogic.gdx.math.Vector2;

import de.htw.saarland.gamedev.nap.client.render.skill.FireballRenderer;
import de.htw.saarland.gamedev.nap.client.render.skill.NovaSkillRenderer;
import de.htw.saarland.gamedev.nap.client.render.skill.PyroblastRenderer;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.skills.Fireball;
import de.htw.saarland.gamedev.nap.data.skills.Nova;
import de.htw.saarland.gamedev.nap.data.skills.Pyroblast;
import de.htw.saarland.gamedev.nap.data.skills.Skill;

public abstract class SkillRenderer implements IRender {
	
	private boolean isActive;
	protected Vector2 position;
	private Entity e;
	
	public SkillRenderer(Vector2 position, Entity e) {
		this.isActive = true;
		this.position = position;
		this.e = e;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public void setPosition(Vector2 pos) {
		this.position = pos;
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SkillRenderer) {
			return e == ((SkillRenderer)obj).e;
		}
		else {
			return false;
		}
	}
	
	public static SkillRenderer getSkillRenderer(Skill skill, Entity e) {
		if (skill instanceof Fireball) {
			return new FireballRenderer(e);
		}
		else if (skill instanceof Pyroblast) {
			return new PyroblastRenderer(e);
		}
		else if (skill instanceof Nova) {
			return new NovaSkillRenderer(skill, e);
		}
		else {
			return null;
		}
	}
}
