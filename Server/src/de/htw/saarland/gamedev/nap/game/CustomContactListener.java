package de.htw.saarland.gamedev.nap.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.data.CapturePoint;
import de.htw.saarland.gamedev.nap.data.GameWorld;
import de.htw.saarland.gamedev.nap.data.PlayableCharacter;
import de.htw.saarland.gamedev.nap.data.Player;
import de.htw.saarland.gamedev.nap.data.SpawnPoint;
import de.htw.saarland.gamedev.nap.data.Team;
import de.htw.saarland.gamedev.nap.data.entities.Entity;
import de.htw.saarland.gamedev.nap.data.skills.Axe;
import de.htw.saarland.gamedev.nap.data.skills.Charge;
import de.htw.saarland.gamedev.nap.data.skills.Fireball;
import de.htw.saarland.gamedev.nap.data.skills.Nova;
import de.htw.saarland.gamedev.nap.data.skills.Pyroblast;
import de.htw.saarland.gamedev.nap.data.skills.Snare;

public class CustomContactListener implements ContactListener {

	private Array<Player> players;
	private Team blueTeam;
	private Team redTeam;
	private Array<CapturePoint> capturePoints;

	public CustomContactListener(Team blueTeam, Team redTeam, Array<CapturePoint> capturePoints) {
		if (blueTeam == null || redTeam == null)
			throw new NullPointerException();

		this.redTeam = redTeam;
		this.blueTeam = blueTeam;
		this.capturePoints=capturePoints;

		players = new Array<Player>();
		players. addAll(blueTeam.getMembers());
		players.addAll(redTeam.getMembers());
	}

	@Override
	public void beginContact(Contact contact) {
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();

		if (fA.getUserData() != null && fB.getUserData() != null) {
			//Character abilities
			Axe.handleContact(fA, fB, players);
			Snare.handleContact(fA, fB, players);
			Charge.handleContact(fA, fB, players);
			Fireball.handleContact(fA, fB, players);
			Pyroblast.handleContact(fA, fB, players);
			Nova.handleContact(fA, fB, players);
			
			//Capture- and spawn point
			CapturePoint.handleContactBegin(fA, fB, players, capturePoints);
			SpawnPoint.handleContactBegin(fA, fB, blueTeam, redTeam);
		}
		
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		
		if(fA!=null && fB!=null){
			CapturePoint.handleContactEnd(fA, fB, players, capturePoints);
			SpawnPoint.handleContactEnd(fA, fB, blueTeam, redTeam);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Fixture fA = contact.getFixtureA();
		Fixture fB = contact.getFixtureB();
		float offsetA = 0;
		float offsetB = 0;
		PolygonShape playerShape;
		Vector2 tmpVector = new Vector2();
		
		// calculate y offset
		if (fA.getUserData() == PlayableCharacter.USERDATA_PLAYER || fA.getUserData() == Charge.USERDATA_CHARGE) {
			playerShape = (PolygonShape) fA.getShape();
			playerShape.getVertex(0, tmpVector);
			offsetA = tmpVector.y;
		}
		if (fB.getUserData() == PlayableCharacter.USERDATA_PLAYER || fB.getUserData() == Charge.USERDATA_CHARGE) {
			playerShape = (PolygonShape) fB.getShape();
			playerShape.getVertex(0, tmpVector);
			offsetB = tmpVector.y;
		}

		// player jumping through a platform from below
		if (fA.getUserData() != null 
				&& (fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_PLATFORM_ONE)
						|| fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_PLATFORM_TWO))) {
			if (fB.getBody().getPosition().y + offsetB < fA.getBody().getPosition().y) {
				contact.setEnabled(false);
			}
		}
		if (fB.getUserData() != null && (fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_PLATFORM_ONE) || fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_PLATFORM_TWO))) {
			if (fA.getBody().getPosition().y + offsetA < fB.getBody().getPosition().y) {
				contact.setEnabled(false);
			}
		}
		// player dropping through a platform from above
		if (fA.getUserData() != null && fA.getUserData().equals(GameWorld.USERDATA_FIXTURE_PLATFORM_TWO)) {
			Player player = null;
			for (Player p : players) {
				if (fB.equals(p.getPlChar().getFixture())) {
					player = p;
					break;
				}
			}
			if (player != null && (Math.abs(fB.getBody().getPosition().y + offsetB - fA.getBody().getPosition().y)) <= 0.1 && player.getPlChar().getDown()) {
				if (fB.equals(player.getPlChar().getFixture()) && player.getPlChar().getTimeOnGround() >= PlayableCharacter.MIN_TIME_ON_GROUND) {
					contact.setEnabled(false);
				}
			}
			if(player!=null && fB.getUserData().equals(Charge.USERDATA_CHARGE) && fB.getBody().getLinearVelocity().y<0
					&& (Math.abs(fB.getBody().getPosition().y + offsetB - fA.getBody().getPosition().y)) <= 0.1){
				player.getPlChar().getAttack2().reset();
			}
		}
		if (fB.getUserData() != null && fB.getUserData().equals(GameWorld.USERDATA_FIXTURE_PLATFORM_TWO)) {
			Player player = null;
			for (Player p : players) {
				if (fB.equals(p.getPlChar().getFixture())) {
					player = p;
					break;
				}
			}
			if (player != null && (Math.abs(fA.getBody().getPosition().y - offsetA - fB.getBody().getPosition().y)) <= 0.1 && player.getPlChar().getDown()) {
				if ((Math.abs(fB.getBody().getPosition().y + offsetB - fA.getBody().getPosition().y)) <= 0.1 && player.getPlChar().getDown()) {
					if (player.getPlChar().getTimeOnGround() >= PlayableCharacter.MIN_TIME_ON_GROUND) {
						contact.setEnabled(false);
					}
				}
			}
			if(player!=null && fA.getUserData().equals(Charge.USERDATA_CHARGE) && fA.getBody().getLinearVelocity().y<0
					&& (Math.abs(fA.getBody().getPosition().y - offsetA - fB.getBody().getPosition().y)) <= 0.1){
				player.getPlChar().getAttack2().reset();
			}
		}
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

}
