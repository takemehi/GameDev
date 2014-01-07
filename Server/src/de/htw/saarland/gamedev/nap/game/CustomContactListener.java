package de.htw.saarland.gamedev.nap.game;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import de.htw.saarland.gamedev.nap.data.GameWorld;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.data.Team;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.skills.Axe;
import de.htw.saarland.gamedev.nap.data.skills.Fireball;
import de.htw.saarland.gamedev.nap.data.skills.Nova;
import de.htw.saarland.gamedev.nap.data.skills.Pyroblast;

public class CustomContactListener implements ContactListener {

	// TODO add constants for userdata

	private LinkedList<Player> players;
	private Team blueTeam;
	private Team redTeam;

	public CustomContactListener(Team blueTeam, Team redTeam) {
		if (blueTeam == null || redTeam == null)
			throw new NullPointerException();

		this.redTeam = redTeam;
		this.blueTeam = blueTeam;

		players = new LinkedList<Player>();
		players.addAll(blueTeam.getMembers());
		players.addAll(redTeam.getMembers());
	}

	@Override
	public void beginContact(Contact contact) {
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();

		if (fA.getUserData() != null && fB.getUserData() != null) {
			// Axe hitting a player
			if ((fA.getUserData() == Axe.USERDATA_AXE && fB.getUserData() == PlayableCharacter.USERDATA_PLAYER)) {
				for (Player p : players) {
					if (p.getPlChar().getFixture().equals(fB)) {
						p.getPlChar().setHealth(
								p.getPlChar().getHealth() - Axe.DAMAGE);
						break;
					}
				}
			} else if (fB.getUserData() == Axe.USERDATA_AXE
					&& fA.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
				for (Player p : players) {
					if (p.getPlChar().getFixture().equals(fA)) {
						p.getPlChar().setHealth(
								p.getPlChar().getHealth() - Axe.DAMAGE);
						break;
					}
				}
			}
			else if (fA.getUserData() == Snare.USERDATA_SNARE
					&& fB.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
				for (Player p : players) {
					if (p.getPlChar().getFixture().equals(fB)) {
						p.getPlChar().setHealth(
								p.getPlChar().getHealth() - Snare.DAMAGE);
						p.getPlChar().setSnared(true, Snare.DURATION_SNARE);
					}
				}
			}
			else if (fB.getUserData() == Snare.USERDATA_SNARE
					&& fA.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
				for (Player p : players) {
					if (p.getPlChar().getFixture().equals(fA)) {
						p.getPlChar().setHealth(
								p.getPlChar().getHealth() - Snare.DAMAGE);
						p.getPlChar().setSnared(true, Snare.DURATION_SNARE);
					}
				}
			}
			// Fireball hitting the world
			else if (fA.getUserData() == Fireball.USERDATA_FIREBALL
					&& fB.getUserData() == GameWorld.USERDATA_FIXTURE_WORLD) {
				fA.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			} else if (fB.getUserData() == Fireball.USERDATA_FIREBALL
					&& fA.getUserData() == GameWorld.USERDATA_FIXTURE_WORLD) {
				fB.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}
			// Fireball hitting a player
			else if (fA.getUserData() == Fireball.USERDATA_FIREBALL
					&& fB.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
				for (Player p : players) {
					if (p.getPlChar().getFixture().equals(fB))
						p.getPlChar().setHealth(
								p.getPlChar().getHealth() - Fireball.DAMAGE);
					break;
				}
				fA.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			} else if (fB.getUserData() == Fireball.USERDATA_FIREBALL
					&& fA.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
				for (Player p : players) {
					if (p.getPlChar().getFixture().equals(fA))
						p.getPlChar().setHealth(
								p.getPlChar().getHealth() - Fireball.DAMAGE);
					break;
				}
				fB.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}
			// Pyroblast hitting the world
			else if (fA.getUserData() == Pyroblast.USERDATA_PYROBLAST
					&& fB.getUserData() == GameWorld.USERDATA_FIXTURE_WORLD) {
				fA.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			} else if (fB.getUserData() == Pyroblast.USERDATA_PYROBLAST
					&& fA.getUserData() == GameWorld.USERDATA_FIXTURE_WORLD) {
				fB.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}
			// Pyroblast hitting a player
			else if (fA.getUserData() == Pyroblast.USERDATA_PYROBLAST
					&& fB.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
				for (Player p : players) {
					if (p.getPlChar().getFixture().equals(fB))
						p.getPlChar().setHealth(
								p.getPlChar().getHealth() - Pyroblast.DAMAGE);
					break;
				}
				fA.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			} else if (fB.getUserData() == Pyroblast.USERDATA_PYROBLAST
					&& fA.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
				for (Player p : players) {
					if (p.getPlChar().getFixture().equals(fA))
						p.getPlChar().setHealth(
								p.getPlChar().getHealth() - Pyroblast.DAMAGE);
					break;
				}
				fB.getBody().setUserData(Entity.USERDATA_BODY_FLAG_DELETE);
			}
			// Nova hitting a player
			else if (fA.getUserData() == Nova.USERDATA_NOVA
					&& fB.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
				Vector2 direction;

				for (Player p : players) {
					if (p.getPlChar().getFixture().equals(fB)) {
						direction = new Vector2(p.getPlChar().getBody()
								.getPosition().x
								- fA.getBody().getPosition().x, p.getPlChar()
								.getBody().getPosition().y
								- fA.getBody().getPosition().y);
						direction = direction.nor();
						direction.mul(Nova.FORCE);
						p.getPlChar().setHealth(
								p.getPlChar().getHealth() - Nova.DAMAGE);
						p.getPlChar().setStunned(true, Nova.DURATION_STUN);
						p.getPlChar()
								.getBody()
								.applyLinearImpulse(direction,
										p.getPlChar().getBody().getPosition(),
										true);
					}
				}
			} else if (fB.getUserData() == Nova.USERDATA_NOVA
					&& fA.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
				Vector2 direction;

				for (Player p : players) {
					if (p.getPlChar().getFixture().equals(fA)) {
						direction = new Vector2(p.getPlChar().getBody()
								.getPosition().x
								- fB.getBody().getPosition().x, p.getPlChar()
								.getBody().getPosition().y
								- fB.getBody().getPosition().y);
						direction = direction.nor();
						direction.mul(Nova.FORCE);
						p.getPlChar().setHealth(
								p.getPlChar().getHealth() - Nova.DAMAGE);
						p.getPlChar().setStunned(true, Nova.DURATION_STUN);
						p.getPlChar()
								.getBody()
								.applyLinearImpulse(direction,
										p.getPlChar().getBody().getPosition(),
										true);
					}
				}
			}
		} else if (fA.getUserData() != null
				&& fA.getUserData().equals(
						GameWorld.USERDATA_FIXTURE_SPAWNPOINT_BLUE)) {
			// spawnPoint team blue
			for (Player p : blueTeam.getMembers()) {
				if (p.getPlChar().getFixture().equals(fB))
					p.getPlChar().setAtSpawn(true);
			}
		} else if (fB.getUserData() != null
				&& fB.getUserData().equals(
						GameWorld.USERDATA_FIXTURE_SPAWNPOINT_BLUE)) {
			for (Player p : blueTeam.getMembers()) {
				if (p.getPlChar().getFixture().equals(fA))
					p.getPlChar().setAtSpawn(true);
			}
		} else if (fA.getUserData() != null
				&& fA.getUserData().equals(
						GameWorld.USERDATA_FIXTURE_SPAWNPOINT_RED)) {
			// spawnPoint team red
			for (Player p : redTeam.getMembers()) {
				if (p.getPlChar().getFixture().equals(fB))
					p.getPlChar().setAtSpawn(true);
			}
		} else if (fB.getUserData() != null
				&& fB.getUserData().equals(
						GameWorld.USERDATA_FIXTURE_SPAWNPOINT_RED)) {
			for (Player p : redTeam.getMembers()) {
				if (p.getPlChar().getFixture().equals(fA))
					p.getPlChar().setAtSpawn(true);
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();

		if (fA != null
				&& fA.getUserData() != null
				&& fA.getUserData().equals(
						GameWorld.USERDATA_FIXTURE_SPAWNPOINT_BLUE)) {
			for (Player p : blueTeam.getMembers()) {
				if (p.getPlChar().getFixture().equals(fB))
					p.getPlChar().setAtSpawn(false);
			}
		} else if (fB != null
				&& fB.getUserData() != null
				&& fB.getUserData().equals(
						GameWorld.USERDATA_FIXTURE_SPAWNPOINT_BLUE)) {
			for (Player p : blueTeam.getMembers()) {
				if (p.getPlChar().getFixture().equals(fA))
					p.getPlChar().setAtSpawn(false);
			}
		}
		// spawnPoint team red
		else if (fA != null
				&& fA.getUserData() != null
				&& fA.getUserData().equals(
						GameWorld.USERDATA_FIXTURE_SPAWNPOINT_RED)) {
			for (Player p : redTeam.getMembers()) {
				if (p.getPlChar().getFixture().equals(fB))
					p.getPlChar().setAtSpawn(false);
			}
		} else if (fB != null
				&& fB.getUserData() != null
				&& fB.getUserData().equals(
						GameWorld.USERDATA_FIXTURE_SPAWNPOINT_RED)) {
			for (Player p : redTeam.getMembers()) {
				if (p.getPlChar().getFixture().equals(fA))
					p.getPlChar().setAtSpawn(false);
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO remove keypress argument
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		float offsetA = 0;
		float offsetB = 0;
		PolygonShape playerShape;
		Vector2 tmpVector = new Vector2();
		// calculate y offset
		if (fA.getUserData() != null
				&& fA.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
			playerShape = (PolygonShape) fA.getShape();
			playerShape.getVertex(0, tmpVector);
			offsetA = tmpVector.y;
		}
		if (fB.getUserData() != null
				&& fB.getUserData() == PlayableCharacter.USERDATA_PLAYER) {
			playerShape = (PolygonShape) fB.getShape();
			playerShape.getVertex(0, tmpVector);
			offsetB = tmpVector.y;
		}

		// player jumping through a platform from below
		if (fA.getUserData() != null
				&& (fA.getUserData().equals(
						GameWorld.USERDATA_FIXTURE_PLATFORM_ONE) || fA
						.getUserData().equals(
								GameWorld.USERDATA_FIXTURE_PLATFORM_TWO))) {
			if (fB.getBody().getPosition().y + offsetB < fA.getBody()
					.getPosition().y) {
				contact.setEnabled(false);
			}
		}
		if (fB.getUserData() != null
				&& (fB.getUserData().equals(
						GameWorld.USERDATA_FIXTURE_PLATFORM_ONE) || fB
						.getUserData().equals(
								GameWorld.USERDATA_FIXTURE_PLATFORM_TWO))) {
			if (fA.getBody().getPosition().y + offsetA < fB.getBody()
					.getPosition().y) {
				contact.setEnabled(false);
			}
		}
		// player dropping through a platform from above
		if (fA.getUserData() != null
				&& fA.getUserData().equals(
						GameWorld.USERDATA_FIXTURE_PLATFORM_TWO)) {
			if ((Math.abs(fB.getBody().getPosition().y + offsetB
					- fA.getBody().getPosition().y)) <= 0.1
					&& Gdx.input.isKeyPressed(Keys.S)) {
				for (Player p : players) {
					if (fB.equals(p.getPlChar().getFixture())
							&& p.getPlChar().getTimeOnGround() >= PlayableCharacter.MIN_TIME_ON_GROUND)
						contact.setEnabled(false);
				}
			}
		}
		if (fB.getUserData() != null
				&& fB.getUserData().equals(
						GameWorld.USERDATA_FIXTURE_PLATFORM_TWO)) {
			if ((Math.abs(fA.getBody().getPosition().y - offsetA
					- fB.getBody().getPosition().y)) <= 0.1
					&& Gdx.input.isKeyPressed(Keys.S)) {
				if ((Math.abs(fB.getBody().getPosition().y + offsetB
						- fA.getBody().getPosition().y)) <= 0.1
						&& Gdx.input.isKeyPressed(Keys.S)) {
					for (Player p : players) {
						if (fA.equals(p.getPlChar().getFixture())
								&& p.getPlChar().getTimeOnGround() >= PlayableCharacter.MIN_TIME_ON_GROUND)
							contact.setEnabled(false);
					}
				}
			}
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

}
