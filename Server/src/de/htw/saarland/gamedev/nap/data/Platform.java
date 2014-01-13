package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import de.htw.saarland.gamedev.nap.data.skills.Charge;

public class Platform {
	public static void handleContactPreSolve(Contact contact, Array<IPlayer> players){
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
			IPlayer player = null;
			for (IPlayer p : players) {
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
			IPlayer player = null;
			for (IPlayer p : players) {
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
}
